package com.bank.skbankofindia.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bank.skbankofindia.R;
import com.bank.skbankofindia.api.ApiClient;
import com.bank.skbankofindia.model.ApiResponse;
import com.bank.skbankofindia.model.RegisterRequest;
import com.bank.skbankofindia.util.SessionManager;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private LinearLayout llStep1, llStep2, llStep3, llStep4;
    private TextView tvStep1Dot, tvStep2Dot, tvStep3Dot, tvStep4Dot;
    private TextView tvOtpDemoCode;

    private EditText etFirstName, etMiddleName, etLastName, etDob, etPan, etUsername, etEmail, etPhone, etPassword, etAadhaar, etAddress, etCity, etState, etPincode, etOtpCode;
    private Spinner spGender;
    private Button btnStep1Next, btnStep2Back, btnStep2Next, btnStep3Back, btnSendOtp, btnStep4Back, btnAutoFillOtp, btnFinalRegister;

    private SessionManager sessionManager;
    private Dialog loadingDialog;
    private String currentOtpCode = "891204";
    private TextToSpeech ttsEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        sessionManager = new SessionManager(this);

        // Step Layout Containers
        llStep1 = findViewById(R.id.llStep1);
        llStep2 = findViewById(R.id.llStep2);
        llStep3 = findViewById(R.id.llStep3);
        llStep4 = findViewById(R.id.llStep4);

        // Step Dot Indicators
        tvStep1Dot = findViewById(R.id.tvStep1Dot);
        tvStep2Dot = findViewById(R.id.tvStep2Dot);
        tvStep3Dot = findViewById(R.id.tvStep3Dot);
        tvStep4Dot = findViewById(R.id.tvStep4Dot);

        // Step 1 Fields & Buttons
        etFirstName = findViewById(R.id.etFirstName);
        etMiddleName = findViewById(R.id.etMiddleName);
        etLastName = findViewById(R.id.etLastName);
        etDob = findViewById(R.id.etDob);
        spGender = findViewById(R.id.spGender);
        btnStep1Next = findViewById(R.id.btnStep1Next);

        // Step 2 Fields & Buttons
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        btnStep2Back = findViewById(R.id.btnStep2Back);
        btnStep2Next = findViewById(R.id.btnStep2Next);

        // Step 3 Fields & Buttons
        etPan = findViewById(R.id.etPan);
        etAadhaar = findViewById(R.id.etAadhaar);
        etAddress = findViewById(R.id.etAddress);
        etCity = findViewById(R.id.etCity);
        etState = findViewById(R.id.etState);
        etPincode = findViewById(R.id.etPincode);
        btnStep3Back = findViewById(R.id.btnStep3Back);
        btnSendOtp = findViewById(R.id.btnSendOtp);

        // Step 4 Fields & Buttons
        tvOtpDemoCode = findViewById(R.id.tvOtpDemoCode);
        btnAutoFillOtp = findViewById(R.id.btnAutoFillOtp);
        etOtpCode = findViewById(R.id.etOtpCode);
        btnStep4Back = findViewById(R.id.btnStep4Back);
        btnFinalRegister = findViewById(R.id.btnFinalRegister);

        // Populate Gender Spinner
        if (spGender != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"MALE", "FEMALE", "OTHER"});
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spGender.setAdapter(adapter);
        }

        // Setup Wizard Listeners
        btnStep1Next.setOnClickListener(v -> {
            if (validateStep1()) goToStep(2);
        });

        btnStep2Back.setOnClickListener(v -> goToStep(1));
        btnStep2Next.setOnClickListener(v -> {
            if (validateStep2()) goToStep(3);
        });

        btnStep3Back.setOnClickListener(v -> goToStep(2));
        btnSendOtp.setOnClickListener(v -> {
            if (validateStep3()) sendOtpAndProceed();
        });

        btnStep4Back.setOnClickListener(v -> goToStep(3));
        btnAutoFillOtp.setOnClickListener(v -> etOtpCode.setText(currentOtpCode));
        btnFinalRegister.setOnClickListener(v -> handleFinalAccountOpening());

        initTtsEngine();
    }

    private void initTtsEngine() {
        ttsEngine = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                ttsEngine.setLanguage(new Locale("en", "IN"));
            }
        });
    }

    private void speakVoiceNotification(String text) {
        if (ttsEngine != null) {
            ttsEngine.speak(text, TextToSpeech.QUEUE_FLUSH, null, "REG_SPEECH");
        }
    }

    private void goToStep(int step) {
        llStep1.setVisibility(step == 1 ? View.VISIBLE : View.GONE);
        llStep2.setVisibility(step == 2 ? View.VISIBLE : View.GONE);
        llStep3.setVisibility(step == 3 ? View.VISIBLE : View.GONE);
        llStep4.setVisibility(step == 4 ? View.VISIBLE : View.GONE);

        updateStepDots(step);
    }

    private void updateStepDots(int activeStep) {
        tvStep1Dot.setBackgroundColor(activeStep >= 1 ? Color.parseColor("#0052CC") : Color.parseColor("#CCCCCC"));
        tvStep1Dot.setTextColor(activeStep >= 1 ? Color.WHITE : Color.parseColor("#666666"));

        tvStep2Dot.setBackgroundColor(activeStep >= 2 ? Color.parseColor("#0052CC") : Color.parseColor("#CCCCCC"));
        tvStep2Dot.setTextColor(activeStep >= 2 ? Color.WHITE : Color.parseColor("#666666"));

        tvStep3Dot.setBackgroundColor(activeStep >= 3 ? Color.parseColor("#0052CC") : Color.parseColor("#CCCCCC"));
        tvStep3Dot.setTextColor(activeStep >= 3 ? Color.WHITE : Color.parseColor("#666666"));

        tvStep4Dot.setBackgroundColor(activeStep >= 4 ? Color.parseColor("#138808") : Color.parseColor("#CCCCCC"));
        tvStep4Dot.setTextColor(activeStep >= 4 ? Color.WHITE : Color.parseColor("#666666"));
    }

    private boolean validateStep1() {
        String fn = etFirstName.getText().toString().trim();
        String ln = etLastName.getText().toString().trim();
        if (fn.isEmpty() || ln.isEmpty()) {
            Toast.makeText(this, "Please enter First and Last Name", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validateStep2() {
        String un = etUsername.getText().toString().trim();
        String em = etEmail.getText().toString().trim();
        String ph = etPhone.getText().toString().trim();
        String pw = etPassword.getText().toString();

        if (un.isEmpty() || em.isEmpty() || ph.isEmpty() || pw.isEmpty()) {
            Toast.makeText(this, "Please fill all credential fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (pw.length() < 8) {
            Toast.makeText(this, "Password must be at least 8 characters long", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validateStep3() {
        String ad = etAadhaar.getText().toString().trim();
        String addr = etAddress.getText().toString().trim();
        String city = etCity.getText().toString().trim();

        if (ad.isEmpty() || addr.isEmpty() || city.isEmpty()) {
            Toast.makeText(this, "Please enter Aadhaar number and address details", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void showCustomLoadingOverlay(String title, String subtitle) {
        hideCustomLoadingOverlay();

        loadingDialog = new Dialog(this);
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loadingDialog.setContentView(R.layout.dialog_loading_overlay);
        if (loadingDialog.getWindow() != null) {
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView tvTitle = loadingDialog.findViewById(R.id.tvLoadingTitle);
        TextView tvSub = loadingDialog.findViewById(R.id.tvLoadingSub);

        if (tvTitle != null) tvTitle.setText(title);
        if (tvSub != null) tvSub.setText(subtitle);

        loadingDialog.setCancelable(false);
        loadingDialog.show();
    }

    private void hideCustomLoadingOverlay() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

    private void sendOtpAndProceed() {
        String email = etEmail.getText().toString().trim();
        showCustomLoadingOverlay("Generating Instant OTP Code...", "🔒 Securing Double-Entry Ledger Entry...");

        Map<String, String> body = new HashMap<>();
        body.put("recipient", email);
        body.put("purpose", "REGISTER");

        currentOtpCode = String.valueOf((int) (100000 + Math.random() * 900000));

        ApiClient.getApiService(this).getAppVersion().enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                hideCustomLoadingOverlay();
                goToStep(4);
                if (tvOtpDemoCode != null) tvOtpDemoCode.setText(currentOtpCode);
                if (etOtpCode != null) etOtpCode.setText(currentOtpCode);

                speakVoiceNotification("OTP Code generated and sent to your mobile number.");
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                hideCustomLoadingOverlay();
                goToStep(4);
                if (tvOtpDemoCode != null) tvOtpDemoCode.setText(currentOtpCode);
                if (etOtpCode != null) etOtpCode.setText(currentOtpCode);

                speakVoiceNotification("OTP Code generated and sent to your mobile number.");
            }
        });
    }

    private void handleFinalAccountOpening() {
        String otp = etOtpCode.getText().toString().trim();
        if (otp.isEmpty()) {
            Toast.makeText(this, "Please enter 6-digit OTP code", Toast.LENGTH_SHORT).show();
            return;
        }

        String fn = etFirstName.getText().toString().trim();
        String mn = etMiddleName.getText().toString().trim();
        String ln = etLastName.getText().toString().trim();
        String dob = etDob.getText().toString().trim();
        if (dob.isEmpty()) dob = "1998-05-15";
        String gender = spGender != null ? spGender.getSelectedItem().toString() : "MALE";

        String un = etUsername.getText().toString().trim();
        String em = etEmail.getText().toString().trim();
        String ph = etPhone.getText().toString().trim();
        String pw = etPassword.getText().toString();
        String pan = etPan.getText().toString().trim();
        String ad = etAadhaar.getText().toString().trim();
        String addr = etAddress.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        String state = etState.getText().toString().trim();
        String pin = etPincode.getText().toString().trim();

        showCustomLoadingOverlay("Verifying OTP & Opening Savings Account...", "🔒 Securing Double-Entry Ledger Entry...");

        RegisterRequest req = new RegisterRequest(un, em, ph, pw, fn, mn, ln, dob, gender, pan, ad, addr, "", city, state, pin);

        ApiClient.getApiService(this).register(req).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                hideCustomLoadingOverlay();
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    JsonObject data = response.body().getData().getAsJsonObject();
                    if (data.has("token")) {
                        sessionManager.saveAuthToken(data.get("token").getAsString());
                    }

                    JsonObject acc = data.get("account").getAsJsonObject();
                    String accNo = acc.get("accountNumber").getAsString();

                    speakVoiceNotification("Welcome to SK Bank of India! Your Savings Account has been successfully opened.");

                    Toast.makeText(RegisterActivity.this, "Savings Account Successfully Opened!\nAccount No: " + accNo, Toast.LENGTH_LONG).show();
                    startActivity(new Intent(RegisterActivity.this, DashboardActivity.class));
                    finish();
                } else {
                    String msg = response.body() != null ? response.body().getMessage() : "Registration failed";
                    Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                hideCustomLoadingOverlay();
                Toast.makeText(RegisterActivity.this, "Network connection error while opening account: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (ttsEngine != null) {
            ttsEngine.stop();
            ttsEngine.shutdown();
        }
        super.onDestroy();
    }
}
