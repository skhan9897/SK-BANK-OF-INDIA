package com.bank.skbankofindia.service;

import com.bank.skbankofindia.config.DatabaseConfig;
import com.bank.skbankofindia.dao.BankAccountDAO;
import com.bank.skbankofindia.dao.LedgerDAO;
import com.bank.skbankofindia.dao.TransactionDAO;
import com.bank.skbankofindia.model.BankAccount;
import com.bank.skbankofindia.model.LedgerEntry;
import com.bank.skbankofindia.model.Transaction;
import com.bank.skbankofindia.util.IDGenerator;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LedgerService {

    private static final Logger LOGGER = Logger.getLogger(LedgerService.class.getName());

    private final BankAccountDAO bankAccountDAO = new BankAccountDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final LedgerDAO ledgerDAO = new LedgerDAO();

    /**
     * Executes a double-entry fund transfer atomically between source and destination accounts.
     */
    public Transaction processFundTransfer(long sourceAccountId, long destinationAccountId, BigDecimal amount,
                                           String paymentMethod, String remarks, long userId) throws Exception {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be strictly greater than zero");
        }
        if (sourceAccountId == destinationAccountId) {
            throw new IllegalArgumentException("Source and destination accounts must be distinct");
        }

        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            // Deterministic Lock Order to prevent DB deadlocks
            long firstLockId = Math.min(sourceAccountId, destinationAccountId);
            long secondLockId = Math.max(sourceAccountId, destinationAccountId);

            BankAccount firstAcc = bankAccountDAO.findByAccountIdForUpdate(conn, firstLockId);
            BankAccount secondAcc = bankAccountDAO.findByAccountIdForUpdate(conn, secondLockId);

            BankAccount sourceAcc = (sourceAccountId == firstLockId) ? firstAcc : secondAcc;
            BankAccount destAcc = (destinationAccountId == firstLockId) ? firstAcc : secondAcc;

            if (sourceAcc == null || destAcc == null) {
                throw new IllegalArgumentException("One or both accounts do not exist");
            }
            if (!"ACTIVE".equals(sourceAcc.getStatus()) || Boolean.TRUE.equals(sourceAcc.getIsFrozen())) {
                throw new IllegalStateException("Source account is inactive or frozen");
            }
            if (!"ACTIVE".equals(destAcc.getStatus()) || Boolean.TRUE.equals(destAcc.getIsFrozen())) {
                throw new IllegalStateException("Destination account is inactive or frozen");
            }

            // Check Balance
            if (sourceAcc.getAvailableBalance().compareTo(amount) < 0) {
                throw new IllegalStateException("Insufficient funds in source account. Available: " + sourceAcc.getAvailableBalance());
            }

            // 1. Create Transaction record
            String txnRef = IDGenerator.generateTransactionRef();
            Transaction txn = new Transaction();
            txn.setTransactionRef(txnRef);
            txn.setSourceAccountId(sourceAccountId);
            txn.setDestinationAccountId(destinationAccountId);
            txn.setAmount(amount);
            txn.setCurrency(sourceAcc.getCurrency());
            txn.setTransactionType("TRANSFER");
            txn.setPaymentMethod(paymentMethod != null ? paymentMethod : "INTERNAL");
            txn.setStatus("PENDING");
            txn.setRemarks(remarks != null ? remarks : "Account Fund Transfer");
            txn.setInitiatedByUserId(userId);

            long txnId = transactionDAO.createTransaction(conn, txn);
            txn.setTransactionId(txnId);

            // 2. Debit Source Account
            BigDecimal newSourceBalance = sourceAcc.getBalance().subtract(amount);
            bankAccountDAO.updateAccountBalances(conn, sourceAccountId, newSourceBalance);

            // Immutable Ledger Debit Entry
            LedgerEntry debitEntry = new LedgerEntry(txnId, sourceAccountId, "DEBIT", amount, newSourceBalance,
                    "Debit transfer to Account: " + destAcc.getAccountNumber());
            ledgerDAO.insertLedgerEntry(conn, debitEntry);

            // 3. Credit Destination Account
            BigDecimal newDestBalance = destAcc.getBalance().add(amount);
            bankAccountDAO.updateAccountBalances(conn, destinationAccountId, newDestBalance);

            // Immutable Ledger Credit Entry
            LedgerEntry creditEntry = new LedgerEntry(txnId, destinationAccountId, "CREDIT", amount, newDestBalance,
                    "Credit transfer from Account: " + sourceAcc.getAccountNumber());
            ledgerDAO.insertLedgerEntry(conn, creditEntry);

            // 4. Mark Transaction COMPLETED
            transactionDAO.updateStatus(conn, txnId, "COMPLETED", null);
            txn.setStatus("COMPLETED");

            conn.commit();
            LOGGER.info("Successfully executed double-entry transfer. TxnRef: " + txnRef);
            return txn;

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, "Failed to rollback transaction", ex);
                }
            }
            LOGGER.log(Level.SEVERE, "Fund transfer failed: " + e.getMessage(), e);
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, "Failed to close connection", ex);
                }
            }
        }
    }
}
