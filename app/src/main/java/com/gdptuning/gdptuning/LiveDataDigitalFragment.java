package com.gdptuning.gdptuning;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class LiveDataDigitalFragment extends Fragment {

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_livedata_digital, container, false);
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
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
        return mSharedPreferences.getInt("vehicle", VFORD1);
    }

    private boolean isMetric() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
        return mSharedPreferences.getBoolean("metric", false);
    }

    @Override
    public void onPause() {
        super.onPause();
        timer.cancel();
        isProcessing = false;
    }

    @Override
    public void onResume() {
        super.onResume();
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
                            tuneMode = variables.getInt("tune_mode");
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

                            String deviceName = response.getString("name");
                            deviceName += response.getString("id");
                            device = deviceName;


                            float egt = variables.getInt("egt");
                            float boost = variables.getInt("boost");
                            float turbo = variables.getInt("turbo");
                            float fuel = variables.getInt("fule");
                            float coolant = variables.getInt("coolant");
                            float oil_pressure = variables.getInt("oil_pressur");

                            ImageIndicator mImageIndicatorSmall = new ImageIndicator(Objects.requireNonNull(getContext()), R.drawable.needle2);
                            ImageIndicator mImageIndicatorLarge = new ImageIndicator(Objects.requireNonNull(getContext()), R.drawable.needle1);



//                            if (getVehicleType() == VFORD1 || getVehicleType() == VFORD2) {
//                                TextView oilText = getView().findViewById(R.id.title4);
//                                oilText.setText("Oil \nTemp");
//                                float fordOilTemp = variables.getInt("oil_temp");

                            // Turbo
                            ImageSpeedometer turboVanes = Objects.requireNonNull(getView()).findViewById(R.id.turbo_vanes);
                            turboVanes.setIndicator(mImageIndicatorSmall);
                            turboVanes.speedTo(turbo);

                            // Fuel
                            ImageSpeedometer injectionFuel = getView().findViewById(R.id.injection_fuel);
                            injectionFuel.setIndicator(mImageIndicatorSmall);
                            injectionFuel.speedTo(fuel);

                            // Oil Pressure
                            ImageSpeedometer oilTemp = getView().findViewById(R.id.oil_temp);
                            oilTemp.setIndicator(mImageIndicatorLarge);
                            oilTemp.speedTo((float) (oil_pressure * 0.145));


//                            } else if (getVehicleType() == VGM1 || getVehicleType() == VGM2 || getVehicleType() == VRAM) { //Gauge1
//                                TextView oilText = getView().findViewById(R.id.title4);
//                                oilText.setText("Oil \nPressure");
//                                float oil_pressure = variables.getInt("oil_pressur");
//                                ImageSpeedometer imageSpeedometer1 = getView().findViewById(R.id.speedGauge1);
//                                imageSpeedometer1.speedTo((float) ((egt * 1.8) + 32));
//
//                                //Gauge2
//                                ImageSpeedometer imageSpeedometer2 = getView().findViewById(R.id.speedGauge2);
//                                if (boost > 5) {
//                                    imageSpeedometer2.speedTo((float) (boost * 0.1450377));
//                                } else {
//                                    imageSpeedometer2.speedTo(0);
//                                }
//
//                                //Gauge3
//                                ImageSpeedometer imageSpeedometer3 = getView().findViewById(R.id.speedGauge3);
//                                imageSpeedometer3.speedTo(turbo);
//
//                                //Gauge4
//                                ImageSpeedometer imageSpeedometer4 = getView().findViewById(R.id.speedGauge4);
//                                imageSpeedometer4.speedTo((float) (oil_pressure * 0.145));
//
//                                //Gauge5
//                                ImageSpeedometer imageSpeedometer5 = getView().findViewById(R.id.speedGauge5);
//                                imageSpeedometer5.speedTo(fuel);
//
//                                //Gauge6
//                                ImageSpeedometer imageSpeedometer6 = getView().findViewById(R.id.speedGauge6);
//                                imageSpeedometer6.speedTo((float) ((coolant * 1.8) + 32));
//                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }

                        isProcessing = false;
                    }
                },
                new Response.ErrorListener()

                {
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

    //Show Connection details
    void displayDevicecInfo() {
        if (isConnected) {
            new SweetAlertDialog(Objects.requireNonNull(getActivity()), SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Connected")
                    .setContentText("You are connected to " + device)
                    .setConfirmText("ok")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            // reuse previous dialog instance
                            sDialog.dismiss();
                        }
                    })
                    .show();
        } else {
        }
    }
}
