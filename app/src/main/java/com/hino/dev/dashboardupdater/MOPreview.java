package com.hino.dev.dashboardupdater;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Call;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MOPreview extends AppCompatActivity {

    private ProgressBar progressBar;
    private ConstraintLayout cl_content;
    private TextView lbl_chassisNumber;
    private TextView lbl_taktTime;
    private TextView lbl_moNumber;
    private TextView lbl_moDate;
    private TextView lbl_dealer;
    private TextView lbl_customer;
    private TextView lbl_chassisModel;
    private TextView lbl_quantity;
    private ImageView btn_cancel;
    private Button btn_secondaryAction;
    private Button btn_primaryAction;
    private ImageView img_status;

    private Intent callerIntent;
    private String chassisNumber;
    private WipChassisNumber wipChassisNumber;

    private RequestQueue requestQueue;
    private Gson gson;

    private String sectionId = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mopreview);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.hide();
        }

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        gson = new Gson();
        callerIntent = getIntent();

        progressBar = findViewById(R.id.progressBar2);
        cl_content = findViewById(R.id.cl_content);
        lbl_chassisNumber = findViewById(R.id.lbl_chassisNumber);
        lbl_taktTime = findViewById(R.id.lbl_taktTime);
        lbl_moNumber = findViewById(R.id.lbl_moNumber);
        lbl_moDate = findViewById(R.id.lbl_moDate);
        lbl_dealer = findViewById(R.id.lbl_dealer);
        lbl_customer = findViewById(R.id.lbl_customer);
        lbl_chassisModel = findViewById(R.id.lbl_chassisModel);
        lbl_quantity = findViewById(R.id.lbl_quantity);
        btn_cancel = findViewById(R.id.img_close);
        btn_secondaryAction = findViewById(R.id.btn_secondaryAction);
        btn_primaryAction = findViewById(R.id.btn_primaryAction);
        img_status = findViewById(R.id.img_status);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        chassisNumber = callerIntent.getStringExtra("chassisNumber");

        fetchDetails(new Callback() {
            @Override
            public void after() {
                adjustActionHandlers();
            }
        });
    }

    private void fetchDetails(final Callback callback){
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
                            if(wipChassisNumber.finishedNormalEntry){
                                Intent intent = new Intent(getApplicationContext(),ReturnToSection.class);
                                intent.putExtra("chassisNumber",wipChassisNumber.chassisNumber);
                                startActivity(intent);
                                finish();
                            }else{
                                progressBar.setVisibility(View.GONE);
                                cl_content.setVisibility(View.VISIBLE);
                                callback.after();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;

                        Intent intent = new Intent(getApplicationContext(),ShowServerResponse.class);
                        if(networkResponse.statusCode == 400){
                            String json = new String(networkResponse.data);
                            ApiResponse response = gson.fromJson(json,ApiResponse.class);
                            intent.putExtra("message",response.Message);
                        }else{
                            intent.putExtra("message","ERROR 500\nPlease contact administrator.");
                        }
                        startActivity(intent);
                        finish();
                        error.printStackTrace();
                    }
                }
        );

        requestQueue.add(request);
    }

    interface Callback{
        void after();
    }

    private class ApiResponse{
        public String Code;
        public String Message;

        public ApiResponse(String Code, String Message) {
            this.Code= Code;
            this.Message= Message;
        }
    }

    private void adjustActionHandlers(){

        lbl_chassisNumber.setText(wipChassisNumber.chassisNumber);
        lbl_taktTime.setText(wipChassisNumber.workTime + " MINS");
        lbl_moNumber.setText(wipChassisNumber.moNumber);
        Date moDate = wipChassisNumber.makeMoDateStringAsDate();
        if(moDate != null){
            DateFormat targetMoDateFormat = new SimpleDateFormat("MM/dd/yyyy");
            lbl_moDate.setText(targetMoDateFormat.format(moDate));
        }
        lbl_dealer.setText(wipChassisNumber.dealer);
        lbl_customer.setText(wipChassisNumber.customer);
        lbl_chassisModel.setText(wipChassisNumber.chassisModel);
        lbl_quantity.setText(wipChassisNumber.moQuantity + "");

        if(wipChassisNumber.timeIn == null) {
            img_status.setVisibility(View.GONE);
            btn_primaryAction.setText("TIME IN");
            btn_secondaryAction.setText("VIEW ATTACHMENTS");

            btn_primaryAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    timeIn();
                }
            });
            btn_secondaryAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {viewAttachments();
                }
            });
        }else if(wipChassisNumber.isMc) {
            img_status.setVisibility(View.VISIBLE);
            img_status.setImageDrawable(getResources().getDrawable(R.drawable.badge_yellow));

            btn_primaryAction.setText("RESOLVE");
            btn_secondaryAction.setText("");
            btn_secondaryAction.setVisibility(View.GONE);

            btn_primaryAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    resolve();
                }
            });
            btn_secondaryAction.setOnClickListener(null);
        }else if(wipChassisNumber.timeIn != null && wipChassisNumber.finishedNormalEntry == false){
            img_status.setVisibility(View.VISIBLE);
            img_status.setImageDrawable(getResources().getDrawable(R.drawable.badge_green));
            btn_primaryAction.setText("TIME OUT");
            btn_secondaryAction.setText("MATERIAL CALL");

            btn_primaryAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {timeout();}
            });
            btn_secondaryAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {materialCall();}
            });
        }

        if(wipChassisNumber.timeIn != null && wipChassisNumber.remainingTime != null){
            long checkInTimeInMinutes = wipChassisNumber.checkInTimeInMinutes();

            if(checkInTimeInMinutes >= wipChassisNumber.remainingTime){
                img_status.setImageDrawable(getResources().getDrawable(R.drawable.badge_red));
                img_status.setVisibility(View.VISIBLE);
            }
        }else if(wipChassisNumber.timeIn != null){
            long checkInTimeInMinutes = wipChassisNumber.checkInTimeInMinutes();

            if(checkInTimeInMinutes >= wipChassisNumber.workTime){
                img_status.setImageDrawable(getResources().getDrawable(R.drawable.badge_red));
                img_status.setVisibility(View.VISIBLE);
            }
        }

    }

    private void resolve() {
        final String url = getResources().getString(R.string.api_resolve)
                .replace("[sectionId]",sectionId)
                .replace("[chassisNumber]",wipChassisNumber.chassisNumber);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                JsonObjectRequest.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Intent intent = new Intent(getApplicationContext(),SuccessAction.class);
                        intent.putExtra("status","RESOLVE");
                        startActivity(intent);;
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        );
        
        requestQueue.add(jsonObjectRequest);
    }

    private void materialCall() {
        Intent intent = new Intent(getApplicationContext(),MaterialCall.class);
        intent.putExtra("chassisNumber",wipChassisNumber.chassisNumber);
        startActivity(intent);
//        finish();
    }

    private void viewAttachments() {
        Intent intent = new Intent(getApplicationContext(),ViewAttachments.class);
        intent.putExtra("wipChassisNumber",gson.toJson(wipChassisNumber));
        startActivity(intent);
    }

    private void timeout() {
        final String url = getResources().getString(R.string.api_time_out);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("sectionId",sectionId);
            jsonObject.put("chassisNumber",wipChassisNumber.chassisNumber);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                JsonObjectRequest.Method.PUT,
                url,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(MOPreview.this, "Unit has been successfully timed-out.", Toast.LENGTH_LONG).show();
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

    // NOT FINAL FOR REVIEW
    private void timeIn() {

        btn_primaryAction.setEnabled(false);
        final String url = getResources().getString(R.string.api_time_in);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("sectionId",sectionId);
            jsonObject.put("chassisNumber",wipChassisNumber.chassisNumber);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(MOPreview.this, "Unit has been successfully timed-in.", Toast.LENGTH_LONG).show();
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: 02/11/2018 Handle appropriete errors
                        error.printStackTrace();
                    }
                }
        );

        requestQueue.add(request);
    }


}
