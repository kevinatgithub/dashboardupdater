package com.hino.dev.dashboardupdater;

import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MOPreview extends DashboardUpdater {

    private ConstraintLayout cl_mo_prevew;
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

    private String chassisNumber;
    private WipChassisNumber wipChassisNumber;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mopreview);

        cl_mo_prevew = findViewById(R.id.cl_mo_preview);
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
            public void execute() {
                adjustActionHandlers();
            }
        });
    }

    @Override
    protected void onPostResume() {
        fetchDetails(new Callback() {
            @Override
            public void execute() {
                adjustActionHandlers();
            }
        });
        super.onPostResume();
    }

    private void fetchDetails(final Callback callback){
        final String url = getResources().getString(R.string.api_mo_chassis)
                .replace("[sectionId]",section.id)
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
                                callback.execute();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        apiErrorHandler(error);
                        finish();
                    }
                }
        );

        requestQueue.add(request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Snackbar snackbar = Snackbar.make(cl_mo_prevew,"",Snackbar.LENGTH_LONG);
        switch(resultCode){
            case MATERIAL_CALL_SUCCESS:
                snackbar.setText(getResources().getString(R.string.success_material_call)).show();
                break;
            case PENDING_SUCCESS:
                snackbar.setText(getResources().getString(R.string.success_pending)).show();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void adjustActionHandlers(){

        lbl_chassisNumber.setText(wipChassisNumber.chassisNumber);
        lbl_taktTime.setText((wipChassisNumber.workTime != null ? wipChassisNumber.workTime : "0") + " MINS");
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

        if(wipChassisNumber.isMc) {
            img_status.setVisibility(View.VISIBLE);
            img_status.setImageDrawable(getResources().getDrawable(R.drawable.badge_yellow));

            btn_primaryAction.setText("RESOLVE");
            btn_secondaryAction.setText("MATERIAL CALL");
            btn_primaryAction.setVisibility(View.VISIBLE);

            btn_primaryAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    resolve();
                }
            });

            btn_secondaryAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {materialCall();}
            });
        }else if(wipChassisNumber.timeIn != null && wipChassisNumber.finishedNormalEntry == false){
            img_status.setVisibility(View.VISIBLE);
            img_status.setImageDrawable(getResources().getDrawable(R.drawable.badge_green));
            btn_primaryAction.setVisibility(View.GONE);
            btn_secondaryAction.setText("MATERIAL CALL");
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
        final Dialog dialog = nonDismissibleDialog("Resolving..");
        dialog.show();
        final String url = getResources().getString(R.string.api_resolve);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("sectionId",section.id);
            jsonObject.put("chassisNumber",wipChassisNumber.chassisNumber);
            jsonObject.put("isResolved",true);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        setResult(RESOLVE_SUCCESS,new Intent().putExtra("chassisNumber",wipChassisNumber.chassisNumber));
                        dialog.dismiss();
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        apiErrorHandler(error);
                        finish();
                    }
                }
        );
        
        requestQueue.add(jsonObjectRequest);
    }

    private void materialCall() {
        Intent intent = new Intent(getApplicationContext(),MaterialCall.class);
        intent.putExtra("wipChassisNumber",gson.toJson(wipChassisNumber));
        startActivityForResult(intent, MATERIAL_CALL_REQUEST);
//        finish();
    }

    private void viewAttachments() {
        Intent intent = new Intent(getApplicationContext(),ViewAttachments.class);
        intent.putExtra("wipChassisNumber",gson.toJson(wipChassisNumber));
        startActivity(intent);
    }






}
