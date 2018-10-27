package com.hino.dev.dashboardupdater;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONObject;

public class MOPreview extends AppCompatActivity {

    private TextView lbl_chasisNumber;
    private TextView lbl_taktTime;
    private TextView lbl_moNumber;
    private TextView lbl_moDate;
    private TextView lbl_dealer;
    private TextView lbl_customer;
    private TextView lbl_chaasisModel;
    private TextView lbl_quantity;
    private ImageView btn_cancel;
    private Button btn_view_attachments;

    private Intent intent;
    private String chasisNumber;
    private WipChasisNumber wipChasisNumber;

    private RequestQueue requestQueue;
    private Gson gson;

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
        intent = getIntent();

        lbl_chasisNumber = findViewById(R.id.lbl_chasisNumber);
        lbl_taktTime = findViewById(R.id.lbl_taktTime);
        lbl_moNumber = findViewById(R.id.lbl_moNumber);
        lbl_moDate = findViewById(R.id.lbl_moDate);
        lbl_dealer = findViewById(R.id.lbl_dealer);
        lbl_customer = findViewById(R.id.lbl_customer);
        lbl_chaasisModel = findViewById(R.id.lbl_chasisModel);
        lbl_quantity = findViewById(R.id.lbl_quantity);
        btn_cancel = findViewById(R.id.img_close);
        btn_view_attachments = findViewById(R.id.btn_view_attachments);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        fetchDetails();

    }

    private void fetchDetails(){
        final String chasisNumber = intent.getStringExtra("chasisNumber");
        final String address = getResources().getString(R.string.api_mo_chasis).replace("[chasisNumber]",chasisNumber);

        JsonObjectRequest request = new JsonObjectRequest(
                JsonObjectRequest.Method.GET,
                address,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(response != null){
                            wipChasisNumber = gson.fromJson(response.toString(),WipChasisNumber.class);
                            lbl_chasisNumber.setText(wipChasisNumber.chassisNumber);
                            lbl_moNumber.setText(wipChasisNumber.moNumber);
                            lbl_moDate.setText(wipChasisNumber.moDate);
                            lbl_dealer.setText(wipChasisNumber.dealer);
                            lbl_customer.setText(wipChasisNumber.customer);
                            lbl_chaasisModel.setText(wipChasisNumber.chassisModel);
                            lbl_quantity.setText(wipChasisNumber.moQuantity + "");
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
