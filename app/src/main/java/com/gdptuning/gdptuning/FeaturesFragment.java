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
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.MODE_PRIVATE;


public class FeaturesFragment extends Fragment {

    private static int VFORD1 = 7;
    private static int VFORD2 = 8;
    private static int VGM1 = 9;
    private static int VGM2 = 10;
    private static int VRAM = 11;
    //ESP32 aREST server address
    final String url = "http://192.168.7.1";
    final String themeColor = "ThemeColor";
    final String vehicleSettings = "vehicle";
    final String tpmsSettings = "pressure_tpms";
    final String tireSizeSettings = "tire_size";
    final String lampCurrentSettings = "lamp_current";
    final String fogLightsSettings = "fog_lights";
    boolean isConnected = false;
    boolean isProcessing = false;
    String device = "GDP";
    RequestQueue queue;
    WifiManager wifi;
    TextView select1, select2, select3, select4, selector_words_first, selector_words_second,
            selector_words_third, selector_words_fourth, actual1, actual2, actual3, actual4;
    ImageView arrowRight1, arrowRight2, arrowRight3, arrowLeft1, arrowLeft2, arrowLeft3, arrowLeft4, arrowRight4;
    Timer timer;
    private int pressureTPMSIndex;
    private int tireIndex;
    private int tpmsNum;
    private int lampNum;
    private int fogNum;
    private int tireNum;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_features, container, false);

        //Id's
        select1 = mView.findViewById(R.id.selector1);
        select2 = mView.findViewById(R.id.selector2);
        select3 = mView.findViewById(R.id.selector3);
        select4 = mView.findViewById(R.id.selector4);
        actual1 = mView.findViewById(R.id.actual1);
        actual2 = mView.findViewById(R.id.actual2);
        actual3 = mView.findViewById(R.id.actual3);
        actual4 = mView.findViewById(R.id.actual4);
        selector_words_first = mView.findViewById(R.id.first_selector_features);
        selector_words_second = mView.findViewById(R.id.second_selector_features);
        selector_words_third = mView.findViewById(R.id.third_selector_features);
        selector_words_fourth = mView.findViewById(R.id.fourth_selector_features);
        arrowLeft1 = mView.findViewById(R.id.arrowLeft);
        arrowLeft2 = mView.findViewById(R.id.arrowLeft2);
        arrowLeft3 = mView.findViewById(R.id.arrowLeft3);
        arrowLeft4 = mView.findViewById(R.id.arrowLeft4);
        arrowRight1 = mView.findViewById(R.id.arrowRight);
        arrowRight2 = mView.findViewById(R.id.arrowRight2);
        arrowRight3 = mView.findViewById(R.id.arrowRight3);
        arrowRight4 = mView.findViewById(R.id.arrowRight4);
        return mView;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        queue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()));

        updateSettingsRequest();
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (isConnected) {
                    if (!isProcessing) {
                        Log.d("TEST2 :", "Sending request");
                        updateSettingsRequest();
                    }
                }
            }
        }, 0, 350);//put here time 1000 milliseconds=1 second


        if (getVehicleType() == VFORD1 || getVehicleType() == VFORD2) {
            //Selector 1
            selector_words_first.setText("TPMS Settings");
            final String[] pressureTPMS = new String[13];
            if (isMetric()) {
                pressureTPMS[0] = "175 kPa";
                pressureTPMS[1] = "205 kPa";
                pressureTPMS[2] = "240 kPa";
                pressureTPMS[4] = "275 kPa";
                pressureTPMS[5] = "310 kPa";
                pressureTPMS[3] = "345 kPa";
                pressureTPMS[6] = "380 kPa";
                pressureTPMS[7] = "405 kPa";
                pressureTPMS[8] = "440 kPa";
                pressureTPMS[9] = "475 kPa";
                pressureTPMS[10] = "510 kPa";
                pressureTPMS[11] = "545 kPa";
                pressureTPMS[12] = "Disable";
            } else {
                pressureTPMS[0] = "25 psi";
                pressureTPMS[1] = "30 psi";
                pressureTPMS[2] = "35 psi";
                pressureTPMS[4] = "45 psi";
                pressureTPMS[5] = "50 psi";
                pressureTPMS[3] = "40 psi";
                pressureTPMS[6] = "55 psi";
                pressureTPMS[7] = "60 psi";
                pressureTPMS[8] = "65 psi";
                pressureTPMS[9] = "70 psi";
                pressureTPMS[10] = "75 psi";
                pressureTPMS[11] = "80 psi";
                pressureTPMS[12] = "Disable";
            }
            if (getTPMS() == 25) {
                select1.setText(pressureTPMS[0]);
                pressureTPMSIndex = 0;
            } else if (getTPMS() == 30) {
                select1.setText(pressureTPMS[1]);
                pressureTPMSIndex = 1;
            } else if (getTPMS() == 35) {
                select1.setText(pressureTPMS[2]);
                pressureTPMSIndex = 2;
            } else if (getTPMS() == 40) {
                select1.setText(pressureTPMS[3]);
                pressureTPMSIndex = 3;
            } else if (getTPMS() == 45) {
                select1.setText(pressureTPMS[4]);
                pressureTPMSIndex = 4;
            } else if (getTPMS() == 50) {
                select1.setText(pressureTPMS[5]);
                pressureTPMSIndex = 5;
            } else if (getTPMS() == 55) {
                select1.setText(pressureTPMS[6]);
                pressureTPMSIndex = 6;
            } else if (getTPMS() == 60) {
                select1.setText(pressureTPMS[7]);
                pressureTPMSIndex = 7;
            } else if (getTPMS() == 65) {
                select1.setText(pressureTPMS[8]);
                pressureTPMSIndex = 8;
            } else if (getTPMS() == 70) {
                select1.setText(pressureTPMS[9]);
                pressureTPMSIndex = 9;
            } else if (getTPMS() == 75) {
                select1.setText(pressureTPMS[10]);
                pressureTPMSIndex = 10;
            } else if (getTPMS() == 80) {
                select1.setText(pressureTPMS[11]);
                pressureTPMSIndex = 11;
            } else if (getTPMS() == 0) {
                select1.setText(pressureTPMS[12]);
                pressureTPMSIndex = 12;
            }
            arrowLeft1.setOnClickListener(new View.OnClickListener() {
                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
                SharedPreferences.Editor edit = mSharedPreferences.edit();

                @Override
                public void onClick(View mView) {
                    if (pressureTPMSIndex > 0 && pressureTPMSIndex <= 12) {
                        pressureTPMSIndex = pressureTPMSIndex - 1;
                        select1.setText(pressureTPMS[pressureTPMSIndex]);
                        switch (pressureTPMSIndex) {
                            case 0:
                                edit.putInt(tpmsSettings, 25);
                                edit.apply();
                                switchTpms(10);
                                break;
                            case 1:
                                edit.putInt(tpmsSettings, 30);
                                edit.apply();
                                switchTpms(11);
                                break;
                            case 2:
                                edit.putInt(tpmsSettings, 35);
                                edit.apply();
                                switchTpms(12);
                                break;
                            case 3:
                                edit.putInt(tpmsSettings, 40);
                                edit.apply();
                                switchTpms(13);
                                break;
                            case 4:
                                edit.putInt(tpmsSettings, 45);
                                edit.apply();
                                switchTpms(14);
                                break;
                            case 5:
                                edit.putInt(tpmsSettings, 50);
                                edit.apply();
                                switchTpms(15);
                                break;
                            case 6:
                                edit.putInt(tpmsSettings, 55);
                                edit.apply();
                                switchTpms(16);
                                break;
                            case 7:
                                edit.putInt(tpmsSettings, 60);
                                edit.apply();
                                switchTpms(17);
                                break;
                            case 8:
                                edit.putInt(tpmsSettings, 65);
                                edit.apply();
                                switchTpms(18);
                                break;
                            case 9:
                                edit.putInt(tpmsSettings, 70);
                                edit.apply();
                                switchTpms(19);
                                break;
                            case 10:
                                edit.putInt(tpmsSettings, 75);
                                edit.apply();
                                switchTpms(20);
                                break;
                            case 11:
                                edit.putInt(tpmsSettings, 80);
                                edit.apply();
                                switchTpms(21);
                            case 12:
                                edit.putInt(tpmsSettings, 0);
                                edit.apply();
                                switchTpms(22);
                                break;
                        }
                    }
                }
            });
            arrowRight1.setOnClickListener(new View.OnClickListener() {
                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
                SharedPreferences.Editor edit = mSharedPreferences.edit();

                @Override
                public void onClick(View mView) {
                    if (pressureTPMSIndex >= 0 && pressureTPMSIndex < 12) {
                        pressureTPMSIndex = pressureTPMSIndex + 1;
                        select1.setText(pressureTPMS[pressureTPMSIndex]);
                        switch (pressureTPMSIndex) {
                            case 0:
                                edit.putInt(tpmsSettings, 25);
                                switchTpms(10);
                                edit.apply();
                                break;
                            case 1:
                                edit.putInt(tpmsSettings, 30);
                                switchTpms(11);
                                edit.apply();
                                break;
                            case 2:
                                edit.putInt(tpmsSettings, 35);
                                switchTpms(12);
                                edit.apply();
                                break;
                            case 3:
                                edit.putInt(tpmsSettings, 40);
                                switchTpms(13);
                                edit.apply();
                                break;
                            case 4:
                                edit.putInt(tpmsSettings, 45);
                                switchTpms(14);
                                edit.apply();
                                break;
                            case 5:
                                edit.putInt(tpmsSettings, 50);
                                switchTpms(15);
                                edit.apply();
                                break;
                            case 6:
                                edit.putInt(tpmsSettings, 55);
                                switchTpms(16);
                                edit.apply();
                                break;
                            case 7:
                                edit.putInt(tpmsSettings, 60);
                                switchTpms(17);
                                edit.apply();
                                break;
                            case 8:
                                edit.putInt(tpmsSettings, 65);
                                switchTpms(18);
                                edit.apply();
                                break;
                            case 9:
                                edit.putInt(tpmsSettings, 70);
                                switchTpms(19);
                                edit.apply();
                                break;
                            case 10:
                                edit.putInt(tpmsSettings, 75);
                                switchTpms(20);
                                edit.apply();
                                break;
                            case 11:
                                edit.putInt(tpmsSettings, 80);
                                switchTpms(21);
                                edit.apply();
                                break;
                            case 12:
                                edit.putInt(tpmsSettings, 0);
                                switchTpms(22);
                                edit.apply();
                                break;
                        }
                    }
                }
            });

            //Selector 2
            selector_words_second.setText("Turn Signal Lamp Outage Disable");
            final String[] lampOutageDisable = new String[2];
            lampOutageDisable[0] = "No";
            lampOutageDisable[1] = "Yes";
            if (!isLampCurrent()) {
                select2.setText(lampOutageDisable[0]);
            } else if (isLampCurrent()) {
                select2.setText(lampOutageDisable[1]);
            }
            arrowLeft2.setOnClickListener(new View.OnClickListener() {
                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
                SharedPreferences.Editor edit = mSharedPreferences.edit();

                @Override
                public void onClick(View mView) {
                    select2.setText(lampOutageDisable[0]);
                    edit.putBoolean(lampCurrentSettings, true);
                    switchTurnSignals(33);
                    edit.apply();
                }
            });
            arrowRight2.setOnClickListener(new View.OnClickListener() {
                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
                SharedPreferences.Editor edit = mSharedPreferences.edit();

                @Override
                public void onClick(View mView) {
                    select2.setText(lampOutageDisable[1]);
                    edit.putBoolean(lampCurrentSettings, false);
                    switchTurnSignals(32);
                    edit.apply();
                }
            });

            //Selector 3
            selector_words_third.setText("Tire Size");
            final String[] tireSize = new String[7];
            if (isMetric()) {
                tireSize[0] = "800 mm";
                tireSize[1] = "825 mm";
                tireSize[2] = "850 mm";
                tireSize[3] = "875 mm";
                tireSize[4] = "900 mm";
                tireSize[5] = "925 mm";
                tireSize[6] = "950 mm";
            } else {
                tireSize[0] = "31\"";
                tireSize[1] = "32\"";
                tireSize[2] = "33\"";
                tireSize[3] = "34\"";
                tireSize[4] = "35\"";
                tireSize[5] = "36\"";
                tireSize[6] = "37\"";
            }
            if (getTireSize() == 31) {
                select3.setText(tireSize[0]);
                tireIndex = 0;
            } else if (getTireSize() == 32) {
                select3.setText(tireSize[1]);
                tireIndex = 1;
            } else if (getTireSize() == 33) {
                select3.setText(tireSize[2]);
                tireIndex = 2;
            } else if (getTireSize() == 34) {
                select3.setText(tireSize[3]);
                tireIndex = 3;
            } else if (getTireSize() == 35) {
                select3.setText(tireSize[4]);
                tireIndex = 4;
            } else if (getTireSize() == 36) {
                select3.setText(tireSize[5]);
                tireIndex = 5;
            } else if (getTireSize() == 37) {
                select3.setText(tireSize[6]);
                tireIndex = 6;
            }
            arrowLeft3.setOnClickListener(new View.OnClickListener() {
                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
                SharedPreferences.Editor edit = mSharedPreferences.edit();

                @Override
                public void onClick(View mView) {

                    if (tireIndex > 0 && tireIndex <= 6) {
                        tireIndex = tireIndex - 1;
                        select3.setText(tireSize[tireIndex]);
                        if (tireIndex == 0) {
                            edit.putInt(tireSizeSettings, 31);
                            switchTireSize(23);
                            edit.apply();
                        } else if (tireIndex == 1) {
                            edit.putInt(tireSizeSettings, 32);
                            switchTireSize(24);
                            edit.apply();
                        } else if (tireIndex == 2) {
                            edit.putInt(tireSizeSettings, 33);
                            switchTireSize(25);
                            edit.apply();
                        } else if (tireIndex == 3) {
                            edit.putInt(tireSizeSettings, 34);
                            switchTireSize(26);
                            edit.apply();
                        } else if (tireIndex == 4) {
                            edit.putInt(tireSizeSettings, 35);
                            switchTireSize(27);
                            edit.apply();
                        } else if (tireIndex == 5) {
                            edit.putInt(tireSizeSettings, 36);
                            switchTireSize(28);
                            edit.apply();
                        } else if (tireIndex == 6) {
                            edit.putInt(tireSizeSettings, 37);
                            switchTireSize(29);
                            edit.apply();
                        }
                    }
                }
            });


            arrowRight3.setOnClickListener(new View.OnClickListener() {
                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
                SharedPreferences.Editor edit = mSharedPreferences.edit();

                @Override
                public void onClick(View mView) {

                    if (tireIndex >= 0 && tireIndex < 6) {
                        tireIndex = tireIndex + 1;
                        select3.setText(tireSize[tireIndex]);
                        if (tireIndex == 0) {
                            edit.putInt(tireSizeSettings, 31);
                            switchTireSize(23);
                            edit.apply();
                        } else if (tireIndex == 1) {
                            edit.putInt(tireSizeSettings, 32);
                            switchTireSize(24);
                            edit.apply();
                        } else if (tireIndex == 2) {
                            edit.putInt(tireSizeSettings, 33);
                            switchTireSize(25);
                            edit.apply();
                        } else if (tireIndex == 3) {
                            edit.putInt(tireSizeSettings, 34);
                            switchTireSize(26);
                            edit.apply();
                        } else if (tireIndex == 4) {
                            edit.putInt(tireSizeSettings, 35);
                            switchTireSize(27);
                            edit.apply();
                        } else if (tireIndex == 5) {
                            edit.putInt(tireSizeSettings, 36);
                            switchTireSize(28);
                            edit.apply();
                        } else if (tireIndex == 6) {
                            edit.putInt(tireSizeSettings, 37);
                            switchTireSize(29);
                            edit.apply();
                        }
                    }
                }
            });

            //Selector 4
            selector_words_fourth.setText("Fog Lights With High Beam");
            final String[] fogLights = new String[2];
            fogLights[0] = "No";
            fogLights[1] = "Yes";
            if (!isFogLights()) {
                select4.setText(fogLights[0]);
            } else if (isFogLights()) {
                select4.setText(fogLights[1]);
            }

            arrowLeft4.setOnClickListener(new View.OnClickListener() {
                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
                SharedPreferences.Editor edit = mSharedPreferences.edit();

                @Override
                public void onClick(View mView) {
                    select4.setText(fogLights[0]);
                    edit.putBoolean(fogLightsSettings, true);
                    switchFogLights(31);
                    edit.apply();
                }
            });
            arrowRight4.setOnClickListener(new View.OnClickListener() {
                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
                SharedPreferences.Editor edit = mSharedPreferences.edit();

                @Override
                public void onClick(View mView) {
                    select4.setText(fogLights[1]);
                    edit.putBoolean(fogLightsSettings, false);
                    switchFogLights(30);
                    edit.apply();
                }
            });
        } else if (getVehicleType() == VGM2) {

            arrowLeft3.setImageDrawable(null);
            arrowLeft4.setImageDrawable(null);
            arrowRight3.setImageDrawable(null);
            arrowRight4.setImageDrawable(null);

            //Selector 1
            selector_words_first.setText("TPMS Settings");
            final String[] pressureTPMS = new String[13];
            if (isMetric()) {
                pressureTPMS[0] = "175 kPa";
                pressureTPMS[1] = "205 kPa";
                pressureTPMS[2] = "240 kPa";
                pressureTPMS[4] = "275 kPa";
                pressureTPMS[5] = "310 kPa";
                pressureTPMS[3] = "345 kPa";
                pressureTPMS[6] = "380 kPa";
                pressureTPMS[7] = "405 kPa";
                pressureTPMS[8] = "440 kPa";
                pressureTPMS[9] = "475 kPa";
                pressureTPMS[10] = "510 kPa";
                pressureTPMS[11] = "545 kPa";
                pressureTPMS[12] = "Disable";
            } else {
                pressureTPMS[0] = "25 psi";
                pressureTPMS[1] = "30 psi";
                pressureTPMS[2] = "35 psi";
                pressureTPMS[4] = "45 psi";
                pressureTPMS[5] = "50 psi";
                pressureTPMS[3] = "40 psi";
                pressureTPMS[6] = "55 psi";
                pressureTPMS[7] = "60 psi";
                pressureTPMS[8] = "65 psi";
                pressureTPMS[9] = "70 psi";
                pressureTPMS[10] = "75 psi";
                pressureTPMS[11] = "80 psi";
                pressureTPMS[12] = "Disable";
            }
            if (getTPMS() == 25) {
                select1.setText(pressureTPMS[0]);
                pressureTPMSIndex = 0;
            } else if (getTPMS() == 30) {
                select1.setText(pressureTPMS[1]);
                pressureTPMSIndex = 1;
            } else if (getTPMS() == 35) {
                select1.setText(pressureTPMS[2]);
                pressureTPMSIndex = 2;
            } else if (getTPMS() == 40) {
                select1.setText(pressureTPMS[3]);
                pressureTPMSIndex = 3;
            } else if (getTPMS() == 45) {
                select1.setText(pressureTPMS[4]);
                pressureTPMSIndex = 4;
            } else if (getTPMS() == 50) {
                select1.setText(pressureTPMS[5]);
                pressureTPMSIndex = 5;
            } else if (getTPMS() == 55) {
                select1.setText(pressureTPMS[6]);
                pressureTPMSIndex = 6;
            } else if (getTPMS() == 60) {
                select1.setText(pressureTPMS[7]);
                pressureTPMSIndex = 7;
            } else if (getTPMS() == 65) {
                select1.setText(pressureTPMS[8]);
                pressureTPMSIndex = 8;
            } else if (getTPMS() == 70) {
                select1.setText(pressureTPMS[9]);
                pressureTPMSIndex = 9;
            } else if (getTPMS() == 75) {
                select1.setText(pressureTPMS[10]);
                pressureTPMSIndex = 10;
            } else if (getTPMS() == 80) {
                select1.setText(pressureTPMS[11]);
                pressureTPMSIndex = 11;
            }
            arrowLeft1.setOnClickListener(new View.OnClickListener() {
                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
                SharedPreferences.Editor edit = mSharedPreferences.edit();

                @Override
                public void onClick(View mView) {
                    if (pressureTPMSIndex > 0 && pressureTPMSIndex <= 11) {
                        pressureTPMSIndex = pressureTPMSIndex - 1;
                        select1.setText(pressureTPMS[pressureTPMSIndex]);
                        switch (pressureTPMSIndex) {
                            case 0:
                                edit.putInt(tpmsSettings, 25);
                                switchTpms(10);
                                edit.apply();
                                break;
                            case 1:
                                edit.putInt(tpmsSettings, 30);
                                switchTpms(11);
                                edit.apply();
                                break;
                            case 2:
                                edit.putInt(tpmsSettings, 35);
                                switchTpms(12);
                                edit.apply();
                                break;
                            case 3:
                                edit.putInt(tpmsSettings, 40);
                                switchTpms(13);
                                edit.apply();
                                break;
                            case 4:
                                edit.putInt(tpmsSettings, 45);
                                switchTpms(14);
                                edit.apply();
                                break;
                            case 5:
                                edit.putInt(tpmsSettings, 50);
                                switchTpms(15);
                                edit.apply();
                                break;
                            case 6:
                                edit.putInt(tpmsSettings, 55);
                                switchTpms(16);
                                edit.apply();
                                break;
                            case 7:
                                edit.putInt(tpmsSettings, 60);
                                switchTpms(17);
                                edit.apply();
                                break;
                            case 8:
                                edit.putInt(tpmsSettings, 65);
                                switchTpms(18);
                                edit.apply();
                                break;
                            case 9:
                                edit.putInt(tpmsSettings, 70);
                                switchTpms(19);
                                edit.apply();
                                break;
                            case 10:
                                edit.putInt(tpmsSettings, 75);
                                switchTpms(20);
                                edit.apply();
                                break;
                            case 11:
                                edit.putInt(tpmsSettings, 80);
                                switchTpms(21);
                                edit.apply();
                                break;
                        }
                    }
                }
            });
            arrowRight1.setOnClickListener(new View.OnClickListener() {
                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
                SharedPreferences.Editor edit = mSharedPreferences.edit();

                @Override
                public void onClick(View mView) {
                    if (pressureTPMSIndex >= 0 && pressureTPMSIndex < 11) {
                        pressureTPMSIndex = pressureTPMSIndex + 1;
                        select1.setText(pressureTPMS[pressureTPMSIndex]);
                        switch (pressureTPMSIndex) {
                            case 0:
                                edit.putInt(tpmsSettings, 25);
                                switchTpms(10);
                                edit.apply();
                                break;
                            case 1:
                                edit.putInt(tpmsSettings, 30);
                                switchTpms(11);
                                edit.apply();
                                break;
                            case 2:
                                edit.putInt(tpmsSettings, 35);
                                switchTpms(12);
                                edit.apply();
                                break;
                            case 3:
                                edit.putInt(tpmsSettings, 40);
                                switchTpms(13);
                                edit.apply();
                                break;
                            case 4:
                                edit.putInt(tpmsSettings, 45);
                                switchTpms(14);
                                edit.apply();
                                break;
                            case 5:
                                edit.putInt(tpmsSettings, 50);
                                switchTpms(15);
                                edit.apply();
                                break;
                            case 6:
                                edit.putInt(tpmsSettings, 55);
                                switchTpms(16);
                                edit.apply();
                                break;
                            case 7:
                                edit.putInt(tpmsSettings, 60);
                                switchTpms(17);
                                edit.apply();
                                break;
                            case 8:
                                edit.putInt(tpmsSettings, 65);
                                switchTpms(18);
                                edit.apply();
                                break;
                            case 9:
                                edit.putInt(tpmsSettings, 70);
                                switchTpms(19);
                                edit.apply();
                                break;
                            case 10:
                                edit.putInt(tpmsSettings, 75);
                                switchTpms(20);
                                edit.apply();
                                break;
                            case 11:
                                edit.putInt(tpmsSettings, 80);
                                switchTpms(21);
                                edit.apply();
                                break;
                        }
                    }
                }
            });

            //Selector 2
            selector_words_second.setText("Fog Lights With High Beam (Currently Only For 2015-2016)");
            final String[] fogLights = new String[2];
            fogLights[0] = "No";
            fogLights[1] = "Yes";
            if (!isFogLights()) {
                select2.setText(fogLights[0]);
            } else if (isFogLights()) {
                select2.setText(fogLights[1]);
            }

            arrowLeft2.setOnClickListener(new View.OnClickListener() {
                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
                SharedPreferences.Editor edit = mSharedPreferences.edit();

                @Override
                public void onClick(View mView) {
                    select2.setText(fogLights[0]);
                    edit.putBoolean(fogLightsSettings, true);
                    switchFogLights(31);
                    edit.apply();
                }
            });
            arrowRight2.setOnClickListener(new View.OnClickListener() {
                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
                SharedPreferences.Editor edit = mSharedPreferences.edit();

                @Override
                public void onClick(View mView) {
                    select2.setText(fogLights[1]);
                    edit.putBoolean(fogLightsSettings, false);
                    switchFogLights(30);
                    edit.apply();
                }
            });

        } else if (getVehicleType() == VRAM) {

            arrowLeft4.setImageDrawable(null);
            arrowRight4.setImageDrawable(null);

            //Selector 1
            selector_words_first.setText("TPMS Settings");
            final String[] pressureTPMS = new String[13];
            if (isMetric()) {
                pressureTPMS[0] = "175 kPa";
                pressureTPMS[1] = "205 kPa";
                pressureTPMS[2] = "240 kPa";
                pressureTPMS[4] = "275 kPa";
                pressureTPMS[5] = "310 kPa";
                pressureTPMS[3] = "345 kPa";
                pressureTPMS[6] = "380 kPa";
                pressureTPMS[7] = "405 kPa";
                pressureTPMS[8] = "440 kPa";
                pressureTPMS[9] = "475 kPa";
                pressureTPMS[10] = "510 kPa";
                pressureTPMS[11] = "545 kPa";
                pressureTPMS[12] = "Disable";
            } else {
                pressureTPMS[0] = "25 psi";
                pressureTPMS[1] = "30 psi";
                pressureTPMS[2] = "35 psi";
                pressureTPMS[4] = "45 psi";
                pressureTPMS[5] = "50 psi";
                pressureTPMS[3] = "40 psi";
                pressureTPMS[6] = "55 psi";
                pressureTPMS[7] = "60 psi";
                pressureTPMS[8] = "65 psi";
                pressureTPMS[9] = "70 psi";
                pressureTPMS[10] = "75 psi";
                pressureTPMS[11] = "80 psi";
                pressureTPMS[12] = "Disable";
            }
            if (getTPMS() == 25) {
                select1.setText(pressureTPMS[0]);
                pressureTPMSIndex = 0;
            } else if (getTPMS() == 30) {
                select1.setText(pressureTPMS[1]);
                pressureTPMSIndex = 1;
            } else if (getTPMS() == 35) {
                select1.setText(pressureTPMS[2]);
                pressureTPMSIndex = 2;
            } else if (getTPMS() == 40) {
                select1.setText(pressureTPMS[3]);
                pressureTPMSIndex = 3;
            } else if (getTPMS() == 45) {
                select1.setText(pressureTPMS[4]);
                pressureTPMSIndex = 4;
            } else if (getTPMS() == 50) {
                select1.setText(pressureTPMS[5]);
                pressureTPMSIndex = 5;
            } else if (getTPMS() == 55) {
                select1.setText(pressureTPMS[6]);
                pressureTPMSIndex = 6;
            } else if (getTPMS() == 60) {
                select1.setText(pressureTPMS[7]);
                pressureTPMSIndex = 7;
            } else if (getTPMS() == 65) {
                select1.setText(pressureTPMS[8]);
                pressureTPMSIndex = 8;
            } else if (getTPMS() == 70) {
                select1.setText(pressureTPMS[9]);
                pressureTPMSIndex = 9;
            } else if (getTPMS() == 75) {
                select1.setText(pressureTPMS[10]);
                pressureTPMSIndex = 10;
            } else if (getTPMS() == 80) {
                select1.setText(pressureTPMS[11]);
                pressureTPMSIndex = 11;
            } else if (getTPMS() == 0) {
                select1.setText(pressureTPMS[12]);
                pressureTPMSIndex = 12;
            }
            arrowLeft1.setOnClickListener(new View.OnClickListener() {
                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
                SharedPreferences.Editor edit = mSharedPreferences.edit();

                @Override
                public void onClick(View mView) {
                    if (pressureTPMSIndex > 0 && pressureTPMSIndex <= 12) {
                        pressureTPMSIndex = pressureTPMSIndex - 1;
                        select1.setText(pressureTPMS[pressureTPMSIndex]);
                        if (pressureTPMSIndex == 0) {
                            edit.putInt(tpmsSettings, 25);
                            switchTpms(10);
                            edit.apply();
                        } else if (pressureTPMSIndex == 1) {
                            edit.putInt(tpmsSettings, 30);
                            switchTpms(11);
                            edit.apply();
                        } else if (pressureTPMSIndex == 2) {
                            edit.putInt(tpmsSettings, 35);
                            switchTpms(12);
                            edit.apply();
                        } else if (pressureTPMSIndex == 3) {
                            edit.putInt(tpmsSettings, 40);
                            switchTpms(13);
                            edit.apply();
                        } else if (pressureTPMSIndex == 4) {
                            edit.putInt(tpmsSettings, 45);
                            switchTpms(14);
                            edit.apply();
                        } else if (pressureTPMSIndex == 5) {
                            edit.putInt(tpmsSettings, 50);
                            switchTpms(15);
                            edit.apply();
                        } else if (pressureTPMSIndex == 6) {
                            edit.putInt(tpmsSettings, 55);
                            switchTpms(16);
                            edit.apply();
                        } else if (pressureTPMSIndex == 7) {
                            edit.putInt(tpmsSettings, 60);
                            switchTpms(17);
                            edit.apply();
                        } else if (pressureTPMSIndex == 8) {
                            edit.putInt(tpmsSettings, 65);
                            switchTpms(18);
                            edit.apply();
                        } else if (pressureTPMSIndex == 9) {
                            edit.putInt(tpmsSettings, 70);
                            switchTpms(19);
                            edit.apply();
                        } else if (pressureTPMSIndex == 10) {
                            edit.putInt(tpmsSettings, 75);
                            switchTpms(20);
                            edit.apply();
                        } else if (pressureTPMSIndex == 11) {
                            edit.putInt(tpmsSettings, 80);
                            switchTpms(21);
                            edit.apply();
                        } else if (pressureTPMSIndex == 12) {
                            edit.putInt(tpmsSettings, 0);
                            switchTpms(22);
                            edit.apply();
                        }
                    }
                }
            });
            arrowRight1.setOnClickListener(new View.OnClickListener() {
                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
                SharedPreferences.Editor edit = mSharedPreferences.edit();

                @Override
                public void onClick(View mView) {
                    if (pressureTPMSIndex >= 0 && pressureTPMSIndex < 12) {
                        pressureTPMSIndex = pressureTPMSIndex + 1;
                        select1.setText(pressureTPMS[pressureTPMSIndex]);
                        if (pressureTPMSIndex == 0) {
                            edit.putInt(tpmsSettings, 25);
                            switchTpms(10);
                            edit.apply();
                        } else if (pressureTPMSIndex == 1) {
                            edit.putInt(tpmsSettings, 30);
                            switchTpms(11);
                            edit.apply();
                        } else if (pressureTPMSIndex == 2) {
                            edit.putInt(tpmsSettings, 35);
                            switchTpms(12);
                            edit.apply();
                        } else if (pressureTPMSIndex == 3) {
                            edit.putInt(tpmsSettings, 40);
                            switchTpms(13);
                            edit.apply();
                        } else if (pressureTPMSIndex == 4) {
                            edit.putInt(tpmsSettings, 45);
                            switchTpms(14);
                            edit.apply();
                        } else if (pressureTPMSIndex == 5) {
                            edit.putInt(tpmsSettings, 50);
                            switchTpms(15);
                            edit.apply();
                        } else if (pressureTPMSIndex == 6) {
                            edit.putInt(tpmsSettings, 55);
                            switchTpms(16);
                            edit.apply();
                        } else if (pressureTPMSIndex == 7) {
                            edit.putInt(tpmsSettings, 60);
                            switchTpms(17);
                            edit.apply();
                        } else if (pressureTPMSIndex == 8) {
                            edit.putInt(tpmsSettings, 65);
                            switchTpms(18);
                            edit.apply();
                        } else if (pressureTPMSIndex == 9) {
                            edit.putInt(tpmsSettings, 70);
                            switchTpms(19);
                            edit.apply();
                        } else if (pressureTPMSIndex == 10) {
                            edit.putInt(tpmsSettings, 75);
                            switchTpms(20);
                            edit.apply();
                        } else if (pressureTPMSIndex == 11) {
                            edit.putInt(tpmsSettings, 80);
                            switchTpms(21);
                            edit.apply();
                        } else if (pressureTPMSIndex == 12) {
                            edit.putInt(tpmsSettings, 0);
                            switchTpms(22);
                            edit.apply();
                        }
                    }
                }
            });

            //Selector 2
            selector_words_second.setText("Tire Size");
            final String[] tireSize = new String[7];
            if (isMetric()) {
                tireSize[0] = "800 mm";
                tireSize[1] = "825 mm";
                tireSize[2] = "850 mm";
                tireSize[3] = "875 mm";
                tireSize[4] = "900 mm";
                tireSize[5] = "925 mm";
                tireSize[6] = "950 mm";
            } else {
                tireSize[0] = "31\"";
                tireSize[1] = "32\"";
                tireSize[2] = "33\"";
                tireSize[3] = "34\"";
                tireSize[4] = "35\"";
                tireSize[5] = "36\"";
                tireSize[6] = "37\"";
            }
            if (getTireSize() == 31) {
                select2.setText(tireSize[0]);
                tireIndex = 0;
            } else if (getTireSize() == 32) {
                select2.setText(tireSize[1]);
                tireIndex = 1;
            } else if (getTireSize() == 33) {
                select2.setText(tireSize[2]);
                tireIndex = 2;
            } else if (getTireSize() == 34) {
                select2.setText(tireSize[3]);
                tireIndex = 3;
            } else if (getTireSize() == 35) {
                select2.setText(tireSize[4]);
                tireIndex = 4;
            } else if (getTireSize() == 36) {
                select2.setText(tireSize[5]);
                tireIndex = 5;
            } else if (getTireSize() == 37) {
                select2.setText(tireSize[6]);
                tireIndex = 6;
            }
            arrowLeft2.setOnClickListener(new View.OnClickListener() {
                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
                SharedPreferences.Editor edit = mSharedPreferences.edit();

                @Override
                public void onClick(View mView) {

                    if (tireIndex > 0 && tireIndex <= 6) {
                        tireIndex = tireIndex - 1;
                        select2.setText(tireSize[tireIndex]);
                        if (tireIndex == 0) {
                            edit.putInt(tireSizeSettings, 31);
                            switchTireSize(23);
                            edit.apply();
                        } else if (tireIndex == 1) {
                            edit.putInt(tireSizeSettings, 32);
                            switchTireSize(24);
                            edit.apply();
                        } else if (tireIndex == 2) {
                            edit.putInt(tireSizeSettings, 33);
                            switchTireSize(25);
                            edit.apply();
                        } else if (tireIndex == 3) {
                            edit.putInt(tireSizeSettings, 34);
                            switchTireSize(26);
                            edit.apply();
                        } else if (tireIndex == 4) {
                            edit.putInt(tireSizeSettings, 35);
                            switchTireSize(27);
                            edit.apply();
                        } else if (tireIndex == 5) {
                            edit.putInt(tireSizeSettings, 36);
                            switchTireSize(28);
                            edit.apply();
                        } else if (tireIndex == 6) {
                            edit.putInt(tireSizeSettings, 37);
                            switchTireSize(29);
                            edit.apply();
                        }
                    }
                }
            });


            arrowRight2.setOnClickListener(new View.OnClickListener() {
                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
                SharedPreferences.Editor edit = mSharedPreferences.edit();

                @Override
                public void onClick(View mView) {

                    if (tireIndex >= 0 && tireIndex < 6) {
                        tireIndex = tireIndex + 1;
                        select2.setText(tireSize[tireIndex]);
                        if (tireIndex == 0) {
                            edit.putInt(tireSizeSettings, 31);
                            switchTireSize(23);
                            edit.apply();
                        } else if (tireIndex == 1) {
                            edit.putInt(tireSizeSettings, 32);
                            switchTireSize(24);
                            edit.apply();
                        } else if (tireIndex == 2) {
                            edit.putInt(tireSizeSettings, 33);
                            switchTireSize(25);
                            edit.apply();
                        } else if (tireIndex == 3) {
                            edit.putInt(tireSizeSettings, 34);
                            switchTireSize(26);
                            edit.apply();
                        } else if (tireIndex == 4) {
                            edit.putInt(tireSizeSettings, 35);
                            switchTireSize(27);
                            edit.apply();
                        } else if (tireIndex == 5) {
                            edit.putInt(tireSizeSettings, 36);
                            switchTireSize(28);
                            edit.apply();
                        } else if (tireIndex == 6) {
                            edit.putInt(tireSizeSettings, 37);
                            switchTireSize(29);
                            edit.apply();
                        }
                    }
                }
            });

            //Selector 3
            selector_words_third.setText("Fog Lights With High Beam");
            final String[] fogLights = new String[2];
            fogLights[0] = "No";
            fogLights[1] = "Yes";
            if (!isFogLights()) {
                select3.setText(fogLights[0]);
            } else if (isFogLights()) {
                select3.setText(fogLights[1]);
            }

            arrowLeft3.setOnClickListener(new View.OnClickListener() {
                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
                SharedPreferences.Editor edit = mSharedPreferences.edit();

                @Override
                public void onClick(View mView) {
                    select3.setText(fogLights[0]);
                    edit.putBoolean(fogLightsSettings, true);
                    switchFogLights(31);
                    edit.apply();
                }
            });
            arrowRight3.setOnClickListener(new View.OnClickListener() {
                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
                SharedPreferences.Editor edit = mSharedPreferences.edit();

                @Override
                public void onClick(View mView) {
                    select3.setText(fogLights[1]);
                    edit.putBoolean(fogLightsSettings, false);
                    switchFogLights(30);
                    edit.apply();
                }
            });

        }
    }

    private boolean isMetric() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
        return mSharedPreferences.getBoolean("metric", false);
    }

    private int getVehicleType() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
        return mSharedPreferences.getInt(vehicleSettings, VFORD1);
    }

    public int getTireSize() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
        return mSharedPreferences.getInt(tireSizeSettings, 31);
    }

    public int getTPMS() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
        return mSharedPreferences.getInt(tpmsSettings, 80);
    }

    public boolean isLampCurrent() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
        return mSharedPreferences.getBoolean(lampCurrentSettings, false);
    }

    public boolean isFogLights() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
        return mSharedPreferences.getBoolean(fogLightsSettings, false);
    }

    public boolean isDefault() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
        return mSharedPreferences.getBoolean("factory_settings", false);
    }

    //Send to sGDP server to get live data
    public void updateSettingsRequest() {
        isProcessing = true;
        // prepare the Request
        final JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    public void onResponse(JSONObject response) {
                        isConnected = true;
                        try {
                            JSONObject variables = response.getJSONObject("variables");
                            Log.d("TEST2 ", variables.toString());
                            int tpms = variables.getInt("tpms");
                            int signals = variables.getInt("lamp_out");
                            int tireSize = variables.getInt("tire_size");
                            int fogLights = variables.getInt("fog_high");

                            if (getVehicleType() == VFORD1 || getVehicleType() == VFORD2) {
                                actual1.setText(tpms + " psi");
                                if (signals == 1) {
                                    actual2.setText("Yes");
                                } else {
                                    actual2.setText("No");
                                }
                                actual3.setText(tireSize + "\"");
                                if (fogLights == 1) {
                                    actual4.setText("Yes");
                                } else {
                                    actual4.setText("No");
                                }
                            } else if (getVehicleType() == VGM2) {
                                actual1.setText(tpms + " psi");
                                if (fogLights == 1) {
                                    actual2.setText("Yes");
                                } else {
                                    actual2.setText("No");
                                }
                            } else if (getVehicleType() == VRAM) {
                                actual1.setText(tpms + " psi");
                                actual2.setText(tireSize + "\"");
                                if (signals == 1) {
                                    actual3.setText("Yes");
                                } else {
                                    actual3.setText("No");
                                }
                            }


                            Log.d("Response", response.toString());
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

                    }
                }
        );
        // add it to the RequestQueue
        queue.add(getRequest);
    }

    //Send to sGDP server to verify connection
    void switchTpms(int requestTpms) {
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url + "/diag_functions?params=" + requestTpms, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        isConnected = true;
                        try {
                            tpmsNum = response.getInt("return_value");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        SharedPreferences readSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
                        SharedPreferences.Editor edit = readSharedPreferences.edit();
                        switch (tpmsNum) {
                            // Set TPMS to 25
                            case 10:
                                edit.putInt(tpmsSettings, 25);
                                break;
                            // Set TPMS to 30
                            case 11:
                                edit.putInt(tpmsSettings, 30);
                                break;
                            // Set TPMS to 35
                            case 12:
                                edit.putInt(tpmsSettings, 35);
                                break;
                            // Set TPMS to 40
                            case 13:
                                edit.putInt(tpmsSettings, 40);
                                break;
                            // Set TPMS to 45
                            case 14:
                                edit.putInt(tpmsSettings, 45);
                                break;
                            // Set TPMS to 50
                            case 15:
                                edit.putInt(tpmsSettings, 50);
                                break;
                            // Set TPMS to 55
                            case 16:
                                edit.putInt(tpmsSettings, 55);
                                break;
                            // Set TPMS to 60
                            case 17:
                                edit.putInt(tpmsSettings, 60);
                                break;
                            // Set TPMS to 65
                            case 18:
                                edit.putInt(tpmsSettings, 65);
                                break;
                            // Set TPMS to 70
                            case 19:
                                edit.putInt(tpmsSettings, 70);
                                break;
                            // Set TPMS to 75
                            case 20:
                                edit.putInt(tpmsSettings, 75);
                                break;
                            // Set TPMS to 80
                            case 21:
                                edit.putInt(tpmsSettings, 80);
                                break;
                            // Set TPMS to Disabled
                            case 22:
                                edit.putInt(tpmsSettings, 0);
                                edit.apply();
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

    //Send to sGDP server to verify connection
    void switchTireSize(int requestTire) {
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url + "/diag_functions?params=" + requestTire, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        isConnected = true;
                        try {
                            tireNum = response.getInt("return_value");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        SharedPreferences readSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
                        SharedPreferences.Editor edit = readSharedPreferences.edit();
                        switch (tireNum) {
                            // Set Tire Size to 31"
                            case 23:
                                edit.putInt(tireSizeSettings, 31);
                                edit.apply();
                                break;
                            // Set Tire Size to 32"
                            case 24:
                                edit.putInt(tireSizeSettings, 32);
                                edit.apply();
                                break;
                            // Set Tire Size to 33"
                            case 25:
                                edit.putInt(tireSizeSettings, 33);
                                edit.apply();
                                break;
                            // Set Tire Size to 34"
                            case 26:
                                edit.putInt(tireSizeSettings, 34);
                                edit.apply();
                                break;
                            // Set Tire Size to 35"
                            case 27:
                                edit.putInt(tireSizeSettings, 35);
                                edit.apply();
                                break;
                            // Set Tire Size to 36"
                            case 28:
                                edit.putInt(tireSizeSettings, 36);
                                edit.apply();
                                break;
                            // Set Tire Size to 37"
                            case 29:
                                edit.putInt(tireSizeSettings, 37);
                                edit.apply();
                                break;
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

    //Send to sGDP server to verify connection
    void switchTurnSignals(int requestTurnSignals) {
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url + "/diag_functions?params=" + requestTurnSignals, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        isConnected = true;
                        try {
                            lampNum = response.getInt("return_value");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        SharedPreferences readSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
                        SharedPreferences.Editor edit = readSharedPreferences.edit();
                        switch (lampNum) {
                            // Set LED turn Signals OFF
                            case 32:
                                edit.putBoolean(lampCurrentSettings, false);
                                edit.apply();
                                break;
                            // Set LED turn Signals ON
                            case 33:
                                edit.putBoolean(lampCurrentSettings, true);
                                edit.apply();
                                break;
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

    //Send to sGDP server to verify connection
    void switchFogLights(int requestFog) {
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url + "/diag_functions?params=" + requestFog, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        isConnected = true;
                        try {
                            fogNum = response.getInt("return_value");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        SharedPreferences readSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
                        SharedPreferences.Editor edit = readSharedPreferences.edit();
                        switch (fogNum) {
                            // NO Fog Lights with High Beams
                            case 30:
                                edit.putBoolean(fogLightsSettings, false);
                                edit.apply();
                                break;
                            // YES Fog Lights with High Beams
                            case 31:
                                edit.putBoolean(fogLightsSettings, true);
                                edit.apply();
                                break;
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

}