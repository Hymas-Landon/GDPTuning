package com.gdptuning.gdptuning;

import android.app.Application;
import android.os.SystemClock;

public class GdpApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SystemClock.sleep(2000);
    }
}

