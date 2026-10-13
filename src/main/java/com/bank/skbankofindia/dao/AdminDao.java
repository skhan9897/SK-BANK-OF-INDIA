package com.bank.skbankofindia.dao;

import com.bank.skbankofindia.model.Bank;
import com.bank.skbankofindia.model.Branch;
import com.bank.skbankofindia.model.Customer;
import com.bank.skbankofindia.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminDao {

    public List<Bank> getAllBanks() {
        List<Bank> list = new ArrayList<>();
        String sql = "SELECT * FROM banks";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Bank b = new Bank();
                b.setId(rs.getInt("id"));
                b.setBankCode(rs.getString("bank_code"));
                b.setBankName(rs.getString("bank_name"));
                b.setSector(rs.getString("sector"));
                b.setHeadquarters(rs.getString("headquarters"));
                b.setActive(rs.getBoolean("is_active"));
                list.add(b);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean registerBank(String bankCode, String bankName, String sector, String headquarters, String website) {
        String sql = "INSERT INTO banks (bank_code, bank_name, sector, headquarters, website_url, is_active) VALUES (?, ?, ?, ?, ?, TRUE)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, bankCode.trim().toUpperCase());
            stmt.setString(2, bankName.trim());
            stmt.setString(3, sector);
            stmt.setString(4, headquarters);
            stmt.setString(5, website);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateBankStatus(int bankId, boolean isActive) {
        String sql = "UPDATE banks SET is_active = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, isActive);
            stmt.setInt(2, bankId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Branch> getAllBranches() {
        List<Branch> list = new ArrayList<>();
        String sql = "SELECT b.*, bk.bank_name FROM bank_branches b JOIN banks bk ON b.bank_id = bk.id";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Branch br = new Branch();
                br.setId(rs.getInt("id"));
                br.setBankId(rs.getInt("bank_id"));
                br.setBranchName(rs.getString("branch_name") + " (" + rs.getString("bank_name") + ")");
                br.setIfscCode(rs.getString("ifsc_code"));
                br.setCity(rs.getString("city"));
                br.setState(rs.getString("state"));
                br.setVerificationStatus(rs.getString("verification_status"));
                list.add(br);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Customer> getAllCustomers() {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customers";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Customer c = new Customer();
                c.setId(rs.getInt("id"));
                c.setCustomerId(rs.getString("customer_id"));
                c.setFirstName(rs.getString("first_name"));
                c.setLastName(rs.getString("last_name"));
                c.setEmail(rs.getString("email"));
                c.setPhone(rs.getString("phone"));
                c.setStatus(rs.getString("status"));
                list.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateBranchVerification(int branchId, String status) {
        String sql = "UPDATE bank_branches SET verification_status = ?, last_verified_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, branchId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
