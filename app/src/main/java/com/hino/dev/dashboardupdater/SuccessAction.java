package com.hino.dev.dashboardupdater;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SuccessAction extends AppCompatActivity {

    private TextView txt_status;
    private Button btn_home;
    private Intent callerIntent;
    private String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_action);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.hide();
        }

        txt_status = findViewById(R.id.txt_status);
        btn_home = findViewById(R.id.btn_home);
        callerIntent = getIntent();
        status = callerIntent.getStringExtra("status");

        switch (status){
            case "TIME IN":
                txt_status.setText("Unit has been successfully\ntimed-in.");
                break;
            case "MATERIAL CALL":
                txt_status.setText("Unit status has been successfully\nflagged as Material Call.");
                break;
            case "RESOLVE":
                txt_status.setText("Unit status has been successfully\nresolved.");
                break;
            case "TIME OUT":
                txt_status.setText("Unit has been successfully\ntimed-out.");
                break;
            case "BACK JOB":
                txt_status.setText("Unit status has been successfully\nflagged as Back Job.");
                break;
            case "SPEC CHANGE":
                txt_status.setText("Unit status has been successfully\nflagged as Spec Change.");
                break;
        }

        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


}
