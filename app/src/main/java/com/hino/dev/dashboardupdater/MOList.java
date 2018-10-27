package com.hino.dev.dashboardupdater;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class MOList extends AppCompatActivity {

    private ListView lv_mo;
    private ImageView img_info;
    private TextView lbl_info;
    private String api_address;
    private RequestQueue requestQueue;
    private Gson gson;
    private String sectionId = "1";
    private ImageView btn_scan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_molist);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        lv_mo = findViewById(R.id.lv_mo);
        img_info = findViewById(R.id.img_info);
        lbl_info = findViewById(R.id.lbl_info);
        api_address = getResources().getString(R.string.api_wip_list);
        requestQueue = Volley.newRequestQueue(this);
        gson = new Gson();
        btn_scan = findViewById(R.id.btn_scan);

        fetchWipList();
//        refreshWipList();

        lv_mo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                WipChasisNumber wipChasisNumber = (WipChasisNumber) lv_mo.getItemAtPosition(position);
                Intent intent = new Intent(getApplicationContext(),MOPreview.class);
                intent.putExtra("chasisNumber",wipChasisNumber.chassisNumber);
                startActivity(intent);
            }
        });

        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Scan.class);
                startActivity(intent);
            }
        });

    }

    private void refreshWipList(){
        new android.os.Handler().postDelayed(
        new Runnable() {
            public void run() {
                fetchWipList();
                refreshWipList();
            }
        },
        15000);
    }

    public class ApiResponse{
        public String sectionId;
        public WipChasisNumber[] wipChasisNumbers;

        public ApiResponse(String sectionId, WipChasisNumber[] wipChasisNumbers) {
            this.sectionId = sectionId;
            this.wipChasisNumbers = wipChasisNumbers;
        }
    }

    private void fetchWipList(){
        String address = api_address.replace("[sectionId]",sectionId);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                address,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        Log.d("SERVER RESPONSE",response.toString());
                        if(response != null){
                            ApiResponse ar = gson.fromJson(response.toString(),ApiResponse.class);
                            ArrayList<WipChasisNumber> wipChasisNumbers = new ArrayList<WipChasisNumber>(Arrays.asList(ar.wipChasisNumbers));

                            if(wipChasisNumbers.size() > 0){
                                MoListAdapter moListAdapter = new MoListAdapter(getApplicationContext(),wipChasisNumbers);
                                lv_mo.setAdapter(moListAdapter);
                                lv_mo.setVisibility(View.VISIBLE);
                                img_info.setVisibility(View.GONE);
                                lbl_info.setVisibility(View.GONE);
                            }else{
                                lv_mo.setVisibility(View.GONE);
                                img_info.setVisibility(View.VISIBLE);
                                lbl_info.setVisibility(View.VISIBLE);
                            }

                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    private class MoListAdapter extends ArrayAdapter<WipChasisNumber>{

        public MoListAdapter(Context context, ArrayList<WipChasisNumber> items){
            super(context, 0, items);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            WipChasisNumber mo = getItem(position);

            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.mo_list_row, parent, false);
            }

            TextView chasis_no = convertView.findViewById(R.id.lbl_chasis_no);
            TextView status = convertView.findViewById(R.id.lbl_status);
            ImageView img_arrow = convertView.findViewById(R.id.img_arrow);

            img_arrow.setImageResource(R.drawable.ic_arrow_right);
            chasis_no.setText(mo.chassisNumber);

            /*NOT FINAL*/
            if(mo.isPending){
                status.setText("Pending");
            }else{
                status.setText("In Section");
            }
            return convertView;
        }
    }


}
