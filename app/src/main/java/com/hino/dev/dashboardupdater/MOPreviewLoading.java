package com.hino.dev.dashboardupdater;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MOPreviewLoading extends AppCompatActivity {

    private Intent callerIntent;
    private String chassisNumber;
    private RequestQueue requestQueue;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mo_preview_loading);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.hide();
        }

        callerIntent = getIntent();
        chassisNumber = callerIntent.getStringExtra("chassisNumber");
        requestQueue = Volley.newRequestQueue(this);
        gson = new Gson();

        fetchDetailsAndProceed();

    }

    private void fetchDetailsAndProceed(){
        final String url = getResources().getString(R.string.api_mo_chassis).replace("[chassisNumber]",chassisNumber);

        JsonObjectRequest request = new JsonObjectRequest(
                JsonObjectRequest.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(response != null){
                            WipChassisNumber wipChassisNumber = gson.fromJson(response.toString(),WipChassisNumber.class);
                            Intent intent = new Intent(getApplicationContext(),MOPreview.class);
                            intent.putExtra("chassisNumber",gson.toJson(wipChassisNumber));
                            intent.putExtra("wipChassisNumber",gson.toJson(wipChassisNumber));
                            startActivity(intent);
                            finish();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        );

        requestQueue.add(request);
    }
}
