package com.bank.skbankofindia;

import com.bank.skbankofindia.util.IDGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IDGeneratorTest {

    @Test
    @DisplayName("Should generate 14-digit Account Numbers with valid prefixes")
    public void testAccountNumberGeneration() {
        String savingsAccount = IDGenerator.generateAccountNumber("SAVINGS");
        String currentAccount = IDGenerator.generateAccountNumber("CURRENT");

        assertNotNull(savingsAccount);
        assertEquals(14, savingsAccount.length());
        assertTrue(savingsAccount.startsWith("1001"));

        assertNotNull(currentAccount);
        assertEquals(14, currentAccount.length());
        assertTrue(currentAccount.startsWith("2001"));
    }

    @Test
    @DisplayName("Should generate Customer Number, Txn Ref, Order Ref and OTP")
    public void testOtherIDGenerations() {
        String customerNo = IDGenerator.generateCustomerNumber();
        String txnRef = IDGenerator.generateTransactionRef();
        String orderRef = IDGenerator.generatePaymentOrderRef();
        String upiVpa = IDGenerator.generateUPIVPA("John.Doe");
        String otp = IDGenerator.generateOTP();

        assertTrue(customerNo.startsWith("SKC"));
        assertTrue(txnRef.startsWith("TXN"));
        assertTrue(orderRef.startsWith("ORD"));
        assertEquals("johndoe@skbank", upiVpa);
        assertEquals(6, otp.length());
    }
}
