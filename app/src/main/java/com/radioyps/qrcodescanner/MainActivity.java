package com.radioyps.qrcodescanner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class MainActivity extends FragmentActivity implements ZXingScannerView.ResultHandler {
        private ZXingScannerView mScannerView;
        private String mScanningContent = null;
        private String mScanningFormat = null;
        private ZXingScannerView.ResultHandler resultHandler = null;
        private static final String TAG = MainActivity.class.getName();
        @Override
        public void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            if(savedInstanceState == null){
                Log.v(TAG, "onCreate()>> first created " );
                mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
                setContentView(mScannerView);                // Set the scanner view as the content view
            }else {
                /*
                not called, during dialog fragment showing onscreen,
                and user click the back button
                 in fact, this crashed the app
                */
                Log.v(TAG, "onCreate()>> I'm back " );
                mScannerView.resumeCameraPreview(resultHandler);
            }

        }

        @Override
        public void onResume() {
            super.onResume();
            Log.v(TAG, "onResume()>> " ); // Prints scan results
            if(resultHandler != null)
            mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
            mScannerView.startCamera();// Start camera on resume
        }

        @Override
        public void onPause() {
            super.onPause();
            Log.v(TAG, "onPause()>> " );
            mScannerView.stopCamera();           // Stop camera on pause
        }

        @Override
        public void handleResult(Result rawResult) {
            // Do something with the result here
            mScanningFormat = rawResult.getBarcodeFormat().toString();
            mScanningContent  = rawResult.getText();
            Log.v(TAG, "QR code: " + rawResult.getText()); // Prints scan results
            Log.v(TAG, "QR Format: " + rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)

            // If you would like to resume scanning, call this method below:
            //mScannerView.resumeCameraPreview(this);
            resultHandler = this;
            new ScanningDialogFragment().show(getSupportFragmentManager(), "QR Result");
        }

    public class ScanningDialogFragment extends android.support.v4.app.DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.Scanning_result)
                    .setMessage(mScanningContent)
                    .setPositiveButton(R.string.Scanning_result, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //enableLocationSettings();
                            mScannerView.resumeCameraPreview(resultHandler);
                        }
                    })
                    .create();
        }

        @Override
        public void onDestroyView() {

               /*
                 this is called , when a dialog fragment showing onscreen,
                and a user click the back button

                */
            super.onDestroyView();
            mScannerView.resumeCameraPreview(resultHandler);
        }
    }
    }
