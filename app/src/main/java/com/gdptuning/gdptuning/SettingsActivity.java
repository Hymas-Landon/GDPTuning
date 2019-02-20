package com.gdptuning.gdptuning;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
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


public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private static int colorIndex = 0;
    //ESP32 aREST server address
    final String url = "http://192.168.7.1";
    boolean isConnected = false;
    boolean isProcessing = false;
    int tuneMode = 0;
    String device = "GDP";
    RequestQueue queue;
    Button btn_home;
    WifiManager wifi;
    TextView tvTune, tvGear, select1, select3, proVersion, appVersion;
    Timer timer;
    ImageView arrowRight1, arrowRight3, arrowLeft1, arrowLeft3;


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
        queue = Volley.newRequestQueue(this);
        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        proVersion = findViewById(R.id.pro_version_num);
        appVersion = findViewById(R.id.app_version_num);

        //Id's
        select1 = findViewById(R.id.selector1);
        select3 = findViewById(R.id.selector3);

        //Selector 1
        final String[] metric = new String[2];
        metric[0] = "Metric";
        metric[1] = "Standard";
        if (isMetric()) {
            select1.setText(metric[0]);
        } else if (!isMetric()) {
            select1.setText(metric[1]);
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
                edit.putBoolean("metric", true);
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
                edit.putBoolean("metric", false);
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

    private boolean isMetric() {
        SharedPreferences mSharedPreferences = getSharedPreferences("ThemeColor", MODE_PRIVATE);
        return mSharedPreferences.getBoolean("metric", false);
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
                            int gear = variables.getInt("gear");
                            String deviceName = response.getString("name");
                            deviceName += " ";
                            deviceName += response.getString("id");
                            device = deviceName;
                            char pos = (char) gear;

                            if (tuneMode == 255) {
                                tvTune.setText("TUNE: E");
                            } else {
                                tvTune.setText("TUNE: " + tuneMode);
                            }
                            tvGear.setText("GEAR: " + pos);

                            proVersion.setText(deviceName);
                            String version = "";
                            try {
                                PackageInfo mPackageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                                version = mPackageInfo.versionName;
                            } catch (PackageManager.NameNotFoundException mE) {
                                mE.printStackTrace();
                            }
                            appVersion.setText(version);
                            Log.d("Response", response.toString());


                        } catch (JSONException mE) {
                            mE.printStackTrace();
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

                        new SweetAlertDialog(SettingsActivity.this, SweetAlertDialog.WARNING_TYPE)
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
                            deviceName += " ";
                            deviceName += response.getString("id");
                            device = deviceName;
                            char pos = (char) gear;

                            if (tuneMode == 255) {
                                tvTune.setText("TUNE: E");
                            } else {
                                tvTune.setText("TUNE: " + tuneMode);
                            }
                            tvGear.setText("GEAR: " + pos);

                            proVersion.setText(deviceName);
                            Log.d("Response", response.toString());

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
                        Log.d("Error.Response", error.toString());

                        new SweetAlertDialog(SettingsActivity.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("No Connection")
                                .setContentText("Your are not connected to a GDP device. Retry by " +
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
}