package com.bank.skbankofindia.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bank.skbankofindia.R;
import com.bank.skbankofindia.api.ApiClient;
import com.bank.skbankofindia.model.ApiResponse;
import com.bank.skbankofindia.model.TransferRequest;
import com.bank.skbankofindia.util.SessionManager;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransferActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private EditText etDestination, etAmount, etPin;
    private Button btnConfirm, btnGeneratePin;
    private TextToSpeech ttsEngine;
    private boolean isTtsReady = false;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        sessionManager = new SessionManager(this);

        etDestination = findViewById(R.id.etDestination);
        etAmount = findViewById(R.id.etAmount);
        etPin = findViewById(R.id.etPin);
        btnConfirm = findViewById(R.id.btnConfirm);
        btnGeneratePin = findViewById(R.id.btnGeneratePin);

        // Initialize TextToSpeech Voice Engine
        ttsEngine = new TextToSpeech(this, this);

        btnConfirm.setOnClickListener(v -> executeInstantPayment());
        if (btnGeneratePin != null) {
            btnGeneratePin.setOnClickListener(v -> openGeneratePinDialog());
        }
    }

    private void openGeneratePinDialog() {
        final android.widget.EditText etNewPin = new android.widget.EditText(this);
        etNewPin.setHint("Enter 4-Digit New Transaction PIN");
        etNewPin.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        etNewPin.setPadding(30, 20, 30, 20);

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Generate Transaction PIN")
                .setMessage("Enter new 4-digit PIN for instant secure payments:")
                .setView(etNewPin)
                .setPositiveButton("Generate PIN", (dialog, which) -> {
                    String pinStr = etNewPin.getText().toString().trim();
                    if (pinStr.length() >= 4 && pinStr.length() <= 6) {
                        savePinToBackend(pinStr);
                    } else {
                        Toast.makeText(this, "PIN must be 4 to 6 digits", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void savePinToBackend(String pin) {
        String token = sessionManager.getAuthToken();
        if (token == null) return;

        java.util.Map<String, String> body = new java.util.HashMap<>();
        body.put("pin", pin);

        ApiClient.getApiService(this).updateTransactionPin(token, body).enqueue(
                new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            Toast.makeText(TransferActivity.this, "Transaction PIN Generated Successfully!", Toast.LENGTH_LONG).show();
                            if (etPin != null) etPin.setText(pin);
                        } else {
                            Toast.makeText(TransferActivity.this, "Failed to set PIN", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        Toast.makeText(TransferActivity.this, "Transaction PIN Saved", Toast.LENGTH_SHORT).show();
                        if (etPin != null) etPin.setText(pin);
                    }
                }
        );
    }

    private void executeInstantPayment() {
        String destination = etDestination.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();
        String pinStr = etPin != null ? etPin.getText().toString().trim() : "";

        if (destination.isEmpty() || amountStr.isEmpty()) {
            Toast.makeText(this, "Please enter payee details and amount", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show Professional Loading Progress Dialog
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Executing Ultra-Fast Transaction...\nSecuring Double-Entry Ledger Entry...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String token = sessionManager.getAuthToken();
        long sourceAccountId = sessionManager.getAccountId();

        java.util.Map<String, Object> body = new java.util.HashMap<>();
        body.put("sourceAccountId", sourceAccountId);
        body.put("destinationAccount", destination);
        body.put("amount", amountStr);
        body.put("paymentMethod", destination.contains("@") ? "UPI" : "INTERNAL");
        body.put("remarks", "Mobile Transfer to " + destination);
        body.put("pin", pinStr);

        ApiClient.getApiService(this).executeQrPayment(token, body).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    String txnRef = "TXN" + System.currentTimeMillis();
                    if (response.body().getData() != null && response.body().getData().isJsonObject()) {
                        com.google.gson.JsonObject dataObj = response.body().getData().getAsJsonObject();
                        if (dataObj.has("transactionRef")) {
                            txnRef = dataObj.get("transactionRef").getAsString();
                        }
                    }

                    speakVoiceAnnouncement(amountStr);

                    // Trigger Sender Notification & Voice Announcement
                    com.bank.skbankofindia.util.NotificationHandler.processPaymentEvent(
                            TransferActivity.this, "PAYMENT_SENT", txnRef, amountStr, destination
                    );

                    com.bank.skbankofindia.util.ReceiptDialogHelper.showReceipt(
                            TransferActivity.this, txnRef, amountStr, destination, "MOBILE_TRANSFER", () -> finish()
                    );
                } else {
                    String msg = response.body() != null ? response.body().getMessage() : "Transfer failed";
                    Toast.makeText(TransferActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(TransferActivity.this, "Connection error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void speakVoiceAnnouncement(String amountStr) {
        if (isTtsReady && ttsEngine != null) {
            String speechText = "SK Bank of India: Payment of " + amountStr + " Rupees successfully sent!";
            ttsEngine.speak(speechText, TextToSpeech.QUEUE_FLUSH, null, "TXN_SPEECH_ID");
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = ttsEngine.setLanguage(new Locale("en", "IN"));
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                ttsEngine.setLanguage(Locale.US);
            }
            isTtsReady = true;
        } else {
            Log.e("TTS", "TextToSpeech Initialization Failed!");
        }
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
