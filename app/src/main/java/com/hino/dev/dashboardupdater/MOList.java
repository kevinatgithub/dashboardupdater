package com.hino.dev.dashboardupdater;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class MOList extends DashboardUpdater {

    private boolean doubleBackToExitPressedOnce = false;

    final private int SECTION_LIST_REQUEST = 100;           //Set to 100 since REQUESTS code in DashboardUpdater.class is 1 and so on
    final static public int SECTION_LIST_RESPONSE_IS_SELECT_A_NEW_SECTION = 1;

    private ListView lv_mo;
    private ImageView img_info;
    private TextView lbl_info;
    private FloatingActionButton fab_scan;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navView;
    private TextView nav_header_textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_molist);

        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);

        navView = findViewById(R.id.nav_view);
        lv_mo = findViewById(R.id.lv_mo);
        img_info = findViewById(R.id.img_info);
        lbl_info = findViewById(R.id.lbl_info);
        fab_scan = findViewById(R.id.fab_scan);

        checkConnection(new Callback() {
            @Override
            public void execute() {
                fetchWipList(true);
                refreshWipListEvery15Seconds();     //Will be removed if not necessary
            }
        });

        lv_mo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                WipChassisNumber wipChasisNumber = (WipChassisNumber) lv_mo.getItemAtPosition(position);
                Intent intent = new Intent(getApplicationContext(),MOPreview.class);
                intent.putExtra("chassisNumber",wipChasisNumber.chassisNumber);
                startActivityForResult(intent,SCAN_REQUEST);
            }
        });

        fab_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Scan.class);
                startActivityForResult(intent,SCAN_REQUEST);
            }
        });

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id){
                    case R.id.sections:
                        Intent sections = new Intent(getApplicationContext(), Sections.class);
                        startActivityForResult(sections,SECTION_LIST_REQUEST);
                        break;
                    case R.id.logout:
                        session.removeUser();
                        session.removeSection();
                        Intent login = new Intent(getApplicationContext(), Login.class);
                        startActivity(login);
                        finish();
                        break;
                }
                return false;
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        switch (requestCode){
            case SCAN_REQUEST:
            case RETURN_TO_SECTION_REQUEST:

                switch(resultCode){
                    case TIME_IN_SUCCESS:
                        showSnackBar(drawerLayout,data,getResources().getString(R.string.success_time_in));
                        break;
                    case TIME_OUT_SUCCESS:
                        showSnackBar(drawerLayout,data,getResources().getString(R.string.success_time_out));
                        break;
                    case RESOLVE_SUCCESS:
                        showSnackBar(drawerLayout,data,getResources().getString(R.string.success_resolve));
                        break;
                    case SPEC_CHANGE_SUCCESS:
                        showSnackBar(drawerLayout,data,getResources().getString(R.string.success_spec_change));
                        break;
                    case BACK_JOB_SUCCESS:
                        showSnackBar(drawerLayout,data,getResources().getString(R.string.success_back_job));
                        break;
                }

                break;
            case SECTION_LIST_REQUEST:

                switch(resultCode){
                    case SECTION_LIST_RESPONSE_IS_SELECT_A_NEW_SECTION:
                        finish();
                        break;
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void refreshWipListEvery15Seconds(){
        new android.os.Handler().postDelayed(
        new Runnable() {
            public void run() {
                checkConnection(new Callback() {
                    @Override
                    public void execute() {
                        fetchWipList(false);
                        refreshWipListEvery15Seconds();
                    }
                });
            }
        },
        15000);
    }

    // Model Class for the API Response
    private class ApiResponseWipChassisNumbers {
        public String sectionId;
        public WipChassisNumber[] wipChassisNumbers;

        public ApiResponseWipChassisNumbers(String sectionId, WipChassisNumber[] wipChassisNumbers) {
            this.sectionId = sectionId;
            this.wipChassisNumbers = wipChassisNumbers;
        }
    }

    // Used in sorting the Chassis Numbers response from API
    public class WipChassisNumbersComparator implements Comparator<WipChassisNumber>{

        @Override
        public int compare(WipChassisNumber left, WipChassisNumber right) {
            return left.chassisNumber.compareTo(right.chassisNumber);
        }
    }

    private void fetchWipList(final boolean showLoading){

        final Dialog dialog = nonDismissibleDialog("Loading..");
        if(showLoading){
            dialog.show();
        }
        final String URL = getResources().getString(R.string.api_wip_list).replace("[sectionId]",section.id);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(showLoading){
                            dialog.dismiss();
                        }
                        if(response != null){
                            ApiResponseWipChassisNumbers ar = gson.fromJson(response.toString(),ApiResponseWipChassisNumbers.class);
                            if(ar.wipChassisNumbers != null){
                                ArrayList<WipChassisNumber> wipChasisNumbers = new ArrayList<WipChassisNumber>(Arrays.asList(ar.wipChassisNumbers));

                                if(wipChasisNumbers.size() > 0){
                                    Collections.sort(wipChasisNumbers,new WipChassisNumbersComparator());
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
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        apiErrorHandler(error);
                        finish();
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    // ListView adapter for WipChassisNumber
    private class MoListAdapter extends ArrayAdapter<WipChassisNumber>{

        public MoListAdapter(Context context, ArrayList<WipChassisNumber> items){
            super(context, 0, items);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            WipChassisNumber mo = getItem(position);

            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.mo_list_row, parent, false);
            }

            TextView chassis_no = convertView.findViewById(R.id.lbl_chassis_no);
            TextView status = convertView.findViewById(R.id.lbl_status);
            ImageView img_arrow = convertView.findViewById(R.id.img_arrow);

            img_arrow.setImageResource(R.drawable.ic_arrow_right);
            chassis_no.setText(mo.chassisNumber);

            if(mo.isPending) {
                status.setText("Pending");
            }else{
                status.setText("In Section");
            }
            return convertView;
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        checkConnection(new Callback() {
            @Override
            public void execute() {
                fetchWipList(false);
            }
        });
    }


}
