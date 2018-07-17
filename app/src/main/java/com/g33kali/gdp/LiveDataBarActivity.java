package com.g33kali.gdp;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.github.anastr.speedviewlib.ProgressiveGauge;

import java.lang.reflect.Type;
import java.util.Timer;

public class LiveDataBarActivity extends AppCompatActivity{

    TextView tvBoost, tvEgt, tvOilPressure, tvFule, tvTrubo, tvDfrp, tvTiming, tvCoolant, tvGear, tvAfrp, tvTune;
    ImageView btn_info, btn_connection;
    Button btn_tune;
    Typeface tf1;

    RequestQueue queue;
    //ESP32 aREST server address
    final String url = "http://192.168.7.1";
    boolean isConnected = false;
    boolean isProcessing = false;

    String device = "GDP";
    int tuneMode = 0;
    Timer timer;

    // code for test json
    private TextView mTextViewResult;
    private RequestQueue mQueue;

    public void change() {
        //Button variables
        btn_tune = (Button) findViewById(R.id.select_tune);
        //Set font for button
        tf1 = Typeface.createFromAsset(getAssets(), "bahnschrift.ttf");
        btn_tune.setTypeface(tf1);
        //Set onClick listeners
        btn_tune.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent click = new Intent(LiveDataBarActivity.this, LiveDataActivity.class);
                startActivity(click);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_livedata_bar);
        change();


    }
}


