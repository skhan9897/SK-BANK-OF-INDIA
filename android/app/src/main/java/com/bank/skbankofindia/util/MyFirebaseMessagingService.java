package com.bank.skbankofindia.util;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCMService";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "Refreshed FCM Token: " + token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Map<String, String> data = remoteMessage.getData();
        if (data != null && !data.isEmpty()) {
            String eventType = data.get("eventType");
            String txnRef = data.get("transactionId");
            if (txnRef == null) txnRef = data.get("txnRef");
            String amount = data.get("amount");
            String name = data.containsKey("recipientName") ? data.get("recipientName") : data.get("senderName");

            Log.d(TAG, "FCM Remote Message Event Received: " + eventType + ", TxnRef: " + txnRef);

            // Delegate to NotificationHandler
            NotificationHandler.processPaymentEvent(this, eventType, txnRef, amount, name);
        }
    }
}
