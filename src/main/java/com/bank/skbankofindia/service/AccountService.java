package com.bank.skbankofindia.service;

import com.bank.skbankofindia.config.DatabaseConfig;
import com.bank.skbankofindia.dao.BankAccountDAO;
import com.bank.skbankofindia.dao.CustomerDAO;
import com.bank.skbankofindia.dao.LedgerDAO;
import com.bank.skbankofindia.dao.TransactionDAO;
import com.bank.skbankofindia.model.BankAccount;
import com.bank.skbankofindia.model.Customer;
import com.bank.skbankofindia.model.LedgerEntry;
import com.bank.skbankofindia.model.Transaction;
import com.bank.skbankofindia.util.IDGenerator;
import com.bank.skbankofindia.util.PasswordUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountService {

    private final BankAccountDAO bankAccountDAO = new BankAccountDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final LedgerDAO ledgerDAO = new LedgerDAO();

    public List<BankAccount> getCustomerAccounts(long userId) throws Exception {
        Customer customer = customerDAO.findByUserId(userId);
        if (customer == null) {
            throw new IllegalArgumentException("Customer profile not found for user.");
        }
        return bankAccountDAO.findByCustomerId(customer.getCustomerId());
    }

    public BankAccount getAccountDetails(long userId, String accountNumber) throws Exception {
        Customer customer = customerDAO.findByUserId(userId);
        if (customer == null) throw new IllegalArgumentException("Customer not found.");

        BankAccount account = bankAccountDAO.findByAccountNumber(accountNumber);
        if (account == null) throw new IllegalArgumentException("Account not found: " + accountNumber);

        if (!account.getCustomerId().equals(customer.getCustomerId())) {
            throw new IllegalStateException("Unauthorized access to requested account details.");
        }
        return account;
    }

    public BankAccount createAdditionalAccount(long userId, String accountTypeCode) throws Exception {
        Customer customer = customerDAO.findByUserId(userId);
        if (customer == null) throw new IllegalArgumentException("Customer profile not found.");

        int typeId = "CURRENT".equalsIgnoreCase(accountTypeCode) ? 2 : ("SALARY".equalsIgnoreCase(accountTypeCode) ? 3 : 1);

        BankAccount newAccount = new BankAccount();
        newAccount.setCustomerId(customer.getCustomerId());
        newAccount.setAccountNumber(IDGenerator.generateAccountNumber(accountTypeCode));
        newAccount.setAccountTypeId(typeId);
        newAccount.setBranchId(customer.getBranchId() != null ? customer.getBranchId() : 1);
        newAccount.setCurrency("INR");
        newAccount.setBalance(new BigDecimal("5000.00")); // Minimum opening deposit
        newAccount.setAvailableBalance(new BigDecimal("5000.00"));
        newAccount.setLedgerBalance(new BigDecimal("5000.00"));
        newAccount.setStatus("ACTIVE");

        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
            long id = bankAccountDAO.createAccount(conn, newAccount);
            newAccount.setAccountId(id);
            conn.commit();
            return newAccount;
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

    public Map<String, Object> getAccountStatement(long userId, String accountNumber, int limit) throws Exception {
        BankAccount account = getAccountDetails(userId, accountNumber);

        List<Transaction> transactions = transactionDAO.findByAccountId(account.getAccountId(), limit);
        List<LedgerEntry> ledgerEntries = ledgerDAO.findByAccountId(account.getAccountId(), limit);

        Map<String, Object> statement = new HashMap<>();
        statement.put("account", account);
        statement.put("transactions", transactions);
        statement.put("ledgerEntries", ledgerEntries);
        statement.put("generatedAt", new Timestamp(System.currentTimeMillis()).toString());

        return statement;
    }

    public void addNominee(long userId, String accountNumber, String nomineeName, String relationship,
                           String dob, String phone, String address, BigDecimal percentage) throws Exception {
        BankAccount account = getAccountDetails(userId, accountNumber);

        String sql = "INSERT INTO nominees (account_id, nominee_name, relationship, date_of_birth, contact_number, address, percentage) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, account.getAccountId());
            stmt.setString(2, nomineeName);
            stmt.setString(3, relationship);
            stmt.setDate(4, Date.valueOf(dob));
            stmt.setString(5, phone);
            stmt.setString(6, address);
            stmt.setBigDecimal(7, percentage != null ? percentage : new BigDecimal("100.00"));
            stmt.executeUpdate();
        }
    }

    public void submitKyc(long userId, String docType, String docNumber, String frontUrl, String backUrl) throws Exception {
        Customer customer = customerDAO.findByUserId(userId);
        if (customer == null) throw new IllegalArgumentException("Customer profile not found.");

        String sql = "INSERT INTO account_kyc (customer_id, document_type, document_number, document_front_url, document_back_url, verification_status) " +
                     "VALUES (?, ?, ?, ?, ?, 'PENDING')";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, customer.getCustomerId());
            stmt.setString(2, docType);
            stmt.setString(3, docNumber);
            stmt.setString(4, frontUrl);
            stmt.setString(5, backUrl);
            stmt.executeUpdate();
        }

        // Update customer KYC status
        String updateCust = "UPDATE customers SET kyc_status = 'PENDING' WHERE customer_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateCust)) {
            stmt.setLong(1, customer.getCustomerId());
            stmt.executeUpdate();
        }
    }

    public void toggleAccountFreeze(long userId, String accountNumber, boolean freeze) throws Exception {
        BankAccount account = getAccountDetails(userId, accountNumber);
        String status = freeze ? "FROZEN" : "ACTIVE";

        String sql = "UPDATE bank_accounts SET is_frozen = ?, status = ? WHERE account_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, freeze);
            stmt.setString(2, status);
            stmt.setLong(3, account.getAccountId());
            stmt.executeUpdate();
        }
    }

    public void updateTransactionPin(long userId, String newPin) throws Exception {
        if (newPin == null || !newPin.matches("\\d{4,6}")) {
            throw new IllegalArgumentException("Transaction PIN must be 4 to 6 numeric digits.");
        }
        String pinHash = PasswordUtil.hashPassword(newPin);
        String sql = "UPDATE users SET transaction_pin_hash = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, pinHash);
            stmt.setLong(2, userId);
            stmt.executeUpdate();
        }
    }
}
