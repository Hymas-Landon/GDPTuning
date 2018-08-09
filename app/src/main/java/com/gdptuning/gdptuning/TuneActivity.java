package com.gdptuning.gdptuning;

import android.content.Context;
import android.content.Intent;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

public class TuneActivity extends AppCompatActivity implements View.OnClickListener {

    //ESP32 aREST server address
    final String url = "http://192.168.7.1";
    boolean isConnected = false;
    String device = "GDP";
    Button btn1, btn2, btn3, btn4, btn5, btn_num1, btn_num2, btn_num3, btn_num4, btn_num5, btn_home;
    ImageView btn_info, wifi_switch;
    int tuneMode = 0;
    WifiManager wifi;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
        btn_home = findViewById(R.id.btn_home);
        wifi_switch = findViewById(R.id.wifi_switch);

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
        btn_home.setOnClickListener(this);

        //Working with wifi
        queue = VolleySingleton.getInstance(this).getRequestQueue();
        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifi_switch = findViewById(R.id.wifi_switch);
        wifi_switch.setOnClickListener(this);
        if (wifi.isWifiEnabled()) {
            wifi_switch.setImageResource(R.drawable.wifi_pressed);
        } else {
            wifi_switch.setImageResource(R.drawable.wifi_not_connected_pressed);
        }
        sendRequest();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn1:
            case R.id.tgl_num1:
                switchMode(1);
                btn1.setBackgroundResource(R.drawable.tune_on);
                btn2.setBackgroundResource(R.drawable.tune_off);
                btn3.setBackgroundResource(R.drawable.tune_off);
                btn4.setBackgroundResource(R.drawable.tune_off);
                btn5.setBackgroundResource(R.drawable.tune_off);
                btn_num1.setBackgroundResource(R.drawable.orange1);
                btn_num2.setBackgroundResource(R.drawable.grey2);
                btn_num3.setBackgroundResource(R.drawable.grey3);
                btn_num4.setBackgroundResource(R.drawable.grey4);
                btn_num5.setBackgroundResource(R.drawable.grey5);
                break;
            case R.id.btn2:
            case R.id.tgl_num2:
                switchMode(2);
                btn1.setBackgroundResource(R.drawable.tune_off);
                btn2.setBackgroundResource(R.drawable.tune_on);
                btn3.setBackgroundResource(R.drawable.tune_off);
                btn4.setBackgroundResource(R.drawable.tune_off);
                btn5.setBackgroundResource(R.drawable.tune_off);
                btn_num1.setBackgroundResource(R.drawable.grey1);
                btn_num2.setBackgroundResource(R.drawable.orange2);
                btn_num3.setBackgroundResource(R.drawable.grey3);
                btn_num4.setBackgroundResource(R.drawable.grey4);
                btn_num5.setBackgroundResource(R.drawable.grey5);
                break;
            case R.id.btn3:
            case R.id.tgl_num3:
                switchMode(3);
                btn1.setBackgroundResource(R.drawable.tune_off);
                btn2.setBackgroundResource(R.drawable.tune_off);
                btn3.setBackgroundResource(R.drawable.tune_on);
                btn4.setBackgroundResource(R.drawable.tune_off);
                btn5.setBackgroundResource(R.drawable.tune_off);
                btn_num1.setBackgroundResource(R.drawable.grey1);
                btn_num2.setBackgroundResource(R.drawable.grey2);
                btn_num3.setBackgroundResource(R.drawable.orange3);
                btn_num4.setBackgroundResource(R.drawable.grey4);
                btn_num5.setBackgroundResource(R.drawable.grey5);
                break;
            case R.id.btn4:
            case R.id.tgl_num4:
                switchMode(4);
                btn1.setBackgroundResource(R.drawable.tune_off);
                btn2.setBackgroundResource(R.drawable.tune_off);
                btn3.setBackgroundResource(R.drawable.tune_off);
                btn4.setBackgroundResource(R.drawable.tune_on);
                btn5.setBackgroundResource(R.drawable.tune_off);
                btn_num1.setBackgroundResource(R.drawable.grey1);
                btn_num2.setBackgroundResource(R.drawable.grey2);
                btn_num3.setBackgroundResource(R.drawable.grey3);
                btn_num4.setBackgroundResource(R.drawable.orange4);
                btn_num5.setBackgroundResource(R.drawable.grey5);
                break;
            case R.id.btn5:
            case R.id.tgl_num5:
                switchMode(5);
                btn1.setBackgroundResource(R.drawable.tune_off);
                btn2.setBackgroundResource(R.drawable.tune_off);
                btn3.setBackgroundResource(R.drawable.tune_off);
                btn4.setBackgroundResource(R.drawable.tune_off);
                btn5.setBackgroundResource(R.drawable.tune_on);
                btn_num1.setBackgroundResource(R.drawable.grey1);
                btn_num2.setBackgroundResource(R.drawable.grey2);
                btn_num3.setBackgroundResource(R.drawable.grey3);
                btn_num4.setBackgroundResource(R.drawable.grey4);
                btn_num5.setBackgroundResource(R.drawable.orange5);
                break;
            case R.id.wifi_switch:
                displayDeviceInfo();
                break;
            case R.id.btn_home:
                startActivity(new Intent(TuneActivity.this, MainActivity.class));
                break;
        }
    }

    void setTuneMode() {
        Log.d("Response", " " + tuneMode);
        switch (tuneMode) {
            case 1:
                btn1.setBackgroundResource(R.drawable.tune_on);
                btn2.setBackgroundResource(R.drawable.tune_off);
                btn3.setBackgroundResource(R.drawable.tune_off);
                btn4.setBackgroundResource(R.drawable.tune_off);
                btn5.setBackgroundResource(R.drawable.tune_off);
                break;
            case 2:
                btn1.setBackgroundResource(R.drawable.tune_off);
                btn2.setBackgroundResource(R.drawable.tune_on);
                btn3.setBackgroundResource(R.drawable.tune_off);
                btn4.setBackgroundResource(R.drawable.tune_off);
                btn5.setBackgroundResource(R.drawable.tune_off);
                break;
            case 3:
                btn1.setBackgroundResource(R.drawable.tune_off);
                btn2.setBackgroundResource(R.drawable.tune_off);
                btn3.setBackgroundResource(R.drawable.tune_on);
                btn4.setBackgroundResource(R.drawable.tune_off);
                btn5.setBackgroundResource(R.drawable.tune_off);
                break;
            case 4:
                btn1.setBackgroundResource(R.drawable.tune_off);
                btn2.setBackgroundResource(R.drawable.tune_off);
                btn3.setBackgroundResource(R.drawable.tune_off);
                btn4.setBackgroundResource(R.drawable.tune_on);
                btn5.setBackgroundResource(R.drawable.tune_off);
                break;
            case 5:
                btn1.setBackgroundResource(R.drawable.tune_off);
                btn2.setBackgroundResource(R.drawable.tune_off);
                btn3.setBackgroundResource(R.drawable.tune_off);
                btn4.setBackgroundResource(R.drawable.tune_off);
                btn5.setBackgroundResource(R.drawable.tune_on);
                break;
            default:
                btn1.setBackgroundResource(R.drawable.tune_off);
                btn2.setBackgroundResource(R.drawable.tune_off);
                btn3.setBackgroundResource(R.drawable.tune_off);
                btn4.setBackgroundResource(R.drawable.tune_off);
                btn5.setBackgroundResource(R.drawable.tune_off);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    //Send to sGDP server to verify connection
    public void sendRequest() {
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        isConnected = true;
                        wifi_switch.setImageResource(R.drawable.wifi_pressed);
                        try {
                            JSONObject variables = response.getJSONObject("variables");
                            Log.d("TEST2 ", variables.toString());
                            tuneMode = variables.getInt("tune_mode");
                            String deviceName = response.getString("name");
                            deviceName += response.getString("id");
                            device = deviceName;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // display response
                        Log.d("Response", response.toString());
                        setTuneMode();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isConnected = false;
                        wifi_switch.setImageResource(R.drawable.wifi_not_connected_pressed);
                        Log.d("Error.Response", error.toString());

                        new SweetAlertDialog(TuneActivity.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("No Connection")
                                .setContentText("Your are not connected to GDP device")
                                .setCancelText("Cancel")
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

    void switchMode(int mode) {
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url + "/change_mode?params=" + mode, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        isConnected = true;
                        wifi_switch.setImageResource(R.drawable.wifi_pressed);
                        try {

                            tuneMode = response.getInt("return_value");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // display response
                        Log.d("Response", response.toString());
                        setTuneMode();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isConnected = false;
                        wifi_switch.setImageResource(R.drawable.wifi_not_connected_pressed);
                        Log.d("Error.Response", error.toString());

                        new SweetAlertDialog(TuneActivity.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("No Connection")
                                .setContentText("Your are not connected to GDP device")
                                .setCancelText("Cancel")
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

    //Show Connection details
    void displayDeviceInfo() {
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
                    .setContentText("Your are not connected to GDP device")
                    .setCancelText("Cancel")
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
}