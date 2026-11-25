package com.bank.skbankofindia;

import com.bank.skbankofindia.service.LedgerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class LedgerTransferTest {

    private LedgerService ledgerService;

    @BeforeEach
    public void setUp() {
        ledgerService = new LedgerService();
    }

    @Test
    @DisplayName("Should reject transfer when source and destination accounts are identical")
    public void testSameAccountTransferRejection() {
        assertThrows(IllegalArgumentException.class, () -> {
            ledgerService.processFundTransfer(1001L, 1001L, new BigDecimal("500.00"), "INTERNAL", "Self transfer", 1L);
        });
    }

    @Test
    @DisplayName("Should reject transfer when amount is zero or negative")
    public void testZeroOrNegativeAmountRejection() {
        assertThrows(IllegalArgumentException.class, () -> {
            ledgerService.processFundTransfer(1001L, 1002L, BigDecimal.ZERO, "INTERNAL", "Invalid amount", 1L);
        });
    }
}
