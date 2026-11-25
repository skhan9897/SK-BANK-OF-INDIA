package com.bank.skbankofindia.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bank.skbankofindia.R;

public class QrScannerActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_CODE = 101;
    private Button btnToggleFlash, btnSimulateScan;
    private boolean isFlashOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner);

        btnToggleFlash = findViewById(R.id.btnToggleFlash);
        btnSimulateScan = findViewById(R.id.btnSimulateScan);

        btnToggleFlash.setOnClickListener(v -> {
            isFlashOn = !isFlashOn;
            Toast.makeText(this, isFlashOn ? "Camera Flash On" : "Camera Flash Off", Toast.LENGTH_SHORT).show();
        });

        btnSimulateScan.setOnClickListener(v -> launchScanResult("customer1@skbank"));

        checkCameraPermission();
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera Permission Granted for QR Scanner", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Camera permission is required to scan QR codes", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void launchScanResult(String payload) {
        Intent intent = new Intent(this, ScanPayConfirmActivity.class);
        intent.putExtra("EXTRA_QR_PAYLOAD", payload);
        startActivity(intent);
        finish();
    }
}
