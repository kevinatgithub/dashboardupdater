package com.hino.dev.dashboardupdater;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;
// TODO: 13/11/2018 Uncomment lines when testing on Actual Device, Do not forget to update manifest file too
//import com.symbol.emdk.EMDKManager;
//import com.symbol.emdk.EMDKResults;
//import com.symbol.emdk.barcode.BarcodeManager;
//import com.symbol.emdk.barcode.ScanDataCollection;
//import com.symbol.emdk.barcode.Scanner;
//import com.symbol.emdk.barcode.ScannerException;
//import com.symbol.emdk.barcode.ScannerResults;


// TODO: 13/11/2018 Use 2nd line when testing on Actual Device
public class Scan extends DashboardUpdater {
//public class Scan extends DashboardUpdater implements EMDKManager.EMDKListener, Scanner.DataListener {


    private ImageView btn_scan;
    private EditText txt_chassisNumber;
    private TextView lbl_scanner_status;
    private WipChassisNumber wipChassisNumber;
    private Dialog dialog;
// TODO: 13/11/2018 Uncomment lines when testing on Actual Device
//    private EMDKManager emdkManager;
//    private BarcodeManager barcodeManager;
//    private com.symbol.emdk.barcode.Scanner scanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        btn_scan = findViewById(R.id.btn_scan);
        txt_chassisNumber = findViewById(R.id.txt_chassisNumber);
        lbl_scanner_status = findViewById(R.id.lbl_scanner_status);
// TODO: 13/11/2018 Uncomment lines when testing on Actual Device
//        EMDKResults emdkResults = EMDKManager.getEMDKManager(getApplicationContext(),this);
//        if(emdkResults.statusCode != EMDKResults.STATUS_CODE.SUCCESS){
//            lbl_scanner_status.setText("SCANNER FAILED TO INITIALIZE!");
//        }

        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!txt_chassisNumber.getText().equals("")){
                    dialog = nonDismissibleDialog("Checking..");
                    dialog.show();
                    fetchChassisNumberDetails(new Callback() {
                        @Override
                        public void execute() {
                            doAppropriateActionToWipChassisNumber();
                        }
                    }, txt_chassisNumber.getText().toString());

                }
            }
        });


    }

    private void doAppropriateActionToWipChassisNumber(){
        if(wipChassisNumber != null){
            if(wipChassisNumber.timeIn == null){
                timeIn();
            }else{
                timeout();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO: 13/11/2018 uncomment when testing in actual device
//        releaseScanner();
        finish();
        return super.onOptionsItemSelected(item);
    }

    private void fetchChassisNumberDetails(final Callback callback, String chassisNumber){
        final String url = getResources().getString(R.string.api_mo_chassis)
                .replace("[sectionId]",section.id)
                .replace("[chassisNumber]",chassisNumber);

        JsonObjectRequest request = new JsonObjectRequest(
                JsonObjectRequest.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(response != null){
                            wipChassisNumber = gson.fromJson(response.toString(),WipChassisNumber.class);
                            if(wipChassisNumber.finishedNormalEntry){
                                Intent intent = new Intent(getApplicationContext(),ReturnToSection.class);
                                intent.putExtra("chassisNumber",wipChassisNumber.chassisNumber);
                                startActivityForResult(intent,RETURN_TO_SECTION_REQUEST);
                            }else{
                                callback.execute();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        apiErrorHandler(error);
                        finish();
                    }
                }
        );

        requestQueue.add(request);
    }

    // Need to override this method to pass result to MOList.class after ReturnToSection.class is finished so SnackBar will show
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        setResult(resultCode,data);
        finish();
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void timeIn() {
        TextView txt_custom_dialog_message  = dialog.findViewById(R.id.txt_custom_dialog_message);
        txt_custom_dialog_message.setText("Timing-in");

        final String url = getResources().getString(R.string.api_time_in);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("sectionId",section.id);
            jsonObject.put("chassisNumber",wipChassisNumber.chassisNumber);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dialog.dismiss();
                        setResult(TIME_IN_SUCCESS,new Intent().putExtra("chassisNumber",wipChassisNumber.chassisNumber));
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        apiErrorHandler(error);
                        finish();
                    }
                }
        );

        requestQueue.add(request);
    }

    private void timeout() {
        TextView txt_custom_dialog_message  = dialog.findViewById(R.id.txt_custom_dialog_message);
        txt_custom_dialog_message.setText("Timing-out");
        final String url = getResources().getString(R.string.api_time_out);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("sectionId",section.id);
            jsonObject.put("chassisNumber",wipChassisNumber.chassisNumber);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                JsonObjectRequest.Method.PUT,
                url,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        setResult(TIME_OUT_SUCCESS,new Intent().putExtra("chassisNumber",wipChassisNumber.chassisNumber));
                        dialog.dismiss();
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        apiErrorHandler(error);
                        finish();
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);
    }

    // TODO: 13/11/2018 Uncomment lines when testing on Actual Device
//    @Override
//    public void onOpened(EMDKManager emdkManager) {
//        this.emdkManager = emdkManager;
//
//        try {
//            initScanner();
//        } catch (ScannerException e) {
//            e.printStackTrace();
//        }
//        lbl_scanner_status.setText("");
//    }
//
//    @Override
//    public void onClosed() {
//        releaseScanner();
//    }
//
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        releaseScanner();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        releaseScanner();
//    }
//
//    private void initScanner() throws ScannerException {
//        if(scanner == null){
//            barcodeManager = (BarcodeManager) this.emdkManager.getInstance(EMDKManager.FEATURE_TYPE.BARCODE);
//            scanner = barcodeManager.getDevice(BarcodeManager.DeviceIdentifier.DEFAULT);
//            scanner.addDataListener(this);
//            scanner.triggerType = Scanner.TriggerType.SOFT_ALWAYS;
//            scanner.enable();
//            scanner.read();
//        }
//    }
//
//    @Override
//    public void onData(ScanDataCollection scanDataCollection) {
//        new AsyncDataUpdate().execute(scanDataCollection);
//    }
//
//    private class AsyncDataUpdate extends AsyncTask<ScanDataCollection,Void,String>{
//
//        @Override
//        protected String doInBackground(ScanDataCollection... scanDataCollections) {
//            String barcodeValue = "";
//            ScanDataCollection scanCollection = scanDataCollections[0];
//
//            if(scanCollection != null && scanCollection.getResult() == ScannerResults.SUCCESS){
//                ArrayList<ScanDataCollection.ScanData> dataArray = scanCollection.getScanData();
//
//                for(ScanDataCollection.ScanData data : dataArray){
//                    barcodeValue = data.getData();
//                }
//            }
//            return barcodeValue;
//        }
//
//        @Override
//        protected void onPostExecute(String barcodeValue) {
//            txt_chassisNumber.setText(barcodeValue);
//            releaseScanner();
//
//            dialog = nonDismissibleDialog("Checking..");
//            dialog.show();
//            fetchChassisNumberDetails(new Callback() {
//                @Override
//                public void execute() {
//                    doAppropriateActionToWipChassisNumber();
//                }
//            }, txt_chassisNumber.getText().toString());
//        }
//    }
//
//    private void releaseScanner(){
//        if(emdkManager != null){
//            emdkManager.release();
//            emdkManager = null;
//        }
//
//        try{
//            if(scanner != null){
//                scanner.removeDataListener(this);
//                scanner.disable();
//                scanner = null;
//            }
//        } catch (ScannerException e) {
//            e.printStackTrace();
//        }
//    }
}
