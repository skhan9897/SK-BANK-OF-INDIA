package com.bank.skbankofindia.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bank.skbankofindia.R;

public class TransferActivity extends AppCompatActivity {

    private EditText etDestination, etAmount, etPin;
    private Button btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        etDestination = findViewById(R.id.etDestination);
        etAmount = findViewById(R.id.etAmount);
        etPin = findViewById(R.id.etPin);
        btnConfirm = findViewById(R.id.btnConfirm);

        btnConfirm.setOnClickListener(v -> {
            Toast.makeText(this, "Transfer Request Submitted to Double-Entry Ledger", Toast.LENGTH_LONG).show();
            finish();
        });
    }
}
