package com.hino.dev.dashboardupdater;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
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
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class MOList extends AppCompatActivity {

    private ListView lv_mo;
    private ImageView img_info;
    private TextView lbl_info;
    private String api_address;
    private RequestQueue requestQueue;
    private Gson gson;
    private String sectionId = "1";
    private Session session;
    private FloatingActionButton fab_scan;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
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
//        nav_header_textView = findViewById(R.id.nav_header_textView);

        // TODO: 02/11/2018  change user name of current logged in user
//        nav_header_textView.setText("Juan Dela Cruz");

        lv_mo = findViewById(R.id.lv_mo);
        img_info = findViewById(R.id.img_info);
        lbl_info = findViewById(R.id.lbl_info);
        api_address = getResources().getString(R.string.api_wip_list);
        fab_scan = findViewById(R.id.fab_scan);
        requestQueue = Volley.newRequestQueue(this);
        gson = new Gson();
        session = new Session(this);

        checkConnection(new CallbackInterface() {
            @Override
            public void onSuccess() {
                fetchWipList();
                refreshWipList();
            }
        });

        lv_mo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                WipChassisNumber wipChasisNumber = (WipChassisNumber) lv_mo.getItemAtPosition(position);
                Intent intent = new Intent(getApplicationContext(),MOPreview.class);
                intent.putExtra("chassisNumber",wipChasisNumber.chassisNumber);
                startActivity(intent);
            }
        });

        fab_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Scan.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        drawerLayout.openDrawer(GravityCompat.START);
        return super.onOptionsItemSelected(item);
    }

    private void refreshWipList(){
        new android.os.Handler().postDelayed(
        new Runnable() {
            public void run() {
                checkConnection(new CallbackInterface() {
                    @Override
                    public void onSuccess() {
                        fetchWipList();
                        refreshWipList();
                    }
                });
            }
        },
        15000);
    }

    public class ApiResponse{
        public String sectionId;
        public WipChassisNumber[] wipChassisNumbers;

        public ApiResponse(String sectionId, WipChassisNumber[] wipChassisNumbers) {
            this.sectionId = sectionId;
            this.wipChassisNumbers = wipChassisNumbers;
        }
    }

    public class WipChassisNumbersComparator implements Comparator<WipChassisNumber>{

        @Override
        public int compare(WipChassisNumber left, WipChassisNumber right) {
            return left.chassisNumber.compareTo(right.chassisNumber);
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
                        if(response != null){
                            ApiResponse ar = gson.fromJson(response.toString(),ApiResponse.class);
                            if(ar.wipChassisNumbers != null){
                                ArrayList<WipChassisNumber> wipChasisNumbers = new ArrayList<WipChassisNumber>(Arrays.asList(ar.wipChassisNumbers));
                                session.setInSection(wipChasisNumbers);

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
                        error.printStackTrace();
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

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
        fetchWipList();
    }

    interface CallbackInterface{

        void onSuccess();
    }

    private void checkConnection(CallbackInterface callbackInterface){
        ConnectivityManager conMgr =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
        if (netInfo == null){
            new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.internet_error_title))
                    .setMessage(getResources().getString(R.string.internet_error))
                    .setPositiveButton("OK", null).show();
        }else{
            callbackInterface.onSuccess();
        }
    }
}
