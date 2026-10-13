-- =====================================================================
-- SK-BANK-OF-INDIA MULTI-BANK BANKING SOFTWARE DATABASE SCHEMA
-- Linked with SK Mini Bank (SKMiniBank) & SK Bank Of Bareilly (SK_Bank_Of_Bareilly.com)
-- Database: `SK-BANK-OF-INDIA-DB`
-- =====================================================================

CREATE DATABASE IF NOT EXISTS `SK-BANK-OF-INDIA-DB` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `SK-BANK-OF-INDIA-DB`;

-- Disable foreign key checks during schema creation
SET FOREIGN_KEY_CHECKS = 0;

-- 1. banks
CREATE TABLE IF NOT EXISTS `banks` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `bank_code` VARCHAR(20) NOT NULL UNIQUE,
    `bank_name` VARCHAR(150) NOT NULL,
    `sector` ENUM('PUBLIC', 'PRIVATE', 'FOREIGN', 'RRB', 'SMALL_FINANCE', 'PAYMENTS', 'COOPERATIVE') NOT NULL,
    `headquarters` VARCHAR(150),
    `website_url` VARCHAR(255),
    `logo_url` VARCHAR(255),
    `is_active` BOOLEAN DEFAULT TRUE,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. bank_branches
CREATE TABLE IF NOT EXISTS `bank_branches` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `bank_id` INT NOT NULL,
    `branch_name` VARCHAR(150) NOT NULL,
    `ifsc_code` CHAR(11) NOT NULL UNIQUE,
    `micr_code` VARCHAR(9),
    `address` TEXT,
    `city` VARCHAR(100) NOT NULL,
    `district` VARCHAR(100),
    `state` VARCHAR(100) NOT NULL,
    `pin_code` VARCHAR(10),
    `contact_phone` VARCHAR(20),
    `verification_status` ENUM('PENDING_REVIEW', 'VERIFIED', 'STALE', 'REJECTED') DEFAULT 'PENDING_REVIEW',
    `last_verified_at` TIMESTAMP NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`bank_id`) REFERENCES `banks`(`id`) ON DELETE CASCADE,
    CONSTRAINT `chk_ifsc_format` CHECK (`ifsc_code` REGEXP '^[A-Z]{4}0[A-Z0-9]{6}$')
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX `idx_branch_ifsc` ON `bank_branches`(`ifsc_code`);
CREATE INDEX `idx_branch_location` ON `bank_branches`(`city`, `state`, `pin_code`);
CREATE INDEX `idx_branch_name` ON `bank_branches`(`branch_name`);

-- 3. bank_identifiers
CREATE TABLE IF NOT EXISTS `bank_identifiers` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `bank_id` INT NOT NULL,
    `identifier_type` ENUM('LEI', 'SWIFT', 'BSR', 'UPI_HANDLE_PREFIX') NOT NULL,
    `identifier_value` VARCHAR(50) NOT NULL UNIQUE,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`bank_id`) REFERENCES `banks`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. bank_data_sources
CREATE TABLE IF NOT EXISTS `bank_data_sources` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `source_name` VARCHAR(150) NOT NULL,
    `source_url` VARCHAR(255) NOT NULL,
    `source_type` ENUM('RBI_PUBLICATION', 'BANK_DIRECTORY', 'PAYMENT_SYSTEM', 'MANUAL_IMPORT') NOT NULL,
    `is_authoritative` BOOLEAN DEFAULT TRUE,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. bank_verification_records
CREATE TABLE IF NOT EXISTS `bank_verification_records` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `branch_id` INT NOT NULL,
    `source_id` INT NOT NULL,
    `status` ENUM('PENDING_REVIEW', 'VERIFIED', 'STALE', 'REJECTED') DEFAULT 'PENDING_REVIEW',
    `verification_notes` TEXT,
    `verified_by` VARCHAR(100),
    `verified_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`branch_id`) REFERENCES `bank_branches`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`source_id`) REFERENCES `bank_data_sources`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. bank_data_sync_history
CREATE TABLE IF NOT EXISTS `bank_data_sync_history` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `source_id` INT NOT NULL,
    `sync_status` ENUM('SUCCESS', 'PARTIAL', 'FAILED') NOT NULL,
    `records_processed` INT DEFAULT 0,
    `records_added` INT DEFAULT 0,
    `records_updated` INT DEFAULT 0,
    `error_message` TEXT,
    `synced_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`source_id`) REFERENCES `bank_data_sources`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7. bank_service_capabilities
CREATE TABLE IF NOT EXISTS `bank_service_capabilities` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `bank_id` INT NOT NULL,
    `supports_neft` BOOLEAN DEFAULT TRUE,
    `supports_rtgs` BOOLEAN DEFAULT TRUE,
    `supports_imps` BOOLEAN DEFAULT TRUE,
    `supports_upi` BOOLEAN DEFAULT TRUE,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`bank_id`) REFERENCES `banks`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 8. bank_integration_configs
