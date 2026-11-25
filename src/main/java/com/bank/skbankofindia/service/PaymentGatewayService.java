package com.bank.skbankofindia.service;

import com.bank.skbankofindia.config.AppConstants;
import com.bank.skbankofindia.config.DatabaseConfig;
import com.bank.skbankofindia.dao.BankAccountDAO;
import com.bank.skbankofindia.dao.CustomerDAO;
import com.bank.skbankofindia.model.BankAccount;
import com.bank.skbankofindia.model.Customer;
import com.bank.skbankofindia.util.IDGenerator;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class PaymentGatewayService {

    private static final Logger LOGGER = Logger.getLogger(PaymentGatewayService.class.getName());
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final BankAccountDAO bankAccountDAO = new BankAccountDAO();

    public Map<String, Object> createPaymentOrder(long userId, String accountNumber, BigDecimal amount, String idempotencyKey) throws Exception {
        Customer customer = customerDAO.findByUserId(userId);
        if (customer == null) throw new IllegalArgumentException("Customer profile not found.");

        BankAccount account = bankAccountDAO.findByAccountNumber(accountNumber);
        if (account == null) throw new IllegalArgumentException("Account not found.");

        // Idempotency check
        String checkSql = "SELECT * FROM payment_orders WHERE idempotency_key = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, idempotencyKey);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> existing = new HashMap<>();
                    existing.put("orderRef", rs.getString("order_ref"));
                    existing.put("gatewayOrderId", rs.getString("gateway_order_id"));
                    existing.put("amount", rs.getBigDecimal("amount"));
                    existing.put("status", rs.getString("status"));
                    return existing;
                }
            }
        }

        String orderRef = IDGenerator.generatePaymentOrderRef();
        String gatewayOrderId = "order_sandbox_" + System.currentTimeMillis();

        String insertSql = "INSERT INTO payment_orders (order_ref, customer_id, account_id, amount, gateway_name, gateway_order_id, status, idempotency_key) " +
                           "VALUES (?, ?, ?, ?, 'SANDBOX_PG', ?, 'CREATED', ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertSql)) {
            stmt.setString(1, orderRef);
            stmt.setLong(2, customer.getCustomerId());
            stmt.setLong(3, account.getAccountId());
            stmt.setBigDecimal(4, amount);
            stmt.setString(5, gatewayOrderId);
            stmt.setString(6, idempotencyKey);
            stmt.executeUpdate();
        }

        Map<String, Object> orderData = new HashMap<>();
        orderData.put("orderRef", orderRef);
        orderData.put("gatewayOrderId", gatewayOrderId);
        orderData.put("keyId", AppConstants.PG_SANDBOX_KEY_ID);
        orderData.put("amount", amount);
        orderData.put("currency", "INR");
        orderData.put("status", "CREATED");

        LOGGER.info("Created payment order: " + orderRef + ", Gateway ID: " + gatewayOrderId);
        return orderData;
    }

    public boolean verifyWebhookSignature(String payload, String signature) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(AppConstants.PG_SANDBOX_SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmac.init(secretKey);
            byte[] hash = hmac.doFinal(payload.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString().equalsIgnoreCase(signature);
        } catch (Exception e) {
            return false;
        }
    }

    public void processWebhookEvent(String providerName, String eventType, String payloadJson, String signature) throws Exception {
        boolean verified = verifyWebhookSignature(payloadJson, signature);

        String sql = "INSERT INTO payment_provider_events (provider_name, event_type, payload_json, signature_verified, processed_status) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, providerName);
            stmt.setString(2, eventType);
            stmt.setString(3, payloadJson);
            stmt.setBoolean(4, verified);
            stmt.setString(5, verified ? "PROCESSED" : "FAILED_SIGNATURE");
            stmt.executeUpdate();
        }

        if (!verified) {
            throw new SecurityException("Webhook signature verification failed.");
        }
    }
}
