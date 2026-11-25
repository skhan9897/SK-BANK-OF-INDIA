package com.bank.skbankofindia.dao;

import com.bank.skbankofindia.config.DatabaseConfig;
import com.bank.skbankofindia.model.Transaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    public long createTransaction(Connection conn, Transaction txn) throws SQLException {
        String sql = "INSERT INTO transactions (transaction_ref, source_account_id, destination_account_id, " +
                     "amount, currency, transaction_type, payment_method, status, remarks, initiated_by_user_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, txn.getTransactionRef());
            if (txn.getSourceAccountId() != null) stmt.setLong(2, txn.getSourceAccountId()); else stmt.setNull(2, Types.BIGINT);
            if (txn.getDestinationAccountId() != null) stmt.setLong(3, txn.getDestinationAccountId()); else stmt.setNull(3, Types.BIGINT);
            stmt.setBigDecimal(4, txn.getAmount());
            stmt.setString(5, txn.getCurrency() != null ? txn.getCurrency() : "INR");
            stmt.setString(6, txn.getTransactionType());
            stmt.setString(7, txn.getPaymentMethod());
            stmt.setString(8, txn.getStatus() != null ? txn.getStatus() : "PENDING");
            stmt.setString(9, txn.getRemarks());
            stmt.setLong(10, txn.getInitiatedByUserId());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
        }
        throw new SQLException("Failed to record transaction.");
    }

    public void updateStatus(Connection conn, long transactionId, String status, String failureReason) throws SQLException {
        String sql = "UPDATE transactions SET status = ?, failure_reason = ?, completed_at = ? WHERE transaction_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setString(2, failureReason);
            stmt.setTimestamp(3, "COMPLETED".equals(status) ? new Timestamp(System.currentTimeMillis()) : null);
            stmt.setLong(4, transactionId);
            stmt.executeUpdate();
        }
    }

    public Transaction findByRef(String txnRef) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE transaction_ref = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, txnRef);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapTransaction(rs);
                }
            }
        }
        return null;
    }

    public List<Transaction> findByAccountId(long accountId, int limit) throws SQLException {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE source_account_id = ? OR destination_account_id = ? ORDER BY created_at DESC LIMIT ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, accountId);
            stmt.setLong(2, accountId);
            stmt.setInt(3, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapTransaction(rs));
                }
            }
        }
        return list;
    }

    private Transaction mapTransaction(ResultSet rs) throws SQLException {
        Transaction t = new Transaction();
        t.setTransactionId(rs.getLong("transaction_id"));
        t.setTransactionRef(rs.getString("transaction_ref"));
        long src = rs.getLong("source_account_id");
        if (!rs.wasNull()) t.setSourceAccountId(src);
        long dst = rs.getLong("destination_account_id");
        if (!rs.wasNull()) t.setDestinationAccountId(dst);
        t.setAmount(rs.getBigDecimal("amount"));
        t.setCurrency(rs.getString("currency"));
        t.setTransactionType(rs.getString("transaction_type"));
        t.setPaymentMethod(rs.getString("payment_method"));
        t.setStatus(rs.getString("status"));
        t.setRemarks(rs.getString("remarks"));
        t.setFailureReason(rs.getString("failure_reason"));
        t.setInitiatedByUserId(rs.getLong("initiated_by_user_id"));
        t.setCreatedAt(rs.getTimestamp("created_at"));
        t.setCompletedAt(rs.getTimestamp("completed_at"));
        return t;
    }
}
