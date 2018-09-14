package com.gdptuning.gdptuning;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class LiveDataActivityNewSpeed extends AppCompatActivity {
//    //ESP32 aREST server address
//    final String url = "http://192.168.7.1";
//    //        final String url = "https://api.myjson.com/bins/17x8hg";
//    boolean isConnected = false;
//    boolean isProcessing = false;
//    String device = "GDP";
//    int tuneMode = 0;
//    private static int VFORD1 = 7;
//    private static int VFORD2 = 8;
//    private static int VGM1 = 9;
//    private static int VGM2 = 10;
//    private static int VRAM = 11;
//    String faren = "℉";
//    String cels = "\u2103";
//    Timer timer;
//    TextView tvBoostView, tvEgt, tvOilPressure, tvFuel, tvTurbo, tvCoolant, tvGear, tvTune;
//    Button btn_home, btn_more;
//    RequestQueue queue;
//    WifiManager wifi;
//
//    //Gauges
//    Gauge gauge1;
//    Gauge gauge2;
//    Gauge gauge3;
//    Gauge gauge4;
//    Gauge gauge5;
//    Gauge gauge6;

    private ViewPager mViewPager;
    private TabLayout mTabLayout;

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
        setContentView(R.layout.activity_live_data3);

        //tab id
        mTabLayout = findViewById(R.id.tabs_digital);

        //add tabs
        mTabLayout.addTab(mTabLayout.newTab().setText("Page 1"));
        mTabLayout.addTab(mTabLayout.newTab().setText("Page 2"));
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //set viewPager
        mViewPager = findViewById(R.id.container_digital);

        PagerDigital adapter = new PagerDigital(getSupportFragmentManager(), mTabLayout.getTabCount());
        mViewPager.setAdapter(adapter);

        //swipe code
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mTabLayout.setScrollPosition(position, 0, true);
                mTabLayout.setSelected(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


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

}

//        //set widget home
//        btn_home = findViewById(R.id.btn_home);
//        btn_more = findViewById(R.id.moreGauges);
//
//        //connect textViews
//        tvEgt = findViewById(R.id.egt);
//        tvBoostView = findViewById(R.id.boost);
//        tvTurbo = findViewById(R.id.turbo);
//        tvOilPressure = findViewById(R.id.oil_pressure);
//        tvFuel = findViewById(R.id.fuel_rate);
//        tvCoolant = findViewById(R.id.coolant);
//        btn_more = findViewById(R.id.moreGauges);
//        tvGear = findViewById(R.id.gear_position);
//        tvTune = findViewById(R.id.tunenum);
//
//        //Gauges information
//        gauge1 = findViewById(R.id.gauge1);
//        gauge2 = findViewById(R.id.gauge2);
//        gauge3 = findViewById(R.id.gauge3);
//        gauge4 = findViewById(R.id.gauge4);
//        gauge5 = findViewById(R.id.gauge5);
//        gauge6 = findViewById(R.id.gauge6);


//        //onclick
//        btn_home.setOnClickListener(this);
//        btn_more.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(LiveDataActivityNewSpeed.this, LiveDataActivityNewSpeed2.class);
//                startActivity(i);
//            }
//        });
//
//
//        //Working with wifi
//        queue = Volley.newRequestQueue(this);
//        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        sendRequest();
//        timer = new Timer();
//        timer.scheduleAtFixedRate(new TimerTask() {
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
//        }, 0, 500);//put here time 1000 milliseconds=1 second
//    }

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
//    private boolean isMetric() {
//        SharedPreferences mSharedPreferences = getSharedPreferences("ThemeColor", MODE_PRIVATE);
//        return mSharedPreferences.getBoolean("metric", false);
//    }
//
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
//        isProcessing = false;
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
//        }, 0, 500);//put here time 1000 milliseconds=1 second
//    }
//
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        Intent i = new Intent(LiveDataActivityNewSpeed.this, MainActivity.class);
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
//                        new SweetAlertDialog(LiveDataActivityNewSpeed.this, SweetAlertDialog.WARNING_TYPE)
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
//
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
//                            float egt = variables.getInt("egt");
//                            float boost = variables.getInt("boost");
//                            float turbo = variables.getInt("turbo");
//                            float fuel = variables.getInt("fule");
//                            float coolant = variables.getInt("coolant");
//
//                            if (getVehicleType() == VFORD1 || getVehicleType() == VFORD2) {
//                                TextView oilText = findViewById(R.id.title4);
//                                oilText.setText("Oil \nTemp");
//                                float fordOilTemp = variables.getInt("oil_temp");
//                                //Gauge1
//                                ImageSpeedometer imageSpeedometer1 = findViewById(R.id.speedGauge1);
//                                imageSpeedometer1.speedTo((float) ((egt * 1.8) + 32));
//
//                                //Gauge2
//                                ImageSpeedometer imageSpeedometer2 = findViewById(R.id.speedGauge2);
//                                if (boost > 5) {
//                                    imageSpeedometer2.speedTo((float) (boost * 0.1450377));
//                                } else {
//                                    imageSpeedometer2.speedTo(0);
//                                }
//
//                                //Gauge3
//                                ImageSpeedometer imageSpeedometer3 = findViewById(R.id.speedGauge3);
//                                imageSpeedometer3.speedTo(turbo);
//
//                                //Gauge4
//                                ImageSpeedometer imageSpeedometer4 = findViewById(R.id.speedGauge4);
//                                imageSpeedometer4.speedTo((float) ((fordOilTemp * 1.8) + 32));
//
//                                //Gauge5
//                                ImageSpeedometer imageSpeedometer5 = findViewById(R.id.speedGauge5);
//                                imageSpeedometer5.speedTo(fuel);
//
//                                //Gauge6
//                                ImageSpeedometer imageSpeedometer6 = findViewById(R.id.speedGauge6);
//                                imageSpeedometer6.speedTo((float) ((coolant * 1.8) + 32));
//                            } else if (getVehicleType() == VGM1 || getVehicleType() == VGM2 || getVehicleType() == VRAM) { //Gauge1
//                                TextView oilText = findViewById(R.id.title4);
//                                oilText.setText("Oil \nPressure");
//                                float oilPressure = variables.getInt("oil_pressur");
//                                final ImageSpeedometer imageSpeedometer1 = findViewById(R.id.speedGauge1);
//                                imageSpeedometer1.speedTo((float) ((egt * 1.8) + 32));
//
//                                //Gauge2
//                                ImageSpeedometer imageSpeedometer2 = findViewById(R.id.speedGauge2);
//                                if (boost > 5) {
//                                    imageSpeedometer2.speedTo((float) (boost * 0.1450377));
//                                } else {
//                                    imageSpeedometer2.speedTo(0);
//                                }
//
//                                //Gauge3
//                                ImageSpeedometer imageSpeedometer3 = findViewById(R.id.speedGauge3);
//                                imageSpeedometer3.speedTo(turbo);
//
//                                //Gauge4
//                                ImageSpeedometer imageSpeedometer4 = findViewById(R.id.speedGauge4);
//                                imageSpeedometer4.speedTo((float) (oilPressure * 0.145));
//
//                                //Gauge5
//                                ImageSpeedometer imageSpeedometer5 = findViewById(R.id.speedGauge5);
//                                imageSpeedometer5.speedTo(fuel);
//
//                                //Gauge6
//                                ImageSpeedometer imageSpeedometer6 = findViewById(R.id.speedGauge6);
//                                imageSpeedometer6.speedTo((float) ((coolant * 1.8) + 32));
//                            }
//                        } catch (JSONException e1) {
//                            e1.printStackTrace();
//                        }
//
//                        isProcessing = false;
//                    }
//                },
//                new Response.ErrorListener()
//
//                {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        isConnected = false;
//                        Log.d("Error.Response", error.toString());
//
//                        new SweetAlertDialog(LiveDataActivityNewSpeed.this, SweetAlertDialog.WARNING_TYPE)
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
//    @Override
//    public void onClick(View v) {
//
//        int id = v.getId();
//
//        switch (id) {
//            case R.id.btn_home:
//                startActivity(new Intent(LiveDataActivityNewSpeed.this, MainActivity.class));
//                break;
//        }
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
