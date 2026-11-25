package com.bank.skbankofindia.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bank.skbankofindia.R;

public class DashboardActivity extends AppCompatActivity {

    private TextView tvBalance, tvAccountNo;
    private Button btnSendMoney, btnCards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        tvBalance = findViewById(R.id.tvBalance);
        tvAccountNo = findViewById(R.id.tvAccountNo);
        btnSendMoney = findViewById(R.id.btnSendMoney);

        btnSendMoney.setOnClickListener(v -> startActivity(new Intent(this, TransferActivity.class)));
    }
}
