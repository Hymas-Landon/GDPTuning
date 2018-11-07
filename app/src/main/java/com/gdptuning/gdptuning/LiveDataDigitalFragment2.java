package com.gdptuning.gdptuning;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.anastr.speedviewlib.ImageSpeedometer;
import com.github.anastr.speedviewlib.components.Indicators.ImageIndicator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class LiveDataDigitalFragment2 extends Fragment implements View.OnTouchListener, GestureDetector.OnGestureListener {

    private static int VFORD1 = 7;
    private static int VFORD2 = 8;
    private static int VGM1 = 9;
    private static int VGM2 = 10;
    private static int VRAM = 11;

    //ESP32 aREST server address
    final String url = "http://192.168.7.1";
    boolean isConnected = false;
    boolean isProcessing = false;
    String device = "GDP";
    int tuneMode = 0;
    Timer timer;
    RequestQueue queue;
    WifiManager wifi;
    ImageSpeedometer egtGauge, boostGauge, coolantTemp;
    private GestureDetector mGestureDetector;
    public static final String TAG = "GDP Tuning";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_livedata_digital_2, container, false);
        egtGauge = mView.findViewById(R.id.egt_temp);
        boostGauge = mView.findViewById(R.id.boost);
        coolantTemp = mView.findViewById(R.id.coolant_temp);
        egtGauge.setOnTouchListener(this);
        boostGauge.setOnTouchListener(this);
        coolantTemp.setOnTouchListener(this);
        mGestureDetector = new GestureDetector(getActivity(), this);

        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        //Working with wifi
        queue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()));
        wifi = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        sendRequest();
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                if (isConnected) {
                    if (!isProcessing) {
                        Log.d("TEST2 :", "Sending request");
                        updateRequest();
                    }
                }
            }
        }, 0, 500);//put here time 1000 milliseconds=1 second
    }

    private int getVehicleType() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(this.getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
        return mSharedPreferences.getInt("vehicle", VFORD1);
    }

    private boolean isMetric() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(this.getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
        return mSharedPreferences.getBoolean("metric", false);
    }

    @Override
    public void onPause() {
        super.onPause();
        timer.cancel();
        isProcessing = false;
    }

    //Send to sGDP server to verify connection
    public void sendRequest() {
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        isConnected = true;
                        try {
                            JSONObject variables = response.getJSONObject("variables");
                            Log.d("TEST2 ", variables.toString());
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
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isConnected = false;
                        Log.d("Error.Response", error.toString());

                    }
                }
        );
        // add it to the RequestQueue
        queue.add(getRequest);
    }

    //Send to sGDP server to get live data
    public void updateRequest() {
        isProcessing = true;
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        isConnected = true;
                        try {
                            JSONObject variables = response.getJSONObject("variables");
                            Log.d("TEST2 ", variables.toString());
                            String deviceName = response.getString("name");
                            deviceName += response.getString("id");
                            device = deviceName;

                            ImageIndicator mImageIndicatorSmall = new ImageIndicator(Objects.requireNonNull(getContext()), R.drawable.needle2);
                            ImageIndicator mImageIndicatorLarge = new ImageIndicator(Objects.requireNonNull(getContext()), R.drawable.needle1);

                            float frp = variables.getInt("frp");
                            float timing = variables.getInt(("timing"));
                            float coolant = variables.getInt("coolant");

                            // EGT Temp Gauge
                            egtGauge.setIndicator(mImageIndicatorSmall);
                            egtGauge.speedTo((float) (frp * 145.0377));

                            // Boost Gauges
                            boostGauge.setIndicator(mImageIndicatorSmall);
                            boostGauge.speedTo(timing);

                            // Coolant Temp
                            coolantTemp.setIndicator(mImageIndicatorLarge);
                            coolantTemp.speedTo((float) ((coolant * 1.8) + 32));

                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }

                        isProcessing = false;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isConnected = false;
                        Log.d("Error.Response", error.toString());


                        isProcessing = false;
                    }
                }
        );
        // add it to the RequestQueue
        queue.add(getRequest);
    }


    @Override
    public boolean onTouch(View mView, MotionEvent mMotionEvent) {
        mGestureDetector.onTouchEvent(mMotionEvent);

        int action = mMotionEvent.getAction();

        switch (action) {
            case (MotionEvent.ACTION_DOWN):
                Log.d(TAG, "Action was DOWN");
                return true;
            case (MotionEvent.ACTION_MOVE):
                Log.d(TAG, "Action was MOVE");
                return true;
            case (MotionEvent.ACTION_UP):
                Log.d(TAG, "Action was UP");
                return true;
            case (MotionEvent.ACTION_CANCEL):
                Log.d(TAG, "Action was CANCEL");
                return true;
            case (MotionEvent.ACTION_OUTSIDE):
                Log.d(TAG, "Movement occurred outside bounds " +
                        "of current screen element");
                return true;
            default:
                return mView.onTouchEvent(mMotionEvent);
        }
//
//
//        if (getView().getId() == R.id.egt_temp) {
//            mGestureDetector.onTouchEvent(mMotionEvent);
//            return true;
//        }
//        if (getView().getId() == R.id.coolant_temp) {
//            return false;
//        }
//        if (getView().getId() == R.id.boost) ;
//        {
//
//        }
//
//        return true;
    }

    /*
     * Gesture Detectors
     * */
    @Override
    public boolean onDown(MotionEvent mMotionEvent) {
        Log.d(TAG, "onDown: called");
        return false;
    }

    @Override
    public void onShowPress(MotionEvent mMotionEvent) {
        Log.d(TAG, "onShowPress: called");
    }

    @Override
    public boolean onSingleTapUp(MotionEvent mMotionEvent) {
        Log.d(TAG, "onSingleTapUp: called");
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent mMotionEvent, MotionEvent mMotionEvent1, float mV, float mV1) {
        Log.d(TAG, "onScroll: called");
        return false;
    }

    @Override
    public void onLongPress(MotionEvent mMotionEvent) {
        Log.d(TAG, "onLongPress: called");
    }

    @Override
    public boolean onFling(MotionEvent mMotionEvent, MotionEvent mMotionEvent1, float mV, float mV1) {
        Log.d(TAG, "onFling: called");
        return false;
    }


}
