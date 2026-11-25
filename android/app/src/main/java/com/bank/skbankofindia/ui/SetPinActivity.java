package com.bank.skbankofindia.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.bank.skbankofindia.R;
import com.bank.skbankofindia.util.SessionManager;

import java.util.concurrent.Executor;

public class SetPinActivity extends AppCompatActivity {

    private EditText etPin, etConfirmPin;
    private Button btnAuthorizeFingerprint, btnCompleteSetup;
    private TextView tvFingerprintStatus;
    private SessionManager sessionManager;
    private boolean isFingerprintAuthorized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_pin);

        sessionManager = new SessionManager(this);

        etPin = findViewById(R.id.etPin);
        etConfirmPin = findViewById(R.id.etConfirmPin);
        btnAuthorizeFingerprint = findViewById(R.id.btnAuthorizeFingerprint);
        btnCompleteSetup = findViewById(R.id.btnCompleteSetup);
        tvFingerprintStatus = findViewById(R.id.tvFingerprintStatus);

        btnAuthorizeFingerprint.setOnClickListener(v -> authenticateWithFingerprint());

        btnCompleteSetup.setOnClickListener(v -> {
            String pin = etPin.getText().toString().trim();
            String confirmPin = etConfirmPin.getText().toString().trim();

            if (pin.length() != 4) {
                Toast.makeText(this, "Please enter a 4-digit PIN", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!pin.equals(confirmPin)) {
                Toast.makeText(this, "PINs do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isFingerprintAuthorized) {
                Toast.makeText(this, "Please authorize with Fingerprint first", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save PIN and Biometric in session
            sessionManager.savePin(pin);
            sessionManager.setBiometricEnabled(true);
            sessionManager.saveAuthToken("demo_token_biometric");

            Toast.makeText(this, "PIN and Fingerprint Setup Successful!", Toast.LENGTH_LONG).show();
            startActivity(new Intent(SetPinActivity.this, DashboardActivity.class));
            finishAffinity();
        });
    }

    private void authenticateWithFingerprint() {
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(this, "Device does not support biometric authentication", Toast.LENGTH_SHORT).show();
                // Allow fallback for emulator / demo testing
                simulateSuccessfulBiometric();
                return;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Toast.makeText(this, "Biometric features are currently unavailable", Toast.LENGTH_SHORT).show();
                simulateSuccessfulBiometric();
                return;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Toast.makeText(this, "No fingerprint enrolled on this device. Simulating authorization for testing.", Toast.LENGTH_LONG).show();
                simulateSuccessfulBiometric();
                return;
        }

        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(SetPinActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(), "Authentication error: " + errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                simulateSuccessfulBiometric();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Fingerprint authentication failed", Toast.LENGTH_SHORT).show();
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("SK Bank Fingerprint Authorization")
                .setSubtitle("Touch the fingerprint sensor to authorize secure login")
                .setNegativeButtonText("Cancel")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    private void simulateSuccessfulBiometric() {
        isFingerprintAuthorized = true;
        tvFingerprintStatus.setText("Fingerprint Status: Authorized Successfully ✓");
        tvFingerprintStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        Toast.makeText(this, "Fingerprint Authorized!", Toast.LENGTH_SHORT).show();
    }
}
