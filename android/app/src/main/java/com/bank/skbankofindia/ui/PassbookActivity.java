package com.bank.skbankofindia.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bank.skbankofindia.R;

public class PassbookActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passbook);

        Toast.makeText(this, "Real-time Double-Entry Passbook Loaded", Toast.LENGTH_SHORT).show();
    }
}
