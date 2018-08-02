package com.gdptuning.gdptuning;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //ESP32 aREST server address
    final String url = "http://192.168.7.1";
    boolean isConnected = false;
    String device = "GDP";

    //Variables to work with layout
    ImageView btn_info, wifi_switch;
    Button btn_tune, btn_live, btn_diagnostics, btn_configuration, btn_home, btn_red, btn_green, btn_blue;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = null;
        if (connManager != null) {
            mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        }


        //Working with wifi
        wifi_switch = findViewById(R.id.wifi_switch);
        wifi_switch.setOnClickListener(this);

        //Set widgets
        btn_tune = findViewById(R.id.btn_tune);
        btn_live = findViewById(R.id.btn_live_data);
        btn_info = findViewById(R.id.btn_info);
        btn_configuration = findViewById(R.id.btn_config);
        btn_diagnostics = findViewById(R.id.btn_diag);
        btn_home = findViewById(R.id.btn_home);


        //Set OnClick Listener
        wifi_switch.setOnClickListener(this);
        btn_tune.setOnClickListener(this);
        btn_live.setOnClickListener(this);
        btn_info.setOnClickListener(this);
        btn_configuration.setOnClickListener(this);
        btn_diagnostics.setOnClickListener(this);
        btn_home.setOnClickListener(this);

        queue = VolleySingleton.getInstance(this).getRequestQueue();

//        sendRequest();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        sendRequest();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        sendRequest();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        switch (id) {
            case R.id.btn_tune:
                startActivity(new Intent(MainActivity.this, TuneActivity.class));
                break;
            case R.id.btn_live_data:
                startActivity(new Intent(MainActivity.this, LiveDataActivity.class));
                break;
            case R.id.btn_info:
                startActivity(new Intent(MainActivity.this, InfoActivity.class));
                break;
            case R.id.wifi_switch:
                startActivity(new Intent(MainActivity.this, WifiActivity.class));
                break;
            case R.id.btn_config:
                startActivity(new Intent(MainActivity.this, ConfigurationActivity.class));
                break;
            case R.id.btn_diag:
                startActivity(new Intent(MainActivity.this, DiagnosticsActivity.class));
                break;
            case R.id.btn_home:
                startActivity(new Intent(MainActivity.this, MainActivity.class));
                break;
        }


    }

    //Send to sGDP server to verify connection
    public void sendRequest(){
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        isConnected = true;
                        wifi_switch.setImageResource(R.drawable.wificonnected);
                        try {
                            String deviceName = response.getString("name");
                            deviceName += response.getString("id");
                            device = deviceName;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // display response
                        Log.d("Response", response.toString());
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isConnected = false;
                        wifi_switch.setImageResource(R.drawable.wifi_not_connected);
                        Log.d("Error.Response", error.toString());
                    }
                }
        );

        // add it to the RequestQueue
        queue.add(getRequest);

    }

    //Show Connection details
    void displayDevicecInfo(){
        if (isConnected){
            new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Connected")
                    .setContentText("You are connected to "+device)
                    .setConfirmText("ok")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            // reuse previous dialog instance
                            sDialog.dismiss();
                        }
                    })
                    .show();
        }else {
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
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
        }

    }
}


