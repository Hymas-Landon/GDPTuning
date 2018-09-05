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
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import de.nitri.gauge.Gauge;

public class LiveDataActivity extends AppCompatActivity implements View.OnClickListener {

    //ESP32 aREST server address
    final String url = "http://192.168.7.1";
    //        final String url = "https://api.myjson.com/bins/17x8hg";
    boolean isConnected = false;
    boolean isProcessing = false;
    String device = "GDP";
    int tuneMode = 0;
    Timer timer;
    private static int VFORD1 = 7;
    private static int VFORD2 = 8;
    private static int VGM1 = 9;
    private static int VGM2 = 10;
    private static int VRAM = 11;
    TextView tvBoostView, tvEgt, tvOilPressure, tvFuel, tvTurbo, tvCoolant,
            tvGear, tvTune;
    Button btn_home, btn_more;
    RequestQueue queue;
    WifiManager wifi;

    //Gauges
    Gauge gauge1;
    Gauge gauge2;
    Gauge gauge3;
    Gauge gauge4;
    Gauge gauge5;
    Gauge gauge6;

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

        setContentView(R.layout.activity_live_data);

        //set widget home
        btn_home = findViewById(R.id.btn_home);

        //connect textViews
        tvEgt = findViewById(R.id.egt);
        tvBoostView = findViewById(R.id.boost);
        tvTurbo = findViewById(R.id.turbo);
        tvOilPressure = findViewById(R.id.oil_pressure);
        tvFuel = findViewById(R.id.fuel_rate);
        tvCoolant = findViewById(R.id.coolant);
        btn_more = findViewById(R.id.moreGauges);
        tvTune = findViewById(R.id.tunenum);
        tvGear = findViewById(R.id.gear_position);

