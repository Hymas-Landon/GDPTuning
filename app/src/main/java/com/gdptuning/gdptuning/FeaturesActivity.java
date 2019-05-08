package com.gdptuning.gdptuning;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
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
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;


public class FeaturesActivity extends AppCompatActivity {

    private static int VFORD1 = 7;
    private static int VFORD2 = 8;
    private static int VGM1 = 9;
    private static int VGM2 = 10;
    private static int VRAM = 11;
    //ESP32 aREST server address
    final String url = "http://192.168.7.1";
    final String themeColor = "ThemeColor";
    final String vehicleSettings = "vehicle";
    final String boostVar = "boost";
    final String tpmsSettings = "pressure_tpms";
    final String tireSizeSettings = "tire_sizes";
    final String daytimeLightsSettings = "daytime_lights";
    final String remoteStartSettings = "remote_start";
    final String navOverrideSettings = "nav_override";
    final String lampCurrentSettings = "lamp_current";
    final String fogLightsSettings = "fog_lights";
    final String remoteWindowSettings = "remote_window";
    final String highIdleSettings = "high_idle";
    TextView tab1, tab2, tab3;


    private boolean isConnected = false;
    private boolean isProcessing = false;
    String device = "GDP";
    RequestQueue queue;
    Button btn_home, btn_program, btn_default;
    WifiManager wifi;
    TextView tvTune, tvGear;
    Timer timer;
    public static final String TAG = "GDP Tuning";
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    ProgressDialog mProgressDialog;


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
        setContentView(R.layout.activity_features);
        mProgressDialog = new ProgressDialog(this);

        //tab
        mTabLayout = findViewById(R.id.tabs);
        tab1 = findViewById(R.id.tab1);
        tab2 = findViewById(R.id.tab2);
        tab3 = findViewById(R.id.tab3);


