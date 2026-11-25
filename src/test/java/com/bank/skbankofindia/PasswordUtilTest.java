package com.bank.skbankofindia;

import com.bank.skbankofindia.util.PasswordUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordUtilTest {

    @Test
    @DisplayName("Should hash password and verify correct password match")
    public void testPasswordHashingAndVerification() {
        String plainPassword = "SecretBankPassword@2025";
        String hash = PasswordUtil.hashPassword(plainPassword);

        assertNotNull(hash);
        assertNotEquals(plainPassword, hash);
        assertTrue(PasswordUtil.verifyPassword(plainPassword, hash));
        assertFalse(PasswordUtil.verifyPassword("WrongPassword123", hash));
    }

    @Test
    @DisplayName("Should reject empty password hashing")
    public void testEmptyPasswordHashing() {
        assertThrows(IllegalArgumentException.class, () -> PasswordUtil.hashPassword(""));
    }
}
