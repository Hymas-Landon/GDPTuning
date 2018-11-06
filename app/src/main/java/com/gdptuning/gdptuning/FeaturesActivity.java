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
    private boolean isConnected = false;
    private boolean isProcessing = false;
    String device = "GDP";
    RequestQueue queue;
    Button btn_home, btn_program, btn_read, btn_default;
    WifiManager wifi;
    TextView tvTune, tvGear;
    Timer timer;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private int tpmsNum, turnSigNum, tireSizeNum, fogLightsNum, dayRunNum, remoteStartNum, navNum,
            windowNum, strobeNum, workNum, auxNum1, auxNum2, auxNum3, highIdleNum, keyFobNum;
    private int tpms;


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
        btn_default = findViewById(R.id.default_settings);
        btn_default.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                SharedPreferences mSharedPreferences = getSharedPreferences("Default_Settings", MODE_PRIVATE);
                SharedPreferences.Editor edit = mSharedPreferences.edit();
                SharedPreferences readSharedPref = getSharedPreferences("Default_Settings", MODE_PRIVATE);
                SharedPreferences.Editor readEdit = readSharedPref.edit();
                readEdit.putBoolean("factory_settings", true);
                readEdit.apply();
                edit.apply();
                recreate();

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
                SharedPreferences mSharedPreferences = getSharedPreferences("ThemeColor", MODE_PRIVATE);
                SharedPreferences.Editor edit = mSharedPreferences.edit();
                edit.putBoolean("read_settings", true);
                edit.apply();
                recreate();
            }
        });

        btn_program.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                switchTpms(checkTpms());
                Toast mToast = Toast.makeText(getApplicationContext(), "TPMS = " + checkTpms(), Toast.LENGTH_LONG);
                mToast.show();
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

    public int checkTpms() {
        switch (getTPMS()) {
            case 25:
                tpms = 10;
                break;
            case 30:
                tpms = 11;
                break;
            case 35:
                tpms = 12;
                break;
            case 40:
                tpms = 13;
                break;
            case 45:
                tpms = 14;
                break;
            case 50:
                tpms = 15;
                break;
            case 55:
                tpms = 16;
                break;
            case 60:
                tpms = 17;
                break;
            case 65:
                tpms = 18;
                break;
            case 70:
                tpms = 19;
                break;
            case 75:
                tpms = 20;
                break;
            case 80:
                tpms = 21;
                break;
            case 0:
                tpms = 22;
                break;
        }
        return tpms;
    }

    public int checkTires() {
        int tireSize = 0;
        switch (getTireSize()) {
            case 31:
                tireSize = 23;
                break;
            case 32:
                tireSize = 24;
                break;
            case 33:
                tireSize = 25;
                break;
            case 34:
                tireSize = 26;
                break;
            case 35:
                tireSize = 27;
                break;
            case 36:
                tireSize = 28;
                break;
            case 37:
                tireSize = 29;
                break;
        }
        return tireSize;
    }

    public int checkLamp() {
        switch (getTPMS()) {
            case 25:
                tpms = 10;
                break;
            case 30:
                tpms = 11;
                break;
            case 35:
                tpms = 12;
                break;
            case 40:
                tpms = 13;
                break;
            case 45:
                tpms = 14;
                break;
            case 50:
                tpms = 15;
                break;
            case 55:
                tpms = 16;
                break;
            case 60:
                tpms = 17;
                break;
            case 65:
                tpms = 18;
                break;
            case 70:
                tpms = 19;
                break;
            case 75:
                tpms = 20;
                break;
            case 80:
                tpms = 21;
                break;
            case 0:
                tpms = 22;
                break;
        }
        return tpms;
    }

    public int checkFog() {
        switch (getTPMS()) {
            case 25:
                tpms = 10;
                break;
            case 30:
                tpms = 11;
                break;
            case 35:
                tpms = 12;
                break;
            case 40:
                tpms = 13;
                break;
            case 45:
                tpms = 14;
                break;
            case 50:
                tpms = 15;
                break;
            case 55:
                tpms = 16;
                break;
            case 60:
                tpms = 17;
                break;
            case 65:
                tpms = 18;
                break;
            case 70:
                tpms = 19;
                break;
            case 75:
                tpms = 20;
                break;
            case 80:
                tpms = 21;
                break;
            case 0:
                tpms = 22;
                break;
        }
        return tpms;
    }

    public int checkDayTime() {
        switch (getTPMS()) {
            case 25:
                tpms = 10;
                break;
            case 30:
                tpms = 11;
                break;
            case 35:
                tpms = 12;
                break;
            case 40:
                tpms = 13;
                break;
            case 45:
                tpms = 14;
                break;
            case 50:
                tpms = 15;
                break;
            case 55:
                tpms = 16;
                break;
            case 60:
                tpms = 17;
                break;
            case 65:
                tpms = 18;
                break;
            case 70:
                tpms = 19;
                break;
            case 75:
                tpms = 20;
                break;
            case 80:
                tpms = 21;
                break;
            case 0:
                tpms = 22;
                break;
        }
        return tpms;
    }

    public int checkRemote() {
        switch (getRemoteStart()) {
            case 25:
                tpms = 10;
                break;
            case 30:
                tpms = 11;
                break;
            case 35:
                tpms = 12;
                break;
            case 40:
                tpms = 13;
                break;
            case 45:
                tpms = 14;
                break;
            case 50:
                tpms = 15;
                break;
            case 55:
                tpms = 16;
                break;
            case 60:
                tpms = 17;
                break;
            case 65:
                tpms = 18;
                break;
            case 70:
                tpms = 19;
                break;
            case 75:
                tpms = 20;
                break;
            case 80:
                tpms = 21;
                break;
            case 0:
                tpms = 22;
                break;
        }
        return tpms;
    }

    public int checkNav() {
        int nav;
        if (isNavOverride()) {
            nav = 43;
        } else {
            nav = 42;
        }
        return nav;
    }

    public int checkRemoteWindow() {
        int result;
        if (isRemoteWindow()) {
            result = 39;
        } else {
            result = 38;
        }
        return result;
    }

   /* public int checkAux1(){
        int result;
        if(isAux1()){
            result = ;
        } else {
            result = 42;
        }
        return result;
    }

    public int checkAux2(){
        int result;
        if(isNavOverride()){
            result = 43;
        } else {
            result = 42;
        }
        return result;
    }

    public int checkAux3(){
        int result;
        if(isNavOverride()){
            result = 43;
        } else {
            result = 42;
        }
        return result;
    }*/

    public int checkWorkLight() {
        int result;
        if (isWorkLight()) {
            result = 47;
        } else {
            result = 48;
        }
        return result;
    }

   /* public int checkStrobe(){
        int result;
        if(isNavOverride()){
            result = 43;
        } else {
            result = 42;
        }
        return result;
    }*/

    public int checkHighIdle() {
        int result;
        if (isHighIdle()) {
            result = 45;
        } else {
            result = 46;
        }
        return result;
    }


 /*   public void setTpms(int tpms) {
        SharedPreferences mSharedPreferences = getSharedPreferences("ThemeColor", MODE_PRIVATE);
        SharedPreferences.Editor edit = mSharedPreferences.edit();

        switch (tpms) {
            case 25:
                changeTpms(10);

                break;
            case 30:
                changeTpms(11);
                break;
            case 35:
                changeTpms(12);
                break;
            case 40:
                changeTpms(13);
                break;
            case 45:
                changeTpms(14);
                break;
            case 50:
                changeTpms(15);
                break;
            case 55:
                changeTpms(16);
                break;
            case 60:
                changeTpms(17);
                break;
            case 65:
                changeTpms(18);
                break;
            case 70:
                changeTpms(19);
                break;
            case 75:
                changeTpms(20);
                break;
            case 80:
                changeTpms(21);
                break;
            case 0:
                changeTpms(22);
                break;
        }
        edit.apply();
    }

    public void setTireSize(int tireSize) {
        switch (tireSize) {
            case 31:
                changeTireSize(23);
                break;
            case 32:
                changeTireSize(24);
                break;
            case 33:
                changeTireSize(25);
                break;
            case 34:
                changeTireSize(26);
                break;
            case 35:
                changeTireSize(27);
                break;
            case 36:
                changeTireSize(28);
                break;
            case 37:
                changeTireSize(29);
                break;
        }
    }

    public void setFogLights(boolean foglights) {
        if (!foglights) {
            changeFogLights(30);
        } else {
            changeFogLights(31);
        }
    }

    public void setLEDTurnSignals(boolean turnSignals) {
        if (!turnSignals) {
            changeLEDTurnSignals(32);
        } else {
            changeLEDTurnSignals(33);
        }
    }

    public void setDaytimeLights(int daytimeLights) {
        if (daytimeLights == 1) {
            changeDaytimeLights(35);
        } else if (daytimeLights == 2) {
            changeDaytimeLights(36);
        } else if (daytimeLights == 3) {
            changeDaytimeLights(37);
        } else if (daytimeLights == 5) {
            changeDaytimeLights(34);
        }
    }

    public void setWindowRemote(boolean windowRemote) {
        if (!windowRemote) {
            changeWindowRemote(38);
        } else {
            changeWindowRemote(39);
        }
    }

    public void setRemoteStart(int remoteStart) {
        if (remoteStart == 2) {
            changeRemoteStart(40);
        } else if (remoteStart == 3) {
            changeRemoteStart(41);
        }
    }

    public void setNavOverride(boolean navOverride) {
        if (!navOverride) {
            changeNav(42);
        } else {
            changeNav(43);
        }
    }

    public void setHighIdle(boolean highIdle) {
        if (highIdle) {
            changeHighIdle(45);
        } else {
            changeHighIdle(46);
        }
    }

    public void setWorkLight(boolean worklight) {
        if (worklight) {
            changeWorkLight(47);
        } else {
            changeWorkLight(48);
        }
    }*/

