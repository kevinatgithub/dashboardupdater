package com.hino.dev.dashboardupdater;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class MaterialCall extends AppCompatActivity {

    private ImageView img_cancel;
    private TextView lbl_chassisNumber;
    private Switch switch_isPending;
    private EditText txt_remarks;
    private Button btn_submit;
    private RequestQueue requestQueue;
    final private String sectionId = "1";
    private Intent callerIntent;
    private String chassisNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_call);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.hide();
        }

        img_cancel = findViewById(R.id.img_cancel);
        lbl_chassisNumber = findViewById(R.id.lbl_chassisModel);
        switch_isPending = findViewById(R.id.switch_isPending);
        txt_remarks = findViewById(R.id.txt_remarks);
        btn_submit = findViewById(R.id.btn_submit);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        callerIntent = getIntent();
        chassisNumber = callerIntent.getStringExtra("chassisNumber");

        img_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();

            }
        });

    }

    private void submit() {
        final String url = getResources().getString(R.string.api_material_call).replace("[sectionId]",sectionId)
                .replace("[chassisNumber]",chassisNumber)
                .replace("[isPending]",switch_isPending.isChecked() ? "true" : "false")
                .replace("[remarks]",txt_remarks.getText());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                JsonObjectRequest.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String promptMessage = "";
                        if(switch_isPending.isChecked()){
                            Intent intent = new Intent(getApplicationContext(),SuccessAction.class);
                            intent.putExtra("status","PENDING");
                            startActivity(intent);;
                        }else{
                            Intent intent = new Intent(getApplicationContext(),SuccessAction.class);
                            intent.putExtra("status","MATERIAL CALL");
                            startActivity(intent);;
                        }
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
}
