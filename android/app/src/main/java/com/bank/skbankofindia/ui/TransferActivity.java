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

import java.util.Locale;

public class TransferActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private EditText etDestination, etAmount, etPin;
    private Button btnConfirm;
    private TextToSpeech ttsEngine;
    private boolean isTtsReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

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

        // Simulate ultra-fast API processing (500ms delay)
        btnConfirm.postDelayed(() -> {
            progressDialog.dismiss();

            Toast.makeText(this, "Payment Successful!", Toast.LENGTH_LONG).show();

            // Speak Voice Announcement!
            speakVoiceAnnouncement(amountStr);

            finish();
        }, 600);
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
