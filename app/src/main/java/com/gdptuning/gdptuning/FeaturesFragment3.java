package com.gdptuning.gdptuning;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;

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
    boolean isConnected = false;
    boolean isProcessing = false;
    String device = "GDP";
    RequestQueue queue;
    Button btn_home, key_fob, high_idle;
    WifiManager wifi;
    TextView select1, select2, select3, select4, select5, selector_words_first_3, selector_words_second_3, selector_words_third_3, selector_words_fourth_3, selector_words_fifth_3;
    ImageView arrowRight1, arrowRight2, arrowRight3, arrowLeft1, arrowLeft2, arrowLeft3, arrowLeft4, arrowRight4, arrowLeft5, arrowRight5;
    Timer timer;


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

        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


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
        arrowLeft1 = getView().findViewById(R.id.arrowLeft);
        arrowLeft1.setOnClickListener(new View.OnClickListener() {
            SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = mSharedPreferences.edit();

            @Override
            public void onClick(View mView) {
                select1.setText(strobeLight[0]);
                edit.putBoolean("strobe_light", true);
                edit.apply();
            }
        });
        arrowRight1 = getView().findViewById(R.id.arrowRight);
        arrowRight1.setOnClickListener(new View.OnClickListener() {
            SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = mSharedPreferences.edit();

            @Override
            public void onClick(View mView) {
                select1.setText(strobeLight[1]);
                edit.putBoolean("strobe_light", false);
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
            SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = mSharedPreferences.edit();

            @Override
            public void onClick(View mView) {
                select2.setText(workLight[0]);
                edit.putBoolean("work_light", true);
                edit.apply();
            }
        });
        arrowRight2 = getView().findViewById(R.id.arrowRight2);
        arrowRight2.setOnClickListener(new View.OnClickListener() {
            SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = mSharedPreferences.edit();

            @Override
            public void onClick(View mView) {
                select2.setText(workLight[1]);
                edit.putBoolean("work_light", false);
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
            SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = mSharedPreferences.edit();

            @Override
            public void onClick(View mView) {
                select3.setText(aux1[0]);
                edit.putBoolean("aux1", true);
                edit.apply();
            }
        });
        arrowRight3 = getView().findViewById(R.id.arrowRight3);
        arrowRight3.setOnClickListener(new View.OnClickListener() {
            SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = mSharedPreferences.edit();

            @Override
            public void onClick(View mView) {
                select3.setText(aux1[1]);
                edit.putBoolean("aux1", false);
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
            SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = mSharedPreferences.edit();

            @Override
            public void onClick(View mView) {
                select4.setText(aux2[0]);
                edit.putBoolean("aux2", true);
                edit.apply();
            }
        });
        arrowRight4 = getView().findViewById(R.id.arrowRight4);
        arrowRight4.setOnClickListener(new View.OnClickListener() {
            SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = mSharedPreferences.edit();

            @Override
            public void onClick(View mView) {
                select4.setText(aux2[1]);
                edit.putBoolean("aux2", false);
                edit.apply();
            }
        });

        //Selector 4
        selector_words_fourth_3.setText("AUXILLARY OUTPUT 3");
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
            SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = mSharedPreferences.edit();

            @Override
            public void onClick(View mView) {
                select5.setText(aux3[0]);
                edit.putBoolean("aux3", true);
                edit.apply();
            }
        });
        arrowRight5 = getView().findViewById(R.id.arrowRight5);
        arrowRight5.setOnClickListener(new View.OnClickListener() {
            SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = mSharedPreferences.edit();

            @Override
            public void onClick(View mView) {
                select5.setText(aux3[1]);
                edit.putBoolean("aux3", false);
                edit.apply();
            }
        });
    }

    public int getDaytimeLights() {
        SharedPreferences mSharedPreferences = getActivity().getSharedPreferences("ThemeColor", MODE_PRIVATE);
        return mSharedPreferences.getInt("daytime_lights", 1);
    }

    public int getRemoteStart() {
        SharedPreferences mSharedPreferences = getActivity().getSharedPreferences("ThemeColor", MODE_PRIVATE);
        return mSharedPreferences.getInt("remote_start", 3);
    }

    public boolean isAux1() {
        SharedPreferences mSharedPreferences = getActivity().getSharedPreferences("ThemeColor", MODE_PRIVATE);
        return mSharedPreferences.getBoolean("aux1", false);
    }

    public boolean isAux2() {
        SharedPreferences mSharedPreferences = getActivity().getSharedPreferences("ThemeColor", MODE_PRIVATE);
        return mSharedPreferences.getBoolean("aux2", false);
    }

    public boolean isAux3() {
        SharedPreferences mSharedPreferences = getActivity().getSharedPreferences("ThemeColor", MODE_PRIVATE);
        return mSharedPreferences.getBoolean("aux3", false);
    }

    public boolean isWorkLight() {
        SharedPreferences mSharedPreferences = getActivity().getSharedPreferences("ThemeColor", MODE_PRIVATE);
        return mSharedPreferences.getBoolean("work_light", false);
    }

    public boolean isStrobeLight() {
        SharedPreferences mSharedPreferences = getActivity().getSharedPreferences("ThemeColor", MODE_PRIVATE);
        return mSharedPreferences.getBoolean("strobe_light", false);
    }

}