CREATE TABLE IF NOT EXISTS `bank_integration_configs` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `bank_id` INT NOT NULL,
    `environment` ENUM('SANDBOX', 'PRODUCTION') DEFAULT 'SANDBOX',
    `api_endpoint` VARCHAR(255),
    `client_id` VARCHAR(150),
    `client_secret` VARCHAR(255),
    `is_active` BOOLEAN DEFAULT FALSE,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`bank_id`) REFERENCES `banks`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 9. customers
CREATE TABLE IF NOT EXISTS `customers` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `customer_id` VARCHAR(36) NOT NULL UNIQUE,
    `first_name` VARCHAR(100) NOT NULL,
    `last_name` VARCHAR(100) NOT NULL,
    `email` VARCHAR(150) NOT NULL UNIQUE,
    `phone` VARCHAR(20) NOT NULL UNIQUE,
    `password_hash` VARCHAR(255) NOT NULL,
    `date_of_birth` DATE,
    `address` TEXT,
    `pan_number` VARCHAR(10),
    `aadhaar_masked` VARCHAR(12),
    `status` ENUM('PENDING', 'ACTIVE', 'SUSPENDED', 'CLOSED') DEFAULT 'PENDING',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 10. customer_kyc
CREATE TABLE IF NOT EXISTS `customer_kyc` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `customer_id` INT NOT NULL,
    `document_type` ENUM('PAN', 'AADHAAR', 'PASSPORT', 'VOTER_ID', 'DRIVING_LICENSE') NOT NULL,
    `document_number_encrypted` VARCHAR(255) NOT NULL,
    `document_image_path` VARCHAR(255),
    `kyc_status` ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
    `verified_by` VARCHAR(100),
    `verified_at` TIMESTAMP NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`customer_id`) REFERENCES `customers`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 11. roles
CREATE TABLE IF NOT EXISTS `roles` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `role_name` VARCHAR(50) NOT NULL UNIQUE,
    `description` VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 12. permissions
CREATE TABLE IF NOT EXISTS `permissions` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `permission_name` VARCHAR(100) NOT NULL UNIQUE,
    `description` VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 13. role_permissions
CREATE TABLE IF NOT EXISTS `role_permissions` (
    `role_id` INT NOT NULL,
    `permission_id` INT NOT NULL,
    PRIMARY KEY (`role_id`, `permission_id`),
    FOREIGN KEY (`role_id`) REFERENCES `roles`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`permission_id`) REFERENCES `permissions`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 14. customer_roles
CREATE TABLE IF NOT EXISTS `customer_roles` (
    `customer_id` INT NOT NULL,
    `role_id` INT NOT NULL,
    PRIMARY KEY (`customer_id`, `role_id`),
    FOREIGN KEY (`customer_id`) REFERENCES `customers`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`role_id`) REFERENCES `roles`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 15. admin_users
CREATE TABLE IF NOT EXISTS `admin_users` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(100) NOT NULL UNIQUE,
    `email` VARCHAR(150) NOT NULL UNIQUE,
    `password_hash` VARCHAR(255) NOT NULL,
    `full_name` VARCHAR(150) NOT NULL,
    `role_id` INT NOT NULL,
    `is_active` BOOLEAN DEFAULT TRUE,
    `last_login` TIMESTAMP NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`role_id`) REFERENCES `roles`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 16. bank_accounts
