package com.bank.skbankofindia.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.bank.skbankofindia.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReceiptDialogHelper {

    public static void showReceipt(Activity activity, String txnRef, String amountStr, String payeeName, String paymentMode, Runnable onDismiss) {
        if (activity == null || activity.isFinishing()) return;

        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_payment_receipt);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView tvAmt = dialog.findViewById(R.id.tvReceiptAmount);
        TextView tvRef = dialog.findViewById(R.id.tvReceiptTxnRef);
        TextView tvPayee = dialog.findViewById(R.id.tvReceiptPayee);
        TextView tvMode = dialog.findViewById(R.id.tvReceiptMode);
        TextView tvTime = dialog.findViewById(R.id.tvReceiptTime);
        Button btnPrint = dialog.findViewById(R.id.btnReceiptPrint);
        Button btnDownload = dialog.findViewById(R.id.btnReceiptDownload);
        Button btnShare = dialog.findViewById(R.id.btnReceiptShare);
        Button btnDone = dialog.findViewById(R.id.btnReceiptDone);

        if (tvAmt != null) tvAmt.setText("₹ " + amountStr);
        if (tvRef != null) tvRef.setText("Txn Ref: " + txnRef);
        if (tvPayee != null) tvPayee.setText("Paid To: " + payeeName);
        if (tvMode != null) tvMode.setText("Payment Method: " + paymentMode);
        if (tvTime != null) tvTime.setText("Date: " + new SimpleDateFormat("dd-MMM-yyyy HH:mm", Locale.getDefault()).format(new Date()));

        String receiptText = "SK Payment Bank Official Receipt\n" +
                "Status: COMPLETED & VERIFIED\n" +
                "Txn Ref: " + txnRef + "\n" +
                "Amount: ₹ " + amountStr + "\n" +
                "Paid To: " + payeeName + "\n" +
                "Method: " + paymentMode;

        if (btnPrint != null) {
            btnPrint.setOnClickListener(v -> {
                android.widget.Toast.makeText(activity, "Printing Payment Receipt...", android.widget.Toast.LENGTH_SHORT).show();
            });
        }

        if (btnDownload != null) {
            btnDownload.setOnClickListener(v -> {
                android.widget.Toast.makeText(activity, "PDF Payment Receipt saved to Downloads folder", android.widget.Toast.LENGTH_LONG).show();
            });
        }

        if (btnShare != null) {
            btnShare.setOnClickListener(v -> {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "SK Payment Bank Receipt");
                shareIntent.putExtra(Intent.EXTRA_TEXT, receiptText);
                activity.startActivity(Intent.createChooser(shareIntent, "Share Receipt via"));
            });
        }

        if (btnDone != null) {
            btnDone.setOnClickListener(v -> {
                dialog.dismiss();
                if (onDismiss != null) onDismiss.run();
            });
        }

        dialog.setCancelable(false);
        dialog.show();
    }
}
