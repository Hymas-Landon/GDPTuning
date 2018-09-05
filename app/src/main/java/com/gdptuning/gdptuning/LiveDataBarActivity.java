package com.gdptuning.gdptuning;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
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

import java.util.Timer;
import java.util.TimerTask;

public class LiveDataBarActivity extends AppCompatActivity implements View.OnClickListener {

    //ESP32 aREST server address
    final String url = "http://192.168.7.1";
    //    final String url = "https://api.myjson.com/bins/sh7ic";
    TextView tvBoostView, tvEgt, tvOilPressure, tvFuel, tvTurbo, tvFrp, tvTiming, tvCoolant, tvGear, tvTune;
    String device = "GDP";
    int tuneMode = 0;
    Timer timer;
    private static int VFORD1 = 7;
    private static int VFORD2 = 8;
    private static int VGM1 = 9;
    private static int VGM2 = 10;
    private static int VRAM = 11;
    Button btn_home, btn_more;
    RequestQueue queue;
    boolean isConnected = false;
    boolean isProcessing = false;
    WifiManager wifi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getColorTheme() == Utils.THEME_DEFAULT) {
            setTheme(R.style.AppThemeNoActionBarOrangeMain);
        } else if (getColorTheme() == Utils.THEME_GREEN) {
            setTheme(R.style.AppThemeNoActionBarGreen);
        } else if (getColorTheme() == Utils.THEME_BLUE) {
            setTheme(R.style.AppThemeNoActionBarBlue);
        } else if (getColorTheme() == Utils.THEME_RED) {
            setTheme(R.style.AppThemeNoActionBarRed);
        }
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);


        setContentView(R.layout.activity_livedata_bar);

        //find views
        btn_home = findViewById(R.id.btn_home);
        btn_more = findViewById(R.id.moreGauges);
        tvEgt = findViewById(R.id.egt);
        tvBoostView = findViewById(R.id.boost);
        tvTurbo = findViewById(R.id.turbo);
        tvOilPressure = findViewById(R.id.oil_pressure);
        tvFuel = findViewById(R.id.fuel_rate);
        tvCoolant = findViewById(R.id.coolant);
        tvGear = findViewById(R.id.gear_position);
        tvTune = findViewById(R.id.tunenum);

        //set OnClickListeners
        btn_home.setOnClickListener(this);
        btn_more.setOnClickListener(this);

        //Working with wifi
        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

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

    private int getColorTheme() {
        SharedPreferences mSharedPreferences = getSharedPreferences("ThemeColor", MODE_PRIVATE);
        return mSharedPreferences.getInt("theme", Utils.THEME_DEFAULT);
    }

    private int getVehicleType() {
        SharedPreferences mSharedPreferences = getSharedPreferences("ThemeColor", MODE_PRIVATE);
        return mSharedPreferences.getInt("vehicle", VFORD1);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(LiveDataBarActivity.this, MainActivity.class);
        startActivity(i);
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
                        try {
                            JSONObject variables = response.getJSONObject("variables");
                            Log.d("TEST2 ", variables.toString());
                            tuneMode = variables.getInt("tune_mode");
                            int gear = variables.getInt("gear");
                            String deviceName = response.getString("name");
                            deviceName += response.getString("id");
                            device = deviceName;

                            char pos = (char) gear;

                            tvTune.setText("TUNE: " + tuneMode);
                            tvGear.setText("GEAR: " + pos);

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

                        try {

                            JSONObject variables = response.getJSONObject("variables");

                            tuneMode = variables.getInt("tune_mode");
                            int gear = variables.getInt("gear");
                            String deviceName = response.getString("name");
                            deviceName += response.getString("id");
                            device = deviceName;

                            char pos = (char) gear;

                            tvTune.setText("TUNE: " + tuneMode);
                            tvGear.setText("GEAR: " + pos);
                            int egt = variables.getInt("egt");
                            int boost = variables.getInt("boost");
                            int turbo = variables.getInt("turbo");
                            int fuel = variables.getInt("fule");
                            int coolant = variables.getInt("coolant");

                            if (getVehicleType() == VFORD1 || getVehicleType() == VFORD2) {
                                int fordOilTemp = variables.getInt("myOilTemp");
                                //Gauge1
                                ImageLinearGauge imageLinearGauge1 = findViewById(R.id.imageLinearGauge1);
                                imageLinearGauge1.speedTo(egt);

                                //Gauge2
                                ImageLinearGauge imageLinearGauge2 = findViewById(R.id.imageLinearGauge2);
                                if (boost > 5) {
                                    imageLinearGauge2.speedTo((float) (boost * 0.1450377));
                                } else {
                                    imageLinearGauge2.speedTo(0);
                                }

                                //Gauge3
                                ImageLinearGauge imageLinearGauge3 = findViewById(R.id.imageLinearGauge3);
                                imageLinearGauge3.speedTo(turbo);

                                //Gauge4
                                ImageLinearGauge imageLinearGauge4 = findViewById(R.id.imageLinearGauge4);
                                imageLinearGauge4.speedTo(fordOilTemp);

                                //Gauge5
                                ImageLinearGauge imageLinearGauge5 = findViewById(R.id.imageLinearGauge5);
                                imageLinearGauge5.speedTo(fuel);

                                //Gauge6
                                ImageLinearGauge imageLinearGauge6 = findViewById(R.id.imageLinearGauge6);
                                imageLinearGauge6.speedTo(coolant);
                            } else if (getVehicleType() == VGM1 || getVehicleType() == VGM2 || getVehicleType() == VRAM) {
                                int oilPressure = variables.getInt("oil_pressur");
                                //Gauge1
                                ImageLinearGauge imageLinearGauge1 = findViewById(R.id.imageLinearGauge1);
                                imageLinearGauge1.speedTo(egt);

                                //Gauge2
                                ImageLinearGauge imageLinearGauge2 = findViewById(R.id.imageLinearGauge2);
                                if (boost > 5) {
                                    imageLinearGauge2.speedTo((float) (boost * 0.1450377));
                                } else {
                                    imageLinearGauge2.speedTo(0);
                                }

                                //Gauge3
                                ImageLinearGauge imageLinearGauge3 = findViewById(R.id.imageLinearGauge3);
                                imageLinearGauge3.speedTo(turbo);

                                //Gauge4
                                ImageLinearGauge imageLinearGauge4 = findViewById(R.id.imageLinearGauge4);
                                imageLinearGauge4.speedTo(oilPressure);

                                //Gauge5
                                ImageLinearGauge imageLinearGauge5 = findViewById(R.id.imageLinearGauge5);
                                imageLinearGauge5.speedTo(fuel);

                                //Gauge6
                                ImageLinearGauge imageLinearGauge6 = findViewById(R.id.imageLinearGauge6);
                                imageLinearGauge6.speedTo(coolant);
                            }

                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }

                        isProcessing = false;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isConnected = false;
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
            case R.id.btn_home:
                startActivity(new Intent(LiveDataBarActivity.this, MainActivity.class));
                break;
            case R.id.moreGauges:
                startActivity(new Intent(LiveDataBarActivity.this, LiveDataBarActivity2.class));
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
}


