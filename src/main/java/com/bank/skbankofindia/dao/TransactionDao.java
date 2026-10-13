package com.bank.skbankofindia.dao;

import com.bank.skbankofindia.model.Transaction;
import com.bank.skbankofindia.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDao {

    public List<Transaction> findTransactionsByAccountId(int accountId) {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM bank_transactions WHERE account_id = ? ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, accountId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Transaction tx = new Transaction();
                    tx.setId(rs.getInt("id"));
                    tx.setTransactionReference(rs.getString("transaction_reference"));
                    tx.setAccountId(rs.getInt("account_id"));
                    tx.setType(rs.getString("type"));
                    tx.setAmount(rs.getBigDecimal("amount"));
                    tx.setBalanceAfter(rs.getBigDecimal("balance_after"));
                    tx.setDescription(rs.getString("description"));
                    tx.setRelatedAccount(rs.getString("related_account"));
                    tx.setCreatedAt(rs.getTimestamp("created_at"));
                    list.add(tx);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
