package com.gdptuning.gdptuning;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;

import java.util.Timer;


public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private static int GAUGEPLAIN = 4;
    private static int GAUGEDIGITAL = 5;
    private static int GAUGEPROGRESS = 6;
    private static int colorIndex = 0;
    private static int gaugeIndex = 0;
    private static int enableMetric = 0;
    private static int disableMetric = 1;
    //ESP32 aREST server address
    final String url = "http://192.168.7.1";
    boolean isConnected = false;
    boolean isProcessing = false;
    String device = "GDP";
    RequestQueue queue;
    Button btn_home;
    WifiManager wifi;
    TextView tvTune, tvGear, select1, select2, select3;
    Timer timer;
    ImageView arrowRight1, arrowRight2, arrowRight3, arrowLeft1, arrowLeft2, arrowLeft3;


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
        setContentView(R.layout.activity_settings);

        //Id's
        select1 = findViewById(R.id.selector1);
        select2 = findViewById(R.id.selector2);
        select3 = findViewById(R.id.selector3);

        //Selector 1
        final String[] metric = new String[2];
        metric[0] = "Enable";
        metric[1] = "Disable";
        if (getSettings1() == enableMetric) {
            select1.setText(metric[0]);
        } else if (getSettings1() == disableMetric) {
            select1.setText(metric[1]);
        }

        //Selector 2
        final String[] gauge = new String[3];
        gauge[0] = "Digital Gauge";
        gauge[1] = "Needle Gauge";
        gauge[2] = "Progress Gauge";
        if (getGaugeListener() == GAUGEDIGITAL) {
            select2.setText(gauge[0]);
            gaugeIndex = 0;
        } else if (getGaugeListener() == GAUGEPLAIN) {
            select2.setText(gauge[1]);
            gaugeIndex = 1;
        } else if (getGaugeListener() == GAUGEPROGRESS) {
            select2.setText(gauge[2]);
            gaugeIndex = 2;
        }

        //Selector 3
        final String[] color = new String[4];
        color[0] = "Orange(default)";
        color[1] = "Green";
        color[2] = "Blue";
        color[3] = "Red";
        if (getColorTheme() == Utils.THEME_DEFAULT) {
            select3.setText(color[0]);
            colorIndex = 0;
        } else if (getColorTheme() == Utils.THEME_GREEN) {
            select3.setText(color[1]);
            colorIndex = 1;
        } else if (getColorTheme() == Utils.THEME_BLUE) {
            select3.setText(color[2]);
            colorIndex = 2;
        } else if (getColorTheme() == Utils.THEME_RED) {
            select3.setText(color[3]);
            colorIndex = 3;
        }

        arrowLeft1 = findViewById(R.id.arrowLeft);
        arrowLeft1.setOnClickListener(new View.OnClickListener() {
            SharedPreferences mSharedPreferences = getSharedPreferences("ThemeColor", MODE_PRIVATE);
            SharedPreferences.Editor edit = mSharedPreferences.edit();

            @Override
            public void onClick(View mView) {
                select1.setText(metric[0]);
                edit.putInt("settings1", enableMetric);
                edit.apply();
            }
        });
        arrowRight1 = findViewById(R.id.arrowRight);
        arrowRight1.setOnClickListener(new View.OnClickListener() {
            SharedPreferences mSharedPreferences = getSharedPreferences("ThemeColor", MODE_PRIVATE);
            SharedPreferences.Editor edit = mSharedPreferences.edit();

            @Override
            public void onClick(View mView) {
                select1.setText(metric[1]);
                edit.putInt("settings1", disableMetric);
                edit.apply();
            }
        });
        arrowLeft2 = findViewById(R.id.arrowLeft2);
        arrowLeft2.setOnClickListener(new View.OnClickListener() {
            SharedPreferences mSharedPreferences = getSharedPreferences("ThemeColor", MODE_PRIVATE);
            SharedPreferences.Editor edit = mSharedPreferences.edit();

            @Override
            public void onClick(View mView) {
                if (gaugeIndex > 0 && gaugeIndex <= 2) {
                    gaugeIndex = gaugeIndex - 1;
                    select2.setText(gauge[gaugeIndex]);
                    if (gaugeIndex == 0) {
                        edit.putInt("gauge", GAUGEDIGITAL);
//                    } else if (gaugeIndex == 1) {
//                        edit.putInt("gauge", GAUGEPLAIN);
//                    } else if (gaugeIndex == 2) {
//                        edit.putInt("gauge", GAUGEPROGRESS);
//                    }
                    }
                } else {
                    select2.setText(gauge[0]);
                }
                edit.apply();
            }
        });

        arrowRight2 = findViewById(R.id.arrowRight2);
        arrowRight2.setOnClickListener(new View.OnClickListener() {
            SharedPreferences mSharedPreferences = getSharedPreferences("ThemeColor", MODE_PRIVATE);
            SharedPreferences.Editor edit = mSharedPreferences.edit();

            @Override
            public void onClick(View mView) {
                if (gaugeIndex >= 0 && gaugeIndex < 2) {
                    gaugeIndex = gaugeIndex + 1;
                    select2.setText(gauge[gaugeIndex]);
                    if (gaugeIndex == 0) {
                        edit.putInt("gauge", GAUGEDIGITAL);
                    } else if (gaugeIndex == 1) {
                        edit.putInt("gauge", GAUGEPLAIN);
                    } else if (gaugeIndex == 2) {
                        edit.putInt("gauge", GAUGEPROGRESS);
                    }
                } else {
                    select2.setText(gauge[2]);
                }
                edit.apply();
            }
        });
        arrowLeft3 = findViewById(R.id.arrowLeft3);
        arrowLeft3.setOnClickListener(new View.OnClickListener() {
            SharedPreferences mSharedPreferences = getSharedPreferences("ThemeColor", MODE_PRIVATE);
            SharedPreferences.Editor edit = mSharedPreferences.edit();

            @Override
            public void onClick(View mView) {
                if (colorIndex > 0 && colorIndex <= 3) {
                    colorIndex = colorIndex - 1;
                    select3.setText(color[colorIndex]);
                    if (colorIndex == 0) {
                        edit.putInt("theme", Utils.THEME_DEFAULT);
                    } else if (colorIndex == 1) {
                        edit.putInt("theme", Utils.THEME_GREEN);
                    } else if (colorIndex == 2) {
                        edit.putInt("theme", Utils.THEME_BLUE);
                    } else if (colorIndex == 3) {
                        edit.putInt("theme", Utils.THEME_RED);
                    }
                } else {
                    select3.setText(color[0]);
                }
                edit.apply();
            }
        });
        arrowRight3 = findViewById(R.id.arrowRight3);
        arrowRight3.setOnClickListener(new View.OnClickListener() {
            SharedPreferences mSharedPreferences = getSharedPreferences("ThemeColor", MODE_PRIVATE);
            SharedPreferences.Editor edit = mSharedPreferences.edit();

            @Override
            public void onClick(View mView) {
                if (colorIndex >= 0 && colorIndex < 3) {
                    colorIndex = colorIndex + 1;
                    select3.setText(color[colorIndex]);
                    if (colorIndex == 0) {
                        edit.putInt("theme", Utils.THEME_DEFAULT);
                    } else if (colorIndex == 1) {
                        edit.putInt("theme", Utils.THEME_GREEN);
                    } else if (colorIndex == 2) {
                        edit.putInt("theme", Utils.THEME_BLUE);
                    } else if (colorIndex == 3) {
                        edit.putInt("theme", Utils.THEME_RED);
                    }
                } else {
                    select3.setText(color[3]);
                }
                edit.apply();
            }
        });

        //set home widget
        btn_home = findViewById(R.id.btn_home);
        tvGear = findViewById(R.id.gear_position);
        tvTune = findViewById(R.id.tunenum);

        //OnClickListener
        btn_home.setOnClickListener(this);

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

    private int getSettings1() {
        SharedPreferences mSharedPreferences = getSharedPreferences("ThemeColor", MODE_PRIVATE);
        return mSharedPreferences.getInt("settings1", enableMetric);
    }

    private int getGaugeListener() {
        SharedPreferences mySharedPreferences = getSharedPreferences("ThemeColor", MODE_PRIVATE);
        return mySharedPreferences.getInt("gauge", GAUGEDIGITAL);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(SettingsActivity.this, MainActivity.class);
        startActivity(i);
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        switch (id) {
            case R.id.btn_home:
                startActivity(new Intent(SettingsActivity.this, MainActivity.class));
                break;
        }
    }
}