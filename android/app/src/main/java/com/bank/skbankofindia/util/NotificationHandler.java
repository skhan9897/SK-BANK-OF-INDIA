package com.bank.skbankofindia.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.bank.skbankofindia.R;
import com.bank.skbankofindia.ui.PassbookActivity;

public class NotificationHandler {

    private static final String TAG = "NotificationHandler";
    private static final String CHANNEL_ID = "skbank_payment_channel";
    private static final String CHANNEL_NAME = "SK Payment Notifications";

    public static void processPaymentEvent(Context context, String eventType, String txnRef, String amount, String recipientOrSender) {
        if (context == null || eventType == null) return;

        TtsManager tts = TtsManager.getInstance(context);

        if ("PAYMENT_SENT".equalsIgnoreCase(eventType)) {
            // SENDER EVENT: 1. Display Debit Push Notification, 2. Voice Announcement
            displaySenderNotification(context, txnRef, amount, recipientOrSender != null ? recipientOrSender : "Beneficiary");
            tts.speakSenderAnnouncement(txnRef, amount, recipientOrSender);

        } else if ("PAYMENT_RECEIVED".equalsIgnoreCase(eventType)) {
            // RECEIVER EVENT: Voice Announcement ONLY (STRICTLY NO PUSH NOTIFICATION)
            Log.d(TAG, "PAYMENT_RECEIVED event for receiver: Suppressing system tray notification. Voice only.");
            tts.speakReceiverAnnouncement(txnRef, amount);
        }
    }

    private static void displaySenderNotification(Context context, String txnRef, String amount, String recipientName) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Notifications for sent payments and account debits");
            nm.createNotificationChannel(channel);
        }

        Intent intent = new Intent(context, PassbookActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, (int) System.currentTimeMillis(), intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );

        String title = "Payment Sent Successfully";
        String message = "₹" + amount + " sent to " + recipientName + ". Your account has been debited by ₹" + amount + ".";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_bank_logo)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        int notifId = (int) (System.currentTimeMillis() & 0xfffffff);
        nm.notify(notifId, builder.build());
    }
}
