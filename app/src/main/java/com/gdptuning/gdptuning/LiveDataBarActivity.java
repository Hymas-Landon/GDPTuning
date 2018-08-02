package com.gdptuning.gdptuning;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class LiveDataBarActivity extends AppCompatActivity implements View.OnClickListener {

    //ESP32 aREST server address
    final String url = "http://192.168.7.1";
    TextView tvBoost, tvEgt, tvOilPressure, tvFule, tvTrubo, tvDfrp, tvTiming, tvCoolant, tvGear, tvAfrp, tvTune;
    String device = "GDP";
    int tuneMode = 0;
    Timer timer;

    ImageView btn_info, wifi_switch;
    Button btn_tune, btn_home;
    Typeface tf1;
    RequestQueue queue;
    boolean isConnected = false;
    boolean isProcessing = false;

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


        Random num = new Random();
        int number[] = new int[6];

        for (int i = 0; i < number.length; i++) {
            number[i] = (i + 1) * num.nextInt(35);
        }


        ImageLinearGauge imageLinearGauge1 = findViewById(R.id.imageLinearGauge1);
        ImageLinearGauge imageLinearGauge2 = findViewById(R.id.imageLinearGauge2);
        ImageLinearGauge imageLinearGauge3 = findViewById(R.id.imageLinearGauge3);
        ImageLinearGauge imageLinearGauge4 = findViewById(R.id.imageLinearGauge4);
        ImageLinearGauge imageLinearGauge5 = findViewById(R.id.imageLinearGauge5);
        ImageLinearGauge imageLinearGauge6 = findViewById(R.id.imageLinearGauge6);


// change speed
        imageLinearGauge1.speedTo(number[0]);
        imageLinearGauge2.speedTo(number[1]);
        imageLinearGauge3.speedTo(number[2]);
        imageLinearGauge4.speedTo(number[3]);
        imageLinearGauge5.speedTo(number[4]);
        imageLinearGauge6.speedTo(number[5]);

        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = null;
        if (connManager != null) {
            mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        }


        //Working with wifi
        wifi_switch = findViewById(R.id.wifi_switch);
        wifi_switch.setOnClickListener(this);

        queue = Volley.newRequestQueue(this);

        sendRequest();

        timer = new

                Timer();
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
                            JSONObject variables = response.getJSONObject("variables");
                            Log.d("TEST2 ", variables.toString());
                            tuneMode = variables.getInt("tune_mode");
                            String deviceName = response.getString("name");
                            deviceName += response.getString("id");
                            device = deviceName;

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
                                .setCancelText("Retry")
                                .setConfirmText("Connect")
                                .showCancelButton(true)
                                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sendRequest();
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
                            JSONObject variables = response.getJSONObject("variables");
                            Log.d("TEST2 ", variables.toString());
                            tuneMode = variables.getInt("tune_mode");
                            tvTune.setText("Tune :" + tuneMode);
                            tvBoost.setText(variables.getString("boost"));
                            tvEgt.setText(variables.getString("egt"));
                            tvFule.setText(variables.getString("fule"));
                            tvOilPressure.setText(variables.getString("oil_pressur"));
                            tvTrubo.setText(variables.getString("turbo"));
                            tvDfrp.setText(variables.getString("frp"));
                            tvTiming.setText(variables.getString("timing"));
                            tvCoolant.setText(variables.getString("coolant"));
                            tvGear.setText(variables.getString("gear"));
                            tvAfrp.setText(variables.getString("frp"));

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
                                .setCancelText("Retry")
                                .setConfirmText("Connect")
                                .showCancelButton(true)
                                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sendRequest();
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
                startActivity(new Intent(LiveDataBarActivity.this, WifiActivity.class));
                break;
            case R.id.btn_home:
                startActivity(new Intent(LiveDataBarActivity.this, MainActivity.class));
                break;
        }
    }
}


