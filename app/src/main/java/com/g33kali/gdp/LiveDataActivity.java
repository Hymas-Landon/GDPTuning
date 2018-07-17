package com.g33kali.gdp;

import android.content.Intent;
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

import java.util.Timer;

public class LiveDataActivity extends AppCompatActivity {


    TextView tvBoost, tvEgt, tvOilPressure, tvFule, tvTrubo, tvDfrp, tvTiming, tvCoolant, tvGear, tvAfrp, tvTune;
    ImageView btn_info, btn_connection;
    Button btn_tune;
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
        //Set onClick listeners
        btn_tune.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent click = new Intent(LiveDataActivity.this, LiveDataBarActivity.class);
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
        setContentView(R.layout.activity_live_data);
        change();

    }
}

/*******************THIS IS WHERE THE OLD CODE BEGINS*******************/
/*
 *
 *
 *
 *
 *
 *
 *
 * */
////        btn_connection = findViewById(R.id.btn_connection);
////        tvBoost = findViewById(R.id.tv_boost);
////        tvEgt = findViewById(R.id.tv_egt);
////        tvOilPressure = findViewById(R.id.tv_oil_pressure);
////        tvFule = findViewById(R.id.tv_fuel);
////        tvTurbo = findViewById(R.id.tv_turbo);
////        tvDfrp = findViewById(R.id.tv_dfrp);
////        tvTiming = findViewById(R.id.tv_timing);
////        tvCoolant = findViewById(R.id.tv_coolant);
////        tvGear = findViewById(R.id.tv_gear);
////        tvAfrp = findViewById(R.id.tv_afrp);
////        tvTune = findViewById(R.id.tv_tune);
//
//        queue = Volley.newRequestQueue(this);
//        sendRequest();
//
//        timer = new Timer();
//        timer.scheduleAtFixedRate(new TimerTask() {
//            int num = 1;
//
//            @Override
//            public void run() {
//                if (isConnected) {
//                    if (!isProcessing) {
//                        Log.d("TEST2 :", "Sending request");
//                        updateRequest();
//                    }
//                }
//
//            }
//        }, 0, 500);//put here time 1000 milliseconds=1 second
//
//
//        @Override
//        protected void onPause () {
//            super.onPause();
//            timer.cancel();
//        }
//
//        @Override
//        protected void onResume () {
//            super.onResume();
//            timer = new Timer();
//            timer.scheduleAtFixedRate(new TimerTask() {
//                int num = 1;
//
//                @Override
//                public void run() {
//                    if (isConnected) {
//                        if (!isProcessing) {
//                            Log.d("TEST2 :", "Sending request");
//                            updateRequest();
//                        }
//                    }
//
//                }
//            }, 0, 500);//put here time 1000 milliseconds=1 second
//        }
//
//        //Send to sGDP server to verify connection
//        public void sendRequest() {
//            // prepare the Request
//            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
//                    new Response.Listener<JSONObject>() {
//                        @Override
//                        public void onResponse(JSONObject response) {
//                            isConnected = true;
//                            btn_connection.setImageResource(R.drawable.wificonnected);
//                            try {
//                                JSONObject variables = response.getJSONObject("variables");
//                                Log.d("TEST2 ", variables.toString());
//                                tuneMode = variables.getInt("tune_mode");
//                                String deviceName = response.getString("name");
//                                deviceName += response.getString("id");
//                                device = deviceName;
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                            // display response
//                            Log.d("Response", response.toString());
//                        }
//                    },
//                    new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            isConnected = false;
//                            btn_connection.setImageResource(R.drawable.wifi_not_connected);
//                            Log.d("Error.Response", error.toString());
//
//                            new SweetAlertDialog(LiveDataActivity.this, SweetAlertDialog.WARNING_TYPE)
//                                    .setTitleText("No Connection")
//                                    .setContentText("Your are not connected to GDP device")
//                                    .setCancelText("Retry")
//                                    .setConfirmText("Connect")
//                                    .showCancelButton(true)
//                                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                                        @Override
//                                        public void onClick(SweetAlertDialog sDialog) {
//                                            sendRequest();
//                                            sDialog.dismiss();
//                                        }
//                                    })
//                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                                        @Override
//                                        public void onClick(SweetAlertDialog sDialog) {
//                                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
//                                        }
//                                    })
//                                    .show();
//                        }
//                    }
//            );
//
//            // add it to the RequestQueue
//            queue.add(getRequest);
//
//        }
//
//        //Send to sGDP server to get live data
//        public void updateRequest () {
//            isProcessing = true;
//            // prepare the Request
//            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
//                    new Response.Listener<JSONObject>() {
//                        @Override
//                        public void onResponse(JSONObject response) {
//                            isConnected = true;
//
//                            btn_connection.setImageResource(R.drawable.wificonnected);
//                            try {
//                                JSONObject variables = response.getJSONObject("variables");
//                                Log.d("TEST2 ", variables.toString());
//                                tuneMode = variables.getInt("tune_mode");
//                                tvTune.setText("Tune :" + tuneMode);
//                                tvBoost.setText(variables.getString("boost"));
//                                tvEgt.setText(variables.getString("egt"));
//                                tvFule.setText(variables.getString("fule"));
//                                tvOilPressure.setText(variables.getString("oil_pressur"));
//                                tvTrubo.setText(variables.getString("turbo"));
//                                tvDfrp.setText(variables.getString("frp"));
//                                tvTiming.setText(variables.getString("timing"));
//                                tvCoolant.setText(variables.getString("coolant"));
//                                tvGear.setText(variables.getString("gear"));
//                                tvAfrp.setText(variables.getString("frp"));
//
//                                Log.d("Response", response.toString());
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                            // display response
//
//                            isProcessing = false;
//                        }
//                    },
//                    new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            isConnected = false;
//                            btn_connection.setImageResource(R.drawable.wifi_not_connected);
//                            Log.d("Error.Response", error.toString());
//
//                            new SweetAlertDialog(LiveDataActivity.this, SweetAlertDialog.WARNING_TYPE)
//                                    .setTitleText("No Connection")
//                                    .setContentText("Your are not connected to GDP device")
//                                    .setCancelText("Retry")
//                                    .setConfirmText("Connect")
//                                    .showCancelButton(true)
//                                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                                        @Override
//                                        public void onClick(SweetAlertDialog sDialog) {
//                                            sendRequest();
//                                            sDialog.dismiss();
//                                        }
//                                    })
//                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                                        @Override
//                                        public void onClick(SweetAlertDialog sDialog) {
//                                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
//                                        }
//                                    })
//                                    .show();
//                            isProcessing = false;
//                        }
//                    }
//            );
//
//            // add it to the RequestQueue
//            queue.add(getRequest);
//
//        }
//
//    }
//}

