package com.bank.skbankofindia.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bank.skbankofindia.R;
import com.bank.skbankofindia.api.ApiClient;
import com.bank.skbankofindia.model.ApiResponse;
import com.bank.skbankofindia.util.SessionManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private TextView tvChatPartnerName;
    private EditText etSearchQuery, etChatMessage;
    private Button btnSearchCustomer, btnChatSendMoney, btnSendMsg;
    private LinearLayout llChatMessagesList;

    private SessionManager sessionManager;
    private long activePartnerUserId = 2L; // Default
    private String activePartnerName = "Bank Employee / Payee";
    private TextToSpeech ttsEngine;
    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        sessionManager = new SessionManager(this);

        tvChatPartnerName = findViewById(R.id.tvChatPartnerName);
        etSearchQuery = findViewById(R.id.etSearchQuery);
        etChatMessage = findViewById(R.id.etChatMessage);
        btnSearchCustomer = findViewById(R.id.btnSearchCustomer);
        btnChatSendMoney = findViewById(R.id.btnChatSendMoney);
        btnSendMsg = findViewById(R.id.btnSendMsg);
        llChatMessagesList = findViewById(R.id.llChatMessagesList);

        btnSearchCustomer.setOnClickListener(v -> performCustomerSearch());
        btnSendMsg.setOnClickListener(v -> sendTextMessage());
        btnChatSendMoney.setOnClickListener(v -> openChatSendMoneyDialog());

        initTtsEngine();
        fetchChatHistory();
    }

    private void initTtsEngine() {
        ttsEngine = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                ttsEngine.setLanguage(new Locale("en", "IN"));
            }
        });
    }

    private void speakVoiceNotification(String text) {
        if (ttsEngine != null) {
            ttsEngine.speak(text, TextToSpeech.QUEUE_FLUSH, null, "CHAT_SPEECH");
        }
    }

    private void showLoadingOverlay(String title) {
        hideLoadingOverlay();
        loadingDialog = new Dialog(this);
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loadingDialog.setContentView(R.layout.dialog_loading_overlay);
        if (loadingDialog.getWindow() != null) {
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        TextView tvTitle = loadingDialog.findViewById(R.id.tvLoadingTitle);
        if (tvTitle != null) tvTitle.setText(title);
        loadingDialog.setCancelable(false);
        loadingDialog.show();
    }

    private void hideLoadingOverlay() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

    private void performCustomerSearch() {
        String q = etSearchQuery.getText().toString().trim();
        if (q.isEmpty()) return;

        String token = sessionManager.getAuthToken();
        showLoadingOverlay("Searching Customers...");

        ApiClient.getApiService(this).searchChatCustomers(token, q).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                hideLoadingOverlay();
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    JsonArray arr = response.body().getData().getAsJsonArray();
                    if (arr.size() > 0) {
                        JsonObject obj = arr.get(0).getAsJsonObject();
                        activePartnerUserId = obj.get("userId").getAsLong();
                        activePartnerName = obj.get("name").getAsString();
                        tvChatPartnerName.setText(activePartnerName);

                        Toast.makeText(ChatActivity.this, "Chat opened with " + activePartnerName, Toast.LENGTH_SHORT).show();
                        fetchChatHistory();
                    } else {
                        Toast.makeText(ChatActivity.this, "No customer found matching query", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                hideLoadingOverlay();
            }
        });
    }

    private void fetchChatHistory() {
        String token = sessionManager.getAuthToken();

        ApiClient.getApiService(this).getChatMessages(token, activePartnerUserId).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    JsonArray arr = response.body().getData().getAsJsonArray();
                    renderMessages(arr);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Ignore network error on history
            }
        });
    }

    private void renderMessages(JsonArray messages) {
        if (llChatMessagesList == null) return;

        for (JsonElement el : messages) {
            JsonObject obj = el.getAsJsonObject();
            String type = obj.get("messageType").getAsString();
            String content = obj.get("content").getAsString();

            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(20, 20, 20, 20);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 16);
            card.setLayoutParams(params);

            TextView tv = new TextView(this);
            tv.setTextSize(13);

            if ("PAYMENT_SUCCESS".equals(type)) {
                card.setBackgroundColor(Color.parseColor("#E8F5E9"));
                tv.setTextColor(Color.parseColor("#138808"));
                tv.setText("💳 " + content);
            } else {
                card.setBackgroundColor(Color.WHITE);
                tv.setTextColor(Color.parseColor("#0A192F"));
                tv.setText("💬 " + content);
            }

            card.addView(tv);
            llChatMessagesList.addView(card);
        }
    }

    private void sendTextMessage() {
        String text = etChatMessage.getText().toString().trim();
        if (text.isEmpty()) return;

        String token = sessionManager.getAuthToken();
        Map<String, Object> body = new HashMap<>();
        body.put("receiverUserId", activePartnerUserId);
        body.put("content", text);
        body.put("messageType", "TEXT");

        etChatMessage.setText("");

        ApiClient.getApiService(this).sendChatMessage(token, body).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                fetchChatHistory();
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Ignore
            }
        });
    }

    private void openChatSendMoneyDialog() {
        final EditText etAmount = new EditText(this);
        etAmount.setHint("Enter Amount (₹)");
        etAmount.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);

        new AlertDialog.Builder(this)
                .setTitle("Send Money in Chat to " + activePartnerName)
                .setView(etAmount)
                .setPositiveButton("Send Payment", (dialog, which) -> {
                    String amt = etAmount.getText().toString().trim();
                    if (!amt.isEmpty()) {
                        executeChatMoneyTransfer(amt);
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void executeChatMoneyTransfer(String amountStr) {
        String token = sessionManager.getAuthToken();
        showLoadingOverlay("Executing Chat Double-Entry Transfer...");

        Map<String, Object> body = new HashMap<>();
        body.put("receiverUserId", activePartnerUserId);
        body.put("amount", amountStr);
        body.put("remarks", "Chat Payment to " + activePartnerName);

        ApiClient.getApiService(this).executeChatPayment(token, body).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                hideLoadingOverlay();
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    speakVoiceNotification("SK Bank of India: Payment of " + amountStr + " Rupees successfully sent in chat!");
                    Toast.makeText(ChatActivity.this, "Chat Payment Successful!", Toast.LENGTH_LONG).show();
                    fetchChatHistory();
                } else {
                    String msg = response.body() != null ? response.body().getMessage() : "Chat payment failed";
                    Toast.makeText(ChatActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                hideLoadingOverlay();
                Toast.makeText(ChatActivity.this, "Connection error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (ttsEngine != null) {
            ttsEngine.stop();
            ttsEngine.shutdown();
        }
        super.onDestroy();
    }
}
