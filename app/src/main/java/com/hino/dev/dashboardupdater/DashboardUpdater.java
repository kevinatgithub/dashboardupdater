package com.hino.dev.dashboardupdater;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

public class DashboardUpdater extends AppCompatActivity {

    final protected int SCAN_REQUEST = 1;
    final protected int MATERIAL_CALL_REQUEST = 2;
    final protected int RETURN_TO_SECTION_REQUEST = 3;
    final protected int TIME_IN_SUCCESS = 1;
    final protected int TIME_OUT_SUCCESS = 2;
    final protected int MATERIAL_CALL_SUCCESS = 3;
    final protected int PENDING_SUCCESS = 4;
    final protected int RESOLVE_SUCCESS = 5;
    final protected int SPEC_CHANGE_SUCCESS = 6;
    final protected int BACK_JOB_SUCCESS = 7;

    protected Intent callerIntent;
    protected Session session;
    protected User user;
    protected User.Section section;
    protected RequestQueue requestQueue;
    protected Gson gson;

    // Used for implementing callback in java
    protected interface Callback{
        void execute();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callerIntent = getIntent();
        session = new Session(DashboardUpdater.this);
        user = session.getUser();
        section = session.getSection();
        requestQueue = Volley.newRequestQueue(DashboardUpdater.this);
        gson = new Gson();
    }

    protected Dialog nonDismissibleDialog(@Nullable String customMessage){
        Dialog dialog = new Dialog(DashboardUpdater.this);
        dialog.setContentView(R.layout.custom_dialog);
        TextView txt_custom_dialog_message = dialog.findViewById(R.id.txt_custom_dialog_message);
        if(customMessage != null){
            txt_custom_dialog_message.setText(customMessage);
        }
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(lp);
        dialog.setCancelable(false);
        return dialog;
    }

    protected void showSnackBar(View container,Intent data,String message){
        Snackbar snackbar = Snackbar.make(container,message,Snackbar.LENGTH_LONG);
        if(data != null){
            final String chassisNumber = data.getStringExtra("chassisNumber");
            snackbar.setAction("VIEW", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(DashboardUpdater.this,MOPreview.class);
                    intent.putExtra("chassisNumber",chassisNumber);
                    startActivity(intent);
                }
            });
            snackbar.setActionTextColor(Color.parseColor("#06d73e"));
        }
        snackbar.show();
    }

    protected void apiErrorHandler(VolleyError error){
        NetworkResponse networkResponse = error.networkResponse;

        Intent intent = new Intent(getApplicationContext(),ShowServerResponse.class);
        if(networkResponse == null){
            intent.putExtra("message","NETWORK ERROR " +getResources().getString(R.string.api_error));
        }else if(networkResponse.statusCode == 400){
            String json = new String(networkResponse.data);
            ApiResponse response = gson.fromJson(json,ApiResponse.class);
            intent.putExtra("message",response.Message);
        }else{
            intent.putExtra("message","ERROR      "+networkResponse.statusCode+" " +getResources().getString(R.string.api_error));
        }
        startActivity(intent);
    }

    protected void checkConnection(final Callback callback){

        ConnectivityManager conMgr =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
        if (netInfo == null){

            LayoutInflater inflater = LayoutInflater.from(DashboardUpdater.this);
            final AlertDialog dialog = new AlertDialog.Builder(DashboardUpdater.this).create();
            dialog.setTitle(getResources().getString(R.string.internet_error_title));
            View customView = inflater.inflate(R.layout.network_fail,null);
            dialog.setView(customView);
            dialog.setCancelable(false);
            dialog.setButton(AlertDialog.BUTTON_POSITIVE, "TRY AGAIN", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialog.dismiss();
                    checkConnection(callback);
                }
            });

            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(R.color.colorPrimary);
                }
            });
            dialog.show();
        }else{
            callback.execute();
        }
    }

    // Model Class for API Response when handling error code 400
    public static class ApiResponse {
        public String Code;
        public String Message;

        public ApiResponse(String Code, String Message) {
            this.Code= Code;
            this.Message= Message;
        }
    }

}
