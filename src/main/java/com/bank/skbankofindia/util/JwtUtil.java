package com.bank.skbankofindia.util;

import com.bank.skbankofindia.config.AppConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {

    private static SecretKey getSigningKey() {
        byte[] keyBytes = AppConstants.JWT_SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public static String generateToken(long userId, String username, String role, String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", role);
        claims.put("email", email);

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + AppConstants.JWT_EXPIRATION_MS);

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    public static Claims extractClaims(String token) throws JwtException {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public static String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public static Long extractUserId(String token) {
        Object val = extractClaims(token).get("userId");
        if (val instanceof Number n) {
            return n.longValue();
        }
        return null;
    }

    public static String extractRole(String token) {
        return (String) extractClaims(token).get("role");
    }

    public static boolean isTokenValid(String token) {
        try {
            Claims claims = extractClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private JwtUtil() {}
}
