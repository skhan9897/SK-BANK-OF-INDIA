package com.bank.skbankofindia.dao;

import com.bank.skbankofindia.model.Customer;
import com.bank.skbankofindia.utils.DBConnection;

import java.sql.*;
import java.util.UUID;

public class CustomerDao {

    public boolean registerCustomer(Customer customer) {
        String sql = "INSERT INTO customers (customer_id, first_name, last_name, email, phone, password_hash, address, pan_number, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'ACTIVE')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            customer.setCustomerId(UUID.randomUUID().toString());
            stmt.setString(1, customer.getCustomerId());
            stmt.setString(2, customer.getFirstName());
            stmt.setString(3, customer.getLastName());
            stmt.setString(4, customer.getEmail());
            stmt.setString(5, customer.getPhone());
            stmt.setString(6, customer.getPasswordHash());
            stmt.setString(7, customer.getAddress());
            stmt.setString(8, customer.getPanNumber());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Customer findByEmail(String email) {
        String sql = "SELECT * FROM customers WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Customer c = new Customer();
                    c.setId(rs.getInt("id"));
                    c.setCustomerId(rs.getString("customer_id"));
                    c.setFirstName(rs.getString("first_name"));
                    c.setLastName(rs.getString("last_name"));
                    c.setEmail(rs.getString("email"));
                    c.setPhone(rs.getString("phone"));
                    c.setPasswordHash(rs.getString("password_hash"));
                    c.setAddress(rs.getString("address"));
                    c.setPanNumber(rs.getString("pan_number"));
                    c.setStatus(rs.getString("status"));
                    return c;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Customer findById(int id) {
        String sql = "SELECT * FROM customers WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Customer c = new Customer();
                    c.setId(rs.getInt("id"));
                    c.setCustomerId(rs.getString("customer_id"));
                    c.setFirstName(rs.getString("first_name"));
                    c.setLastName(rs.getString("last_name"));
                    c.setEmail(rs.getString("email"));
                    c.setPhone(rs.getString("phone"));
                    c.setPasswordHash(rs.getString("password_hash"));
                    c.setAddress(rs.getString("address"));
                    c.setPanNumber(rs.getString("pan_number"));
                    c.setStatus(rs.getString("status"));
                    return c;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