/*    void setTpms(int tuneMode) {
        Log.d("Response", " " + tuneMode);
        switch (tuneMode) {
            case 1:

                break;
            case 2:

                break;
            case 3:

                break;
            case 4:

                break;
            case 5:

                break;
        }
    }*/

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

    public boolean isRead() {
        SharedPreferences mSharedPreferences = getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
        return mSharedPreferences.getBoolean("read_settings", false);
    }

    public int getTPMS() {
        SharedPreferences mSharedPreferences = getSharedPreferences("ThemeColor", MODE_PRIVATE);
        return mSharedPreferences.getInt("pressure_tpms", 80);
    }

    public int getTireSize() {
        SharedPreferences mSharedPreferences = getSharedPreferences("ThemeColor", MODE_PRIVATE);
        return mSharedPreferences.getInt("tire_size", 31);
    }

    public boolean isLampCurrent() {
        SharedPreferences mSharedPreferences = getSharedPreferences("ThemeColor", MODE_PRIVATE);
        return mSharedPreferences.getBoolean("lamp_current", false);
    }

    public boolean isFogLights() {
        SharedPreferences mSharedPreferences = getSharedPreferences("ThemeColor", MODE_PRIVATE);
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

                            if (!isRead()) {
                                SharedPreferences mSharedPreferences = getSharedPreferences("Default_Settings", MODE_PRIVATE);
                                SharedPreferences.Editor edit = mSharedPreferences.edit();
                                SharedPreferences readSharedPreferences = getSharedPreferences("ThemeColor", MODE_PRIVATE);
                                SharedPreferences.Editor readEdit = readSharedPreferences.edit();
                                if (getVehicleType() == VFORD1 || getVehicleType() == VFORD2) {
                                    int tpms = variables.getInt("tpms");
                                    int daytimeRunningLights = variables.getInt("drl");
                                    int lampOutage = variables.getInt("lamp_out");
                                    int fogLights = variables.getInt("fog_high");
                                    int tireSize = variables.getInt("tire_size");
                                    int remoteWindow = variables.getInt("rke_windows");
                                    int remoteStartDuration = variables.getInt("rvs");

                                    edit.putInt("pressure_tpms", tpms);
                                    if (lampOutage == 0) {
                                        edit.putBoolean("lamp_current", false);
                                    } else if (lampOutage == 1) {
                                        edit.putBoolean("lamp_current", true);
                                    }
                                    edit.putInt("tire_size", tireSize);
                                    if (fogLights == 0) {
                                        edit.putBoolean("fog_lights", false);
                                    } else if (fogLights == 1) {
                                        edit.putBoolean("fog_lights", true);
                                    }
                                    edit.putInt("daytime_lights", daytimeRunningLights);
                                    edit.putInt("remote_start", remoteStartDuration);
                                    edit.putInt("remote_window", remoteWindow);
                                    if (remoteWindow == 0) {
                                        edit.putBoolean("remote_window", false);
                                    } else if (remoteWindow == 1) {
                                        edit.putBoolean("remote_window", true);
                                    }
                                } else if (getVehicleType() == VGM2) {
                                    int tpms = variables.getInt("tpms");
                                    edit.putInt("pressure_tpms", tpms);
                                } else if (getVehicleType() == VRAM) {
                                    int tpms = variables.getInt("tpms");
                                    int fogLights = variables.getInt("fog_high");
                                    int tireSize = variables.getInt("tire_size");
                                    edit.putInt("tire_size", tireSize);
                                    if (fogLights == 0) {
                                        edit.putBoolean("fog_lights", false);
                                    } else if (fogLights == 1) {
                                        edit.putBoolean("fog_lights", true);
                                    }
                                    edit.putInt("pressure_tpms", tpms);
                                }
                                readEdit.putBoolean("factory_settings", true);
                                readEdit.apply();
                                edit.apply();
                                sendRequest();
                            } else if (isRead()) {
                                SharedPreferences mSharedPreferences = getSharedPreferences("ThemeColor", MODE_PRIVATE);
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
                                    if (lampOutage == 0) {
                                        edit.putBoolean("lamp_current", false);
                                    } else if (lampOutage == 1) {
                                        edit.putBoolean("lamp_current", true);
                                    }
                                    edit.putInt("tire_size", tireSize);
                                    if (fogLights == 0) {
                                        edit.putBoolean("fog_lights", false);
                                    } else if (fogLights == 1) {
                                        edit.putBoolean("fog_lights", true);
                                    }
                                    edit.putInt("daytime_lights", daytimeRunningLights);
                                    edit.putInt("remote_start", remoteStartDuration);
                                    edit.putInt("remote_window", remoteWindow);
                                    if (remoteWindow == 0) {
                                        edit.putBoolean("remote_window", false);
                                    } else if (remoteWindow == 1) {
                                        edit.putBoolean("remote_window", true);
                                    }
                                    edit.apply();
                                } else if (getVehicleType() == VGM2) {
                                    int tpms = variables.getInt("tpms");
                                    edit.putInt("pressure_tpms", tpms);
                                    edit.apply();
                                } else if (getVehicleType() == VRAM) {
                                    int tpms = variables.getInt("tpms");
                                    int fogLights = variables.getInt("fog_high");
                                    int tireSize = variables.getInt("tire_size");
                                    edit.putInt("tire_size", tireSize);
                                    if (fogLights == 0) {
                                        edit.putBoolean("fog_lights", false);
                                    } else if (fogLights == 1) {
                                        edit.putBoolean("fog_lights", true);
                                    }
                                    edit.putInt("pressure_tpms", tpms);
                                    edit.apply();
                                }
                                edit.putBoolean("factory_settings", false);
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
                                .setContentText("You are not connected to a GDP device. Retry by " +
                                        "tapping 'Retry' or check your wifi settings by tapping " +
                                        "'Connect'.")
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
    void switchTpms(int requestTpms) {
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url + "/diag_functions?params=" + requestTpms, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        isConnected = true;
                        try {
                            tpmsNum = response.getInt("return_value");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        SharedPreferences readSharedPreferences = getSharedPreferences("ThemeColor", MODE_PRIVATE);
                        SharedPreferences.Editor edit = readSharedPreferences.edit();
                        switch (tpmsNum) {
                            case 10:
                                edit.putInt("pressure_tpms", 25);
                                break;
                            case 11:
                                edit.putInt("pressure_tpms", 30);
                                break;
                            case 12:
                                edit.putInt("pressure_tpms", 35);
                                break;
                            case 13:
                                edit.putInt("pressure_tpms", 40);
                                break;
                            case 14:
                                edit.putInt("pressure_tpms", 45);
                                break;
                            case 15:
                                edit.putInt("pressure_tpms", 50);
                                break;
                            case 16:
                                edit.putInt("pressure_tpms", 55);
                                break;
                            case 17:
                                edit.putInt("pressure_tpms", 60);
                                break;
                            case 18:
                                edit.putInt("pressure_tpms", 65);
                                break;
                            case 19:
                                edit.putInt("pressure_tpms", 70);
                                break;
                            case 20:
                                edit.putInt("pressure_tpms", 75);
                                break;
                            case 21:
                                edit.putInt("pressure_tpms", 80);
                                break;
                            case 22:
                                edit.putInt("pressure_tpms", 0);
                                edit.apply();
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

                        new SweetAlertDialog(FeaturesActivity.this, SweetAlertDialog.WARNING_TYPE)
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