package com.hino.dev.dashboardupdater;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MOPreview extends AppCompatActivity {

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
    private WipChassisNumber wipChassisNumber;
    private Session session;
    private ArrayList<WipChassisNumber> inSection;

    private RequestQueue requestQueue;
    private Gson gson;

    private String sectionId = "chassisAssembly";

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
        session = new Session(this);
        inSection = session.getInSection();

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

        String wipChassisNumbersStr = callerIntent.getStringExtra("wipChassisNumber");
        Type type = new TypeToken<WipChassisNumber>() {}.getType();
        wipChassisNumber = gson.fromJson(wipChassisNumbersStr,type);
        adjustActionHandlers();
    }

    private WipChassisNumber findInSection(String chassisNumber){
        for(WipChassisNumber mo : inSection){
            if(mo.chassisNumber.equals(chassisNumber)){
                return mo;
            }
        }
        return null;
    }

    private void adjustActionHandlers(){

        lbl_chassisNumber.setText(wipChassisNumber.chassisNumber);
        lbl_taktTime.setText(wipChassisNumber.workTime + " MINS");
        lbl_moNumber.setText(wipChassisNumber.moNumber);
        lbl_moDate.setText(wipChassisNumber.moDate);
        lbl_dealer.setText(wipChassisNumber.dealer);
        lbl_customer.setText(wipChassisNumber.customer);
        lbl_chassisModel.setText(wipChassisNumber.chassisModel);
        lbl_quantity.setText(wipChassisNumber.moQuantity + "");

        final Boolean inList = findInSection(wipChassisNumber.chassisNumber) != null;

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
        }else if(wipChassisNumber.isPending || wipChassisNumber.isMc) {
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
        }else if(wipChassisNumber.timeIn != null && !inList){
            Intent intent = new Intent(MOPreview.this,ReturnToSection.class);
            intent.putExtra("chassisNumber",wipChassisNumber.chassisNumber);
            startActivity(intent);
            finish();
        }else{
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

        long checkInTimeInMinutes = wipChassisNumber.checkInTimeInMinutes();

        if(checkInTimeInMinutes > wipChassisNumber.workTime){
            img_status.setImageDrawable(getResources().getDrawable(R.drawable.badge_red));
            img_status.setVisibility(View.VISIBLE);
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
        finish();
    }

    private void viewAttachments() {
    }

    private void timeout() {
        final String url = getResources().getString(R.string.api_time_out)
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
                        intent.putExtra("status","MATERIAL CALL");
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

    // NOT FINAL FOR REVIEW
    private void timeIn() {

        btn_primaryAction.setEnabled(false);
        final String url = getResources().getString(R.string.api_time_in)+"/"+wipChassisNumber.chassisNumber;

        JsonObjectRequest request = new JsonObjectRequest(
                JsonObjectRequest.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Intent intent = new Intent(getApplicationContext(),SuccessAction.class);
                        intent.putExtra("status","TIME IN");
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
        ){
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<String,String>();
                params.put("sectionId",sectionId);
                params.put("chassisNumber", wipChassisNumber.chassisNumber);
                return params;
            }
        };

        requestQueue.add(request);
    }


}
