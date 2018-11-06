package com.hino.dev.dashboardupdater;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
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

import org.json.JSONException;
import org.json.JSONObject;

public class MaterialCall extends AppCompatActivity {

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

        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        lbl_chassisNumber = findViewById(R.id.lbl_chassisNumber);
        switch_isPending = findViewById(R.id.switch_isPending);
        txt_remarks = findViewById(R.id.txt_remarks);
        btn_submit = findViewById(R.id.btn_submit);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        callerIntent = getIntent();
        chassisNumber = callerIntent.getStringExtra("chassisNumber");

        if(chassisNumber != null){
            lbl_chassisNumber.setText(chassisNumber);
        }

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    private void submit() {
        final String url = getResources().getString(R.string.api_material_call);

        JSONObject params = new JSONObject();
        try {
            params.put("sectionId",sectionId);
            params.put("chassisNumber",chassisNumber);
            params.put("isPending",switch_isPending.isChecked());
            params.put("remarks",txt_remarks.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                JsonObjectRequest.Method.POST,
                url,
                params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String promptMessage = "";
                        if(switch_isPending.isChecked()){
                            Toast.makeText(MaterialCall.this, "Unit has been successfully flagged as pending.", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(MaterialCall.this, "Unit has been successfully flagged as material call.", Toast.LENGTH_LONG).show();
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
