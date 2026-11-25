package com.bank.skbankofindia.util;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[6-9]\\d{9}$");
    private static final Pattern PAN_PATTERN = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}");
    private static final Pattern AADHAAR_PATTERN = Pattern.compile("^[2-9]{1}[0-9]{11}$");
    private static final Pattern IFSC_PATTERN = Pattern.compile("^[A-Z]{4}0[A-Z0-9]{6}$");
    private static final Pattern VPA_PATTERN = Pattern.compile("^[a-zA-Z0-9.-]+@[a-zA-Z0-9.-]+$");

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    public static boolean isValidPAN(String pan) {
        return pan != null && PAN_PATTERN.matcher(pan.toUpperCase()).matches();
    }

    public static boolean isValidAadhaar(String aadhaar) {
        return aadhaar != null && AADHAAR_PATTERN.matcher(aadhaar).matches();
    }

    public static boolean isValidIFSC(String ifsc) {
        return ifsc != null && IFSC_PATTERN.matcher(ifsc.toUpperCase()).matches();
    }

    public static boolean isValidVPA(String vpa) {
        return vpa != null && VPA_PATTERN.matcher(vpa).matches();
    }

    public static boolean isValidAmount(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }

    private ValidationUtil() {}
}
