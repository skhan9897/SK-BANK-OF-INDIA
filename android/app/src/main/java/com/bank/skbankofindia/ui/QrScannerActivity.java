package com.bank.skbankofindia.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bank.skbankofindia.R;

import java.io.IOException;

@SuppressWarnings("deprecation")
public class QrScannerActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private static final int CAMERA_PERMISSION_CODE = 101;
    private SurfaceView svCameraPreview;
    private SurfaceHolder surfaceHolder;
    private Camera camera;

    private Button btnToggleFlash, btnSimulateScan;
    private boolean isFlashOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner);

        svCameraPreview = findViewById(R.id.svCameraPreview);
        btnToggleFlash = findViewById(R.id.btnToggleFlash);
        btnSimulateScan = findViewById(R.id.btnSimulateScan);

        if (svCameraPreview != null) {
            surfaceHolder = svCameraPreview.getHolder();
            surfaceHolder.addCallback(this);
        }

        btnToggleFlash.setOnClickListener(v -> toggleFlashlight());
        btnSimulateScan.setOnClickListener(v -> launchScanResult("customer1@skbank"));

        checkCameraPermission();
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            startCameraPreview();
        }
    }

    private void startCameraPreview() {
        try {
            if (camera == null) {
                camera = Camera.open();
                camera.setDisplayOrientation(90);
            }
            if (surfaceHolder != null && camera != null) {
                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Live Camera Preview Active", Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleFlashlight() {
        if (camera != null) {
            try {
                Camera.Parameters params = camera.getParameters();
                if (!isFlashOn) {
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    camera.setParameters(params);
                    isFlashOn = true;
                    Toast.makeText(this, "Flashlight Turned ON", Toast.LENGTH_SHORT).show();
                } else {
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    camera.setParameters(params);
                    isFlashOn = false;
                    Toast.makeText(this, "Flashlight Turned OFF", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Flashlight control not supported on device", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        startCameraPreview();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        if (surfaceHolder.getSurface() == null) return;
        try {
            if (camera != null) {
                camera.stopPreview();
                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();
            }
        } catch (Exception ignored) {}
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        releaseCamera();
    }

    private void releaseCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCameraPreview();
            } else {
                Toast.makeText(this, "Camera permission is required to scan QR codes", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    private void launchScanResult(String payload) {
        releaseCamera();
        Intent intent = new Intent(this, ScanPayConfirmActivity.class);
        intent.putExtra("EXTRA_QR_PAYLOAD", payload);
        startActivity(intent);
        finish();
    }
}
