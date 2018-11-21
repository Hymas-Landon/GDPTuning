package com.gdptuning.gdptuning;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
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
    final String readSettingsSettings = "read_settings";
    final String tpmsSettings = "pressure_tpms";
    final String tireSizeSettings = "tire_sizes";
    final String daytimeLightsSettings = "daytime_lights";
    final String remoteStartSettings = "remote_start";
    final String navOverrideSettings = "nav_override";
    final String lampCurrentSettings = "lamp_current";
    final String fogLightsSettings = "fog_lights";
    final String remoteWindowSettings = "remote_window";
    final String strobeSettings = "strobe_lights";
    final String workLightSettings = "work_lights";
    final String aux1Settings = "aux1_var";
    final String aux2Settings = "aux2_var";
    final String aux3Settings = "aux3_var";
    final String highIdleSettings = "high_idle";


    private boolean isConnected = false;
    private boolean isProcessing = false;
    String device = "GDP";
    RequestQueue queue;
    Button btn_home, btn_program, btn_read, btn_default;
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

        //tab id
        mTabLayout = findViewById(R.id.tabs);

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
                                    if (isRead()) {
                                        JSONObject variables = response.getJSONObject("variables");
                                        Log.d("TEST2 ", variables.toString());

                                        SharedPreferences mSharedPreferences = getSharedPreferences(themeColor, MODE_PRIVATE);
                                        SharedPreferences.Editor edit = mSharedPreferences.edit();
                                        int factoryHighIdle = variables.getInt("factory_secure_idle");

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
                                            if (factoryHighIdle == 0) {
                                                edit.putBoolean(highIdleSettings, false);
                                            } else if (factoryHighIdle == 1) {
                                                edit.putBoolean(highIdleSettings, true);
                                            }
                                            edit.apply();
                                        } else if (getVehicleType() == VGM2) {
                                            int factoryTpms = variables.getInt("factory_tpms");
                                            edit.putInt(tpmsSettings, factoryTpms);
                                            if (factoryHighIdle == 0) {
                                                edit.putBoolean(highIdleSettings, false);
                                            } else if (factoryHighIdle == 1) {
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
                                            if (factoryHighIdle == 0) {
                                                edit.putBoolean(highIdleSettings, false);
                                            } else if (factoryHighIdle == 1) {
                                                edit.putBoolean(highIdleSettings, true);
                                            }
                                            edit.putInt(tpmsSettings, factoryTpms);
                                            edit.apply();
                                        }
                                        recreate();
                                    } else {
                                        Toast toast = Toast.makeText(FeaturesActivity.this, "You must first 'READ SETTINGS' from the current settings on your Vehicle", Toast.LENGTH_SHORT);
                                        toast.show();
                                    }
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
        btn_read = findViewById(R.id.read_settings);
        btn_read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                // prepare the Request
                readSettings();
            }
        });

        btn_program.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                if (isRead()) {
                    programBCM();
                } else {
                    Toast toast = Toast.makeText(FeaturesActivity.this, "You must first 'READ SETTINGS' from the current settings on your Vehicle", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        //Working with wifi
        queue = VolleySingleton.getInstance(this).getRequestQueue();
        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        sendRequest();
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (isConnected) {
                    if (!isProcessing) {
                        Log.d("TEST2 :", "Sending request");
                        updateSettingsRequest();
                    }
                }

            }
        }, 0, 500);//put here time 1000 milliseconds=1 second
    }

    public boolean isRead() {
        SharedPreferences mSharedPreferences = getSharedPreferences(themeColor, MODE_PRIVATE);
        return mSharedPreferences.getBoolean(readSettingsSettings, false);
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
                    @Override
                    public void onResponse(JSONObject response) {
                        isConnected = true;
                        try {
                            JSONObject variables = response.getJSONObject("variables");
                            Log.d("TEST2 ", variables.toString());
                            int tuneMode = variables.getInt("tune_mode");
                            String gear = variables.getString("gear");
                            String deviceName = response.getString("name");
                            deviceName += response.getString("id");
                            device = deviceName;

                            tvTune.setText("TUNE: " + tuneMode);
                            tvGear.setText("GEAR: " + gear);

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
                        Log.d("Response", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isConnected = false;
                        Log.d("Error.Response", error.toString());
                    }
                }
        );
        // add it to the RequestQueue
        queue.add(getRequest);
    }

    public void pause() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException mE) {
            mE.printStackTrace();
        }
    }

    //Send to sGDP server to get live data
    public void updateSettingsRequest() {
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
                            int bcmStatus = variables.getInt("bcm_stat");
                            switch (bcmStatus) {
                                // Nothing
                                case 0:
                                    Log.d(TAG, "UPDATEDEDED:0");
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    mProgressDialog.hide();
                                    break;
                                // Factory BCM read in progress
                                case 1:
                                    Log.d(TAG, "UPDATEDEDED:1");
                                    mProgressDialog.setMessage("Reading configuration from vehicle. Please wait...");
                                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    mProgressDialog.show();
                                    break;
                                // Factory BCM read successfully completed
                                case 2:
                                    Log.d(TAG, "UPDATEDEDED:2");
                                    pause();
                                    mProgressDialog.hide();
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    new SweetAlertDialog(FeaturesActivity.this, SweetAlertDialog.WARNING_TYPE)
                                            .setTitleText("Factory Read Successfully")
                                            .setConfirmText("OK")
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sDialog) {
                                                    sDialog.dismiss();
                                                    sendRequest();
                                                    recreate();
                                                }
                                            })
                                            .show();
                                    SharedPreferences mSharedPreferences = getSharedPreferences(themeColor, MODE_PRIVATE);
                                    SharedPreferences.Editor edit = mSharedPreferences.edit();
                                    edit.putBoolean(readSettingsSettings, true);
                                    edit.apply();
                                    break;
                                // Factory BCM read failure
                                case 3:
                                    Log.d(TAG, "UPDATEDEDED:3");
                                    pause();
                                    mProgressDialog.hide();
                                    new SweetAlertDialog(FeaturesActivity.this, SweetAlertDialog.WARNING_TYPE)
                                            .setTitleText("Error")
                                            .setContentText("Procedure failed, please " +
                                                    "cycle vehicle ignition OFF, wait 2 " +
                                                    "minutes, cycle key ON, and try again ")
                                            .setConfirmText("Try Again")
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sDialog) {
                                                    sendRequest();
                                                    sDialog.dismiss();
                                                }
                                            })
                                            .show();
                                    break;
                                // Factory BCM write(dongle is returning bcm back to stock)
                                case 4:
                                    Log.d(TAG, "UPDATEDEDED:4");
                                    mProgressDialog.setMessage("Sending configuration to vehicle. Please wait...");
                                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    mProgressDialog.show();
                                    break;
                                // Factory BCM write complete(BCM successfully returned back to stock
                                case 5:
                                    Log.d(TAG, "UPDATEDEDED:5");
                                    pause();
                                    mProgressDialog.hide();
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    new SweetAlertDialog(FeaturesActivity.this, SweetAlertDialog.WARNING_TYPE)
                                            .setTitleText("Program Successful")
                                            .setContentText("To confirm changes, please turn the ignition OFF, wait 5 seconds, then turn the ignition on. Then tap refresh")
                                            .setConfirmText("Refresh")
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sDialog) {
                                                    sDialog.dismiss();
                                                    sendRequest();
                                                    recreate();
                                                }
                                            })
                                            .show();
                                    break;
                                // Reading current BCM config in progress
                                case 6:
                                    Log.d(TAG, "UPDATEDEDED:6");
                                    mProgressDialog.setMessage("Reading configuration from vehicle. Please wait...");
                                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    mProgressDialog.show();
                                    break;
                                // Reading current BCM config success/finished
                                case 7:
                                    Log.d(TAG, "UPDATEDEDED:7");
                                    pause();
                                    mProgressDialog.hide();
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    new SweetAlertDialog(FeaturesActivity.this, SweetAlertDialog.WARNING_TYPE)
                                            .setTitleText("Read Successfully")
                                            .setConfirmText("OK")
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sDialog) {
                                                    sDialog.dismiss();
                                                    sendRequest();
                                                    recreate();
                                                }
                                            })
                                            .show();
                                    break;
                                // Reading current BCM config Failure
                                case 8:
                                    Log.d(TAG, "UPDATEDEDED:8");
                                    pause();
                                    mProgressDialog.hide();
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    new SweetAlertDialog(FeaturesActivity.this, SweetAlertDialog.WARNING_TYPE)
                                            .setTitleText("Error")
                                            .setContentText("Procedure failed, please " +
                                                    "cycle vehicle ignition OFF, wait 2 " +
                                                    "minutes, cycle key ON, and try again")
                                            .setConfirmText("Try Again")
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sDialog) {
                                                    sDialog.dismiss();
                                                    sendRequest();
                                                }
                                            })
                                            .show();
                                    break;
                                // Writing current user BCM config settings
                                case 9:
                                    Log.d(TAG, "UPDATEDEDED:9");
                                    mProgressDialog.setMessage("Sending configuration to vehicle. Please wait...");
                                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    mProgressDialog.show();
                                    break;
                                // BCM has successfully been written with user settings
                                case 10:
                                    Log.d(TAG, "UPDATEDEDED:10");
                                    mProgressDialog.hide();
                                    pause();
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    new SweetAlertDialog(FeaturesActivity.this, SweetAlertDialog.WARNING_TYPE)
                                            .setTitleText("Program Successful")
                                            .setContentText("To confirm changes, please turn the ignition OFF, wait 5 seconds, then turn the ignition on. Then tap refresh")
                                            .setConfirmText("Refresh")
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sDialog) {
                                                    sDialog.dismiss();
                                                    sendRequest();
                                                    recreate();
                                                }
                                            })
                                            .show();
                                    break;
                                // BCM read/write not available or blocked
                                //(ie, vehicle is running or ignition switch is off)
                                case 11:
                                    Log.d(TAG, "UPDATEDEDED:11");
                                    pause();
                                    mProgressDialog.hide();
                                    new SweetAlertDialog(FeaturesActivity.this, SweetAlertDialog.WARNING_TYPE)
                                            .setTitleText("Error")
                                            .setContentText("Please verify ignition switch is ON, and engine is NOT running.")
                                            .setConfirmText("Try Again")
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sDialog) {
                                                    sDialog.dismiss();
                                                    sendRequest();
                                                }
                                            })
                                            .show();
                                    break;
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
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isConnected = false;
                        Log.d("Error.Response", error.toString());

                    }
                }
        );
        // add it to the RequestQueue
        queue.add(getRequest);
    }

    //Send to sGDP server to verify connection
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
                        isConnected = false;
                        Log.d("Error.Response", error.toString());

                        new SweetAlertDialog(Objects.requireNonNull(FeaturesActivity.this), SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("No Connection")
                                .setContentText("You are not connected to a GDP device. Retry by " +
                                        "tapping 'Retry' or check your wifi settings by tapping " +
                                        "'Connect'.")
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
                                })
                                .show();
                    }
                }
        );
        // add it to the RequestQueue
        queue.add(getRequest);
    }

}