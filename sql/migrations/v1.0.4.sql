
-- Add operation channel column to operations table
ALTER TABLE functions_operations ADD COLUMN channel VARCHAR(255) NOT NULL DEFAULT 'Control' AFTER operation_id;

-- Recreate role permissions with unique constraint to avoid duplicates getting added
TRUNCATE TABLE role_permissions;

ALTER TABLE role_permissions ADD UNIQUE (role_id);

INSERT INTO role_permissions (role_id, login, admin_create_users, admin_modify_users, admin_delete_users, admin_reset_password, most_components_release, most_components_create, most_components_modify, most_components_view, types_create, types_modify, reviews_approval, reviews_comments, reviews_voting, reviews_view)
VALUES
        (1, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        (2, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1),
        (3, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1),
        (4, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 1, 1),
        (5, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 1),
        (6, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

-- Add is_deleted column to accounts table
ALTER TABLE accounts ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE AFTER company_id;

-- Add creator_account_id column to function_catalogs, function_blocks, and interfaces
ALTER TABLE function_catalogs ADD COLUMN creator_account_id INT UNSIGNED NULL AFTER prior_version_id;
ALTER TABLE function_blocks ADD COLUMN creator_account_id INT UNSIGNED NULL AFTER prior_version_id;
ALTER TABLE interfaces ADD COLUMN creator_account_id INT UNSIGNED NULL AFTER prior_version_id;

-- Set foreign key constraints for creator_account_id columns. Constrain them to the id column of the accounts table
ALTER TABLE function_catalogs ADD FOREIGN KEY (creator_account_id) REFERENCES accounts (id);
ALTER TABLE function_blocks ADD FOREIGN KEY (creator_account_id) REFERENCES accounts (id);
ALTER TABLE interfaces ADD FOREIGN KEY (creator_account_id) REFERENCES accounts (id);

-- Add columns for deleting MOST components (Trash Bin)
ALTER TABLE function_catalogs ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE AFTER company_id;
ALTER TABLE function_catalogs ADD COLUMN deleted_date DATETIME NULL AFTER is_deleted;
ALTER TABLE function_catalogs_function_blocks ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE AFTER id;

ALTER TABLE function_blocks ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE AFTER access;
ALTER TABLE function_blocks ADD COLUMN deleted_date DATETIME NULL AFTER is_deleted;
ALTER TABLE function_blocks_interfaces ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE AFTER id;

ALTER TABLE interfaces ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE AFTER version;
ALTER TABLE interfaces ADD COLUMN deleted_date DATETIME NULL AFTER is_deleted;

ALTER TABLE interfaces_functions ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE AFTER id;
ALTER TABLE functions ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE AFTER supports_notification;
ALTER TABLE functions ADD COLUMN deleted_date DATETIME NULL AFTER is_deleted;

-- Add approval_review_id column to function_catalogs, function_blocks, interfaces, and functions

ALTER TABLE function_catalogs ADD COLUMN approval_review_id INT UNSIGNED NULL AFTER is_approved;
ALTER TABLE function_blocks ADD COLUMN approval_review_id INT UNSIGNED NULL AFTER is_approved;
ALTER TABLE interfaces ADD COLUMN approval_review_id INT UNSIGNED NULL AFTER is_approved;
ALTER TABLE functions ADD COLUMN approval_review_id INT UNSIGNED NULL AFTER is_approved;

-- Add approval_date column to reviews

ALTER TABLE reviews ADD COLUMN approval_date DATETIME NULL AFTER created_date;

-- Convert Interface most_id entries to hex string with 4 nibbles.
UPDATE interfaces SET most_id = CONCAT('0x', LPAD(CONV(most_id, 10, 16), 8, '0'));

-- Add permanently deleted columns
ALTER TABLE function_catalogs ADD COLUMN is_permanently_deleted BOOLEAN NOT NULL DEFAULT FALSE AFTER deleted_date;
ALTER TABLE function_catalogs ADD COLUMN permanently_deleted_date DATETIME NULL AFTER is_permanently_deleted;

ALTER TABLE function_blocks ADD COLUMN is_permanently_deleted BOOLEAN NOT NULL DEFAULT FALSE AFTER deleted_date;
ALTER TABLE function_blocks ADD COLUMN permanently_deleted_date DATETIME NULL AFTER is_permanently_deleted;

ALTER TABLE interfaces ADD COLUMN is_permanently_deleted BOOLEAN NOT NULL DEFAULT FALSE AFTER deleted_date;
ALTER TABLE interfaces ADD COLUMN permanently_deleted_date DATETIME NULL AFTER is_permanently_deleted;

ALTER TABLE functions ADD COLUMN is_permanently_deleted BOOLEAN NOT NULL DEFAULT FALSE AFTER deleted_date;
ALTER TABLE functions ADD COLUMN permanently_deleted_date DATETIME NULL AFTER is_permanently_deleted;

-- Set approval review ID for historical components (if review exists for that component, use it)
UPDATE function_catalogs
INNER JOIN reviews ON reviews.function_catalog_id = function_catalogs.id
SET approval_review_id = reviews.id;

UPDATE function_blocks
INNER JOIN reviews ON reviews.function_block_id = function_blocks.id
SET approval_review_id = reviews.id;

UPDATE interfaces
INNER JOIN reviews ON reviews.interface_id = interfaces.id
SET approval_review_id = reviews.id;

UPDATE functions
INNER JOIN reviews ON reviews.function_id = functions.id
SET approval_review_id = reviews.id;

-- Set approval review ID for historical components (get all parent objects and find the oldest review)
UPDATE function_blocks
INNER JOIN (
        SELECT function_blocks.id AS function_block_id, reviews.id AS review_id
        FROM function_blocks
        LEFT OUTER JOIN function_catalogs_function_blocks ON function_catalogs_function_blocks.function_block_id = function_blocks.id
        INNER JOIN reviews ON reviews.function_catalog_id = function_catalogs_function_blocks.function_catalog_id
        GROUP BY function_blocks.id
        HAVING MIN(reviews.created_date)
     ) A
ON A.function_block_id = function_blocks.id AND function_blocks.approval_review_id is NULL
SET approval_review_id = A.review_id;

UPDATE interfaces
INNER JOIN (
        SELECT interfaces.id AS interface_id, reviews.id AS review_id
        FROM interfaces
        LEFT OUTER JOIN function_blocks_interfaces ON function_blocks_interfaces.interface_id = interfaces.id
        LEFT OUTER JOIN function_catalogs_function_blocks ON function_catalogs_function_blocks.function_block_id = function_blocks_interfaces.function_block_id
        INNER JOIN reviews ON reviews.function_block_id = function_blocks_interfaces.function_block_id OR reviews.function_catalog_id = function_catalogs_function_blocks.function_catalog_id
        GROUP BY interfaces.id
        HAVING MIN(reviews.created_date)
     ) A
ON A.interface_id = interfaces.id AND interfaces.approval_review_id is NULL
SET approval_review_id = A.review_id;

UPDATE functions
INNER JOIN (
        SELECT functions.id AS function_id, reviews.id AS review_id
        FROM functions
        LEFT OUTER JOIN interfaces_functions ON interfaces_functions.function_id = functions.id
        LEFT OUTER JOIN function_blocks_interfaces ON function_blocks_interfaces.interface_id = interfaces_functions.interface_id
        LEFT OUTER JOIN function_catalogs_function_blocks ON function_catalogs_function_blocks.function_block_id = function_blocks_interfaces.function_block_id
        INNER JOIN reviews ON reviews.function_block_id = function_blocks_interfaces.function_block_id OR reviews.function_catalog_id = function_catalogs_function_blocks.function_catalog_id
        GROUP BY functions.id
        HAVING MIN(reviews.created_date)
     ) A
ON A.function_id = functions.id AND functions.approval_review_id is NULL
SET approval_review_id = A.review_id;

-- Global setting for review approval upvote minimum
DROP TABLE IF EXISTS application_settings;

CREATE TABLE application_settings (
    name VARCHAR(255) NOT NULL,
    value VARCHAR(255) NULL,
    CONSTRAINT uc_application_settings_name UNIQUE (name)
) ENGINE=INNODB;

INSERT INTO application_settings (name, value)
VALUES ('REVIEW_APPROVAL_MINIMUM_UPVOTES', '3');
