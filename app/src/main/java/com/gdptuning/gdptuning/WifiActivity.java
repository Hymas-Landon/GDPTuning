package com.gdptuning.gdptuning;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

public class WifiActivity extends AppCompatActivity implements View.OnClickListener {
    /*
     * We are wanting to give the ability to enable and disable wifi
     * */

    NetworkInfo mWifi;
    TextView wifiSpeed;
    TextView txtWifiInfo;
    TextView connected;
    Button btnWifiSettings, btn_home;
    //WiFi Variables
    private ToggleButton wifi_switch;
    private WifiManager wifiManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_wifi);

        //set home button
        btn_home = findViewById(R.id.btn_home);

        //onclick
        btn_home.setOnClickListener(this);

        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connManager != null) {
            mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        }
        btnWifiSettings = findViewById(R.id.cur_wifi);

        //Working with wifi
        wifi_switch = findViewById(R.id.wifi_switch);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        txtWifiInfo = findViewById(R.id.current_ssid);
        wifiSpeed = findViewById(R.id.current_password);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int linkSpeed = wifiInfo.getLinkSpeed();
        String ssid = wifiInfo.getSSID();
        txtWifiInfo.setText(ssid);
        wifiSpeed.setText(String.valueOf(linkSpeed));
        connected = findViewById(R.id.connection);
        wifi_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if ((isChecked) && (mWifi.isConnected())) {
                    wifiManager.setWifiEnabled(true);
                    wifi_switch.setBackgroundResource(R.drawable.wificonnected);
                    connected.setText(R.string.wifi_connection);
                } else {
                    wifiManager.setWifiEnabled(false);
                    wifi_switch.setBackgroundResource(R.drawable.wifi_not_connected);
                    connected.setText(R.string.no_connection);
                }
            }
        });

        if ((wifiManager.isWifiEnabled()) && (mWifi.isConnected())) {
            wifi_switch.setChecked(true);
            wifi_switch.setBackgroundResource(R.drawable.wificonnected);
            connected.setText(R.string.wifi_connection);
        } else {
            wifi_switch.setChecked(false);
            wifi_switch.setBackgroundResource(R.drawable.wifi_not_connected);
            connected.setText(R.string.no_connection);
        }

    }

    public void goToWifiSettings(View view) {
        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.btn_home:
                startActivity(new Intent(WifiActivity.this, MainActivity.class));
                break;
        }
    }
}