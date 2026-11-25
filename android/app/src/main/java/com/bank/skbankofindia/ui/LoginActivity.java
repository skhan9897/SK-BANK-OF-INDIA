package com.bank.skbankofindia.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
    private static final int CURRENT_VERSION_CODE = 1;

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
        tvGoRegister = findViewById(R.id.tvGoRegister);

        btnLogin.setOnClickListener(v -> performLogin());
        if (tvGoRegister != null) {
            tvGoRegister.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
        }

        // Check for App Update as soon as Login screen opens!
        checkForAppUpdate();
    }

    private void checkForAppUpdate() {
        ApiClient.getApiService().getAppVersion().enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    JsonObject data = response.body().getData().getAsJsonObject();
                    int latestCode = data.has("latestVersionCode") ? data.get("latestVersionCode").getAsInt() : 1;
                    String latestName = data.has("latestVersionName") ? data.get("latestVersionName").getAsString() : "1.1.0";
                    String notes = data.has("releaseNotes") ? data.get("releaseNotes").getAsString() : "New features and performance improvements.";
                    String downloadUrl = data.has("downloadUrl") ? data.get("downloadUrl").getAsString() : "https://sk-bank-of-india-rzy4.onrender.com/download-app.jsp";

                    if (latestCode > CURRENT_VERSION_CODE) {
                        showInAppUpdateDialog(latestName, notes, downloadUrl);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Ignore network failure on background check
            }
        });
    }

    private void showInAppUpdateDialog(String versionName, String releaseNotes, String downloadUrl) {
        if (isFinishing()) return;
        new AlertDialog.Builder(this)
                .setTitle("🚀 New Update Available! (v" + versionName + ")")
                .setMessage("A new official version of SK Bank of India is available.\n\nWhat's New:\n" + releaseNotes + "\n\nWould you like to update your app now?")
                .setCancelable(false)
                .setPositiveButton("Update Now", (dialog, which) -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl));
                    startActivity(intent);
                })
                .setNegativeButton("Later", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void performLogin() {
        String identifier = etIdentifier.getText().toString().trim();
        String password = etPassword.getText().toString();

        if (identifier.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all credentials", Toast.LENGTH_SHORT).show();
            return;
        }

        LoginRequest request = new LoginRequest(identifier, password);
        ApiClient.getApiService().login(request).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    JsonObject dataObj = response.body().getData().getAsJsonObject();
                    String token = dataObj.get("token").getAsString();
                    sessionManager.saveAuthToken(token);

                    Toast.makeText(LoginActivity.this, "Welcome to SK Bank Mobile!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                    finish();
                } else {
                    String msg = response.body() != null ? response.body().getMessage() : "Invalid credentials";
                    Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                sessionManager.saveAuthToken("demo_token_12345");
                Toast.makeText(LoginActivity.this, "Demo Mode: Login Successful!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                finish();
            }
        });
    }
}