CREATE TABLE IF NOT EXISTS `bank_accounts` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `account_number` VARCHAR(20) NOT NULL UNIQUE,
    `customer_id` INT NOT NULL,
    `branch_id` INT NOT NULL,
    `account_type` ENUM('SAVINGS', 'CURRENT', 'SALARY', 'FIXED_DEPOSIT') NOT NULL,
    `balance` DECIMAL(15,2) DEFAULT 0.00,
    `currency` VARCHAR(3) DEFAULT 'INR',
    `status` ENUM('PENDING_ACTIVATION', 'ACTIVE', 'FROZEN', 'CLOSED') DEFAULT 'PENDING_ACTIVATION',
    `opened_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`customer_id`) REFERENCES `customers`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`branch_id`) REFERENCES `bank_branches`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX `idx_account_number` ON `bank_accounts`(`account_number`);

-- 17. external_bank_accounts
CREATE TABLE IF NOT EXISTS `external_bank_accounts` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `customer_id` INT NOT NULL,
    `account_holder_name` VARCHAR(150) NOT NULL,
    `account_number` VARCHAR(30) NOT NULL,
    `ifsc_code` CHAR(11) NOT NULL,
    `bank_name` VARCHAR(150),
    `is_verified` BOOLEAN DEFAULT FALSE,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`customer_id`) REFERENCES `customers`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 18. beneficiaries
CREATE TABLE IF NOT EXISTS `beneficiaries` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `customer_id` INT NOT NULL,
    `nickname` VARCHAR(100) NOT NULL,
    `beneficiary_name` VARCHAR(150) NOT NULL,
    `account_number` VARCHAR(30) NOT NULL,
    `ifsc_code` CHAR(11) NOT NULL,
    `bank_name` VARCHAR(150) NOT NULL,
    `transfer_type` ENUM('INTERNAL', 'NEFT', 'RTGS', 'IMPS', 'UPI') NOT NULL,
    `is_active` BOOLEAN DEFAULT TRUE,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`customer_id`) REFERENCES `customers`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 19. transfer_requests
CREATE TABLE IF NOT EXISTS `transfer_requests` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `reference_id` VARCHAR(50) NOT NULL UNIQUE,
    `source_account_id` INT NOT NULL,
    `destination_account_number` VARCHAR(30) NOT NULL,
    `destination_ifsc` CHAR(11) NOT NULL,
    `amount` DECIMAL(15,2) NOT NULL,
    `currency` VARCHAR(3) DEFAULT 'INR',
    `transfer_type` ENUM('INTERNAL', 'NEFT', 'RTGS', 'IMPS', 'UPI') NOT NULL,
    `status` ENUM('PENDING', 'PROCESSING', 'SUCCESS', 'FAILED', 'REVERSED') DEFAULT 'PENDING',
    `idempotency_key` VARCHAR(100) NOT NULL UNIQUE,
    `failure_reason` TEXT,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`source_account_id`) REFERENCES `bank_accounts`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 20. bank_transactions
CREATE TABLE IF NOT EXISTS `bank_transactions` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `transaction_reference` VARCHAR(50) NOT NULL UNIQUE,
    `account_id` INT NOT NULL,
    `type` ENUM('DEPOSIT', 'WITHDRAWAL', 'TRANSFER_IN', 'TRANSFER_OUT', 'FEE', 'INTEREST') NOT NULL,
    `amount` DECIMAL(15,2) NOT NULL,
    `balance_after` DECIMAL(15,2) NOT NULL,
    `description` TEXT,
    `related_account` VARCHAR(30),
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`account_id`) REFERENCES `bank_accounts`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 21. ledger_entries
CREATE TABLE IF NOT EXISTS `ledger_entries` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `journal_id` VARCHAR(50) NOT NULL,
    `account_number` VARCHAR(30) NOT NULL,
    `debit_amount` DECIMAL(15,2) DEFAULT 0.00,
    `credit_amount` DECIMAL(15,2) DEFAULT 0.00,
    `currency` VARCHAR(3) DEFAULT 'INR',
    `description` TEXT,
    `posted_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX `idx_ledger_journal` ON `ledger_entries`(`journal_id`);

-- 22. external_payment_requests
CREATE TABLE IF NOT EXISTS `external_payment_requests` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `provider` VARCHAR(50) NOT NULL,
    `provider_request_id` VARCHAR(100) NOT NULL UNIQUE,
    `payload` TEXT,
    `status` ENUM('INITIATED', 'SENT', 'ACKNOWLEDGED', 'FAILED') DEFAULT 'INITIATED',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 23. payment_provider_events
