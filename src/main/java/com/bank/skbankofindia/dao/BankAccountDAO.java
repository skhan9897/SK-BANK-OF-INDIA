package com.bank.skbankofindia.dao;

import com.bank.skbankofindia.config.DatabaseConfig;
import com.bank.skbankofindia.model.BankAccount;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BankAccountDAO {

    public BankAccount findByAccountNumber(String accountNumber) throws SQLException {
        String sql = "SELECT a.*, at.type_code, b.ifsc_code FROM bank_accounts a " +
                     "JOIN account_types at ON a.account_type_id = at.account_type_id " +
                     "JOIN branches b ON a.branch_id = b.branch_id WHERE a.account_number = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, accountNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapAccount(rs);
                }
            }
        }
        return null;
    }

    public BankAccount findByAccountIdForUpdate(Connection conn, long accountId) throws SQLException {
        String sql = "SELECT a.*, at.type_code, b.ifsc_code FROM bank_accounts a " +
                     "JOIN account_types at ON a.account_type_id = at.account_type_id " +
                     "JOIN branches b ON a.branch_id = b.branch_id WHERE a.account_id = ? FOR UPDATE";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, accountId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapAccount(rs);
                }
            }
        }
        return null;
    }

    public List<BankAccount> findByCustomerId(long customerId) throws SQLException {
        List<BankAccount> list = new ArrayList<>();
        String sql = "SELECT a.*, at.type_code, b.ifsc_code FROM bank_accounts a " +
                     "JOIN account_types at ON a.account_type_id = at.account_type_id " +
                     "JOIN branches b ON a.branch_id = b.branch_id WHERE a.customer_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapAccount(rs));
                }
            }
        }
        return list;
    }

    public long createAccount(Connection conn, BankAccount account) throws SQLException {
        String sql = "INSERT INTO bank_accounts (customer_id, account_number, account_type_id, branch_id, " +
                     "currency, balance, available_balance, ledger_balance, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, account.getCustomerId());
            stmt.setString(2, account.getAccountNumber());
            stmt.setInt(3, account.getAccountTypeId());
            stmt.setInt(4, account.getBranchId() != null ? account.getBranchId() : 1);
            stmt.setString(5, account.getCurrency() != null ? account.getCurrency() : "INR");
            stmt.setBigDecimal(6, account.getBalance() != null ? account.getBalance() : BigDecimal.ZERO);
            stmt.setBigDecimal(7, account.getAvailableBalance() != null ? account.getAvailableBalance() : BigDecimal.ZERO);
            stmt.setBigDecimal(8, account.getLedgerBalance() != null ? account.getLedgerBalance() : BigDecimal.ZERO);
            stmt.setString(9, account.getStatus() != null ? account.getStatus() : "ACTIVE");
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
        }
        throw new SQLException("Failed to create bank account.");
    }

    public void updateAccountBalances(Connection conn, long accountId, BigDecimal newBalance) throws SQLException {
        String sql = "UPDATE bank_accounts SET balance = ?, available_balance = ?, ledger_balance = ? WHERE account_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBigDecimal(1, newBalance);
            stmt.setBigDecimal(2, newBalance);
            stmt.setBigDecimal(3, newBalance);
            stmt.setLong(4, accountId);
            stmt.executeUpdate();
        }
    }

    private BankAccount mapAccount(ResultSet rs) throws SQLException {
        BankAccount a = new BankAccount();
        a.setAccountId(rs.getLong("account_id"));
        a.setCustomerId(rs.getLong("customer_id"));
        a.setAccountNumber(rs.getString("account_number"));
        a.setAccountTypeId(rs.getInt("account_type_id"));
        a.setAccountTypeCode(rs.getString("type_code"));
        a.setBranchId(rs.getInt("branch_id"));
        a.setBranchIfsc(rs.getString("ifsc_code"));
        a.setCurrency(rs.getString("currency"));
        a.setBalance(rs.getBigDecimal("balance"));
        a.setAvailableBalance(rs.getBigDecimal("available_balance"));
        a.setLedgerBalance(rs.getBigDecimal("ledger_balance"));
        a.setStatus(rs.getString("status"));
        a.setIsFrozen(rs.getBoolean("is_frozen"));
        a.setOpenedAt(rs.getTimestamp("opened_at"));
        a.setClosedAt(rs.getTimestamp("closed_at"));
        a.setCreatedAt(rs.getTimestamp("created_at"));
        a.setUpdatedAt(rs.getTimestamp("updated_at"));
        return a;
    }
}
