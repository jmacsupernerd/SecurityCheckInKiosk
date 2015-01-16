package com.mcwilliams.securitykiosk;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class MainActivity extends ActionBarActivity implements ZXingScannerView.ResultHandler {
    private String TAG = MainActivity.class.getSimpleName();
    private ZXingScannerView mScannerView;
    private List<BarcodeFormat> barcodeFormats = new ArrayList<>();
    String [] licenseAttr;


    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);

        barcodeFormats.add(BarcodeFormat.PDF_417);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.setFormats(barcodeFormats);
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        mScannerView.stopCamera();
        // Do something with the result here
        Log.v(TAG, rawResult.getText()); // Prints scan results
        Log.v(TAG, rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)

        licenseAttr = rawResult.getText().split("\\n");

        Log.d(TAG, licenseAttr[0]);
        Log.d(TAG, licenseAttr[5]);

        String firstName = licenseAttr[6];
        String lastName = licenseAttr[5];

        firstName = firstName.replace("DCT","");
        lastName = lastName.replace("DCS","");

        String[] splitName = firstName.split("\\s+");

        Intent goHome = new Intent();
        goHome.putExtra("name",splitName[0] + " " + lastName);
        setResult(0, goHome);
        finish();

    }
}