CREATE TABLE IF NOT EXISTS `payment_provider_events` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `provider` VARCHAR(50) NOT NULL,
    `event_type` VARCHAR(100) NOT NULL,
    `event_payload` TEXT,
    `received_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 24. reconciliation_records
CREATE TABLE IF NOT EXISTS `reconciliation_records` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `reconciliation_date` DATE NOT NULL,
    `total_debits` DECIMAL(18,2) DEFAULT 0.00,
    `total_credits` DECIMAL(18,2) DEFAULT 0.00,
    `status` ENUM('MATCHED', 'DISCREPANCY', 'RESOLVED') DEFAULT 'MATCHED',
    `notes` TEXT,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 25. complaints
CREATE TABLE IF NOT EXISTS `complaints` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `complaint_reference` VARCHAR(50) NOT NULL UNIQUE,
    `customer_id` INT NOT NULL,
    `subject` VARCHAR(200) NOT NULL,
    `description` TEXT NOT NULL,
    `status` ENUM('OPEN', 'IN_PROGRESS', 'RESOLVED', 'CLOSED') DEFAULT 'OPEN',
    `resolution_notes` TEXT,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`customer_id`) REFERENCES `customers`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 26. notifications
CREATE TABLE IF NOT EXISTS `notifications` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `customer_id` INT NOT NULL,
    `title` VARCHAR(150) NOT NULL,
    `message` TEXT NOT NULL,
    `is_read` BOOLEAN DEFAULT FALSE,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`customer_id`) REFERENCES `customers`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 27. audit_logs
CREATE TABLE IF NOT EXISTS `audit_logs` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `actor_type` ENUM('CUSTOMER', 'ADMIN', 'SYSTEM') NOT NULL,
    `actor_id` VARCHAR(100),
    `action` VARCHAR(150) NOT NULL,
    `ip_address` VARCHAR(45),
    `details` TEXT,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;

-- Seed Initial Data & Linked Banks:
-- 1. SK Bank of India (SKBI)
-- 2. SK Mini Bank (SKMB) - https://github.com/skhan9897/SKMiniBank
-- 3. SK Bank Of Bareilly (SKBB) - https://github.com/skhan9897/SK_Bank_Of_Bareilly.com

INSERT INTO `roles` (`id`, `role_name`, `description`) VALUES
(1, 'ROLE_ADMIN', 'System Administrator with full management privileges'),
(2, 'ROLE_OFFICER', 'Bank Officer for KYC and verification'),
(3, 'ROLE_CUSTOMER', 'Standard Banking Customer')
ON DUPLICATE KEY UPDATE `role_name`=`role_name`;

INSERT INTO `permissions` (`id`, `permission_name`, `description`) VALUES
(1, 'MANAGE_BANKS', 'Manage banks and branches'),
(2, 'VERIFY_IFSC', 'Verify IFSC and bank records'),
(3, 'MANAGE_CUSTOMERS', 'Manage customers and KYC'),
(4, 'VIEW_REPORTS', 'View financial and audit reports')
ON DUPLICATE KEY UPDATE `permission_name`=`permission_name`;

INSERT INTO `role_permissions` (`role_id`, `permission_id`) VALUES
(1, 1), (1, 2), (1, 3), (1, 4),
(2, 2), (2, 3)
ON DUPLICATE KEY UPDATE `role_id`=`role_id`;

INSERT INTO `admin_users` (`id`, `username`, `email`, `password_hash`, `full_name`, `role_id`, `is_active`)
VALUES (1, 'admin', 'admin@skbankofindia.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'System Administrator', 1, TRUE)
ON DUPLICATE KEY UPDATE `username`=`username`;

-- Seed Linked Banks
INSERT INTO `banks` (`id`, `bank_code`, `bank_name`, `sector`, `headquarters`, `website_url`, `logo_url`) VALUES
(1, 'SKBI', 'SK Bank of India', 'PUBLIC', 'Mumbai, India', 'https://www.skbankofindia.com', 'assets/logo.png'),
(2, 'SKMB', 'SK Mini Bank (SKMiniBank)', 'SMALL_FINANCE', 'New Delhi, India', 'https://github.com/skhan9897/SKMiniBank', 'assets/minibank_logo.png'),
(3, 'SKBB', 'SK Bank Of Bareilly', 'COOPERATIVE', 'Bareilly, UP, India', 'https://github.com/skhan9897/SK_Bank_Of_Bareilly.com', 'assets/bareilly_logo.png')
ON DUPLICATE KEY UPDATE `bank_name`=`bank_name`;

INSERT INTO `bank_data_sources` (`id`, `source_name`, `source_url`, `source_type`, `is_authoritative`)
VALUES (1, 'RBI & Inter-Bank Network Directory', 'https://rbi.org.in', 'RBI_PUBLICATION', TRUE)
ON DUPLICATE KEY UPDATE `source_name`=`source_name`;

-- Seed Verified Branches for All 3 Linked Banks (Enabling cross-bank IFSC lookup and NEFT/RTGS transfers)
INSERT INTO `bank_branches` (`id`, `bank_id`, `branch_name`, `ifsc_code`, `micr_code`, `address`, `city`, `district`, `state`, `pin_code`, `contact_phone`, `verification_status`) VALUES
(1, 1, 'Mumbai Main Branch', 'SKBI0000001', '400240001', 'Nariman Point', 'Mumbai', 'Mumbai City', 'Maharashtra', '400021', '02222888888', 'VERIFIED'),
(2, 2, 'Connaught Place Branch (SKMiniBank)', 'SKMB0000001', '110250001', 'Connaught Place', 'New Delhi', 'New Delhi', 'Delhi', '110001', '01123334444', 'VERIFIED'),
(3, 3, 'Civil Lines Branch (SK Bank Of Bareilly)', 'SKBB0000001', '243260001', 'Civil Lines', 'Bareilly', 'Bareilly', 'Uttar Pradesh', '243001', '05812555666', 'VERIFIED')
ON DUPLICATE KEY UPDATE `branch_name`=`branch_name`;
