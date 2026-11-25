package com.bank.skbankofindia;

import com.bank.skbankofindia.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AccountServiceTest {

    private AccountService accountService;

    @BeforeEach
    public void setUp() {
        accountService = new AccountService();
    }

    @Test
    @DisplayName("Should throw exception when fetching accounts for non-existent user")
    public void testGetAccountsNonExistentUser() {
        assertThrows(Exception.class, () -> {
            accountService.getCustomerAccounts(-999L);
        });
    }

    @Test
    @DisplayName("Should throw exception when fetching statement for non-existent account")
    public void testGetStatementNonExistentAccount() {
        assertThrows(Exception.class, () -> {
            accountService.getAccountStatement(1L, "INVALID_ACC_NO", 10);
        });
    }
}
