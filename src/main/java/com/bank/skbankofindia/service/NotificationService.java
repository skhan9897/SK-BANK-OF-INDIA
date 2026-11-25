package com.bank.skbankofindia.service;

import com.bank.skbankofindia.config.DatabaseConfig;
import com.bank.skbankofindia.dao.BankAccountDAO;
import com.bank.skbankofindia.dao.CustomerDAO;
import com.bank.skbankofindia.model.BankAccount;
import com.bank.skbankofindia.model.Customer;
import com.bank.skbankofindia.model.Transaction;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class NotificationService {

    private static final Logger LOGGER = Logger.getLogger(NotificationService.class.getName());
    private final BankAccountDAO bankAccountDAO = new BankAccountDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();

    /**
     * Creates and dispatches dual-sided transaction alerts (SMS, Push & In-App) for Sender and Receiver.
     */
    public void sendTransactionAlerts(Transaction txn) {
        if (txn == null) return;

        try {
            BankAccount srcAcc = txn.getSourceAccountId() != null ? bankAccountDAO.findByAccountIdForUpdate(DatabaseConfig.getConnection(), txn.getSourceAccountId()) : null;
            BankAccount dstAcc = txn.getDestinationAccountId() != null ? bankAccountDAO.findByAccountIdForUpdate(DatabaseConfig.getConnection(), txn.getDestinationAccountId()) : null;

            String timeStr = new SimpleDateFormat("dd-MMM-yyyy HH:mm").format(txn.getCreatedAt() != null ? txn.getCreatedAt() : new Date());
            String formattedAmount = Number(txn.getAmount());

            Customer senderCust = srcAcc != null ? customerDAO.findByCustomerId(srcAcc.getCustomerId()) : null;
            Customer receiverCust = dstAcc != null ? customerDAO.findByCustomerId(dstAcc.getCustomerId()) : null;

            String senderName = senderCust != null ? senderCust.getFirstName() + " " + senderCust.getLastName() : "SK Bank Customer";
            String receiverName = receiverCust != null ? receiverCust.getFirstName() + " " + receiverCust.getLastName() : "Beneficiary";

            // 1. Sender Notification (Payment Sent + Debited)
            if (senderCust != null) {
                String senderTitle = "Payment Sent Successfully";
                String senderMsg = String.format("₹%s sent to %s. Your account has been debited by ₹%s. TxnRef: %s.",
                        formattedAmount, receiverName, formattedAmount, txn.getTransactionRef());

                saveNotification(senderCust.getUserId(), senderTitle, senderMsg, "PAYMENT_SENT", "PUSH_AND_INAPP");
                LOGGER.info("[SENDER DEBIT NOTIFICATION]: " + senderMsg);
            }

            // 2. Receiver Event (Voice Event Only - No Visible Push Notification)
            if (receiverCust != null) {
                String receiverTitle = "Payment Received Voice Event";
                String receiverMsg = String.format("You have received ₹%s from %s. TxnRef: %s.",
                        formattedAmount, senderName, txn.getTransactionRef());

                saveNotification(receiverCust.getUserId(), receiverTitle, receiverMsg, "PAYMENT_RECEIVED", "SILENT_VOICE_ONLY");
                LOGGER.info("[RECEIVER SILENT VOICE EVENT]: " + receiverMsg);
            }

        } catch (Exception e) {
            LOGGER.severe("Failed to dispatch transaction notifications: " + e.getMessage());
        }
    }

    public void saveNotification(long userId, String title, String message, String type, String channel) throws SQLException {
        String sql = "INSERT INTO notifications (user_id, title, message, type, is_read, channel) VALUES (?, ?, ?, ?, FALSE, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            stmt.setString(2, title);
            stmt.setString(3, message);
            stmt.setString(4, type);
            stmt.setString(5, channel);
            stmt.executeUpdate();
        }
    }

    public List<Map<String, Object>> getUserNotifications(long userId, int limit) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC LIMIT ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            stmt.setInt(2, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("notificationId", rs.getLong("notification_id"));
                    map.put("title", rs.getString("title"));
                    map.put("message", rs.getString("message"));
                    map.put("type", rs.getString("type"));
                    map.put("isRead", rs.getBoolean("is_read"));
                    map.put("createdAt", rs.getTimestamp("created_at").toString());
                    list.add(map);
                }
            }
        }
        return list;
    }

    private String Number(BigDecimal val) {
        return val != null ? val.toString() : "0.00";
    }

    private String maskAccountNumber(String acc) {
        if (acc == null || acc.length() < 4) return "••••";
        return "••••" + acc.substring(acc.length() - 4);
    }
}
