package com.bank.skbankofindia;

import com.bank.skbankofindia.model.Transaction;
import com.bank.skbankofindia.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class NotificationServiceTest {

    private NotificationService notificationService;

    @BeforeEach
    public void setUp() {
        notificationService = new NotificationService();
    }

    @Test
    @DisplayName("Should handle notification dispatch safely without throwing unexpected exceptions")
    public void testNotificationDispatchSafety() {
        Transaction txn = new Transaction();
        txn.setTransactionRef("TXN_TEST_101");
        txn.setAmount(new BigDecimal("1500.00"));
        txn.setSourceAccountId(101L);
        txn.setDestinationAccountId(102L);

        assertDoesNotThrow(() -> {
            notificationService.sendTransactionAlerts(txn);
        });
    }
}
