package com.gdptuning.gdptuning;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;

import java.util.Objects;
import java.util.Timer;

import static android.content.Context.MODE_PRIVATE;


public class FeaturesFragment2 extends Fragment {

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
    Button btn_home;
    WifiManager wifi;
    TextView select1, select2, select3, select4, selector_words_first_2, selector_words_second_2,
            selector_words_third_2, selector_words_fourth_2;
    ImageView arrowRight1, arrowRight2, arrowRight3, arrowLeft1, arrowLeft2, arrowLeft3, arrowLeft4, arrowRight4;
    Timer timer;
    private int daytimeLightIndex;
    private int remoteStartIndex;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_features2, container, false);

        //Id's
        select1 = mView.findViewById(R.id.selector1);
        select2 = mView.findViewById(R.id.selector2);
        select3 = mView.findViewById(R.id.selector3);
        select4 = mView.findViewById(R.id.selector4);
        selector_words_first_2 = mView.findViewById(R.id.first_selector_features_2);
        selector_words_second_2 = mView.findViewById(R.id.second_selector_features_2);
        selector_words_third_2 = mView.findViewById(R.id.third_selector_features_2);
        selector_words_fourth_2 = mView.findViewById(R.id.fourth_selector_features_2);
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!isRead()) {
            if (getVehicleType() == VFORD1 || getVehicleType() == VFORD2) {
                //Selector 1
                selector_words_first_2.setText("Daytime running light configuration");
                final String[] pressureTPMS = new String[1];
                pressureTPMS[0] = "0";
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
                selector_words_second_2.setText("Remote start duration");
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
                selector_words_third_2.setText("Navigation Override (Destination Entry while Driving)");
                final String[] tireSize = new String[1];
                tireSize[0] = "0";
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
                selector_words_fourth_2.setText("Windows up/down with remote key fob");
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
                selector_words_first_2.setText("TPMS SETTINGS");
                final String[] pressureTPMS = new String[1];
                pressureTPMS[0] = "0";
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
                selector_words_first_2.setText("TPMS SETTINGS");
                final String[] pressureTPMS = new String[1];
                pressureTPMS[0] = "0";
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
                selector_words_second_2.setText("TIRE SIZE");
                final String[] tireSize = new String[1];
                tireSize[0] = "0";
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
                selector_words_third_2.setText("FOG LIGHTS WITH HIGH BEAM");
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
                selector_words_first_2.setText("Daytime Running Light Configuration");
                final String[] daytimeLight = new String[7];
                daytimeLight[0] = "Low Beam";
                daytimeLight[1] = "Fog Lights";
                daytimeLight[2] = "Dedicated LED";
                daytimeLight[3] = "Turn Signals";
                daytimeLight[4] = "Disabled";
                if (getDaytimeLights() == 1) {
                    select1.setText(daytimeLight[0]);
                    daytimeLightIndex = 0;
                } else if (getDaytimeLights() == 2) {
                    select1.setText(daytimeLight[1]);
                    daytimeLightIndex = 1;
                } else if (getDaytimeLights() == 3) {
                    select1.setText(daytimeLight[2]);
                    daytimeLightIndex = 2;
                } else if (getDaytimeLights() == 4) {
                    select1.setText(daytimeLight[3]);
                    daytimeLightIndex = 3;
                } else if (getDaytimeLights() == 5) {
                    select1.setText(daytimeLight[4]);
                    daytimeLightIndex = 4;
                }
                arrowLeft1.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View mView) {
                        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                        SharedPreferences.Editor edit = mSharedPreferences.edit();
                        if (daytimeLightIndex > 0 && daytimeLightIndex <= 4) {
                            daytimeLightIndex = daytimeLightIndex - 1;
                            select1.setText(daytimeLight[daytimeLightIndex]);
                            if (daytimeLightIndex == 0) {
                                edit.putInt("daytime_lights", 1);
                            } else if (daytimeLightIndex == 1) {
                                edit.putInt("daytime_lights", 2);
                            } else if (daytimeLightIndex == 2) {
                                edit.putInt("daytime_lights", 3);
                            } else if (daytimeLightIndex == 3) {
                                edit.putInt("daytime_lights", 4);
                            } else if (daytimeLightIndex == 4) {
                                edit.putInt("daytime_lights", 5);
                            }
                            edit.apply();
                        }
                    }
                });
                arrowRight1.setOnClickListener(new View.OnClickListener() {


                    @Override
                    public void onClick(View mView) {
                        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                        SharedPreferences.Editor edit = mSharedPreferences.edit();
                        if (daytimeLightIndex >= 0 && daytimeLightIndex < 4) {
                            daytimeLightIndex = daytimeLightIndex + 1;
                            select1.setText(daytimeLight[daytimeLightIndex]);
                            if (daytimeLightIndex == 0) {
                                edit.putInt("daytime_lights", 1);
                            } else if (daytimeLightIndex == 1) {
                                edit.putInt("daytime_lights", 2);
                            } else if (daytimeLightIndex == 2) {
                                edit.putInt("daytime_lights", 3);
                            } else if (daytimeLightIndex == 3) {
                                edit.putInt("daytime_lights", 4);
                            } else if (daytimeLightIndex == 4) {
                                edit.putInt("daytime_lights", 5);
                            }
                            edit.apply();
                        }
                    }
                });


                //Selector 2
                selector_words_second_2.setText("Remote Start Duration");
                final String[] remoteStart = new String[7];
                remoteStart[0] = "5 Minutes";
                remoteStart[1] = "10 Minutes";
                remoteStart[2] = "15 Minutes";
                if (getRemoteStart() == 1) {
                    select2.setText(remoteStart[0]);
                    remoteStartIndex = 0;
                } else if (getRemoteStart() == 2) {
                    select2.setText(remoteStart[1]);
                    remoteStartIndex = 1;
                } else if (getRemoteStart() == 3) {
                    select2.setText(remoteStart[2]);
                    remoteStartIndex = 2;
                }
                arrowLeft2.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View mView) {
                        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                        SharedPreferences.Editor edit = mSharedPreferences.edit();
                        if (remoteStartIndex > 0 && remoteStartIndex <= 2) {
                            remoteStartIndex = remoteStartIndex - 1;
                            select2.setText(remoteStart[remoteStartIndex]);
                            if (remoteStartIndex == 0) {
                                edit.putInt("remote_start", 1);
                            } else if (remoteStartIndex == 1) {
                                edit.putInt("remote_start", 2);
                            } else if (remoteStartIndex == 2) {
                                edit.putInt("remote_start", 3);
                            }
                            edit.apply();
                        }
                    }
                });
                arrowRight2.setOnClickListener(new View.OnClickListener() {


                    @Override
                    public void onClick(View mView) {
                        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                        SharedPreferences.Editor edit = mSharedPreferences.edit();
                        if (remoteStartIndex >= 0 && remoteStartIndex < 2) {
                            remoteStartIndex = remoteStartIndex + 1;
                            select2.setText(remoteStart[remoteStartIndex]);
                            if (remoteStartIndex == 0) {
                                edit.putInt("remote_start", 1);
                            } else if (remoteStartIndex == 1) {
                                edit.putInt("remote_start", 2);
                            } else if (remoteStartIndex == 2) {
                                edit.putInt("remote_start", 3);
                            }
                            edit.apply();
                        }
                    }
                });

                //Selector 3
                selector_words_third_2.setText("Navigation Override(Allows Destination Entry While Driving)");
                final String[] navOverride = new String[2];
                navOverride[0] = "NO";
                navOverride[1] = "YES";
                if (!isNavOverride()) {
                    select3.setText(navOverride[0]);
                } else if (isNavOverride()) {
                    select3.setText(navOverride[1]);
                }
                arrowLeft3.setOnClickListener(new View.OnClickListener() {
                    SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                    SharedPreferences.Editor edit = mSharedPreferences.edit();

                    @Override
                    public void onClick(View mView) {
                        select3.setText(navOverride[0]);
                        edit.putBoolean("nav_override", true);
                        edit.apply();
                    }
                });
                arrowRight3.setOnClickListener(new View.OnClickListener() {
                    SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                    SharedPreferences.Editor edit = mSharedPreferences.edit();

                    @Override
                    public void onClick(View mView) {
                        select3.setText(navOverride[1]);
                        edit.putBoolean("nav_override", false);
                        edit.apply();
                    }
                });

                //Selector 4
                selector_words_fourth_2.setText("Windows Up/Down With Remote Keyfob");
                final String[] remoteWindow = new String[2];
                remoteWindow[0] = "NO";
                remoteWindow[1] = "YES";
                if (!isRemoteWindow()) {
                    select4.setText(remoteWindow[0]);
                } else if (isRemoteWindow()) {
                    select4.setText(remoteWindow[1]);
                }
                arrowLeft4.setOnClickListener(new View.OnClickListener() {
                    SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                    SharedPreferences.Editor edit = mSharedPreferences.edit();

                    @Override
                    public void onClick(View mView) {
                        select4.setText(remoteWindow[0]);
                        edit.putBoolean("remote_window", true);
                        edit.apply();
                    }
                });
                arrowRight4.setOnClickListener(new View.OnClickListener() {
                    SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                    SharedPreferences.Editor edit = mSharedPreferences.edit();

                    @Override
                    public void onClick(View mView) {
                        select4.setText(remoteWindow[1]);
                        edit.putBoolean("remote_window", false);
                        edit.apply();
                    }
                });
            }

            if (getVehicleType() == VGM2) {
                //Selector 1
                selector_words_first_2.setText("Daytime running light configuration");
                final String[] daytimeLight = new String[7];
                daytimeLight[0] = "Low Beam";
                daytimeLight[1] = "Fog Lights";
                daytimeLight[2] = "Dedicated LED";
                daytimeLight[3] = "Turn Signals";
                daytimeLight[4] = "Disabled";
                if (getDaytimeLights() == 1) {
                    select1.setText(daytimeLight[0]);
                    daytimeLightIndex = 0;
                } else if (getDaytimeLights() == 2) {
                    select1.setText(daytimeLight[1]);
                    daytimeLightIndex = 1;
                } else if (getDaytimeLights() == 3) {
                    select1.setText(daytimeLight[2]);
                    daytimeLightIndex = 2;
                } else if (getDaytimeLights() == 4) {
                    select1.setText(daytimeLight[3]);
                    daytimeLightIndex = 3;
                } else if (getDaytimeLights() == 5) {
                    select1.setText(daytimeLight[4]);
                    daytimeLightIndex = 4;
                }
                arrowLeft1.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View mView) {
                        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                        SharedPreferences.Editor edit = mSharedPreferences.edit();
                        if (daytimeLightIndex > 0 && daytimeLightIndex <= 4) {
                            daytimeLightIndex = daytimeLightIndex - 1;
                            select1.setText(daytimeLight[daytimeLightIndex]);
                            if (daytimeLightIndex == 0) {
                                edit.putInt("daytime_lights", 1);
                            } else if (daytimeLightIndex == 1) {
                                edit.putInt("daytime_lights", 2);
                            } else if (daytimeLightIndex == 2) {
                                edit.putInt("daytime_lights", 3);
                            } else if (daytimeLightIndex == 3) {
                                edit.putInt("daytime_lights", 4);
                            } else if (daytimeLightIndex == 4) {
                                edit.putInt("daytime_lights", 5);
                            }
                            edit.apply();
                        }
                    }
                });
                arrowRight1.setOnClickListener(new View.OnClickListener() {


                    @Override
                    public void onClick(View mView) {
                        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                        SharedPreferences.Editor edit = mSharedPreferences.edit();
                        if (daytimeLightIndex >= 0 && daytimeLightIndex < 4) {
                            daytimeLightIndex = daytimeLightIndex + 1;
                            select1.setText(daytimeLight[daytimeLightIndex]);
                            if (daytimeLightIndex == 0) {
                                edit.putInt("daytime_lights", 1);
                            } else if (daytimeLightIndex == 1) {
                                edit.putInt("daytime_lights", 2);
                            } else if (daytimeLightIndex == 2) {
                                edit.putInt("daytime_lights", 3);
                            } else if (daytimeLightIndex == 3) {
                                edit.putInt("daytime_lights", 4);
                            } else if (daytimeLightIndex == 4) {
                                edit.putInt("daytime_lights", 5);
                            }
                            edit.apply();
                        }
                    }
                });


                //Selector 2
                selector_words_second_2.setText("Remote Start Duration");
                final String[] remoteStart = new String[7];
                remoteStart[0] = "5 Minutes";
                remoteStart[1] = "10 Minutes";
                remoteStart[2] = "15 Minutes";
                if (getRemoteStart() == 1) {
                    select2.setText(remoteStart[0]);
                    remoteStartIndex = 0;
                } else if (getRemoteStart() == 2) {
                    select2.setText(remoteStart[1]);
                    remoteStartIndex = 1;
                } else if (getRemoteStart() == 3) {
                    select2.setText(remoteStart[2]);
                    remoteStartIndex = 2;
                }
                arrowLeft2.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View mView) {
                        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                        SharedPreferences.Editor edit = mSharedPreferences.edit();
                        if (remoteStartIndex > 0 && remoteStartIndex <= 2) {
                            remoteStartIndex = remoteStartIndex - 1;
                            select2.setText(remoteStart[remoteStartIndex]);
                            if (remoteStartIndex == 0) {
                                edit.putInt("remote_start", 1);
                            } else if (remoteStartIndex == 1) {
                                edit.putInt("remote_start", 2);
                            } else if (remoteStartIndex == 2) {
                                edit.putInt("remote_start", 3);
                            }
                            edit.apply();
                        }
                    }
                });
                arrowRight2.setOnClickListener(new View.OnClickListener() {


                    @Override
                    public void onClick(View mView) {
                        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                        SharedPreferences.Editor edit = mSharedPreferences.edit();
                        if (remoteStartIndex >= 0 && remoteStartIndex < 2) {
                            remoteStartIndex = remoteStartIndex + 1;
                            select2.setText(remoteStart[remoteStartIndex]);
                            if (remoteStartIndex == 0) {
                                edit.putInt("remote_start", 1);
                            } else if (remoteStartIndex == 1) {
                                edit.putInt("remote_start", 2);
                            } else if (remoteStartIndex == 2) {
                                edit.putInt("remote_start", 3);
                            }
                            edit.apply();
                        }
                    }
                });

                //Selector 3
                selector_words_third_2.setText("Navigation Override(Allows destination entry while driving)");
                final String[] navOverride = new String[2];
                navOverride[0] = "No";
                navOverride[1] = "Yes";
                if (!isNavOverride()) {
                    select3.setText(navOverride[0]);
                } else if (isNavOverride()) {
                    select3.setText(navOverride[1]);
                }
                arrowLeft3.setOnClickListener(new View.OnClickListener() {
                    SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                    SharedPreferences.Editor edit = mSharedPreferences.edit();

                    @Override
                    public void onClick(View mView) {
                        select3.setText(navOverride[0]);
                        edit.putBoolean("nav_override", true);
                        edit.apply();
                    }
                });
                arrowRight3.setOnClickListener(new View.OnClickListener() {
                    SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                    SharedPreferences.Editor edit = mSharedPreferences.edit();

                    @Override
                    public void onClick(View mView) {
                        select3.setText(navOverride[1]);
                        edit.putBoolean("nav_override", false);
                        edit.apply();
                    }
                });

                //Selector 4
                selector_words_fourth_2.setText("Windows up/down with remote keyfob");
                final String[] remoteWindow = new String[2];
                remoteWindow[0] = "No";
                remoteWindow[1] = "Yes";
                if (!isRemoteWindow()) {
                    select4.setText(remoteWindow[0]);
                } else if (isRemoteWindow()) {
                    select4.setText(remoteWindow[1]);
                }
                arrowLeft4.setOnClickListener(new View.OnClickListener() {
                    SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                    SharedPreferences.Editor edit = mSharedPreferences.edit();

                    @Override
                    public void onClick(View mView) {
                        select4.setText(remoteWindow[0]);
                        edit.putBoolean("remote_window", true);
                        edit.apply();
                    }
                });
                arrowRight4.setOnClickListener(new View.OnClickListener() {
                    SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
                    SharedPreferences.Editor edit = mSharedPreferences.edit();

                    @Override
                    public void onClick(View mView) {
                        select4.setText(remoteWindow[1]);
                        edit.putBoolean("remote_window", false);
                        edit.apply();
                    }
                });
            }
        }


    }


    private int getVehicleType() {
        SharedPreferences mSharedPreferences = getActivity().getSharedPreferences("ThemeColor", MODE_PRIVATE);
        return mSharedPreferences.getInt("vehicle", VFORD1);
    }

    public int getDaytimeLights() {
        SharedPreferences mSharedPreferences = getActivity().getSharedPreferences("ThemeColor", MODE_PRIVATE);
        return mSharedPreferences.getInt("daytime_lights", 1);
    }

    public int getRemoteStart() {
        SharedPreferences mSharedPreferences = getActivity().getSharedPreferences("ThemeColor", MODE_PRIVATE);
        return mSharedPreferences.getInt("remote_start", 3);
    }

    public boolean isNavOverride() {
        SharedPreferences mSharedPreferences = getActivity().getSharedPreferences("ThemeColor", MODE_PRIVATE);
        return mSharedPreferences.getBoolean("nav_override", false);
    }

    public boolean isRemoteWindow() {
        SharedPreferences mSharedPreferences = getActivity().getSharedPreferences("ThemeColor", MODE_PRIVATE);
        return mSharedPreferences.getBoolean("remote_window", false);
    }

    public boolean isRead() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
        return mSharedPreferences.getBoolean("read_settings", false);
    }

}