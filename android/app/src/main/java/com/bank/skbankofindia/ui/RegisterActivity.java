package com.bank.skbankofindia.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bank.skbankofindia.R;
import com.bank.skbankofindia.api.ApiClient;
import com.bank.skbankofindia.model.ApiResponse;
import com.bank.skbankofindia.model.RegisterRequest;
import com.bank.skbankofindia.util.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFirstName, etLastName, etUsername, etEmail, etPhone, etPassword, etAadhaar, etAddress, etCity, etState, etPincode;
    private Button btnRegister;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        sessionManager = new SessionManager(this);

        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etAadhaar = findViewById(R.id.etAadhaar);
        etAddress = findViewById(R.id.etAddress);
        etCity = findViewById(R.id.etCity);
        etState = findViewById(R.id.etState);
        etPincode = findViewById(R.id.etPincode);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> performRegistration());
    }

    private void performRegistration() {
        String fn = etFirstName.getText().toString().trim();
        String ln = etLastName.getText().toString().trim();
        String un = etUsername.getText().toString().trim();
        String em = etEmail.getText().toString().trim();
        String ph = etPhone.getText().toString().trim();
        String pw = etPassword.getText().toString();
        String ad = etAadhaar.getText().toString().trim();
        String addr = etAddress.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        String state = etState.getText().toString().trim();
        String pin = etPincode.getText().toString().trim();

        if (un.isEmpty() || em.isEmpty() || ph.isEmpty() || pw.isEmpty()) {
            Toast.makeText(this, "Please complete required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Opening SK Bank Savings Account...");
        dialog.setCancelable(false);
        dialog.show();

        RegisterRequest req = new RegisterRequest(un, em, ph, pw, fn, ln, "1998-05-15", ad, addr, city, state, pin);

        ApiClient.getApiService().register(req).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                dialog.dismiss();
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(RegisterActivity.this, "Account Opened Successfully!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
                } else {
                    String msg = response.body() != null ? response.body().getMessage() : "Registration failed";
                    Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(RegisterActivity.this, "Connection Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
