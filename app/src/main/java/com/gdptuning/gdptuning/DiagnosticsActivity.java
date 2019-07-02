package com.gdptuning.gdptuning;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class DiagnosticsActivity extends AppCompatActivity implements View.OnClickListener {

    //ESP32 aREST server address
    final String url = "http://192.168.7.1";
    boolean isConnected = false;
    boolean isProcessing = false;
    String device = "GDP";
    RequestQueue queue;
    Button btn_home, btn_read, btn_clear, btn_reset_trans;
    WifiManager wifi;
    TextView tvTune, tvGear;
    Timer timer;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter<DiagnosticsAdapter.DiagnosticsViewHolder> adapter;
    private ArrayList<Code> diagnosticsList = new ArrayList<Code>();
    ProgressDialog mProgressDialog;
    InputStream mInputStream;
    String[] data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getColorTheme() == Utils.THEME_DEFAULT) {
            setTheme(R.style.AppThemeNoActionBarOrangeMain);
        } else if (getColorTheme() == Utils.THEME_GREEN) {
            setTheme(R.style.AppThemeNoActionBarGreen);
        } else if (getColorTheme() == Utils.THEME_BLUE) {
            setTheme(R.style.AppThemeNoActionBarBlue);
        } else if (getColorTheme() == Utils.THEME_RED) {
            setTheme(R.style.AppThemeNoActionBarRed);
        }
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        setContentView(R.layout.activity_diagnostics);
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (isConnected) {
                    if (!isProcessing) {
                        updateSettingsRequest();
                    }
                }
            }
        }, 0, 500);//put here time 1000 milliseconds=1 second

        recyclerView = findViewById(R.id.recycler_codes);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLayoutManager);
        diagnosticsList = new ArrayList<Code>();
        queue = Volley.newRequestQueue(this);
        mProgressDialog = new ProgressDialog(this);

        //set home widget
        btn_read = findViewById(R.id.read_codes);
        btn_clear = findViewById(R.id.clear_codes);
        btn_reset_trans = findViewById(R.id.reset_trans);
        btn_home = findViewById(R.id.btn_home);
        tvGear = findViewById(R.id.gear_position);
        tvTune = findViewById(R.id.tunenum);

        //OnClickListener
        btn_home.setOnClickListener(this);
        btn_read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                // prepare the Request
                JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url + "/diag_functions?params=" + 1, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                new MyAsyncTaskCode(DiagnosticsActivity.this).execute();
                                isConnected = true;
                                sendRequest();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                isConnected = false;
                                Intent i = new Intent(DiagnosticsActivity.this, MainActivity.class);
                                startActivity(i);
                            }
                        }
                );
                // add it to the RequestQueue
                queue.add(getRequest);
            }
        });
        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                // prepare the Request
                JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url + "/diag_functions?params=" + 2, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                isConnected = true;
                                new MyAsyncTaskCode(DiagnosticsActivity.this).execute();
                                sendRequest();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                isConnected = false;
                                Intent i = new Intent(DiagnosticsActivity.this, MainActivity.class);
                                startActivity(i);   }
                        }
                );
                // add it to the RequestQueue
                queue.add(getRequest);
            }
        });
        btn_reset_trans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                // prepare the Request
                JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url + "/diag_functions?params=" + 3, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                isConnected = true;
                                new MyAsyncTaskCode(DiagnosticsActivity.this).execute();
                                sendRequest();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                isConnected = false;
                                Intent i = new Intent(DiagnosticsActivity.this, MainActivity.class);
                                startActivity(i);}
                        }
                );
                // add it to the RequestQueue
                queue.add(getRequest);
            }
        });

        mInputStream = getResources().openRawResource(R.raw.dtc_list);

        BufferedReader mReader = new BufferedReader(new InputStreamReader(mInputStream));
        try {
            String csvLine;
            while ((csvLine = mReader.readLine()) != null) {
                data = csvLine.split("-");
                try {
                    Log.d("DataFind ", "" + data[0] + " - " + data[1]);
                } catch (Exception e) {
                    Log.e("Problem", e.toString());
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file: " + ex);
        }

        //Working with wifi
        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        queue = VolleySingleton.getInstance(this).getRequestQueue();

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    private int getColorTheme() {
        SharedPreferences mSharedPreferences = getSharedPreferences("ThemeColor", MODE_PRIVATE);
        return mSharedPreferences.getInt("theme", Utils.THEME_DEFAULT);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(DiagnosticsActivity.this, MainActivity.class);
        startActivity(i);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btn_home) {
            startActivity(new Intent(DiagnosticsActivity.this, MainActivity.class));
        }
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
                            new MyAsyncTaskCode(DiagnosticsActivity.this).execute();
                            JSONObject variables = response.getJSONObject("variables");
                            int tuneMode = variables.getInt("tune_mode");
                            int gear = variables.getInt("gear");
                            String deviceName = response.getString("name");
                            deviceName += response.getString("id");
                            device = deviceName;
                            final String codes;
                            codes = variables.getString("dtcList");

                            for (final String mCodes : codes.split(" ")) {
                                new Timer().schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        if (mCodes.equals("P0000") || mCodes.equals("p0000")) {
                                        } else {
                                            diagnosticsList.add(new Code(mCodes));
                                        }
                                    }
                                }, 3000);
                            }

                            adapter = new DiagnosticsAdapter(DiagnosticsActivity.this, diagnosticsList);
                            recyclerView.setAdapter(adapter);

                            char pos = (char) gear;

                            if (tuneMode == 255) {
                                tvTune.setText("TUNE: E");
                            } else {
                                tvTune.setText("TUNE: " + tuneMode);
                            }
                            tvGear.setText("GEAR: " + pos);



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
                        Intent i = new Intent(DiagnosticsActivity.this, MainActivity.class);
                        startActivity(i);}
                }
        );

        // add it to the RequestQueue
        queue.add(getRequest);
    }

    //Send to sGDP server to get live data
    public void updateSettingsRequest() {
        isProcessing = true;
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {
                        isConnected = true;
                        try {
                            JSONObject variables = response.getJSONObject("variables");
                            int tuneMode = variables.getInt("tune_mode");
                            int gear = variables.getInt("gear");
                            String deviceName = response.getString("name");
                            deviceName += response.getString("id");
                            device = deviceName;
                            char pos = (char) gear;

                            if (tuneMode == 255) {
                                tvTune.setText("TUNE: E");
                            } else {
                                tvTune.setText("TUNE: " + tuneMode);
                            }
                            tvGear.setText("GEAR: " + pos);

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
                        Intent i = new Intent(DiagnosticsActivity.this, MainActivity.class);
                        startActivity(i);
                    }
                }
        );
        // add it to the RequestQueue
        queue.add(getRequest);
    }
}
