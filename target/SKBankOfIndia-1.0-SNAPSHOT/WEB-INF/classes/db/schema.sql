-- ==============================================================================
-- SK BANK OF INDIA - SHARED DATABASE SCHEMA
-- Compatible with MySQL 8.0+ / 5.7+
-- ==============================================================================

CREATE DATABASE IF NOT EXISTS `sk_bank_of_india`
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE `sk_bank_of_india`;

-- 1. Roles Table
CREATE TABLE IF NOT EXISTS `roles` (
    `role_id` INT AUTO_INCREMENT PRIMARY KEY,
    `role_name` VARCHAR(50) NOT NULL UNIQUE,
    `description` VARCHAR(255),
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Permissions Table
CREATE TABLE IF NOT EXISTS `permissions` (
    `permission_id` INT AUTO_INCREMENT PRIMARY KEY,
    `permission_name` VARCHAR(100) NOT NULL UNIQUE,
    `category` VARCHAR(50) NOT NULL,
    `description` VARCHAR(255),
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Role Permissions Table
CREATE TABLE IF NOT EXISTS `role_permissions` (
    `role_id` INT NOT NULL,
    `permission_id` INT NOT NULL,
    PRIMARY KEY (`role_id`, `permission_id`),
    CONSTRAINT `fk_rp_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`) ON DELETE CASCADE,
    CONSTRAINT `fk_rp_perm` FOREIGN KEY (`permission_id`) REFERENCES `permissions` (`permission_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Users Table
CREATE TABLE IF NOT EXISTS `users` (
    `user_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `email` VARCHAR(100) NOT NULL UNIQUE,
    `phone_number` VARCHAR(15) NOT NULL UNIQUE,
    `password_hash` VARCHAR(255) NOT NULL,
    `transaction_pin_hash` VARCHAR(255),
    `role_id` INT NOT NULL,
    `status` VARCHAR(20) DEFAULT 'ACTIVE', -- ACTIVE, INACTIVE, SUSPENDED, LOCKED
    `failed_login_attempts` INT DEFAULT 0,
    `is_locked` BOOLEAN DEFAULT FALSE,
    `lock_until` DATETIME NULL,
    `force_password_change` BOOLEAN DEFAULT FALSE,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_user_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`),
    INDEX `idx_users_email` (`email`),
    INDEX `idx_users_phone` (`phone_number`),
    INDEX `idx_users_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. Branches Table
CREATE TABLE IF NOT EXISTS `branches` (
    `branch_id` INT AUTO_INCREMENT PRIMARY KEY,
    `branch_code` VARCHAR(20) NOT NULL UNIQUE,
    `branch_name` VARCHAR(100) NOT NULL,
    `ifsc_code` VARCHAR(11) NOT NULL UNIQUE,
    `address` VARCHAR(255) NOT NULL,
    `city` VARCHAR(50) NOT NULL,
    `state` VARCHAR(50) NOT NULL,
    `pincode` VARCHAR(10) NOT NULL,
    `contact_number` VARCHAR(15),
    `email` VARCHAR(100),
    `status` VARCHAR(20) DEFAULT 'ACTIVE',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_branches_ifsc` (`ifsc_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. Customers Table
CREATE TABLE IF NOT EXISTS `customers` (
    `customer_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL UNIQUE,
    `customer_number` VARCHAR(20) NOT NULL UNIQUE,
    `first_name` VARCHAR(50) NOT NULL,
    `middle_name` VARCHAR(50),
    `last_name` VARCHAR(50) NOT NULL,
    `date_of_birth` DATE NOT NULL,
    `gender` VARCHAR(10) NOT NULL,
    `pan_number` VARCHAR(10) UNIQUE,
    `aadhaar_number` VARCHAR(12) UNIQUE,
    `address_line1` VARCHAR(150) NOT NULL,
    `address_line2` VARCHAR(150),
    `city` VARCHAR(50) NOT NULL,
    `state` VARCHAR(50) NOT NULL,
    `pincode` VARCHAR(10) NOT NULL,
    `branch_id` INT NOT NULL,
    `kyc_status` VARCHAR(20) DEFAULT 'PENDING', -- PENDING, VERIFIED, REJECTED
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_cust_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
    CONSTRAINT `fk_cust_branch` FOREIGN KEY (`branch_id`) REFERENCES `branches` (`branch_id`),
    INDEX `idx_cust_number` (`customer_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7. Employees Table
CREATE TABLE IF NOT EXISTS `employees` (
    `employee_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL UNIQUE,
    `employee_code` VARCHAR(20) NOT NULL UNIQUE,
    `first_name` VARCHAR(50) NOT NULL,
    `last_name` VARCHAR(50) NOT NULL,
    `email` VARCHAR(100) NOT NULL UNIQUE,
    `phone` VARCHAR(15) NOT NULL,
    `designation` VARCHAR(50) NOT NULL,
    `department` VARCHAR(50) NOT NULL,
    `branch_id` INT NOT NULL,
    `status` VARCHAR(20) DEFAULT 'ACTIVE',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_emp_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
    CONSTRAINT `fk_emp_branch` FOREIGN KEY (`branch_id`) REFERENCES `branches` (`branch_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 8. Admins Table
CREATE TABLE IF NOT EXISTS `admins` (
    `admin_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL UNIQUE,
    `admin_code` VARCHAR(20) NOT NULL UNIQUE,
    `full_name` VARCHAR(100) NOT NULL,
    `email` VARCHAR(100) NOT NULL UNIQUE,
    `phone` VARCHAR(15) NOT NULL,
    `department` VARCHAR(50) DEFAULT 'SYSTEM_ADMIN',
    `status` VARCHAR(20) DEFAULT 'ACTIVE',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_admin_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 9. Account Types Table
CREATE TABLE IF NOT EXISTS `account_types` (
    `account_type_id` INT AUTO_INCREMENT PRIMARY KEY,
    `type_code` VARCHAR(20) NOT NULL UNIQUE, -- SAVINGS, CURRENT, SALARY
    `type_name` VARCHAR(50) NOT NULL,
    `description` VARCHAR(255),
    `min_balance` DECIMAL(15, 2) DEFAULT 0.00,
    `interest_rate` DECIMAL(5, 2) DEFAULT 0.00,
    `daily_withdrawal_limit` DECIMAL(15, 2) DEFAULT 50000.00,
    `daily_transfer_limit` DECIMAL(15, 2) DEFAULT 100000.00,
    `status` VARCHAR(20) DEFAULT 'ACTIVE',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 10. Bank Accounts Table
CREATE TABLE IF NOT EXISTS `bank_accounts` (
    `account_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `customer_id` BIGINT NOT NULL,
    `account_number` VARCHAR(20) NOT NULL UNIQUE,
    `account_type_id` INT NOT NULL,
    `branch_id` INT NOT NULL,
    `currency` VARCHAR(3) DEFAULT 'INR',
    `balance` DECIMAL(18, 2) DEFAULT 0.00,
    `available_balance` DECIMAL(18, 2) DEFAULT 0.00,
    `ledger_balance` DECIMAL(18, 2) DEFAULT 0.00,
    `status` VARCHAR(20) DEFAULT 'ACTIVE', -- ACTIVE, INACTIVE, FROZEN, CLOSED
    `is_frozen` BOOLEAN DEFAULT FALSE,
    `opened_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `closed_at` DATETIME NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_acc_cust` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`),
    CONSTRAINT `fk_acc_type` FOREIGN KEY (`account_type_id`) REFERENCES `account_types` (`account_type_id`),
    CONSTRAINT `fk_acc_branch` FOREIGN KEY (`branch_id`) REFERENCES `branches` (`branch_id`),
    INDEX `idx_acc_number` (`account_number`),
    INDEX `idx_acc_customer` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 11. Account KYC Table
CREATE TABLE IF NOT EXISTS `account_kyc` (
    `kyc_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `customer_id` BIGINT NOT NULL,
    `document_type` VARCHAR(30) NOT NULL, -- AADHAAR, PAN, PASSPORT, VOTER_ID
    `document_number` VARCHAR(50) NOT NULL,
    `document_front_url` VARCHAR(255),
    `document_back_url` VARCHAR(255),
    `verification_status` VARCHAR(20) DEFAULT 'PENDING', -- PENDING, APPROVED, REJECTED
    `rejection_reason` VARCHAR(255),
    `verified_by_employee_id` BIGINT NULL,
    `verified_at` DATETIME NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_kyc_cust` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`),
    CONSTRAINT `fk_kyc_emp` FOREIGN KEY (`verified_by_employee_id`) REFERENCES `employees` (`employee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 12. Nominees Table
CREATE TABLE IF NOT EXISTS `nominees` (
    `nominee_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `account_id` BIGINT NOT NULL,
    `nominee_name` VARCHAR(100) NOT NULL,
    `relationship` VARCHAR(50) NOT NULL,
    `date_of_birth` DATE NOT NULL,
    `contact_number` VARCHAR(15),
    `address` VARCHAR(255),
    `guardian_name` VARCHAR(100),
    `percentage` DECIMAL(5, 2) DEFAULT 100.00,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_nominee_acc` FOREIGN KEY (`account_id`) REFERENCES `bank_accounts` (`account_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 13. Beneficiaries Table
CREATE TABLE IF NOT EXISTS `beneficiaries` (
    `beneficiary_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `customer_id` BIGINT NOT NULL,
    `beneficiary_name` VARCHAR(100) NOT NULL,
    `nickname` VARCHAR(50),
    `account_number` VARCHAR(20) NOT NULL,
    `ifsc_code` VARCHAR(11) NOT NULL,
    `bank_name` VARCHAR(100) DEFAULT 'SK Bank of India',
    `branch_name` VARCHAR(100),
    `upi_id` VARCHAR(50),
    `transfer_type` VARCHAR(20) DEFAULT 'INTERNAL', -- INTERNAL, NEFT, IMPS, RTGS, UPI
    `max_limit` DECIMAL(15, 2) DEFAULT 50000.00,
    `status` VARCHAR(20) DEFAULT 'ACTIVE',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_ben_cust` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`),
    INDEX `idx_ben_cust` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 14. Transactions Table
CREATE TABLE IF NOT EXISTS `transactions` (
    `transaction_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `transaction_ref` VARCHAR(50) NOT NULL UNIQUE,
    `source_account_id` BIGINT NULL,
    `destination_account_id` BIGINT NULL,
    `amount` DECIMAL(18, 2) NOT NULL,
    `currency` VARCHAR(3) DEFAULT 'INR',
    `transaction_type` VARCHAR(30) NOT NULL, -- DEPOSIT, WITHDRAWAL, TRANSFER, LOAN_DISBURSAL, BILL_PAYMENT
    `payment_method` VARCHAR(20) NOT NULL, -- INTERNAL, UPI, IMPS, NEFT, RTGS, CARD, CASH
    `status` VARCHAR(20) DEFAULT 'PENDING', -- PENDING, COMPLETED, FAILED, REVERSED
    `remarks` VARCHAR(255),
    `failure_reason` VARCHAR(255),
    `initiated_by_user_id` BIGINT NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `completed_at` DATETIME NULL,
    CONSTRAINT `fk_txn_src` FOREIGN KEY (`source_account_id`) REFERENCES `bank_accounts` (`account_id`),
    CONSTRAINT `fk_txn_dst` FOREIGN KEY (`destination_account_id`) REFERENCES `bank_accounts` (`account_id`),
    CONSTRAINT `fk_txn_user` FOREIGN KEY (`initiated_by_user_id`) REFERENCES `users` (`user_id`),
    INDEX `idx_txn_ref` (`transaction_ref`),
    INDEX `idx_txn_src` (`source_account_id`),
    INDEX `idx_txn_dst` (`destination_account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 15. Ledger Entries Table (STRICTLY IMMUTABLE DOUBLE-ENTRY LEDGER)
CREATE TABLE IF NOT EXISTS `ledger_entries` (
    `ledger_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `transaction_id` BIGINT NOT NULL,
    `account_id` BIGINT NOT NULL,
    `entry_type` VARCHAR(10) NOT NULL, -- DEBIT, CREDIT
    `amount` DECIMAL(18, 2) NOT NULL,
    `balance_after` DECIMAL(18, 2) NOT NULL,
    `description` VARCHAR(255) NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_ledger_txn` FOREIGN KEY (`transaction_id`) REFERENCES `transactions` (`transaction_id`),
    CONSTRAINT `fk_ledger_acc` FOREIGN KEY (`account_id`) REFERENCES `bank_accounts` (`account_id`),
    INDEX `idx_ledger_acc` (`account_id`),
    INDEX `idx_ledger_txn` (`transaction_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 16. Payment Orders Table (Gateway Integrations)
CREATE TABLE IF NOT EXISTS `payment_orders` (
    `order_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `order_ref` VARCHAR(50) NOT NULL UNIQUE,
    `customer_id` BIGINT NOT NULL,
    `account_id` BIGINT NOT NULL,
    `merchant_id` VARCHAR(50) DEFAULT 'SKBANK_DIRECT',
    `amount` DECIMAL(18, 2) NOT NULL,
    `currency` VARCHAR(3) DEFAULT 'INR',
    `gateway_name` VARCHAR(30) DEFAULT 'SANDBOX_PG',
    `gateway_order_id` VARCHAR(100),
    `gateway_payment_id` VARCHAR(100),
    `gateway_signature` VARCHAR(255),
    `status` VARCHAR(20) DEFAULT 'CREATED', -- CREATED, SUCCESS, FAILED, REFUNDED
    `idempotency_key` VARCHAR(100) NOT NULL UNIQUE,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_po_cust` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`),
    CONSTRAINT `fk_po_acc` FOREIGN KEY (`account_id`) REFERENCES `bank_accounts` (`account_id`),
    INDEX `idx_po_ref` (`order_ref`),
    INDEX `idx_po_idem` (`idempotency_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 17. Payment Provider Events (Webhooks Log)
CREATE TABLE IF NOT EXISTS `payment_provider_events` (
    `event_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `provider_name` VARCHAR(50) NOT NULL,
    `event_type` VARCHAR(50) NOT NULL,
    `payload_json` TEXT NOT NULL,
    `signature_verified` BOOLEAN DEFAULT FALSE,
    `processed_status` VARCHAR(20) DEFAULT 'PENDING',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 18. Payment Refunds Table
CREATE TABLE IF NOT EXISTS `payment_refunds` (
    `refund_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `refund_ref` VARCHAR(50) NOT NULL UNIQUE,
    `payment_order_id` BIGINT NOT NULL,
    `transaction_id` BIGINT NOT NULL,
    `amount` DECIMAL(18, 2) NOT NULL,
    `reason` VARCHAR(255),
    `status` VARCHAR(20) DEFAULT 'PROCESSING',
    `gateway_refund_id` VARCHAR(100),
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_pr_order` FOREIGN KEY (`payment_order_id`) REFERENCES `payment_orders` (`order_id`),
    CONSTRAINT `fk_pr_txn` FOREIGN KEY (`transaction_id`) REFERENCES `transactions` (`transaction_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 19. Payment Reconciliation Table
CREATE TABLE IF NOT EXISTS `payment_reconciliation` (
    `reconciliation_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `reconciliation_date` DATE NOT NULL UNIQUE,
    `total_gateway_transactions` INT DEFAULT 0,
    `total_ledger_transactions` INT DEFAULT 0,
    `total_amount_gateway` DECIMAL(18, 2) DEFAULT 0.00,
    `total_amount_ledger` DECIMAL(18, 2) DEFAULT 0.00,
    `status` VARCHAR(20) DEFAULT 'MATCHED', -- MATCHED, DISCREPANCY
    `discrepancies_count` INT DEFAULT 0,
    `report_json` TEXT,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 20. UPI Profiles Table
CREATE TABLE IF NOT EXISTS `upi_profiles` (
    `upi_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `customer_id` BIGINT NOT NULL,
    `account_id` BIGINT NOT NULL,
    `vpa` VARCHAR(50) NOT NULL UNIQUE, -- e.g., john@skbank
    `upi_pin_hash` VARCHAR(255) NOT NULL,
    `status` VARCHAR(20) DEFAULT 'ACTIVE',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_upi_cust` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`),
    CONSTRAINT `fk_upi_acc` FOREIGN KEY (`account_id`) REFERENCES `bank_accounts` (`account_id`),
    INDEX `idx_upi_vpa` (`vpa`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 21. Cards Table
CREATE TABLE IF NOT EXISTS `cards` (
    `card_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `account_id` BIGINT NOT NULL,
    `customer_id` BIGINT NOT NULL,
    `card_number_masked` VARCHAR(20) NOT NULL,
    `card_hash` VARCHAR(255) NOT NULL UNIQUE,
    `card_type` VARCHAR(20) NOT NULL, -- DEBIT, CREDIT, VIRTUAL
    `expiry_month` INT NOT NULL,
    `expiry_year` INT NOT NULL,
    `cvv_hash` VARCHAR(255) NOT NULL,
    `pin_hash` VARCHAR(255) NOT NULL,
    `daily_pos_limit` DECIMAL(15, 2) DEFAULT 50000.00,
    `daily_atm_limit` DECIMAL(15, 2) DEFAULT 25000.00,
    `is_international_enabled` BOOLEAN DEFAULT FALSE,
    `status` VARCHAR(20) DEFAULT 'ACTIVE', -- ACTIVE, LOCKED, BLOCKED
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_card_acc` FOREIGN KEY (`account_id`) REFERENCES `bank_accounts` (`account_id`),
    CONSTRAINT `fk_card_cust` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 22. Loans Table
CREATE TABLE IF NOT EXISTS `loans` (
    `loan_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `customer_id` BIGINT NOT NULL,
    `account_id` BIGINT NOT NULL,
    `loan_type` VARCHAR(30) NOT NULL, -- PERSONAL, HOME, VEHICLE, EDUCATION
    `principal_amount` DECIMAL(18, 2) NOT NULL,
    `interest_rate` DECIMAL(5, 2) NOT NULL,
    `tenure_months` INT NOT NULL,
    `emi_amount` DECIMAL(15, 2) NOT NULL,
    `outstanding_amount` DECIMAL(18, 2) NOT NULL,
    `status` VARCHAR(20) DEFAULT 'PENDING', -- PENDING, APPROVED, REJECTED, DISBURSED, CLOSED
    `approved_by_employee_id` BIGINT NULL,
    `approved_at` DATETIME NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_loan_cust` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`),
    CONSTRAINT `fk_loan_acc` FOREIGN KEY (`account_id`) REFERENCES `bank_accounts` (`account_id`),
    CONSTRAINT `fk_loan_emp` FOREIGN KEY (`approved_by_employee_id`) REFERENCES `employees` (`employee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 23. Loan Repayments Table
CREATE TABLE IF NOT EXISTS `loan_repayments` (
    `repayment_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `loan_id` BIGINT NOT NULL,
    `transaction_id` BIGINT NOT NULL,
    `installment_number` INT NOT NULL,
    `emi_amount` DECIMAL(15, 2) NOT NULL,
    `principal_component` DECIMAL(15, 2) NOT NULL,
    `interest_component` DECIMAL(15, 2) NOT NULL,
    `payment_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `status` VARCHAR(20) DEFAULT 'PAID',
    CONSTRAINT `fk_lr_loan` FOREIGN KEY (`loan_id`) REFERENCES `loans` (`loan_id`),
    CONSTRAINT `fk_lr_txn` FOREIGN KEY (`transaction_id`) REFERENCES `transactions` (`transaction_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 24. Fixed Deposits Table
CREATE TABLE IF NOT EXISTS `fixed_deposits` (
    `fd_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `customer_id` BIGINT NOT NULL,
    `account_id` BIGINT NOT NULL,
    `fd_number` VARCHAR(30) NOT NULL UNIQUE,
    `principal_amount` DECIMAL(18, 2) NOT NULL,
    `interest_rate` DECIMAL(5, 2) NOT NULL,
    `tenure_months` INT NOT NULL,
    `maturity_amount` DECIMAL(18, 2) NOT NULL,
    `maturity_date` DATE NOT NULL,
    `auto_renewal` BOOLEAN DEFAULT FALSE,
    `status` VARCHAR(20) DEFAULT 'ACTIVE', -- ACTIVE, CLOSED, MATURED
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_fd_cust` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`),
    CONSTRAINT `fk_fd_acc` FOREIGN KEY (`account_id`) REFERENCES `bank_accounts` (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 25. Recurring Deposits Table
CREATE TABLE IF NOT EXISTS `recurring_deposits` (
    `rd_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `customer_id` BIGINT NOT NULL,
    `account_id` BIGINT NOT NULL,
    `rd_number` VARCHAR(30) NOT NULL UNIQUE,
    `monthly_installment` DECIMAL(15, 2) NOT NULL,
    `interest_rate` DECIMAL(5, 2) NOT NULL,
    `tenure_months` INT NOT NULL,
    `maturity_amount` DECIMAL(18, 2) NOT NULL,
    `total_deposited` DECIMAL(18, 2) DEFAULT 0.00,
    `maturity_date` DATE NOT NULL,
    `status` VARCHAR(20) DEFAULT 'ACTIVE',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_rd_cust` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`),
    CONSTRAINT `fk_rd_acc` FOREIGN KEY (`account_id`) REFERENCES `bank_accounts` (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 26. Cheque Books Table
CREATE TABLE IF NOT EXISTS `cheque_books` (
    `cheque_book_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `account_id` BIGINT NOT NULL,
    `starting_cheque_num` INT NOT NULL,
    `total_leaves` INT DEFAULT 25,
    `status` VARCHAR(20) DEFAULT 'REQUESTED', -- REQUESTED, ISSUED, DELIVERED, DISPATCHED
    `requested_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `issued_at` DATETIME NULL,
    `delivered_at` DATETIME NULL,
    CONSTRAINT `fk_cb_acc` FOREIGN KEY (`account_id`) REFERENCES `bank_accounts` (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 27. Notifications Table
CREATE TABLE IF NOT EXISTS `notifications` (
    `notification_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL,
    `title` VARCHAR(100) NOT NULL,
    `message` TEXT NOT NULL,
    `type` VARCHAR(30) DEFAULT 'TRANSACTION', -- TRANSACTION, SECURITY, SYSTEM, LOAN
    `is_read` BOOLEAN DEFAULT FALSE,
    `channel` VARCHAR(20) DEFAULT 'IN_APP', -- IN_APP, PUSH, EMAIL, SMS
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_notif_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
    INDEX `idx_notif_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 28. OTP Verifications Table
CREATE TABLE IF NOT EXISTS `otp_verifications` (
    `otp_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `recipient` VARCHAR(100) NOT NULL, -- Email or Phone number
    `otp_code_hash` VARCHAR(255) NOT NULL,
    `purpose` VARCHAR(50) NOT NULL, -- LOGIN, REGISTER, TRANSFER, FORGOT_PASSWORD
    `is_verified` BOOLEAN DEFAULT FALSE,
    `expires_at` DATETIME NOT NULL,
    `attempt_count` INT DEFAULT 0,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_otp_rec` (`recipient`, `purpose`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 29. Service Requests Table
CREATE TABLE IF NOT EXISTS `service_requests` (
    `request_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `customer_id` BIGINT NOT NULL,
    `request_type` VARCHAR(50) NOT NULL, -- ADDRESS_CHANGE, CARD_REPLACEMENT, STATEMENT_REQUEST
    `title` VARCHAR(100) NOT NULL,
    `description` TEXT NOT NULL,
    `status` VARCHAR(20) DEFAULT 'OPEN', -- OPEN, IN_PROGRESS, RESOLVED, REJECTED
    `assigned_employee_id` BIGINT NULL,
    `resolution_notes` TEXT,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_sr_cust` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`),
    CONSTRAINT `fk_sr_emp` FOREIGN KEY (`assigned_employee_id`) REFERENCES `employees` (`employee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 30. Complaints Table
CREATE TABLE IF NOT EXISTS `complaints` (
    `complaint_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `customer_id` BIGINT NOT NULL,
    `complaint_ref` VARCHAR(50) NOT NULL UNIQUE,
    `category` VARCHAR(50) NOT NULL, -- FAILED_TRANSACTION, WRONG_DEBIT, CARD_ISSUE, OTHER
    `description` TEXT NOT NULL,
    `status` VARCHAR(20) DEFAULT 'LODGED', -- LODGED, INVESTIGATING, RESOLVED, CLOSED
    `priority` VARCHAR(20) DEFAULT 'MEDIUM', -- LOW, MEDIUM, HIGH, URGENT
    `resolution_details` TEXT,
    `resolved_by_employee_id` BIGINT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_comp_cust` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`),
    CONSTRAINT `fk_comp_emp` FOREIGN KEY (`resolved_by_employee_id`) REFERENCES `employees` (`employee_id`),
    INDEX `idx_comp_ref` (`complaint_ref`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 31. Audit Logs Table
CREATE TABLE IF NOT EXISTS `audit_logs` (
    `log_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NULL,
    `action` VARCHAR(100) NOT NULL,
    `entity_name` VARCHAR(50) NOT NULL,
    `entity_id` VARCHAR(50),
    `old_value_json` TEXT,
    `new_value_json` TEXT,
    `ip_address` VARCHAR(45),
    `user_agent` VARCHAR(255),
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_audit_user` (`user_id`),
    INDEX `idx_audit_action` (`action`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 32. Login History Table
CREATE TABLE IF NOT EXISTS `login_history` (
    `history_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL,
    `login_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `ip_address` VARCHAR(45),
    `user_agent` VARCHAR(255),
    `status` VARCHAR(20) NOT NULL, -- SUCCESS, FAILED_PASSWORD, LOCKED_OUT
    `failure_reason` VARCHAR(255),
    CONSTRAINT `fk_lh_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
    INDEX `idx_lh_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 33. Device Sessions Table
CREATE TABLE IF NOT EXISTS `device_sessions` (
    `session_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL,
    `session_token_hash` VARCHAR(255) NOT NULL UNIQUE,
    `device_id` VARCHAR(100) NOT NULL,
    `device_name` VARCHAR(100),
    `fcm_token` VARCHAR(255),
    `last_active` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `expires_at` DATETIME NOT NULL,
    `is_revoked` BOOLEAN DEFAULT FALSE,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_ds_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
    INDEX `idx_ds_token` (`session_token_hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 34. Fraud Alerts Table
CREATE TABLE IF NOT EXISTS `fraud_alerts` (
    `alert_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL,
    `transaction_id` BIGINT NULL,
    `rule_triggered` VARCHAR(100) NOT NULL,
    `risk_score` INT NOT NULL, -- 0-100
    `status` VARCHAR(20) DEFAULT 'OPEN', -- OPEN, UNDER_REVIEW, DISMISSED, CONFIRMED_FRAUD
    `resolution_notes` TEXT,
    `reviewed_by_admin_id` BIGINT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_fa_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
    CONSTRAINT `fk_fa_txn` FOREIGN KEY (`transaction_id`) REFERENCES `transactions` (`transaction_id`),
    CONSTRAINT `fk_fa_admin` FOREIGN KEY (`reviewed_by_admin_id`) REFERENCES `admins` (`admin_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 35. Chat Messages Table
CREATE TABLE IF NOT EXISTS `chat_messages` (
    `message_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `sender_user_id` BIGINT NOT NULL,
    `receiver_user_id` BIGINT NOT NULL,
    `message_type` VARCHAR(20) DEFAULT 'TEXT', -- TEXT, PAYMENT_REQUEST, PAYMENT_SUCCESS
    `content` TEXT NOT NULL,
    `amount` DECIMAL(18, 2) NULL,
    `transaction_ref` VARCHAR(50) NULL,
    `is_read` BOOLEAN DEFAULT FALSE,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_chat_snd` FOREIGN KEY (`sender_user_id`) REFERENCES `users` (`user_id`),
    CONSTRAINT `fk_chat_rcv` FOREIGN KEY (`receiver_user_id`) REFERENCES `users` (`user_id`),
    INDEX `idx_chat_users` (`sender_user_id`, `receiver_user_id`),
    INDEX `idx_chat_rcv` (`receiver_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

