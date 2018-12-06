package com.gdptuning.gdptuning;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

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

public class LiveDataDigitalFragment extends Fragment {

    final int VFORD1 = 7;
    final int VFORD2 = 8;
    final int VGM1 = 9;
    final int VGM2 = 10;
    final int VRAM = 11;
    //ESP32 aREST server address
    final String url = "http://192.168.7.1";
    boolean isConnected = false;
    boolean isProcessing = false;
    String device = "GDP";
    int tuneMode = 0;
    Timer timer;
    RequestQueue queue;
    WifiManager wifi;
    ImageSpeedometer gauge1, gauge2, gauge3;
    String TAG = "LiveDataFragment:";
    final String coolantVar = "coolant";
    final String oilPressureVar = "oil_pressur";
    final String oilTempVar = "oil_temp";
    final String injectionFuelRateVar = "fule";
    final String injectionTimingVar = "timing";
    final String boostVar = "boost";
    final String turboVar = "turbo";
    final String frpVar = "frp";
    final String EGTVar = "egt";
    //    TextView digitalTextFirst1, digitalTextFirst2, digitalTextFirst3;
    HorizontalScrollView mScrollView1, mScrollView2, mScrollView3;
    ImageView boost_icon1, coolant_icon1, egt_icon1, injection_fuel_icon1, injection_timing_icon1,
            oil_pressure_icon1, turbo_icon1, fuel_rail_icon1, oil_temp_icon1, boost_icon2, coolant_icon2,
            egt_icon2, injection_fuel_icon2, injection_timing_icon2,
            oil_pressure_icon2, turbo_icon2, fuel_rail_icon2, oil_temp_icon2,
            boost_icon3, coolant_icon3, egt_icon3, injection_fuel_icon3, injection_timing_icon3,
            oil_pressure_icon3, turbo_icon3, fuel_rail_icon3, oil_temp_icon3;
    LinearLayout mLinearLayout1, mLinearLayout2, mLinearLayout3;
    final private int BOOST = 1;
    final private int EGT = 2;
    final private int OILTEMP = 3;
    final private int OILPRESSURE = 4;
    final private int TURBO = 5;
    final private int COOLANT = 6;
    final private int INJECTIONFUEL = 7;
    final private int INJECTIONTIMING = 8;
    final private int FUELRAILPRESSURE = 9;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_livedata_digital, container, false);
        gauge1 = mView.findViewById(R.id.gauge1);
        gauge2 = mView.findViewById(R.id.gauge3);
        gauge3 = mView.findViewById(R.id.gauge2);
        mScrollView1 = mView.findViewById(R.id.horizontal_menu1);
        mScrollView2 = mView.findViewById(R.id.horizontal_menu2);
        mScrollView3 = mView.findViewById(R.id.horizontal_menu3);
        boost_icon1 = mView.findViewById(R.id.boost_ic1);
        coolant_icon1 = mView.findViewById(R.id.coolant_ic1);
        egt_icon1 = mView.findViewById(R.id.egt_ic1);
        injection_fuel_icon1 = mView.findViewById(R.id.injection_fuel_ic1);
        injection_timing_icon1 = mView.findViewById(R.id.injection_timing_ic1);
        oil_pressure_icon1 = mView.findViewById(R.id.oil_pressure_ic1);
        turbo_icon1 = mView.findViewById(R.id.turbo_ic1);
        fuel_rail_icon1 = mView.findViewById(R.id.fuel_rail_ic1);
        oil_temp_icon1 = mView.findViewById(R.id.oil_temp_ic1);
        boost_icon2 = mView.findViewById(R.id.boost_ic2);
        coolant_icon2 = mView.findViewById(R.id.coolant_ic2);
        egt_icon2 = mView.findViewById(R.id.egt_ic2);
        injection_fuel_icon2 = mView.findViewById(R.id.injection_fuel_ic2);
        injection_timing_icon2 = mView.findViewById(R.id.injection_timing_ic2);
        oil_pressure_icon2 = mView.findViewById(R.id.oil_pressure_ic2);
        turbo_icon2 = mView.findViewById(R.id.turbo_ic2);
        fuel_rail_icon2 = mView.findViewById(R.id.fuel_rail_ic2);
        oil_temp_icon2 = mView.findViewById(R.id.oil_temp_ic2);
        boost_icon3 = mView.findViewById(R.id.boost_ic3);
        coolant_icon3 = mView.findViewById(R.id.coolant_ic3);
        egt_icon3 = mView.findViewById(R.id.egt_ic3);
        injection_fuel_icon3 = mView.findViewById(R.id.injection_fuel_ic3);
        injection_timing_icon3 = mView.findViewById(R.id.injection_timing_ic3);
        oil_pressure_icon3 = mView.findViewById(R.id.oil_pressure_ic3);
        turbo_icon3 = mView.findViewById(R.id.turbo_ic3);
        fuel_rail_icon3 = mView.findViewById(R.id.fuel_rail_ic3);
        oil_temp_icon3 = mView.findViewById(R.id.oil_temp_ic3);
        mLinearLayout1 = mView.findViewById(R.id.linear_live_data1);
        mLinearLayout2 = mView.findViewById(R.id.linear_live_data2);
        mLinearLayout3 = mView.findViewById(R.id.linear_live_data3);


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

    private int getGauge1() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
        return mSharedPreferences.getInt("gauge1", 1);
    }

    private int getGauge2() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
        return mSharedPreferences.getInt("gauge2", 2);
    }

    private int getGauge3() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
        return mSharedPreferences.getInt("gauge3", 3);
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
                            float boost = variables.getInt(boostVar);
                            float egt = variables.getInt(EGTVar);
                            float fuel = variables.getInt(injectionFuelRateVar);
                            float timing = variables.getInt((injectionTimingVar));
                            float coolant = variables.getInt(coolantVar);
                            float turbo = variables.getInt(turboVar);
                            float frp = variables.getInt(frpVar);

                            switch (getVehicleType()) {
                                case VFORD1:
                                case VFORD2:
                                    Log.d("BLUEBERRY", "onResponse: YOU MADE IT TO FORD!!!");
                                    float fordOilTemp = variables.getInt(oilTempVar);
                                    oil_pressure_icon1.setVisibility(View.GONE);
                                    oil_pressure_icon2.setVisibility(View.GONE);
                                    oil_pressure_icon3.setVisibility(View.GONE);
                                    // Set gauge 1 value
                                    switch (getGauge1()) {
                                        case BOOST:
                                            gauge1.speedTo((float) (boost * 0.1450377));
                                            gauge1.setImageSpeedometer(R.drawable.boost_standard);
                                            gauge1.setEndDegree(377);
                                            gauge1.setMaxSpeed(60);
                                            gauge1.setStartDegree(163);
                                            gauge1.setMinSpeed(0);
                                            break;
                                        case COOLANT:
                                            gauge1.speedTo((float) (coolant * 1.8 + 32));
                                            gauge1.setImageSpeedometer(R.drawable.coolant_temperature_standard);
                                            gauge1.setEndDegree(390);
                                            gauge1.setMaxSpeed(280);
                                            gauge1.setStartDegree(180);
                                            gauge1.setMinSpeed(-40);
                                            break;
                                        case EGT:
                                            gauge1.speedTo((float) (egt * 1.8 + 32));
                                            gauge1.setImageSpeedometer(R.drawable.egt_temperature_standard);
                                            gauge1.setEndDegree(390);
                                            gauge1.setMaxSpeed(2000);
                                            gauge1.setStartDegree(151);
                                            gauge1.setMinSpeed(0);
                                            break;
                                        case OILTEMP:
                                            gauge1.speedTo((float) (fordOilTemp * 1.8 + 32));
                                            gauge1.setImageSpeedometer(R.drawable.oil_temperature_standard);
                                            gauge1.setEndDegree(360);
                                            gauge1.setMaxSpeed(350);
                                            gauge1.setStartDegree(188);
                                            gauge1.setMinSpeed(-40);
                                            break;
                                        case FUELRAILPRESSURE:
                                            gauge1.speedTo((float) (frp * 145.0377));
                                            gauge1.setImageSpeedometer(R.drawable.fuel_rail_pressure_standard);
                                            gauge1.setEndDegree(390);
                                            gauge1.setMaxSpeed(32000);
                                            gauge1.setStartDegree(150);
                                            gauge1.setMinSpeed(0);
                                            break;
                                        case TURBO:
                                            gauge1.speedTo(turbo);
                                            gauge1.setImageSpeedometer(R.drawable.turbo_vanes_standard);
                                            gauge1.setEndDegree(370);
                                            gauge1.setMaxSpeed(100);
                                            gauge1.setStartDegree(170);
                                            gauge1.setMinSpeed(0);
                                            break;
                                        case INJECTIONFUEL:
                                            gauge1.speedTo(fuel);
                                            gauge1.setImageSpeedometer(R.drawable.injection_fuel_rate_standard);
                                            gauge1.setEndDegree(374);
                                            gauge1.setMaxSpeed(150);
                                            gauge1.setStartDegree(155);
                                            gauge1.setMinSpeed(0);
                                            break;
                                        case INJECTIONTIMING:
                                            gauge1.speedTo(timing);
                                            gauge1.setImageSpeedometer(R.drawable.injection_time_standard);
                                            gauge1.setEndDegree(390);
                                            gauge1.setMaxSpeed(40);
                                            gauge1.setStartDegree(150);
                                            gauge1.setMinSpeed(-40);
                                            break;
                                        default:
                                            gauge1.speedTo(0);
                                            break;
                                    }
                                    switch (getGauge2()) {
                                        case BOOST:
                                            gauge2.speedTo((float) (boost * 0.1450377));
                                            gauge2.setImageSpeedometer(R.drawable.boost_standard);
                                            gauge2.setEndDegree(377);
                                            gauge2.setMaxSpeed(60);
                                            gauge2.setStartDegree(163);
                                            gauge2.setMinSpeed(0);
                                            break;
                                        case COOLANT:
                                            gauge2.speedTo((float) (coolant * 1.8 + 32));
                                            gauge2.setImageSpeedometer(R.drawable.coolant_temperature_standard);
                                            gauge2.setEndDegree(390);
                                            gauge2.setMaxSpeed(280);
                                            gauge2.setStartDegree(180);
                                            gauge2.setMinSpeed(-40);
                                            break;
                                        case EGT:
                                            gauge2.speedTo((float) (egt * 1.8 + 32));
                                            gauge2.setImageSpeedometer(R.drawable.egt_temperature_standard);
                                            gauge2.setEndDegree(390);
                                            gauge2.setMaxSpeed(2000);
                                            gauge2.setStartDegree(151);
                                            gauge2.setMinSpeed(0);
                                            break;
                                        case OILTEMP:
                                            gauge2.speedTo((float) (fordOilTemp * 1.8 + 32));
                                            gauge2.setImageSpeedometer(R.drawable.oil_temperature_standard);
                                            gauge2.setEndDegree(360);
                                            gauge2.setMaxSpeed(350);
                                            gauge2.setStartDegree(188);
                                            gauge2.setMinSpeed(-40);
                                            break;
                                        case FUELRAILPRESSURE:
                                            gauge2.speedTo((float) (frp * 145.0377));
                                            gauge2.setImageSpeedometer(R.drawable.fuel_rail_pressure_standard);
                                            gauge2.setEndDegree(390);
                                            gauge2.setMaxSpeed(32000);
                                            gauge2.setStartDegree(150);
                                            gauge2.setMinSpeed(0);
                                            break;
                                        case TURBO:
                                            gauge2.speedTo(turbo);
                                            gauge2.setImageSpeedometer(R.drawable.turbo_vanes_standard);
                                            gauge2.setEndDegree(370);
                                            gauge2.setMaxSpeed(100);
                                            gauge2.setStartDegree(170);
                                            gauge2.setMinSpeed(0);
                                            break;
                                        case INJECTIONFUEL:
                                            gauge2.speedTo(fuel);
                                            gauge2.setImageSpeedometer(R.drawable.injection_fuel_rate_standard);
                                            gauge2.setEndDegree(374);
                                            gauge2.setMaxSpeed(150);
                                            gauge2.setStartDegree(155);
                                            gauge2.setMinSpeed(0);
                                            break;
                                        case INJECTIONTIMING:
                                            gauge2.speedTo(timing);
                                            gauge2.setImageSpeedometer(R.drawable.injection_time_standard);
                                            gauge2.setEndDegree(390);
                                            gauge2.setMaxSpeed(40);
                                            gauge2.setStartDegree(150);
                                            gauge2.setMinSpeed(-40);
                                            break;
                                        default:
                                            gauge2.speedTo(0);
                                            break;
                                    }
                                    switch (getGauge3()) {
                                        case BOOST:
                                            gauge3.speedTo((float) (boost * 0.1450377));
                                            gauge3.setImageSpeedometer(R.drawable.boost_standard);
                                            gauge3.setEndDegree(377);
                                            gauge3.setMaxSpeed(60);
                                            gauge3.setStartDegree(163);
                                            gauge3.setMinSpeed(0);
                                            break;
                                        case COOLANT:
                                            gauge3.speedTo((float) (coolant * 1.8 + 32));
                                            gauge3.setImageSpeedometer(R.drawable.coolant_temperature_standard);
                                            gauge3.setEndDegree(390);
                                            gauge3.setMaxSpeed(280);
                                            gauge3.setStartDegree(180);
                                            gauge3.setMinSpeed(-40);
                                            break;
                                        case EGT:
                                            gauge3.speedTo((float) (egt * 1.8 + 32));
                                            gauge3.setImageSpeedometer(R.drawable.egt_temperature_standard);
                                            gauge3.setEndDegree(390);
                                            gauge3.setMaxSpeed(2000);
                                            gauge3.setStartDegree(151);
                                            gauge3.setMinSpeed(0);
                                            break;
                                        case OILTEMP:
                                            gauge3.speedTo((float) (fordOilTemp * 1.8 + 32));
                                            gauge3.setImageSpeedometer(R.drawable.oil_temperature_standard);
                                            gauge3.setEndDegree(360);
                                            gauge3.setMaxSpeed(350);
                                            gauge3.setStartDegree(188);
                                            gauge3.setMinSpeed(-40);
                                            break;
                                        case FUELRAILPRESSURE:
                                            gauge3.speedTo((float) (frp * 145.0377));
                                            gauge3.setImageSpeedometer(R.drawable.fuel_rail_pressure_standard);
                                            gauge3.setEndDegree(390);
                                            gauge3.setMaxSpeed(32000);
                                            gauge3.setStartDegree(150);
                                            gauge3.setMinSpeed(0);
                                            break;
                                        case TURBO:
                                            gauge3.speedTo(turbo);
                                            gauge3.setImageSpeedometer(R.drawable.turbo_vanes_standard);
                                            gauge3.setEndDegree(370);
                                            gauge3.setMaxSpeed(100);
                                            gauge3.setStartDegree(170);
                                            gauge3.setMinSpeed(0);
                                            break;
                                        case INJECTIONFUEL:
                                            gauge3.speedTo(fuel);
                                            gauge3.setImageSpeedometer(R.drawable.injection_fuel_rate_standard);
                                            gauge3.setEndDegree(374);
                                            gauge3.setMaxSpeed(150);
                                            gauge3.setStartDegree(155);
                                            gauge3.setMinSpeed(0);
                                            break;
                                        case INJECTIONTIMING:
                                            gauge3.speedTo(timing);
                                            gauge3.setImageSpeedometer(R.drawable.injection_time_standard);
                                            gauge3.setEndDegree(390);
                                            gauge3.setMaxSpeed(40);
                                            gauge3.setStartDegree(150);
                                            gauge3.setMinSpeed(-40);
                                            break;
                                        default:
                                            gauge3.speedTo(0);
                                            break;
                                    }
                                    break;
                                case VGM1:
                                case VGM2:
                                case VRAM:
                                    //Gauge1
                                    float oil_pressure = variables.getInt(oilPressureVar);
                                    Log.d("BLUEBERRY", "onResponse: YOU MADE IT!!!");
                                    oil_temp_icon1.setVisibility(View.GONE);
                                    oil_temp_icon2.setVisibility(View.GONE);
                                    oil_temp_icon3.setVisibility(View.GONE);
                                    // Set gauge 1 value
                                    switch (getGauge1()) {
                                        case BOOST:
                                            gauge1.speedTo((float) (boost * 0.1450377));
                                            gauge1.setImageSpeedometer(R.drawable.boost_standard);
                                            gauge1.setEndDegree(377);
                                            gauge1.setMaxSpeed(60);
                                            gauge1.setStartDegree(163);
                                            gauge1.setMinSpeed(0);
                                            break;
                                        case COOLANT:
                                            gauge1.speedTo((float) (coolant * 1.8 + 32));
                                            gauge1.setImageSpeedometer(R.drawable.coolant_temperature_standard);
                                            gauge1.setEndDegree(390);
                                            gauge1.setMaxSpeed(280);
                                            gauge1.setStartDegree(180);
                                            gauge1.setMinSpeed(-40);
                                            break;
                                        case EGT:
                                            gauge1.speedTo((float) (egt * 1.8 + 32));
                                            gauge1.setImageSpeedometer(R.drawable.egt_temperature_standard);
                                            gauge1.setEndDegree(390);
                                            gauge1.setMaxSpeed(2000);
                                            gauge1.setStartDegree(151);
                                            gauge1.setMinSpeed(0);
                                            break;
                                        case OILPRESSURE:
                                            gauge1.speedTo((float) (oil_pressure * 0.145));
                                            gauge1.setImageSpeedometer(R.drawable.oil_pressure_standard);
                                            gauge1.setEndDegree(390);
                                            gauge1.setMaxSpeed(160);
                                            gauge1.setStartDegree(150);
                                            gauge1.setMinSpeed(0);
                                            break;
                                        case FUELRAILPRESSURE:
                                            gauge1.speedTo((float) (frp * 145.0377));
                                            gauge1.setImageSpeedometer(R.drawable.fuel_rail_pressure_standard);
                                            gauge1.setEndDegree(390);
                                            gauge1.setMaxSpeed(32000);
                                            gauge1.setStartDegree(150);
                                            gauge1.setMinSpeed(0);
                                            break;
                                        case TURBO:
                                            gauge1.speedTo(turbo);
                                            gauge1.setImageSpeedometer(R.drawable.turbo_vanes_standard);
                                            gauge1.setEndDegree(370);
                                            gauge1.setMaxSpeed(100);
                                            gauge1.setStartDegree(170);
                                            gauge1.setMinSpeed(0);
                                            break;
                                        case INJECTIONFUEL:
                                            gauge1.speedTo(fuel);
                                            gauge1.setImageSpeedometer(R.drawable.injection_fuel_rate_standard);
                                            gauge1.setEndDegree(374);
                                            gauge1.setMaxSpeed(150);
                                            gauge1.setStartDegree(155);
                                            gauge1.setMinSpeed(0);
                                            break;
                                        case INJECTIONTIMING:
                                            gauge1.speedTo(timing);
                                            gauge1.setImageSpeedometer(R.drawable.injection_time_standard);
                                            gauge1.setEndDegree(390);
                                            gauge1.setMaxSpeed(40);
                                            gauge1.setStartDegree(150);
                                            gauge1.setMinSpeed(-40);
                                            break;
                                        default:
                                            gauge1.speedTo(0);
                                            break;
                                    }
                                    switch (getGauge2()) {
                                        case BOOST:
                                            gauge2.speedTo((float) (boost * 0.1450377));
                                            gauge2.setImageSpeedometer(R.drawable.boost_standard);
                                            gauge2.setEndDegree(377);
                                            gauge2.setMaxSpeed(60);
                                            gauge2.setStartDegree(163);
                                            gauge2.setMinSpeed(0);
                                            break;
                                        case COOLANT:
                                            gauge2.speedTo((float) (coolant * 1.8 + 32));
                                            gauge2.setImageSpeedometer(R.drawable.coolant_temperature_standard);
                                            gauge2.setEndDegree(390);
                                            gauge2.setMaxSpeed(280);
                                            gauge2.setStartDegree(180);
                                            gauge2.setMinSpeed(-40);
                                            break;
                                        case EGT:
                                            gauge2.speedTo((float) (egt * 1.8 + 32));
                                            gauge2.setImageSpeedometer(R.drawable.egt_temperature_standard);
                                            gauge2.setEndDegree(390);
                                            gauge2.setMaxSpeed(2000);
                                            gauge2.setStartDegree(151);
                                            gauge2.setMinSpeed(0);
                                            break;
                                        case OILPRESSURE:
                                            gauge2.speedTo((float) (oil_pressure * 0.145));
                                            gauge2.setImageSpeedometer(R.drawable.oil_pressure_standard);
                                            gauge2.setEndDegree(390);
                                            gauge2.setMaxSpeed(160);
                                            gauge2.setStartDegree(150);
                                            gauge2.setMinSpeed(0);
                                            break;
                                        case FUELRAILPRESSURE:
                                            gauge2.speedTo((float) (frp * 145.0377));
                                            gauge2.setImageSpeedometer(R.drawable.fuel_rail_pressure_standard);
                                            gauge2.setEndDegree(390);
                                            gauge2.setMaxSpeed(32000);
                                            gauge2.setStartDegree(150);
                                            gauge2.setMinSpeed(0);
                                            break;
                                        case TURBO:
                                            gauge2.speedTo(turbo);
                                            gauge2.setImageSpeedometer(R.drawable.turbo_vanes_standard);
                                            gauge2.setEndDegree(370);
                                            gauge2.setMaxSpeed(100);
                                            gauge2.setStartDegree(170);
                                            gauge2.setMinSpeed(0);
                                            break;
                                        case INJECTIONFUEL:
                                            gauge2.speedTo(fuel);
                                            gauge2.setImageSpeedometer(R.drawable.injection_fuel_rate_standard);
                                            gauge2.setEndDegree(374);
                                            gauge2.setMaxSpeed(150);
                                            gauge2.setStartDegree(155);
                                            gauge2.setMinSpeed(0);
                                            break;
                                        case INJECTIONTIMING:
                                            gauge2.speedTo(timing);
                                            gauge2.setImageSpeedometer(R.drawable.injection_time_standard);
                                            gauge2.setEndDegree(390);
                                            gauge2.setMaxSpeed(40);
                                            gauge2.setStartDegree(150);
                                            gauge2.setMinSpeed(-40);
                                            break;
                                        default:
                                            gauge2.speedTo(0);
                                            break;
                                    }
                                    switch (getGauge3()) {
                                        case BOOST:
                                            gauge3.speedTo((float) (boost * 0.1450377));
                                            gauge3.setImageSpeedometer(R.drawable.boost_standard);
                                            gauge3.setEndDegree(377);
                                            gauge3.setMaxSpeed(60);
                                            gauge3.setStartDegree(163);
                                            gauge3.setMinSpeed(0);
                                            break;
                                        case COOLANT:
                                            gauge3.speedTo((float) (coolant * 1.8 + 32));
                                            gauge3.setImageSpeedometer(R.drawable.coolant_temperature_standard);
                                            gauge3.setEndDegree(390);
                                            gauge3.setMaxSpeed(280);
                                            gauge3.setStartDegree(180);
                                            gauge3.setMinSpeed(-40);
                                            break;
                                        case EGT:
                                            gauge3.speedTo((float) (egt * 1.8 + 32));
                                            gauge3.setImageSpeedometer(R.drawable.egt_temperature_standard);
                                            gauge3.setEndDegree(390);
                                            gauge3.setMaxSpeed(2000);
                                            gauge3.setStartDegree(151);
                                            gauge3.setMinSpeed(0);
                                            break;
                                        case OILPRESSURE:
                                            gauge3.speedTo((float) (oil_pressure * 0.145));
                                            gauge3.setImageSpeedometer(R.drawable.oil_pressure_standard);
                                            gauge3.setEndDegree(390);
                                            gauge3.setMaxSpeed(160);
                                            gauge3.setStartDegree(150);
                                            gauge3.setMinSpeed(0);
                                            break;
                                        case FUELRAILPRESSURE:
                                            gauge3.speedTo((float) (frp * 145.0377));
                                            gauge3.setImageSpeedometer(R.drawable.fuel_rail_pressure_standard);
                                            gauge3.setEndDegree(390);
                                            gauge3.setMaxSpeed(32000);
                                            gauge3.setStartDegree(150);
                                            gauge3.setMinSpeed(0);
                                            break;
                                        case TURBO:
                                            gauge3.speedTo(turbo);
                                            gauge3.setImageSpeedometer(R.drawable.turbo_vanes_standard);
                                            gauge3.setEndDegree(370);
                                            gauge3.setMaxSpeed(100);
                                            gauge3.setStartDegree(170);
                                            gauge3.setMinSpeed(0);
                                            break;
                                        case INJECTIONFUEL:
                                            gauge3.speedTo(fuel);
                                            gauge3.setImageSpeedometer(R.drawable.injection_fuel_rate_standard);
                                            gauge3.setEndDegree(374);
                                            gauge3.setMaxSpeed(150);
                                            gauge3.setStartDegree(155);
                                            gauge3.setMinSpeed(0);
                                            break;
                                        case INJECTIONTIMING:
                                            gauge3.speedTo(timing);
                                            gauge3.setImageSpeedometer(R.drawable.injection_time_standard);
                                            gauge3.setEndDegree(390);
                                            gauge3.setMaxSpeed(40);
                                            gauge3.setStartDegree(150);
                                            gauge3.setMinSpeed(-40);
                                            break;
                                        default:
                                            gauge3.speedTo(0);
                                            break;
                                    }
                                    break;
                            }
                        } catch (
                                JSONException e)

                        {
                            e.printStackTrace();
                        }
                        // display response
                        Log.d("Response", response.toString());
                    }
                },
                new Response.ErrorListener()

                {
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
        final JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        isConnected = true;
                        try {
                            JSONObject variables = response.getJSONObject("variables");
                            String deviceName = response.getString("name");
                            deviceName += response.getString("id");
                            device = deviceName;

                            final float boost = variables.getInt(boostVar);
                            float egt = variables.getInt(EGTVar);
                            final float fuel = variables.getInt(injectionFuelRateVar);
                            float timing = variables.getInt((injectionTimingVar));
                            final float coolant = variables.getInt(coolantVar);
                            final float turbo = variables.getInt(turboVar);
                            float frp = variables.getInt(frpVar);

                            ImageIndicator smallIndicator = new ImageIndicator(Objects.requireNonNull(getContext()), R.drawable.needle2);
                            ImageIndicator largeIndicator = new ImageIndicator(Objects.requireNonNull(getContext()), R.drawable.needle1);
                            gauge1.setIndicator(smallIndicator);
                            gauge2.setIndicator(largeIndicator);
                            gauge3.setIndicator(smallIndicator);

                            if (getVehicleType() == VFORD1 || getVehicleType() == VFORD2) {
                                float fordOilTemp = variables.getInt(oilTempVar);
                                oil_pressure_icon1.setVisibility(View.GONE);
                                oil_pressure_icon2.setVisibility(View.GONE);
                                oil_pressure_icon3.setVisibility(View.GONE);

                                // Set gauge 1 value
                                switch (getGauge1()) {
                                    case BOOST:
                                        gauge1.speedTo((float) (boost * 0.1450377));
                                        break;
                                    case COOLANT:
                                        gauge1.speedTo((float) (coolant * 1.8 + 32));
                                        break;
                                    case EGT:
                                        gauge1.speedTo((float) (egt * 1.8 + 32));
                                        break;
                                    case OILTEMP:
                                        gauge1.speedTo((float) (fordOilTemp * 1.8 + 32));
                                        break;
                                    case FUELRAILPRESSURE:
                                        gauge1.speedTo((float) (frp * 145.0377));
                                        break;
                                    case TURBO:
                                        gauge1.speedTo(turbo);
                                        break;
                                    case INJECTIONFUEL:
                                        gauge1.speedTo(fuel);
                                        break;
                                    case INJECTIONTIMING:
                                        gauge1.speedTo(timing);
                                        break;
                                    default:
                                        gauge1.speedTo(0);
                                        break;
                                }


                                gauge1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View mView) {
                                        mScrollView1.setBackgroundColor(Color.parseColor("#B3000000"));
                                        mScrollView1.setVisibility(View.VISIBLE);
                                        boost_icon1.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View mView) {
                                                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor edit = mSharedPreferences.edit();
                                                edit.putInt("gauge1", BOOST);
                                                edit.apply();
                                                mScrollView1.setVisibility(View.GONE);
                                                gauge1.setImageSpeedometer(R.drawable.boost_standard);
                                                gauge1.setEndDegree(377);
                                                gauge1.setMaxSpeed(60);
                                                gauge1.setStartDegree(163);
                                                gauge1.setMinSpeed(0);
                                            }
                                        });
                                        coolant_icon1.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View mView) {
                                                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor edit = mSharedPreferences.edit();
                                                edit.putInt("gauge1", COOLANT);
                                                edit.apply();
                                                mScrollView1.setVisibility(View.GONE);
                                                gauge1.setImageSpeedometer(R.drawable.coolant_temperature_standard);
                                                gauge1.setEndDegree(390);
                                                gauge1.setMaxSpeed(280);
                                                gauge1.setStartDegree(180);
                                                gauge1.setMinSpeed(-40);
                                            }
                                        });
                                        egt_icon1.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View mView) {
                                                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor edit = mSharedPreferences.edit();
                                                edit.putInt("gauge1", EGT);
                                                edit.apply();
                                                mScrollView1.setVisibility(View.GONE);
                                                gauge1.setImageSpeedometer(R.drawable.egt_temperature_standard);
                                                gauge1.setEndDegree(390);
                                                gauge1.setMaxSpeed(2000);
                                                gauge1.setStartDegree(151);
                                                gauge1.setMinSpeed(0);
                                            }
                                        });
                                        injection_fuel_icon1.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View mView) {
                                                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor edit = mSharedPreferences.edit();
                                                edit.putInt("gauge1", INJECTIONFUEL);
                                                edit.apply();
                                                mScrollView1.setVisibility(View.GONE);
                                                gauge1.setImageSpeedometer(R.drawable.injection_fuel_rate_standard);
                                                gauge1.setEndDegree(374);
                                                gauge1.setMaxSpeed(150);
                                                gauge1.setStartDegree(155);
                                                gauge1.setMinSpeed(0);
                                            }
                                        });
                                        injection_timing_icon1.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View mView) {
                                                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor edit = mSharedPreferences.edit();
                                                edit.putInt("gauge1", INJECTIONTIMING);
                                                edit.apply();
                                                mScrollView1.setVisibility(View.GONE);
                                                gauge1.setImageSpeedometer(R.drawable.injection_time_standard);
                                                gauge1.setEndDegree(390);
                                                gauge1.setMaxSpeed(40);
                                                gauge1.setStartDegree(150);
                                                gauge1.setMinSpeed(-40);
                                            }
                                        });
                                        turbo_icon1.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View mView) {
                                                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor edit = mSharedPreferences.edit();
                                                edit.putInt("gauge1", TURBO);
                                                edit.apply();
                                                mScrollView1.setVisibility(View.GONE);
                                                gauge1.setImageSpeedometer(R.drawable.turbo_vanes_standard);
                                                gauge1.setEndDegree(370);
                                                gauge1.setMaxSpeed(100);
                                                gauge1.setStartDegree(170);
                                                gauge1.setMinSpeed(0);
                                            }
                                        });
                                        fuel_rail_icon1.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View mView) {
                                                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor edit = mSharedPreferences.edit();
                                                edit.putInt("gauge1", FUELRAILPRESSURE);
                                                edit.apply();
                                                mScrollView1.setVisibility(View.GONE);
                                                gauge1.setImageSpeedometer(R.drawable.fuel_rail_pressure_standard);
                                                gauge1.setEndDegree(390);
                                                gauge1.setMaxSpeed(32000);
                                                gauge1.setStartDegree(150);
                                                gauge1.setMinSpeed(0);
                                            }
                                        });
                                        oil_temp_icon1.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View mView) {
                                                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor edit = mSharedPreferences.edit();
                                                edit.putInt("gauge1", OILTEMP);
                                                edit.apply();
                                                mScrollView1.setVisibility(View.GONE);
                                                gauge1.setImageSpeedometer(R.drawable.oil_temperature_standard);
                                                gauge1.setEndDegree(360);
                                                gauge1.setMaxSpeed(350);
                                                gauge1.setStartDegree(188);
                                                gauge1.setMinSpeed(-40);
                                            }
                                        });
                                    }
                                });

                                switch (getGauge2()) {
                                    case BOOST:
                                        gauge2.speedTo((float) (boost * 0.1450377));
                                        break;
                                    case COOLANT:
                                        gauge2.speedTo((float) (coolant * 1.8 + 32));
                                        break;
                                    case EGT:
                                        gauge2.speedTo((float) (egt * 1.8 + 32));
                                        break;
                                    case OILTEMP:
                                        gauge2.speedTo((float) (fordOilTemp * 1.8 + 32));
                                        break;
                                    case FUELRAILPRESSURE:
                                        gauge2.speedTo((float) (frp * 145.0377));
                                        break;
                                    case TURBO:
                                        gauge2.speedTo(turbo);
                                        break;
                                    case INJECTIONFUEL:
                                        gauge2.speedTo(fuel);
                                        break;
                                    case INJECTIONTIMING:
                                        gauge2.speedTo(timing);
                                        break;
                                    default:
                                        gauge2.speedTo(0);
                                        break;
                                }
                                gauge2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View mView) {
                                        mScrollView2.setBackgroundColor(Color.parseColor("#B3000000"));
                                        mScrollView2.setVisibility(View.VISIBLE);
                                        boost_icon2.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View mView) {
                                                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor edit = mSharedPreferences.edit();
                                                edit.putInt("gauge2", BOOST);
                                                edit.apply();
                                                mScrollView2.setVisibility(View.GONE);
                                                gauge2.setImageSpeedometer(R.drawable.boost_standard);
                                                gauge2.setEndDegree(377);
                                                gauge2.setMaxSpeed(60);
                                                gauge2.setStartDegree(163);
                                                gauge2.setMinSpeed(0);
                                            }
                                        });
                                        coolant_icon2.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View mView) {
                                                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor edit = mSharedPreferences.edit();
                                                edit.putInt("gauge2", COOLANT);
                                                edit.apply();
                                                mScrollView2.setVisibility(View.GONE);
                                                gauge2.setImageSpeedometer(R.drawable.coolant_temperature_standard);
                                                gauge2.setEndDegree(390);
                                                gauge2.setMaxSpeed(280);
                                                gauge2.setStartDegree(180);
                                                gauge2.setMinSpeed(-40);
                                            }
                                        });
                                        egt_icon2.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View mView) {
                                                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor edit = mSharedPreferences.edit();
                                                edit.putInt("gauge2", EGT);
                                                edit.apply();
                                                mScrollView2.setVisibility(View.GONE);
                                                gauge2.setImageSpeedometer(R.drawable.egt_temperature_standard);
                                                gauge2.setEndDegree(390);
                                                gauge2.setMaxSpeed(2000);
                                                gauge2.setStartDegree(151);
                                                gauge2.setMinSpeed(0);
                                            }
                                        });
                                        injection_fuel_icon2.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View mView) {
                                                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor edit = mSharedPreferences.edit();
                                                edit.putInt("gauge2", INJECTIONFUEL);
                                                edit.apply();
                                                mScrollView2.setVisibility(View.GONE);
                                                gauge2.setImageSpeedometer(R.drawable.injection_fuel_rate_standard);
                                                gauge2.setEndDegree(374);
                                                gauge2.setMaxSpeed(150);
                                                gauge2.setStartDegree(155);
                                                gauge2.setMinSpeed(0);
                                            }
                                        });
                                        injection_timing_icon2.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View mView) {
                                                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor edit = mSharedPreferences.edit();
                                                edit.putInt("gauge2", INJECTIONTIMING);
                                                edit.apply();
                                                mScrollView2.setVisibility(View.GONE);
                                                gauge2.setImageSpeedometer(R.drawable.injection_time_standard);
                                                gauge2.setEndDegree(390);
                                                gauge2.setMaxSpeed(40);
                                                gauge2.setStartDegree(150);
                                                gauge2.setMinSpeed(-40);
                                            }
                                        });
                                        turbo_icon2.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View mView) {
                                                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor edit = mSharedPreferences.edit();
                                                edit.putInt("gauge2", TURBO);
                                                edit.apply();
                                                mScrollView2.setVisibility(View.GONE);
                                                gauge2.setImageSpeedometer(R.drawable.turbo_vanes_standard);
                                                gauge2.setEndDegree(370);
                                                gauge2.setMaxSpeed(100);
                                                gauge2.setStartDegree(170);
                                                gauge2.setMinSpeed(0);
                                            }
                                        });
                                        fuel_rail_icon2.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View mView) {
                                                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor edit = mSharedPreferences.edit();
                                                edit.putInt("gauge2", FUELRAILPRESSURE);
                                                edit.apply();
                                                mScrollView2.setVisibility(View.GONE);
                                                gauge2.setImageSpeedometer(R.drawable.fuel_rail_pressure_standard);
                                                gauge2.setEndDegree(390);
                                                gauge2.setMaxSpeed(32000);
                                                gauge2.setStartDegree(150);
                                                gauge2.setMinSpeed(0);
                                            }
                                        });
                                        oil_temp_icon2.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View mView) {
                                                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor edit = mSharedPreferences.edit();
                                                edit.putInt("gauge2", OILTEMP);
                                                edit.apply();
                                                mScrollView2.setVisibility(View.GONE);
                                                gauge2.setImageSpeedometer(R.drawable.oil_temperature_standard);
                                                gauge2.setEndDegree(360);
                                                gauge2.setMaxSpeed(350);
                                                gauge2.setStartDegree(188);
                                                gauge2.setMinSpeed(-40);
                                            }
                                        });
                                    }
                                });
                                switch (getGauge3()) {
                                    case BOOST:
                                        gauge3.speedTo((float) (boost * 0.1450377));
                                        break;
                                    case COOLANT:
                                        gauge3.speedTo((float) (coolant * 1.8 + 32));
                                        break;
                                    case EGT:
                                        gauge3.speedTo((float) (egt * 1.8 + 32));
                                        break;
                                    case OILTEMP:
                                        gauge3.speedTo((float) (fordOilTemp * 1.8 + 32));
                                        break;
                                    case FUELRAILPRESSURE:
                                        gauge3.speedTo((float) (frp * 145.0377));
                                        break;
                                    case TURBO:
                                        gauge3.speedTo(turbo);
                                        break;
                                    case INJECTIONFUEL:
                                        gauge3.speedTo(fuel);
                                        break;
                                    case INJECTIONTIMING:
                                        gauge3.speedTo(timing);
                                        break;
                                    default:
                                        gauge3.speedTo(0);
                                        break;
                                }
                                gauge3.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View mView) {
                                        mScrollView3.setBackgroundColor(Color.parseColor("#B3000000"));
                                        mScrollView3.setVisibility(View.VISIBLE);
                                        boost_icon3.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View mView) {
                                                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor edit = mSharedPreferences.edit();
                                                edit.putInt("gauge3", BOOST);
                                                edit.apply();
                                                mScrollView3.setVisibility(View.GONE);
                                                gauge3.setImageSpeedometer(R.drawable.boost_standard);
                                                gauge3.setEndDegree(377);
                                                gauge3.setMaxSpeed(60);
                                                gauge3.setStartDegree(163);
                                                gauge3.setMinSpeed(0);
                                            }
                                        });
                                        coolant_icon3.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View mView) {
                                                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor edit = mSharedPreferences.edit();
                                                edit.putInt("gauge3", COOLANT);
                                                edit.apply();
                                                mScrollView3.setVisibility(View.GONE);
                                                gauge3.setImageSpeedometer(R.drawable.coolant_temperature_standard);
                                                gauge3.setEndDegree(390);
                                                gauge3.setMaxSpeed(280);
                                                gauge3.setStartDegree(180);
                                                gauge3.setMinSpeed(-40);
                                            }
                                        });
                                        egt_icon3.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View mView) {
                                                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor edit = mSharedPreferences.edit();
                                                edit.putInt("gauge3", EGT);
                                                edit.apply();
                                                mScrollView3.setVisibility(View.GONE);
                                                gauge3.setImageSpeedometer(R.drawable.egt_temperature_standard);
                                                gauge3.setEndDegree(390);
                                                gauge3.setMaxSpeed(2000);
                                                gauge3.setStartDegree(151);
                                                gauge3.setMinSpeed(0);
                                            }
                                        });
                                        injection_fuel_icon3.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View mView) {
                                                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor edit = mSharedPreferences.edit();
                                                edit.putInt("gauge3", INJECTIONFUEL);
                                                edit.apply();
                                                mScrollView3.setVisibility(View.GONE);
                                                gauge3.setImageSpeedometer(R.drawable.injection_fuel_rate_standard);
                                                gauge3.setEndDegree(374);
                                                gauge3.setMaxSpeed(150);
                                                gauge3.setStartDegree(155);
                                                gauge3.setMinSpeed(0);
                                            }
                                        });
                                        injection_timing_icon3.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View mView) {
                                                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor edit = mSharedPreferences.edit();
                                                edit.putInt("gauge3", INJECTIONTIMING);
                                                edit.apply();
                                                mScrollView3.setVisibility(View.GONE);
                                                gauge3.setImageSpeedometer(R.drawable.injection_time_standard);
                                                gauge3.setEndDegree(390);
                                                gauge3.setMaxSpeed(40);
                                                gauge3.setStartDegree(150);
                                                gauge3.setMinSpeed(-40);
                                            }
                                        });
                                        turbo_icon3.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View mView) {
                                                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor edit = mSharedPreferences.edit();
                                                edit.putInt("gauge3", TURBO);
                                                edit.apply();
                                                mScrollView3.setVisibility(View.GONE);
                                                gauge3.setImageSpeedometer(R.drawable.turbo_vanes_standard);
                                                gauge3.setEndDegree(370);
                                                gauge3.setMaxSpeed(100);
                                                gauge3.setStartDegree(170);
                                                gauge3.setMinSpeed(0);
                                            }
                                        });
                                        fuel_rail_icon3.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View mView) {
                                                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor edit = mSharedPreferences.edit();
                                                edit.putInt("gauge3", FUELRAILPRESSURE);
                                                edit.apply();
                                                mScrollView3.setVisibility(View.GONE);
                                                gauge3.setImageSpeedometer(R.drawable.fuel_rail_pressure_standard);
                                                gauge3.setEndDegree(390);
                                                gauge3.setMaxSpeed(32000);
                                                gauge3.setStartDegree(150);
                                                gauge3.setMinSpeed(0);
                                            }
                                        });
                                        oil_temp_icon3.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View mView) {
                                                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor edit = mSharedPreferences.edit();
                                                edit.putInt("gauge3", OILTEMP);
                                                edit.apply();
                                                mScrollView3.setVisibility(View.GONE);
                                                gauge3.setImageSpeedometer(R.drawable.oil_temperature_standard);
                                                gauge3.setEndDegree(360);
                                                gauge3.setMaxSpeed(350);
                                                gauge3.setStartDegree(188);
                                                gauge3.setMinSpeed(-40);
                                            }
                                        });
                                    }
                                });


                            } else if (getVehicleType() == VGM1 || getVehicleType() == VGM2 || getVehicleType() == VRAM) { //Gauge1
                                final float oil_pressure = variables.getInt(oilPressureVar);
                                oil_temp_icon1.setVisibility(View.GONE);
                                oil_temp_icon2.setVisibility(View.GONE);
                                oil_temp_icon3.setVisibility(View.GONE);

                                // Set gauge 1 value
                                switch (getGauge1()) {
                                    case BOOST:
                                        gauge1.speedTo((float) (boost * 0.1450377));
                                        break;
                                    case COOLANT:
                                        gauge1.speedTo((float) (coolant * 1.8 + 32));
                                        break;
                                    case EGT:
                                        gauge1.speedTo((float) (egt * 1.8 + 32));
                                        break;
                                    case OILPRESSURE:
                                        gauge1.speedTo((float) (oil_pressure * 0.145));
                                        break;
                                    case FUELRAILPRESSURE:
                                        gauge1.speedTo((float) (frp * 145.0377));
                                        break;
                                    case TURBO:
                                        gauge1.speedTo(turbo);
                                        break;
                                    case INJECTIONFUEL:
                                        gauge1.speedTo(fuel);
                                        break;
                                    case INJECTIONTIMING:
                                        gauge1.speedTo(timing);
                                        break;
                                    default:
                                        gauge1.speedTo(0);
                                        break;
                                }

                                gauge1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View mView) {
                                        mScrollView1.setBackgroundColor(Color.parseColor("#B3000000"));
                                        mScrollView1.setVisibility(View.VISIBLE);
                                        boost_icon1.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View mView) {
                                                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor edit = mSharedPreferences.edit();
                                                edit.putInt("gauge1", BOOST);
                                                edit.apply();
                                                mScrollView1.setVisibility(View.GONE);
                                                gauge1.setImageSpeedometer(R.drawable.boost_standard);
                                                gauge1.setEndDegree(377);
                                                gauge1.setMaxSpeed(60);
                                                gauge1.setStartDegree(163);
                                                gauge1.setMinSpeed(0);
                                            }
                                        });
                                        coolant_icon1.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View mView) {
                                                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor edit = mSharedPreferences.edit();
                                                edit.putInt("gauge1", COOLANT);
                                                edit.apply();
                                                mScrollView1.setVisibility(View.GONE);
                                                gauge1.setImageSpeedometer(R.drawable.coolant_temperature_standard);
                                                gauge1.setEndDegree(390);
                                                gauge1.setMaxSpeed(280);
                                                gauge1.setStartDegree(180);
                                                gauge1.setMinSpeed(-40);
                                            }
                                        });
                                        egt_icon1.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View mView) {
                                                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor edit = mSharedPreferences.edit();
                                                edit.putInt("gauge1", EGT);
                                                edit.apply();
                                                mScrollView1.setVisibility(View.GONE);
                                                gauge1.setImageSpeedometer(R.drawable.egt_temperature_standard);
                                                gauge1.setEndDegree(390);
                                                gauge1.setMaxSpeed(2000);
                                                gauge1.setStartDegree(151);
                                                gauge1.setMinSpeed(0);
                                            }
                                        });
                                        injection_fuel_icon1.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View mView) {
                                                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor edit = mSharedPreferences.edit();
                                                edit.putInt("gauge1", INJECTIONFUEL);
                                                edit.apply();
                                                mScrollView1.setVisibility(View.GONE);
                                                gauge1.setImageSpeedometer(R.drawable.injection_fuel_rate_standard);
                                                gauge1.setEndDegree(374);
                                                gauge1.setMaxSpeed(150);
                                                gauge1.setStartDegree(155);
                                                gauge1.setMinSpeed(0);
                                            }
                                        });
                                        injection_timing_icon1.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View mView) {
                                                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor edit = mSharedPreferences.edit();
                                                edit.putInt("gauge1", INJECTIONTIMING);
                                                edit.apply();
                                                mScrollView1.setVisibility(View.GONE);
                                                gauge1.setImageSpeedometer(R.drawable.injection_time_standard);
                                                gauge1.setEndDegree(390);
                                                gauge1.setMaxSpeed(40);
                                                gauge1.setStartDegree(150);
                                                gauge1.setMinSpeed(-40);
                                            }
                                        });
                                        turbo_icon1.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View mView) {
                                                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor edit = mSharedPreferences.edit();
                                                edit.putInt("gauge1", TURBO);
                                                edit.apply();
                                                mScrollView1.setVisibility(View.GONE);
                                                gauge1.setImageSpeedometer(R.drawable.turbo_vanes_standard);
                                                gauge1.setEndDegree(370);
                                                gauge1.setMaxSpeed(100);
                                                gauge1.setStartDegree(170);
                                                gauge1.setMinSpeed(0);
                                            }
                                        });
                                        fuel_rail_icon1.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View mView) {
                                                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor edit = mSharedPreferences.edit();
                                                edit.putInt("gauge1", FUELRAILPRESSURE);
                                                edit.apply();
                                                mScrollView1.setVisibility(View.GONE);
                                                gauge1.setImageSpeedometer(R.drawable.fuel_rail_pressure_standard);
                                                gauge1.setEndDegree(390);
                                                gauge1.setMaxSpeed(32000);
                                                gauge1.setStartDegree(150);
                                                gauge1.setMinSpeed(0);
                                            }
                                        });
                                        oil_pressure_icon1.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View mView) {
                                                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor edit = mSharedPreferences.edit();
                                                edit.putInt("gauge1", OILPRESSURE);
                                                edit.apply();
                                                mScrollView1.setVisibility(View.GONE);
                                                gauge1.setImageSpeedometer(R.drawable.oil_pressure_standard);
                                                gauge1.setEndDegree(390);
                                                gauge1.setMaxSpeed(160);
                                                gauge1.setStartDegree(150);
                                                gauge1.setMinSpeed(0);
                                            }
                                        });
                                    }
                                });

                                switch (getGauge2()) {
                                    case BOOST:
                                        gauge2.speedTo((float) (boost * 0.1450377));
                                        break;
                                    case COOLANT:
                                        gauge2.speedTo((float) (coolant * 1.8 + 32));
                                        break;
                                    case EGT:
                                        gauge2.speedTo((float) (egt * 1.8 + 32));
                                        break;
                                    case OILPRESSURE:
                                        gauge1.speedTo((float) (oil_pressure * 0.145));
                                        break;
                                    case FUELRAILPRESSURE:
                                        gauge2.speedTo((float) (frp * 145.0377));
                                        break;
                                    case TURBO:
                                        gauge2.speedTo(turbo);
                                        break;
                                    case INJECTIONFUEL:
                                        gauge2.speedTo(fuel);
                                        break;
                                    case INJECTIONTIMING:
                                        gauge2.speedTo(timing);
                                        break;
                                    default:
                                        gauge2.speedTo(0);
                                        break;
                                }
                                gauge2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View mView) {
                                        mScrollView2.setBackgroundColor(Color.parseColor("#B3000000"));
                                        mScrollView2.setVisibility(View.VISIBLE);
                                        boost_icon2.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View mView) {
                                                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor edit = mSharedPreferences.edit();
                                                edit.putInt("gauge2", BOOST);
                                                edit.apply();
                                                mScrollView2.setVisibility(View.GONE);
                                                gauge2.setImageSpeedometer(R.drawable.boost_standard);
                                                gauge2.setEndDegree(377);
                                                gauge2.setMaxSpeed(60);
                                                gauge2.setStartDegree(163);
                                                gauge2.setMinSpeed(0);
                                            }
                                        });
                                        coolant_icon2.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View mView) {
                                                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor edit = mSharedPreferences.edit();
                                                edit.putInt("gauge2", COOLANT);
                                                edit.apply();
                                                mScrollView2.setVisibility(View.GONE);
                                                gauge2.setImageSpeedometer(R.drawable.coolant_temperature_standard);
                                                gauge2.setEndDegree(390);
                                                gauge2.setMaxSpeed(280);
                                                gauge2.setStartDegree(180);
                                                gauge2.setMinSpeed(-40);
                                            }
                                        });
                                        egt_icon2.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View mView) {
                                                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor edit = mSharedPreferences.edit();
                                                edit.putInt("gauge2", EGT);
                                                edit.apply();
                                                mScrollView2.setVisibility(View.GONE);
                                                gauge2.setImageSpeedometer(R.drawable.egt_temperature_standard);
                                                gauge2.setEndDegree(390);
                                                gauge2.setMaxSpeed(2000);
                                                gauge2.setStartDegree(151);
                                                gauge2.setMinSpeed(0);
                                            }
                                        });
                                        injection_fuel_icon2.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View mView) {
                                                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor edit = mSharedPreferences.edit();
                                                edit.putInt("gauge2", INJECTIONFUEL);
                                                edit.apply();
                                                mScrollView2.setVisibility(View.GONE);
                                                gauge2.setImageSpeedometer(R.drawable.injection_fuel_rate_standard);
                                                gauge2.setEndDegree(374);
                                                gauge2.setMaxSpeed(150);
                                                gauge2.setStartDegree(155);
                                                gauge2.setMinSpeed(0);
                                            }
                                        });
                                        injection_timing_icon2.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View mView) {
                                                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor edit = mSharedPreferences.edit();
                                                edit.putInt("gauge2", INJECTIONTIMING);
                                                edit.apply();
                                                mScrollView2.setVisibility(View.GONE);
                                                gauge2.setImageSpeedometer(R.drawable.injection_time_standard);
                                                gauge2.setEndDegree(390);
                                                gauge2.setMaxSpeed(40);
                                                gauge2.setStartDegree(150);
                                                gauge2.setMinSpeed(-40);
                                            }
                                        });
                                        turbo_icon2.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View mView) {
                                                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor edit = mSharedPreferences.edit();
                                                edit.putInt("gauge2", TURBO);
                                                edit.apply();
                                                mScrollView2.setVisibility(View.GONE);
                                                gauge2.setImageSpeedometer(R.drawable.turbo_vanes_standard);
                                                gauge2.setEndDegree(370);
                                                gauge2.setMaxSpeed(100);
                                                gauge2.setStartDegree(170);
                                                gauge2.setMinSpeed(0);
                                            }
                                        });
                                        fuel_rail_icon2.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View mView) {
                                                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor edit = mSharedPreferences.edit();
                                                edit.putInt("gauge2", FUELRAILPRESSURE);
                                                edit.apply();
                                                mScrollView2.setVisibility(View.GONE);
                                                gauge2.setImageSpeedometer(R.drawable.fuel_rail_pressure_standard);
                                                gauge2.setEndDegree(390);
                                                gauge2.setMaxSpeed(32000);
                                                gauge2.setStartDegree(150);
                                                gauge2.setMinSpeed(0);
                                            }
                                        });
                                        oil_pressure_icon2.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View mView) {
                                                Log.d(TAG, "Oil Pressure: " + oil_pressure);
                                                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor edit = mSharedPreferences.edit();
                                                edit.putInt("gauge2", OILPRESSURE);
                                                edit.apply();
                                                mScrollView2.setVisibility(View.GONE);
                                                gauge2.setImageSpeedometer(R.drawable.oil_pressure_standard);
                                                gauge2.setEndDegree(390);
                                                gauge2.setMaxSpeed(160);
                                                gauge2.setStartDegree(150);
                                                gauge2.setMinSpeed(0);
                                            }
                                        });
                                    }
                                });
                                switch (getGauge3()) {
                                    case BOOST:
                                        gauge3.speedTo((float) (boost * 0.1450377));
                                        break;
                                    case COOLANT:
                                        gauge3.speedTo((float) (coolant * 1.8 + 32));
                                        break;
                                    case EGT:
                                        gauge3.speedTo((float) (egt * 1.8 + 32));
                                        break;
                                    case OILPRESSURE:
                                        gauge3.speedTo((float) (oil_pressure * 0.145));
                                        Log.d(TAG, "Oil Pressure: " + oil_pressure * 0.145);
                                        break;
                                    case FUELRAILPRESSURE:
                                        gauge3.speedTo((float) (frp * 145.0377));
                                        break;
                                    case TURBO:
                                        gauge3.speedTo(turbo);
                                        break;
                                    case INJECTIONFUEL:
                                        gauge3.speedTo(fuel);
                                        break;
                                    case INJECTIONTIMING:
                                        gauge3.speedTo(timing);
                                        break;
                                    default:
                                        gauge3.speedTo(0);
                                        break;
                                }
                                gauge3.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View mView) {
                                        mScrollView3.setBackgroundColor(Color.parseColor("#B3000000"));
                                        mScrollView3.setVisibility(View.VISIBLE);
                                        boost_icon3.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View mView) {
                                                Log.d(TAG, "Boost: " + boost);
                                                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor edit = mSharedPreferences.edit();
                                                edit.putInt("gauge3", BOOST);
                                                edit.apply();
                                                mScrollView3.setVisibility(View.GONE);
                                                gauge3.setImageSpeedometer(R.drawable.boost_standard);
                                                gauge3.setEndDegree(377);
                                                gauge3.setMaxSpeed(60);
                                                gauge3.setMinSpeed(0);
                                                gauge3.setStartDegree(163);
                                            }
                                        });
                                        coolant_icon3.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View mView) {
                                                Log.d(TAG, "Coolant: " + coolant);
                                                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor edit = mSharedPreferences.edit();
                                                edit.putInt("gauge3", COOLANT);
                                                edit.apply();
                                                mScrollView3.setVisibility(View.GONE);
                                                gauge3.setImageSpeedometer(R.drawable.coolant_temperature_standard);
                                                gauge3.setEndDegree(390);
                                                gauge3.setMaxSpeed(280);
                                                gauge3.setStartDegree(180);
                                                gauge3.setMinSpeed(-40);
                                            }
                                        });
                                        egt_icon3.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View mView) {
                                                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor edit = mSharedPreferences.edit();
                                                edit.putInt("gauge3", EGT);
                                                edit.apply();
                                                mScrollView3.setVisibility(View.GONE);
                                                gauge3.setImageSpeedometer(R.drawable.egt_temperature_standard);
                                                gauge3.setEndDegree(390);
                                                gauge3.setMaxSpeed(2000);
                                                gauge3.setStartDegree(151);
                                                gauge3.setMinSpeed(0);
                                            }
                                        });
                                        injection_fuel_icon3.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View mView) {
                                                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor edit = mSharedPreferences.edit();
                                                edit.putInt("gauge3", INJECTIONFUEL);
                                                edit.apply();
                                                mScrollView3.setVisibility(View.GONE);
                                                gauge3.setImageSpeedometer(R.drawable.injection_fuel_rate_standard);
                                                gauge3.setEndDegree(374);
                                                gauge3.setMaxSpeed(150);
                                                gauge3.setStartDegree(155);
                                                gauge3.setMinSpeed(0);
                                            }
                                        });
                                        injection_timing_icon3.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View mView) {
                                                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor edit = mSharedPreferences.edit();
                                                edit.putInt("gauge3", INJECTIONTIMING);
                                                edit.apply();
                                                mScrollView3.setVisibility(View.GONE);
                                                gauge3.setImageSpeedometer(R.drawable.injection_time_standard);
                                                gauge3.setEndDegree(390);
                                                gauge3.setMaxSpeed(40);
                                                gauge3.setStartDegree(150);
                                                gauge3.setMinSpeed(-40);
                                            }
                                        });
                                        turbo_icon3.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View mView) {
                                                Log.d(TAG, "Turbo: " + turbo);
                                                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor edit = mSharedPreferences.edit();
                                                edit.putInt("gauge3", TURBO);
                                                edit.apply();
                                                mScrollView3.setVisibility(View.GONE);
                                                gauge3.setImageSpeedometer(R.drawable.turbo_vanes_standard);
                                                gauge3.setEndDegree(370);
                                                gauge3.setMaxSpeed(100);
                                                gauge3.setStartDegree(170);
                                                gauge3.setMinSpeed(0);
                                            }
                                        });
                                        fuel_rail_icon3.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View mView) {
                                                Log.d(TAG, "Fuel Rail: " + fuel);
                                                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor edit = mSharedPreferences.edit();
                                                edit.putInt("gauge3", FUELRAILPRESSURE);
                                                edit.apply();
                                                mScrollView3.setVisibility(View.GONE);
                                                gauge3.setImageSpeedometer(R.drawable.fuel_rail_pressure_standard);
                                                gauge3.setEndDegree(390);
                                                gauge3.setMaxSpeed(32000);
                                                gauge3.setStartDegree(150);
                                                gauge3.setMinSpeed(0);
                                            }
                                        });
                                        oil_pressure_icon3.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View mView) {
                                                Log.d(TAG, "Oil Pressure: " + oil_pressure);
                                                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor edit = mSharedPreferences.edit();
                                                edit.putInt("gauge3", OILPRESSURE);
                                                edit.apply();
                                                mScrollView3.setVisibility(View.GONE);
                                                gauge3.setImageSpeedometer(R.drawable.oil_pressure_standard);
                                                gauge3.setEndDegree(390);
                                                gauge3.setMaxSpeed(160);
                                                gauge3.setStartDegree(150);
                                                gauge3.setMinSpeed(0);
                                            }
                                        });
                                    }
                                });
                            }
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
}
