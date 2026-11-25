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
            btnResetPin.setOnClickListener(v -> {
                Toast.makeText(this, "Card PIN reset link sent via SMS to registered mobile", Toast.LENGTH_LONG).show();
            });
        }
    }
}
