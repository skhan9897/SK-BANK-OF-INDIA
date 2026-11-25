package com.bank.skbankofindia;

import com.bank.skbankofindia.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthServiceTest {

    private AuthService authService;

    @BeforeEach
    public void setUp() {
        authService = new AuthService();
    }

    @Test
    @DisplayName("Should reject registration with invalid email format")
    public void testRegistrationInvalidEmail() {
        assertThrows(IllegalArgumentException.class, () -> {
            authService.registerCustomer("user123", "invalid-email-format", "9876543210", "Password@123",
                    "John", "", "Doe", "1995-05-15", "MALE", "ABCDE1234F", "998877665544",
                    "Street 1", "", "Mumbai", "Maharashtra", "400001");
        });
    }

    @Test
    @DisplayName("Should reject registration with invalid 10-digit mobile number")
    public void testRegistrationInvalidPhone() {
        assertThrows(IllegalArgumentException.class, () -> {
            authService.registerCustomer("user123", "user@example.com", "12345", "Password@123",
                    "John", "", "Doe", "1995-05-15", "MALE", "ABCDE1234F", "998877665544",
                    "Street 1", "", "Mumbai", "Maharashtra", "400001");
        });
    }

    @Test
    @DisplayName("Should reject registration with short password (< 8 chars)")
    public void testRegistrationShortPassword() {
        assertThrows(IllegalArgumentException.class, () -> {
            authService.registerCustomer("user123", "user@example.com", "9876543210", "pass",
                    "John", "", "Doe", "1995-05-15", "MALE", "ABCDE1234F", "998877665544",
                    "Street 1", "", "Mumbai", "Maharashtra", "400001");
        });
    }
}
