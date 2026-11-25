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
    private Button btnConfirm;
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

        // Initialize TextToSpeech Voice Engine
        ttsEngine = new TextToSpeech(this, this);

        btnConfirm.setOnClickListener(v -> executeInstantPayment());
    }

    private void executeInstantPayment() {
        String destination = etDestination.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();

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

        TransferRequest req = new TransferRequest(sourceAccountId, 1L, amountStr, "INTERNAL", "Mobile Transfer to " + destination);

        ApiClient.getApiService().processFundTransfer(token, req).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(TransferActivity.this, "Payment Successful!", Toast.LENGTH_LONG).show();

                    // Speak Real-Time Voice Announcement!
                    speakVoiceAnnouncement(amountStr);

                    finish();
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
