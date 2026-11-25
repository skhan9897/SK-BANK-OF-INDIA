package com.bank.skbankofindia.service;

import com.bank.skbankofindia.config.DatabaseConfig;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class AdminService {

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
