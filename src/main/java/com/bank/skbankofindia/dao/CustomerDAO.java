package com.bank.skbankofindia.dao;

import com.bank.skbankofindia.config.DatabaseConfig;
import com.bank.skbankofindia.model.Customer;

import java.sql.*;

public class CustomerDAO {

    public Customer findByUserId(long userId) throws SQLException {
        String sql = "SELECT * FROM customers WHERE user_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapCustomer(rs);
                }
            }
        }
        return null;
    }

    public Customer findByCustomerId(long customerId) throws SQLException {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapCustomer(rs);
                }
            }
        }
        return null;
    }

    public long createCustomer(Connection conn, Customer customer) throws SQLException {
        String sql = "INSERT INTO customers (user_id, customer_number, first_name, middle_name, last_name, " +
                     "date_of_birth, gender, pan_number, aadhaar_number, address_line1, address_line2, " +
                     "city, state, pincode, branch_id, kyc_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, customer.getUserId());
            stmt.setString(2, customer.getCustomerNumber());
            stmt.setString(3, customer.getFirstName());
            stmt.setString(4, customer.getMiddleName());
            stmt.setString(5, customer.getLastName());
            stmt.setDate(6, customer.getDateOfBirth());
            stmt.setString(7, customer.getGender());
            stmt.setString(8, customer.getPanNumber());
            stmt.setString(9, customer.getAadhaarNumber());
            stmt.setString(10, customer.getAddressLine1());
            stmt.setString(11, customer.getAddressLine2());
            stmt.setString(12, customer.getCity());
            stmt.setString(13, customer.getState());
            stmt.setString(14, customer.getPincode());
            stmt.setInt(15, customer.getBranchId() != null ? customer.getBranchId() : 1);
            stmt.setString(16, customer.getKycStatus() != null ? customer.getKycStatus() : "PENDING");
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
        }
        throw new SQLException("Failed to create customer record.");
    }

    private Customer mapCustomer(ResultSet rs) throws SQLException {
        Customer c = new Customer();
        c.setCustomerId(rs.getLong("customer_id"));
        c.setUserId(rs.getLong("user_id"));
        c.setCustomerNumber(rs.getString("customer_number"));
        c.setFirstName(rs.getString("first_name"));
        c.setMiddleName(rs.getString("middle_name"));
        c.setLastName(rs.getString("last_name"));
        c.setDateOfBirth(rs.getDate("date_of_birth"));
        c.setGender(rs.getString("gender"));
        c.setPanNumber(rs.getString("pan_number"));
        c.setAadhaarNumber(rs.getString("aadhaar_number"));
        c.setAddressLine1(rs.getString("address_line1"));
        c.setAddressLine2(rs.getString("address_line2"));
        c.setCity(rs.getString("city"));
        c.setState(rs.getString("state"));
        c.setPincode(rs.getString("pincode"));
        c.setBranchId(rs.getInt("branch_id"));
        c.setKycStatus(rs.getString("kyc_status"));
        c.setCreatedAt(rs.getTimestamp("created_at"));
        c.setUpdatedAt(rs.getTimestamp("updated_at"));
        return c;
    }
}
