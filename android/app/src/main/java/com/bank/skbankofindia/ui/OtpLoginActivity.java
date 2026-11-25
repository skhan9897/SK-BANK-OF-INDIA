package com.bank.skbankofindia.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bank.skbankofindia.R;

public class OtpLoginActivity extends AppCompatActivity {

    private EditText etMobileNumber, etOtp;
    private Button btnGenerateOtp, btnVerifyOtp;
    private LinearLayout layoutOtpSection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_login);

        etMobileNumber = findViewById(R.id.etMobileNumber);
        etOtp = findViewById(R.id.etOtp);
        btnGenerateOtp = findViewById(R.id.btnGenerateOtp);
        btnVerifyOtp = findViewById(R.id.btnVerifyOtp);
        layoutOtpSection = findViewById(R.id.layoutOtpSection);

        btnGenerateOtp.setOnClickListener(v -> {
            String mobile = etMobileNumber.getText().toString().trim();
            if (mobile.length() < 10) {
                Toast.makeText(this, "Please enter a valid 10-digit mobile number", Toast.LENGTH_SHORT).show();
                return;
            }

            // Simulate OTP Generation
            Toast.makeText(this, "OTP Sent successfully! (Demo OTP: 1234)", Toast.LENGTH_LONG).show();
            layoutOtpSection.setVisibility(View.VISIBLE);
            etOtp.setText("1234"); // Auto-fill OTP
        });

        btnVerifyOtp.setOnClickListener(v -> {
            String otp = etOtp.getText().toString().trim();
            if (otp.equals("1234")) {
                Toast.makeText(this, "OTP Verified Successfully!", Toast.LENGTH_SHORT).show();
                // Proceed to Set PIN & Fingerprint setup
                Intent intent = new Intent(OtpLoginActivity.this, SetPinActivity.class);
                intent.putExtra("mobile", etMobileNumber.getText().toString().trim());
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Invalid OTP. Please enter 1234", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
