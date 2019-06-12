package com.gdptuning.gdptuning;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

public class MyAsyncTaskCodeReset extends AsyncTask<String, Integer, Boolean> {
    static private final int Iterations = 2;

    private ProgressDialog mProgress = null;
    private Context mContext = null;
    RequestQueue queue;
    WifiManager wifi;
    final String url = "http://192.168.7.1";
    boolean isConnected = false;
    boolean isProcessing = false;

    MyAsyncTaskCodeReset(Context context) {
        mContext = context;
    }

    @Override
    protected Boolean doInBackground(String... mStrings) {
        for (int i = 0; i < Iterations; i++) {
            myLongRunningOperation();
            queue = Volley.newRequestQueue(mContext);
        }
        return true;
    }

    @Override
    protected void onPreExecute() {
        mProgress = new ProgressDialog(mContext);

        mProgress.setMessage("Returning to factory settings. Please wait...");
        mProgress.setTitle("Request in Progress.");

        mProgress.show();
    }

    private void myLongRunningOperation() {
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        mProgress.setProgress(progress[0]);
    }

    @Override
    protected void onPostExecute(Boolean mBoolean) {
        mProgress.dismiss();
        Intent i = new Intent(mContext.getApplicationContext(), MainActivity.class);
        mContext.startActivity(i);
    }
}
