package com.bank.skbankofindia.dao;

import com.bank.skbankofindia.config.DatabaseConfig;
import com.bank.skbankofindia.model.LedgerEntry;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LedgerDAO {

    public long insertLedgerEntry(Connection conn, LedgerEntry entry) throws SQLException {
        String sql = "INSERT INTO ledger_entries (transaction_id, account_id, entry_type, amount, balance_after, description) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, entry.getTransactionId());
            stmt.setLong(2, entry.getAccountId());
            stmt.setString(3, entry.getEntryType()); // DEBIT, CREDIT
            stmt.setBigDecimal(4, entry.getAmount());
            stmt.setBigDecimal(5, entry.getBalanceAfter());
            stmt.setString(6, entry.getDescription());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
        }
        throw new SQLException("Failed to record immutable ledger entry.");
    }

    public List<LedgerEntry> findByAccountId(long accountId, int limit) throws SQLException {
        List<LedgerEntry> list = new ArrayList<>();
        String sql = "SELECT * FROM ledger_entries WHERE account_id = ? ORDER BY created_at DESC LIMIT ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, accountId);
            stmt.setInt(2, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapLedgerEntry(rs));
                }
            }
        }
        return list;
    }

    public List<LedgerEntry> findByTransactionId(long transactionId) throws SQLException {
        List<LedgerEntry> list = new ArrayList<>();
        String sql = "SELECT * FROM ledger_entries WHERE transaction_id = ? ORDER BY ledger_id ASC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, transactionId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapLedgerEntry(rs));
                }
            }
        }
        return list;
    }

    private LedgerEntry mapLedgerEntry(ResultSet rs) throws SQLException {
        LedgerEntry le = new LedgerEntry();
        le.setLedgerId(rs.getLong("ledger_id"));
        le.setTransactionId(rs.getLong("transaction_id"));
        le.setAccountId(rs.getLong("account_id"));
        le.setEntryType(rs.getString("entry_type"));
        le.setAmount(rs.getBigDecimal("amount"));
        le.setBalanceAfter(rs.getBigDecimal("balance_after"));
        le.setDescription(rs.getString("description"));
        le.setCreatedAt(rs.getTimestamp("created_at"));
        return le;
    }
}
