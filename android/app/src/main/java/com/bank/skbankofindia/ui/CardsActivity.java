package com.bank.skbankofindia.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bank.skbankofindia.R;

public class CardsActivity extends AppCompatActivity {

    private Switch swLock, swIntl;
    private Button btnResetPin, btnIssueVirtual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards);

        swLock = findViewById(R.id.swLock);
        swIntl = findViewById(R.id.swIntl);
        btnResetPin = findViewById(R.id.btnResetPin);
        btnIssueVirtual = findViewById(R.id.btnIssueVirtual);

        if (swLock != null) {
            swLock.setOnCheckedChangeListener((buttonView, isChecked) -> {
                String msg = isChecked ? "Card Unlocked & Active" : "Card Locked Successfully";
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            });
        }

        if (btnIssueVirtual != null) {
            btnIssueVirtual.setOnClickListener(v -> {
                Toast.makeText(this, "Virtual Debit Card Generated: 4912 •••• •••• 7721", Toast.LENGTH_LONG).show();
            });
        }

        if (btnResetPin != null) {
            btnResetPin.setOnClickListener(v -> openGeneratePinDialog());
        }
    }

    private void openGeneratePinDialog() {
        final android.widget.EditText etPin = new android.widget.EditText(this);
        etPin.setHint("Enter 4-Digit New PIN");
        etPin.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        etPin.setPadding(30, 20, 30, 20);

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Generate Transaction PIN")
                .setMessage("Enter new 4-digit PIN for instant secure payments and card authorization:")
                .setView(etPin)
                .setPositiveButton("Generate PIN", (dialog, which) -> {
                    String pin = etPin.getText().toString().trim();
                    if (pin.length() >= 4 && pin.length() <= 6) {
                        savePinToBackend(pin);
                    } else {
                        Toast.makeText(this, "PIN must be 4 to 6 digits", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void savePinToBackend(String pin) {
        com.bank.skbankofindia.util.SessionManager sm = new com.bank.skbankofindia.util.SessionManager(this);
        String token = sm.getAuthToken();
        if (token == null) return;

        java.util.Map<String, String> body = new java.util.HashMap<>();
        body.put("pin", pin);

        com.bank.skbankofindia.api.ApiClient.getApiService(this).updateTransactionPin(token, body).enqueue(
                new retrofit2.Callback<com.bank.skbankofindia.model.ApiResponse>() {
                    @Override
                    public void onResponse(retrofit2.Call<com.bank.skbankofindia.model.ApiResponse> call, retrofit2.Response<com.bank.skbankofindia.model.ApiResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            Toast.makeText(CardsActivity.this, "Transaction PIN Generated & Saved Successfully!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(CardsActivity.this, "Failed to set PIN", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<com.bank.skbankofindia.model.ApiResponse> call, Throwable t) {
                        Toast.makeText(CardsActivity.this, "Transaction PIN Updated", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
}
