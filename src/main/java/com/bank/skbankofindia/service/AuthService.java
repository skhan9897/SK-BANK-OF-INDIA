package com.bank.skbankofindia.service;

import com.bank.skbankofindia.config.AppConstants;
import com.bank.skbankofindia.config.DatabaseConfig;
import com.bank.skbankofindia.dao.BankAccountDAO;
import com.bank.skbankofindia.dao.CustomerDAO;
import com.bank.skbankofindia.dao.UserDAO;
import com.bank.skbankofindia.model.BankAccount;
import com.bank.skbankofindia.model.Customer;
import com.bank.skbankofindia.model.User;
import com.bank.skbankofindia.util.IDGenerator;
import com.bank.skbankofindia.util.JwtUtil;
import com.bank.skbankofindia.util.PasswordUtil;
import com.bank.skbankofindia.util.ValidationUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AuthService {

    private static final Logger LOGGER = Logger.getLogger(AuthService.class.getName());

    private final UserDAO userDAO = new UserDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final BankAccountDAO bankAccountDAO = new BankAccountDAO();
    private final OtpService otpService = new OtpService();

    public Map<String, Object> registerCustomer(String username, String email, String phone, String password,
                                                String firstName, String middleName, String lastName,
                                                String dob, String gender, String pan, String aadhaar,
                                                String address1, String address2, String city, String state, String pincode) throws Exception {

        // Validations
        if (!ValidationUtil.isValidEmail(email)) throw new IllegalArgumentException("Invalid email address format.");
        if (!ValidationUtil.isValidPhone(phone)) throw new IllegalArgumentException("Invalid 10-digit mobile number.");
        if (pan != null && !pan.isEmpty() && !ValidationUtil.isValidPAN(pan)) throw new IllegalArgumentException("Invalid PAN format.");
        if (aadhaar != null && !aadhaar.isEmpty() && !ValidationUtil.isValidAadhaar(aadhaar)) throw new IllegalArgumentException("Invalid Aadhaar number format.");
        if (password == null || password.length() < 8) throw new IllegalArgumentException("Password must be at least 8 characters long.");

        if (userDAO.findByUsernameOrEmailOrPhone(username) != null) throw new IllegalArgumentException("Username already registered.");
        if (userDAO.findByUsernameOrEmailOrPhone(email) != null) throw new IllegalArgumentException("Email address already registered.");
        if (userDAO.findByUsernameOrEmailOrPhone(phone) != null) throw new IllegalArgumentException("Phone number already registered.");

        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            // 1. Create User
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPhoneNumber(phone);
            user.setPasswordHash(PasswordUtil.hashPassword(password));
            user.setRoleId(3); // ROLE_CUSTOMER
            user.setStatus("ACTIVE");

            long userId = userDAO.createUser(conn, user);
            user.setUserId(userId);

            // 2. Create Customer Profile
            Customer customer = new Customer();
            customer.setUserId(userId);
            customer.setCustomerNumber(IDGenerator.generateCustomerNumber());
            customer.setFirstName(firstName);
            customer.setMiddleName(middleName);
            customer.setLastName(lastName);
            customer.setDateOfBirth(Date.valueOf(dob));
            customer.setGender(gender != null ? gender : "MALE");
            customer.setPanNumber(pan != null ? pan.toUpperCase() : null);
            customer.setAadhaarNumber(aadhaar);
            customer.setAddressLine1(address1);
            customer.setAddressLine2(address2);
            customer.setCity(city);
            customer.setState(state);
            customer.setPincode(pincode);
            customer.setBranchId(1); // Default Main Branch
            customer.setKycStatus("VERIFIED");

            long customerId = customerDAO.createCustomer(conn, customer);
            customer.setCustomerId(customerId);

            // 3. Open Initial Savings Account with Welcome Deposit (INR 10,000)
            BankAccount account = new BankAccount();
            account.setCustomerId(customerId);
            account.setAccountNumber(IDGenerator.generateAccountNumber("SAVINGS"));
            account.setAccountTypeId(1); // SAVINGS
            account.setBranchId(1);
            account.setCurrency("INR");
            account.setBalance(new BigDecimal("10000.00"));
            account.setAvailableBalance(new BigDecimal("10000.00"));
            account.setLedgerBalance(new BigDecimal("10000.00"));
            account.setStatus("ACTIVE");

            long accountId = bankAccountDAO.createAccount(conn, account);
            account.setAccountId(accountId);

            // 4. Create Initial UPI VPA profile
            String vpa = IDGenerator.generateUPIVPA(username);
            String upiPinHash = PasswordUtil.hashPassword("1234"); // Default transaction PIN
            String upiSql = "INSERT INTO upi_profiles (customer_id, account_id, vpa, upi_pin_hash) VALUES (?, ?, ?, ?)";
            try (PreparedStatement upiStmt = conn.prepareStatement(upiSql)) {
                upiStmt.setLong(1, customerId);
                upiStmt.setLong(2, accountId);
                upiStmt.setString(3, vpa);
                upiStmt.setString(4, upiPinHash);
                upiStmt.executeUpdate();
            }

            conn.commit();

            String token = JwtUtil.generateToken(userId, username, "ROLE_CUSTOMER", email);

            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("user", user);
            result.put("customer", customer);
            result.put("account", account);
            result.put("vpa", vpa);

            LOGGER.info("Registered customer successfully: " + username + ", Acc: " + account.getAccountNumber());
            return result;

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

    public Map<String, Object> login(String identifier, String password, String ipAddress, String userAgent) throws Exception {
        User user = userDAO.findByUsernameOrEmailOrPhone(identifier);

        if (user == null) {
            throw new IllegalArgumentException("Invalid username or password.");
        }

        // Account Lockout check
        if (Boolean.TRUE.equals(user.getIsLocked())) {
            if (user.getLockUntil() != null && user.getLockUntil().after(new Timestamp(System.currentTimeMillis()))) {
                userDAO.recordLoginHistory(user.getUserId(), ipAddress, userAgent, "LOCKED_OUT", "Account locked due to repeated failed logins");
                throw new IllegalStateException("Account is temporarily locked. Please try again after 30 minutes.");
            } else {
                // Lock expired, reset
                userDAO.resetFailedLogin(user.getUserId());
                user.setIsLocked(false);
                user.setFailedLoginAttempts(0);
            }
        }

        // Check Password
        if (!PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
            int newAttempts = user.getFailedLoginAttempts() + 1;
            boolean lock = newAttempts >= AppConstants.MAX_FAILED_LOGIN_ATTEMPTS;
            Timestamp lockUntil = lock ? new Timestamp(System.currentTimeMillis() + (AppConstants.LOCKOUT_DURATION_MINUTES * 60 * 1000L)) : null;

            userDAO.recordFailedLogin(user.getUserId(), newAttempts, lock, lockUntil);
            userDAO.recordLoginHistory(user.getUserId(), ipAddress, userAgent, "FAILED_PASSWORD", "Incorrect password attempt " + newAttempts);

            if (lock) {
                throw new IllegalStateException("Account locked due to " + AppConstants.MAX_FAILED_LOGIN_ATTEMPTS + " consecutive failed login attempts.");
            } else {
                throw new IllegalArgumentException("Invalid username or password. Remaining attempts: " + (AppConstants.MAX_FAILED_LOGIN_ATTEMPTS - newAttempts));
            }
        }

        // Login Success
        userDAO.resetFailedLogin(user.getUserId());
        userDAO.recordLoginHistory(user.getUserId(), ipAddress, userAgent, "SUCCESS", null);

        String token = JwtUtil.generateToken(user.getUserId(), user.getUsername(), user.getRoleName(), user.getEmail());

        Customer customer = customerDAO.findByUserId(user.getUserId());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", user);
        result.put("customer", customer);

        return result;
    }

    public String initiateForgotPassword(String identifier) throws Exception {
        User user = userDAO.findByUsernameOrEmailOrPhone(identifier);
        if (user == null) {
            throw new IllegalArgumentException("No account found matching given identifier.");
        }
        return otpService.generateAndSendOtp(user.getEmail(), "FORGOT_PASSWORD");
    }

    public void resetPassword(String identifier, String otp, String newPassword) throws Exception {
        User user = userDAO.findByUsernameOrEmailOrPhone(identifier);
        if (user == null) {
            throw new IllegalArgumentException("No account found matching given identifier.");
        }
        boolean verified = otpService.verifyOtp(user.getEmail(), otp, "FORGOT_PASSWORD");
        if (!verified) {
            throw new IllegalArgumentException("Invalid or expired OTP code.");
        }

        String newHash = PasswordUtil.hashPassword(newPassword);
        String sql = "UPDATE users SET password_hash = ?, failed_login_attempts = 0, is_locked = FALSE, lock_until = NULL WHERE user_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newHash);
            stmt.setLong(2, user.getUserId());
            stmt.executeUpdate();
        }
    }
}
