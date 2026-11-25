package com.bank.skbankofindia.service;

import com.bank.skbankofindia.config.DatabaseConfig;
import com.bank.skbankofindia.dao.BankAccountDAO;
import com.bank.skbankofindia.dao.LedgerDAO;
import com.bank.skbankofindia.dao.TransactionDAO;
import com.bank.skbankofindia.model.BankAccount;
import com.bank.skbankofindia.model.LedgerEntry;
import com.bank.skbankofindia.model.Transaction;
import com.bank.skbankofindia.util.IDGenerator;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdminService {

    private static final Logger LOGGER = Logger.getLogger(AdminService.class.getName());
    private final BankAccountDAO bankAccountDAO = new BankAccountDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final LedgerDAO ledgerDAO = new LedgerDAO();
    private final LedgerService ledgerService = new LedgerService();

    public Map<String, Object> getDashboardAnalytics() throws SQLException {
        Map<String, Object> metrics = new HashMap<>();

        try (Connection conn = DatabaseConfig.getConnection()) {
            // Total Customers
            try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM customers");
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) metrics.put("totalCustomers", rs.getLong(1));
            }

            // Total Accounts
            try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM bank_accounts");
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) metrics.put("totalAccounts", rs.getLong(1));
            }

            // Total Deposits
            try (PreparedStatement stmt = conn.prepareStatement("SELECT COALESCE(SUM(balance), 0) FROM bank_accounts");
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) metrics.put("totalDeposits", rs.getBigDecimal(1));
            }

            // Pending KYCs
            try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM account_kyc WHERE verification_status = 'PENDING'");
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) metrics.put("pendingKycs", rs.getLong(1));
            }

            // Total Active Loans
            try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM loans WHERE status = 'APPROVED'");
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) metrics.put("activeLoans", rs.getLong(1));
            }
        }
        return metrics;
    }

    public List<Map<String, Object>> getAllAccounts() throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT a.account_id, a.account_number, a.balance, a.available_balance, a.status AS account_status, " +
                     "a.is_frozen, c.first_name, c.last_name, u.user_id, u.username, u.is_locked, u.status AS user_status " +
                     "FROM bank_accounts a " +
                     "JOIN customers c ON a.customer_id = c.customer_id " +
                     "JOIN users u ON c.user_id = u.user_id ORDER BY a.account_id DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("accountId", rs.getLong("account_id"));
                map.put("accountNumber", rs.getString("account_number"));
                map.put("balance", rs.getBigDecimal("balance"));
                map.put("availableBalance", rs.getBigDecimal("available_balance"));
                map.put("accountStatus", rs.getString("account_status"));
                map.put("isFrozen", rs.getBoolean("is_frozen"));
                map.put("customerName", rs.getString("first_name") + " " + rs.getString("last_name"));
                map.put("userId", rs.getLong("user_id"));
                map.put("username", rs.getString("username"));
                map.put("isLocked", rs.getBoolean("is_locked"));
                map.put("userStatus", rs.getString("user_status"));
                list.add(map);
            }
        }
        return list;
    }

    public void toggleAccountFreeze(String accountNumber, boolean freeze) throws SQLException {
        String status = freeze ? "FROZEN" : "ACTIVE";
        String sql = "UPDATE bank_accounts SET is_frozen = ?, status = ? WHERE account_number = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, freeze);
            stmt.setString(2, status);
            stmt.setString(3, accountNumber);
            stmt.executeUpdate();
            LOGGER.info("Admin toggled freeze for account: " + accountNumber + " to " + freeze);
        }
    }

    public void toggleUserBlock(long userId, boolean block) throws SQLException {
        String status = block ? "LOCKED" : "ACTIVE";
        String sql = "UPDATE users SET is_locked = ?, status = ?, failed_login_attempts = 0, lock_until = NULL WHERE user_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, block);
            stmt.setString(2, status);
            stmt.setLong(3, userId);
            stmt.executeUpdate();
            LOGGER.info("Admin toggled block for user ID: " + userId + " to " + block);
        }
    }

    public Transaction adminDeposit(String accountNumber, BigDecimal amount, String remarks, long adminUserId) throws Exception {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be strictly greater than zero.");
        }

        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            BankAccount account = bankAccountDAO.findByAccountNumber(accountNumber);
            if (account == null) throw new IllegalArgumentException("Account not found: " + accountNumber);

            // 1. Create Transaction
            String txnRef = IDGenerator.generateTransactionRef();
            Transaction txn = new Transaction();
            txn.setTransactionRef(txnRef);
            txn.setDestinationAccountId(account.getAccountId());
            txn.setAmount(amount);
            txn.setCurrency(account.getCurrency());
            txn.setTransactionType("DEPOSIT");
            txn.setPaymentMethod("ADMIN_DEPOSIT");
            txn.setStatus("PENDING");
            txn.setRemarks(remarks != null ? remarks : "Admin Cash Deposit");
            txn.setInitiatedByUserId(adminUserId);

            long txnId = transactionDAO.createTransaction(conn, txn);
            txn.setTransactionId(txnId);

            // 2. Update Balance
            BigDecimal newBalance = account.getBalance().add(amount);
            bankAccountDAO.updateAccountBalances(conn, account.getAccountId(), newBalance);

            // 3. Insert Immutable Ledger Credit Entry
            LedgerEntry creditEntry = new LedgerEntry(txnId, account.getAccountId(), "CREDIT", amount, newBalance,
                    "Admin Deposit: " + (remarks != null ? remarks : "Cash Deposit"));
            ledgerDAO.insertLedgerEntry(conn, creditEntry);

            // 4. Mark COMPLETED
            transactionDAO.updateStatus(conn, txnId, "COMPLETED", null);
            txn.setStatus("COMPLETED");

            conn.commit();

            // Dispatch notification
            new NotificationService().sendTransactionAlerts(txn);

            return txn;
        } catch (Exception e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    public Transaction adminWithdraw(String accountNumber, BigDecimal amount, String remarks, long adminUserId) throws Exception {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be strictly greater than zero.");
        }

        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            BankAccount account = bankAccountDAO.findByAccountNumber(accountNumber);
            if (account == null) throw new IllegalArgumentException("Account not found: " + accountNumber);

            if (account.getAvailableBalance().compareTo(amount) < 0) {
                throw new IllegalStateException("Insufficient funds in customer account for withdrawal. Available: " + account.getAvailableBalance());
            }

            // 1. Create Transaction
            String txnRef = IDGenerator.generateTransactionRef();
            Transaction txn = new Transaction();
            txn.setTransactionRef(txnRef);
            txn.setSourceAccountId(account.getAccountId());
            txn.setAmount(amount);
            txn.setCurrency(account.getCurrency());
            txn.setTransactionType("WITHDRAWAL");
            txn.setPaymentMethod("ADMIN_WITHDRAW");
            txn.setStatus("PENDING");
            txn.setRemarks(remarks != null ? remarks : "Admin Cash Withdrawal");
            txn.setInitiatedByUserId(adminUserId);

            long txnId = transactionDAO.createTransaction(conn, txn);
            txn.setTransactionId(txnId);

            // 2. Update Balance
            BigDecimal newBalance = account.getBalance().subtract(amount);
            bankAccountDAO.updateAccountBalances(conn, account.getAccountId(), newBalance);

            // 3. Insert Immutable Ledger Debit Entry
            LedgerEntry debitEntry = new LedgerEntry(txnId, account.getAccountId(), "DEBIT", amount, newBalance,
                    "Admin Withdrawal: " + (remarks != null ? remarks : "Cash Withdrawal"));
            ledgerDAO.insertLedgerEntry(conn, debitEntry);

            // 4. Mark COMPLETED
            transactionDAO.updateStatus(conn, txnId, "COMPLETED", null);
            txn.setStatus("COMPLETED");

            conn.commit();

            // Dispatch notification
            new NotificationService().sendTransactionAlerts(txn);

            return txn;
        } catch (Exception e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    public Transaction adminTransfer(String sourceAccountNo, String destAccountNo, BigDecimal amount, String remarks, long adminUserId) throws Exception {
        BankAccount srcAcc = bankAccountDAO.findByAccountNumber(sourceAccountNo);
        BankAccount dstAcc = bankAccountDAO.findByAccountNumber(destAccountNo);

        if (srcAcc == null) throw new IllegalArgumentException("Source account not found: " + sourceAccountNo);
        if (dstAcc == null) throw new IllegalArgumentException("Destination account not found: " + destAccountNo);

        return ledgerService.processFundTransfer(srcAcc.getAccountId(), dstAcc.getAccountId(), amount, "ADMIN_TRANSFER", remarks, adminUserId);
    }

    public void approveKyc(long kycId, long employeeId, boolean approve, String reason) throws SQLException {
        String status = approve ? "APPROVED" : "REJECTED";
        String sql = "UPDATE account_kyc SET verification_status = ?, rejection_reason = ?, verified_by_employee_id = ?, verified_at = CURRENT_TIMESTAMP WHERE kyc_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setString(2, reason);
            stmt.setLong(3, employeeId);
            stmt.setLong(4, kycId);
            stmt.executeUpdate();
        }
    }
}
