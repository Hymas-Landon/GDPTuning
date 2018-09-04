package com.gdptuning.gdptuning;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ImageView;
import android.widget.TextView;

public class WifiActivity extends AppCompatActivity implements View.OnClickListener {
    /*
     * We are wanting to give the ability to enable and disable wifi
     * */

    NetworkInfo mWifi;
    TextView wifiSpeed, txtWifiInfo, connected;
    Button btnWifiSettings, btn_home;

    //WiFi Variables
    ImageView wifi_switch;
    WifiManager wifiManager;


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

        setContentView(R.layout.activity_wifi);

        //set home button
        btn_home = findViewById(R.id.btn_home);

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

        if ((wifiManager.isWifiEnabled()) && (mWifi.isConnected())) {
            wifi_switch.setBackgroundResource(R.drawable.gray_wifi);
            connected.setText(R.string.wifi_connection);
        } else {
            wifi_switch.setBackgroundResource(R.drawable.gray_wifi_not_connected);
            connected.setText(R.string.no_connection);
        }

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