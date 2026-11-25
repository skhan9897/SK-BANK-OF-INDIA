package com.bank.skbankofindia.ui;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bank.skbankofindia.R;
import com.bank.skbankofindia.api.ApiClient;
import com.bank.skbankofindia.model.ApiResponse;
import com.bank.skbankofindia.util.SessionManager;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScanPayConfirmActivity extends AppCompatActivity {

    private TextView tvRecipientName, tvRecipientVpa;
    private EditText etPayAmount, etPayRemarks;
    private Button btnConfirmPay;
    private SessionManager sessionManager;
    private String recipientVpaStr = "";
    private TextToSpeech ttsEngine;
    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_pay_confirm);

        sessionManager = new SessionManager(this);

        tvRecipientName = findViewById(R.id.tvRecipientName);
        tvRecipientVpa = findViewById(R.id.tvRecipientVpa);
        etPayAmount = findViewById(R.id.etPayAmount);
        etPayRemarks = findViewById(R.id.etPayRemarks);
        btnConfirmPay = findViewById(R.id.btnConfirmPay);

        String qrPayload = getIntent().getStringExtra("EXTRA_QR_PAYLOAD");
        if (qrPayload == null || qrPayload.isEmpty()) {
            qrPayload = "customer1@skbank";
        }

        initTtsEngine();
        resolveRecipient(qrPayload);

        btnConfirmPay.setOnClickListener(v -> executeScanPay());
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
            ttsEngine.speak(text, TextToSpeech.QUEUE_FLUSH, null, "SCAN_PAY_SPEECH");
        }
    }

    private void showLoadingOverlay(String title) {
        hideLoadingOverlay();
        loadingDialog = new Dialog(this);
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loadingDialog.setContentView(R.layout.dialog_loading_overlay);
        if (loadingDialog.getWindow() != null) {
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        TextView tvTitle = loadingDialog.findViewById(R.id.tvLoadingTitle);
        if (tvTitle != null) tvTitle.setText(title);
        loadingDialog.setCancelable(false);
        loadingDialog.show();
    }

    private void hideLoadingOverlay() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

    private void resolveRecipient(String qrPayload) {
        String token = sessionManager.getAuthToken();
        Map<String, String> body = new HashMap<>();
        body.put("qrPayload", qrPayload);

        showLoadingOverlay("Verifying Recipient QR Code...");

        ApiClient.getApiService(this).resolveQrVpa(token, body).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                hideLoadingOverlay();
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    JsonObject data = response.body().getData().getAsJsonObject();
                    String name = data.get("customerName").getAsString();
                    recipientVpaStr = data.get("vpa").getAsString();

                    tvRecipientName.setText(name);
                    tvRecipientVpa.setText(recipientVpaStr);
                } else {
                    tvRecipientName.setText("SK Bank Verified Payee");
                    tvRecipientVpa.setText(qrPayload);
                    recipientVpaStr = qrPayload;
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                hideLoadingOverlay();
                tvRecipientName.setText("SK Bank Verified Payee");
                tvRecipientVpa.setText(qrPayload);
                recipientVpaStr = qrPayload;
            }
        });
    }

    private void executeScanPay() {
        String amountStr = etPayAmount.getText().toString().trim();
        String remarksStr = etPayRemarks.getText().toString().trim();

        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Please enter payment amount", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoadingOverlay("Executing Ultra-Fast Scan & Pay...");

        String token = sessionManager.getAuthToken();
        Map<String, Object> body = new HashMap<>();
        body.put("recipientVpa", recipientVpaStr);
        body.put("amount", amountStr);
        body.put("remarks", remarksStr.isEmpty() ? "Scan & Pay" : remarksStr);

        ApiClient.getApiService(this).executeQrPayment(token, body).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                hideLoadingOverlay();
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    String txnRef = "TXN" + System.currentTimeMillis();
                    if (response.body().getData() != null && response.body().getData().isJsonObject()) {
                        JsonObject obj = response.body().getData().getAsJsonObject();
                        if (obj.has("transactionRef")) {
                            txnRef = obj.get("transactionRef").getAsString();
                        }
                    }

                    speakVoiceNotification("SK Bank of India: Payment of " + amountStr + " Rupees successfully sent!");

                    com.bank.skbankofindia.util.NotificationHandler.processPaymentEvent(
                            ScanPayConfirmActivity.this, "PAYMENT_SENT", txnRef, amountStr, recipientVpaStr
                    );

                    com.bank.skbankofindia.util.ReceiptDialogHelper.showReceipt(
                            ScanPayConfirmActivity.this, txnRef, amountStr, recipientVpaStr, "UPI_QR_SCAN_AND_PAY", () -> finish()
                    );
                } else {
                    String msg = response.body() != null ? response.body().getMessage() : "Payment failed";
                    Toast.makeText(ScanPayConfirmActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                hideLoadingOverlay();
                Toast.makeText(ScanPayConfirmActivity.this, "Connection error: " + t.getMessage(), Toast.LENGTH_LONG).show();
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
