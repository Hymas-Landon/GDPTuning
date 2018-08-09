package com.gdptuning.gdptuning;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.anastr.speedviewlib.ImageLinearGauge;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class LiveDataBarActivity extends AppCompatActivity implements View.OnClickListener {

    //ESP32 aREST server address
    final String url = "http://192.168.7.1";
    TextView tvBoostView, tvEgt, tvOilPressure, tvFuel, tvTurbo, tvDfrp, tvTiming, tvCoolant, tvGear, tvAfrp, tvTune;
    String device = "GDP";
    int tuneMode = 0;
    Timer timer;

    ImageView btn_info, wifi_switch;
    Button btn_tune, btn_home;
    RequestQueue queue;
    boolean isConnected = false;
    boolean isProcessing = false;
    WifiManager wifi;

    public void change() {
        //Button variables
        btn_tune = findViewById(R.id.select_tune);
        //Set onClick listeners
        btn_tune.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent click = new Intent(LiveDataBarActivity.this, LiveDataActivity.class);
                startActivity(click);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_livedata_bar);
        change();


        btn_home = findViewById(R.id.btn_home);

        btn_home.setOnClickListener(this);

        tvEgt = findViewById(R.id.egt);
        tvBoostView = findViewById(R.id.boost);
        tvTurbo = findViewById(R.id.turbo);
        tvOilPressure = findViewById(R.id.oil_pressure);
        tvFuel = findViewById(R.id.fuel_rate);
        tvCoolant = findViewById(R.id.coolant);

        //Working with wifi
        wifi_switch = findViewById(R.id.wifi_switch);
        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifi_switch.setOnClickListener(this);
        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifi.isWifiEnabled()) {
            wifi_switch.setImageResource(R.drawable.wifi_pressed);
        } else {
            wifi_switch.setImageResource(R.drawable.wifi_not_connected_pressed);
        }

        queue = Volley.newRequestQueue(this);
        sendRequest();

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int num = 1;

            @Override
            public void run() {
                if (isConnected) {
                    if (!isProcessing) {
                        Log.d("TEST2 :", "Sending request");
                        updateRequest();
                    }
                }
            }
        }, 0, 500);//put here time 1000 milliseconds=1 second
    }

    @Override
    protected void onPause() {
        super.onPause();
        timer.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int num = 1;

            @Override
            public void run() {
                if (isConnected) {
                    if (!isProcessing) {
                        Log.d("TEST2 :", "Sending request");
                        updateRequest();
                    }
                }
            }
        }, 0, 500);//put here time 1000 milliseconds=1 second
    }

    //Send to sGDP server to verify connection
    public void sendRequest() {
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        isConnected = true;
                        wifi_switch.setImageResource(R.drawable.wifi_pressed);
                        try {
                            JSONArray variables = response.getJSONArray("variables");

                            for (int i = 0; i < variables.length(); i++) {
                                JSONObject variable = variables.getJSONObject(i);
                                Log.d("TEST2 ", variables.toString());
                                tuneMode = variable.getInt("tune_mode");
                                String deviceName = response.getString("name");
                                deviceName += response.getString("id");
                                device = deviceName;
                            }

                            /*This is not my code but I won't delete it just in case we need it for reference */
//                            JSONObject variables = response.getJSONObject("variables");
//                            Log.d("TEST2 ", variables.toString());
//                            tuneMode = variables.getInt("tune_mode");
//                            String deviceName = response.getString("name");
//                            deviceName += response.getString("id");
//                            device = deviceName;
                            /*End of the code that does not belong to me*/

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // display response
                        Log.d("Response", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isConnected = false;
                        wifi_switch.setImageResource(R.drawable.wifi_not_connected_pressed);
                        Log.d("Error.Response", error.toString());

                        new SweetAlertDialog(LiveDataBarActivity.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("No Connection")
                                .setContentText("Your are not connected to GDP device")
                                .setCancelText("Cancel")
                                .setConfirmText("Connect")
                                .showCancelButton(true)
                                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismiss();
                                    }
                                })
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                                    }
                                })
                                .show();
                    }
                }
        );
        // add it to the RequestQueue
        queue.add(getRequest);
    }

    //Send to sGDP server to get live data
    public void updateRequest() {
        isProcessing = true;
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        isConnected = true;

                        wifi_switch.setImageResource(R.drawable.wifi_pressed);
                        try {

                            JSONArray variables = response.getJSONArray("variables");

                            for (int i = 0; i < variables.length(); i++) {
                                JSONObject variable = variables.getJSONObject(i);

                                /*This is not my code but I won't delete it just in case we need it for reference */
//
//                                JSONObject variables = response.getJSONObject("variables");
//                            Log.d("TEST2 ", variables.toString());
//                            tuneMode = variables.getInt("tune_mode");
//                            tvTune.setText("Tune :" + tuneMode);
//                            tvBoost.setText(variables.getString("boost"));
//                            tvEgt.setText(variables.getString("egt"));
//                            tvFuel.setText(variables.getString("fuel"));
//                            tvOilPressure.setText(variables.getString("oil_pressure"));
//                            tvTurbo.setText(variables.getString("turbo"));
//                            tvDfrp.setText(variables.getString("frp"));
//                            tvTiming.setText(variables.getString("timing"));
//                            tvCoolant.setText(variables.getString("coolant"));
//                            tvGear.setText(variables.getString("gear"));
//                            tvAfrp.setText(variables.getString("frp"));

                                /*End of the code that does not belong to me*/

                                int egt = variable.getInt("egt");
                                int boost = variable.getInt("boost");
                                int turbo = variable.getInt("egt");
                                int oilPressure = variable.getInt("oil_pressur");
                                int fuel = variable.getInt("fule");
                                int coolant = variable.getInt("coolant");
                                int gear = variable.getInt("gear");
                                int timing = variable.getInt("timing");
                                int frp = variable.getInt("frp");

                                //move gauge to value
                                //Gauges for linear bar gauges
                                ImageLinearGauge imageLinearGauge1 = findViewById(R.id.imageLinearGauge1);
                                imageLinearGauge1.speedTo(egt);
                                ImageLinearGauge imageLinearGauge2 = findViewById(R.id.imageLinearGauge2);
                                imageLinearGauge2.speedTo(boost);
                                ImageLinearGauge imageLinearGauge3 = findViewById(R.id.imageLinearGauge3);
                                imageLinearGauge3.speedTo(turbo);
                                ImageLinearGauge imageLinearGauge4 = findViewById(R.id.imageLinearGauge4);
                                imageLinearGauge4.speedTo(oilPressure);
                                ImageLinearGauge imageLinearGauge5 = findViewById(R.id.imageLinearGauge5);
                                imageLinearGauge5.speedTo(fuel);
                                ImageLinearGauge imageLinearGauge6 = findViewById(R.id.imageLinearGauge6);
                                imageLinearGauge6.speedTo(coolant);

                                //assign textViews
                                tvEgt = findViewById(R.id.egt);
                                tvBoostView = findViewById(R.id.boost);
                                tvTurbo = findViewById(R.id.turbo);
                                tvOilPressure = findViewById(R.id.oil_pressure);
                                tvFuel = findViewById(R.id.fuel_rate);
                                tvCoolant = findViewById(R.id.coolant);

                            }

                            Log.d("Response", response.toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // display response

                        isProcessing = false;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isConnected = false;
                        wifi_switch.setImageResource(R.drawable.wifi_not_connected_pressed);
                        Log.d("Error.Response", error.toString());

                        new SweetAlertDialog(LiveDataBarActivity.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("No Connection")
                                .setContentText("Your are not connected to GDP device")
                                .setCancelText("Cancel")
                                .setConfirmText("Connect")
                                .showCancelButton(true)
                                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismiss();
                                    }
                                })
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                                    }
                                })
                                .show();
                        isProcessing = false;
                    }
                }
        );

        // add it to the RequestQueue
        queue.add(getRequest);

    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        switch (id) {
            case R.id.wifi_switch:
                displayDevicecInfo();
                break;
            case R.id.btn_home:
                startActivity(new Intent(LiveDataBarActivity.this, MainActivity.class));
                break;
        }
    }

    //Show Connection details
    void displayDevicecInfo() {
        if (isConnected) {
            new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Connected")
                    .setContentText("You are connected to " + device)
                    .setConfirmText("ok")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            // reuse previous dialog instance
                            sDialog.dismiss();
                        }
                    })
                    .show();
        } else {
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("No Connection")
                    .setContentText("You are not connected to a GDP device")
                    .setCancelText("Cancel")
                    .setConfirmText("Connect")
                    .showCancelButton(true)
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismiss();
                        }
                    })
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    })
                    .show();
        }

    }
}


