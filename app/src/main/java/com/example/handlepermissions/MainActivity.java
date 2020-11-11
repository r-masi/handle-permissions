package com.example.handlepermissions;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_READ_PHONE_STATE = 201;
    private Context _context;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.txtImei);
        _context = this;


        if (ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.READ_PHONE_STATE) ==
                PackageManager.PERMISSION_GRANTED) {
            // You can use the API that requires the permission.
            performAction();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)) {
            // In an educational UI, explain to the user why your app requires this
            // permission for a specific feature to behave as expected. In this UI,
            // include a "cancel" or "no thanks" button that allows the user to
            // continue using your app without granting the permission.
            showInContextUI();
        } else {
            // You can directly ask for the permission.
            askPermission();
        }


    }

    private void askPermission() {
        ActivityCompat.requestPermissions(this,
                new String[] { Manifest.permission.READ_PHONE_STATE },
                REQUEST_READ_PHONE_STATE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_PHONE_STATE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                    performAction();
                }  else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                    permissionDeniedUI();
                }
                return;
        }

    }

    private void showInContextUI() {

        new AlertDialog.Builder(_context)
                .setTitle("Permission Required")
                .setMessage("We need permission to READ YOUR PHONE BOOK")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> {
                        askPermission();
                        })
                .setNegativeButton("No, Thanks", (dialog, which) -> {
                    System.out.println("DO NOTHING!");
                })
                .show();

    }

    private void permissionDeniedUI() {

        new AlertDialog.Builder(_context)
                .setTitle("Permission Denied")
                .setMessage("You've declined the required permissions, please grant them from your phone settings")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> {

                })
                .show();
    }

    private void performAction() {
        textView.setText(deviceId(_context));
    }

    @SuppressLint("HardwareIds")
    public static String deviceId(Context context) {

        String deviceId;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            deviceId = android.provider.Settings.Secure.getString(
                    context.getContentResolver(),
                    android.provider.Settings.Secure.ANDROID_ID);
        } else {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    return "";
                }
            }

            final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (mTelephony != null) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                {
                    deviceId = mTelephony.getImei();
                }else {
                    deviceId = mTelephony.getDeviceId();
                }
            } else {
                deviceId = android.provider.Settings.Secure.getString(
                        context.getContentResolver(),
                        android.provider.Settings.Secure.ANDROID_ID);
            }
        }

        return deviceId;
    }
}