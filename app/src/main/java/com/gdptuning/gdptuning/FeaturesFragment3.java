package com.gdptuning.gdptuning;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.MODE_PRIVATE;


public class FeaturesFragment3 extends Fragment {

    private static int VFORD1 = 7;
    private static int VFORD2 = 8;
    private static int VGM1 = 9;
    private static int VGM2 = 10;
    private static int VRAM = 11;
    //ESP32 aREST server address
    final String url = "http://192.168.7.1";
    final String themeColor = "ThemeColor";
    final String vehicleSettings = "vehicle";
    final String strobeSettings = "strobe_lights";
    final String workLightSettings = "work_lights";
    final String aux1Settings = "aux1_var";
    final String aux2Settings = "aux2_var";
    final String aux3Settings = "aux3_var";
    final String highIdleSettings = "high_idle";
    final String secureIdleSettings = "secure_idle";
    boolean isConnected = false;
    boolean isProcessing = false;
    String device = "GDP";
    RequestQueue queue;
    ToggleButton toggle_high_idle, toggle_secure_idle;
    WifiManager wifi;
    TextView selector_words_first_3, selector_words_second_3, selector_words_third_3, selector_words_fourth_3, selector_words_fifth_3;
    Button strobeOn, workLightOn, aux1On, strobeOff, workLightOff, aux1Off, aux2Off, aux2On, aux3Off, aux3On;
    Timer timer;
    private int strobeNum;
    private int workLightNum;
    private int aux1Num;
    private int aux2Num;
    private int aux3Num;
    private int highIdleNum;
    private int secureIdleNum;
    String mTag = "TEST";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_features3, container, false);

        //Id's
        selector_words_first_3 = mView.findViewById(R.id.first_selector_features_3);
        selector_words_second_3 = mView.findViewById(R.id.second_selector_features_3);
        selector_words_third_3 = mView.findViewById(R.id.third_selector_features_3);
        selector_words_fourth_3 = mView.findViewById(R.id.fourth_selector_features_3);
        selector_words_fifth_3 = mView.findViewById(R.id.fifth_selector_features_3);
        toggle_high_idle = mView.findViewById(R.id.high_idle);
        toggle_secure_idle = mView.findViewById(R.id.secure_idle);

        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Working with wifi
        queue = VolleySingleton.getInstance(getContext()).getRequestQueue();
        wifi = (WifiManager) Objects.requireNonNull(getContext()).getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifi = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        strobeOn = Objects.requireNonNull(getView()).findViewById(R.id.strobe_on);
        workLightOff = getView().findViewById(R.id.work_light_off);
        workLightOn = getView().findViewById(R.id.work_light_on);
        aux1Off = getView().findViewById(R.id.aux1_off);
        aux1On = getView().findViewById(R.id.aux1_on);
        aux2Off = getView().findViewById(R.id.aux2_off);
        aux2On = getView().findViewById(R.id.aux2_on);
        aux3Off = getView().findViewById(R.id.aux3_off);
        aux3On = getView().findViewById(R.id.aux3_on);
        strobeOff = Objects.requireNonNull(getView()).findViewById(R.id.strobe_off);
        if (getVehicleType() == VFORD2){
            toggle_secure_idle.setVisibility(View.VISIBLE);
        } else {
            toggle_secure_idle.setVisibility(View.INVISIBLE);
            toggle_high_idle.setX(-120);
        }

        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putBoolean(highIdleSettings, false);
        edit.putBoolean(secureIdleSettings, false);
        edit.putBoolean(workLightSettings, false);
        edit.putBoolean(strobeSettings, false);
        edit.apply();

        if (isHighIdle()) {
            toggle_high_idle.setChecked(true);
        } else {
            toggle_high_idle.setChecked(false);
        }
        if (isSecureIdle()) {
            toggle_secure_idle.setChecked(true);
        } else {
            toggle_secure_idle.setChecked(false);
        }
        toggle_high_idle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton mCompoundButton, boolean mB) {
                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = mSharedPreferences.edit();
                if (mB) {
                    edit.putBoolean(highIdleSettings, true);
                    switchHighIdle(47);
                    edit.apply();
                } else {
                    edit.putBoolean(highIdleSettings, false);
                    switchHighIdle(48);
                    edit.apply();
                }
            }
        });

        toggle_secure_idle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton mCompoundButton, boolean mB) {
                SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = mSharedPreferences.edit();
                if (mB) {
                    edit.putBoolean(secureIdleSettings, true);
                    switchSecureIdle(51);
                    edit.apply();
                } else {
                    edit.putBoolean(secureIdleSettings, false);
                    switchSecureIdle(52);
                    edit.apply();
                }
            }
        });

        //Selector 1
        selector_words_first_3.setText("EXTERIOR STROBE LIGHT MODE");
        strobeOff.setOnClickListener(new View.OnClickListener() {
            SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = mSharedPreferences.edit();

            @Override
            public void onClick(View mView) {
                edit.putBoolean(strobeSettings, false);
                switchStrobeLights(4);
                edit.apply();
            }
        });

        strobeOn.setOnClickListener(new View.OnClickListener() {
            SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = mSharedPreferences.edit();

            @Override
            public void onClick(View mView) {
                edit.putBoolean(strobeSettings, true);
                switchStrobeLights(5);
                edit.apply();
            }
        });

        //Selector 2
        selector_words_second_3.setText("WORK LIGHT MODE");
        workLightOff.setOnClickListener(new View.OnClickListener() {
            SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = mSharedPreferences.edit();

            @Override
            public void onClick(View mView) {
                edit.putBoolean(workLightSettings, false);
                switchWorkLight(49);
                edit.apply();
            }
        });
        workLightOn.setOnClickListener(new View.OnClickListener() {
            SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = mSharedPreferences.edit();

            @Override
            public void onClick(View mView) {
                edit.putBoolean(workLightSettings, true);
                switchWorkLight(50);
                edit.apply();
            }
        });

        //Selector 3
        selector_words_third_3.setText("AUXILLARY OUTPUT 1");
        aux1Off.setOnClickListener(new View.OnClickListener() {
            SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = mSharedPreferences.edit();

            @Override
            public void onClick(View mView) {
                edit.putBoolean(aux1Settings, false);
                switchAux1(54);
                edit.apply();
            }
        });
        aux1On.setOnClickListener(new View.OnClickListener() {
            SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = mSharedPreferences.edit();

            @Override
            public void onClick(View mView) {
                edit.putBoolean(aux1Settings, true);
                switchAux1(53);
                edit.apply();
            }
        });

        //Selector 4
        selector_words_fourth_3.setText("AUXILLARY OUTPUT 2");
        aux2Off.setOnClickListener(new View.OnClickListener() {
            SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = mSharedPreferences.edit();

            @Override
            public void onClick(View mView) {
                edit.putBoolean(aux2Settings, false);
                switchAux2(56);
                edit.apply();
            }
        });
        aux2On.setOnClickListener(new View.OnClickListener() {
            SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = mSharedPreferences.edit();

            @Override
            public void onClick(View mView) {
                edit.putBoolean(aux2Settings, true);
                switchAux2(55);
                edit.apply();
            }
        });

        //Selector 5
        selector_words_fifth_3.setText("AUXILLARY OUTPUT 3");
        aux3Off.setOnClickListener(new View.OnClickListener() {
            SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = mSharedPreferences.edit();

            @Override
            public void onClick(View mView) {
                edit.putBoolean(aux3Settings, false);
                switchAux3(58);
                edit.apply();
            }
        });
        aux3On.setOnClickListener(new View.OnClickListener() {
            SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = mSharedPreferences.edit();

            @Override
            public void onClick(View mView) {
                edit.putBoolean(aux3Settings, true);
                switchAux3(57);
                edit.apply();
            }
        });
    }

    public boolean isAux1() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
        return mSharedPreferences.getBoolean(aux1Settings, false);
    }

    public boolean isAux2() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
        return mSharedPreferences.getBoolean(aux2Settings, false);
    }

    public boolean isAux3() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
        return mSharedPreferences.getBoolean(aux3Settings, false);
    }

    public boolean isWorkLight() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
        return mSharedPreferences.getBoolean(workLightSettings, false);
    }

    public boolean isStrobeLight() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
        return mSharedPreferences.getBoolean(strobeSettings, false);
    }

    private int getVehicleType() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, Context.MODE_PRIVATE);
        return mSharedPreferences.getInt(vehicleSettings, VFORD1);
    }

    public boolean isHighIdle() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
        return mSharedPreferences.getBoolean(highIdleSettings, false);
    }

    public boolean isSecureIdle() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
        return mSharedPreferences.getBoolean(secureIdleSettings, false);
    }
