package com.gdptuning.gdptuning;

import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;

import java.util.Objects;
import java.util.Timer;

import static android.content.Context.MODE_PRIVATE;


public class FeaturesFragment extends Fragment {

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
    RequestQueue queue;
    WifiManager wifi;
    TextView select1, select2, select3, select4, selector_words_first, selector_words_second,
            selector_words_third, selector_words_fourth;
    ImageView arrowRight1, arrowRight2, arrowRight3, arrowLeft1, arrowLeft2, arrowLeft3, arrowLeft4, arrowRight4;
    Timer timer;
    private int pressureTPMSIndex;
    private int tireIndex;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_features, container, false);

        //Id's
        select1 = mView.findViewById(R.id.selector1);
        select2 = mView.findViewById(R.id.selector2);
        select3 = mView.findViewById(R.id.selector3);
        select4 = mView.findViewById(R.id.selector4);
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

        if (!isRead()) {
            if (getVehicleType() == VFORD1 || getVehicleType() == VFORD2) {
                //Selector 1
                selector_words_first.setText("TPMS settings");
                final String[] pressureTPMS = new String[1];
                pressureTPMS[0] = "0 psi";
                select1.setText(pressureTPMS[0]);
                arrowLeft1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View mView) {
                        Toast toast = Toast.makeText(getActivity(), "You must first 'Read' the current settings from your Vehicle", Toast.LENGTH_LONG);
                        toast.show();
                    }
                });
                arrowRight1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View mView) {
                        Toast toast = Toast.makeText(getActivity(), "You must first 'Read' the current settings from your Vehicle", Toast.LENGTH_LONG);
                        toast.show();
                    }
                });

                //Selector 2
                selector_words_second.setText("Turn signal lamp outage disable");
                final String[] lampOutageDisable = new String[1];
                lampOutageDisable[0] = "0";
                select2.setText(lampOutageDisable[0]);
                arrowLeft2.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View mView) {
                        Toast toast = Toast.makeText(getActivity(), "You must first 'Read' the current settings from your Vehicle", Toast.LENGTH_LONG);
                        toast.show();
                    }
                });

                arrowRight2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View mView) {
                        Toast toast = Toast.makeText(getActivity(), "You must first 'Read' the current settings from your Vehicle", Toast.LENGTH_LONG);
                        toast.show();
                    }
                });

                //Selector 3
                selector_words_third.setText("Tire size");
                final String[] tireSize = new String[1];
                tireSize[0] = "0\"";
                select3.setText(tireSize[0]);

                arrowLeft3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View mView) {
                        Toast toast = Toast.makeText(getActivity(), "You must first 'Read' the current settings from your Vehicle", Toast.LENGTH_LONG);
                        toast.show();
                    }
                });

                arrowRight3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View mView) {
                        Toast toast = Toast.makeText(getActivity(), "You must first 'Read' the current settings from your Vehicle", Toast.LENGTH_LONG);
                        toast.show();
                    }
                });

                //Selector 4
                selector_words_fourth.setText("Fog lights with high beam");
                final String[] fogLights = new String[2];
                fogLights[0] = "0";
                select4.setText(fogLights[0]);

                arrowLeft4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View mView) {
                        Toast toast = Toast.makeText(getActivity(), "You must first 'Read' the current settings from your Vehicle", Toast.LENGTH_LONG);
                        toast.show();
                    }
                });
                arrowRight4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View mView) {
                        Toast toast = Toast.makeText(getActivity(), "You must first 'Read' the current settings from your Vehicle", Toast.LENGTH_LONG);
                        toast.show();
                    }
                });

            } else if (getVehicleType() == VGM2) {

                arrowLeft2.setImageDrawable(null);
                arrowLeft3.setImageDrawable(null);
                arrowLeft4.setImageDrawable(null);
                arrowRight2.setImageDrawable(null);
                arrowRight3.setImageDrawable(null);
                arrowRight4.setImageDrawable(null);

                //Selector 1
                selector_words_first.setText("TPMS settings");
                final String[] pressureTPMS = new String[1];
                pressureTPMS[0] = "0psi";
                select1.setText(pressureTPMS[0]);

                arrowLeft1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View mView) {
                        Toast toast = Toast.makeText(getActivity(), "You must first 'Read' the current settings from your Vehicle", Toast.LENGTH_LONG);
                        toast.show();
                    }
                });
                arrowRight1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View mView) {
                        Toast toast = Toast.makeText(getActivity(), "You must first 'Read' the current settings from your Vehicle", Toast.LENGTH_LONG);
                        toast.show();
                    }
                });
            } else if (getVehicleType() == VRAM) {

                arrowLeft4.setImageDrawable(null);
                arrowRight4.setImageDrawable(null);

                //Selector 1
                selector_words_first.setText("TPMS settings");
                final String[] pressureTPMS = new String[1];
                pressureTPMS[0] = "0psi";
                select1.setText(pressureTPMS[0]);

                arrowLeft1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View mView) {
                        Toast toast = Toast.makeText(getActivity(), "You must first 'Read' the current settings from your Vehicle", Toast.LENGTH_LONG);
                        toast.show();
                    }
                });
                arrowRight1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View mView) {
                        Toast toast = Toast.makeText(getActivity(), "You must first 'Read' the current settings from your Vehicle", Toast.LENGTH_LONG);
                        toast.show();
                    }
                });

                //Selector 2
                selector_words_second.setText("Tire size");
                final String[] tireSize = new String[1];
                tireSize[0] = "0\"";
                select2.setText(tireSize[0]);

                arrowLeft2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View mView) {
                        Toast toast = Toast.makeText(getActivity(), "You must first 'Read' the current settings from your Vehicle", Toast.LENGTH_LONG);
                        toast.show();
                    }
                });
                arrowRight2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View mView) {
                        Toast toast = Toast.makeText(getActivity(), "You must first 'Read' the current settings from your Vehicle", Toast.LENGTH_LONG);
                        toast.show();
                    }
                });

                //Selector 3
                selector_words_third.setText("Fog lights with high beam");
                final String[] fogLights = new String[2];
                fogLights[0] = "0";
                select3.setText(fogLights[0]);

                arrowLeft3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View mView) {
                        Toast toast = Toast.makeText(getActivity(), "You must first 'Read' the current settings from your Vehicle", Toast.LENGTH_LONG);
                        toast.show();
                    }
                });
                arrowRight3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View mView) {
                        Toast toast = Toast.makeText(getActivity(), "You must first 'Read' the current settings from your Vehicle", Toast.LENGTH_LONG);
                        toast.show();
                    }
                });
            }
        } else if (isRead()) {
            if (getVehicleType() == VFORD1 || getVehicleType() == VFORD2) {
                //Selector 1
                selector_words_first.setText("TPMS settings");
                final String[] pressureTPMS = new String[13];
                pressureTPMS[0] = "25psi";
                pressureTPMS[1] = "30psi";
                pressureTPMS[2] = "35psi";
                pressureTPMS[4] = "45psi";
                pressureTPMS[5] = "50psi";
                pressureTPMS[3] = "40psi";
                pressureTPMS[6] = "55psi";
                pressureTPMS[7] = "60psi";
                pressureTPMS[8] = "65psi";
                pressureTPMS[9] = "70psi";
                pressureTPMS[10] = "75psi";
                pressureTPMS[11] = "80psi";
                pressureTPMS[12] = "Disable";
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
                    SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", MODE_PRIVATE);
                    SharedPreferences.Editor edit = mSharedPreferences.edit();

                    @Override
                    public void onClick(View mView) {
                        if (pressureTPMSIndex > 0 && pressureTPMSIndex <= 12) {
                            pressureTPMSIndex = pressureTPMSIndex - 1;
                            select1.setText(pressureTPMS[pressureTPMSIndex]);
                            switch (pressureTPMSIndex) {
                                case 0:
                                    edit.putInt("pressure_tpms", 25);
                                    edit.apply();
                                    break;
                                case 1:
                                    edit.putInt("pressure_tpms", 30);
                                    edit.apply();
                                    break;
                                case 2:
                                    edit.putInt("pressure_tpms", 35);
                                    edit.apply();
                                    break;
                                case 3:
                                    edit.putInt("pressure_tpms", 40);
                                    edit.apply();
                                    break;
                                case 4:
                                    edit.putInt("pressure_tpms", 45);
                                    edit.apply();
                                    break;
                                case 5:
                                    edit.putInt("pressure_tpms", 50);
                                    edit.apply();
                                    break;
                                case 6:
                                    edit.putInt("pressure_tpms", 55);
                                    edit.apply();
                                    break;
                                case 7:
                                    edit.putInt("pressure_tpms", 60);
                                    edit.apply();
                                    break;
                                case 8:
                                    edit.putInt("pressure_tpms", 65);
                                    edit.apply();
                                    break;
                                case 9:
                                    edit.putInt("pressure_tpms", 70);
                                    edit.apply();
                                    break;
                                case 10:
                                    edit.putInt("pressure_tpms", 75);
                                    edit.apply();
                                    break;
                                case 11:
                                    edit.putInt("pressure_tpms", 80);
                                    edit.apply();
                                    break;
                                case 12:
                                    edit.putInt("pressure_tpms", 0);
                                    edit.apply();
                                    break;
                            }
                        }
                    }
                });
                arrowRight1.setOnClickListener(new View.OnClickListener() {
                    SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", MODE_PRIVATE);
                    SharedPreferences.Editor edit = mSharedPreferences.edit();

                    @Override
                    public void onClick(View mView) {
                        if (pressureTPMSIndex >= 0 && pressureTPMSIndex < 12) {
                            pressureTPMSIndex = pressureTPMSIndex + 1;
                            select1.setText(pressureTPMS[pressureTPMSIndex]);
                            switch (pressureTPMSIndex) {
                                case 0:
                                    edit.putInt("pressure_tpms", 25);
                                    edit.apply();
                                    break;
                                case 1:
                                    edit.putInt("pressure_tpms", 30);
                                    edit.apply();
                                    break;
                                case 2:
                                    edit.putInt("pressure_tpms", 35);
                                    edit.apply();
                                    break;
                                case 3:
                                    edit.putInt("pressure_tpms", 40);
                                    edit.apply();
                                    break;
                                case 4:
                                    edit.putInt("pressure_tpms", 45);
                                    edit.apply();
                                    break;
                                case 5:
                                    edit.putInt("pressure_tpms", 50);
                                    edit.apply();
                                    break;
                                case 6:
                                    edit.putInt("pressure_tpms", 55);
                                    edit.apply();
                                    break;
                                case 7:
                                    edit.putInt("pressure_tpms", 60);
                                    edit.apply();
                                    break;
                                case 8:
                                    edit.putInt("pressure_tpms", 65);
                                    edit.apply();
                                    break;
                                case 9:
                                    edit.putInt("pressure_tpms", 70);
                                    edit.apply();
                                    break;
                                case 10:
                                    edit.putInt("pressure_tpms", 75);
                                    edit.apply();
                                    break;
                                case 11:
                                    edit.putInt("pressure_tpms", 80);
                                    edit.apply();
                                    break;
                                case 12:
                                    edit.putInt("pressure_tpms", 0);
                                    edit.apply();
                                    break;
                            }
                        }
                    }
                });

                //Selector 2
                selector_words_second.setText("Turn signal lamp outage disable");
                final String[] lampOutageDisable = new String[2];
                lampOutageDisable[0] = "No";
                lampOutageDisable[1] = "Yes";
                if (!isLampCurrent()) {
                    select2.setText(lampOutageDisable[0]);
                } else if (isLampCurrent()) {
                    select2.setText(lampOutageDisable[1]);
                }
                arrowLeft2.setOnClickListener(new View.OnClickListener() {
                    SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", MODE_PRIVATE);
                    SharedPreferences.Editor edit = mSharedPreferences.edit();

                    @Override
                    public void onClick(View mView) {
                        select2.setText(lampOutageDisable[0]);
                        edit.putBoolean("lamp_current", true);
                        edit.apply();
                    }
                });
                arrowRight2.setOnClickListener(new View.OnClickListener() {
                    SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", MODE_PRIVATE);
                    SharedPreferences.Editor edit = mSharedPreferences.edit();

                    @Override
                    public void onClick(View mView) {
                        select2.setText(lampOutageDisable[1]);
                        edit.putBoolean("lamp_current", false);
                        edit.apply();
                    }
                });

                //Selector 3
                selector_words_third.setText("Tire size");
                final String[] tireSize = new String[7];
                tireSize[0] = "31\"";
                tireSize[1] = "32\"";
                tireSize[2] = "33\"";
                tireSize[3] = "34\"";
                tireSize[4] = "35\"";
                tireSize[5] = "36\"";
                tireSize[6] = "37\"";
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
                    SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", MODE_PRIVATE);
                    SharedPreferences.Editor edit = mSharedPreferences.edit();

                    @Override
                    public void onClick(View mView) {

                        if (tireIndex > 0 && tireIndex <= 6) {
                            tireIndex = tireIndex - 1;
                            select3.setText(tireSize[tireIndex]);
                            if (tireIndex == 0) {
                                edit.putInt("tire_size", 31);
                                edit.apply();
                            } else if (tireIndex == 1) {
                                edit.putInt("tire_size", 32);
                                edit.apply();
                            } else if (tireIndex == 2) {
                                edit.putInt("tire_size", 33);
                                edit.apply();
                            } else if (tireIndex == 3) {
                                edit.putInt("tire_size", 34);
                                edit.apply();
                            } else if (tireIndex == 4) {
                                edit.putInt("tire_size", 35);
                                edit.apply();
                            } else if (tireIndex == 5) {
                                edit.putInt("tire_size", 36);
                                edit.apply();
                            } else if (tireIndex == 6) {
                                edit.putInt("tire_size", 37);
                                edit.apply();
                            }
                        }
                    }
                });


                arrowRight3.setOnClickListener(new View.OnClickListener() {
                    SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", MODE_PRIVATE);
                    SharedPreferences.Editor edit = mSharedPreferences.edit();

                    @Override
                    public void onClick(View mView) {

                        if (tireIndex >= 0 && tireIndex < 6) {
                            tireIndex = tireIndex + 1;
                            select3.setText(tireSize[tireIndex]);
                            if (tireIndex == 0) {
                                edit.putInt("tire_size", 31);
                                edit.apply();
                            } else if (tireIndex == 1) {
                                edit.putInt("tire_size", 32);
                                edit.apply();
                            } else if (tireIndex == 2) {
                                edit.putInt("tire_size", 33);
                                edit.apply();
                            } else if (tireIndex == 3) {
                                edit.putInt("tire_size", 34);
                                edit.apply();
                            } else if (tireIndex == 4) {
                                edit.putInt("tire_size", 35);
                                edit.apply();
                            } else if (tireIndex == 5) {
                                edit.putInt("tire_size", 36);
                                edit.apply();
                            } else if (tireIndex == 6) {
                                edit.putInt("tire_size", 37);
                                edit.apply();
                            }
                        }
                    }
                });

                //Selector 4
                selector_words_fourth.setText("Fog lights with high beam");
                final String[] fogLights = new String[2];
                fogLights[0] = "No";
                fogLights[1] = "Yes";
                if (!isFogLights()) {
                    select4.setText(fogLights[0]);
                } else if (isFogLights()) {
                    select4.setText(fogLights[1]);
                }

                arrowLeft4.setOnClickListener(new View.OnClickListener() {
                    SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", MODE_PRIVATE);
                    SharedPreferences.Editor edit = mSharedPreferences.edit();

                    @Override
                    public void onClick(View mView) {
                        select4.setText(fogLights[0]);
                        edit.putBoolean("fog_lights", true);
                        edit.apply();
                    }
                });
                arrowRight4.setOnClickListener(new View.OnClickListener() {
                    SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", MODE_PRIVATE);
                    SharedPreferences.Editor edit = mSharedPreferences.edit();

                    @Override
                    public void onClick(View mView) {
                        select4.setText(fogLights[1]);
                        edit.putBoolean("fog_lights", false);
                        edit.apply();
                    }
                });
            } else if (getVehicleType() == VGM2) {

                arrowLeft2.setImageDrawable(null);
                arrowLeft3.setImageDrawable(null);
                arrowLeft4.setImageDrawable(null);
                arrowRight2.setImageDrawable(null);
                arrowRight3.setImageDrawable(null);
                arrowRight4.setImageDrawable(null);

                //Selector 1
                selector_words_first.setText("TPMS settings");
                final String[] pressureTPMS = new String[13];
                pressureTPMS[0] = "25psi";
                pressureTPMS[1] = "30psi";
                pressureTPMS[2] = "35psi";
                pressureTPMS[4] = "45psi";
                pressureTPMS[5] = "50psi";
                pressureTPMS[3] = "40psi";
                pressureTPMS[6] = "55psi";
                pressureTPMS[7] = "60psi";
                pressureTPMS[8] = "65psi";
                pressureTPMS[9] = "70psi";
                pressureTPMS[10] = "75psi";
                pressureTPMS[11] = "80psi";
                pressureTPMS[12] = "Disable";
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
                    SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", MODE_PRIVATE);
                    SharedPreferences.Editor edit = mSharedPreferences.edit();

                    @Override
                    public void onClick(View mView) {
                        if (pressureTPMSIndex > 0 && pressureTPMSIndex <= 12) {
                            pressureTPMSIndex = pressureTPMSIndex - 1;
                            select1.setText(pressureTPMS[pressureTPMSIndex]);
                            switch (pressureTPMSIndex) {
                                case 0:
                                    edit.putInt("pressure_tpms", 25);
                                    edit.apply();
                                    break;
                                case 1:
                                    edit.putInt("pressure_tpms", 30);
                                    edit.apply();
                                    break;
                                case 2:
                                    edit.putInt("pressure_tpms", 35);
                                    edit.apply();
                                    break;
                                case 3:
                                    edit.putInt("pressure_tpms", 40);
                                    edit.apply();
                                    break;
                                case 4:
                                    edit.putInt("pressure_tpms", 45);
                                    edit.apply();
                                    break;
                                case 5:
                                    edit.putInt("pressure_tpms", 50);
                                    edit.apply();
                                    break;
                                case 6:
                                    edit.putInt("pressure_tpms", 55);
                                    edit.apply();
                                    break;
                                case 7:
                                    edit.putInt("pressure_tpms", 60);
                                    edit.apply();
                                    break;
                                case 8:
                                    edit.putInt("pressure_tpms", 65);
                                    edit.apply();
                                    break;
                                case 9:
                                    edit.putInt("pressure_tpms", 70);
                                    edit.apply();
                                    break;
                                case 10:
                                    edit.putInt("pressure_tpms", 75);
                                    edit.apply();
                                    break;
                                case 11:
                                    edit.putInt("pressure_tpms", 80);
                                    edit.apply();
                                    break;
                                case 12:
                                    edit.putInt("pressure_tpms", 0);
                                    edit.apply();
                                    break;
                            }
                        }
                    }
                });
                arrowRight1.setOnClickListener(new View.OnClickListener() {
                    SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", MODE_PRIVATE);
                    SharedPreferences.Editor edit = mSharedPreferences.edit();

                    @Override
                    public void onClick(View mView) {
                        if (pressureTPMSIndex >= 0 && pressureTPMSIndex < 12) {
                            pressureTPMSIndex = pressureTPMSIndex + 1;
                            select1.setText(pressureTPMS[pressureTPMSIndex]);
                            switch (pressureTPMSIndex) {
                                case 0:
                                    edit.putInt("pressure_tpms", 25);
                                    edit.apply();
                                    break;
                                case 1:
                                    edit.putInt("pressure_tpms", 30);
                                    edit.apply();
                                    break;
                                case 2:
                                    edit.putInt("pressure_tpms", 35);
                                    edit.apply();
                                    break;
                                case 3:
                                    edit.putInt("pressure_tpms", 40);
                                    edit.apply();
                                    break;
                                case 4:
                                    edit.putInt("pressure_tpms", 45);
                                    edit.apply();
                                    break;
                                case 5:
                                    edit.putInt("pressure_tpms", 50);
                                    edit.apply();
                                    break;
                                case 6:
                                    edit.putInt("pressure_tpms", 55);
                                    edit.apply();
                                    break;
                                case 7:
                                    edit.putInt("pressure_tpms", 60);
                                    edit.apply();
                                    break;
                                case 8:
                                    edit.putInt("pressure_tpms", 65);
                                    edit.apply();
                                    break;
                                case 9:
                                    edit.putInt("pressure_tpms", 70);
                                    edit.apply();
                                    break;
                                case 10:
                                    edit.putInt("pressure_tpms", 75);
                                    edit.apply();
                                    break;
                                case 11:
                                    edit.putInt("pressure_tpms", 80);
                                    edit.apply();
                                    break;
                                case 12:
                                    edit.putInt("pressure_tpms", 0);
                                    edit.apply();
                                    break;
                            }
                        }
                    }
                });


            } else if (getVehicleType() == VRAM) {

                arrowLeft4.setImageDrawable(null);
                arrowRight4.setImageDrawable(null);

                //Selector 1
                selector_words_first.setText("TPMS settings");
                final String[] pressureTPMS = new String[13];
                pressureTPMS[0] = "25psi";
                pressureTPMS[1] = "30psi";
                pressureTPMS[2] = "35psi";
                pressureTPMS[4] = "45psi";
                pressureTPMS[5] = "50psi";
                pressureTPMS[3] = "40psi";
                pressureTPMS[6] = "55psi";
                pressureTPMS[7] = "60psi";
                pressureTPMS[8] = "65psi";
                pressureTPMS[9] = "70psi";
                pressureTPMS[10] = "75psi";
                pressureTPMS[11] = "80psi";
                pressureTPMS[12] = "Disable";
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
                    SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", MODE_PRIVATE);
                    SharedPreferences.Editor edit = mSharedPreferences.edit();

                    @Override
                    public void onClick(View mView) {
                        if (pressureTPMSIndex > 0 && pressureTPMSIndex <= 12) {
                            pressureTPMSIndex = pressureTPMSIndex - 1;
                            select1.setText(pressureTPMS[pressureTPMSIndex]);
                            if (pressureTPMSIndex == 0) {
                                edit.putInt("pressure_tpms", 25);
                                edit.apply();
                            } else if (pressureTPMSIndex == 1) {
                                edit.putInt("pressure_tpms", 30);
                                edit.apply();
                            } else if (pressureTPMSIndex == 2) {
                                edit.putInt("pressure_tpms", 35);
                                edit.apply();
                            } else if (pressureTPMSIndex == 3) {
                                edit.putInt("pressure_tpms", 40);
                                edit.apply();
                            } else if (pressureTPMSIndex == 4) {
                                edit.putInt("pressure_tpms", 45);
                                edit.apply();
                            } else if (pressureTPMSIndex == 5) {
                                edit.putInt("pressure_tpms", 50);
                                edit.apply();
                            } else if (pressureTPMSIndex == 6) {
                                edit.putInt("pressure_tpms", 55);
                                edit.apply();
                            } else if (pressureTPMSIndex == 7) {
                                edit.putInt("pressure_tpms", 60);
                                edit.apply();
                            } else if (pressureTPMSIndex == 8) {
                                edit.putInt("pressure_tpms", 65);
                                edit.apply();
                            } else if (pressureTPMSIndex == 9) {
                                edit.putInt("pressure_tpms", 70);
                                edit.apply();
                            } else if (pressureTPMSIndex == 10) {
                                edit.putInt("pressure_tpms", 75);
                                edit.apply();
                            } else if (pressureTPMSIndex == 11) {
                                edit.putInt("pressure_tpms", 80);
                                edit.apply();
                            } else if (pressureTPMSIndex == 12) {
                                edit.putInt("pressure_tpms", 0);
                                edit.apply();
                            }
                        }
                    }
                });
                arrowRight1.setOnClickListener(new View.OnClickListener() {
                    SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", MODE_PRIVATE);
                    SharedPreferences.Editor edit = mSharedPreferences.edit();

                    @Override
                    public void onClick(View mView) {
                        if (pressureTPMSIndex >= 0 && pressureTPMSIndex < 12) {
                            pressureTPMSIndex = pressureTPMSIndex + 1;
                            select1.setText(pressureTPMS[pressureTPMSIndex]);
                            if (pressureTPMSIndex == 0) {
                                edit.putInt("pressure_tpms", 25);
                                edit.apply();
                            } else if (pressureTPMSIndex == 1) {
                                edit.putInt("pressure_tpms", 30);
                                edit.apply();
                            } else if (pressureTPMSIndex == 2) {
                                edit.putInt("pressure_tpms", 35);
                                edit.apply();
                            } else if (pressureTPMSIndex == 3) {
                                edit.putInt("pressure_tpms", 40);
                                edit.apply();
                            } else if (pressureTPMSIndex == 4) {
                                edit.putInt("pressure_tpms", 45);
                                edit.apply();
                            } else if (pressureTPMSIndex == 5) {
                                edit.putInt("pressure_tpms", 50);
                                edit.apply();
                            } else if (pressureTPMSIndex == 6) {
                                edit.putInt("pressure_tpms", 55);
                                edit.apply();
                            } else if (pressureTPMSIndex == 7) {
                                edit.putInt("pressure_tpms", 60);
                                edit.apply();
                            } else if (pressureTPMSIndex == 8) {
                                edit.putInt("pressure_tpms", 65);
                                edit.apply();
                            } else if (pressureTPMSIndex == 9) {
                                edit.putInt("pressure_tpms", 70);
                                edit.apply();
                            } else if (pressureTPMSIndex == 10) {
                                edit.putInt("pressure_tpms", 75);
                                edit.apply();
                            } else if (pressureTPMSIndex == 11) {
                                edit.putInt("pressure_tpms", 80);
                                edit.apply();
                            } else if (pressureTPMSIndex == 12) {
                                edit.putInt("pressure_tpms", 0);
                                edit.apply();
                            }
                        }
                    }
                });

                //Selector 2
                selector_words_third.setText("Tire size");
                final String[] tireSize = new String[7];
                tireSize[0] = "31\"";
                tireSize[1] = "32\"";
                tireSize[2] = "33\"";
                tireSize[3] = "34\"";
                tireSize[4] = "35\"";
                tireSize[5] = "36\"";
                tireSize[6] = "37\"";
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
                arrowLeft2.setOnClickListener(new View.OnClickListener() {
                    SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", MODE_PRIVATE);
                    SharedPreferences.Editor edit = mSharedPreferences.edit();

                    @Override
                    public void onClick(View mView) {

                        if (tireIndex > 0 && tireIndex <= 6) {
                            tireIndex = tireIndex - 1;
                            select2.setText(tireSize[tireIndex]);
                            if (tireIndex == 0) {
                                edit.putInt("tire_size", 31);
                                edit.apply();
                            } else if (tireIndex == 1) {
                                edit.putInt("tire_size", 32);
                                edit.apply();
                            } else if (tireIndex == 2) {
                                edit.putInt("tire_size", 33);
                                edit.apply();
                            } else if (tireIndex == 3) {
                                edit.putInt("tire_size", 34);
                                edit.apply();
                            } else if (tireIndex == 4) {
                                edit.putInt("tire_size", 35);
                                edit.apply();
                            } else if (tireIndex == 5) {
                                edit.putInt("tire_size", 36);
                                edit.apply();
                            } else if (tireIndex == 6) {
                                edit.putInt("tire_size", 37);
                                edit.apply();
                            }
                        }
                    }
                });


                arrowRight2.setOnClickListener(new View.OnClickListener() {
                    SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", MODE_PRIVATE);
                    SharedPreferences.Editor edit = mSharedPreferences.edit();

                    @Override
                    public void onClick(View mView) {

                        if (tireIndex >= 0 && tireIndex < 6) {
                            tireIndex = tireIndex + 1;
                            select2.setText(tireSize[tireIndex]);
                            if (tireIndex == 0) {
                                edit.putInt("tire_size", 31);
                                edit.apply();
                            } else if (tireIndex == 1) {
                                edit.putInt("tire_size", 32);
                                edit.apply();
                            } else if (tireIndex == 2) {
                                edit.putInt("tire_size", 33);
                                edit.apply();
                            } else if (tireIndex == 3) {
                                edit.putInt("tire_size", 34);
                                edit.apply();
                            } else if (tireIndex == 4) {
                                edit.putInt("tire_size", 35);
                                edit.apply();
                            } else if (tireIndex == 5) {
                                edit.putInt("tire_size", 36);
                                edit.apply();
                            } else if (tireIndex == 6) {
                                edit.putInt("tire_size", 37);
                                edit.apply();
                            }
                        }
                    }
                });

                //Selector 3
                selector_words_fourth.setText("Fog lights with high beam");
                final String[] fogLights = new String[2];
                fogLights[0] = "No";
                fogLights[1] = "Yes";
                if (!isFogLights()) {
                    select3.setText(fogLights[0]);
                } else if (isFogLights()) {
                    select3.setText(fogLights[1]);
                }

                arrowLeft3.setOnClickListener(new View.OnClickListener() {
                    SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", MODE_PRIVATE);
                    SharedPreferences.Editor edit = mSharedPreferences.edit();

                    @Override
                    public void onClick(View mView) {
                        select3.setText(fogLights[0]);
                        edit.putBoolean("fog_lights", true);
                        edit.apply();
                    }
                });
                arrowRight3.setOnClickListener(new View.OnClickListener() {
                    SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", MODE_PRIVATE);
                    SharedPreferences.Editor edit = mSharedPreferences.edit();

                    @Override
                    public void onClick(View mView) {
                        select3.setText(fogLights[1]);
                        edit.putBoolean("fog_lights", false);
                        edit.apply();
                    }
                });
            }
        }
    }


    private int getVehicleType() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", MODE_PRIVATE);
        return mSharedPreferences.getInt("vehicle", VFORD1);
    }


    public int getTireSize() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", MODE_PRIVATE);
        return mSharedPreferences.getInt("tire_size", 31);
    }

    public int getTPMS() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", MODE_PRIVATE);
        return mSharedPreferences.getInt("pressure_tpms", 80);
    }

    public boolean isLampCurrent() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", MODE_PRIVATE);
        return mSharedPreferences.getBoolean("lamp_current", false);
    }

    public boolean isRead() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", MODE_PRIVATE);
        return mSharedPreferences.getBoolean("read_settings", false);
    }

    public boolean isFogLights() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", MODE_PRIVATE);
        return mSharedPreferences.getBoolean("fog_lights", false);
    }

    public boolean isDefault() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", MODE_PRIVATE);
        return mSharedPreferences.getBoolean("factory_settings", false);
    }

    public int getDefaultTireSize() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("Default_Settings", MODE_PRIVATE);
        return mSharedPreferences.getInt("tire_size", 31);
    }

    public int getDefaultTPMS() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("Default_Settings", MODE_PRIVATE);
        return mSharedPreferences.getInt("pressure_tpms", 80);
    }

    public boolean isDefaultLampCurrent() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("Default_Settings", MODE_PRIVATE);
        return mSharedPreferences.getBoolean("lamp_current", false);
    }

    public boolean isDefaultFogLights() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("Default_Settings", MODE_PRIVATE);
        return mSharedPreferences.getBoolean("fog_lights", false);
    }

    public int getDefaultDaytimeLights() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("Default_Settings", MODE_PRIVATE);
        return mSharedPreferences.getInt("daytime_lights", 1);
    }

    public int getDefaultRemoteStart() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("Default_Settings", MODE_PRIVATE);
        return mSharedPreferences.getInt("remote_start", 3);
    }

    public boolean isDefaultNavOverride() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("Default_Settings", MODE_PRIVATE);
        return mSharedPreferences.getBoolean("nav_override", false);
    }

    public boolean isDefaultRemoteWindow() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("Default_Settings", MODE_PRIVATE);
        return mSharedPreferences.getBoolean("remote_window", false);
    }

    public boolean isDefaultAux1() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("Default_Settings", MODE_PRIVATE);
        return mSharedPreferences.getBoolean("aux1", false);
    }

    public boolean isDefaultAux2() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("Default_Settings", MODE_PRIVATE);
        return mSharedPreferences.getBoolean("aux2", false);
    }

    public boolean isDefaultAux3() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("Default_Settings", MODE_PRIVATE);
        return mSharedPreferences.getBoolean("aux3", false);
    }

    public boolean isDefaultWorkLight() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("Default_Settings", MODE_PRIVATE);
        return mSharedPreferences.getBoolean("work_light", false);
    }

    public boolean isDefaultStrobeLight() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("Default_Settings", MODE_PRIVATE);
        return mSharedPreferences.getBoolean("strobe_light", false);
    }

    public boolean isDefaultHighIdle() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("Default_Settings", MODE_PRIVATE);
        return mSharedPreferences.getBoolean("high_idle", false);
    }

}