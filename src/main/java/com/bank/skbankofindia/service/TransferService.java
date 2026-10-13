package com.bank.skbankofindia.service;

import com.bank.skbankofindia.utils.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.UUID;

public class TransferService {

    public boolean executeTransfer(int sourceAccountId, String destinationAccountNumber, String destinationIfsc, BigDecimal amount, String idempotencyKey) throws SQLException {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be greater than zero.");
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Check Idempotency key
            String checkIdemp = "SELECT id FROM transfer_requests WHERE idempotency_key = ?";
            try (PreparedStatement stmt = conn.prepareStatement(checkIdemp)) {
                stmt.setString(1, idempotencyKey);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        conn.rollback();
                        return false;
                    }
                }
            }

            // 2. Lock source account and check balance
            String lockSource = "SELECT id, balance, account_number FROM bank_accounts WHERE id = ? FOR UPDATE";
            BigDecimal sourceBalance = BigDecimal.ZERO;
            String sourceAccNum = "";
            try (PreparedStatement stmt = conn.prepareStatement(lockSource)) {
                stmt.setInt(1, sourceAccountId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        sourceBalance = rs.getBigDecimal("balance");
                        sourceAccNum = rs.getString("account_number");
                    } else {
                        throw new SQLException("Source account not found.");
                    }
                }
            }

            if (sourceBalance.compareTo(amount) < 0) {
                throw new SQLException("Insufficient balance in source account.");
            }

            // 3. Find destination account (internal or external simulation)
            String lockDest = "SELECT id, balance, account_number FROM bank_accounts WHERE account_number = ? FOR UPDATE";
            Integer destAccountId = null;
            BigDecimal destBalance = BigDecimal.ZERO;
            try (PreparedStatement stmt = conn.prepareStatement(lockDest)) {
                stmt.setString(1, destinationAccountNumber);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        destAccountId = rs.getInt("id");
                        destBalance = rs.getBigDecimal("balance");
                    }
                }
            }

            BigDecimal newSourceBalance = sourceBalance.subtract(amount);
            String refId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            String journalId = "JRN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            // 4. Update source account balance
            String updateSource = "UPDATE bank_accounts SET balance = ? WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateSource)) {
                stmt.setBigDecimal(1, newSourceBalance);
                stmt.setInt(2, sourceAccountId);
                stmt.executeUpdate();
            }

            // 5. If destination account is internal, credit it
            if (destAccountId != null) {
                BigDecimal newDestBalance = destBalance.add(amount);
                String updateDest = "UPDATE bank_accounts SET balance = ? WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(updateDest)) {
                    stmt.setBigDecimal(1, newDestBalance);
                    stmt.setInt(2, destAccountId);
                    stmt.executeUpdate();
                }

                String insertDestTxn = "INSERT INTO bank_transactions (transaction_reference, account_id, type, amount, balance_after, description, related_account) VALUES (?, ?, 'TRANSFER_IN', ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(insertDestTxn)) {
                    stmt.setString(1, refId + "-IN");
                    stmt.setInt(2, destAccountId);
                    stmt.setBigDecimal(3, amount);
                    stmt.setBigDecimal(4, newDestBalance);
                    stmt.setString(5, "Transfer from " + sourceAccNum);
                    stmt.setString(6, sourceAccNum);
                    stmt.executeUpdate();
                }
            }

            // 6. Debit transaction for source
            String insertSourceTxn = "INSERT INTO bank_transactions (transaction_reference, account_id, type, amount, balance_after, description, related_account) VALUES (?, ?, 'TRANSFER_OUT', ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertSourceTxn)) {
                stmt.setString(1, refId + "-OUT");
                stmt.setInt(2, sourceAccountId);
                stmt.setBigDecimal(3, amount);
                stmt.setBigDecimal(4, newSourceBalance);
                stmt.setString(5, "Transfer to " + destinationAccountNumber + " (" + destinationIfsc + ")");
                stmt.setString(6, destinationAccountNumber);
                stmt.executeUpdate();
            }

            // 7. Double-entry ledger entries
            String insertLedger = "INSERT INTO ledger_entries (journal_id, account_number, debit_amount, credit_amount, currency, description) VALUES (?, ?, ?, ?, 'INR', ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertLedger)) {
                stmt.setString(1, journalId);
                stmt.setString(2, sourceAccNum);
                stmt.setBigDecimal(3, amount);
                stmt.setBigDecimal(4, BigDecimal.ZERO);
                stmt.setString(5, "Transfer out to " + destinationAccountNumber);
                stmt.executeUpdate();

                stmt.setString(1, journalId);
                stmt.setString(2, destinationAccountNumber);
                stmt.setBigDecimal(3, BigDecimal.ZERO);
                stmt.setBigDecimal(4, amount);
                stmt.setString(5, "Transfer in from " + sourceAccNum);
                stmt.executeUpdate();
            }

            // 8. Record transfer request
            String insertReq = "INSERT INTO transfer_requests (reference_id, source_account_id, destination_account_number, destination_ifsc, amount, transfer_type, status, idempotency_key) VALUES (?, ?, ?, ?, ?, 'NEFT', 'SUCCESS', ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertReq)) {
                stmt.setString(1, refId);
                stmt.setInt(2, sourceAccountId);
                stmt.setString(3, destinationAccountNumber);
                stmt.setString(4, destinationIfsc);
                stmt.setBigDecimal(5, amount);
                stmt.setString(6, idempotencyKey);
                stmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            throw new SQLException("Transfer failed: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }
}
