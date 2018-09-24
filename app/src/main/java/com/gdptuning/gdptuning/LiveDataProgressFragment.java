package com.gdptuning.gdptuning;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.anastr.speedviewlib.ImageLinearGauge;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import de.nitri.gauge.Gauge;

import static android.content.Context.MODE_PRIVATE;

public class LiveDataProgressFragment extends Fragment {

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
    String faren = "℉";
    String cels = "\u2103";
    Timer timer;
    TextView tvBoostView, tvEgt, tvOilPressure, tvFuel, tvTurbo, tvCoolant;
    RequestQueue queue;
    WifiManager wifi;
    Button btn_home;

    //Gauges
    Gauge gauge1;
    Gauge gauge2;
    Gauge gauge3;
    Gauge gauge4;
    Gauge gauge5;
    Gauge gauge6;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_livedata_progress, container, false);

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

        //set widget home
        btn_home = getView().findViewById(R.id.btn_home);
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                Intent i = new Intent(getActivity(), MainActivity.class);
                startActivity(i);
            }
        });


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
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", MODE_PRIVATE);
        return mSharedPreferences.getInt("vehicle", VFORD1);
    }

    private boolean isMetric() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", MODE_PRIVATE);
        return mSharedPreferences.getBoolean("metric", false);
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

                        new SweetAlertDialog(Objects.requireNonNull(getActivity()), SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("No Connection")
                                .setContentText("Your are not connected to a GDP device. Retry by " +
                                        "tapping 'Retry' or check your wifi settings by tapping " +
                                        "'Connect'.")
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

                            String deviceName = response.getString("name");
                            deviceName += response.getString("id");
                            device = deviceName;

                            float egt = variables.getInt("egt");
                            float boost = variables.getInt("boost");
                            float turbo = variables.getInt("turbo");
                            float fuel = variables.getInt("fule");
                            float coolant = variables.getInt("coolant");

                            if (getVehicleType() == VFORD1 || getVehicleType() == VFORD2) {
                                TextView oilText = Objects.requireNonNull(getView()).findViewById(R.id.title4);
                                oilText.setText("Oil \nTemp");
                                float fordOilTemp = variables.getInt("oil_temp");
                                //Gauge1
                                ImageLinearGauge imageLinearGauge1 = getView().findViewById(R.id.imageLinearGauge1);
                                imageLinearGauge1.speedTo((float) ((egt * 1.8) + 32));

                                //Gauge2
                                ImageLinearGauge imageLinearGauge2 = getView().findViewById(R.id.imageLinearGauge2);
                                if (boost > 5) {
                                    imageLinearGauge2.speedTo((float) (boost * 0.1450377));
                                } else {
                                    imageLinearGauge2.speedTo(0);
                                }

                                //Gauge3
                                ImageLinearGauge imageLinearGauge3 = getView().findViewById(R.id.imageLinearGauge3);
                                imageLinearGauge3.speedTo(turbo);

                                //Gauge4
                                ImageLinearGauge imageLinearGauge4 = getView().findViewById(R.id.imageLinearGauge4);
                                imageLinearGauge4.speedTo((float) ((fordOilTemp * 1.8) + 32));

                                //Gauge5
                                ImageLinearGauge imageLinearGauge5 = getView().findViewById(R.id.imageLinearGauge5);
                                imageLinearGauge5.speedTo(fuel);

                                //Gauge6
                                ImageLinearGauge imageLinearGauge6 = getView().findViewById(R.id.imageLinearGauge6);
                                imageLinearGauge6.speedTo((float) ((coolant * 1.8) + 32));
                            } else if (getVehicleType() == VGM1 || getVehicleType() == VGM2 || getVehicleType() == VRAM) { //Gauge1
                                TextView oilText = Objects.requireNonNull(getView()).findViewById(R.id.title4);
                                oilText.setText("Oil \nPressure");
                                float oilPressure = variables.getInt("oil_pressur");
                                final ImageLinearGauge imageLinearGauge1 = getView().findViewById(R.id.imageLinearGauge1);
                                imageLinearGauge1.speedTo((float) ((egt * 1.8) + 32));

                                //Gauge2
                                ImageLinearGauge imageLinearGauge2 = getView().findViewById(R.id.imageLinearGauge2);
                                if (boost > 5) {
                                    imageLinearGauge2.speedTo((float) (boost * 0.1450377));
                                } else {
                                    imageLinearGauge2.speedTo(0);
                                }

                                //Gauge3
                                ImageLinearGauge imageLinearGauge3 = getView().findViewById(R.id.imageLinearGauge3);
                                imageLinearGauge3.speedTo(turbo);

                                //Gauge4
                                ImageLinearGauge imageLinearGauge4 = getView().findViewById(R.id.imageLinearGauge4);
                                imageLinearGauge4.speedTo((float) (oilPressure * 0.145));

                                //Gauge5
                                ImageLinearGauge imageLinearGauge5 = getView().findViewById(R.id.imageLinearGauge5);
                                imageLinearGauge5.speedTo(fuel);

                                //Gauge6
                                ImageLinearGauge imageLinearGauge6 = getView().findViewById(R.id.imageLinearGauge6);
                                imageLinearGauge6.speedTo((float) ((coolant * 1.8) + 32));
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

                        new SweetAlertDialog(Objects.requireNonNull(getActivity()), SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("No Connection")
                                .setContentText("Your are not connected to a GDP device. Retry by " +
                                        "tapping 'Retry' or check your wifi settings by tapping " +
                                        "'Connect'.")
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
                    .setContentText("Your are not connected to a GDP device. Retry by " +
                            "tapping 'Retry' or check your wifi settings by tapping " +
                            "'Connect'.")
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