        //onclick
        btn_home.setOnClickListener(this);
        btn_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LiveDataActivity.this, LiveDataActivity2.class);
                startActivity(i);
            }
        });

        //Gauges information
        gauge1 = findViewById(R.id.gauge1);
        gauge2 = findViewById(R.id.gauge2);
        gauge3 = findViewById(R.id.gauge3);
        gauge4 = findViewById(R.id.gauge4);
        gauge5 = findViewById(R.id.gauge5);
        gauge6 = findViewById(R.id.gauge6);

        //Working with wifi
        queue = Volley.newRequestQueue(this);
        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
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
        }, 0, 1);//put here time 1000 milliseconds=1 second
    }

    private int getColorTheme() {
        SharedPreferences mSharedPreferences = getSharedPreferences("ThemeColor", MODE_PRIVATE);
        return mSharedPreferences.getInt("theme", Utils.THEME_DEFAULT);
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

    private int getVehicleType() {
        SharedPreferences mSharedPreferences = getSharedPreferences("ThemeColor", MODE_PRIVATE);
        return mSharedPreferences.getInt("vehicle", VFORD1);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(LiveDataActivity.this, MainActivity.class);
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
        }, 0, 1);//put here time 1000 milliseconds=1 second
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

                        new SweetAlertDialog(LiveDataActivity.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("No Connection")
                                .setContentText("You are not connected to a GDP device")
                                .setCancelText("Retry")
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
                                }).show();
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
                            Log.d("TEST2 ", variables.toString());
                            int tuneMode = variables.getInt("tune_mode");
                            int gear = variables.getInt("gear");
                            String deviceName = response.getString("name");
                            deviceName += response.getString("id");
                            device = deviceName;

                            char pos = (char) gear;

                            tvTune.setText("TUNE: " + tuneMode);
                            tvGear.setText("GEAR: " + pos);

                            int egt = variables.getInt("egt");
                            int boost = variables.getInt("boost");
                            int turbo = variables.getInt("egt");
                            int fuel = variables.getInt("fule");
                            int coolant = variables.getInt("coolant");
                            int egtText = (int) (egt * 1.8 + 32);
                            int boostText = (int) ((boost * 0.1450377));
                            int coolantText = (int) (coolant * 1.8 + 32);

                            if (getVehicleType() == VFORD1 || getVehicleType() == VFORD2) {
                                int fordOilTemp = variables.getInt("myOilTemp");
                                int fordOilText = (int) (fordOilTemp * 0.145);

                                //Set the text on the gauges
                                tvEgt.setText(String.valueOf(egtText + "\u2109"));
                                tvBoostView.setText(String.valueOf(boostText + " PSI"));
                                tvTurbo.setText(String.valueOf(turbo + " %"));
                                tvOilPressure.setText(String.valueOf(fordOilText + " PSI"));
                                tvFuel.setText(String.valueOf(fuel + " MM3"));
                                tvCoolant.setText(String.valueOf(coolantText + "\u2109"));

                                //Gauge 1
                                gauge1.setMajorNickInterval(10);
                                gauge1.setValuePerNick(20);
                                gauge1.setMinValue(0);
                                gauge1.setMaxValue(2000);
                                gauge1.setTotalNicks(120);
                                gauge1.setValue((float) ((egt * 1.8) + 32));

                                //Gauge 2
                                gauge2.setMajorNickInterval(5);
                                gauge2.setValuePerNick(1);
                                gauge2.setMinValue(0);
                                gauge2.setMaxValue(70);
                                gauge2.setTotalNicks(90);
                                if (boost > 5) {
                                    gauge2.setValue((float) (boost * 0.1450377));
                                } else {
                                    gauge2.setValue(0);
                                }

                                //Gauge 3
                                gauge3.setMajorNickInterval(10);
                                gauge3.setValuePerNick(1);
                                gauge3.setMinValue(0);
                                gauge3.setMaxValue(100);
                                gauge3.setTotalNicks(140);
                                gauge3.setValue(turbo);

                                //Gauge 4
                                gauge4.setMajorNickInterval(40);
                                gauge4.setValuePerNick(1);
                                gauge4.setMinValue(0);
                                gauge4.setMaxValue(350);
                                gauge4.setTotalNicks(520);
                                gauge4.setValue((float) (fordOilTemp * 0.145));

                                //Gauge 5
                                gauge5.setMajorNickInterval(20);
                                gauge5.setValuePerNick(1);
                                gauge5.setMinValue(0);
                                gauge5.setMaxValue(160);
                                gauge5.setTotalNicks(200);
                                gauge5.setValue(fuel);

                                //Gauge 6
                                gauge6.setMajorNickInterval(40);
                                gauge6.setValuePerNick(1);
                                gauge6.setMinValue(-40);
                                gauge6.setMaxValue(320);
                                gauge6.setTotalNicks(480);
                                gauge6.setValue((float) ((coolant * 1.8) + 32));


                            } else if (getVehicleType() == VGM1 || getVehicleType() == VGM2 || getVehicleType() == VRAM) {
                                int oilPressure = variables.getInt("oil_pressur");
                                int oilPressureText = (int) (oilPressure * 0.145);

                                //Set the text on the gauges
                                tvEgt.setText(String.valueOf(egtText + "\u2109"));
                                tvBoostView.setText(String.valueOf(boostText + " PSI"));
                                tvTurbo.setText(String.valueOf(turbo + " %"));
                                tvOilPressure.setText(String.valueOf(oilPressureText + " PSI"));
                                tvFuel.setText(String.valueOf(fuel + " MM3"));
                                tvCoolant.setText(String.valueOf(coolantText + "\u2109"));

                                //Gauge 1
                                gauge1.setMajorNickInterval(10);
                                gauge1.setValuePerNick(20);
                                gauge1.setMinValue(0);
                                gauge1.setMaxValue(2000);
                                gauge1.setTotalNicks(120);
                                gauge1.setValue((float) ((egt * 1.8) + 32));

                                //Gauge 2
                                gauge2.setMajorNickInterval(5);
                                gauge2.setValuePerNick(1);
                                gauge2.setMinValue(0);
                                gauge2.setMaxValue(70);
                                gauge2.setTotalNicks(90);
                                if (boost > 5) {
                                    gauge2.setValue((float) (boost * 0.1450377));
                                } else {
                                    gauge2.setValue(0);
                                }

                                //Gauge 3
                                gauge3.setMajorNickInterval(10);
                                gauge3.setValuePerNick(1);
                                gauge3.setMinValue(0);
                                gauge3.setMaxValue(100);
                                gauge3.setTotalNicks(140);
                                gauge3.setValue(turbo);

                                //Gauge 4
                                gauge4.setMajorNickInterval(40);
                                gauge4.setValuePerNick(1);
                                gauge4.setMinValue(0);
                                gauge4.setMaxValue(380);
                                gauge4.setTotalNicks(520);
                                gauge4.setValue((float) (oilPressure * 0.145));

                                //Gauge 5
                                gauge5.setMajorNickInterval(20);
                                gauge5.setValuePerNick(1);
                                gauge5.setMinValue(0);
                                gauge5.setMaxValue(160);
                                gauge5.setTotalNicks(200);
                                gauge5.setValue(fuel);

                                //Gauge 6
                                gauge6.setMajorNickInterval(40);
                                gauge6.setValuePerNick(1);
                                gauge6.setMinValue(-40);
                                gauge6.setMaxValue(320);
                                gauge6.setTotalNicks(480);
                                gauge6.setValue((float) ((coolant * 1.8) + 32));
                            }

                            Log.d("Response", response.toString());
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

                        new SweetAlertDialog(LiveDataActivity.this, SweetAlertDialog.WARNING_TYPE)
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
                startActivity(new Intent(LiveDataActivity.this, MainActivity.class));
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
