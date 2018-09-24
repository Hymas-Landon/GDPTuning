package com.gdptuning.gdptuning;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
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

public class TuneActivity extends AppCompatActivity implements View.OnClickListener {

    //ESP32 aREST server address
    final String url = "http://192.168.7.1";
    boolean isConnected = false;
    boolean isProcessing = false;
    String device = "GDP";
    Button btn1, btn2, btn3, btn4, btn5, btn_num1, btn_num2, btn_num3, btn_num4, btn_num5, btn_home;
    int tuneMode = 0;
    WifiManager wifi;
    TextView tvTune, tvGear;
    Timer timer;
    private RequestQueue queue;


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

        setContentView(R.layout.activity_tune);

        //set widget
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);
        btn5 = findViewById(R.id.btn5);
        btn_num1 = findViewById(R.id.tgl_num1);
        btn_num2 = findViewById(R.id.tgl_num2);
        btn_num3 = findViewById(R.id.tgl_num3);
        btn_num4 = findViewById(R.id.tgl_num4);
        btn_num5 = findViewById(R.id.tgl_num5);
        tvTune = findViewById(R.id.tunenum);
        tvGear = findViewById(R.id.gear_position);

        //Set On Click Listener
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn_num1.setOnClickListener(this);
        btn_num2.setOnClickListener(this);
        btn_num3.setOnClickListener(this);
        btn_num4.setOnClickListener(this);
        btn_num5.setOnClickListener(this);
        btn_home = findViewById(R.id.btn_home);
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                Intent i = new Intent(TuneActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        queue = VolleySingleton.getInstance(this).getRequestQueue();
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
        Intent i = new Intent(TuneActivity.this, MainActivity.class);
        startActivity(i);
    }

    @Override
    protected void onPause() {
        isProcessing = false;
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
    protected void onRestart() {
        super.onRestart();
        sendRequest();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn1:
            case R.id.tgl_num1:
                switchMode(1);
                if (getColorTheme() == Utils.THEME_DEFAULT) {
                    btn1.setBackgroundResource(R.drawable.tune_on);
                } else if (getColorTheme() == Utils.THEME_GREEN) {
                    btn1.setBackgroundResource(R.drawable.tune_on_green);
                } else if (getColorTheme() == Utils.THEME_BLUE) {
                    btn1.setBackgroundResource(R.drawable.tune_on_blue);
                } else if (getColorTheme() == Utils.THEME_RED) {
                    btn1.setBackgroundResource(R.drawable.tune_on_red);
                }
                btn2.setBackgroundResource(R.drawable.tune_off);
                btn3.setBackgroundResource(R.drawable.tune_off);
                btn4.setBackgroundResource(R.drawable.tune_off);
                btn5.setBackgroundResource(R.drawable.tune_off);
                if (getColorTheme() == Utils.THEME_DEFAULT) {
                    btn_num1.setBackgroundResource(R.drawable.orange1);
                } else if (getColorTheme() == Utils.THEME_GREEN) {
                    btn_num1.setBackgroundResource(R.drawable.green1);
                } else if (getColorTheme() == Utils.THEME_BLUE) {
                    btn_num1.setBackgroundResource(R.drawable.blue1);
                } else if (getColorTheme() == Utils.THEME_RED) {
                    btn_num1.setBackgroundResource(R.drawable.red1);
                }
                btn_num2.setBackgroundResource(R.drawable.grey2);
                btn_num3.setBackgroundResource(R.drawable.grey3);
                btn_num4.setBackgroundResource(R.drawable.grey4);
                btn_num5.setBackgroundResource(R.drawable.grey5);
                break;
            case R.id.btn2:
            case R.id.tgl_num2:
                switchMode(2);
                btn1.setBackgroundResource(R.drawable.tune_off);
                if (getColorTheme() == Utils.THEME_DEFAULT) {
                    btn2.setBackgroundResource(R.drawable.tune_on);
                } else if (getColorTheme() == Utils.THEME_GREEN) {
                    btn2.setBackgroundResource(R.drawable.tune_on_green);
                } else if (getColorTheme() == Utils.THEME_BLUE) {
                    btn2.setBackgroundResource(R.drawable.tune_on_blue);
                } else if (getColorTheme() == Utils.THEME_RED) {
                    btn2.setBackgroundResource(R.drawable.tune_on_red);
                }
                btn3.setBackgroundResource(R.drawable.tune_off);
                btn4.setBackgroundResource(R.drawable.tune_off);
                btn5.setBackgroundResource(R.drawable.tune_off);
                btn_num1.setBackgroundResource(R.drawable.grey1);
                if (getColorTheme() == Utils.THEME_DEFAULT) {
                    btn_num2.setBackgroundResource(R.drawable.orange2);
                } else if (getColorTheme() == Utils.THEME_GREEN) {
                    btn_num2.setBackgroundResource(R.drawable.green2);
                } else if (getColorTheme() == Utils.THEME_BLUE) {
                    btn_num2.setBackgroundResource(R.drawable.blue2);
                } else if (getColorTheme() == Utils.THEME_RED) {
                    btn_num2.setBackgroundResource(R.drawable.red2);
                }
                btn_num3.setBackgroundResource(R.drawable.grey3);
                btn_num4.setBackgroundResource(R.drawable.grey4);
                btn_num5.setBackgroundResource(R.drawable.grey5);
                break;
            case R.id.btn3:
            case R.id.tgl_num3:
                switchMode(3);
                btn1.setBackgroundResource(R.drawable.tune_off);
                btn2.setBackgroundResource(R.drawable.tune_off);
                if (getColorTheme() == Utils.THEME_DEFAULT) {
                    btn3.setBackgroundResource(R.drawable.tune_on);
                } else if (getColorTheme() == Utils.THEME_GREEN) {
                    btn3.setBackgroundResource(R.drawable.tune_on_green);
                } else if (getColorTheme() == Utils.THEME_BLUE) {
                    btn3.setBackgroundResource(R.drawable.tune_on_blue);
                } else if (getColorTheme() == Utils.THEME_RED) {
                    btn3.setBackgroundResource(R.drawable.tune_on_red);
                }
                btn4.setBackgroundResource(R.drawable.tune_off);
                btn5.setBackgroundResource(R.drawable.tune_off);
                btn_num1.setBackgroundResource(R.drawable.grey1);
                btn_num2.setBackgroundResource(R.drawable.grey2);
                if (getColorTheme() == Utils.THEME_DEFAULT) {
                    btn_num3.setBackgroundResource(R.drawable.orange3);
                } else if (getColorTheme() == Utils.THEME_GREEN) {
                    btn_num3.setBackgroundResource(R.drawable.green3);
                } else if (getColorTheme() == Utils.THEME_BLUE) {
                    btn_num3.setBackgroundResource(R.drawable.blue3);
                } else if (getColorTheme() == Utils.THEME_RED) {
                    btn_num3.setBackgroundResource(R.drawable.red3);
                }
                btn_num4.setBackgroundResource(R.drawable.grey4);
                btn_num5.setBackgroundResource(R.drawable.grey5);
                break;
            case R.id.btn4:
            case R.id.tgl_num4:
                switchMode(4);
                btn1.setBackgroundResource(R.drawable.tune_off);
                btn2.setBackgroundResource(R.drawable.tune_off);
                btn3.setBackgroundResource(R.drawable.tune_off);
                if (getColorTheme() == Utils.THEME_DEFAULT) {
                    btn4.setBackgroundResource(R.drawable.tune_on);
                } else if (getColorTheme() == Utils.THEME_GREEN) {
                    btn4.setBackgroundResource(R.drawable.tune_on_green);
                } else if (getColorTheme() == Utils.THEME_BLUE) {
                    btn4.setBackgroundResource(R.drawable.tune_on_blue);
                } else if (getColorTheme() == Utils.THEME_RED) {
                    btn4.setBackgroundResource(R.drawable.tune_on_red);
                }
                btn5.setBackgroundResource(R.drawable.tune_off);
                btn_num1.setBackgroundResource(R.drawable.grey1);
                btn_num2.setBackgroundResource(R.drawable.grey2);
                btn_num3.setBackgroundResource(R.drawable.grey3);
                if (getColorTheme() == Utils.THEME_DEFAULT) {
                    btn_num4.setBackgroundResource(R.drawable.orange4);
                } else if (getColorTheme() == Utils.THEME_GREEN) {
                    btn_num4.setBackgroundResource(R.drawable.green4);
                } else if (getColorTheme() == Utils.THEME_BLUE) {
                    btn_num4.setBackgroundResource(R.drawable.blue4);
                } else if (getColorTheme() == Utils.THEME_RED) {
                    btn_num4.setBackgroundResource(R.drawable.red4);
                }
                btn_num5.setBackgroundResource(R.drawable.grey5);
                break;
            case R.id.btn5:
            case R.id.tgl_num5:
                switchMode(5);
                btn1.setBackgroundResource(R.drawable.tune_off);
                btn2.setBackgroundResource(R.drawable.tune_off);
                btn3.setBackgroundResource(R.drawable.tune_off);
                btn4.setBackgroundResource(R.drawable.tune_off);
                if (getColorTheme() == Utils.THEME_DEFAULT) {
                    btn5.setBackgroundResource(R.drawable.tune_on);
                } else if (getColorTheme() == Utils.THEME_GREEN) {
                    btn5.setBackgroundResource(R.drawable.tune_on_green);
                } else if (getColorTheme() == Utils.THEME_BLUE) {
                    btn5.setBackgroundResource(R.drawable.tune_on_blue);
                } else if (getColorTheme() == Utils.THEME_RED) {
                    btn5.setBackgroundResource(R.drawable.tune_on_red);
                }
                btn_num1.setBackgroundResource(R.drawable.grey1);
                btn_num2.setBackgroundResource(R.drawable.grey2);
                btn_num3.setBackgroundResource(R.drawable.grey3);
                btn_num4.setBackgroundResource(R.drawable.grey4);
                if (getColorTheme() == Utils.THEME_DEFAULT) {
                    btn_num5.setBackgroundResource(R.drawable.orange5);
                } else if (getColorTheme() == Utils.THEME_GREEN) {
                    btn_num5.setBackgroundResource(R.drawable.green5);
                } else if (getColorTheme() == Utils.THEME_BLUE) {
                    btn_num5.setBackgroundResource(R.drawable.blue5);
                } else if (getColorTheme() == Utils.THEME_RED) {
                    btn_num5.setBackgroundResource(R.drawable.red5);
                }
                break;

            default:
                btn_num1.setBackgroundResource(R.drawable.grey1);
                btn_num2.setBackgroundResource(R.drawable.grey2);
                btn_num3.setBackgroundResource(R.drawable.grey3);
                btn_num4.setBackgroundResource(R.drawable.grey4);
                btn_num5.setBackgroundResource(R.drawable.grey5);
        }
    }

    void setTuneMode(int tuneMode) {
        Log.d("Response", " " + tuneMode);
        switch (tuneMode) {
            case 1:
                if (getColorTheme() == Utils.THEME_DEFAULT) {
                    btn1.setBackgroundResource(R.drawable.tune_on);
                } else if (getColorTheme() == Utils.THEME_GREEN) {
                    btn1.setBackgroundResource(R.drawable.tune_on_green);
                } else if (getColorTheme() == Utils.THEME_BLUE) {
                    btn1.setBackgroundResource(R.drawable.tune_on_blue);
                } else if (getColorTheme() == Utils.THEME_RED) {
                    btn1.setBackgroundResource(R.drawable.tune_on_red);
                }
                btn2.setBackgroundResource(R.drawable.tune_off);
                btn3.setBackgroundResource(R.drawable.tune_off);
                btn4.setBackgroundResource(R.drawable.tune_off);
                btn5.setBackgroundResource(R.drawable.tune_off);
                if (getColorTheme() == Utils.THEME_DEFAULT) {
                    btn_num1.setBackgroundResource(R.drawable.orange1);
                } else if (getColorTheme() == Utils.THEME_GREEN) {
                    btn_num1.setBackgroundResource(R.drawable.green1);
                } else if (getColorTheme() == Utils.THEME_BLUE) {
                    btn_num1.setBackgroundResource(R.drawable.blue1);
                } else if (getColorTheme() == Utils.THEME_RED) {
                    btn_num1.setBackgroundResource(R.drawable.red1);
                }
                btn_num2.setBackgroundResource(R.drawable.grey2);
                btn_num3.setBackgroundResource(R.drawable.grey3);
                btn_num4.setBackgroundResource(R.drawable.grey4);
                btn_num5.setBackgroundResource(R.drawable.grey5);
                break;
            case 2:
                btn1.setBackgroundResource(R.drawable.tune_off);
                if (getColorTheme() == Utils.THEME_DEFAULT) {
                    btn2.setBackgroundResource(R.drawable.tune_on);
                } else if (getColorTheme() == Utils.THEME_GREEN) {
                    btn2.setBackgroundResource(R.drawable.tune_on_green);
                } else if (getColorTheme() == Utils.THEME_BLUE) {
                    btn2.setBackgroundResource(R.drawable.tune_on_blue);
                } else if (getColorTheme() == Utils.THEME_RED) {
                    btn2.setBackgroundResource(R.drawable.tune_on_red);
                }
                btn3.setBackgroundResource(R.drawable.tune_off);
                btn4.setBackgroundResource(R.drawable.tune_off);
                btn5.setBackgroundResource(R.drawable.tune_off);
                btn_num1.setBackgroundResource(R.drawable.grey1);
                if (getColorTheme() == Utils.THEME_DEFAULT) {
                    btn_num2.setBackgroundResource(R.drawable.orange2);
                } else if (getColorTheme() == Utils.THEME_GREEN) {
                    btn_num2.setBackgroundResource(R.drawable.green2);
                } else if (getColorTheme() == Utils.THEME_BLUE) {
                    btn_num2.setBackgroundResource(R.drawable.blue2);
                } else if (getColorTheme() == Utils.THEME_RED) {
                    btn_num2.setBackgroundResource(R.drawable.red2);
                }
                btn_num3.setBackgroundResource(R.drawable.grey3);
                btn_num4.setBackgroundResource(R.drawable.grey4);
                btn_num5.setBackgroundResource(R.drawable.grey5);
                break;
            case 3:
                btn1.setBackgroundResource(R.drawable.tune_off);
                btn2.setBackgroundResource(R.drawable.tune_off);
                if (getColorTheme() == Utils.THEME_DEFAULT) {
                    btn3.setBackgroundResource(R.drawable.tune_on);
                } else if (getColorTheme() == Utils.THEME_GREEN) {
                    btn3.setBackgroundResource(R.drawable.tune_on_green);
                } else if (getColorTheme() == Utils.THEME_BLUE) {
                    btn3.setBackgroundResource(R.drawable.tune_on_blue);
                } else if (getColorTheme() == Utils.THEME_RED) {
                    btn3.setBackgroundResource(R.drawable.tune_on_red);
                }
                btn4.setBackgroundResource(R.drawable.tune_off);
                btn5.setBackgroundResource(R.drawable.tune_off);
                btn_num1.setBackgroundResource(R.drawable.grey1);
                btn_num2.setBackgroundResource(R.drawable.grey2);
                if (getColorTheme() == Utils.THEME_DEFAULT) {
                    btn_num3.setBackgroundResource(R.drawable.orange3);
                } else if (getColorTheme() == Utils.THEME_GREEN) {
                    btn_num3.setBackgroundResource(R.drawable.green3);
                } else if (getColorTheme() == Utils.THEME_BLUE) {
                    btn_num3.setBackgroundResource(R.drawable.blue3);
                } else if (getColorTheme() == Utils.THEME_RED) {
                    btn_num3.setBackgroundResource(R.drawable.red3);
                }
                btn_num4.setBackgroundResource(R.drawable.grey4);
                btn_num5.setBackgroundResource(R.drawable.grey5);
                break;
            case 4:
                btn1.setBackgroundResource(R.drawable.tune_off);
                btn2.setBackgroundResource(R.drawable.tune_off);
                btn3.setBackgroundResource(R.drawable.tune_off);
                if (getColorTheme() == Utils.THEME_DEFAULT) {
                    btn4.setBackgroundResource(R.drawable.tune_on);
                } else if (getColorTheme() == Utils.THEME_GREEN) {
                    btn4.setBackgroundResource(R.drawable.tune_on_green);
                } else if (getColorTheme() == Utils.THEME_BLUE) {
                    btn4.setBackgroundResource(R.drawable.tune_on_blue);
                } else if (getColorTheme() == Utils.THEME_RED) {
                    btn4.setBackgroundResource(R.drawable.tune_on_red);
                }
                btn5.setBackgroundResource(R.drawable.tune_off);
                btn_num1.setBackgroundResource(R.drawable.grey1);
                btn_num2.setBackgroundResource(R.drawable.grey2);
                btn_num3.setBackgroundResource(R.drawable.grey3);
                if (getColorTheme() == Utils.THEME_DEFAULT) {
                    btn_num4.setBackgroundResource(R.drawable.orange4);
                } else if (getColorTheme() == Utils.THEME_GREEN) {
                    btn_num4.setBackgroundResource(R.drawable.green4);
                } else if (getColorTheme() == Utils.THEME_BLUE) {
                    btn_num4.setBackgroundResource(R.drawable.blue4);
                } else if (getColorTheme() == Utils.THEME_RED) {
                    btn_num4.setBackgroundResource(R.drawable.red4);
                }
                btn_num5.setBackgroundResource(R.drawable.grey5);
                break;
            case 5:
                btn1.setBackgroundResource(R.drawable.tune_off);
                btn2.setBackgroundResource(R.drawable.tune_off);
                btn3.setBackgroundResource(R.drawable.tune_off);
                btn4.setBackgroundResource(R.drawable.tune_off);
                if (getColorTheme() == Utils.THEME_DEFAULT) {
                    btn5.setBackgroundResource(R.drawable.tune_on);
                } else if (getColorTheme() == Utils.THEME_GREEN) {
                    btn5.setBackgroundResource(R.drawable.tune_on_green);
                } else if (getColorTheme() == Utils.THEME_BLUE) {
                    btn5.setBackgroundResource(R.drawable.tune_on_blue);
                } else if (getColorTheme() == Utils.THEME_RED) {
                    btn5.setBackgroundResource(R.drawable.tune_on_red);
                }
                btn_num1.setBackgroundResource(R.drawable.grey1);
                btn_num2.setBackgroundResource(R.drawable.grey2);
                btn_num3.setBackgroundResource(R.drawable.grey3);
                btn_num4.setBackgroundResource(R.drawable.grey4);
                if (getColorTheme() == Utils.THEME_DEFAULT) {
                    btn_num5.setBackgroundResource(R.drawable.orange5);
                } else if (getColorTheme() == Utils.THEME_GREEN) {
                    btn_num5.setBackgroundResource(R.drawable.green5);
                } else if (getColorTheme() == Utils.THEME_BLUE) {
                    btn_num5.setBackgroundResource(R.drawable.blue5);
                } else if (getColorTheme() == Utils.THEME_RED) {
                    btn_num5.setBackgroundResource(R.drawable.red5);
                }
                break;
            default:
                btn1.setBackgroundResource(R.drawable.tune_off);
                btn2.setBackgroundResource(R.drawable.tune_off);
                btn3.setBackgroundResource(R.drawable.tune_off);
                btn4.setBackgroundResource(R.drawable.tune_off);
                btn5.setBackgroundResource(R.drawable.tune_off);
        }
    }

    //Send to sGDP server to verify connection

    public void sendRequest() {
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @SuppressLint("SetTextI18n")
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

                            setTuneMode(tuneMode);

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

                        new SweetAlertDialog(TuneActivity.this, SweetAlertDialog.WARNING_TYPE)
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
    //Send to sGDP server to get live data

    public void updateRequest() {
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

                        new SweetAlertDialog(TuneActivity.this, SweetAlertDialog.WARNING_TYPE)
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

    //Send to sGDP server to verify connection
    void switchMode(int mode) {
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url + "/change_mode?params=" + mode, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        isConnected = true;
                        try {
                            tuneMode = response.getInt("return_value");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // display response
                        Log.d("Response", response.toString());
                        setTuneMode(tuneMode);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isConnected = false;
                        Log.d("Error.Response", error.toString());

                        new SweetAlertDialog(TuneActivity.this, SweetAlertDialog.WARNING_TYPE)
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