package com.gdptuning.gdptuning;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

    final private static int VFORD1 = 7;
    final private static int VFORD2 = 8;
    final private static int VGM2 = 10;
    final private static int VRAM = 11;
    //ESP32 aREST server address
    final String url = "http://192.168.7.1";
    final String themeColor = "ThemeColor";
    final String vehicleSettings = "vehicle";
    final String tpmsSettings = "pressure_tpms";
    final String tireSizeSettings = "tire_size";
    final String lampCurrentSettings = "lamp_current";
    final String fogLightsSettings = "fog_lights";
    final String daytimeLightsSettings = "daytime_lights";
    final String tpmsSettingsChanged = "pressure_tpms_changed";
    final String tireSizeSettingsChanged = "tire_size_changed";
    final String lampCurrentSettingsChanged = "lamp_current_changed";
    final String fogLightsSettingsChanged = "fog_lights_changed";
    final String daytimeLightsSettingsChanged = "daytime_lights_changed";
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
    private int daytimeLightIndex;
    private int daytimeNum;
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
        arrowLeft1 = mView.findViewById(R.id.strobe_off);
        arrowLeft2 = mView.findViewById(R.id.work_light_off);
        arrowLeft3 = mView.findViewById(R.id.aux1_off);
        arrowLeft4 = mView.findViewById(R.id.aux2_off);
        arrowRight1 = mView.findViewById(R.id.strobe_on);
        arrowRight2 = mView.findViewById(R.id.work_light_on);
        arrowRight3 = mView.findViewById(R.id.aux1_on);
        arrowRight4 = mView.findViewById(R.id.aux2_on);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        queue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()));

        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = mSharedPreferences.edit();

        if (isFirstTime()) {
            edit.putInt(lampCurrentSettings, 99);
            edit.putInt(tpmsSettings, 99);
            edit.putInt(tireSizeSettings, 99);
            edit.putInt(fogLightsSettings, 99);
            edit.putInt(daytimeLightsSettings, 99);
            edit.putBoolean(tpmsSettingsChanged, false);
            edit.putBoolean(lampCurrentSettingsChanged, false);
            edit.putBoolean(tireSizeSettingsChanged, false);
            edit.putBoolean(fogLightsSettingsChanged, false);
            edit.putBoolean(daytimeLightsSettingsChanged, false);
            edit.apply();
        } else {
            if (getVehicleType() == VFORD1 || getVehicleType() == VFORD2) {
                        sendRequestFord();
            } else if (getVehicleType() == VGM2) {
                        sendRequestGM();
            } else if (getVehicleType() == VRAM) {
                        sendRequestRam();
            }
        }


        switch (getVehicleType()) {
            case VFORD1:
            case VFORD2:
                timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        if (isConnected) {
                            if (!isProcessing) {
                                updateFordSettings();
                            }
                        }
                    }
                }, 0, 500);//put here time 1000 milliseconds=1 second
                break;
            case VGM2:
                timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        if (isConnected) {
                            if (!isProcessing) {
                                updateGMSettings();
                            }
                        }
                    }
                }, 0, 500);//put here time 1000 milliseconds=1 second
                break;
            case VRAM:
                timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        if (isConnected) {
                            if (!isProcessing) {
                                updateRAMSettings();
                            }
                        }
                    }
                }, 0, 500);//put here time 1000 milliseconds=1 second
                break;

        }


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
                pressureTPMS[12] = "0 psi";
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
                pressureTPMS[12] = "0 psi";
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
            } else if (getTPMS() == 99) {
                select1.setText("--");
            }
            arrowLeft1.setOnClickListener(new View.OnClickListener() {
                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
                SharedPreferences.Editor edit = mSharedPreferences.edit();

                @Override
                public void onClick(View mView) {
                    if (pressureTPMSIndex > 0 && pressureTPMSIndex <= 12) {
                        pressureTPMSIndex = pressureTPMSIndex - 1;
                        select1.setText(pressureTPMS[pressureTPMSIndex]);
                        edit.putBoolean(tpmsSettingsChanged, true);
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
                        edit.putBoolean(tpmsSettingsChanged, true);
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
            if (getLampCurrent() == 0) {
                select2.setText(lampOutageDisable[0]);
            } else if (getLampCurrent() == 1) {
                select2.setText(lampOutageDisable[1]);
            } else if (getLampCurrent() == 99) {
                select2.setText("--");
            }
            arrowLeft2.setOnClickListener(new View.OnClickListener() {
                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
                SharedPreferences.Editor edit = mSharedPreferences.edit();

                @Override
                public void onClick(View mView) {
                    edit.putBoolean(lampCurrentSettingsChanged, true);
                    select2.setText(lampOutageDisable[0]);
                    edit.putInt(lampCurrentSettings, 1);
                    switchTurnSignals(33);
                    edit.apply();
                }
            });
            arrowRight2.setOnClickListener(new View.OnClickListener() {
                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
                SharedPreferences.Editor edit = mSharedPreferences.edit();

                @Override
                public void onClick(View mView) {
                    edit.putBoolean(lampCurrentSettingsChanged, true);
                    select2.setText(lampOutageDisable[1]);
                    edit.putInt(lampCurrentSettings, 0);
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
            } else if (getTireSize() == 99) {
                select3.setText("--");
            }
            arrowLeft3.setOnClickListener(new View.OnClickListener() {
                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
                SharedPreferences.Editor edit = mSharedPreferences.edit();

                @Override
                public void onClick(View mView) {

                    edit.putBoolean(tireSizeSettingsChanged, true);
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

                    edit.putBoolean(tireSizeSettingsChanged, true);
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
            if (getFogLights() == 0) {
                select4.setText(fogLights[0]);
            } else if (getFogLights() == 1) {
                select4.setText(fogLights[1]);
            } else if (getFogLights() == 99) {
                select4.setText("--");
            }

            arrowLeft4.setOnClickListener(new View.OnClickListener() {
                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
                SharedPreferences.Editor edit = mSharedPreferences.edit();

                @Override
                public void onClick(View mView) {
                    edit.putBoolean(fogLightsSettingsChanged, true);
                    select4.setText(fogLights[0]);
                    edit.putInt(fogLightsSettings, 1);
                    switchFogLights(31);
                    edit.apply();
                }
            });
            arrowRight4.setOnClickListener(new View.OnClickListener() {
                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
                SharedPreferences.Editor edit = mSharedPreferences.edit();

                @Override
                public void onClick(View mView) {
                    edit.putBoolean(fogLightsSettingsChanged, true);
                    select4.setText(fogLights[1]);
                    edit.putInt(fogLightsSettings, 1);
                    switchFogLights(30);
                    edit.apply();
                }
            });
        } else if (getVehicleType() == VGM2) {
            select1.setText("--");
            select2.setText("--");
            select3.setText("--");
            select4.setText(null);
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
                pressureTPMS[12] = "0 psi";
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
                pressureTPMS[12] = "0 psi";
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
            } else if (getTPMS() == 99) {
                select1.setText("--");
            }
            arrowLeft1.setOnClickListener(new View.OnClickListener() {
                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
                SharedPreferences.Editor edit = mSharedPreferences.edit();

                @Override
                public void onClick(View mView) {
                    edit.putBoolean(tpmsSettingsChanged, true);
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
                    edit.putBoolean(tpmsSettingsChanged, true);
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
            if (getFogLights() == 0) {
                select2.setText(fogLights[0]);
            } else if (getFogLights() == 1) {
                select2.setText(fogLights[1]);
            } else if (getFogLights() == 99) {
                select2.setText("--");
            }

            arrowLeft2.setOnClickListener(new View.OnClickListener() {
                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
                SharedPreferences.Editor edit = mSharedPreferences.edit();

                @Override
                public void onClick(View mView) {
                    edit.putBoolean(fogLightsSettingsChanged, true);
                    select2.setText(fogLights[0]);
                    edit.putInt(fogLightsSettings, 1);
                    switchFogLights(31);
                    edit.apply();
                }
            });
            arrowRight2.setOnClickListener(new View.OnClickListener() {
                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
                SharedPreferences.Editor edit = mSharedPreferences.edit();

                @Override
                public void onClick(View mView) {
                    edit.putBoolean(fogLightsSettingsChanged, true);
                    select2.setText(fogLights[1]);
                    edit.putInt(fogLightsSettings, 0);
                    switchFogLights(30);
                    edit.apply();
                }
            });

            //Selector 3
            selector_words_third.setText("Daytime Running Light Configuration (2015-2016)");
            final String[] daytimeLight = new String[3];
            daytimeLight[0] = "Low Beam";
            daytimeLight[1] = "Fog Lights";
            daytimeLight[2] = "Disabled";
            if (getDaytimeLights() == 0) {
                select3.setText(daytimeLight[0]);
                daytimeLightIndex = 0;
            } else if (getDaytimeLights() == 1) {
                select3.setText(daytimeLight[1]);
                daytimeLightIndex = 1;
            } else if (getDaytimeLights() == 2) {
                select3.setText(daytimeLight[2]);
                daytimeLightIndex = 2;
            } else if (getDaytimeLights() == 99) {
                select3.setText("--");
            }
            arrowLeft3.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View mView) {
                    SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, Context.MODE_PRIVATE);
                    SharedPreferences.Editor edit = mSharedPreferences.edit();
                    edit.putBoolean(daytimeLightsSettingsChanged, true);
                    if (daytimeLightIndex > 0 && daytimeLightIndex <= 2) {
                        daytimeLightIndex = daytimeLightIndex - 1;
                        select3.setText(daytimeLight[daytimeLightIndex]);
                        if (daytimeLightIndex == 0) {
                            edit.putInt(daytimeLightsSettings, 0);
                            switchDayTime(34);
                        } else if (daytimeLightIndex == 1) {
                            edit.putInt(daytimeLightsSettings, 1);
                            switchDayTime(35);
                        } else if (daytimeLightIndex == 2) {
                            edit.putInt(daytimeLightsSettings, 2);
                            switchDayTime(37);
                        }
                        edit.apply();
                    }
                }
            });
            arrowRight3.setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View mView) {
                    SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, Context.MODE_PRIVATE);
                    SharedPreferences.Editor edit = mSharedPreferences.edit();
                    edit.putBoolean(daytimeLightsSettingsChanged, true);
                    if (daytimeLightIndex >= 0 && daytimeLightIndex < 2) {
                        daytimeLightIndex = daytimeLightIndex + 1;
                        select3.setText(daytimeLight[daytimeLightIndex]);
                        if (daytimeLightIndex == 0) {
                            edit.putInt(daytimeLightsSettings, 0);
                            switchDayTime(34);
                        } else if (daytimeLightIndex == 1) {
                            edit.putInt(daytimeLightsSettings, 1);
                            switchDayTime(35);
                        } else if (daytimeLightIndex == 2) {
                            edit.putInt(daytimeLightsSettings, 2);
                            switchDayTime(37);
                        }
                        edit.apply();
                    }
                }
            });

        } else if (getVehicleType() == VRAM) {

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
                pressureTPMS[12] = "0 psi";
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
                pressureTPMS[12] = "0 psi";
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
            } else if (getTPMS() == 99) {
                select1.setText("--");
            }
            arrowLeft1.setOnClickListener(new View.OnClickListener() {
                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
                SharedPreferences.Editor edit = mSharedPreferences.edit();

                @Override
                public void onClick(View mView) {
                    edit.putBoolean(tpmsSettingsChanged, true);
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
                    edit.putBoolean(tpmsSettingsChanged, true);
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
            } else if (getTireSize() == 99) {
                select3.setText("--");
            }
            arrowLeft2.setOnClickListener(new View.OnClickListener() {
                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
                SharedPreferences.Editor edit = mSharedPreferences.edit();

                @Override
                public void onClick(View mView) {

                    edit.putBoolean(tireSizeSettingsChanged, true);
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

                    edit.putBoolean(tireSizeSettingsChanged, true);
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
            if (getFogLights() == 0) {
                select3.setText(fogLights[0]);
            } else if (getFogLights() == 1) {
                select3.setText(fogLights[1]);
            } else if (getFogLights() == 99) {
                select3.setText("--");
            }

            arrowLeft3.setOnClickListener(new View.OnClickListener() {
                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
                SharedPreferences.Editor edit = mSharedPreferences.edit();

                @Override
                public void onClick(View mView) {
                    edit.putBoolean(fogLightsSettingsChanged, true);
                    select3.setText(fogLights[0]);
                    edit.putInt(fogLightsSettings, 1);
                    switchFogLights(31);
                    edit.apply();
                }
            });
            arrowRight3.setOnClickListener(new View.OnClickListener() {
                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
                SharedPreferences.Editor edit = mSharedPreferences.edit();

                @Override
                public void onClick(View mView) {
                    edit.putBoolean(fogLightsSettingsChanged, true);
                    select3.setText(fogLights[1]);
                    edit.putInt(fogLightsSettings, 0);
                    switchFogLights(30);
                    edit.apply();
                }
            });

            //Selector 2
            selector_words_fourth.setText("Turn Signal Lamp Outage Disable");
            final String[] lampOutageDisable = new String[2];
            lampOutageDisable[0] = "No";
            lampOutageDisable[1] = "Yes";
            if (getLampCurrent() == 0) {
                select4.setText(lampOutageDisable[0]);
            } else if (getLampCurrent() == 1) {
                select4.setText(lampOutageDisable[1]);
            } else if (getLampCurrent() == 99) {
                select4.setText("--");
            }
            arrowLeft4.setOnClickListener(new View.OnClickListener() {
                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
                SharedPreferences.Editor edit = mSharedPreferences.edit();

                @Override
                public void onClick(View mView) {
                    edit.putBoolean(lampCurrentSettingsChanged, true);
                    select4.setText(lampOutageDisable[0]);
                    edit.putInt(lampCurrentSettings, 1);
                    switchTurnSignals(33);
                    edit.apply();
                }
            });
            arrowRight4.setOnClickListener(new View.OnClickListener() {
                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
                SharedPreferences.Editor edit = mSharedPreferences.edit();

                @Override
                public void onClick(View mView) {
                    edit.putBoolean(lampCurrentSettingsChanged, true);
                    select4.setText(lampOutageDisable[1]);
                    edit.putInt(lampCurrentSettings, 0);
                    switchTurnSignals(32);
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

    public int getLampCurrent() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
        return mSharedPreferences.getInt(lampCurrentSettings, 0);
    }

    public int getFogLights() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
        return mSharedPreferences.getInt(fogLightsSettings, 0);
    }

    public int getDaytimeLights() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
        return mSharedPreferences.getInt(daytimeLightsSettings, 1);
    }

    public boolean isDefault() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
        return mSharedPreferences.getBoolean("factory_settings", false);
    }

    private boolean isTpmsSettingsChanged() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", MODE_PRIVATE);
        return mSharedPreferences.getBoolean(tpmsSettingsChanged, false);
    }

    private boolean isFogLightsSettingsChanged() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", MODE_PRIVATE);
        return mSharedPreferences.getBoolean(fogLightsSettingsChanged, false);
    }

    private boolean isTireSizeSettingsChanged() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", MODE_PRIVATE);
        return mSharedPreferences.getBoolean(tireSizeSettingsChanged, false);
    }

    private boolean isLampCurrentSettingsChanged() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", MODE_PRIVATE);
        return mSharedPreferences.getBoolean(lampCurrentSettingsChanged, false);
    }

    private boolean isDaytimeLightsSettingsChanged() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", MODE_PRIVATE);
        return mSharedPreferences.getBoolean(daytimeLightsSettingsChanged, false);
    }

    private boolean isFirstTime() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", MODE_PRIVATE);
        return mSharedPreferences.getBoolean("first_time", false);
    }

    //Send to sGDP server to verify connection
    public void sendRequestFord() {
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(final JSONObject response) {
                        isConnected = true;
                        try {
                            JSONObject variables = response.getJSONObject("variables");


                            SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, Context.MODE_PRIVATE);
                            SharedPreferences.Editor edit = mSharedPreferences.edit();
                            int tpms = variables.getInt("tpms");
                            int lampOutage = variables.getInt("lamp_out");
                            int fogLights = variables.getInt("fog_high");
                            int tireSize = variables.getInt("tire_size");

                            if (isTpmsSettingsChanged()) {
                                edit.putInt(tpmsSettings, tpms);
                                select1.setText(tpms + " psi");
                                edit.apply();
                            } else {
                                select1.setText("--");
                            }
                            if (isLampCurrentSettingsChanged()) {
                                if (lampOutage == 0) {
                                    edit.putInt(lampCurrentSettings, lampOutage);
                                    select2.setText("No");
                                    edit.apply();
                                } else if (lampOutage == 1) {
                                    edit.putInt(lampCurrentSettings, lampOutage);
                                    select2.setText("Yes");
                                    edit.apply();
                                }
                            } else {
                                select2.setText("--");
                            }
                            if (isTireSizeSettingsChanged()) {
                                select3.setText(tireSize + "\"");
                                edit.putInt(tireSizeSettings, tireSize);
                                edit.apply();
                            } else {
                                select3.setText("--");
                            }
                            if (isFogLightsSettingsChanged()) {
                                if (fogLights == 0) {
                                    edit.putInt(fogLightsSettings, fogLights);
                                    select4.setText("No");
                                    edit.apply();
                                } else if (fogLights == 1) {
                                    edit.putInt(fogLightsSettings, fogLights);
                                    select4.setText("Yes");
                                    edit.apply();
                                }
                            } else {
                                select4.setText("--");
                            }
                            edit.apply();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // display response
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isConnected = false;
                    }
                }
        );
        // add it to the RequestQueue
        queue.add(getRequest);
    }

    //Send to sGDP server to verify connection
    public void sendRequestGM() {
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(final JSONObject response) {
                        isConnected = true;
                        try {
                            JSONObject variables = response.getJSONObject("variables");

                            SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, Context.MODE_PRIVATE);
                            SharedPreferences.Editor edit = mSharedPreferences.edit();
                            int tpms = variables.getInt("tpms");
                            int fogLights = variables.getInt("fog_high");
                            int daytimeRunningLights = variables.getInt("drl");

                            select4.setText(null);
                            selector_words_fourth.setText(null);
                            if (isTpmsSettingsChanged()) {
                                edit.putInt(tpmsSettings, tpms);
                                select1.setText(tpms + " psi");
                                edit.apply();
                            } else {
                                select1.setText("--");
                            }
                            if (isFogLightsSettingsChanged()) {
                                if (fogLights == 0) {
                                    edit.putInt(fogLightsSettings, fogLights);
                                    select2.setText("No");
                                    edit.apply();
                                } else if (fogLights == 1) {
                                    edit.putInt(fogLightsSettings, fogLights);
                                    select2.setText("Yes");
                                    edit.apply();
                                }
                            } else {
                                select2.setText("--");
                            }
                            if (isDaytimeLightsSettingsChanged()) {
                                if (daytimeRunningLights == 0) {
                                    edit.putInt(daytimeLightsSettings, daytimeRunningLights);
                                    select3.setText("Low Beam");
                                    edit.apply();
                                } else if (daytimeRunningLights == 1) {
                                    select3.setText("Fog Lights");
                                    edit.putInt(daytimeLightsSettings, daytimeRunningLights);
                                    edit.apply();
                                } else if (daytimeRunningLights == 2) {
                                    select3.setText("Disabled");
                                    edit.putInt(daytimeLightsSettings, daytimeRunningLights);
                                    edit.apply();
                                }
                            } else {
                                select3.setText("--");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // display response
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isConnected = false;
                    }
                }
        );
        // add it to the RequestQueue
        queue.add(getRequest);
    }

    //Send to sGDP server to verify connection
    public void sendRequestRam() {
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(final JSONObject response) {
                        isConnected = true;
                        try {
                            JSONObject variables = response.getJSONObject("variables");

                            SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, Context.MODE_PRIVATE);
                            @SuppressLint("CommitPrefEdits") SharedPreferences.Editor edit = mSharedPreferences.edit();
                            int tpms = variables.getInt("tpms");
                            int fogLights = variables.getInt("fog_high");
                            int tireSize = variables.getInt("tire_size");
                            int lampOutage = variables.getInt("lamp_out");

                            select4.setText(null);
                            if (isTpmsSettingsChanged()) {
                                select1.setText(tpms + " psi");
                                edit.putInt(tpmsSettings, tpms);
                                edit.apply();
                            } else {
                                select1.setText("--");
                            }
                            if (isTireSizeSettingsChanged()) {
                                select2.setText(tireSize + "\"");
                                edit.putInt(tireSizeSettings, tireSize);
                                edit.apply();
                            } else {
                                select2.setText("--");
                            }
                            if (isFogLightsSettingsChanged()) {
                                if (fogLights == 0) {
                                    select3.setText("No");
                                    edit.putInt(fogLightsSettings, fogLights);
                                    edit.apply();
                                } else if (fogLights == 1) {
                                    select3.setText("Yes");
                                    edit.putInt(fogLightsSettings, fogLights);
                                    edit.apply();
                                }
                            } else {
                                select3.setText("--");
                            }
                            if (isLampCurrentSettingsChanged()) {
                                if (lampOutage == 0) {
                                    edit.putInt(lampCurrentSettings, lampOutage);
                                    select4.setText("No");
                                    edit.apply();
                                } else if (lampOutage == 1) {
                                    edit.putInt(lampCurrentSettings, lampOutage);
                                    select4.setText("Yes");
                                    edit.apply();
                                }
                            } else {
                                select4.setText("--");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // display response
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isConnected = false;
                    }
                }
        );
        // add it to the RequestQueue
        queue.add(getRequest);
    }

    //Send to sGDP server to get live data
    public void updateRAMSettings() {
        isProcessing = true;
        // prepare the Request
        final JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    public void onResponse(JSONObject response) {
                        isConnected = true;
                        try {
                            JSONObject variables = response.getJSONObject("variables");
                            int tpms = variables.getInt("tpms");
                            int fogLights = variables.getInt("fog_high");
                            int tireSize = variables.getInt("tire_size");
                            int lampOutage = variables.getInt("lamp_out");

                            actual1.setText(tpms + " psi");
                            actual2.setText(tireSize + "\"");
                            if (fogLights == 0) {
                                actual3.setText("No");
                            } else if (fogLights == 1) {
                                actual3.setText("Yes");
                            }
                            if (lampOutage == 0) {
                                actual4.setText("No");
                            } else if (lampOutage == 1) {
                                actual4.setText("Yes");
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

                    }
                }
        );
        // add it to the RequestQueue
        queue.add(getRequest);
    }

    //Send to sGDP server to get live data
    public void updateGMSettings() {
        isProcessing = true;
        // prepare the Request
        final JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    public void onResponse(JSONObject response) {
                        isConnected = true;
                        try {
                            JSONObject variables = response.getJSONObject("variables");
                            int tpms = variables.getInt("tpms");
                            int fogLights = variables.getInt("fog_high");
                            int drl = variables.getInt("drl");

                            actual1.setText(tpms + " psi");
                            if (fogLights == 0) {
                                actual2.setText("No");
                            } else if (fogLights == 1) {
                                actual2.setText("Yes");
                            }
                            if (drl == 0) {
                                actual3.setText("Low Beam");
                            } else if (drl == 1) {
                                actual3.setText("Fog Lights");
                            } else if (drl == 2) {
                                actual3.setText("Disabled");
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

                    }
                }
        );
        // add it to the RequestQueue
        queue.add(getRequest);
    }

    //Send to sGDP server to get live data
    public void updateFordSettings() {
        isProcessing = true;
        // prepare the Request
        final JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    public void onResponse(JSONObject response) {
                        isConnected = true;
                        try {
                            JSONObject variables = response.getJSONObject("variables");
                            int tpms = variables.getInt("tpms");
                            int signals = variables.getInt("lamp_out");
                            int tireSize = variables.getInt("tire_size");
                            int fogLights = variables.getInt("fog_high");

                            actual1.setText(tpms + " psi");
                            if (signals == 0) {
                                actual2.setText("No");
                            } else if (signals == 1) {
                                actual2.setText("Yes");
                            }
                            actual3.setText(tireSize + "\"");
                            if (fogLights == 0) {
                                actual4.setText("No");
                            } else if (fogLights == 1) {
                                actual4.setText("Yes");
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
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isConnected = false;

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
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isConnected = false;

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
                                edit.putInt(lampCurrentSettings, 0);
                                edit.apply();
                                break;
                            // Set LED turn Signals ON
                            case 33:
                                edit.putInt(lampCurrentSettings, 1);
                                edit.apply();
                                break;
                        }
                        // display response
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isConnected = false;

                    }
                }
        );
        // add it to the RequestQueue
        queue.add(getRequest);
    }

    //Send to sGDP server to verify connection
    void switchDayTime(int requestDayLights) {
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url + "/diag_functions?params=" + requestDayLights, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        isConnected = true;
                        try {
                            daytimeNum = response.getInt("return_value");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        SharedPreferences readSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
                        SharedPreferences.Editor edit = readSharedPreferences.edit();
                        switch (daytimeNum) {
                            // Set as Headlights
                            case 34:
                                edit.putInt(daytimeLightsSettings, 1);
                                edit.apply();
                                break;
                            // Set as Fog Lights
                            case 35:
                                edit.putInt(daytimeLightsSettings, 2);
                                edit.apply();
                                break;
                            // Set as Turn Signals
                            case 36:
                                edit.putInt(daytimeLightsSettings, 4);
                                edit.apply();
                                break;
                            // Set as Disabled
                            case 37:
                                edit.putInt(daytimeLightsSettings, 5);
                                edit.apply();
                                break;
                            // Set as Led's
                            case 38:
                                edit.putInt(daytimeLightsSettings, 3);
                                edit.apply();
                                break;

                        }
                        // display response
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isConnected = false;

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
                                edit.putInt(fogLightsSettings, 0);
                                edit.apply();
                                break;
                            // YES Fog Lights with High Beams
                            case 31:
                                edit.putInt(fogLightsSettings, 1);
                                edit.apply();
                                break;
                        }
                        // display response
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isConnected = false;

                    }
                }
        );
        // add it to the RequestQueue
        queue.add(getRequest);
    }

}