        if (getVehicleType() == VFORD1 || getVehicleType() == VFORD2) {
            //add tabs
            mTabLayout.addTab(mTabLayout.newTab().setText("PAGE 1"));
            mTabLayout.addTab(mTabLayout.newTab().setText("PAGE 2"));
            mTabLayout.addTab(mTabLayout.newTab().setText("Bonus Features"));
            mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

            //set viewPager
            mViewPager = findViewById(R.id.container);

            PagerFeatures adapter = new PagerFeatures(getSupportFragmentManager(), mTabLayout.getTabCount());
            mViewPager.setAdapter(adapter);

            tab1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View mView) {
                    mViewPager.setCurrentItem(0);
                }
            });
            tab2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View mView) {
                    mViewPager.setCurrentItem(1);
                }
            });
            tab3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View mView) {
                    mViewPager.setCurrentItem(2);
                }
            });

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
        } else if (getVehicleType() == VGM2 || getVehicleType() == VRAM) {
            //add tabs
            mTabLayout.addTab(mTabLayout.newTab().setText("PAGE 1"));
            mTabLayout.addTab(mTabLayout.newTab().setText("Bonus Features"));
            mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

            //set viewPager
            mViewPager = findViewById(R.id.container);

            PagerFeatures2 adapter = new PagerFeatures2(getSupportFragmentManager(), mTabLayout.getTabCount());
            mViewPager.setAdapter(adapter);

            tab1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View mView) {
                    mViewPager.setCurrentItem(0);
                }
            });
            tab2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View mView) {
                    mViewPager.setCurrentItem(1);
                }
            });

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
        } else if (getVehicleType() == VGM1) {
            //add tabs
            mTabLayout.addTab(mTabLayout.newTab().setText("Bonus Features"));
            mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

            //set viewPager
            mViewPager = findViewById(R.id.container);

            PagerFeatures3 adapter = new PagerFeatures3(getSupportFragmentManager(), mTabLayout.getTabCount());
            mViewPager.setAdapter(adapter);

            tab1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View mView) {
                    mViewPager.setCurrentItem(0);
                }
            });

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



        //Working with wifi
        queue = VolleySingleton.getInstance(this).getRequestQueue();
        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (isFirstTime()) {
            readSettings();
            sendRequest();
        } else {
            sendRequest();
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (isConnected) {
                    if (!isProcessing) {
                        updateSettingsRequest();
                    }
                }
            }
        }, 0, 500);//put here time 1000 milliseconds=1 second

        // Set textView
        btn_default = findViewById(R.id.default_settings);
        btn_default.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {

                // prepare the Request
                JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                isConnected = true;
                                try {
                                    JSONObject variables = response.getJSONObject("variables");

                                    new MyAsyncTaskCode(FeaturesActivity.this).execute();
                                    SharedPreferences mSharedPreferences = getSharedPreferences(themeColor, MODE_PRIVATE);
                                    Log.d(TAG, "onResponse: Did WE make it here?");
                                    SharedPreferences.Editor edit = mSharedPreferences.edit();
                                    int factorySecureIdle = variables.getInt("factory_secure_idle");

                                    if (getVehicleType() == VFORD1 || getVehicleType() == VFORD2) {
                                        int factoryTpms = variables.getInt("factory_tpms");
                                        int factoryDaytimeRunningLights = variables.getInt("factory_drl");
                                        int factoryLampOutage = variables.getInt("factory_lamp_out");
                                        int factoryFogLights = variables.getInt("factory_fog_high");
                                        int factoryTireSize = variables.getInt("factory_tire_size");
                                        int factoryRemoteWindow = variables.getInt("factory_rke_windows");
                                        int factoryRemoteStartDuration = variables.getInt("factory_rvs");
                                        int factoryNavOverride = variables.getInt("factory_nav_override");

                                        edit.putInt(tpmsSettings, factoryTpms);
                                        if (factoryLampOutage == 0) {
                                            edit.putBoolean(lampCurrentSettings, false);
                                        } else if (factoryLampOutage == 1) {
                                            edit.putBoolean(lampCurrentSettings, true);
                                        }
                                        edit.putInt(tireSizeSettings, factoryTireSize);
                                        if (factoryFogLights == 0) {
                                            edit.putBoolean(fogLightsSettings, false);
                                        } else if (factoryFogLights == 1) {
                                            edit.putBoolean(fogLightsSettings, true);
                                        }
                                        edit.putInt(daytimeLightsSettings, factoryDaytimeRunningLights);
                                        edit.putInt(remoteStartSettings, factoryRemoteStartDuration);
                                        edit.putInt(remoteWindowSettings, factoryRemoteWindow);
                                        if (factoryRemoteWindow == 0) {
                                            edit.putBoolean(remoteWindowSettings, false);
                                        } else if (factoryRemoteWindow == 1) {
                                            edit.putBoolean(remoteWindowSettings, true);
                                        }
                                        if (factoryFogLights == 0) {
                                            edit.putBoolean(fogLightsSettings, false);
                                        } else if (factoryFogLights == 1) {
                                            edit.putBoolean(fogLightsSettings, true);
                                        }
                                        if (factoryNavOverride == 0) {
                                            edit.putBoolean(navOverrideSettings, false);
                                        } else if (factoryNavOverride == 1) {
                                            edit.putBoolean(navOverrideSettings, true);
                                        }
                                        if (factorySecureIdle == 0) {
                                            edit.putBoolean(highIdleSettings, false);
                                        } else if (factorySecureIdle == 1) {
                                            edit.putBoolean(highIdleSettings, true);
                                        }
                                        edit.apply();
                                    } else if (getVehicleType() == VGM2) {
                                        int factoryTpms = variables.getInt("factory_tpms");
                                        edit.putInt(tpmsSettings, factoryTpms);
                                        if (factorySecureIdle == 0) {
                                            edit.putBoolean(highIdleSettings, false);
                                        } else if (factorySecureIdle == 1) {
                                            edit.putBoolean(highIdleSettings, true);
                                        }
                                        edit.apply();
                                    } else if (getVehicleType() == VRAM) {
                                        int factoryTpms = variables.getInt("factory_tpms");
                                        int factoryFogLights = variables.getInt("factory_fog_high");
                                        if (factoryFogLights == 0) {
                                            edit.putBoolean(fogLightsSettings, false);
                                        } else if (factoryFogLights == 1) {
                                            edit.putBoolean(fogLightsSettings, true);
                                        }
                                        if (factorySecureIdle == 0) {
                                            edit.putBoolean(highIdleSettings, false);
                                        } else if (factorySecureIdle == 1) {
                                            edit.putBoolean(highIdleSettings, true);
                                        }
                                        edit.putInt(tpmsSettings, factoryTpms);
                                        edit.apply();
                                    }
                                    recreate();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                // display response
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                isConnected = false;
                            }
                        }
                );
                // add it to the RequestQueue
                queue.add(getRequest);
            }
        });
        btn_home = findViewById(R.id.btn_home);
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                Intent i = new Intent(FeaturesActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
        tvGear = findViewById(R.id.gear_position);
        tvTune = findViewById(R.id.tunenum);
        btn_program = findViewById(R.id.program_features);

        btn_program.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                programBCM();
                new MyAsyncTaskCode(FeaturesActivity.this).execute();
            }
        });
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

    private int getColorTheme() {
        SharedPreferences mSharedPreferences = getSharedPreferences(themeColor, MODE_PRIVATE);
        return mSharedPreferences.getInt("theme", Utils.THEME_DEFAULT);
    }

    private int getVehicleType() {
        SharedPreferences mSharedPreferences = getSharedPreferences(themeColor, Context.MODE_PRIVATE);
        return mSharedPreferences.getInt(vehicleSettings, VFORD1);
    }

    private boolean isFirstTime() {
        SharedPreferences mSharedPreferences = getSharedPreferences("ThemeColor", MODE_PRIVATE);
        return mSharedPreferences.getBoolean("first_time", false);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(FeaturesActivity.this, MainActivity.class);
        startActivity(i);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    //Send to sGDP server to verify connection
    public void sendRequest() {
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(final JSONObject response) {
                        isConnected = true;
                        try {
                            JSONObject variables = response.getJSONObject("variables");
                            int tuneMode = variables.getInt("tune_mode");
                            int gear = variables.getInt("gear");
                            String deviceName = response.getString("name");
                            deviceName += response.getString("id");
                            device = deviceName;
                            char pos = (char) gear;
                            String boost = variables.getString(boostVar);

                            if (boost.equals("65535")){
                                new SweetAlertDialog(FeaturesActivity.this, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText("Logging Paused")
                                        .setContentText("Please close any other apps communicating through the OBD II Port, logging should resume.")
                                        .setConfirmText("Okay")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {
                                                sDialog.dismiss();
                                                SharedPreferences mSharedPreferences = getSharedPreferences("ThemeColor", MODE_PRIVATE);
                                                SharedPreferences.Editor edit = mSharedPreferences.edit();

                                                edit.putBoolean("logging", true);
                                            }
                                        })
                                        .show();
                            }
//                            new MyAsyncTaskCode(FeaturesActivity.this).execute();

                            if (tuneMode == 255) {
                                tvTune.setText("TUNE: E");
                            } else {
                                tvTune.setText("TUNE: " + tuneMode);
                            }
                            tvGear.setText("GEAR: " + pos);


                            SharedPreferences mSharedPreferences = getSharedPreferences(themeColor, MODE_PRIVATE);
                            SharedPreferences.Editor edit = mSharedPreferences.edit();
                            if (getVehicleType() == VFORD1 || getVehicleType() == VFORD2) {

                                int tpms = variables.getInt("tpms");
                                int daytimeRunningLights = variables.getInt("drl");
                                int lampOutage = variables.getInt("lamp_out");
                                int fogLights = variables.getInt("fog_high");
                                int tireSize = variables.getInt("tire_size");
                                int remoteWindow = variables.getInt("rke_windows");
                                int remoteStartDuration = variables.getInt("rvs");
                                int navOverride = variables.getInt("nav_override");

                                edit.putInt(tpmsSettings, tpms);
                                if (lampOutage == 0) {
                                    edit.putBoolean(lampCurrentSettings, false);
                                } else if (lampOutage == 1) {
                                    edit.putBoolean(lampCurrentSettings, true);
                                }
                                edit.putInt(tireSizeSettings, tireSize);
                                if (fogLights == 0) {
                                    edit.putBoolean(fogLightsSettings, false);
                                } else if (fogLights == 1) {
                                    edit.putBoolean(fogLightsSettings, true);
                                }
                                edit.putInt(daytimeLightsSettings, daytimeRunningLights);
                                edit.putInt(remoteStartSettings, remoteStartDuration);
                                edit.putInt(remoteWindowSettings, remoteWindow);
                                if (remoteWindow == 0) {
                                    edit.putBoolean(remoteWindowSettings, false);
                                } else if (remoteWindow == 1) {
                                    edit.putBoolean(remoteWindowSettings, true);
                                }
                                if (fogLights == 0) {
                                    edit.putBoolean(fogLightsSettings, false);
                                } else if (fogLights == 1) {
                                    edit.putBoolean(fogLightsSettings, true);
                                }
                                if (navOverride == 0) {
                                    edit.putBoolean(navOverrideSettings, false);
                                } else if (navOverride == 1) {
                                    edit.putBoolean(navOverrideSettings, true);
                                }
                                edit.apply();
                            } else if (getVehicleType() == VGM2) {
                                int tpms = variables.getInt("tpms");
                                edit.putInt(tpmsSettings, tpms);
                                edit.apply();
                            } else if (getVehicleType() == VRAM) {
                                int tpms = variables.getInt("tpms");
                                int fogLights = variables.getInt("fog_high");
                                int tireSize = variables.getInt(tireSizeSettings);
                                edit.putInt(tireSizeSettings, tireSize);
                                if (fogLights == 0) {
                                    edit.putBoolean(fogLightsSettings, false);
                                } else if (fogLights == 1) {
                                    edit.putBoolean(fogLightsSettings, true);
                                }
                                edit.putInt(tpmsSettings, tpms);
                                edit.apply();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // display response
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isConnected = false;
                    }
                }
        );
        // add it to the RequestQueue
        queue.add(getRequest);
    }

    //Send to sGDP server to get live data
    public void updateSettingsRequest() {
        isProcessing = true;
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {
                        isConnected = true;
                        try {
                            JSONObject variables = response.getJSONObject("variables");
                            int tuneMode = variables.getInt("tune_mode");
                            int gear = variables.getInt("gear");
                            String deviceName = response.getString("name");
                            deviceName += response.getString("id");
                            device = deviceName;
                            char pos = (char) gear;

                            if (tuneMode == 255) {
                                tvTune.setText("TUNE: E");
                            } else {
                                tvTune.setText("TUNE: " + tuneMode);
                            }
                            tvGear.setText("GEAR: " + pos);


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

                    }
                }
        );
        // add it to the RequestQueue
        queue.add(getRequest);
    }


    //Send to sGDP server to verify connection
    void programBCM() {
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url + "/diag_functions?params=" + 9, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        isConnected = true;
                        sendRequest();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isConnected = false;

                    }
                }
        );
        // add it to the RequestQueue
        queue.add(getRequest);
    }

    void readSettings() {
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url + "/diag_functions?params=" + 6, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        isConnected = true;
                        sendRequest();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }

                }
        );
        // add it to the RequestQueue
        queue.add(getRequest);
    }
}