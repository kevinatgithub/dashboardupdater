package com.hino.dev.dashboardupdater;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//import com.symbol.emdk.EMDKManager;
//import com.symbol.emdk.EMDKResults;
//import com.symbol.emdk.barcode.BarcodeManager;
//import com.symbol.emdk.barcode.ScanDataCollection;
//import com.symbol.emdk.barcode.Scanner;
//import com.symbol.emdk.barcode.ScannerException;
//import com.symbol.emdk.barcode.ScannerResults;

import java.util.ArrayList;

public class Scan extends AppCompatActivity {
//public class Scan extends AppCompatActivity implements EMDKManager.EMDKListener, Scanner.DataListener {


    private ImageView btn_scan;
    private EditText txt_chassisNumber;
    private TextView lbl_scanner_status;
//    private EMDKManager emdkManager;
//    private BarcodeManager barcodeManager;
//    private Scanner scanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        btn_scan = findViewById(R.id.btn_scan);
        txt_chassisNumber = findViewById(R.id.txt_chassisNumber);
        lbl_scanner_status = findViewById(R.id.lbl_scanner_status);

//        EMDKResults emdkResults = EMDKManager.getEMDKManager(getApplicationContext(),this);
//        if(emdkResults.statusCode != EMDKResults.STATUS_CODE.SUCCESS){
//            lbl_scanner_status.setText("SCANNER FAILED TO INITIALIZE!");
//        }

        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!txt_chassisNumber.getText().equals("")){
                    Intent intent = new Intent(getApplicationContext(),MOPreview.class);
                    intent.putExtra("chassisNumber",txt_chassisNumber.getText().toString());
                    startActivity(intent);
                    finish();
                }
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

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
//        finish();
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
//            Intent intent = new Intent(getApplicationContext(),MOPreviewLoading.class);
//            intent.putExtra("chassisNumber",barcodeValue);
//            startActivity(intent);
//            finish();
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
