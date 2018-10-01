package com.gdptuning.gdptuning;

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
    boolean isConnected = false;
    boolean isProcessing = false;
    String device = "GDP";
    RequestQueue queue;
    Button btn_home, btn_program, btn_read;
    WifiManager wifi;
    TextView tvTune, tvGear;
    Timer timer;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    int tpmsNum, turnSigNum, tireSizeNum, fogLightsNum, dayRunNum, remoteStartNum, navNum, windowNum, strobeNum, workNum, auxNum1, auxNum2, auxNum3, highIdleNum, keyFobNum;

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

        btn_home = findViewById(R.id.btn_home);
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                Intent i = new Intent(FeaturesActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

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


        //Set textView
        tvGear = findViewById(R.id.gear_position);
        tvTune = findViewById(R.id.tunenum);
        btn_program = findViewById(R.id.program_features);
        btn_program.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                Toast toast = Toast.makeText(FeaturesActivity.this, "Program Button Clicked", Toast.LENGTH_LONG);
                toast.show();
            }
        });
        btn_read = findViewById(R.id.read_settings);
        btn_read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                SharedPreferences mSharedPreferences = getSharedPreferences("ThemeColor", MODE_PRIVATE);
                SharedPreferences.Editor edit = mSharedPreferences.edit();
                edit.putBoolean("read_settings", true);
                edit.apply();
                recreate();
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
                        updateRequest();
                    }
                }

            }
        }, 0, 500);//put here time 1000 milliseconds=1 second
    }

    public void setTpms(int tpms) {
        tpms = getTPMS();
        switch (tpms) {
            case 25:
                tpms = 10;
                switchTpms(tpms);
                break;
            case 30:
                tpms = 11;
                switchTpms(tpms);
                break;
            case 35:
                tpms = 12;
                switchTpms(tpms);
                break;
            case 40:
                tpms = 13;
                switchTpms(tpms);
                break;
            case 45:
                tpms = 14;
                switchTpms(tpms);
                break;
            case 50:
                tpms = 15;
                switchTpms(tpms);
                break;
            case 55:
                tpms = 16;
                switchTpms(tpms);
                break;
            case 60:
                tpms = 17;
                switchTpms(tpms);
                break;
            case 65:
                tpms = 18;
                switchTpms(tpms);
                break;
            case 70:
                tpms = 19;
                switchTpms(tpms);
                break;
            case 75:
                tpms = 20;
                switchTpms(tpms);
                break;
            case 80:
                tpms = 21;
                switchTpms(tpms);
                break;
            case 0:
                tpms = 22;
                switchTpms(tpms);
                break;
        }
    }

    public int setTireSize(int tireSize) {
        if (getTireSize() == 31) {
            tireSize = 23;
        } else if (getTireSize() == 32) {
            tireSize = 24;
        } else if (getTireSize() == 33) {
            tireSize = 25;
        } else if (getTireSize() == 34) {
            tireSize = 26;
        } else if (getTireSize() == 35) {
            tireSize = 27;
        } else if (getTireSize() == 36) {
            tireSize = 28;
        } else if (getTireSize() == 37) {
            tireSize = 29;
        }
        return tireSize;
    }

    public int setDaytimeLights(int daytimeLights) {
        if (getDaytimeLights() == 1) {
            daytimeLights = 35;
        } else if (getDaytimeLights() == 2) {
            daytimeLights = 36;
        } else if (getDaytimeLights() == 3) {
            daytimeLights = 37;
        } else if (getDaytimeLights() == 5) {
            daytimeLights = 34;
        }
        return daytimeLights;
    }

    public int setFogLights(int foglights) {
        if (!isFogLights()) {
            foglights = 30;
        } else if (isFogLights()) {
            foglights = 31;
        }
        return foglights;
    }

    public int setLEDTurnSignals(int turnSignals) {
        if (!isLampCurrent()) {
            turnSignals = 32;
        } else if (isLampCurrent()) {
            turnSignals = 33;
        }
        return turnSignals;
    }

    public int setWindowRemote(int windowRemote) {
        if (!isRemoteWindow()) {
            windowRemote = 38;
        } else if (isRemoteWindow()) {
            windowRemote = 39;
        }
        return windowRemote;
    }

    public int setRemoteStart(int remoteStart) {
        if (getRemoteStart() == 2) {
            remoteStart = 40;
        } else if (getRemoteStart() == 3) {
            remoteStart = 41;
        }
        return remoteStart;
    }

    public int setNavOverride(int navOverride) {
        if (!isNavOverride()) {
            navOverride = 42;
        } else if (isNavOverride()) {
            navOverride = 43;
        }
        return navOverride;
    }

    public int setWorkLight(int worklight) {
        if (isWorkLight()) {
            worklight = 47;
        } else if (!isWorkLight()) {
            worklight = 48;
        }
        return worklight;
    }

    public int setHighIdle(int highIdle) {
        if (isHighIdle()) {
            highIdle = 45;
        } else if (!isHighIdle()) {
            highIdle = 46;
        }
        return highIdle;
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
        SharedPreferences mSharedPreferences = getSharedPreferences("ThemeColor", MODE_PRIVATE);
        return mSharedPreferences.getInt("theme", Utils.THEME_DEFAULT);
    }

    private int getVehicleType() {
        SharedPreferences mSharedPreferences = getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
        return mSharedPreferences.getInt("vehicle", VFORD1);
    }

    public int getTireSize() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getSharedPreferences("ThemeColor", Context.MODE_PRIVATE));
        return mSharedPreferences.getInt("tire_size", 31);
    }

    public int getTPMS() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getSharedPreferences("ThemeColor", Context.MODE_PRIVATE));
        return mSharedPreferences.getInt("pressure_tpms", 80);
    }

    public boolean isLampCurrent() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getSharedPreferences("ThemeColor", Context.MODE_PRIVATE));
        return mSharedPreferences.getBoolean("lamp_current", false);
    }

    public boolean isFogLights() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getSharedPreferences("ThemeColor", Context.MODE_PRIVATE));
        return mSharedPreferences.getBoolean("fog_lights", false);
    }

    public int getDaytimeLights() {
        SharedPreferences mSharedPreferences = getSharedPreferences("ThemeColor", MODE_PRIVATE);
        return mSharedPreferences.getInt("daytime_lights", 1);
    }

    public int getRemoteStart() {
        SharedPreferences mSharedPreferences = getSharedPreferences("ThemeColor", MODE_PRIVATE);
        return mSharedPreferences.getInt("remote_start", 3);
    }

    public boolean isNavOverride() {
        SharedPreferences mSharedPreferences = getSharedPreferences("ThemeColor", MODE_PRIVATE);
        return mSharedPreferences.getBoolean("nav_override", false);
    }

    public boolean isRemoteWindow() {
        SharedPreferences mSharedPreferences = getSharedPreferences("ThemeColor", MODE_PRIVATE);
        return mSharedPreferences.getBoolean("remote_window", false);
    }

    public boolean isAux1() {
        SharedPreferences mSharedPreferences = getSharedPreferences("ThemeColor", MODE_PRIVATE);
        return mSharedPreferences.getBoolean("aux1", false);
    }

    public boolean isAux2() {
        SharedPreferences mSharedPreferences = getSharedPreferences("ThemeColor", MODE_PRIVATE);
        return mSharedPreferences.getBoolean("aux2", false);
    }

    public boolean isAux3() {
        SharedPreferences mSharedPreferences = getSharedPreferences("ThemeColor", MODE_PRIVATE);
        return mSharedPreferences.getBoolean("aux3", false);
    }

    public boolean isWorkLight() {
        SharedPreferences mSharedPreferences = getSharedPreferences("ThemeColor", MODE_PRIVATE);
        return mSharedPreferences.getBoolean("work_light", false);
    }

    public boolean isStrobeLight() {
        SharedPreferences mSharedPreferences = getSharedPreferences("ThemeColor", MODE_PRIVATE);
        return mSharedPreferences.getBoolean("strobe_light", false);
    }


    public boolean isHighIdle() {
        SharedPreferences mSharedPreferences = getSharedPreferences("ThemeColor", MODE_PRIVATE);
        return mSharedPreferences.getBoolean("high_idle", false);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(FeaturesActivity.this, MainActivity.class);
        startActivity(i);
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

                            SharedPreferences mSharedPreferences = getSharedPreferences("Default_Settings", MODE_PRIVATE);
                            SharedPreferences.Editor edit = mSharedPreferences.edit();
                            if (getVehicleType() == VFORD1 || getVehicleType() == VFORD2) {
                                int tpms = variables.getInt("tpms");
                                int daytimeRunningLights = variables.getInt("drl");
                                int lampOutage = variables.getInt("lamp_out");
                                int fogLights = variables.getInt("fog_high");
                                int tireSize = variables.getInt("tire_size");
                                int remoteWindow = variables.getInt("rke_windows");
                                int remoteStartDuration = variables.getInt("rvs");

                                edit.putInt("pressure_tpms", tpms);
                                edit.putInt("lamp_current", lampOutage);
                                edit.putInt("tire_size", tireSize);
                                edit.putInt("fog_lights", fogLights);
                                edit.putInt("daytime_lights", daytimeRunningLights);
                                edit.putInt("remote_start", remoteStartDuration);
                                edit.putInt("remote_window", remoteWindow);
                                edit.apply();
                            } else if (getVehicleType() == VGM2) {
                                int tpms = variables.getInt("tpms");
                                edit.putInt("pressure_tpms", tpms);
                            } else if (getVehicleType() == VRAM) {
                                int tpms = variables.getInt("tpms");
                                int fogLights = variables.getInt("fog_high");
                                int tireSize = variables.getInt("tire_size");
                                edit.putInt("tire_size", tireSize);
                                edit.putInt("fog_lights", fogLights);
                                edit.putInt("pressure_tpms", tpms);
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

                        new SweetAlertDialog(FeaturesActivity.this, SweetAlertDialog.WARNING_TYPE)
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
//                            sendRequest();
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

    //Send to sGDP server to verify connection
    void switchTpms(int tpms) {
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url + "/diag_functions?params=" + tpms, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        isConnected = true;
                        try {
                            tpmsNum = response.getInt("return_value");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // display response
                        Log.d("Response", response.toString());
                        setTpms(tpmsNum);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isConnected = false;
                        Log.d("Error.Response", error.toString());

                        new SweetAlertDialog(FeaturesActivity.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("No Connection")
                                .setContentText("Your are not connected to GDP device")
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
