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
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.anastr.speedviewlib.ImageSpeedometer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class LiveDataDigitalFragment2 extends Fragment {

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
    Timer timer;
    RequestQueue queue;
    WifiManager wifi;
    public static final String TAG = "GDP Tuning";
    final String coolantVar = "coolant";
    final String oilPressureVar = "oil_pressur";
    final String oilTempVar = "oil_temp";
    final String injectionFuelRateVar = "fule";
    final String injectionTimingVar = "timing";
    final String boostVar = "boost";
    final String turboVar = "turbo";
    final String frpVar = "frp";
    final String EGTVar = "egt";
    ImageSpeedometer oilPressure_Temp, EGT, turboGauge, FRP, coolantTemp, boostGauge, injectionTiming, fuelRate;
    float boostActual;
    float egtActual;
    float fuelActual;
    float timingActual;
    float coolantActual;
    float turboActual;
    float frpActual;
    float oilPressureActual;
    float oilTempActual;
    ImageView oil_digital;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_livedata_digital_2, container, false);
        oilPressure_Temp = mView.findViewById(R.id.digital_gauge1);
        EGT = mView.findViewById(R.id.digital_gauge2);
        turboGauge = mView.findViewById(R.id.digital_gauge3);
        FRP = mView.findViewById(R.id.digital_gauge4);
        coolantTemp = mView.findViewById(R.id.digital_gauge5);
        boostGauge = mView.findViewById(R.id.digital_gauge6);
        injectionTiming = mView.findViewById(R.id.digital_gauge7);
        fuelRate = mView.findViewById(R.id.digital_gauge8);
        oil_digital = mView.findViewById(R.id.oil_digital);


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
                            float boost = variables.getInt(boostVar);
                            float egt = variables.getInt(EGTVar);
                            float fuel = variables.getInt(injectionFuelRateVar);
                            float timing = variables.getInt((injectionTimingVar));
                            float coolant = variables.getInt(coolantVar);
                            float turbo = variables.getInt(turboVar);
                            float frp = variables.getInt(frpVar);
                            if (isMetric()) {
                                boostActual = boost;
                                egtActual = egt;
                                fuelActual = fuel;
                                timingActual = timing;
                                coolantActual = coolant;
                                turboActual = turbo;
                                frpActual = frp;
                            } else {
                                boostActual = (float) (boost * 0.1450377);
                                egtActual = (float) (egt * 1.8 + 32);
                                fuelActual = fuel;
                                timingActual = timing;
                                coolantActual = (float) (coolant * 1.8 + 32);
                                turboActual = turbo;
                                frpActual = (float) (frp * 0.1450377);
                            }
                            if (getVehicleType() == VFORD1 || getVehicleType() == VFORD2) {
                                oil_digital.setBackgroundResource(R.drawable.oil_temperature_analog);
                                float fordOilTemp = variables.getInt(oilTempVar);
                                if (isMetric()) {
                                    oilTempActual = fordOilTemp;

                                } else {
                                    oilTempActual = (float) (fordOilTemp * 1.8 + 32);

                                }
                                oilPressure_Temp.speedTo(oilTempActual);
                                if (isMetric()) {
                                    oilPressure_Temp.setUnit("°C");
                                    oilPressure_Temp.setMinSpeed(-40);
                                    oilPressure_Temp.setMaxSpeed(200);
                                } else {
                                    oilPressure_Temp.setUnit("°F");
                                    oilPressure_Temp.setMinSpeed(-40);
                                    oilPressure_Temp.setMaxSpeed(350);
                                }
                                EGT.speedTo(egtActual);
                                if (isMetric()) {
                                    EGT.setUnit("°C");
                                    EGT.setMinSpeed(0);
                                    EGT.setMaxSpeed(1000);
                                } else {
                                    EGT.setUnit("°F");
                                    EGT.setMinSpeed(0);
                                    EGT.setMaxSpeed(1800);
                                }
                                coolantTemp.speedTo(coolantActual);
                                if (isMetric()) {
                                    coolantTemp.setUnit("°C");
                                    coolantTemp.setMinSpeed(-40);
                                    coolantTemp.setMaxSpeed(150);
                                } else {
                                    coolantTemp.setUnit("°F");
                                    coolantTemp.setMinSpeed(-40);
                                    coolantTemp.setMaxSpeed(300);
                                }
                                turboGauge.speedTo(turboActual);
                                FRP.speedTo(frpActual);
                                if (isMetric()) {
                                    FRP.setUnit("MPa");
                                    FRP.setMinSpeed(0);
                                    FRP.setMaxSpeed(220);
                                } else {
                                    FRP.setUnit("psi");
                                    FRP.setMinSpeed(0);
                                    FRP.setMaxSpeed(32000);
                                }
                                boostGauge.speedTo(boostActual);
                                if (isMetric()) {
                                    boostGauge.setUnit("kPa");
                                    boostGauge.setMinSpeed(0);
                                    boostGauge.setMaxSpeed(400);
                                } else {
                                    boostGauge.setUnit("psi");
                                    boostGauge.setMinSpeed(0);
                                    boostGauge.setMaxSpeed(60);
                                }
                                injectionTiming.speedTo(timingActual);
                                injectionTiming.setUnit("°");
                                fuelRate.speedTo(fuelActual);
                                fuelRate.setUnit("mm3");


                            } else if (getVehicleType() == VGM1 || getVehicleType() == VGM2 || getVehicleType() == VRAM) {
                                float oil_pressure = variables.getInt(oilPressureVar);
                                if (isMetric()) {
                                    oilPressureActual = oil_pressure;
                                } else {
                                    oilPressureActual = (float) (oil_pressure * 0.1450377);
                                }
                                oilPressure_Temp.speedTo(oilPressureActual);
                                if (isMetric()) {
                                    oilPressure_Temp.setUnit("kPa");
                                    oilPressure_Temp.setMinSpeed(-40);
                                    oilPressure_Temp.setMaxSpeed(200);
                                } else {
                                    oilPressure_Temp.setUnit("psi");
                                    oilPressure_Temp.setMinSpeed(-40);
                                    oilPressure_Temp.setMaxSpeed(350);
                                }
                                EGT.speedTo(egtActual);
                                if (isMetric()) {
                                    EGT.setUnit("°C");
                                    EGT.setMinSpeed(0);
                                    EGT.setMaxSpeed(1000);
                                } else {
                                    EGT.setUnit("°F");
                                    EGT.setMinSpeed(0);
                                    EGT.setMaxSpeed(1800);
                                }
                                coolantTemp.speedTo(coolantActual);
                                if (isMetric()) {
                                    coolantTemp.setUnit("°C");
                                    coolantTemp.setMinSpeed(-40);
                                    coolantTemp.setMaxSpeed(150);
                                } else {
                                    coolantTemp.setUnit("°F");
                                    coolantTemp.setMinSpeed(-40);
                                    coolantTemp.setMaxSpeed(300);
                                }
                                turboGauge.speedTo(turboActual);
                                FRP.speedTo(frpActual);
                                if (isMetric()) {
                                    FRP.setUnit("MPa");
                                    FRP.setMinSpeed(0);
                                    FRP.setMaxSpeed(220);
                                } else {
                                    FRP.setUnit("psi");
                                    FRP.setMinSpeed(0);
                                    FRP.setMaxSpeed(32000);
                                }
                                boostGauge.speedTo(boostActual);
                                if (isMetric()) {
                                    boostGauge.setUnit("kPa");
                                    boostGauge.setMinSpeed(0);
                                    boostGauge.setMaxSpeed(400);
                                } else {
                                    boostGauge.setUnit("psi");
                                    boostGauge.setMinSpeed(0);
                                    boostGauge.setMaxSpeed(60);
                                }
                                injectionTiming.speedTo(timingActual);
                                injectionTiming.setUnit("°");
                                fuelRate.speedTo(fuelActual);
                                fuelRate.setUnit("mm3");

                            }

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

                            float boost = variables.getInt(boostVar);
                            float egt = variables.getInt(EGTVar);
                            float fuel = variables.getInt(injectionFuelRateVar);
                            float timing = variables.getInt((injectionTimingVar));
                            float coolant = variables.getInt(coolantVar);
                            float turbo = variables.getInt(turboVar);
                            float frp = variables.getInt(frpVar);

                            if (isMetric()) {
                                boostActual = boost;
                                egtActual = egt;
                                fuelActual = fuel;
                                timingActual = timing;
                                coolantActual = coolant;
                                turboActual = turbo;
                                frpActual = frp;
                            } else {
                                boostActual = (float) (boost * 0.1450377);
                                egtActual = (float) (egt * 1.8 + 32);
                                fuelActual = fuel;
                                timingActual = timing;
                                coolantActual = (float) (coolant * 1.8 + 32);
                                turboActual = turbo;
                                frpActual = (float) (frp * 0.1450377);
                            }
                            if (getVehicleType() == VFORD1 || getVehicleType() == VFORD2) {

                                float fordOilTemp = variables.getInt(oilTempVar);
                                if (isMetric()) {
                                    oilTempActual = fordOilTemp;
                                } else {
                                    oilTempActual = (float) (fordOilTemp * 1.8 + 32);
                                }
                                oilPressure_Temp.speedTo(oilTempActual);
                                if (isMetric()) {
                                    oilPressure_Temp.setUnit("°C");
                                } else {
                                    oilPressure_Temp.setUnit("°F");
                                }
                                EGT.speedTo(egtActual);
                                if (isMetric()) {
                                    EGT.setUnit("°C");
                                } else {
                                    EGT.setUnit("°F");
                                }
                                coolantTemp.speedTo(coolantActual);
                                if (isMetric()) {
                                    coolantTemp.setUnit("°C");
                                } else {
                                    coolantTemp.setUnit("°F");
                                }
                                turboGauge.speedTo(turboActual);
                                FRP.speedTo(frpActual);
                                if (isMetric()) {
                                    FRP.setUnit("MPa");
                                } else {
                                    FRP.setUnit("psi");
                                }
                                boostGauge.speedTo(boostActual);
                                if (isMetric()) {
                                    boostGauge.setUnit("kPa");
                                } else {
                                    boostGauge.setUnit("psi");
                                }
                                injectionTiming.speedTo(timingActual);
                                injectionTiming.setUnit("°");
                                fuelRate.speedTo(fuelActual);
                                fuelRate.setUnit("mm3");

                            } else if (getVehicleType() == VGM1 || getVehicleType() == VGM2 || getVehicleType() == VRAM) {
                                float oil_pressure = variables.getInt(oilPressureVar);
                                if (isMetric()) {
                                    oilPressureActual = oil_pressure;
                                } else {
                                    oilPressureActual = (float) (oil_pressure * 0.1450377);
                                }
                                oilPressure_Temp.speedTo(oilPressureActual);
                                if (isMetric()) {
                                    oilPressure_Temp.setUnit("kPa");
                                } else {
                                    oilPressure_Temp.setUnit("psi");
                                }
                                EGT.speedTo(egtActual);
                                if (isMetric()) {
                                    EGT.setUnit("°C");
                                } else {
                                    EGT.setUnit("°F");
                                }
                                coolantTemp.speedTo(coolantActual);
                                if (isMetric()) {
                                    coolantTemp.setUnit("°C");
                                } else {
                                    coolantTemp.setUnit("°F");
                                }
                                turboGauge.speedTo(turboActual);
                                FRP.speedTo(frpActual);
                                if (isMetric()) {
                                    FRP.setUnit("MPa");
                                } else {
                                    FRP.setUnit("psi");
                                }
                                boostGauge.speedTo(boostActual);
                                if (isMetric()) {
                                    boostGauge.setUnit("kPa");
                                } else {
                                    boostGauge.setUnit("psi");
                                }
                                injectionTiming.speedTo(timingActual);
                                injectionTiming.setUnit("°");
                                fuelRate.speedTo(fuelActual);
                                fuelRate.setUnit("mm3");

                            }

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
}
