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
    final String remoteWindowSettingsChanged = "remote_window";
    final String remoteStartSettingsChanged = "remoteStartSettings_changed";
    final String navOverrideSettingsChanged = "navOverrideSettings_changed";
    final String daytimeLightsSettingsChanged = "daytimeLightsSettings_changed";
    final String tpmsSettingsChanged = "pressure_tpms_changed";
    final String tireSizeSettingsChanged = "tire_size_changed";
    final String lampCurrentSettingsChanged = "lamp_current_changed";
    final String fogLightsSettingsChanged = "fog_lights_changed";
    TextView tab1, tab2, tab3;

    private boolean isConnected = false;
    private boolean isProcessing = false;
    String device = "GDP";
    RequestQueue queue;
    Button btn_home, btn_program, btn_default;
    WifiManager wifi;
    TextView tvTune, tvGear;
    Timer timer;
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
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE);
        setContentView(R.layout.activity_features);

        mProgressDialog = new ProgressDialog(this);

        //tab
        mTabLayout = findViewById(R.id.tabs);
        tab1 = findViewById(R.id.tab1);
        tab2 = findViewById(R.id.tab2);
        tab3 = findViewById(R.id.tab3);


        int mVGM1 = 9;
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
                    btn_default.setVisibility(View.VISIBLE);
                    btn_program.setVisibility(View.VISIBLE);
                }
            });
            tab2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View mView) {
                    mViewPager.setCurrentItem(1);
                    btn_default.setVisibility(View.VISIBLE);
                    btn_program.setVisibility(View.VISIBLE);
                }
            });
            tab3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View mView) {
                    mViewPager.setCurrentItem(2);
                    btn_default.setVisibility(View.INVISIBLE);
                    btn_program.setVisibility(View.INVISIBLE);
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
                    if (position == 2) {
                        btn_default.setVisibility(View.INVISIBLE);
                        btn_program.setVisibility(View.INVISIBLE);
                    } else {
                        btn_default.setVisibility(View.VISIBLE);
                        btn_program.setVisibility(View.VISIBLE);
                    }
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
        } else if (getVehicleType() == mVGM1) {
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

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                // this code will be executed after 3 seconds
                sendRequest();
            }
        }, 3000);

        //Working with wifi
        queue = VolleySingleton.getInstance(this).getRequestQueue();
        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        readSettings();
        new MyAsyncTaskCodeRead(FeaturesActivity.this).execute();
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
                                    SharedPreferences mSharedPreferences = getSharedPreferences(themeColor, MODE_PRIVATE);
                                    SharedPreferences.Editor edit = mSharedPreferences.edit();
                                    int SecureIdle = variables.getInt("secure_idle");

                                    if (getVehicleType() == VFORD1 || getVehicleType() == VFORD2) {
                                        int Tpms = variables.getInt("tpms");
                                        int DaytimeRunningLights = variables.getInt("drl");
                                        int LampOutage = variables.getInt("lamp_out");
                                        int FogLights = variables.getInt("fog_high");
                                        int TireSize = variables.getInt("tire_size");
                                        int RemoteWindow = variables.getInt("rke_windows");
                                        int RemoteStartDuration = variables.getInt("rvs");
                                        int NavOverride = variables.getInt("nav_override");

                                        edit.putInt(tpmsSettings, Tpms);
                                        if (LampOutage == 0) {
                                            edit.putInt(lampCurrentSettings, 0);
                                        } else if (LampOutage == 1) {
                                            edit.putInt(lampCurrentSettings, 1);
                                        }
                                        edit.putInt(tireSizeSettings, TireSize);
                                        if (FogLights == 0) {
                                            edit.putInt(fogLightsSettings, 0);
                                        } else if (FogLights == 1) {
                                            edit.putInt(fogLightsSettings, 1);
                                        }
                                        edit.putInt(daytimeLightsSettings, DaytimeRunningLights);
                                        edit.putInt(remoteStartSettings, RemoteStartDuration);
                                        edit.putInt(remoteWindowSettings, RemoteWindow);
                                        if (RemoteWindow == 0) {
                                            edit.putInt(remoteWindowSettings, 0);
                                        } else if (RemoteWindow == 1) {
                                            edit.putInt(remoteWindowSettings, 1);
                                        }
                                        if (FogLights == 0) {
                                            edit.putInt(fogLightsSettings, 0);
                                        } else if (FogLights == 1) {
                                            edit.putInt(fogLightsSettings, 1);
                                        }
                                        if (NavOverride == 0) {
                                            edit.putInt(navOverrideSettings, 0);
                                        } else if (NavOverride == 1) {
                                            edit.putInt(navOverrideSettings, 1);
                                        }
                                        if (SecureIdle == 0) {
                                            edit.putInt(highIdleSettings, 0);
                                        } else if (SecureIdle == 1) {
                                            edit.putInt(highIdleSettings, 1);
                                        }
                                        edit.apply();
                                    } else if (getVehicleType() == VGM2) {
                                        int Tpms = variables.getInt("tpms");
                                        edit.putInt(tpmsSettings, Tpms);
                                        if (SecureIdle == 0) {
                                            edit.putInt(highIdleSettings, 0);
                                        } else if (SecureIdle == 1) {
                                            edit.putInt(highIdleSettings, 1);
                                        }
                                        edit.apply();
                                    } else if (getVehicleType() == VRAM) {
                                        int Tpms = variables.getInt("tpms");
                                        int FogLights = variables.getInt("fog_high");
                                        if (FogLights == 0) {
                                            edit.putInt(fogLightsSettings, 0);
                                        } else if (FogLights == 1) {
                                            edit.putInt(fogLightsSettings, 1);
                                        }
                                        if (SecureIdle == 0) {
                                            edit.putInt(highIdleSettings, 0);
                                        } else if (SecureIdle == 1) {
                                            edit.putInt(highIdleSettings, 1);
                                        }
                                        edit.putInt(tpmsSettings, Tpms);
                                        edit.apply();
                                    }
                                    new SweetAlertDialog(FeaturesActivity.this, SweetAlertDialog.WARNING_TYPE)
                                            .setTitleText("Default Settings?")
                                            .setContentText("You are about to program your BCM back to factory. Do you want to continue?")
                                            .setCancelText("Cancel")
                                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sDialog) {
                                                    sDialog.dismiss();
                                                }
                                            })
                                            .setConfirmText("Program")
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sDialog) {
                                                    sDialog.dismiss();
                                                    programDefaultBCM();
                                                    new MyAsyncTaskCode(FeaturesActivity.this).execute();
                                                }
                                            })
                                            .show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
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
                new SweetAlertDialog(FeaturesActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Program BCM?")
                        .setContentText("You are about to program your BCM. You are responsible for any and all changes that are made to your BCM and how your vehicle responds to those changes. Do not make changes unless you understand how this will affect your vehicle. Do you want to continue?")
                        .setCancelText("Cancel")
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismiss();
                            }
                        })
                        .setConfirmText("Program")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismiss();
                                programBCM();
                            }
                        })
                        .show();
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

                            String bcm_stat = variables.getString("bcm_stat");
                            if (bcm_stat.equals("12")) {
                                timer.cancel();
                                new SweetAlertDialog(FeaturesActivity.this, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText("Warning")
                                        .setContentText("You are not able to program the BCM while the engine is running. If you wish to do so, please turn the ignition off and then turn it to the run position.")
                                        .setConfirmText("Okay")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {
                                                sDialog.dismiss();
                                            }
                                        })
                                        .show();
                            }

                            if (tuneMode == 255) {
                                tvTune.setText("TUNE: E");
                            } else {
                                tvTune.setText("TUNE: " + tuneMode);
                            }
                            tvGear.setText("GEAR: " + pos);

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
                            String boost = variables.getString(boostVar);
                            if (boost.equals("65535")) {
                                timer.cancel();
                                new SweetAlertDialog(FeaturesActivity.this, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText("Logging Paused")
                                        .setContentText("Please close any other apps communicating through the OBD II Port, logging should resume.")
                                        .setConfirmText("Okay")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {
                                                sDialog.dismiss();
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
                                            }
                                        })
                                        .show();
                            }

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
                        new MyAsyncTaskCodeProgram(FeaturesActivity.this).execute();
                        onWindowFocusChanged(true);
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
    void programDefaultBCM() {
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url + "/diag_functions?params=" + 8, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        isConnected = true;
                        SharedPreferences mSharedPreferences = getSharedPreferences("ThemeColor", MODE_PRIVATE);
                        SharedPreferences.Editor edit = mSharedPreferences.edit();
                        new MyAsyncTaskCodeReset(FeaturesActivity.this).execute();
                        onWindowFocusChanged(true);
                        edit.putBoolean("first_time", true);
                        edit.putInt(navOverrideSettings, 99);
                        edit.putInt(remoteWindowSettings, 99);
                        edit.putInt(remoteStartSettings, 99);
                        edit.putInt(lampCurrentSettings, 99);
                        edit.putInt(tpmsSettings, 99);
                        edit.putInt(tireSizeSettings, 99);
                        edit.putInt(fogLightsSettings, 99);
                        edit.putInt(daytimeLightsSettings, 99);
                        edit.putBoolean(tpmsSettingsChanged, false);
                        edit.putBoolean(lampCurrentSettingsChanged, false);
                        edit.putBoolean(tireSizeSettingsChanged, false);
                        edit.putBoolean(fogLightsSettingsChanged, false);
                        edit.putBoolean(daytimeLightsSettingsChanged, false);
                        edit.putBoolean(remoteWindowSettingsChanged, false);
                        edit.putBoolean(remoteStartSettingsChanged, false);
                        edit.putBoolean(navOverrideSettingsChanged, false);
                        edit.apply();
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
                        onWindowFocusChanged(true);
                        isConnected = true;
                        SharedPreferences mSharedPreferences = getSharedPreferences("ThemeColor", MODE_PRIVATE);
                        SharedPreferences.Editor edit = mSharedPreferences.edit();
                        edit.putBoolean("first_time", false);
                        edit.apply();
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