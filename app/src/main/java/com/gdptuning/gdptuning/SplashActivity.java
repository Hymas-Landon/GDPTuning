package com.gdptuning.gdptuning;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        ImageView iv = findViewById(R.id.iv);

        final Animation fade_in = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        iv.startAnimation(fade_in);

        final Intent i = new Intent(this, MainActivity.class);
        Thread timer = new Thread() {
            public void run() {

                try {
                    synchronized (this) {
                        sleep(4500);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    startActivity(i);
                    finish();
                }
            }
        };
        timer.start();
    }
}
