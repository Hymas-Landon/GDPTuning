//package com.gdptuning.gdptuning;
//
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.net.wifi.WifiManager;
//import android.os.Bundle;
//import android.provider.Settings;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//import android.view.View;
//import android.view.Window;
//import android.view.WindowManager;
//import android.widget.Button;
//import android.widget.TextView;
//
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.JsonObjectRequest;
//import com.android.volley.toolbox.Volley;
//import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.Timer;
//import java.util.TimerTask;
//
//import de.nitri.gauge.Gauge;
//
//public class LiveDataActivity2 extends AppCompatActivity {
//
//    //ESP32 aREST server address
//    final String url = "http://192.168.7.1";
//    //    final String url = "https://api.myjson.com/bins/fxv1w";
//    boolean isConnected = false;
//    boolean isProcessing = false;
//    String device = "GDP";
//    int tuneMode = 0;
//    Timer timer;
//    private static int VFORD1 = 7;
//    private static int VFORD2 = 8;
//    private static int VGM1 = 9;
//    private static int VGM2 = 10;
//    private static int VRAM = 11;
//    TextView tvTiming, tvGear, tvTune, tvFrp;
//    Button btn_home, btn_back;
//    RequestQueue queue;
//    WifiManager wifi;
//
//    //Gauges
//    Gauge gauge2_1;
//    Gauge gauge2_2;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        if (getColorTheme() == Utils.THEME_DEFAULT) {
//            setTheme(R.style.AppThemeNoActionBarOrangeMain);
//        } else if (getColorTheme() == Utils.THEME_GREEN) {
//            setTheme(R.style.AppThemeNoActionBarGreen);
//        } else if (getColorTheme() == Utils.THEME_BLUE) {
//            setTheme(R.style.AppThemeNoActionBarBlue);
//        } else if (getColorTheme() == Utils.THEME_RED) {
//            setTheme(R.style.AppThemeNoActionBarRed);
//        }
//        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//
//        setContentView(R.layout.activity_live_data2);
//
////        //set widget home
////        btn_home = findViewById(R.id.btn_home);
////        btn_back = findViewById(R.id.back);
////
////        //connect textViews
////        tvTiming = findViewById(R.id.timing);
////        tvFrp = findViewById(R.id.frp);
////        tvTune = findViewById(R.id.tunenum);
////        tvGear = findViewById(R.id.gear_position);
//
//        //onclick
//        btn_home.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View mView) {
//                startActivity(new Intent(LiveDataActivity2.this, MainActivity.class));
//            }
//        });
//
//
//        //Gauges information
//        gauge2_1 = findViewById(R.id.gauge2_1);
//        gauge2_2 = findViewById(R.id.gauge2_2);
//
//        //Working with wifi
//        queue = Volley.newRequestQueue(this);
//        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        sendRequest();
//        timer = new Timer();
//        timer.scheduleAtFixedRate(new TimerTask() {
//            int num = 1;
//
//            @Override
//            public void run() {
//                if (isConnected) {
//                    if (!isProcessing) {
//                        Log.d("TEST2 :", "Sending request");
//                        updateRequest();
//                    }
//                }
//            }
//        }, 0, 1);//put here time 1000 milliseconds=1 second
//    }
//
//    private int getColorTheme() {
//        SharedPreferences mSharedPreferences = getSharedPreferences("ThemeColor", MODE_PRIVATE);
//        return mSharedPreferences.getInt("theme", Utils.THEME_DEFAULT);
//    }
//
//    private int getVehicleType() {
//        SharedPreferences mSharedPreferences = getSharedPreferences("ThemeColor", MODE_PRIVATE);
//        return mSharedPreferences.getInt("vehicle", VFORD1);
//    }
//
//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        if (hasFocus) {
//            getWindow().getDecorView().setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//        }
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        timer.cancel();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        timer = new Timer();
//        timer.scheduleAtFixedRate(new TimerTask() {
//            int num = 1;
//
//            @Override
//            public void run() {
//                if (isConnected) {
//                    if (!isProcessing) {
//                        Log.d("TEST2 :", "Sending request");
//                        updateRequest();
//                    }
//                }
//            }
//        }, 0, 1);//put here time 1000 milliseconds=1 second
//    }
//
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        Intent i = new Intent(LiveDataActivity2.this, LiveDataActivity.class);
//        startActivity(i);
//    }
//
//    //Send to sGDP server to verify connection
//    public void sendRequest() {
//        // prepare the Request
//        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        isConnected = true;
//                        try {
//                            JSONObject variables = response.getJSONObject("variables");
//                            Log.d("TEST2 ", variables.toString());
//                            tuneMode = variables.getInt("tune_mode");
//                            int gear = variables.getInt("gear");
//                            String deviceName = response.getString("name");
//                            deviceName += response.getString("id");
//                            device = deviceName;
//
//                            char pos = (char) gear;
//
//                            tvTune.setText("TUNE: " + tuneMode);
//                            tvGear.setText("GEAR: " + pos);
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                        // display response
//                        Log.d("Response", response.toString());
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        isConnected = false;
//                        Log.d("Error.Response", error.toString());
//
//                        new SweetAlertDialog(LiveDataActivity2.this, SweetAlertDialog.WARNING_TYPE)
//                                .setTitleText("No Connection")
//                                .setContentText("You are not connected to a GDP device")
//                                .setCancelText("Retry")
//                                .setConfirmText("Connect")
//                                .showCancelButton(true)
//                                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                                    @Override
//                                    public void onClick(SweetAlertDialog sDialog) {
//                                        sendRequest();
//                                        sDialog.dismiss();
//                                    }
//                                })
//                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                                    @Override
//                                    public void onClick(SweetAlertDialog sDialog) {
//                                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
//                                    }
//                                }).show();
//                    }
//                }
//        );
//        // add it to the RequestQueue
//        queue.add(getRequest);
//    }
//
//    //Send to sGDP server to get live data
//    public void updateRequest() {
//        isProcessing = true;
//        // prepare the Request
//        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        isConnected = true;
//                        try {
//                            JSONObject variables = response.getJSONObject("variables");
//                            Log.d("TEST2 ", variables.toString());
//                            int tuneMode = variables.getInt("tune_mode");
//                            int gear = variables.getInt("gear");
//                            String deviceName = response.getString("name");
//                            deviceName += response.getString("id");
//                            device = deviceName;
//
//                            char pos = (char) gear;
//
//                            tvTune.setText("TUNE: " + tuneMode);
//                            tvGear.setText("GEAR: " + pos);
//
//                            float frp = variables.getInt("frp");
//                            float timing = variables.getInt(("timing"));
//
//                            float frpText = (float) (frp * 145.0377);
//
//                            tvFrp.setText(String.valueOf(frpText));
//                            tvTiming.setText(String.valueOf(timing));
//
//                            //Gauge1
//                            gauge2_1.setMajorNickInterval(100);
//                            gauge2_1.setValuePerNick(50);
//                            gauge2_1.setMinValue(0);
//                            gauge2_1.setMaxValue(35000);
//                            gauge2_1.setTotalNicks(800);
//                            gauge2_1.setValue(frpText);
//
//                            //Gauge2
//                            gauge2_2.setMajorNickInterval(10);
//                            gauge2_2.setValuePerNick(1);
//                            gauge2_2.setMinValue(-40);
//                            gauge2_2.setMaxValue(40);
//                            gauge2_2.setTotalNicks(100);
//                            gauge2_2.setValue(timing);
//
//                        } catch (JSONException e1) {
//                            e1.printStackTrace();
//                        }
//
//                        isProcessing = false;
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        isConnected = false;
//                        Log.d("Error.Response", error.toString());
//
//                        new SweetAlertDialog(LiveDataActivity2.this, SweetAlertDialog.WARNING_TYPE)
//                                .setTitleText("No Connection")
//                                .setContentText("Your are not connected to GDP device")
//                                .setCancelText("Retry")
//                                .setConfirmText("Connect")
//                                .showCancelButton(true)
//                                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                                    @Override
//                                    public void onClick(SweetAlertDialog sDialog) {
//                                        sendRequest();
//                                        sDialog.dismiss();
//                                    }
//                                })
//                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                                    @Override
//                                    public void onClick(SweetAlertDialog sDialog) {
//                                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
//                                    }
//                                })
//                                .show();
//
//                        isProcessing = false;
//                    }
//                }
//        );
//        // add it to the RequestQueue
//        queue.add(getRequest);
//    }
//
//    //Show Connection details
//    void displayDevicecInfo() {
//        if (isConnected) {
//            new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
//                    .setTitleText("Connected")
//                    .setContentText("You are connected to " + device)
//                    .setConfirmText("ok")
//                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                        @Override
//                        public void onClick(SweetAlertDialog sDialog) {
//                            // reuse previous dialog instance
//                            sDialog.dismiss();
//                        }
//                    })
//                    .show();
//        } else {
//            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
//                    .setTitleText("No Connection")
//                    .setContentText("You are not connected to a GDP device")
//                    .setCancelText("Retry")
//                    .setConfirmText("Connect")
//                    .showCancelButton(true)
//                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                        @Override
//                        public void onClick(SweetAlertDialog sDialog) {
//                            sendRequest();
//                            sDialog.dismiss();
//                        }
//                    })
//                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                        @Override
//                        public void onClick(SweetAlertDialog sDialog) {
//                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
//                        }
//                    })
//                    .show();
//        }
//    }
//}
