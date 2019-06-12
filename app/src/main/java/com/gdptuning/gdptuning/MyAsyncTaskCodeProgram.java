package com.gdptuning.gdptuning;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.view.View;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

public class MyAsyncTaskCodeProgram extends AsyncTask<String, Integer, Boolean> {
    static private final int Iterations = 3;

    private ProgressDialog mProgress = null;
    @SuppressLint("StaticFieldLeak")
    private Context mContext = null;
    View mView;
    RequestQueue queue;
    WifiManager wifi;
    final String url = "http://192.168.7.1";
    boolean isConnected = false;
    boolean isProcessing = false;

    MyAsyncTaskCodeProgram(Context context) {
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
        mProgress.setMessage("Request in progress. Please wait...");
        mProgress.setTitle("Programming in Progress");
        mProgress.show();
    }

    private void myLongRunningOperation() {
        try {
            Thread.sleep(900);
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
        new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Program Successful")
                .setContentText("Program was Successful, please cycle the ignition to the off position for 5 seconds to complete programming.")
                .setConfirmText("OK")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismiss();
                        Intent i = new Intent(mContext.getApplicationContext(), MainActivity.class);
                        mContext.startActivity(i);
                    }
                })
                .show();
    }


}

