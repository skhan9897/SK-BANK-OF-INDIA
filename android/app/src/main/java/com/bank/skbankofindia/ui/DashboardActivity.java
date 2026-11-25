package com.bank.skbankofindia.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bank.skbankofindia.R;
import com.bank.skbankofindia.api.ApiClient;
import com.bank.skbankofindia.model.ApiResponse;
import com.bank.skbankofindia.util.SessionManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity {

    private TextView tvBalance, tvAccountNo, tvWelcomeUser;
    private ImageButton btnToggleBalance;
    private Button btnSendMoney, btnLogout;
    private SessionManager sessionManager;
    private boolean isBalanceHidden = true;
    private String realBalance = "10,000.00";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        sessionManager = new SessionManager(this);

        tvBalance = findViewById(R.id.tvBalance);
        tvAccountNo = findViewById(R.id.tvAccountNo);
        btnSendMoney = findViewById(R.id.btnSendMoney);
        btnLogout = findViewById(R.id.btnLogout);
        btnToggleBalance = findViewById(R.id.btnToggleBalance);

        if (btnSendMoney != null) {
            btnSendMoney.setOnClickListener(v -> startActivity(new Intent(this, TransferActivity.class)));
        }

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                sessionManager.clearSession();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            });
        }

        if (btnToggleBalance != null) {
            btnToggleBalance.setOnClickListener(v -> toggleBalance());
        }

        fetchAccountsFromBackend();
    }

    private void toggleBalance() {
        if (isBalanceHidden) {
            tvBalance.setText("₹ " + realBalance);
            isBalanceHidden = false;
        } else {
            tvBalance.setText("••••••••");
            isBalanceHidden = true;
        }
    }

    private void fetchAccountsFromBackend() {
        String token = sessionManager.getAuthToken();
        if (token == null) return;

        ApiClient.getApiService().getCustomerAccounts(token).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    JsonArray arr = response.body().getData().getAsJsonArray();
                    if (arr.size() > 0) {
                        JsonObject acc = arr.get(0).getAsJsonObject();
                        String accNo = acc.get("accountNumber").getAsString();
                        double bal = acc.get("availableBalance").getAsDouble();
                        long accId = acc.get("accountId").getAsLong();

                        realBalance = String.format("%,.2f", bal);
                        tvAccountNo.setText("A/c " + accNo);
                        tvBalance.setText("••••••••");

                        sessionManager.saveUserAccount(accId, accNo, realBalance, "Customer");
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(DashboardActivity.this, "Offline Mode: Showing cached balance", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
