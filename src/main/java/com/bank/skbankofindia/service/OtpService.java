package com.bank.skbankofindia.service;

import com.bank.skbankofindia.config.DatabaseConfig;
import com.bank.skbankofindia.util.IDGenerator;
import com.bank.skbankofindia.util.PasswordUtil;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OtpService {

    private static final Logger LOGGER = Logger.getLogger(OtpService.class.getName());
    private static final int OTP_EXPIRY_MINUTES = 10;

    public String generateAndSendOtp(String recipient, String purpose) throws Exception {
        String plainOtp = IDGenerator.generateOTP();
        String otpHash = PasswordUtil.hashPassword(plainOtp);
        Timestamp expiresAt = new Timestamp(System.currentTimeMillis() + (OTP_EXPIRY_MINUTES * 60 * 1000L));

        String sql = "INSERT INTO otp_verifications (recipient, otp_code_hash, purpose, expires_at) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, recipient);
            stmt.setString(2, otpHash);
            stmt.setString(3, purpose);
            stmt.setTimestamp(4, expiresAt);
            stmt.executeUpdate();
        }

        // Simulate sending OTP via SMS / Email Provider Sandbox
        LOGGER.info(String.format("========================================"));
        LOGGER.info(String.format("[OTP SERVICE] Sent OTP [%s] to [%s] for Purpose [%s]. Valid for %d mins.", plainOtp, recipient, purpose, OTP_EXPIRY_MINUTES));
        LOGGER.info(String.format("========================================"));

        return plainOtp; // Returned for sandbox testing
    }

    public boolean verifyOtp(String recipient, String plainOtp, String purpose) throws Exception {
        String sql = "SELECT * FROM otp_verifications WHERE recipient = ? AND purpose = ? AND is_verified = FALSE " +
                     "AND expires_at > CURRENT_TIMESTAMP ORDER BY otp_id DESC LIMIT 1";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, recipient);
            stmt.setString(2, purpose);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    long otpId = rs.getLong("otp_id");
                    String otpHash = rs.getString("otp_code_hash");

                    if (PasswordUtil.verifyPassword(plainOtp, otpHash)) {
                        markOtpAsVerified(otpId);
                        return true;
                    } else {
                        incrementAttemptCount(otpId);
                    }
                }
            }
        }
        return false;
    }

    private void markOtpAsVerified(long otpId) throws SQLException {
        String sql = "UPDATE otp_verifications SET is_verified = TRUE WHERE otp_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, otpId);
            stmt.executeUpdate();
        }
    }

    private void incrementAttemptCount(long otpId) throws SQLException {
        String sql = "UPDATE otp_verifications SET attempt_count = attempt_count + 1 WHERE otp_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, otpId);
            stmt.executeUpdate();
        }
    }
}
