-- ==============================================================================
-- SK BANK OF INDIA - SEED DATA SCRIPT
-- Default roles, permissions, branches, account types, and admin user
-- Password for default admin & sample users is: BankAdmin@123 (BCrypt hashed)
-- ==============================================================================

USE `sk_bank_of_india`;

-- Insert Roles
INSERT INTO `roles` (`role_id`, `role_name`, `description`) VALUES
(1, 'ROLE_ADMIN', 'System Administrator with full management access'),
(2, 'ROLE_EMPLOYEE', 'Bank Employee / Branch Manager for KYC & approvals'),
(3, 'ROLE_CUSTOMER', 'Banking Customer for web and mobile banking access')
ON DUPLICATE KEY UPDATE `description` = VALUES(`description`);

-- Insert Permissions
INSERT INTO `permissions` (`permission_id`, `permission_name`, `category`, `description`) VALUES
(1, 'VIEW_ANALYTICS', 'ADMIN', 'View administrative dashboard analytics'),
(2, 'MANAGE_CUSTOMERS', 'ADMIN', 'Create, update, freeze or deactivate customers'),
(3, 'APPROVE_KYC', 'EMPLOYEE', 'Review and approve/reject customer KYC documents'),
(4, 'APPROVE_LOAN', 'EMPLOYEE', 'Review and approve/reject loan applications'),
(5, 'EXECUTE_TRANSFER', 'CUSTOMER', 'Perform money transfers and payments')
ON DUPLICATE KEY UPDATE `description` = VALUES(`description`);

-- Role-Permission Mapping
INSERT INTO `role_permissions` (`role_id`, `permission_id`) VALUES
(1, 1), (1, 2), (1, 3), (1, 4),
(2, 3), (2, 4),
(3, 5)
ON DUPLICATE KEY UPDATE `role_id` = VALUES(`role_id`);

-- Insert Initial Branches
INSERT INTO `branches` (`branch_id`, `branch_code`, `branch_name`, `ifsc_code`, `address`, `city`, `state`, `pincode`, `contact_number`, `email`, `status`) VALUES
(1, 'SKB0001', 'Mumbai Main Branch', 'SKBI0000001', 'Financial District, Bandra Kurla Complex', 'Mumbai', 'Maharashtra', '400051', '022-61234567', 'mumbai.branch@skbank.com', 'ACTIVE'),
(2, 'SKB0002', 'New Delhi Connaught Place', 'SKBI0000002', 'Outer Circle, Connaught Place', 'New Delhi', 'Delhi', '110001', '011-41234567', 'delhi.branch@skbank.com', 'ACTIVE'),
(3, 'SKB0003', 'Bengaluru MG Road', 'SKBI0000003', 'MG Road, Commercial Center', 'Bengaluru', 'Karnataka', '560001', '080-21234567', 'bengaluru.branch@skbank.com', 'ACTIVE')
ON DUPLICATE KEY UPDATE `branch_name` = VALUES(`branch_name`);

-- Insert Account Types
INSERT INTO `account_types` (`account_type_id`, `type_code`, `type_name`, `description`, `min_balance`, `interest_rate`, `daily_withdrawal_limit`, `daily_transfer_limit`) VALUES
(1, 'SAVINGS', 'Regular Savings Account', 'Standard savings account for individuals with 3.50% interest', 1000.00, 3.50, 50000.00, 100000.00),
(2, 'CURRENT', 'Corporate Current Account', 'Business account with zero balance requirement and high daily limits', 0.00, 0.00, 200000.00, 1000000.00),
(3, 'SALARY', 'Premium Salary Account', 'Zero balance salary account with complementary debit card', 0.00, 4.00, 100000.00, 200000.00)
ON DUPLICATE KEY UPDATE `type_name` = VALUES(`type_name`);

-- Insert Default Admin User (Password: BankAdmin@123 -> $2a$10$E2S.02tD12N3wPzF.bYIauXwT61/2kQJ6bW1r05MvM3P.qT4mO82y)
INSERT INTO `users` (`user_id`, `username`, `email`, `phone_number`, `password_hash`, `role_id`, `status`) VALUES
(1, 'admin', 'admin@skbank.com', '9876543210', '$2a$10$E2S.02tD12N3wPzF.bYIauXwT61/2kQJ6bW1r05MvM3P.qT4mO82y', 1, 'ACTIVE')
ON DUPLICATE KEY UPDATE `email` = VALUES(`email`);

INSERT INTO `admins` (`admin_id`, `user_id`, `admin_code`, `full_name`, `email`, `phone`, `department`) VALUES
(1, 1, 'ADM001', 'SK Bank System Admin', 'admin@skbank.com', '9876543210', 'SYSTEM_ADMIN')
ON DUPLICATE KEY UPDATE `full_name` = VALUES(`full_name`);

-- Insert Default Bank Employee User (Password: BankAdmin@123)
INSERT INTO `users` (`user_id`, `username`, `email`, `phone_number`, `password_hash`, `role_id`, `status`) VALUES
(2, 'employee1', 'emp1@skbank.com', '9876543211', '$2a$10$E2S.02tD12N3wPzF.bYIauXwT61/2kQJ6bW1r05MvM3P.qT4mO82y', 2, 'ACTIVE')
ON DUPLICATE KEY UPDATE `email` = VALUES(`email`);

INSERT INTO `employees` (`employee_id`, `user_id`, `employee_code`, `first_name`, `last_name`, `email`, `phone`, `designation`, `department`, `branch_id`) VALUES
(1, 2, 'EMP001', 'Rajesh', 'Kumar', 'emp1@skbank.com', '9876543211', 'Senior Loan Officer', 'KYC & Loans', 1)
ON DUPLICATE KEY UPDATE `first_name` = VALUES(`first_name`);
