package com.hino.dev.dashboardupdater;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

public class MaterialCall extends DashboardUpdater {

    private TextView lbl_chassisNumber;
    private Switch switch_isPending;
    private EditText txt_remarks;
    private Button btn_submit;
    private WipChassisNumber wipChassisNumber;

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
        wipChassisNumber = gson.fromJson(callerIntent.getStringExtra("wipChassisNumber"),WipChassisNumber.class);

        if(wipChassisNumber != null){
            lbl_chassisNumber.setText(wipChassisNumber.chassisNumber);
        }

        if(wipChassisNumber.mcs != null){
            switch_isPending.setChecked(wipChassisNumber.isPending);
            txt_remarks.setText(wipChassisNumber.mcs.remarks);
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
        final Dialog dialog = nonDismissibleDialog(null);
        TextView txt_custom_dialog_message = dialog.findViewById(R.id.txt_custom_dialog_message);
        if (switch_isPending.isChecked()){
            txt_custom_dialog_message.setText("Flagging as pending");
        }else{
            txt_custom_dialog_message.setText("Flagging as material call");
        }
        dialog.show();
        final String url = getResources().getString(R.string.api_material_call);

        JSONObject params = new JSONObject();
        try {
            params.put("sectionId",section.id);
            params.put("chassisNumber",wipChassisNumber.chassisNumber);
            params.put("isPending",switch_isPending.isChecked());
            params.put("remarks",txt_remarks.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                wipChassisNumber.mcs != null ? JsonObjectRequest.Method.PUT : JsonObjectRequest.Method.POST,
                url,
                params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // TODO: 10/11/2018
                        try {
                            TimeUnit.SECONDS.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                        if(switch_isPending.isChecked()){
                            setResult(PENDING_SUCCESS);
                        }else{
                            setResult(MATERIAL_CALL_SUCCESS);
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
