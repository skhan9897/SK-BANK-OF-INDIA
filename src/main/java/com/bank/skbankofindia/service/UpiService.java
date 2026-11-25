package com.bank.skbankofindia.service;

import com.bank.skbankofindia.config.DatabaseConfig;
import com.bank.skbankofindia.dao.BankAccountDAO;
import com.bank.skbankofindia.dao.CustomerDAO;
import com.bank.skbankofindia.model.BankAccount;
import com.bank.skbankofindia.model.Customer;
import com.bank.skbankofindia.model.Transaction;
import com.bank.skbankofindia.util.IDGenerator;
import com.bank.skbankofindia.util.PasswordUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class UpiService {

    private static final Logger LOGGER = Logger.getLogger(UpiService.class.getName());
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final BankAccountDAO bankAccountDAO = new BankAccountDAO();
    private final LedgerService ledgerService = new LedgerService();

    public Map<String, Object> getOrCreateUpiProfile(long userId) throws Exception {
        Customer customer = customerDAO.findByUserId(userId);
        if (customer == null) throw new IllegalArgumentException("Customer profile not found.");

        BankAccount account = bankAccountDAO.findByCustomerId(customer.getCustomerId()).stream()
                .filter(a -> "ACTIVE".equalsIgnoreCase(a.getStatus()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No active account found for customer."));

        String selectSql = "SELECT * FROM upi_profiles WHERE customer_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(selectSql)) {
            stmt.setLong(1, customer.getCustomerId());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String vpa = rs.getString("vpa");
                    return buildQrResponse(customer, account, vpa);
                }
            }
        }

        // Create new VPA profile
        String vpa = IDGenerator.generateUPIVPA(customer.getCustomerNumber().substring(customer.getCustomerNumber().length() - 6));
        String defaultPinHash = PasswordUtil.hashPassword("1234");
        String insertSql = "INSERT INTO upi_profiles (customer_id, account_id, vpa, upi_pin_hash) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertSql)) {
            stmt.setLong(1, customer.getCustomerId());
            stmt.setLong(2, account.getAccountId());
            stmt.setString(3, vpa);
            stmt.setString(4, defaultPinHash);
            stmt.executeUpdate();
        }

        return buildQrResponse(customer, account, vpa);
    }

    private Map<String, Object> buildQrResponse(Customer customer, BankAccount account, String vpa) {
        String displayName = customer.getFirstName() + " " + customer.getLastName();
        String qrPayload = "upi://pay?pa=" + vpa + "&pn=" + displayName.replace(" ", "%20") + "&mc=0000&mode=02&orgid=000000";

        Map<String, Object> res = new HashMap<>();
        res.put("vpa", vpa);
        res.put("customerName", displayName);
        res.put("accountNumber", account.getAccountNumber());
        res.put("ifscCode", account.getBranchIfsc() != null ? account.getBranchIfsc() : "SKBI0000001");
        res.put("bankName", "SK Bank of India");
        res.put("qrPayload", qrPayload);
        return res;
    }

    public Map<String, Object> resolveVpa(String qrPayloadOrVpa) throws Exception {
        String cleanVpa = extractVpaFromPayload(qrPayloadOrVpa);

        String sql = "SELECT u.vpa, c.customer_id, c.first_name, c.last_name, a.account_id, a.account_number, b.ifsc_code " +
                     "FROM upi_profiles u " +
                     "JOIN customers c ON u.customer_id = c.customer_id " +
                     "JOIN bank_accounts a ON u.account_id = a.account_id " +
                     "JOIN branches b ON a.branch_id = b.branch_id " +
                     "WHERE u.vpa = ? AND a.status = 'ACTIVE'";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cleanVpa);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> recipient = new HashMap<>();
                    recipient.put("vpa", rs.getString("vpa"));
                    recipient.put("customerName", rs.getString("first_name") + " " + rs.getString("last_name"));
                    recipient.put("accountId", rs.getLong("account_id"));
                    recipient.put("accountNumber", rs.getString("account_number"));
                    recipient.put("ifscCode", rs.getString("ifsc_code"));
                    recipient.put("bankName", "SK Bank of India");
                    return recipient;
                }
            }
        }
        throw new IllegalArgumentException("Invalid or inactive SK Payment QR Code VPA: " + cleanVpa);
    }

    public Transaction processQrPayment(long senderUserId, String recipientVpa, BigDecimal amount, String remarks) throws Exception {
        Customer senderCust = customerDAO.findByUserId(senderUserId);
        if (senderCust == null) throw new IllegalArgumentException("Sender profile not found.");

        BankAccount senderAcc = bankAccountDAO.findByCustomerId(senderCust.getCustomerId()).stream()
                .filter(a -> "ACTIVE".equalsIgnoreCase(a.getStatus()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No active account for sender."));

        Map<String, Object> recipient = resolveVpa(recipientVpa);
        long destAccountId = Long.parseLong(recipient.get("accountId").toString());

        return ledgerService.processFundTransfer(senderAcc.getAccountId(), destAccountId, amount, "UPI_QR",
                remarks != null ? remarks : "Scan & Pay to " + recipient.get("customerName"), senderUserId);
    }

    private String extractVpaFromPayload(String payload) {
        if (payload == null) return "";
        if (payload.startsWith("upi://pay?")) {
            String[] params = payload.substring("upi://pay?".length()).split("&");
            for (String p : params) {
                if (p.startsWith("pa=")) {
                    return p.substring(3);
                }
            }
        }
        return payload.trim();
    }
}
