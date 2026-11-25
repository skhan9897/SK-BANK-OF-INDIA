package com.bank.skbankofindia.config;

public class AppConstants {
    public static final String APP_NAME = "SK Bank of India";
    public static final String BANK_CODE = "SKBI";
    public static final String DEFAULT_IFSC = "SKBI0000001";
    
    // Auth & JWT
    public static final String JWT_SECRET_KEY = System.getenv().getOrDefault("JWT_SECRET", "SKBankOfIndiaSecretKeyForJWTAuth2025ProductionKeyRequire256BitsMin!");
    public static final long JWT_EXPIRATION_MS = 24 * 60 * 60 * 1000L; // 24 hours
    
    // Account Settings
    public static final String ACCOUNT_PREFIX_SAVINGS = "1001";
    public static final String ACCOUNT_PREFIX_CURRENT = "2001";
    public static final String ACCOUNT_PREFIX_SALARY = "3001";
    
    // Security limits
    public static final int MAX_FAILED_LOGIN_ATTEMPTS = 5;
    public static final int LOCKOUT_DURATION_MINUTES = 30;
    
    // Payment Gateway Sandbox
    public static final String PG_SANDBOX_KEY_ID = System.getenv().getOrDefault("PG_KEY_ID", "rzp_test_skbank_sandbox_key");
    public static final String PG_SANDBOX_SECRET = System.getenv().getOrDefault("PG_KEY_SECRET", "skbank_sandbox_secret_signature_key_998877");

    private AppConstants() {}
}
