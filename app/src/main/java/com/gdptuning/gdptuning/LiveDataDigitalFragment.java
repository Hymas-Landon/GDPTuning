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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

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
    ImageSpeedometer gauge1, gauge2, gauge3;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_livedata_digital, container, false);
        gauge1 = mView.findViewById(R.id.egt_temp);
        gauge2 = mView.findViewById(R.id.boost);
        gauge3 = mView.findViewById(R.id.oil_temp);

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

                            /*
                             * LIST OF VARIABLE FOR GAUGES
                             * "boost", "egt", "fule", "timing", "coolant", "turbo", "frp", "oil_pressur", "oil_temp"
                             * */
                            float boost = variables.getInt("boost");
                            float egt = variables.getInt("egt");
                            float fuel = variables.getInt("fule");
                            float timing = variables.getInt(("timing"));
                            float coolant = variables.getInt("coolant");
                            float turbo = variables.getInt("turbo");
                            float frp = variables.getInt("frp");

                            gauge2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View mView) {
                                    PopupMenu mPopupMenu = new PopupMenu(getActivity(), gauge2);
                                    mPopupMenu.getMenuInflater().inflate(R.menu.gauge_menu, mPopupMenu.getMenu());

                                    mPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                        @Override
                                        public boolean onMenuItemClick(MenuItem mMenuItem) {
                                            Toast.makeText(getActivity(), "" + mMenuItem.getTitle(), Toast.LENGTH_SHORT).show();
                                            return true;
                                        }
                                    });
                                    mPopupMenu.show();
                                }
                            });


                            ImageIndicator smallIndicator = new ImageIndicator(Objects.requireNonNull(getContext()), R.drawable.needle2);
                            ImageIndicator largeIndicator = new ImageIndicator(Objects.requireNonNull(getContext()), R.drawable.needle1);


                            if (getVehicleType() == VFORD1 || getVehicleType() == VFORD2) {
                                float fordOilTemp = variables.getInt("oil_temp");

                                // EGT Temp
                                ImageSpeedometer egtTemp = Objects.requireNonNull(getView()).findViewById(R.id.egt_temp);
                                egtTemp.setIndicator(smallIndicator);
                                egtTemp.speedTo(egt);

                                // Boost
                                ImageSpeedometer boost_perc = getView().findViewById(R.id.boost);
                                boost_perc.setIndicator(largeIndicator);
                                if (boost > 5) {
                                    boost_perc.speedTo((float) (boost * 0.1450377));
                                } else {
                                    boost_perc.speedTo(0);
                                }

                                // Ford oil temp
                                ImageSpeedometer oilTemp = getView().findViewById(R.id.oil_temp);
                                oilTemp.setIndicator(largeIndicator);
                                oilTemp.speedTo((float) (fordOilTemp * 0.145));

                            } else if (getVehicleType() == VGM1 || getVehicleType() == VGM2 || getVehicleType() == VRAM) { //Gauge1
                                float oil_pressure = variables.getInt("oil_pressur");

                                // Gauge 1
                                ImageSpeedometer egtTemp = Objects.requireNonNull(getView()).findViewById(R.id.egt_temp);
                                egtTemp.setIndicator(smallIndicator);
                                egtTemp.speedTo((float) ((egt * 1.8) + 32));

                                // Gauge 2
                                ImageSpeedometer boost_perc = getView().findViewById(R.id.boost);
                                boost_perc.setIndicator(largeIndicator);
                                if (boost > 5) {
                                    boost_perc.speedTo((float) (boost * 0.1450377));
                                } else {
                                    boost_perc.speedTo(0);
                                }

                                // Gauge 3
                                ImageSpeedometer oilPressure = getView().findViewById(R.id.oil_temp);
                                oilPressure.setIndicator(smallIndicator);
                                oilPressure.speedTo((float) (oil_pressure * 0.145));
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
