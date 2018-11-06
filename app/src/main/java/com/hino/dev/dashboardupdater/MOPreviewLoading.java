package com.hino.dev.dashboardupdater;

import android.content.Intent;
import android.support.design.button.MaterialButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
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

    private String sectionId;
    private Intent callerIntent;
    private ImageView img_loading;
    private TextView txt_status;
    private MaterialButton btn_back;
    private String chassisNumber;
    private RequestQueue requestQueue;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mo_preview_loading);

        // TODO: 02/11/2018 fetch sectionId from session
        sectionId = "1";
        img_loading = findViewById(R.id.img_loading);
        txt_status = findViewById(R.id.txt_status);
        btn_back = findViewById(R.id.btn_back);
        callerIntent = getIntent();
        chassisNumber = callerIntent.getStringExtra("chassisNumber");
        requestQueue = Volley.newRequestQueue(this);
        gson = new Gson();

        fetchDetailsAndProceed();

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Scan.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void fetchDetailsAndProceed(){
        final String url = getResources().getString(R.string.api_mo_chassis)
                .replace("[sectionId]",sectionId)
                .replace("[chassisNumber]",chassisNumber);

        JsonObjectRequest request = new JsonObjectRequest(
                JsonObjectRequest.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(response != null){
                            WipChassisNumber wipChassisNumber = gson.fromJson(response.toString(),WipChassisNumber.class);
                            if(wipChassisNumber.finishedNormalEntry){
                                Intent intent = new Intent(MOPreviewLoading.this,ReturnToSection.class);
                                intent.putExtra("chassisNumber",wipChassisNumber.chassisNumber);
                                startActivity(intent);
                            }else{
                                Intent intent = new Intent(getApplicationContext(),MOPreview.class);
                                intent.putExtra("chassisNumber",gson.toJson(wipChassisNumber));
                                intent.putExtra("wipChassisNumber",gson.toJson(wipChassisNumber));
                                startActivity(intent);
                            }
                            finish();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;


                        if(networkResponse.statusCode == 400){
                            String json = new String(networkResponse.data);
                            ApiResponse response = gson.fromJson(json,ApiResponse.class);
                            img_loading.setVisibility(View.GONE);
                            txt_status.setText(response.Message);
                            btn_back.setVisibility(View.VISIBLE);
                        }else{
                            // TODO: 02/11/2018  show fatal error
                            Toast.makeText(MOPreviewLoading.this, "ERROR 500", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        error.printStackTrace();
                    }
                }
        );

        requestQueue.add(request);
    }

    private class ApiResponse{
        public String Code;
        public String Message;

        public ApiResponse(String Code, String Message) {
            this.Code= Code;
            this.Message= Message;
        }
    }
}
