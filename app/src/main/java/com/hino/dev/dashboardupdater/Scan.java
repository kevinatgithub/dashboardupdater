package com.hino.dev.dashboardupdater;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class Scan extends AppCompatActivity {

    private ImageView btn_back;
    private ImageView btn_scan;
    private EditText txt_chasisNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.hide();
        }

        btn_back = findViewById(R.id.btn_back);
        btn_scan = findViewById(R.id.btn_scan);
        txt_chasisNumber = findViewById(R.id.txt_chasisNumber);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }
}
