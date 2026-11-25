package com.bank.skbankofindia.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.bank.skbankofindia.R;
import com.bank.skbankofindia.api.ApiClient;
import com.bank.skbankofindia.model.ApiResponse;
import com.bank.skbankofindia.model.LoginRequest;
import com.bank.skbankofindia.util.SessionManager;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etIdentifier, etPassword;
    private Button btnLogin;
    private TextView tvGoRegister;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, DashboardActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        etIdentifier = findViewById(R.id.etIdentifier);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        Button btnOtpLogin = findViewById(R.id.btnOtpLogin);
        Button btnFingerprintLogin = findViewById(R.id.btnFingerprintLogin);
        tvGoRegister = findViewById(R.id.tvGoRegister);

        btnLogin.setOnClickListener(v -> performLogin());
        btnOtpLogin.setOnClickListener(v -> startActivity(new Intent(this, OtpLoginActivity.class)));

        if (sessionManager.isBiometricEnabled()) {
            btnFingerprintLogin.setVisibility(View.VISIBLE);
            btnFingerprintLogin.setOnClickListener(v -> authenticateWithFingerprint());
        }

        if (tvGoRegister != null) {
            tvGoRegister.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
        }

        checkForAppUpdate();
    }

    private int getInstalledVersionCode() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (Exception e) {
            return 1;
        }
    }

    private void checkForAppUpdate() {
        ApiClient.getApiService(this).getAppVersion().enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    JsonObject data = response.body().getData().getAsJsonObject();
                    int latestCode = data.has("latestVersionCode") ? data.get("latestVersionCode").getAsInt() : 1;
                    String latestName = data.has("latestVersionName") ? data.get("latestVersionName").getAsString() : "1.1.0";
                    String notes = data.has("releaseNotes") ? data.get("releaseNotes").getAsString() : "New features and performance improvements.";
                    String downloadUrl = data.has("downloadUrl") ? data.get("downloadUrl").getAsString() : ApiClient.WEBSITE_DOWNLOAD_URL;
                    boolean mandatory = data.has("mandatoryUpdate") && data.get("mandatoryUpdate").getAsBoolean();

                    if (latestCode > getInstalledVersionCode()) {
                        showInAppUpdateDialog(latestName, notes, downloadUrl, mandatory);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Ignore network failure on background check
            }
        });
    }

    private void showInAppUpdateDialog(String versionName, String releaseNotes, String downloadUrl, boolean mandatory) {
        if (isFinishing()) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("New Update Available (v" + versionName + ")")
                .setMessage("SK Payment Bank has a new version available. Update now to get the latest features and improvements.\n\nWhat's New:\n" + releaseNotes)
                .setCancelable(!mandatory)
                .setPositiveButton("Update Now", (dialog, which) -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl));
                    startActivity(intent);
                });

        if (!mandatory) {
            builder.setNegativeButton("Later", (dialog, which) -> dialog.dismiss());
        }

        builder.show();
    }

    private void performLogin() {
        String identifier = etIdentifier.getText().toString().trim();
        String password = etPassword.getText().toString();

        if (identifier.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all credentials", Toast.LENGTH_SHORT).show();
            return;
        }

        LoginRequest request = new LoginRequest(identifier, password);
        ApiClient.getApiService(this).login(request).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    JsonObject dataObj = response.body().getData().getAsJsonObject();
                    String token = dataObj.get("token").getAsString();
                    sessionManager.saveAuthToken(token);

                    Toast.makeText(LoginActivity.this, "Welcome to SK Payment Bank!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                    finish();
                } else {
                    String msg = response.body() != null ? response.body().getMessage() : "Invalid credentials";
                    Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Unable to connect to SK Bank server: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void authenticateWithFingerprint() {
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("SK Bank Fingerprint Login")
                .setSubtitle("Touch the fingerprint sensor to log in securely")
                .setNegativeButtonText("Cancel")
                .build();

        BiometricPrompt biometricPrompt = new BiometricPrompt(this, ContextCompat.getMainExecutor(this), new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(LoginActivity.this, "Fingerprint Login Successful!", Toast.LENGTH_SHORT).show();
                sessionManager.saveAuthToken("demo_token_biometric");
                startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                finish();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(LoginActivity.this, "Fingerprint authentication failed", Toast.LENGTH_SHORT).show();
            }
        });

        biometricPrompt.authenticate(promptInfo);
    }
}
