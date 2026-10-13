package com.bank.skbankofindia;

import com.bank.skbankofindia.utils.SecurityUtils;
import com.bank.skbankofindia.utils.ValidationUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ValidationTest {

    @Test
    public void testValidIfsc() {
        assertTrue(ValidationUtils.isValidIfsc("SKBI0000001"));
        assertTrue(ValidationUtils.isValidIfsc("HDFC0INBB01"));
        assertFalse(ValidationUtils.isValidIfsc("SKBI1000001")); // 5th char not '0'
        assertFalse(ValidationUtils.isValidIfsc("SKBI00000"));    // too short
        assertFalse(ValidationUtils.isValidIfsc(null));
    }

    @Test
    public void testValidEmail() {
        assertTrue(ValidationUtils.isValidEmail("user@skbankofindia.com"));
        assertFalse(ValidationUtils.isValidEmail("invalid-email"));
    }

    @Test
    public void testPasswordHashing() {
        String password = "SecurePassword@123";
        String hash = SecurityUtils.hashPassword(password);
        assertNotNull(hash);
        assertTrue(SecurityUtils.verifyPassword(password, hash));
        assertFalse(SecurityUtils.verifyPassword("WrongPassword", hash));
    }
}
