package com.bank.skbankofindia.dao;

import com.bank.skbankofindia.config.DatabaseConfig;
import com.bank.skbankofindia.model.User;

import java.sql.*;

public class UserDAO {

    public User findByUsernameOrEmailOrPhone(String identifier) throws SQLException {
        String sql = "SELECT u.*, r.role_name FROM users u JOIN roles r ON u.role_id = r.role_id " +
                     "WHERE u.username = ? OR u.email = ? OR u.phone_number = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, identifier);
            stmt.setString(2, identifier);
            stmt.setString(3, identifier);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        }
        return null;
    }

    public User findById(long userId) throws SQLException {
        String sql = "SELECT u.*, r.role_name FROM users u JOIN roles r ON u.role_id = r.role_id WHERE u.user_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        }
        return null;
    }

    public long createUser(Connection conn, User user) throws SQLException {
        String sql = "INSERT INTO users (username, email, phone_number, password_hash, role_id, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPhoneNumber());
            stmt.setString(4, user.getPasswordHash());
            stmt.setInt(5, user.getRoleId());
            stmt.setString(6, user.getStatus() != null ? user.getStatus() : "ACTIVE");
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
        }
        throw new SQLException("Failed to create user, no ID returned.");
    }

    public void recordFailedLogin(long userId, int attempts, boolean lock, Timestamp lockUntil) throws SQLException {
        String sql = "UPDATE users SET failed_login_attempts = ?, is_locked = ?, lock_until = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, attempts);
            stmt.setBoolean(2, lock);
            stmt.setTimestamp(3, lockUntil);
            stmt.setLong(4, userId);
            stmt.executeUpdate();
        }
    }

    public void resetFailedLogin(long userId) throws SQLException {
        String sql = "UPDATE users SET failed_login_attempts = 0, is_locked = FALSE, lock_until = NULL WHERE user_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            stmt.executeUpdate();
        }
    }

    public void recordLoginHistory(long userId, String ipAddress, String userAgent, String status, String failureReason) throws SQLException {
        String sql = "INSERT INTO login_history (user_id, ip_address, user_agent, status, failure_reason) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            stmt.setString(2, ipAddress);
            stmt.setString(3, userAgent);
            stmt.setString(4, status);
            stmt.setString(5, failureReason);
            stmt.executeUpdate();
        }
    }

    private User mapUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUserId(rs.getLong("user_id"));
        u.setUsername(rs.getString("username"));
        u.setEmail(rs.getString("email"));
        u.setPhoneNumber(rs.getString("phone_number"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setTransactionPinHash(rs.getString("transaction_pin_hash"));
        u.setRoleId(rs.getInt("role_id"));
        u.setRoleName(rs.getString("role_name"));
        u.setStatus(rs.getString("status"));
        u.setFailedLoginAttempts(rs.getInt("failed_login_attempts"));
        u.setIsLocked(rs.getBoolean("is_locked"));
        u.setLockUntil(rs.getTimestamp("lock_until"));
        u.setForcePasswordChange(rs.getBoolean("force_password_change"));
        u.setCreatedAt(rs.getTimestamp("created_at"));
        u.setUpdatedAt(rs.getTimestamp("updated_at"));
        return u;
    }
}
