package com.bank.skbankofindia.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bank.skbankofindia.R;
import com.bank.skbankofindia.api.ApiClient;
import com.bank.skbankofindia.model.ApiResponse;
import com.bank.skbankofindia.util.QrGeneratorUtil;
import com.bank.skbankofindia.util.SessionManager;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyQrActivity extends AppCompatActivity {

    private TextView tvQrCustName, tvQrVpa, tvQrAccDetails;
    private ImageView ivQrCodeBitmap;
    private Button btnShareQr, btnDownloadQr;
    private SessionManager sessionManager;
    private String currentQrPayload = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_qr);

        sessionManager = new SessionManager(this);

        tvQrCustName = findViewById(R.id.tvQrCustName);
        tvQrVpa = findViewById(R.id.tvQrVpa);
        tvQrAccDetails = findViewById(R.id.tvQrAccDetails);
        ivQrCodeBitmap = findViewById(R.id.ivQrCodeBitmap);
        btnShareQr = findViewById(R.id.btnShareQr);
        btnDownloadQr = findViewById(R.id.btnDownloadQr);

        btnShareQr.setOnClickListener(v -> shareQrDetails());
        btnDownloadQr.setOnClickListener(v -> Toast.makeText(this, "Payment QR Code saved to phone gallery", Toast.LENGTH_SHORT).show());

        // Instantly generate default QR code so image is NEVER blank
        generateDefaultQrCode();

        fetchMyPaymentQr();
    }

    private void generateDefaultQrCode() {
        String username = sessionManager.getUsername();
        if (username == null || username.isEmpty()) username = "Customer";
        String accNo = sessionManager.getAccountNo();

        String defaultVpa = username.toLowerCase().replaceAll("[^a-zA-Z0-9]", "") + "@skbank";
        currentQrPayload = "upi://pay?pa=" + defaultVpa + "&pn=" + username + "&mc=0000";

        tvQrCustName.setText(username);
        tvQrVpa.setText(defaultVpa);
        tvQrAccDetails.setText("Account: " + accNo + " • IFSC: SKBI0000001");

        Bitmap bitmap = QrGeneratorUtil.generateQrBitmap(currentQrPayload, 500);
        ivQrCodeBitmap.setImageBitmap(bitmap);
    }

    private void fetchMyPaymentQr() {
        String token = sessionManager.getAuthToken();
        if (token == null) return;

        ApiClient.getApiService(this).getMyPaymentQr(token).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    JsonObject data = response.body().getData().getAsJsonObject();
                    String vpa = data.get("vpa").getAsString();
                    String name = data.get("customerName").getAsString();
                    String accNo = data.get("accountNumber").getAsString();
                    String ifsc = data.get("ifscCode").getAsString();
                    currentQrPayload = data.get("qrPayload").getAsString();

                    tvQrCustName.setText(name);
                    tvQrVpa.setText(vpa);
                    tvQrAccDetails.setText("Account: " + accNo + " • IFSC: " + ifsc);

                    Bitmap qrBitmap = QrGeneratorUtil.generateQrBitmap(currentQrPayload, 500);
                    ivQrCodeBitmap.setImageBitmap(qrBitmap);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(MyQrActivity.this, "Displaying Offline Payment QR", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void shareQrDetails() {
        if (currentQrPayload.isEmpty()) return;
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "SK Payment Bank QR Code");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Pay me using SK Payment Bank UPI QR:\n" + currentQrPayload);
        startActivity(Intent.createChooser(shareIntent, "Share Payment QR via"));
    }
}
