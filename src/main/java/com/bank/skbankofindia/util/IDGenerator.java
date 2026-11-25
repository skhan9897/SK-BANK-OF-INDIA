package com.bank.skbankofindia.util;

import com.bank.skbankofindia.config.AppConstants;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class IDGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public static String generateAccountNumber(String accountTypeCode) {
        String prefix = AppConstants.ACCOUNT_PREFIX_SAVINGS;
        if ("CURRENT".equalsIgnoreCase(accountTypeCode)) {
            prefix = AppConstants.ACCOUNT_PREFIX_CURRENT;
        } else if ("SALARY".equalsIgnoreCase(accountTypeCode)) {
            prefix = AppConstants.ACCOUNT_PREFIX_SALARY;
        }
        
        long randomTenDigits = 1000000000L + (long)(RANDOM.nextDouble() * 9000000000L);
        return prefix + randomTenDigits; // 14 digits account number
    }

    public static String generateCustomerNumber() {
        long randomEightDigits = 10000000L + (long)(RANDOM.nextDouble() * 90000000L);
        return "SKC" + randomEightDigits;
    }

    public static String generateTransactionRef() {
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        int randomFour = 1000 + RANDOM.nextInt(9000);
        return "TXN" + timestamp + randomFour;
    }

    public static String generatePaymentOrderRef() {
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        int randomFour = 1000 + RANDOM.nextInt(9000);
        return "ORD" + timestamp + randomFour;
    }

    public static String generateUPIVPA(String username) {
        String cleanUsername = username.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        return cleanUsername + "@skbank";
    }

    public static String generateIdempotencyKey() {
        return UUID.randomUUID().toString();
    }

    public static String generateOTP() {
        int otp = 100000 + RANDOM.nextInt(900000);
        return String.valueOf(otp);
    }

    private IDGenerator() {}
}
