package com.gdptuning.gdptuning;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ToggleButton;


public class ConfigurationActivity extends AppCompatActivity implements View.OnClickListener {

    Button btn_home;
    ImageView wifi_switch;
    private ToggleButton wifiSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_configuration);


        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = null;
        if (connManager != null) {
            mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        }


        //Working with wifi
        wifi_switch = findViewById(R.id.wifi_switch);

        // Connected to WiFi
        if (mWifi != null) {
            if (mWifi.isConnected()) {
                wifi_switch.setBackgroundResource(R.drawable.wifi_pressed);
            } else {
                wifi_switch.setBackgroundResource(R.drawable.wifi_not_connected_pressed);
            }
        }

        wifi_switch.setOnClickListener(this);


        btn_home = findViewById(R.id.btn_home);
        btn_home.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        switch (id) {
            case R.id.wifi_switch:
                startActivity(new Intent(ConfigurationActivity.this, WifiActivity.class));
                break;
            case R.id.btn_home:
                startActivity(new Intent(ConfigurationActivity.this, MainActivity.class));
                break;
        }
    }
}
