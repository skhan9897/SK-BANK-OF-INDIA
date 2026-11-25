package com.bank.skbankofindia;

import com.bank.skbankofindia.util.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JwtUtilTest {

    @Test
    @DisplayName("Should generate valid JWT token and extract claims correctly")
    public void testTokenGenerationAndExtraction() {
        long userId = 101L;
        String username = "john_doe";
        String role = "ROLE_CUSTOMER";
        String email = "john@example.com";

        String token = JwtUtil.generateToken(userId, username, role, email);

        assertNotNull(token);
        assertTrue(JwtUtil.isTokenValid(token));
        assertEquals(username, JwtUtil.extractUsername(token));
        assertEquals(userId, JwtUtil.extractUserId(token));
        assertEquals(role, JwtUtil.extractRole(token));
    }

    @Test
    @DisplayName("Should return false for invalid token verification")
    public void testInvalidToken() {
        assertFalse(JwtUtil.isTokenValid("invalid.jwt.token"));
    }
}
