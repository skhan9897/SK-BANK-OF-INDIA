package com.bank.skbankofindia.dao;

import com.bank.skbankofindia.model.Account;
import com.bank.skbankofindia.utils.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AccountDao {

    public boolean createAccount(Account account) {
        String sql = "INSERT INTO bank_accounts (account_number, customer_id, branch_id, account_type, balance, currency, status) VALUES (?, ?, ?, ?, ?, ?, 'ACTIVE')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Generate 12 digit unique account number starting with SKBI
            String accNum = "SKBI" + (100000000L + new Random().nextLong(900000000L));
            account.setAccountNumber(accNum);
            stmt.setString(1, accNum);
            stmt.setInt(2, account.getCustomerId());
            stmt.setInt(3, account.getBranchId());
            stmt.setString(4, account.getAccountType());
            stmt.setBigDecimal(5, BigDecimal.ZERO); // Zero opening balance
            stmt.setString(6, "INR");
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Account> findAccountsByCustomerId(int customerId) {
        List<Account> list = new ArrayList<>();
        String sql = "SELECT * FROM bank_accounts WHERE customer_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Account acc = new Account();
                    acc.setId(rs.getInt("id"));
                    acc.setAccountNumber(rs.getString("account_number"));
                    acc.setCustomerId(rs.getInt("customer_id"));
                    acc.setBranchId(rs.getInt("branch_id"));
                    acc.setAccountType(rs.getString("account_type"));
                    acc.setBalance(rs.getBigDecimal("balance"));
                    acc.setCurrency(rs.getString("currency"));
                    acc.setStatus(rs.getString("status"));
                    list.add(acc);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Account findByAccountNumber(String accountNumber) {
        String sql = "SELECT * FROM bank_accounts WHERE account_number = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, accountNumber.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Account acc = new Account();
                    acc.setId(rs.getInt("id"));
                    acc.setAccountNumber(rs.getString("account_number"));
                    acc.setCustomerId(rs.getInt("customer_id"));
                    acc.setBranchId(rs.getInt("branch_id"));
                    acc.setAccountType(rs.getString("account_type"));
                    acc.setBalance(rs.getBigDecimal("balance"));
                    acc.setCurrency(rs.getString("currency"));
                    acc.setStatus(rs.getString("status"));
                    return acc;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