//
//    void updateButtons(){
//        if (isStrobeLight()){
//            strobeOff.setPressed(true);
//        } else {
//            strobeOn.setPressed(true);
//        }
//        if (isWorkLight()){
//            workLightOff.setPressed(true);
//        } else {
//            workLightOn.setPressed(true);
//        }
//        if (isAux1()){
//            aux1Off.setPressed(true);
//        } else {
//            aux1On.setPressed(true);
//        }
//        if (isAux2()){
//            aux2Off.setPressed(true);
//        } else {
//            aux2On.setPressed(true);
//        }
//        if (isAux3()){
//            aux3Off.setPressed(true);
//        } else {
//            aux3On.setPressed(true);
//        }
//    }

    //Send to sGDP server to verify connection
    void switchStrobeLights(int requestTurnSignals) {
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url + "/diag_functions?params=" + requestTurnSignals, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        isConnected = true;
                        try {
                            strobeNum = response.getInt("return_value");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        SharedPreferences readSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
                        SharedPreferences.Editor edit = readSharedPreferences.edit();
                        switch (strobeNum) {
                            // Nav Entry not allowed while driving
                            case 5:
                                edit.putBoolean(strobeSettings, false);
                                edit.apply();
                                break;
                            // Nav Entry allowed while driving
                            case 4:
                                edit.putBoolean(strobeSettings, true);
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
    void switchWorkLight(int requestTurnSignals) {
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url + "/diag_functions?params=" + requestTurnSignals, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        isConnected = true;
                        try {
                            workLightNum = response.getInt("return_value");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        SharedPreferences readSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
                        SharedPreferences.Editor edit = readSharedPreferences.edit();
                        switch (workLightNum) {
                            // Nav Entry not allowed while driving
                            case 50:
                                edit.putBoolean(workLightSettings, false);
                                edit.apply();
                                break;
                            // Nav Entry allowed while driving
                            case 49:
                                edit.putBoolean(workLightSettings, true);
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
    void switchAux1(int requestTurnSignals) {
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url + "/diag_functions?params=" + requestTurnSignals, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        isConnected = true;
                        try {
                            aux1Num = response.getInt("return_value");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        SharedPreferences readSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
                        SharedPreferences.Editor edit = readSharedPreferences.edit();
                        switch (aux1Num) {
                            // Nav Entry not allowed while driving
                            case 54:
                                edit.putBoolean(aux1Settings, false);
                                edit.apply();
                                break;
                            // Nav Entry allowed while driving
                            case 53:
                                edit.putBoolean(aux1Settings, true);
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
    void switchAux2(int requestTurnSignals) {
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url + "/diag_functions?params=" + requestTurnSignals, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        isConnected = true;
                        try {
                            aux2Num = response.getInt("return_value");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        SharedPreferences readSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
                        SharedPreferences.Editor edit = readSharedPreferences.edit();
                        switch (aux2Num) {
                            // Nav Entry not allowed while driving
                            case 56:
                                edit.putBoolean(aux2Settings, false);
                                edit.apply();
                                break;
                            // Nav Entry allowed while driving
                            case 55:
                                edit.putBoolean(aux2Settings, true);
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
    void switchAux3(int requestTurnSignals) {
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url + "/diag_functions?params=" + requestTurnSignals, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        isConnected = true;
                        try {
                            aux3Num = response.getInt("return_value");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        SharedPreferences readSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
                        SharedPreferences.Editor edit = readSharedPreferences.edit();
                        switch (aux3Num) {
                            // Nav Entry not allowed while driving
                            case 58:
                                edit.putBoolean(aux3Settings, false);
                                edit.apply();
                                break;
                            // Nav Entry allowed while driving
                            case 57:
                                edit.putBoolean(aux3Settings, true);
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
    void switchHighIdle(int requestTurnSignals) {
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url + "/diag_functions?params=" + requestTurnSignals, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        isConnected = true;
                        try {
                            highIdleNum = response.getInt("return_value");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        SharedPreferences readSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
                        SharedPreferences.Editor edit = readSharedPreferences.edit();
                        switch (highIdleNum) {
                            // Nav Entry not allowed while driving
                            case 48:
                                edit.putBoolean(highIdleSettings, false);
                                edit.apply();
                                break;
                            // Nav Entry allowed while driving
                            case 47:
                                edit.putBoolean(highIdleSettings, true);
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
    void switchSecureIdle(int requestSecureIdle) {
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url + "/diag_functions?params=" + requestSecureIdle, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        isConnected = true;
                        try {
                            secureIdleNum = response.getInt("return_value");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        SharedPreferences readSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
                        SharedPreferences.Editor edit = readSharedPreferences.edit();
                        switch (secureIdleNum) {
                            // Nav Entry not allowed while driving
                            case 52:
                                edit.putBoolean(secureIdleSettings, false);
                                edit.apply();
                                break;
                            // Nav Entry allowed while driving
                            case 51:
                                edit.putBoolean(secureIdleSettings, true);
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