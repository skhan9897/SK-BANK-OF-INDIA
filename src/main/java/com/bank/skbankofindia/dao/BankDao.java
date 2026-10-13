package com.bank.skbankofindia.dao;

import com.bank.skbankofindia.model.Branch;
import com.bank.skbankofindia.model.Bank;
import com.bank.skbankofindia.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BankDao {

    public Bank findBankById(int bankId) {
        String sql = "SELECT * FROM banks WHERE id = ? AND is_active = TRUE";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bankId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Bank bank = new Bank();
                    bank.setId(rs.getInt("id"));
                    bank.setBankCode(rs.getString("bank_code"));
                    bank.setBankName(rs.getString("bank_name"));
                    bank.setSector(rs.getString("sector"));
                    bank.setHeadquarters(rs.getString("headquarters"));
                    bank.setWebsiteUrl(rs.getString("website_url"));
                    bank.setLogoUrl(rs.getString("logo_url"));
                    bank.setActive(rs.getBoolean("is_active"));
                    return bank;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Branch findBranchByIfsc(String ifsc) {
        String sql = "SELECT b.* FROM bank_branches b JOIN banks bk ON b.bank_id = bk.id WHERE b.ifsc_code = ? AND bk.is_active = TRUE";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, ifsc.trim().toUpperCase());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Branch branch = new Branch();
                    branch.setId(rs.getInt("id"));
                    branch.setBankId(rs.getInt("bank_id"));
                    branch.setBranchName(rs.getString("branch_name"));
                    branch.setIfscCode(rs.getString("ifsc_code"));
                    branch.setMicrCode(rs.getString("micr_code"));
                    branch.setAddress(rs.getString("address"));
                    branch.setCity(rs.getString("city"));
                    branch.setDistrict(rs.getString("district"));
                    branch.setState(rs.getString("state"));
                    branch.setPinCode(rs.getString("pin_code"));
                    branch.setContactPhone(rs.getString("contact_phone"));
                    branch.setVerificationStatus(rs.getString("verification_status"));
                    return branch;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Branch> searchBranches(String query) {
        List<Branch> list = new ArrayList<>();
        String sql = "SELECT b.* FROM bank_branches b JOIN banks bk ON b.bank_id = bk.id " +
                     "WHERE (b.ifsc_code LIKE ? OR b.branch_name LIKE ? OR b.city LIKE ? OR b.state LIKE ? OR b.pin_code LIKE ? OR bk.bank_name LIKE ?) AND bk.is_active = TRUE";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String wildcard = "%" + query.trim() + "%";
            for (int i = 1; i <= 6; i++) {
                stmt.setString(i, wildcard);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Branch branch = new Branch();
                    branch.setId(rs.getInt("id"));
                    branch.setBankId(rs.getInt("bank_id"));
                    branch.setBranchName(rs.getString("branch_name"));
                    branch.setIfscCode(rs.getString("ifsc_code"));
                    branch.setMicrCode(rs.getString("micr_code"));
                    branch.setAddress(rs.getString("address"));
                    branch.setCity(rs.getString("city"));
                    branch.setDistrict(rs.getString("district"));
                    branch.setState(rs.getString("state"));
                    branch.setPinCode(rs.getString("pin_code"));
                    branch.setContactPhone(rs.getString("contact_phone"));
                    branch.setVerificationStatus(rs.getString("verification_status"));
                    list.add(branch);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
