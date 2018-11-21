package com.gdptuning.gdptuning;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
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
    boolean isConnected = false;
    boolean isProcessing = false;
    String device = "GDP";
    RequestQueue queue;
    Button btn_home, key_fob;
    ToggleButton toggle_high_idle;
    WifiManager wifi;
    TextView select1, select2, select3, select4, select5, selector_words_first_3, selector_words_second_3, selector_words_third_3, selector_words_fourth_3, selector_words_fifth_3;
    ImageView arrowRight1, arrowRight2, arrowRight3, arrowLeft1, arrowLeft2, arrowLeft3, arrowLeft4, arrowRight4, arrowLeft5, arrowRight5;
    Timer timer;
    private int strobeNum;
    private int workLightNum;
    private int aux1Num;
    private int aux2Num;
    private int aux3Num;
    private int highIdleNum;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_features3, container, false);

        //Id's
        select1 = mView.findViewById(R.id.selector1);
        select2 = mView.findViewById(R.id.selector2);
        select3 = mView.findViewById(R.id.selector3);
        select4 = mView.findViewById(R.id.selector4);
        select5 = mView.findViewById(R.id.selector5);
        selector_words_first_3 = mView.findViewById(R.id.first_selector_features_3);
        selector_words_second_3 = mView.findViewById(R.id.second_selector_features_3);
        selector_words_third_3 = mView.findViewById(R.id.third_selector_features_3);
        selector_words_fourth_3 = mView.findViewById(R.id.fourth_selector_features_3);
        selector_words_fifth_3 = mView.findViewById(R.id.fifth_selector_features_3);
        key_fob = mView.findViewById(R.id.key_fob);
        toggle_high_idle = mView.findViewById(R.id.high_idle);
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Working with wifi
        queue = VolleySingleton.getInstance(getContext()).getRequestQueue();
        wifi = (WifiManager) Objects.requireNonNull(getContext()).getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifi = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        key_fob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                if (getVehicleType() == VGM1 || getVehicleType() == VGM2) {
                    learnNewKeyFob();
                } else {
                    Toast mToast = Toast.makeText(getContext(), "Sorry, this feature currently only " +
                            "works for GM vehicles", Toast.LENGTH_SHORT);
                    mToast.show();
                }
            }
        });
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

        //Selector 1
        selector_words_first_3.setText("EXTERIOR STROBE LIGHT MODE");
        final String[] strobeLight = new String[2];
        strobeLight[0] = "OFF";
        strobeLight[1] = "ON";
        if (!isStrobeLight()) {
            select1.setText(strobeLight[0]);
        } else if (isStrobeLight()) {
            select1.setText(strobeLight[1]);
        }
        arrowLeft1 = Objects.requireNonNull(getView()).findViewById(R.id.arrowLeft);
        arrowLeft1.setOnClickListener(new View.OnClickListener() {
            SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = mSharedPreferences.edit();

            @Override
            public void onClick(View mView) {
                select1.setText(strobeLight[0]);
                edit.putBoolean(strobeSettings, true);
                switchStrobeLights(4);
                edit.apply();
            }
        });
        arrowRight1 = getView().findViewById(R.id.arrowRight);
        arrowRight1.setOnClickListener(new View.OnClickListener() {
            SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = mSharedPreferences.edit();

            @Override
            public void onClick(View mView) {
                select1.setText(strobeLight[1]);
                edit.putBoolean(strobeSettings, false);
                switchStrobeLights(5);
                edit.apply();
            }
        });

        //Selector 2
        selector_words_second_3.setText("WORK LIGHT MODE");
        final String[] workLight = new String[2];
        workLight[0] = "OFF";
        workLight[1] = "ON";
        if (!isWorkLight()) {
            select2.setText(workLight[0]);
        } else if (isWorkLight()) {
            select2.setText(workLight[1]);
        }
        arrowLeft2 = getView().findViewById(R.id.arrowLeft2);
        arrowLeft2.setOnClickListener(new View.OnClickListener() {
            SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = mSharedPreferences.edit();

            @Override
            public void onClick(View mView) {
                select2.setText(workLight[0]);
                edit.putBoolean(workLightSettings, true);
                switchWorkLight(49);
                edit.apply();
            }
        });
        arrowRight2 = getView().findViewById(R.id.arrowRight2);
        arrowRight2.setOnClickListener(new View.OnClickListener() {
            SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = mSharedPreferences.edit();

            @Override
            public void onClick(View mView) {
                select2.setText(workLight[1]);
                edit.putBoolean(workLightSettings, false);
                switchWorkLight(50);
                edit.apply();
            }
        });

        //Selector 3
        selector_words_third_3.setText("AUXILLARY OUTPUT 1");
        final String[] aux1 = new String[2];
        aux1[0] = "OFF";
        aux1[1] = "ON";
        if (!isAux1()) {
            select3.setText(aux1[0]);
        } else if (isAux1()) {
            select3.setText(aux1[1]);
        }
        arrowLeft3 = getView().findViewById(R.id.arrowLeft3);
        arrowLeft3.setOnClickListener(new View.OnClickListener() {
            SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = mSharedPreferences.edit();

            @Override
            public void onClick(View mView) {
                select3.setText(aux1[0]);
                edit.putBoolean(aux1Settings, true);
                switchAux1(53);
                edit.apply();
            }
        });
        arrowRight3 = getView().findViewById(R.id.arrowRight3);
        arrowRight3.setOnClickListener(new View.OnClickListener() {
            SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = mSharedPreferences.edit();

            @Override
            public void onClick(View mView) {
                select3.setText(aux1[1]);
                edit.putBoolean(aux1Settings, false);
                switchAux1(54);
                edit.apply();
            }
        });

        //Selector 4
        selector_words_fourth_3.setText("AUXILLARY OUTPUT 2");
        final String[] aux2 = new String[2];
        aux2[0] = "OFF";
        aux2[1] = "ON";
        if (!isAux2()) {
            select4.setText(aux2[0]);
        } else if (isAux2()) {
            select4.setText(aux2[1]);
        }
        arrowLeft4 = getView().findViewById(R.id.arrowLeft4);
        arrowLeft4.setOnClickListener(new View.OnClickListener() {
            SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = mSharedPreferences.edit();

            @Override
            public void onClick(View mView) {
                select4.setText(aux2[0]);
                edit.putBoolean(aux2Settings, true);
                switchAux2(55);
                edit.apply();
            }
        });
        arrowRight4 = getView().findViewById(R.id.arrowRight4);
        arrowRight4.setOnClickListener(new View.OnClickListener() {
            SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = mSharedPreferences.edit();

            @Override
            public void onClick(View mView) {
                select4.setText(aux2[1]);
                edit.putBoolean(aux2Settings, false);
                switchAux2(56);
                edit.apply();
            }
        });

        //Selector 5
        selector_words_fifth_3.setText("AUXILLARY OUTPUT 3");
        final String[] aux3 = new String[2];
        aux3[0] = "OFF";
        aux3[1] = "ON";
        if (!isAux3()) {
            select5.setText(aux3[0]);
        } else if (isAux3()) {
            select5.setText(aux3[1]);
        }
        arrowLeft5 = getView().findViewById(R.id.arrowLeft5);
        arrowLeft5.setOnClickListener(new View.OnClickListener() {
            SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = mSharedPreferences.edit();

            @Override
            public void onClick(View mView) {
                select5.setText(aux3[0]);
                edit.putBoolean(aux3Settings, true);
                switchAux3(57);
                edit.apply();
            }
        });
        arrowRight5 = getView().findViewById(R.id.arrowRight5);
        arrowRight5.setOnClickListener(new View.OnClickListener() {
            SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = mSharedPreferences.edit();

            @Override
            public void onClick(View mView) {
                select5.setText(aux3[1]);
                edit.putBoolean(aux3Settings, false);
                switchAux3(58);
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

    public boolean isHighIdle() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(themeColor, MODE_PRIVATE);
        return mSharedPreferences.getBoolean(highIdleSettings, false);
    }

    private int getVehicleType() {
        SharedPreferences mSharedPreferences = getActivity().getSharedPreferences(themeColor, Context.MODE_PRIVATE);
        return mSharedPreferences.getInt(vehicleSettings, VFORD1);
    }

    //Send to sGDP server to verify connection
    void learnNewKeyFob() {
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url + "/diag_functions?params=" + 44, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        isConnected = true;
                        Toast mToast = Toast.makeText(getContext(), "Message sent to learn new key fob", Toast.LENGTH_SHORT);
                        mToast.show();
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