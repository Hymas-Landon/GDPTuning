package com.gdptuning.gdptuning;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
    final String coolantVar = "coolant";
    final String oilPressureVar = "oil_pressur";
    final String oilTempVar = "oil_temp";
    final String injectionFuelRateVar = "fule";
    final String injectionTimingVar = "timing";
    final String boostVar = "boost";
    final String turboVar = "turbo";
    final String frpVar = "frp";
    final String EGTVar = "egt";
    final String APPVar = "app";
    HorizontalScrollView mScrollView1, mScrollView2, mScrollView3;
    ImageView boost_icon1, coolant_icon1, egt_icon1, injection_fuel_icon1, injection_timing_icon1,
            oil_pressure_icon1, turbo_icon1, fuel_rail_icon1, oil_temp_icon1, boost_icon2, coolant_icon2,
            egt_icon2, injection_fuel_icon2, injection_timing_icon2,
            oil_pressure_icon2, turbo_icon2, fuel_rail_icon2, oil_temp_icon2,
            boost_icon3, coolant_icon3, egt_icon3, injection_fuel_icon3, injection_timing_icon3,
            oil_pressure_icon3, turbo_icon3, fuel_rail_icon3, oil_temp_icon3, app_icon1, app_icon2, app_icon3;
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
    final private int APP = 10;
    float boostActual;
    float egtActual;
    float fuelActual;
    float timingActual;
    float coolantActual;
    float turboActual;
    float frpActual;
    float oilPressureActual;
    float oilTempActual;
    float appActual;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_livedata_digital, container, false);
        gauge1 = mView.findViewById(R.id.gauge1);
        gauge2 = mView.findViewById(R.id.gauge2);
        gauge3 = mView.findViewById(R.id.gauge3);
        mScrollView1 = mView.findViewById(R.id.horizontal_menu1);
        mScrollView2 = mView.findViewById(R.id.horizontal_menu2);
        mScrollView3 = mView.findViewById(R.id.horizontal_menu3);

        /*Icon 1*/
        boost_icon1 = mView.findViewById(R.id.boost_ic1);
        coolant_icon1 = mView.findViewById(R.id.coolant_ic1);
        egt_icon1 = mView.findViewById(R.id.egt_ic1);
        injection_fuel_icon1 = mView.findViewById(R.id.injection_fuel_ic1);
        injection_timing_icon1 = mView.findViewById(R.id.injection_timing_ic1);
        oil_pressure_icon1 = mView.findViewById(R.id.oil_pressure_ic1);
        turbo_icon1 = mView.findViewById(R.id.turbo_ic1);
        fuel_rail_icon1 = mView.findViewById(R.id.fuel_rail_ic1);
        oil_temp_icon1 = mView.findViewById(R.id.oil_temp_ic1);
        app_icon1 = mView.findViewById(R.id.app_ic1);

        /*Icon 2*/
        boost_icon2 = mView.findViewById(R.id.boost_ic2);
        coolant_icon2 = mView.findViewById(R.id.coolant_ic2);
        egt_icon2 = mView.findViewById(R.id.egt_ic2);
        injection_fuel_icon2 = mView.findViewById(R.id.injection_fuel_ic2);
        injection_timing_icon2 = mView.findViewById(R.id.injection_timing_ic2);
        oil_pressure_icon2 = mView.findViewById(R.id.oil_pressure_ic2);
        turbo_icon2 = mView.findViewById(R.id.turbo_ic2);
        fuel_rail_icon2 = mView.findViewById(R.id.fuel_rail_ic2);
        oil_temp_icon2 = mView.findViewById(R.id.oil_temp_ic2);
        app_icon2 = mView.findViewById(R.id.app_ic2);

        /*Icon 3*/
        boost_icon3 = mView.findViewById(R.id.boost_ic3);
        coolant_icon3 = mView.findViewById(R.id.coolant_ic3);
        egt_icon3 = mView.findViewById(R.id.egt_ic3);
        injection_fuel_icon3 = mView.findViewById(R.id.injection_fuel_ic3);
        injection_timing_icon3 = mView.findViewById(R.id.injection_timing_ic3);
        oil_pressure_icon3 = mView.findViewById(R.id.oil_pressure_ic3);
        turbo_icon3 = mView.findViewById(R.id.turbo_ic3);
        fuel_rail_icon3 = mView.findViewById(R.id.fuel_rail_ic3);
        oil_temp_icon3 = mView.findViewById(R.id.oil_temp_ic3);
        app_icon3 = mView.findViewById(R.id.app_ic3);
        mLinearLayout1 = mView.findViewById(R.id.linear_live_data1);
        mLinearLayout2 = mView.findViewById(R.id.linear_live_data2);
        mLinearLayout3 = mView.findViewById(R.id.linear_live_data3);
        ImageIndicator smallIndicator = new ImageIndicator(Objects.requireNonNull(getContext()), R.drawable.needle2);
        ImageIndicator largeIndicator = new ImageIndicator(Objects.requireNonNull(getContext()), R.drawable.needle1);
        gauge1.setIndicator(smallIndicator);
        gauge2.setIndicator(largeIndicator);
        gauge3.setIndicator(smallIndicator);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getVehicleType() == VFORD1 || getVehicleType() == VFORD2) {
            oil_pressure_icon1.setVisibility(View.GONE);
            oil_pressure_icon2.setVisibility(View.GONE);
            oil_pressure_icon3.setVisibility(View.GONE);
            app_icon1.setVisibility(View.GONE);
            app_icon2.setVisibility(View.GONE);
            app_icon3.setVisibility(View.GONE);

            gauge1.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ClickableViewAccessibility")
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
                            sendRequest();
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
                            sendRequest();
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
                            sendRequest();
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
                            sendRequest();
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
                            sendRequest();
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
                            sendRequest();
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
                            sendRequest();
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
                            sendRequest();
                        }
                    });
                }
            });

            gauge2.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ClickableViewAccessibility")
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
                            sendRequest();
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
                            sendRequest();
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
                            sendRequest();
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
                            sendRequest();
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
                            sendRequest();
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
                            sendRequest();
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
                            sendRequest();
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
                            sendRequest();
                        }
                    });
                }
            });

            gauge3.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ClickableViewAccessibility")
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
                            sendRequest();
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
                            sendRequest();
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
                            sendRequest();
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
                            sendRequest();
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
                            sendRequest();
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
                            sendRequest();
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
                            sendRequest();
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
                            sendRequest();
                        }
                    });
                }
            });

        }
        if (getVehicleType() == VRAM) {
            oil_pressure_icon1.setVisibility(View.GONE);
            oil_pressure_icon2.setVisibility(View.GONE);
            oil_pressure_icon3.setVisibility(View.GONE);

            gauge1.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ClickableViewAccessibility")
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
                            sendRequest();
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
                            sendRequest();
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
                            sendRequest();
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
                            sendRequest();
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
                            sendRequest();
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
                            sendRequest();
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
                            sendRequest();
                        }
                    });
                    app_icon1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View mView) {
                            SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                            SharedPreferences.Editor edit = mSharedPreferences.edit();
                            edit.putInt("gauge1", APP);
                            edit.apply();
                            mScrollView1.setVisibility(View.GONE);
                            sendRequest();
                        }
                    });
                }
            });

            gauge2.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ClickableViewAccessibility")
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
                            sendRequest();
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
                            sendRequest();
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
                            sendRequest();
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
                            sendRequest();
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
                            sendRequest();
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
                            sendRequest();
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
                            sendRequest();
                        }
                    });
                    app_icon2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View mView) {
                            SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                            SharedPreferences.Editor edit = mSharedPreferences.edit();
                            edit.putInt("gauge2", APP);
                            edit.apply();
                            mScrollView2.setVisibility(View.GONE);
                            sendRequest();
                        }
                    });
                }
            });

            gauge3.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ClickableViewAccessibility")
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
                            sendRequest();
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
                            sendRequest();
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
                            sendRequest();
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
                            sendRequest();
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
                            sendRequest();
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
                            sendRequest();
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
                            sendRequest();
                        }
                    });
                    app_icon3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View mView) {
                            SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                            SharedPreferences.Editor edit = mSharedPreferences.edit();
                            edit.putInt("gauge3", APP);
                            edit.apply();
                            mScrollView3.setVisibility(View.GONE);
                            sendRequest();
                        }
                    });
                }
            });


        } else if (getVehicleType() == VGM1 || getVehicleType() == VGM2) {

            oil_temp_icon1.setVisibility(View.GONE);
            oil_temp_icon2.setVisibility(View.GONE);
            oil_temp_icon3.setVisibility(View.GONE);
            app_icon1.setVisibility(View.GONE);
            app_icon2.setVisibility(View.GONE);
            app_icon3.setVisibility(View.GONE);

            gauge1.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ClickableViewAccessibility")
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
                            sendRequest();
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
                            sendRequest();
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
                            sendRequest();
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
                            sendRequest();
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
                            sendRequest();
                        }
                    });
                }
            });


            gauge2.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ClickableViewAccessibility")
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
                            sendRequest();
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
                            sendRequest();
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
                            sendRequest();
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
                            sendRequest();
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
                            sendRequest();
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
                            sendRequest();
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
                            sendRequest();
                        }
                    });
                    oil_pressure_icon2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View mView) {
                            SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                            SharedPreferences.Editor edit = mSharedPreferences.edit();
                            edit.putInt("gauge2", OILPRESSURE);
                            edit.apply();
                            mScrollView2.setVisibility(View.GONE);
                            sendRequest();
                        }
                    });
                }
            });

            gauge3.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ClickableViewAccessibility")
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
                            sendRequest();
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
                            sendRequest();
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
                            sendRequest();
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
                            sendRequest();
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
                            sendRequest();
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
                            sendRequest();
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
                            sendRequest();
                        }
                    });
                    oil_pressure_icon3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View mView) {
                            SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                            SharedPreferences.Editor edit = mSharedPreferences.edit();
                            edit.putInt("gauge3", OILPRESSURE);
                            edit.apply();
                            mScrollView3.setVisibility(View.GONE);
                            sendRequest();
                        }
                    });
                }
            });
        }

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
                            int frp = variables.getInt(frpVar);
                            if (isMetric()) {
                                if (boost < 5)
                                    boostActual = 0;
                                else
                                    boostActual = boost;
                                egtActual = egt;
                                fuelActual = fuel;
                                timingActual = timing;
                                coolantActual = coolant;
                                turboActual = turbo;
                                frpActual = frp;
                            } else {
                                if (boost < 5)
                                    boostActual = 0;
                                else
                                    boostActual = (float) (boost * 0.1450377);
                                egtActual = (float) (egt * 1.8 + 32);
                                fuelActual = fuel;
                                timingActual = timing;
                                coolantActual = (float) (coolant * 1.8 + 32);
                                turboActual = turbo;
                                frpActual = (int) (frp * 0.1450377);
                            }
                            switch (getVehicleType()) {
                                case VFORD1:
                                case VFORD2:
                                    float fordOilTemp = variables.getInt(oilTempVar);
                                    if (isMetric()) {
                                        oilTempActual = fordOilTemp;
                                    } else {
                                        oilTempActual = (float) (fordOilTemp * 1.8 + 32);
                                    }
                                    oil_pressure_icon1.setVisibility(View.GONE);
                                    oil_pressure_icon2.setVisibility(View.GONE);
                                    oil_pressure_icon3.setVisibility(View.GONE);
                                    // Set gauge 1 value
                                    switch (getGauge1()) {
                                        case BOOST:
                                            gauge1.speedTo(boostActual);
                                            if (isMetric()) {
                                                gauge1.setMinSpeed(0);
                                                gauge1.setMaxSpeed(400);
                                                gauge1.setUnit("kPa");
                                            } else {
                                                gauge1.setMinSpeed(0);
                                                gauge1.setMaxSpeed(80);
                                                gauge1.setUnit("psi");
                                            }
                                            break;
                                        case COOLANT:
                                            if (isMetric()) {
                                                gauge1.setMinSpeed(-40);
                                                gauge1.setMaxSpeed(160);
                                                gauge1.setUnit("°C");
                                            } else {
                                                gauge1.setMinSpeed(-40);
                                                gauge1.setMaxSpeed(280);
                                                gauge1.setUnit("°F");
                                            }
                                            break;
                                        case EGT:
                                            if (isMetric()) {
                                                gauge1.setMinSpeed(0);
                                                gauge1.setMaxSpeed(1000);
                                                gauge1.setUnit("°C");
                                            } else {
                                                gauge1.setMinSpeed(0);
                                                gauge1.setMaxSpeed(2400);
                                                gauge1.setUnit("°F");
                                            }
                                            break;
                                        case OILTEMP:
                                            if (isMetric()) {
                                                gauge1.setMinSpeed(-40);
                                                gauge1.setMaxSpeed(200);
                                                gauge1.setUnit("°C");
                                            } else {
                                                gauge1.setMinSpeed(-40);
                                                gauge1.setMaxSpeed(360);
                                                gauge1.setUnit("°F");
                                            }
                                            break;
                                        case FUELRAILPRESSURE:
                                            if (isMetric()) {
                                                gauge1.setMinSpeed(0);
                                                gauge1.setMaxSpeed(220);
                                                gauge1.setUnit("MPa");
                                            } else {
                                                gauge1.setMinSpeed(0);
                                                gauge1.setMaxSpeed(32000);
                                                gauge1.setUnit("psi");
                                            }
                                            break;
                                        case TURBO:
                                            gauge1.setMinSpeed(0);
                                            gauge1.setMaxSpeed(100);
                                            gauge1.setUnit("%");
                                            break;
                                        case INJECTIONFUEL:
                                            gauge1.setMinSpeed(0);
                                            gauge1.setMaxSpeed(160);
                                            gauge1.setUnit("L/H");
                                            break;
                                        case INJECTIONTIMING:
                                            gauge1.setMinSpeed(-40);
                                            gauge1.setMaxSpeed(40);
                                            gauge1.setUnit("°");
                                            break;
                                        default:
                                            gauge1.speedTo(0);
                                            break;
                                    }
                                    switch (getGauge2()) {
                                        case BOOST:
                                            gauge2.speedTo(boostActual);
                                            if (isMetric()) {
                                                gauge2.setMinSpeed(0);
                                                gauge2.setMaxSpeed(400);
                                                gauge2.setUnit("kPa");
                                            } else {
                                                gauge2.setMinSpeed(0);
                                                gauge2.setMaxSpeed(80);
                                                gauge2.setUnit("psi");
                                            }
                                            break;
                                        case COOLANT:
                                            if (isMetric()) {
                                                gauge2.setMinSpeed(-40);
                                                gauge2.setMaxSpeed(100);
                                                gauge2.setUnit("°C");
                                            } else {
                                                gauge2.setMinSpeed(-40);
                                                gauge2.setMaxSpeed(280);
                                                gauge2.setUnit("°F");
                                            }
                                            break;
                                        case EGT:
                                            if (isMetric()) {
                                                gauge2.setMinSpeed(0);
                                                gauge2.setMaxSpeed(1000);
                                                gauge2.setUnit("°C");
                                            } else {
                                                gauge2.setMinSpeed(0);
                                                gauge2.setMaxSpeed(2400);
                                                gauge2.setUnit("°F");
                                            }
                                            break;
                                        case OILTEMP:
                                            if (isMetric()) {
                                                gauge2.setMinSpeed(-40);
                                                gauge2.setMaxSpeed(200);
                                                gauge2.setUnit("°C");
                                            } else {
                                                gauge2.setMinSpeed(-40);
                                                gauge2.setMaxSpeed(360);
                                                gauge2.setUnit("°F");
                                            }
                                            break;
                                        case FUELRAILPRESSURE:
                                            if (isMetric()) {
                                                gauge2.setMinSpeed(0);
                                                gauge2.setMaxSpeed(220);
                                                gauge2.setUnit("MPa");
                                            } else {
                                                gauge2.setMinSpeed(0);
                                                gauge2.setMaxSpeed(32000);
                                                gauge2.setUnit("psi");
                                            }
                                            break;
                                        case TURBO:
                                            gauge2.setMinSpeed(0);
                                            gauge2.setMaxSpeed(100);
                                            gauge2.setUnit("%");
                                            break;
                                        case INJECTIONFUEL:
                                            gauge2.setMinSpeed(0);
                                            gauge2.setMaxSpeed(160);
                                            gauge2.setUnit("mm3");
                                            break;
                                        case INJECTIONTIMING:
                                            gauge2.setMinSpeed(-40);
                                            gauge2.setMaxSpeed(40);
                                            gauge2.setUnit("°");
                                            break;
                                        default:
                                            gauge2.speedTo(0);
                                            break;
                                    }
                                    switch (getGauge3()) {
                                        case BOOST:
                                            gauge3.speedTo(boostActual);
                                            if (isMetric()) {
                                                gauge3.setMinSpeed(0);
                                                gauge3.setMaxSpeed(400);
                                                gauge3.setUnit("kPa");
                                            } else {
                                                gauge3.setMinSpeed(0);
                                                gauge3.setMaxSpeed(80);
                                                gauge3.setUnit("psi");
                                            }
                                            break;
                                        case COOLANT:
                                            if (isMetric()) {
                                                gauge3.setTickNumber(8);
                                                gauge3.setMinSpeed(-40);
                                                gauge3.setMaxSpeed(100);
                                                gauge3.setUnit("°C");
                                            } else {
                                                gauge3.setTickNumber(9);
                                                gauge3.setMinSpeed(-40);
                                                gauge3.setMaxSpeed(280);
                                                gauge3.setUnit("°F");
                                            }
                                            break;
                                        case EGT:
                                            if (isMetric()) {
                                                gauge3.setMinSpeed(0);
                                                gauge3.setMaxSpeed(1000);
                                                gauge3.setUnit("°C");
                                            } else {
                                                gauge3.setMinSpeed(0);
                                                gauge3.setMaxSpeed(2400);
                                                gauge3.setUnit("°F");
                                            }
                                            break;
                                        case OILTEMP:
                                            if (isMetric()) {
                                                gauge3.setMinSpeed(-40);
                                                gauge3.setMaxSpeed(200);
                                                gauge3.setUnit("°C");
                                            } else {
                                                gauge3.setMinSpeed(-40);
                                                gauge3.setMaxSpeed(360);
                                                gauge3.setUnit("°F");
                                            }
                                            break;
                                        case FUELRAILPRESSURE:
                                            if (isMetric()) {
                                                gauge3.setMinSpeed(0);
                                                gauge3.setMaxSpeed(220);
                                                gauge3.setUnit("MPa");
                                            } else {
                                                gauge3.setMinSpeed(0);
                                                gauge3.setMaxSpeed(32000);
                                                gauge3.setUnit("psi");
                                            }
                                            break;
                                        case TURBO:
                                            gauge3.setMinSpeed(0);
                                            gauge3.setMaxSpeed(100);
                                            gauge3.setUnit("%");
                                            break;
                                        case INJECTIONFUEL:
                                            gauge3.setMinSpeed(0);
                                            gauge3.setMaxSpeed(160);
                                            gauge3.setUnit("mm3");
                                            break;
                                        case INJECTIONTIMING:
                                            gauge3.setMinSpeed(-40);
                                            gauge3.setMaxSpeed(40);
                                            gauge3.setUnit("°");
                                            break;
                                        default:
                                            gauge3.speedTo(0);
                                            break;
                                    }
                                    break;
                                case VRAM:
                                    appActual = variables.getInt(APPVar);
                                    oil_pressure_icon1.setVisibility(View.GONE);
                                    oil_pressure_icon2.setVisibility(View.GONE);
                                    oil_pressure_icon3.setVisibility(View.GONE);
                                    oil_temp_icon1.setVisibility(View.GONE);
                                    oil_temp_icon2.setVisibility(View.GONE);
                                    oil_temp_icon3.setVisibility(View.GONE);
                                    // Set gauge 1 value
                                    switch (getGauge1()) {
                                        case BOOST:
                                            gauge1.speedTo(boostActual);
                                            if (isMetric()) {
                                                gauge1.setMinSpeed(0);
                                                gauge1.setMaxSpeed(400);
                                                gauge1.setUnit("kPa");
                                            } else {
                                                gauge1.setMinSpeed(0);
                                                gauge1.setMaxSpeed(80);
                                                gauge1.setUnit("psi");
                                            }
                                            break;
                                        case COOLANT:
                                            if (isMetric()) {
                                                gauge1.setMinSpeed(-40);
                                                gauge1.setMaxSpeed(100);
                                                gauge1.setUnit("°C");
                                            } else {
                                                gauge1.setMinSpeed(-40);
                                                gauge1.setMaxSpeed(280);
                                                gauge1.setUnit("°F");
                                            }
                                            break;
                                        case EGT:
                                            if (isMetric()) {
                                                gauge1.setMinSpeed(0);
                                                gauge1.setMaxSpeed(1000);
                                                gauge1.setUnit("°C");
                                            } else {
                                                gauge1.setMinSpeed(0);
                                                gauge1.setMaxSpeed(2400);
                                                gauge1.setUnit("°F");
                                            }
                                            break;
                                        case APP:
                                            gauge1.setMinSpeed(0);
                                            gauge1.setMaxSpeed(100);
                                            gauge1.setUnit("%");
                                            break;
                                        case FUELRAILPRESSURE:
                                            if (isMetric()) {
                                                gauge1.setMinSpeed(0);
                                                gauge1.setMaxSpeed(220);
                                                gauge1.setUnit("MPa");
                                            } else {
                                                gauge1.setMinSpeed(0);
                                                gauge1.setMaxSpeed(32000);
                                                gauge1.setUnit("psi");
                                            }
                                            break;
                                        case TURBO:
                                            gauge1.setMinSpeed(0);
                                            gauge1.setMaxSpeed(100);
                                            gauge1.setUnit("%");
                                            break;
                                        case INJECTIONFUEL:
                                            gauge1.setMinSpeed(0);
                                            gauge1.setMaxSpeed(160);
                                            gauge1.setUnit("L/H");
                                            break;
                                        case INJECTIONTIMING:
                                            gauge1.setMinSpeed(-40);
                                            gauge1.setMaxSpeed(40);
                                            gauge1.setUnit("°");
                                            break;
                                        default:
                                            gauge1.speedTo(0);
                                            break;
                                    }
                                    switch (getGauge2()) {
                                        case BOOST:
                                            gauge2.speedTo(boostActual);
                                            if (isMetric()) {
                                                gauge2.setMinSpeed(0);
                                                gauge2.setMaxSpeed(400);
                                                gauge2.setUnit("kPa");
                                            } else {
                                                gauge2.setMinSpeed(0);
                                                gauge2.setMaxSpeed(80);
                                                gauge2.setUnit("psi");
                                            }
                                            break;
                                        case COOLANT:
                                            if (isMetric()) {
                                                gauge2.setMinSpeed(-40);
                                                gauge2.setMaxSpeed(100);
                                                gauge2.setUnit("°C");
                                            } else {
                                                gauge2.setMinSpeed(-40);
                                                gauge2.setMaxSpeed(280);
                                                gauge2.setUnit("°F");
                                            }
                                            break;
                                        case EGT:
                                            if (isMetric()) {
                                                gauge2.setMinSpeed(0);
                                                gauge2.setMaxSpeed(1000);
                                                gauge2.setUnit("°C");
                                            } else {
                                                gauge2.setMinSpeed(0);
                                                gauge2.setMaxSpeed(2400);
                                                gauge2.setUnit("°F");
                                            }
                                            break;
                                        case APP:
                                            gauge2.setMinSpeed(0);
                                            gauge2.setMaxSpeed(100);
                                            gauge2.setUnit("%");
                                            break;
                                        case FUELRAILPRESSURE:
                                            if (isMetric()) {
                                                gauge2.setMinSpeed(0);
                                                gauge2.setMaxSpeed(220);
                                                gauge2.setUnit("MPa");
                                            } else {
                                                gauge2.setMinSpeed(0);
                                                gauge2.setMaxSpeed(32000);
                                                gauge2.setUnit("psi");
                                            }
                                            break;
                                        case TURBO:
                                            gauge2.setMinSpeed(0);
                                            gauge2.setMaxSpeed(100);
                                            gauge2.setUnit("%");
                                            break;
                                        case INJECTIONFUEL:
                                            gauge2.setMinSpeed(0);
                                            gauge2.setMaxSpeed(160);
                                            gauge2.setUnit("mm3");
                                            break;
                                        case INJECTIONTIMING:
                                            gauge2.setMinSpeed(-40);
                                            gauge2.setMaxSpeed(40);
                                            gauge2.setUnit("°");
                                            break;
                                        default:
                                            gauge2.speedTo(0);
                                            break;
                                    }
                                    switch (getGauge3()) {
                                        case BOOST:
                                            gauge3.speedTo(boostActual);
                                            if (isMetric()) {
                                                gauge3.setMinSpeed(0);
                                                gauge3.setMaxSpeed(400);
                                                gauge3.setUnit("kPa");
                                            } else {
                                                gauge3.setMinSpeed(0);
                                                gauge3.setMaxSpeed(80);
                                                gauge3.setUnit("psi");
                                            }
                                            break;
                                        case COOLANT:
                                            if (isMetric()) {
                                                gauge3.setMinSpeed(-40);
                                                gauge3.setMaxSpeed(100);
                                                gauge3.setUnit("°C");
                                            } else {
                                                gauge3.setMinSpeed(-40);
                                                gauge3.setMaxSpeed(280);
                                                gauge3.setUnit("°F");
                                            }
                                            break;
                                        case EGT:
                                            if (isMetric()) {
                                                gauge3.setMinSpeed(0);
                                                gauge3.setMaxSpeed(1000);
                                                gauge3.setUnit("°C");
                                            } else {
                                                gauge3.setMinSpeed(0);
                                                gauge3.setMaxSpeed(2400);
                                                gauge3.setUnit("°F");
                                            }
                                            break;
                                        case APP:
                                            gauge3.setMinSpeed(0);
                                            gauge3.setMaxSpeed(100);
                                            gauge3.setUnit("%");
                                            break;
                                        case FUELRAILPRESSURE:
                                            if (isMetric()) {
                                                gauge3.setMinSpeed(0);
                                                gauge3.setMaxSpeed(220);
                                                gauge3.setUnit("MPa");
                                            } else {
                                                gauge3.setMinSpeed(0);
                                                gauge3.setMaxSpeed(32000);
                                                gauge3.setUnit("psi");
                                            }
                                            break;
                                        case TURBO:
                                            gauge3.setMinSpeed(0);
                                            gauge3.setMaxSpeed(100);
                                            gauge3.setUnit("%");
                                            break;
                                        case INJECTIONFUEL:
                                            gauge3.setMinSpeed(0);
                                            gauge3.setMaxSpeed(160);
                                            gauge3.setUnit("mm3");
                                            break;
                                        case INJECTIONTIMING:
                                            gauge3.setMinSpeed(-40);
                                            gauge3.setMaxSpeed(40);
                                            gauge3.setUnit("°");
                                            break;
                                        default:
                                            gauge3.speedTo(0);
                                            break;
                                    }
                                    break;
                                case VGM1:
                                case VGM2:

                                    //Gauge1
                                    float oil_pressure = variables.getInt(oilPressureVar);
                                    if (isMetric()) {
                                        oilPressureActual = oil_pressure;
                                    } else {
                                        oilPressureActual = (float) (oil_pressure * 0.1450377);
                                    }
                                    oil_temp_icon1.setVisibility(View.GONE);
                                    oil_temp_icon2.setVisibility(View.GONE);
                                    oil_temp_icon3.setVisibility(View.GONE);
                                    // Set gauge 1 value
                                    switch (getGauge1()) {
                                        case BOOST:
                                            gauge1.speedTo(boostActual);
                                            if (isMetric()) {
                                                gauge1.setMinSpeed(0);
                                                gauge1.setMaxSpeed(400);
                                                gauge1.setUnit("kPa");
                                            } else {
                                                gauge1.setMinSpeed(0);
                                                gauge1.setMaxSpeed(80);
                                                gauge1.setUnit("psi");
                                            }
                                            break;
                                        case COOLANT:
                                            if (isMetric()) {
                                                gauge1.setMinSpeed(-40);
                                                gauge1.setMaxSpeed(100);
                                                gauge1.setUnit("°C");
                                            } else {
                                                gauge1.setMinSpeed(-40);
                                                gauge1.setMaxSpeed(280);
                                                gauge1.setUnit("°F");
                                            }
                                            break;
                                        case EGT:
                                            if (isMetric()) {
                                                gauge1.setMinSpeed(0);
                                                gauge1.setMaxSpeed(1000);
                                                gauge1.setUnit("°C");
                                            } else {
                                                gauge1.setMinSpeed(0);
                                                gauge1.setMaxSpeed(2400);
                                                gauge1.setUnit("°F");
                                            }
                                            break;
                                        case OILPRESSURE:
                                            if (isMetric()) {
                                                gauge1.setMinSpeed(0);
                                                gauge1.setMaxSpeed(1000);
                                                gauge1.setUnit("kPa");
                                            } else {
                                                gauge1.setMinSpeed(0);
                                                gauge1.setMaxSpeed(160);
                                                gauge1.setUnit("psi");
                                            }
                                            break;
                                        case FUELRAILPRESSURE:
                                            if (isMetric()) {
                                                gauge1.setMinSpeed(0);
                                                gauge1.setMaxSpeed(220);
                                                gauge1.setUnit("MPa");
                                            } else {
                                                gauge1.setMinSpeed(0);
                                                gauge1.setMaxSpeed(32000);
                                                gauge1.setUnit("psi");
                                            }
                                            break;
                                        case TURBO:
                                            gauge1.setMinSpeed(0);
                                            gauge1.setMaxSpeed(100);
                                            gauge1.setUnit("%");
                                            break;
                                        case INJECTIONFUEL:
                                            gauge1.setMinSpeed(0);
                                            gauge1.setMaxSpeed(160);
                                            gauge1.setUnit("L/H");
                                            break;
                                        case INJECTIONTIMING:
                                            gauge1.setMinSpeed(-40);
                                            gauge1.setMaxSpeed(40);
                                            gauge1.setUnit("°");
                                            break;
                                        default:
                                            gauge1.speedTo(0);
                                            break;
                                    }
                                    switch (getGauge2()) {
                                        case BOOST:
                                            gauge2.speedTo(boostActual);
                                            if (isMetric()) {
                                                gauge2.setMinSpeed(0);
                                                gauge2.setMaxSpeed(400);
                                                gauge2.setUnit("kPa");
                                            } else {
                                                gauge2.setMinSpeed(0);
                                                gauge2.setMaxSpeed(80);
                                                gauge2.setUnit("psi");
                                            }
                                            break;
                                        case COOLANT:
                                            if (isMetric()) {
                                                gauge2.setMinSpeed(-40);
                                                gauge2.setMaxSpeed(140);
                                                gauge2.setUnit("°C");
                                            } else {
                                                gauge2.setMinSpeed(-40);
                                                gauge2.setMaxSpeed(280);
                                                gauge2.setUnit("°F");
                                            }
                                            break;
                                        case EGT:
                                            if (isMetric()) {
                                                gauge2.setMinSpeed(0);
                                                gauge2.setMaxSpeed(1000);
                                                gauge2.setUnit("°C");
                                            } else {
                                                gauge2.setMinSpeed(0);
                                                gauge2.setMaxSpeed(2400);
                                                gauge2.setUnit("°F");
                                            }
                                            break;
                                        case OILPRESSURE:
                                            if (isMetric()) {
                                                gauge2.setMinSpeed(0);
                                                gauge2.setMaxSpeed(1000);
                                                gauge2.setUnit("kPa");
                                            } else {
                                                gauge2.setMinSpeed(0);
                                                gauge2.setMaxSpeed(160);
                                                gauge2.setUnit("psi");
                                            }
                                            break;
                                        case FUELRAILPRESSURE:
                                            if (isMetric()) {
                                                gauge2.setMinSpeed(0);
                                                gauge2.setMaxSpeed(220);
                                                gauge2.setUnit("MPa");
                                            } else {
                                                gauge2.setMinSpeed(0);
                                                gauge2.setMaxSpeed(32000);
                                                gauge2.setUnit("psi");
                                            }
                                            break;
                                        case TURBO:
                                            gauge2.setMinSpeed(0);
                                            gauge2.setMaxSpeed(100);
                                            gauge2.setUnit("%");
                                            break;
                                        case INJECTIONFUEL:
                                            gauge2.setMinSpeed(0);
                                            gauge2.setMaxSpeed(160);
                                            gauge2.setUnit("mm3");
                                            break;
                                        case INJECTIONTIMING:
                                            gauge2.setMinSpeed(-40);
                                            gauge2.setMaxSpeed(40);
                                            gauge2.setUnit("°");
                                            break;
                                        default:
                                            gauge2.speedTo(0);
                                            break;
                                    }
                                    switch (getGauge3()) {
                                        case BOOST:
                                            gauge3.speedTo(boostActual);
                                            if (isMetric()) {
                                                gauge3.setMinSpeed(0);
                                                gauge3.setMaxSpeed(400);
                                                gauge3.setUnit("kPa");
                                            } else {
                                                gauge3.setMinSpeed(0);
                                                gauge3.setMaxSpeed(80);
                                                gauge3.setUnit("psi");
                                            }
                                            break;
                                        case COOLANT:
                                            if (isMetric()) {
                                                gauge3.setMinSpeed(-40);
                                                gauge3.setMaxSpeed(100);
                                                gauge3.setUnit("°C");
                                            } else {
                                                gauge3.setMinSpeed(-40);
                                                gauge3.setMaxSpeed(280);
                                                gauge3.setUnit("°F");
                                            }
                                            break;
                                        case EGT:
                                            if (isMetric()) {
                                                gauge3.setMinSpeed(0);
                                                gauge3.setMaxSpeed(1000);
                                                gauge3.setUnit("°C");
                                            } else {
                                                gauge3.setMinSpeed(0);
                                                gauge3.setMaxSpeed(1400);
                                                gauge3.setUnit("°F");
                                            }
                                            break;
                                        case OILPRESSURE:
                                            if (isMetric()) {
                                                gauge3.setMinSpeed(0);
                                                gauge3.setMaxSpeed(1000);
                                                gauge3.setUnit("kPa");
                                            } else {
                                                gauge3.setMinSpeed(0);
                                                gauge3.setMaxSpeed(160);
                                                gauge3.setUnit("psi");
                                            }
                                            break;
                                        case FUELRAILPRESSURE:
                                            if (isMetric()) {
                                                gauge3.setMinSpeed(0);
                                                gauge3.setMaxSpeed(220);
                                                gauge3.setUnit("MPa");
                                            } else {
                                                gauge3.setMinSpeed(0);
                                                gauge3.setMaxSpeed(32000);
                                                gauge3.setUnit("psi");
                                            }
                                            break;
                                        case TURBO:
                                            gauge3.setMinSpeed(0);
                                            gauge3.setMaxSpeed(100);
                                            gauge3.setUnit("%");
                                            break;
                                        case INJECTIONFUEL:
                                            gauge3.setMinSpeed(0);
                                            gauge3.setMaxSpeed(160);
                                            gauge3.setUnit("mm3");
                                            break;
                                        case INJECTIONTIMING:
                                            gauge3.setMinSpeed(-40);
                                            gauge3.setMaxSpeed(40);
                                            gauge3.setUnit("°");
                                            break;
                                        default:
                                            gauge3.speedTo(0);
                                            break;
                                    }
                                    break;
                            }
                        } catch (
                                JSONException e) {
                            e.printStackTrace();
                        }
                        // display response
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isConnected = false;
                        Intent i = new Intent(getActivity(), MainActivity.class);
                        startActivity(i);
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

                            float boost = variables.getInt(boostVar);
                            float egt = variables.getInt(EGTVar);
                            float fuel = variables.getInt(injectionFuelRateVar);
                            float timing = variables.getInt((injectionTimingVar));
                            float coolant = variables.getInt(coolantVar);
                            float turbo = variables.getInt(turboVar);
                            float frp = variables.getInt(frpVar);


                            if (isMetric()) {
                                if (boost < 5)
                                    boostActual = 0;
                                else
                                    boostActual = boost;
                                egtActual = egt;
                                fuelActual = fuel;
                                timingActual = timing;
                                coolantActual = coolant;
                                turboActual = turbo;
                                frpActual = frp;
                            } else {
                                if (boost < 5)
                                    boostActual = 0;
                                else
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
                                oil_pressure_icon1.setVisibility(View.GONE);
                                oil_pressure_icon2.setVisibility(View.GONE);
                                oil_pressure_icon3.setVisibility(View.GONE);
                                app_icon1.setVisibility(View.GONE);
                                app_icon2.setVisibility(View.GONE);
                                app_icon3.setVisibility(View.GONE);
                                if (isMetric()) {
                                    oilTempActual = fordOilTemp;
                                } else {
                                    oilTempActual = (float) (fordOilTemp * 1.8 + 32);
                                }

                                // Set gauge 1 value
                                switch (getGauge1()) {
                                    case BOOST:
                                        gauge1.setTickNumber(9);
                                        gauge1.speedTo(boostActual);
                                        gauge1.setSpeedTextSize(40);
                                        break;
                                    case COOLANT:
                                        if (isMetric()) {
                                            gauge1.setTickNumber(8);
                                        } else {
                                            gauge1.setTickNumber(9);
                                        }
                                        gauge1.setTickNumber(9);
                                        gauge1.speedTo((coolantActual));
                                        gauge1.setSpeedTextSize(40);
                                        break;
                                    case EGT:
                                        if (isMetric()) {
                                            gauge1.setTickNumber(11);
                                        } else {
                                            gauge1.setTickNumber(9);
                                        }
                                        gauge1.speedTo((egtActual));
                                        gauge1.setSpeedTextSize(40);
                                        break;
                                    case OILTEMP:
                                        gauge1.setTickNumber(9);
                                        gauge1.speedTo(oilTempActual);
                                        gauge1.setSpeedTextSize(40);
                                        break;
                                    case FUELRAILPRESSURE:
                                        if (isMetric()) {
                                            gauge1.setTickNumber(6);
                                        } else {
                                            gauge1.setTickNumber(5);
                                        }
                                        gauge1.speedTo((frpActual));
                                        gauge1.setSpeedTextSize(31);
                                        break;
                                    case TURBO:
                                        gauge1.setTickNumber(11);
                                        gauge1.speedTo(turboActual);
                                        gauge1.setSpeedTextSize(40);
                                        break;
                                    case INJECTIONFUEL:
                                        gauge1.setTickNumber(9);
                                        gauge1.speedTo(fuelActual);
                                        gauge1.setSpeedTextSize(40);
                                        break;
                                    case INJECTIONTIMING:
                                        gauge1.setTickNumber(9);
                                        gauge1.speedTo(timingActual);
                                        gauge1.setSpeedTextSize(40);
                                        break;
                                    default:
                                        gauge1.speedTo(0);
                                        gauge1.setSpeedTextSize(40);
                                        break;
                                }
                                switch (getGauge2()) {
                                    case BOOST:
                                        gauge2.setTickNumber(9);
                                        gauge2.speedTo(boostActual);
                                        gauge2.setSpeedTextSize(45);
                                        break;
                                    case COOLANT:
                                        if (isMetric()) {
                                            gauge2.setTickNumber(8);
                                        } else {
                                            gauge2.setTickNumber(9);
                                        }
                                        gauge2.speedTo((coolantActual));
                                        gauge2.setSpeedTextSize(41);
                                        break;
                                    case EGT:
                                        if (isMetric()) {
                                            gauge2.setTickNumber(11);
                                        } else {
                                            gauge2.setTickNumber(9);
                                        }
                                        gauge2.speedTo((egtActual));
                                        gauge2.setSpeedTextSize(41);
                                        break;
                                    case OILTEMP:
                                        gauge2.setTickNumber(9);
                                        gauge2.speedTo(oilTempActual);
                                        gauge2.setSpeedTextSize(41);
                                        break;
                                    case FUELRAILPRESSURE:
                                        if (isMetric()) {
                                            gauge2.setTickNumber(6);
                                        } else {
                                            gauge2.setTickNumber(5);
                                        }
                                        gauge2.speedTo((frpActual));
                                        gauge2.setSpeedTextSize(34);
                                        break;
                                    case TURBO:
                                        gauge2.setTickNumber(11);
                                        gauge2.speedTo(turboActual);
                                        gauge2.setSpeedTextSize(41);
                                        break;
                                    case INJECTIONFUEL:
                                        gauge2.setTickNumber(9);
                                        gauge2.speedTo(fuelActual);
                                        gauge2.setSpeedTextSize(41);
                                        break;
                                    case INJECTIONTIMING:
                                        gauge2.setTickNumber(9);
                                        gauge2.speedTo(timingActual);
                                        gauge2.setSpeedTextSize(41);
                                        break;
                                    default:
                                        gauge2.speedTo(0);
                                        gauge2.setSpeedTextSize(41);
                                        break;
                                }
                                switch (getGauge3()) {
                                    case BOOST:
                                        gauge3.setTickNumber(9);
                                        gauge3.speedTo(boostActual);
                                        gauge3.setSpeedTextSize(40);
                                        break;
                                    case COOLANT:
                                        if (isMetric()) {
                                            gauge3.setTickNumber(8);
                                        } else {
                                            gauge3.setTickNumber(9);
                                        }
                                        gauge3.speedTo((coolantActual));
                                        gauge3.setSpeedTextSize(40);
                                        break;
                                    case EGT:
                                        if (isMetric()) {
                                            gauge3.setTickNumber(11);
                                        } else {
                                            gauge3.setTickNumber(9);
                                        }
                                        gauge3.speedTo((egtActual));
                                        gauge3.setSpeedTextSize(40);
                                        break;
                                    case OILTEMP:
                                        gauge3.setTickNumber(9);
                                        gauge3.speedTo(oilTempActual);
                                        gauge3.setSpeedTextSize(40);
                                        break;
                                    case FUELRAILPRESSURE:
                                        if (isMetric()) {
                                            gauge3.setTickNumber(6);
                                        } else {
                                            gauge3.setTickNumber(5);
                                        }
                                        gauge3.speedTo((frpActual));
                                        gauge3.setSpeedTextSize(31);
                                        break;
                                    case TURBO:
                                        gauge3.setTickNumber(11);
                                        gauge3.speedTo(turboActual);
                                        gauge3.setSpeedTextSize(40);
                                        break;
                                    case INJECTIONFUEL:
                                        gauge3.setTickNumber(9);
                                        gauge3.speedTo(fuelActual);
                                        gauge3.setSpeedTextSize(40);
                                        break;
                                    case INJECTIONTIMING:
                                        gauge3.setTickNumber(9);
                                        gauge3.speedTo(timingActual);
                                        gauge3.setSpeedTextSize(40);
                                        break;
                                    default:
                                        gauge3.speedTo(0);
                                        gauge3.setSpeedTextSize(40);
                                        break;
                                }

                            }
                            if (getVehicleType() == VRAM) {
                                oil_pressure_icon1.setVisibility(View.GONE);
                                oil_pressure_icon2.setVisibility(View.GONE);
                                oil_pressure_icon3.setVisibility(View.GONE);
                                appActual = variables.getInt(APPVar);

                                // Set gauge 1 value
                                switch (getGauge1()) {
                                    case BOOST:
                                        gauge1.setTickNumber(9);
                                        gauge1.speedTo(boostActual);
                                        gauge1.setSpeedTextSize(40);
                                        break;
                                    case COOLANT:
                                        if (isMetric()) {
                                            gauge1.setTickNumber(8);
                                        } else {
                                            gauge1.setTickNumber(9);
                                        }
                                        gauge1.speedTo((coolantActual));
                                        gauge1.setSpeedTextSize(40);
                                        break;
                                    case EGT:
                                        if (isMetric()) {
                                            gauge1.setTickNumber(11);
                                        } else {
                                            gauge1.setTickNumber(9);
                                        }
                                        gauge1.speedTo((egtActual));
                                        gauge1.setSpeedTextSize(40);
                                        break;
                                    case APP:
                                        gauge1.setTickNumber(11);
                                        gauge1.speedTo(appActual);
                                        gauge1.setSpeedTextSize(40);
                                        break;
                                    case FUELRAILPRESSURE:
                                        if (isMetric()) {
                                            gauge1.setTickNumber(6);
                                        } else {
                                            gauge1.setTickNumber(5);
                                        }
                                        gauge1.speedTo((frpActual));
                                        gauge1.setSpeedTextSize(33);
                                        break;
                                    case TURBO:
                                        gauge1.setTickNumber(11);
                                        gauge1.speedTo(turboActual);
                                        gauge1.setSpeedTextSize(40);
                                        break;
                                    case INJECTIONFUEL:
                                        gauge1.setTickNumber(9);
                                        gauge1.speedTo(fuelActual);
                                        gauge1.setSpeedTextSize(40);
                                        break;
                                    case INJECTIONTIMING:
                                        gauge1.setTickNumber(9);
                                        gauge1.speedTo(timingActual);
                                        gauge1.setSpeedTextSize(40);
                                        break;
                                    default:
                                        gauge1.speedTo(0);
                                        gauge1.setSpeedTextSize(40);
                                        break;
                                }

                                switch (getGauge2()) {
                                    case BOOST:
                                        gauge2.setTickNumber(9);
                                        gauge2.speedTo(boostActual);
                                        gauge2.setSpeedTextSize(41);
                                        break;
                                    case COOLANT:
                                        gauge2.setTickNumber(9);
                                        gauge2.speedTo((coolantActual));
                                        gauge2.setSpeedTextSize(41);
                                        break;
                                    case EGT:
                                        if (isMetric()) {
                                            gauge2.setTickNumber(11);
                                        } else {
                                            gauge2.setTickNumber(9);
                                        }
                                        gauge2.speedTo((egtActual));
                                        gauge2.setSpeedTextSize(41);
                                        break;
                                    case APP:
                                        gauge2.setTickNumber(11);
                                        gauge2.speedTo(appActual);
                                        gauge2.setSpeedTextSize(41);
                                        break;
                                    case FUELRAILPRESSURE:
                                        if (isMetric()) {
                                            gauge2.setTickNumber(6);
                                        } else {
                                            gauge2.setTickNumber(5);
                                        }
                                        gauge2.speedTo((frpActual));
                                        gauge2.setSpeedTextSize(38);
                                        break;
                                    case TURBO:
                                        gauge2.setTickNumber(11);
                                        gauge2.speedTo(turboActual);
                                        gauge2.setSpeedTextSize(41);
                                        break;
                                    case INJECTIONFUEL:
                                        gauge2.setTickNumber(9);
                                        gauge2.speedTo(fuelActual);
                                        gauge2.setSpeedTextSize(41);
                                        break;
                                    case INJECTIONTIMING:
                                        gauge2.setTickNumber(9);
                                        gauge2.speedTo(timingActual);
                                        gauge2.setSpeedTextSize(41);
                                        break;
                                    default:
                                        gauge2.speedTo(0);
                                        gauge2.setSpeedTextSize(41);
                                        break;
                                }
                                switch (getGauge3()) {
                                    case BOOST:
                                        gauge3.setTickNumber(9);
                                        gauge3.speedTo(boostActual);
                                        gauge3.setSpeedTextSize(40);
                                        break;
                                    case COOLANT:
                                        if (isMetric()) {
                                            gauge3.setTickNumber(8);
                                        } else {
                                            gauge3.setTickNumber(9);
                                        }
                                        gauge3.speedTo((coolantActual));
                                        gauge3.setSpeedTextSize(40);
                                        break;
                                    case EGT:
                                        if (isMetric()) {
                                            gauge3.setTickNumber(11);
                                        } else {
                                            gauge3.setTickNumber(9);
                                        }
                                        gauge3.speedTo((egtActual));
                                        gauge3.setSpeedTextSize(40);
                                        break;
                                    case APP:
                                        gauge1.setTickNumber(11);
                                        gauge3.speedTo(appActual);
                                        gauge3.setSpeedTextSize(40);
                                        break;
                                    case FUELRAILPRESSURE:
                                        if (isMetric()) {
                                            gauge3.setTickNumber(6);
                                        } else {
                                            gauge3.setTickNumber(5);
                                        }
                                        gauge3.speedTo((frpActual));
                                        gauge3.setSpeedTextSize(33);
                                        break;
                                    case TURBO:
                                        gauge3.setTickNumber(11);
                                        gauge3.speedTo(turboActual);
                                        gauge3.setSpeedTextSize(40);
                                        break;
                                    case INJECTIONFUEL:
                                        gauge3.setTickNumber(9);
                                        gauge3.speedTo(fuelActual);
                                        gauge3.setSpeedTextSize(40);
                                        break;
                                    case INJECTIONTIMING:
                                        gauge3.setTickNumber(9);
                                        gauge3.speedTo(timingActual);
                                        gauge3.setSpeedTextSize(40);
                                        break;
                                    default:
                                        gauge3.speedTo(0);
                                        gauge3.setSpeedTextSize(40);
                                        break;
                                }

                            } else if (getVehicleType() == VGM1 || getVehicleType() == VGM2) {
                                float oil_pressure = variables.getInt(oilPressureVar);
                                if (isMetric()) {
                                    oilPressureActual = oil_pressure;
                                } else {
                                    oilPressureActual = (float) (oil_pressure * 0.1450377);
                                }
                                // Set gauge 1 value
                                switch (getGauge1()) {
                                    case BOOST:
                                        gauge1.setTickNumber(9);
                                        gauge1.speedTo(boostActual);
                                        gauge1.setSpeedTextSize(40);
                                        break;
                                    case COOLANT:
                                        if (isMetric()) {
                                            gauge1.setTickNumber(8);
                                        } else {
                                            gauge1.setTickNumber(9);
                                        }
                                        gauge1.speedTo((coolantActual));
                                        gauge1.setSpeedTextSize(40);
                                        break;
                                    case EGT:
                                        if (isMetric()) {
                                            gauge1.setTickNumber(11);
                                        } else {
                                            gauge1.setTickNumber(9);
                                        }
                                        gauge1.speedTo((egtActual));
                                        gauge1.setSpeedTextSize(40);
                                        break;
                                    case OILPRESSURE:
                                        if (isMetric()) {
                                            gauge1.setTickNumber(11);
                                        } else {
                                            gauge1.setTickNumber(9);
                                        }
                                        gauge1.speedTo(oilPressureActual);
                                        gauge1.setSpeedTextSize(40);
                                        break;
                                    case FUELRAILPRESSURE:
                                        if (isMetric()) {
                                            gauge1.setTickNumber(6);
                                        } else {
                                            gauge1.setTickNumber(5);
                                        }
                                        gauge1.speedTo((frpActual));
                                        gauge1.setSpeedTextSize(33);
                                        break;
                                    case TURBO:
                                        gauge1.setTickNumber(11);
                                        gauge1.speedTo(turboActual);
                                        gauge1.setSpeedTextSize(40);
                                        break;
                                    case INJECTIONFUEL:
                                        gauge1.setTickNumber(9);
                                        gauge1.speedTo(fuelActual);
                                        gauge1.setSpeedTextSize(40);
                                        break;
                                    case INJECTIONTIMING:
                                        gauge1.setTickNumber(9);
                                        gauge1.speedTo(timingActual);
                                        gauge1.setSpeedTextSize(40);
                                        break;
                                    default:
                                        gauge1.speedTo(0);
                                        gauge1.setSpeedTextSize(40);
                                        break;
                                }

                                switch (getGauge2()) {
                                    case BOOST:
                                        gauge2.setTickNumber(9);
                                        gauge2.speedTo(boostActual);
                                        gauge2.setSpeedTextSize(41);
                                        break;
                                    case COOLANT:
                                        if (isMetric()) {
                                            gauge2.setTickNumber(8);
                                        } else {
                                            gauge2.setTickNumber(9);
                                        }
                                        gauge2.speedTo((coolantActual));
                                        gauge2.setSpeedTextSize(41);
                                        break;
                                    case EGT:
                                        if (isMetric()) {
                                            gauge2.setTickNumber(11);
                                        } else {
                                            gauge2.setTickNumber(9);
                                        }
                                        gauge2.speedTo((egtActual));
                                        gauge2.setSpeedTextSize(41);
                                        break;
                                    case OILPRESSURE:
                                        if (isMetric()) {
                                            gauge2.setTickNumber(11);
                                        } else {
                                            gauge2.setTickNumber(9);
                                        }
                                        gauge2.speedTo(oilPressureActual);
                                        gauge2.setSpeedTextSize(41);
                                        break;
                                    case FUELRAILPRESSURE:
                                        if (isMetric()) {
                                            gauge2.setTickNumber(6);
                                        } else {
                                            gauge2.setTickNumber(5);
                                        }
                                        gauge2.speedTo((frpActual));
                                        gauge2.setSpeedTextSize(90);
                                        break;
                                    case TURBO:
                                        gauge2.setTickNumber(11);
                                        gauge2.speedTo(turboActual);
                                        gauge2.setSpeedTextSize(41);
                                        break;
                                    case INJECTIONFUEL:
                                        gauge2.setTickNumber(9);
                                        gauge2.speedTo(fuelActual);
                                        gauge2.setSpeedTextSize(41);
                                        break;
                                    case INJECTIONTIMING:
                                        gauge2.setTickNumber(9);
                                        gauge2.speedTo(timingActual);
                                        gauge2.setSpeedTextSize(41);
                                        break;
                                    default:
                                        gauge2.speedTo(0);
                                        gauge2.setSpeedTextSize(41);
                                        break;
                                }
                                switch (getGauge3()) {
                                    case BOOST:
                                        gauge3.setTickNumber(9);
                                        gauge3.speedTo(boostActual);
                                        gauge3.setSpeedTextSize(40);
                                        break;
                                    case COOLANT:
                                        if (isMetric()) {
                                            gauge3.setTickNumber(8);
                                        } else {
                                            gauge3.setTickNumber(9);
                                        }
                                        gauge3.speedTo((coolantActual));
                                        gauge3.setSpeedTextSize(40);
                                        break;
                                    case EGT:
                                        if (isMetric()) {
                                            gauge3.setTickNumber(11);
                                        } else {
                                            gauge3.setTickNumber(9);
                                        }
                                        gauge3.speedTo((egtActual));
                                        gauge3.setSpeedTextSize(40);
                                        break;
                                    case OILPRESSURE:
                                        if (isMetric()) {
                                            gauge3.setTickNumber(11);
                                        } else {
                                            gauge3.setTickNumber(9);
                                        }
                                        gauge3.speedTo(oilPressureActual);
                                        gauge3.setSpeedTextSize(40);
                                        break;
                                    case FUELRAILPRESSURE:
                                        if (isMetric()) {
                                            gauge3.setTickNumber(6);
                                        } else {
                                            gauge3.setTickNumber(5);
                                        }
                                        gauge3.speedTo((frpActual));
                                        gauge3.setSpeedTextSize(33);
                                        break;
                                    case TURBO:
                                        gauge3.setTickNumber(11);
                                        gauge3.speedTo(turboActual);
                                        gauge3.setSpeedTextSize(40);
                                        break;
                                    case INJECTIONFUEL:
                                        gauge3.setTickNumber(9);
                                        gauge3.speedTo(fuelActual);
                                        gauge3.setSpeedTextSize(40);
                                        break;
                                    case INJECTIONTIMING:
                                        gauge3.setTickNumber(9);
                                        gauge3.speedTo(timingActual);
                                        gauge3.setSpeedTextSize(40);
                                        break;
                                    default:
                                        gauge3.speedTo(0);
                                        gauge3.setSpeedTextSize(40);
                                        break;
                                }
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
                        Intent i = new Intent(getActivity(), MainActivity.class);
                        startActivity(i);
                    }
                }
        );
        // add it to the RequestQueue
        queue.add(getRequest);
    }
}
