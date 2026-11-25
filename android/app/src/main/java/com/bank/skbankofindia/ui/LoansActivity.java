package com.bank.skbankofindia.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bank.skbankofindia.R;

public class LoansActivity extends AppCompatActivity {

    private Button btnApplyLoan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loans);

        btnApplyLoan = findViewById(R.id.btnApplyLoan);
        if (btnApplyLoan != null) {
            btnApplyLoan.setOnClickListener(v -> {
                Toast.makeText(this, "Pre-Approved Loan Application Submitted Successfully!", Toast.LENGTH_LONG).show();
            });
        }
    }
}
