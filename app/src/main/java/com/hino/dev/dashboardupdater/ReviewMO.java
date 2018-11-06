package com.hino.dev.dashboardupdater;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReviewMO extends AppCompatActivity {

    private ProgressBar pb_spinner;
    private ConstraintLayout cl_layout1;
    private ConstraintLayout cl_layout2;
    private ConstraintLayout cl_layout3;
    private ConstraintLayout cl_layout4;
    private ConstraintLayout cl_layout5;
    private ConstraintLayout cl_layout6;
    private ConstraintLayout cl_layout7;
    private TextView lbl_chassisNumber;
    private TextView lbl_chassisNumber_hint;
    private TextView lbl_taktTime;
    private TextView lbl_moNumber;
    private TextView lbl_moDate;
    private TextView lbl_dealer;
    private TextView lbl_customer;
    private TextView lbl_chassisModel;
    private TextView lbl_quantity;
    private ImageView img_cancel;
    private Button btn_viewAttachments;
    private RequestQueue requestQueue;
    private Gson gson;
    private Intent callerIntent;

    private WipChassisNumber wipChassisNumber;
    private String chassisNumber;
    private String sectionId = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_mo);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.hide();
        }

        pb_spinner = findViewById(R.id.progressBar);
        cl_layout1 = findViewById(R.id.constraintLayout1);
        cl_layout2 = findViewById(R.id.constraintLayout2);
        cl_layout3 = findViewById(R.id.constraintLayout3);
        cl_layout4 = findViewById(R.id.constraintLayout4);
        cl_layout5 = findViewById(R.id.constraintLayout5);
        cl_layout6 = findViewById(R.id.constraintLayout6);
        cl_layout7 = findViewById(R.id.constraintLayout7);
        lbl_chassisNumber = findViewById(R.id.lbl_chassisNumber);
        lbl_chassisNumber_hint = findViewById(R.id.lbl_chassisNumber_hint);
        lbl_taktTime = findViewById(R.id.lbl_taktTime);
        lbl_moNumber = findViewById(R.id.lbl_moNumber);
        lbl_moDate = findViewById(R.id.lbl_moDate);
        lbl_dealer = findViewById(R.id.lbl_dealer);
        lbl_customer = findViewById(R.id.lbl_customer);
        lbl_chassisModel = findViewById(R.id.lbl_chassisModel);
        lbl_quantity = findViewById(R.id.lbl_quantity);
        img_cancel = findViewById(R.id.img_close);
        btn_viewAttachments = findViewById(R.id.btn_view_attachments);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        callerIntent = getIntent();
        gson = new Gson();

        img_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        
        btn_viewAttachments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewAttachments();
            }
        });

        fetchDetails();
    }

    private void viewAttachments() {
        Intent intent = new Intent(getApplicationContext(),ViewAttachments.class);
        intent.putExtra("wipChassisNumber",gson.toJson(wipChassisNumber));
        startActivity(intent);
    }

    private void fetchDetails(){
        final String chassisNumber = callerIntent.getStringExtra("chassisNumber");
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
                            wipChassisNumber = gson.fromJson(response.toString(),WipChassisNumber.class);
                            lbl_chassisNumber.setText(wipChassisNumber.chassisNumber);
                            lbl_moNumber.setText(wipChassisNumber.moNumber);
                            lbl_moDate.setText(wipChassisNumber.moDate);
                            lbl_dealer.setText(wipChassisNumber.dealer);
                            lbl_customer.setText(wipChassisNumber.customer);
                            lbl_chassisModel.setText(wipChassisNumber.chassisModel);
                            lbl_quantity.setText(wipChassisNumber.moQuantity + "");

                            lbl_chassisNumber.setVisibility(View.VISIBLE);
                            lbl_chassisNumber_hint.setVisibility(View.VISIBLE);
                            img_cancel.setVisibility(View.VISIBLE);
                            cl_layout1.setVisibility(View.VISIBLE);
                            cl_layout2.setVisibility(View.VISIBLE);
                            cl_layout3.setVisibility(View.VISIBLE);
                            cl_layout4.setVisibility(View.VISIBLE);
                            cl_layout5.setVisibility(View.VISIBLE);
                            cl_layout6.setVisibility(View.VISIBLE);
                            cl_layout7.setVisibility(View.VISIBLE);
                            btn_viewAttachments.setVisibility(View.VISIBLE);
                            pb_spinner.setVisibility(View.GONE);

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
