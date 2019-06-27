package com.gdptuning.gdptuning;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String sharedPrefFile = "com.gpdtuning.sharedPref";
    private static int VFORD1 = 7;
    private static int VFORD2 = 8;
    private static int VGM1 = 9;
    private static int VGM2 = 10;
    private static int VRAM = 11;
    private static int start = 0;
    //ESP32 aREST server address
    final String url = "http://192.168.7.1";
    boolean isConnected = false;
    boolean isProcessing = false;
    String device = "GDP";
    RequestQueue queue;
    final String boostVar = "boost";
    int item_select = 0;
    Timer timer;
    WifiManager wifi;
    TextView tvTune, tvGear;
    Button btn_tune, btn_live, btn_diagnostics, btn_configuration;
    SharedPreferences mPreferences;

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
        setContentView(R.layout.activity_main);

        btn_live = findViewById(R.id.btn_live_data);

        mPreferences = getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE);

        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        toolbar.setTitle("");
        toolbar.setSubtitle("");

        //Working with wifi
        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        //Set widgets
        btn_tune = findViewById(R.id.btn_tune);
        btn_configuration = findViewById(R.id.btn_config);
        btn_diagnostics = findViewById(R.id.btn_diag);
        tvGear = findViewById(R.id.gear_position);
        tvTune = findViewById(R.id.tunenum);

        //Set OnClick Listener
        btn_tune.setOnClickListener(this);
        btn_configuration.setOnClickListener(this);
        btn_diagnostics.setOnClickListener(this);
        btn_live.setOnClickListener(this);

        queue = VolleySingleton.getInstance(this).getRequestQueue();
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
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences mSharedPreferences = getSharedPreferences("ThemeColor", MODE_PRIVATE);
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        switch (item.getItemId()) {
            case R.id.settings_drawer:
                Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(i);
                return true;
            case R.id.ford_11_16_radio:
                item_select = 1;
                item.setChecked(true);
                edit.putInt("vehicle", VFORD1);
                edit.putBoolean("read_settings", false);
                edit.apply();
                recreate();
                return true;
            case R.id.ford_17up_radio:
                item_select = 2;
                item.setChecked(true);
                edit.putInt("vehicle", VFORD2);
                edit.putBoolean("read_settings", false);
                edit.apply();
                recreate();
                return true;
            case R.id.ram_13up_radio:
                item_select = 3;
                item.setChecked(true);
                edit.putInt("vehicle", VRAM);
                edit.putBoolean("read_settings", false);
                edit.apply();
                recreate();
                return true;
            case R.id.gm_7_14_radio:
                item_select = 4;
                item.setChecked(true);
                edit.putInt("vehicle", VGM1);
                edit.putBoolean("read_settings", false);
                edit.apply();
                recreate();
                return true;
            case R.id.gm_15up_radio:
                item_select = 5;
                item.setChecked(true);
                edit.putInt("vehicle", VGM2);
                edit.putBoolean("read_settings", false);
                edit.apply();
                recreate();
                return true;
            case R.id.automatic:
                item_select = 6;
                item.setChecked(true);
                edit.putBoolean("auto", true);
                edit.putBoolean("read_settings", false);
                edit.apply();
                recreate();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        SharedPreferences mSharedPreferences = getSharedPreferences("ThemeColor", MODE_PRIVATE);
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        MenuInflater mMenuInflater = getMenuInflater();
        mMenuInflater.inflate(R.menu.activity_main_drawer, menu);
        MenuItem ford1 = menu.findItem(R.id.ford_11_16_radio);
        MenuItem ford2 = menu.findItem(R.id.ford_17up_radio);
        MenuItem ram = menu.findItem(R.id.ram_13up_radio);
        MenuItem gm1 = menu.findItem(R.id.gm_7_14_radio);
        MenuItem gm2 = menu.findItem(R.id.gm_15up_radio);
        MenuItem auto = menu.findItem(R.id.automatic);

        if (item_select == 1) {
            ford1.setChecked(true);
            edit.putInt("vehicle", VFORD1);
            edit.putBoolean("auto", false);
            edit.apply();
        } else if (item_select == 2) {
            ford2.setChecked(true);
            edit.putInt("vehicle", VFORD2);
            edit.putBoolean("auto", false);
            edit.apply();
        } else if (item_select == 3) {
            ram.setChecked(true);
            edit.putInt("vehicle", VRAM);
            edit.putBoolean("auto", false);
            edit.apply();
        } else if (item_select == 4) {
            gm1.setChecked(true);
            edit.putInt("vehicle", VGM1);
            edit.putBoolean("auto", false);
            edit.apply();
        } else if (item_select == 5) {
            gm2.setChecked(true);
            edit.putInt("vehicle", VGM2);
            edit.putBoolean("auto", false);
            edit.apply();
        } else if (item_select == 6) {
            auto.setChecked(true);
            edit.putBoolean("auto", true);
            edit.apply();
        }
        if (getVehicleType() == VFORD1) {
            ford1.setChecked(true);
        } else if (getVehicleType() == VFORD2) {
            ford2.setChecked(true);
        } else if (getVehicleType() == VRAM) {
            ram.setChecked(true);
        } else if (getVehicleType() == VGM1) {
            gm1.setChecked(true);
        } else if (getVehicleType() == VGM2) {
            gm2.setChecked(true);
        }
        return true;
    }

    private int getColorTheme() {
        SharedPreferences mSharedPreferences = getSharedPreferences("ThemeColor", MODE_PRIVATE);
        return mSharedPreferences.getInt("theme", Utils.THEME_DEFAULT);
    }

    private int getVehicleType() {
        SharedPreferences mSharedPreferences = getSharedPreferences("ThemeColor", MODE_PRIVATE);
        return mSharedPreferences.getInt("vehicle", VFORD1);
    }

    private boolean isAutomatic() {
        SharedPreferences mSharedPreferences = getSharedPreferences("ThemeColor", MODE_PRIVATE);
        return mSharedPreferences.getBoolean("auto", true);
    }

    @Override
    public void onClick(final View v) {
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(final JSONObject response) {
                        isConnected = true;
                        try {
                            JSONObject variables = response.getJSONObject("variables");
                            int pltfrm = variables.getInt("pltfrm");
                            int bcm_stat = variables.getInt("bcm_stat");
                            int id = v.getId();
                            switch (id) {
                                case R.id.btn_live_data:
                                    if (pltfrm == 0) {
                                        new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                                                .setTitleText("VIN not Detected")
                                                .setContentText("Please turn the ignition off, unplug the pro dongle, plug it back in, and turn the ignition back on.")
                                                .setConfirmText("Okay")
                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                    @Override
                                                    public void onClick(SweetAlertDialog sDialog) {
                                                        sDialog.dismiss();
                                                    }
                                                })
                                                .show();
                                    } else {
                                        startActivity(new Intent(MainActivity.this, LiveDataActivity.class));
                                    }
                                    break;
                                case R.id.btn_tune:
                                    if (pltfrm == 0) {
                                        new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                                                .setTitleText("VIN not Detected")
                                                .setContentText("Please turn the ignition off, unplug the pro dongle, plug it back in, and turn the ignition back on.")
                                                .setConfirmText("Okay")
                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                    @Override
                                                    public void onClick(SweetAlertDialog sDialog) {
                                                        sDialog.dismiss();
                                                    }
                                                })
                                                .show();
                                    } else {
                                        startActivity(new Intent(MainActivity.this, TuneActivity.class));
                                    }
                                    break;
                                case R.id.btn_config:
                                    if (pltfrm == 0) {
                                        new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                                                .setTitleText("VIN not Detected")
                                                .setContentText("Please turn the ignition off, unplug the pro dongle, plug it back in, and turn the ignition back on.")
                                                .setConfirmText("Okay")
                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                    @Override
                                                    public void onClick(SweetAlertDialog sDialog) {
                                                        sDialog.dismiss();
                                                    }
                                                })
                                                .show();
                                    } else if (bcm_stat == 11) {
                                        new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                                                .setTitleText("VIN Mismatch")
                                                .setContentText("Please reconnect Pro Series to original vehicle and return BCM settings to stock before transferring Pro Series to a new vehicle.")
                                                .setConfirmText("Okay")
                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                    @Override
                                                    public void onClick(SweetAlertDialog sDialog) {
                                                        sDialog.dismiss();
                                                    }
                                                })
                                                .show();
                                    } else {
                                        startActivity(new Intent(MainActivity.this, FeaturesActivity.class));
                                    }
                                    break;
                                case R.id.btn_diag:
                                    if (pltfrm == 0) {
                                        new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                                                .setTitleText("VIN not Detected")
                                                .setContentText("Please turn the ignition off, unplug the pro dongle, plug it back in, and turn the ignition back on.")
                                                .setConfirmText("Okay")
                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                    @Override
                                                    public void onClick(SweetAlertDialog sDialog) {
                                                        sDialog.dismiss();
                                                    }
                                                })
                                                .show();
                                    } else {
                                        startActivity(new Intent(MainActivity.this, DiagnosticsActivity.class));
                                    }
                                    break;
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

                            String vehicle = variables.getString("pltfrm");
                            SharedPreferences mSharedPreferences = getSharedPreferences("ThemeColor", MODE_PRIVATE);
                            SharedPreferences.Editor edit = mSharedPreferences.edit();
                            if (isAutomatic()) {
                                switch (vehicle) {
                                    case "2":
                                        edit.putInt("vehicle", VGM1);
                                        edit.apply();
                                        break;
                                    case "3":
                                    case "6":
                                    case "7":
                                        edit.putInt("vehicle", VGM2);
                                        edit.apply();
                                        break;
                                    case "12":
                                        edit.putInt("vehicle", VFORD1);
                                        edit.apply();
                                        break;
                                    case "14":
                                        edit.putInt("vehicle", VFORD2);
                                        edit.apply();
                                        break;
                                    case "21":
                                        edit.putInt("vehicle", VRAM);
                                        edit.apply();
                                        break;
                                }
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
                        if (start == 0 || start == 1 || start == 2) {
                            new MyAsyncTaskCodeConnecting(MainActivity.this).execute();
                            start++;
                            new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    // this code will be executed after 3 seconds
                                    sendRequest();
                                }
                            }, 1500);
                        } else {
                            new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
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
                            start = 0;
                        }
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
                                new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
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
                                                                updateRequest();
                                                            }
                                                        }
                                                    }
                                                }, 0, 500);//put here time 1000 milliseconds=1 second
                                            }
                                        })
                                        .show();
                            }

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

                        new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
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
}

