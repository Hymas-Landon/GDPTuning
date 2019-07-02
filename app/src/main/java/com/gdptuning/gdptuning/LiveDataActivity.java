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

public class LiveDataActivity extends AppCompatActivity {

    //ESP32 aREST server address
    final String url = "http://192.168.7.1";
    boolean isConnected = false;
    final String boostVar = "boost";
    boolean isProcessing = false;
    String device = "GDP";
    int tuneMode = 0;
    Timer timer;
    RequestQueue queue;
    WifiManager wifi;
    TextView tvGear, tvTune;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    Button btn_home;
    TextView tab1, tab2;


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
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                Intent i = new Intent(LiveDataActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
        tab1 = findViewById(R.id.tab1);
        tab2 = findViewById(R.id.tab2);

        //tab id
        mTabLayout = findViewById(R.id.tabs);

        //add tabs
        mTabLayout.addTab(mTabLayout.newTab().setText("Digital Readings"));
        mTabLayout.addTab(mTabLayout.newTab().setText("Gauges"));
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //set viewPager
        mViewPager = findViewById(R.id.container);

        PagerDigital adapter = new PagerDigital(getSupportFragmentManager(), mTabLayout.getTabCount());
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

        tvTune = findViewById(R.id.tunenum);
        tvGear = findViewById(R.id.gear_position);
        //Working with wifi
        queue = Volley.newRequestQueue(this);
        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        sendRequest();
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (isConnected) {
                    if (!isProcessing) {
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
        Intent i = new Intent(LiveDataActivity.this, MainActivity.class);
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
                            tuneMode = variables.getInt("tune_mode");
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

                            SharedPreferences mSharedPreferences = getSharedPreferences("ThemeColor", MODE_PRIVATE);
                            SharedPreferences.Editor edit = mSharedPreferences.edit();
                            edit.putBoolean("logging", true);
                            edit.apply();


                        } catch (JSONException mE) {
                            mE.printStackTrace();
                        }
                        // display response
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isConnected = false;

                        Intent i = new Intent(LiveDataActivity.this, MainActivity.class);
                        startActivity(i);
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

                            String boost = variables.getString(boostVar);
                            if (boost.equals("65535")) {
                                timer.cancel();
                                new SweetAlertDialog(LiveDataActivity.this, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText("Logging Paused")
                                        .setContentText("Please close any other apps communicating through the OBD II Port, logging should resume.")
                                        .setConfirmText("Okay")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {
                                                sDialog.dismiss();
                                                timer = new Timer();
                                                timer.scheduleAtFixedRate(new TimerTask() {
                                                    @Override
                                                    public void run() {
                                                        if (isConnected) {
                                                            if (!isProcessing) {
                                                                updateRequest();
                                                            }
                                                        }
                                                    }
                                                }, 0, 500);//put here time 1000 milliseconds=1 second
                                            }
                                        })
                                        .show();
                            }

                        } catch (JSONException mE) {
                            mE.printStackTrace();
                        }
                        isProcessing = false;
                    }
                },
                new Response.ErrorListener()

                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isConnected = false;

                        Intent i = new Intent(LiveDataActivity.this, MainActivity.class);
                        startActivity(i);

                    }
                }
        );
        // add it to the RequestQueue
        queue.add(getRequest);
    }

}
