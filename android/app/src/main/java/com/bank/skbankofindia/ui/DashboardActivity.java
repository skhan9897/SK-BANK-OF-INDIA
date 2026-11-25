package com.bank.skbankofindia.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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

    private TextView tvDashBalance, tvDashAccountNo, tvWelcomeUser, tvProfileAvatar, tvUpiIdDisplay;
    private Button btnToggleBalance;
    private TextView btnLogout, btnHeaderSearch, btnHeaderNotif, btnCentralScanPay;
    private android.view.View btnTransferMobile, btnTransferBank, btnTransferSelf, btnCheckBalance;
    private android.view.View btnWalletCard, btnMyQrCard, btnChatCard;
    private android.view.View btnMobileRecharge, btnFastag, btnDth, btnElectricity;
    private Button navBank, navCards, navLoans, navPassbook;

    private SessionManager sessionManager;
    private boolean isBalanceHidden = false;
    private String realBalance = "0.00";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        sessionManager = new SessionManager(this);

        tvDashBalance = findViewById(R.id.tvDashBalance);
        tvDashAccountNo = findViewById(R.id.tvDashAccountNo);
        tvWelcomeUser = findViewById(R.id.tvWelcomeUser);
        tvProfileAvatar = findViewById(R.id.tvProfileAvatar);
        tvUpiIdDisplay = findViewById(R.id.tvUpiIdDisplay);

        btnToggleBalance = findViewById(R.id.btnToggleBalance);
        btnLogout = findViewById(R.id.btnLogout);
        btnHeaderSearch = findViewById(R.id.btnHeaderSearch);
        btnHeaderNotif = findViewById(R.id.btnHeaderNotif);
        btnCentralScanPay = findViewById(R.id.btnCentralScanPay);

        btnTransferMobile = findViewById(R.id.btnTransferMobile);
        btnTransferBank = findViewById(R.id.btnTransferBank);
        btnTransferSelf = findViewById(R.id.btnTransferSelf);
        btnCheckBalance = findViewById(R.id.btnCheckBalance);

        btnWalletCard = findViewById(R.id.btnWalletCard);
        btnMyQrCard = findViewById(R.id.btnMyQrCard);
        btnChatCard = findViewById(R.id.btnChatCard);

        btnMobileRecharge = findViewById(R.id.btnMobileRecharge);
        btnFastag = findViewById(R.id.btnFastag);
        btnDth = findViewById(R.id.btnDth);
        btnElectricity = findViewById(R.id.btnElectricity);

        navBank = findViewById(R.id.navBank);
        navCards = findViewById(R.id.navCards);
        navLoans = findViewById(R.id.navLoans);
        navPassbook = findViewById(R.id.navPassbook);

        // Set Click Listeners
        if (btnTransferMobile != null) btnTransferMobile.setOnClickListener(v -> startActivity(new Intent(this, TransferActivity.class)));
        if (btnTransferBank != null) btnTransferBank.setOnClickListener(v -> startActivity(new Intent(this, TransferActivity.class)));
        if (btnTransferSelf != null) btnTransferSelf.setOnClickListener(v -> startActivity(new Intent(this, TransferActivity.class)));
        if (btnCheckBalance != null) btnCheckBalance.setOnClickListener(v -> toggleBalance());

        if (btnCentralScanPay != null) btnCentralScanPay.setOnClickListener(v -> startActivity(new Intent(this, QrScannerActivity.class)));

        if (btnWalletCard != null) btnWalletCard.setOnClickListener(v -> Toast.makeText(this, "SK Bank Wallet Active", Toast.LENGTH_SHORT).show());
        if (btnMyQrCard != null) btnMyQrCard.setOnClickListener(v -> startActivity(new Intent(this, MyQrActivity.class)));
        if (btnChatCard != null) btnChatCard.setOnClickListener(v -> startActivity(new Intent(this, ChatActivity.class)));

        if (btnMobileRecharge != null || btnFastag != null || btnDth != null || btnElectricity != null) {
            android.view.View.OnClickListener billListener = v -> Toast.makeText(this, "Utility Bill Payments & Recharge Active", Toast.LENGTH_SHORT).show();
            if (btnMobileRecharge != null) btnMobileRecharge.setOnClickListener(billListener);
            if (btnFastag != null) btnFastag.setOnClickListener(billListener);
            if (btnDth != null) btnDth.setOnClickListener(billListener);
            if (btnElectricity != null) btnElectricity.setOnClickListener(billListener);
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

        // Bottom Navigation Listeners
        if (navBank != null) navBank.setOnClickListener(v -> fetchAccountsFromBackend());
        if (navCards != null) navCards.setOnClickListener(v -> startActivity(new Intent(this, CardsActivity.class)));
        if (navLoans != null) navLoans.setOnClickListener(v -> startActivity(new Intent(this, LoansActivity.class)));
        if (navPassbook != null) navPassbook.setOnClickListener(v -> startActivity(new Intent(this, PassbookActivity.class)));

        fetchAccountsFromBackend();
        checkForAppUpdate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchAccountsFromBackend();
        checkForAppUpdate();
    }

    private int getInstalledVersionCode() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (Exception e) {
            return 1;
        }
    }

    private void toggleBalance() {
        if (tvDashBalance == null) return;
        if (isBalanceHidden) {
            tvDashBalance.setText("₹ " + realBalance);
            isBalanceHidden = false;
        } else {
            tvDashBalance.setText("••••••••");
            isBalanceHidden = true;
        }
    }

    private void fetchAccountsFromBackend() {
        String token = sessionManager.getAuthToken();
        if (token == null) return;

        ApiClient.getApiService(this).getCustomerAccounts(token).enqueue(new Callback<ApiResponse>() {
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
                        if (tvDashAccountNo != null) tvDashAccountNo.setText(accNo);
                        if (tvDashBalance != null) tvDashBalance.setText("₹ " + realBalance);

                        sessionManager.saveUserAccount(accId, accNo, realBalance, "Customer");
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(DashboardActivity.this, "Active: Connected to SK Payment Bank", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkForAppUpdate() {
        ApiClient.getApiService(this).getAppVersion().enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    JsonObject data = response.body().getData().getAsJsonObject();
                    int latestCode = data.has("latestVersionCode") ? data.get("latestVersionCode").getAsInt() : 1;
                    String latestName = data.has("latestVersionName") ? data.get("latestVersionName").getAsString() : "1.3.0";
                    String notes = data.has("releaseNotes") ? data.get("releaseNotes").getAsString() : "New features and performance improvements.";
                    String downloadUrl = data.has("downloadUrl") ? data.get("downloadUrl").getAsString() : ApiClient.WEBSITE_DOWNLOAD_URL;
                    boolean mandatory = data.has("mandatoryUpdate") && data.get("mandatoryUpdate").getAsBoolean();

                    if (latestCode > getInstalledVersionCode() && latestCode > sessionManager.getDismissedUpdateVersionCode()) {
                        showInAppUpdateDialog(latestName, notes, downloadUrl, mandatory, latestCode);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Ignore network errors on update check
            }
        });
    }

    private void showInAppUpdateDialog(String versionName, String releaseNotes, String downloadUrl, boolean mandatory, int latestCode) {
        if (isFinishing()) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("New Update Available (v" + versionName + ")")
                .setMessage("SK Payment Bank has a new version available. Update now to get the latest features and improvements.\n\nWhat's New:\n" + releaseNotes)
                .setCancelable(!mandatory)
                .setPositiveButton("Update Now", (dialog, which) -> {
                    sessionManager.setDismissedUpdateVersionCode(latestCode);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl));
                    startActivity(intent);
                });

        if (!mandatory) {
            builder.setNegativeButton("Later", (dialog, which) -> {
                sessionManager.setDismissedUpdateVersionCode(latestCode);
                dialog.dismiss();
            });
        }

        builder.show();
    }
}
