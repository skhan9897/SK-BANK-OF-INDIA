package com.bank.skbankofindia;

import com.bank.skbankofindia.util.ValidationUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class ValidationUtilTest {

    @Test
    @DisplayName("Should validate Email, Phone, PAN, Aadhaar, IFSC, VPA, and Amounts correctly")
    public void testValidations() {
        assertTrue(ValidationUtil.isValidEmail("user@skbank.com"));
        assertFalse(ValidationUtil.isValidEmail("invalid-email"));

        assertTrue(ValidationUtil.isValidPhone("9876543210"));
        assertFalse(ValidationUtil.isValidPhone("12345"));

        assertTrue(ValidationUtil.isValidPAN("ABCDE1234F"));
        assertFalse(ValidationUtil.isValidPAN("12345ABCDE"));

        assertTrue(ValidationUtil.isValidAadhaar("998877665544"));
        assertFalse(ValidationUtil.isValidAadhaar("000011112222"));

        assertTrue(ValidationUtil.isValidIFSC("SKBI0000001"));
        assertFalse(ValidationUtil.isValidIFSC("INVALIDIFSC"));

        assertTrue(ValidationUtil.isValidVPA("alex@skbank"));
        assertFalse(ValidationUtil.isValidVPA("invalidvpa"));

        assertTrue(ValidationUtil.isValidAmount(new BigDecimal("100.50")));
        assertFalse(ValidationUtil.isValidAmount(new BigDecimal("-50.00")));
        assertFalse(ValidationUtil.isValidAmount(BigDecimal.ZERO));
    }
}
