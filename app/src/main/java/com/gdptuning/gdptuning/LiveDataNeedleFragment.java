package com.gdptuning.gdptuning;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import de.nitri.gauge.Gauge;

import static android.content.Context.MODE_PRIVATE;


public class LiveDataNeedleFragment extends Fragment {

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
    TextView tvBoostView, tvEgt, tvOilPressure, tvFuel, tvTurbo, tvCoolant;
    RequestQueue queue;
    WifiManager wifi;
    //Gauges
    Gauge gauge1;
    Gauge gauge2;
    Gauge gauge3;
    Gauge gauge4;
    Gauge gauge5;
    Gauge gauge6;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_livedata_needle, container, false);

        //connect textViews
        tvEgt = mView.findViewById(R.id.egt);
        tvBoostView = mView.findViewById(R.id.boost);
        tvTurbo = mView.findViewById(R.id.turbo);
        tvOilPressure = mView.findViewById(R.id.oil_pressure);
        tvFuel = mView.findViewById(R.id.fuel_rate);
        tvCoolant = mView.findViewById(R.id.coolant);


        //Gauges information
        gauge1 = mView.findViewById(R.id.gauge1);
        gauge2 = mView.findViewById(R.id.gauge2);
        gauge3 = mView.findViewById(R.id.gauge3);
        gauge4 = mView.findViewById(R.id.gauge4);
        gauge5 = mView.findViewById(R.id.gauge5);
        gauge6 = mView.findViewById(R.id.gauge6);

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
            int num = 1;

            @Override
            public void run() {
                if (isConnected) {
                    if (!isProcessing) {
                        Log.d("TEST2 :", "Sending request");
                        updateRequest();
                    }
                }
            }
        }, 0, 1);//put here time 1000 milliseconds=1 second
    }

    public void onBackPressed() {
        Intent i = new Intent(getActivity(), MainActivity.class);
        startActivity(i);
    }

    @Override
    public void onPause() {
        super.onPause();
        timer.cancel();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private int getVehicleType() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", MODE_PRIVATE);
        return mSharedPreferences.getInt("vehicle", VFORD1);
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

                        new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("No Connection")
                                .setContentText("You are not connected to a GDP device")
                                .setCancelText("Retry")
                                .setConfirmText("Connect")
                                .showCancelButton(true)
                                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismiss();
                                    }
                                })
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                                    }
                                }).show();
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
                            int tuneMode = variables.getInt("tune_mode");
                            int gear = variables.getInt("gear");
                            String deviceName = response.getString("name");
                            deviceName += response.getString("id");
                            device = deviceName;

                            int egt = variables.getInt("egt");
                            int boost = variables.getInt("boost");
                            int turbo = variables.getInt("egt");
                            int fuel = variables.getInt("fule");
                            int coolant = variables.getInt("coolant");
                            int egtText = (int) (egt * 1.8 + 32);
                            int boostText = (int) ((boost * 0.1450377));
                            int coolantText = (int) (coolant * 1.8 + 32);

                            if (getVehicleType() == VFORD1 || getVehicleType() == VFORD2) {
                                TextView oilText = Objects.requireNonNull(getView()).findViewById(R.id.title4);
                                oilText.setText("Oil \nTemp");
                                int fordOilTemp = variables.getInt("oil_temp");
                                int fordOilText = (int) (fordOilTemp * 0.145);
                                //Set the text on the gauges
                                tvEgt.setText(String.valueOf(egtText + "\u2109"));
                                tvBoostView.setText(String.valueOf(boostText + " psi"));
                                tvTurbo.setText(String.valueOf(turbo + " %"));
                                tvOilPressure.setText(String.valueOf(fordOilText + " psi"));
                                tvFuel.setText(String.valueOf(fuel + " mm3"));
                                tvCoolant.setText(String.valueOf(coolantText + "\u2109"));

                                //Gauge 1
                                gauge1.setMajorNickInterval(10);
                                gauge1.setValuePerNick(20);
                                gauge1.setMinValue(0);
                                gauge1.setMaxValue(2000);
                                gauge1.setTotalNicks(120);
                                gauge1.setValue((float) ((egt * 1.8) + 32));

                                //Gauge 2
                                gauge2.setMajorNickInterval(5);
                                gauge2.setValuePerNick(1);
                                gauge2.setMinValue(0);
                                gauge2.setMaxValue(70);
                                gauge2.setTotalNicks(90);
                                if (boost > 5) {
                                    gauge2.setValue((float) (boost * 0.1450377));
                                } else {
                                    gauge2.setValue(0);
                                }

                                //Gauge 3
                                gauge3.setMajorNickInterval(10);
                                gauge3.setValuePerNick(1);
                                gauge3.setMinValue(0);
                                gauge3.setMaxValue(100);
                                gauge3.setTotalNicks(140);
                                gauge3.setValue(turbo);

                                //Gauge 4
                                gauge4.setMajorNickInterval(40);
                                gauge4.setValuePerNick(1);
                                gauge4.setMinValue(0);
                                gauge4.setMaxValue(350);
                                gauge4.setTotalNicks(520);
                                gauge4.setValue((float) (fordOilTemp * 0.145));

                                //Gauge 5
                                gauge5.setMajorNickInterval(20);
                                gauge5.setValuePerNick(1);
                                gauge5.setMinValue(0);
                                gauge5.setMaxValue(160);
                                gauge5.setTotalNicks(200);
                                gauge5.setValue(fuel);

                                //Gauge 6
                                gauge6.setMajorNickInterval(40);
                                gauge6.setValuePerNick(1);
                                gauge6.setMinValue(-40);
                                gauge6.setMaxValue(320);
                                gauge6.setTotalNicks(480);
                                gauge6.setValue((float) ((coolant * 1.8) + 32));


                            } else if (getVehicleType() == VGM1 || getVehicleType() == VGM2 || getVehicleType() == VRAM) {
                                TextView oilText = Objects.requireNonNull(getView()).findViewById(R.id.title4);
                                oilText.setText("Oil \nPressure");
                                int oilPressure = variables.getInt("oil_pressur");
                                int oilPressureText = (int) (oilPressure * 0.145);

                                //Set the text on the gauges
                                tvEgt.setText(String.valueOf(egtText + "\u2109"));
                                tvBoostView.setText(String.valueOf(boostText + " psi"));
                                tvTurbo.setText(String.valueOf(turbo + " %"));
                                tvOilPressure.setText(String.valueOf(oilPressureText + " psi"));
                                tvFuel.setText(String.valueOf(fuel + " mm3"));
                                tvCoolant.setText(String.valueOf(coolantText + "\u2109"));

                                //Gauge 1
                                gauge1.setMajorNickInterval(10);
                                gauge1.setValuePerNick(20);
                                gauge1.setMinValue(0);
                                gauge1.setMaxValue(2000);
                                gauge1.setTotalNicks(120);
                                gauge1.setValue((float) ((egt * 1.8) + 32));

                                //Gauge 2
                                gauge2.setMajorNickInterval(5);
                                gauge2.setValuePerNick(1);
                                gauge2.setMinValue(0);
                                gauge2.setMaxValue(70);
                                gauge2.setTotalNicks(90);
                                if (boost > 5) {
                                    gauge2.setValue((float) (boost * 0.1450377));
                                } else {
                                    gauge2.setValue(0);
                                }

                                //Gauge 3
                                gauge3.setMajorNickInterval(10);
                                gauge3.setValuePerNick(1);
                                gauge3.setMinValue(0);
                                gauge3.setMaxValue(100);
                                gauge3.setTotalNicks(140);
                                gauge3.setValue(turbo);

                                //Gauge 4
                                gauge4.setMajorNickInterval(40);
                                gauge4.setValuePerNick(1);
                                gauge4.setMinValue(0);
                                gauge4.setMaxValue(380);
                                gauge4.setTotalNicks(520);
                                gauge4.setValue((float) (oilPressure * 0.145));

                                //Gauge 5
                                gauge5.setMajorNickInterval(20);
                                gauge5.setValuePerNick(1);
                                gauge5.setMinValue(0);
                                gauge5.setMaxValue(160);
                                gauge5.setTotalNicks(200);
                                gauge5.setValue(fuel);

                                //Gauge 6
                                gauge6.setMajorNickInterval(40);
                                gauge6.setValuePerNick(1);
                                gauge6.setMinValue(-40);
                                gauge6.setMaxValue(320);
                                gauge6.setTotalNicks(480);
                                gauge6.setValue((float) ((coolant * 1.8) + 32));
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

                        new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("No Connection")
                                .setContentText("Your are not connected to GDP device")
                                .setCancelText("Retry")
                                .setConfirmText("Connect")
                                .showCancelButton(true)
                                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sendRequest();
                                        sDialog.dismiss();
                                    }
                                })
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                                    }
                                })
                                .show();

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
            new SweetAlertDialog(Objects.requireNonNull(getActivity()), SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("No Connection")
                    .setContentText("You are not connected to a GDP device")
                    .setCancelText("Retry")
                    .setConfirmText("Connect")
                    .showCancelButton(true)
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sendRequest();
                            sDialog.dismiss();
                        }
                    })
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    })
                    .show();
        }
    }
}
