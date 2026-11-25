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
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatService {

    private final CustomerDAO customerDAO = new CustomerDAO();
    private final BankAccountDAO bankAccountDAO = new BankAccountDAO();
    private final LedgerService ledgerService = new LedgerService();

    public List<Map<String, Object>> searchCustomers(String query) throws Exception {
        List<Map<String, Object>> results = new ArrayList<>();
        String sql = "SELECT c.customer_id, c.user_id, c.first_name, c.last_name, u.username, u.phone_number, up.vpa " +
                     "FROM customers c " +
                     "JOIN users u ON c.user_id = u.user_id " +
                     "LEFT JOIN upi_profiles up ON c.customer_id = up.customer_id " +
                     "WHERE u.username LIKE ? OR u.phone_number LIKE ? OR c.first_name LIKE ? OR up.vpa LIKE ? LIMIT 20";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String q = "%" + query + "%";
            stmt.setString(1, q);
            stmt.setString(2, q);
            stmt.setString(3, q);
            stmt.setString(4, q);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("customerId", rs.getLong("customer_id"));
                    item.put("userId", rs.getLong("user_id"));
                    item.put("name", rs.getString("first_name") + " " + rs.getString("last_name"));
                    item.put("username", rs.getString("username"));
                    item.put("phone", rs.getString("phone_number"));
                    item.put("vpa", rs.getString("vpa") != null ? rs.getString("vpa") : rs.getString("username") + "@skbank");
                    results.add(item);
                }
            }
        }
        return results;
    }

    public List<Map<String, Object>> getMessages(long userId, long partnerUserId) throws Exception {
        List<Map<String, Object>> messages = new ArrayList<>();
        String sql = "SELECT * FROM chat_messages " +
                     "WHERE (sender_user_id = ? AND receiver_user_id = ?) OR (sender_user_id = ? AND receiver_user_id = ?) " +
                     "ORDER BY created_at ASC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            stmt.setLong(2, partnerUserId);
            stmt.setLong(3, partnerUserId);
            stmt.setLong(4, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> msg = new HashMap<>();
                    msg.put("messageId", rs.getLong("message_id"));
                    msg.put("senderUserId", rs.getLong("sender_user_id"));
                    msg.put("receiverUserId", rs.getLong("receiver_user_id"));
                    msg.put("messageType", rs.getString("message_type"));
                    msg.put("content", rs.getString("content"));
                    msg.put("amount", rs.getBigDecimal("amount"));
                    msg.put("transactionRef", rs.getString("transaction_ref"));
                    msg.put("isRead", rs.getBoolean("is_read"));
                    msg.put("createdAt", rs.getTimestamp("created_at").toString());
                    messages.add(msg);
                }
            }
        }
        return messages;
    }

    public void sendMessage(long senderUserId, long receiverUserId, String messageType, String content, BigDecimal amount, String txnRef) throws Exception {
        String sql = "INSERT INTO chat_messages (sender_user_id, receiver_user_id, message_type, content, amount, transaction_ref) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, senderUserId);
            stmt.setLong(2, receiverUserId);
            stmt.setString(3, messageType != null ? messageType : "TEXT");
            stmt.setString(4, content);
            if (amount != null) stmt.setBigDecimal(5, amount); else stmt.setNull(5, Types.DECIMAL);
            stmt.setString(6, txnRef);
            stmt.executeUpdate();
        }
    }

    public Map<String, Object> executeChatPayment(long senderUserId, long receiverUserId, BigDecimal amount, String remarks) throws Exception {
        Customer senderCust = customerDAO.findByUserId(senderUserId);
        Customer receiverCust = customerDAO.findByUserId(receiverUserId);

        if (senderCust == null || receiverCust == null) {
            throw new IllegalArgumentException("Invalid sender or receiver identity.");
        }

        BankAccount senderAcc = bankAccountDAO.findByCustomerId(senderCust.getCustomerId()).stream()
                .filter(a -> "ACTIVE".equalsIgnoreCase(a.getStatus()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Sender has no active account."));

        BankAccount receiverAcc = bankAccountDAO.findByCustomerId(receiverCust.getCustomerId()).stream()
                .filter(a -> "ACTIVE".equalsIgnoreCase(a.getStatus()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Receiver has no active account."));

        Transaction txn = ledgerService.processFundTransfer(senderAcc.getAccountId(), receiverAcc.getAccountId(),
                amount, "CHAT_PAY", remarks != null ? remarks : "Chat Payment", senderUserId);

        // Record Chat Payment Message
        String content = "Sent ₹ " + amount.toString() + " via SK Payment Chat";
        sendMessage(senderUserId, receiverUserId, "PAYMENT_SUCCESS", content, amount, txn.getTransactionRef());

        Map<String, Object> res = new HashMap<>();
        res.put("transaction", txn);
        res.put("senderName", senderCust.getFirstName() + " " + senderCust.getLastName());
        res.put("receiverName", receiverCust.getFirstName() + " " + receiverCust.getLastName());
        return res;
    }
}
