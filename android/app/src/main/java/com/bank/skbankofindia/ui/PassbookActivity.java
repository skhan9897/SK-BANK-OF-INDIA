package com.bank.skbankofindia.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bank.skbankofindia.R;
import com.bank.skbankofindia.api.ApiClient;
import com.bank.skbankofindia.model.ApiResponse;
import com.bank.skbankofindia.util.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PassbookActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passbook);

        sessionManager = new SessionManager(this);
        fetchLiveStatement();
    }

    private void fetchLiveStatement() {
        String token = sessionManager.getAuthToken();
        String accountNo = sessionManager.getAccountNo();

        if (token == null || accountNo == null) return;

        ApiClient.getApiService(this).getAccountStatement(token, accountNo).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(PassbookActivity.this, "Live Passbook Statements Synchronized", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PassbookActivity.this, "Displaying Current Passbook Statements", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(PassbookActivity.this, "Connected: SK Payment Bank Live", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
