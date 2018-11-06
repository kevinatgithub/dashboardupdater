package com.hino.dev.dashboardupdater;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class ReturnToSection extends AppCompatActivity {

    private Button review_mo;
    private Button btn_back_job;
    private Button btn_spec_change;
    private Intent callerIntent;
    private String chassisNumber;
    final private String sectionId = "1";

    private RequestQueue requestQueue;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_to_section);
        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        review_mo = findViewById(R.id.btn_review_mo);
        btn_back_job = findViewById(R.id.btn_back_job);
        btn_spec_change = findViewById(R.id.btn_spec_change);
        callerIntent = getIntent();
        chassisNumber = callerIntent.getStringExtra("chassisNumber");
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        gson = new Gson();


        
        review_mo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reviewMo();
            }
        });
        
        btn_back_job.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_back_job.setEnabled(false);
                backJob();
            }
        });
        
        btn_spec_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_spec_change.setEnabled(false);
                specChange();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    private void specChange() {
        final String url = getResources().getString(R.string.api_spec_change);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("sectionId",sectionId);
            jsonObject.put("chassisNumber",chassisNumber);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                JsonObjectRequest.Method.POST,
                url,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(ReturnToSection.this, "Unit has been successfully flagged as Spec Change.", Toast.LENGTH_LONG).show();
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: 02/11/2018 Handle Error Code Status
                        error.printStackTrace();
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);
    }

    private void backJob() {
        final String url = getResources().getString(R.string.api_back_job);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("sectionId",sectionId);
            jsonObject.put("chassisNumber",chassisNumber);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                JsonObjectRequest.Method.POST,
                url,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(ReturnToSection.this, "Unit has been successfully flagged as back job.", Toast.LENGTH_LONG).show();
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: 02/11/2018 Handle Error Code Status
                        error.printStackTrace();
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);
    }

    private void reviewMo() {
        Intent intent = new Intent(this,ReviewMO.class);
        intent.putExtra("chassisNumber",chassisNumber);
        startActivity(intent);
    }
}
