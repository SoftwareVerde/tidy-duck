-- (Re-)Create Tidy Duck tables

-- drop many-to-many tables
DROP TABLE IF EXISTS versions_function_catalogs;
DROP TABLE IF EXISTS function_catalogs_function_blocks;
DROP TABLE IF EXISTS function_blocks_interfaces;
DROP TABLE IF EXISTS interfaces_functions;
DROP TABLE IF EXISTS functions_operations;
-- drop data tables in reverse hierarchical order
DROP TABLE IF EXISTS function_stereotypes_operations;
DROP TABLE IF EXISTS operations;
DROP TABLE IF EXISTS function_parameters;
DROP TABLE IF EXISTS functions;
DROP TABLE IF EXISTS function_stereotypes;
DROP TABLE IF EXISTS function_categories;
DROP TABLE IF EXISTS most_types;
DROP TABLE IF EXISTS interfaces;
DROP TABLE IF EXISTS function_blocks;
DROP TABLE IF EXISTS function_catalogs;
DROP TABLE IF EXISTS versions;
DROP TABLE IF EXISTS accounts;
DROP TABLE IF EXISTS companies;

CREATE TABLE companies (
    id int unsigned NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name varchar(255) NOT NULL
) ENGINE=INNODB;

CREATE TABLE accounts (
    id int unsigned NOT NULL PRIMARY KEY AUTO_INCREMENT,
    username varchar(255) NOT NULL,
    password varchar(255) NOT NULL,
    name varchar(255) NOT NULL,
    theme varchar(255) NOT NULL DEFAULT 'Tidy',
    company_id int unsigned NOT NULL,
    FOREIGN KEY (company_id) REFERENCES companies (id)
) ENGINE=INNODB;

CREATE TABLE function_catalogs (
    id int unsigned NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name varchar(255) NOT NULL,
    release_version varchar(255) NOT NULL,
    account_id int unsigned NOT NULL,
    company_id int unsigned NOT NULL,
    is_released boolean NOT NULL DEFAULT FALSE,
    base_version_id int unsigned NULL,
    prior_version_id int unsigned NULL,
    FOREIGN KEY (account_id) REFERENCES accounts (id),
    FOREIGN KEY (company_id) REFERENCES companies (id),
    FOREIGN KEY (base_version_id) REFERENCES function_catalogs (id),
    FOREIGN KEY (prior_version_id) REFERENCES function_catalogs (id)
) ENGINE=INNODB;

CREATE TABLE function_blocks (
    id int unsigned NOT NULL PRIMARY KEY AUTO_INCREMENT,
    most_id varchar(255) NOT NULL,
    kind varchar(255) NOT NULL,
    name varchar(255) NOT NULL,
    description text NOT NULL,
    last_modified_date date NOT NULL,
    release_version varchar(255) NOT NULL,
    account_id int unsigned NOT NULL,
    company_id int unsigned NOT NULL,
    access varchar(255) NOT NULL,
    is_released boolean NOT NULL DEFAULT FALSE,
    base_version_id int unsigned NULL,
    prior_version_id int unsigned NULL,
    FOREIGN KEY (account_id) REFERENCES accounts (id),
    FOREIGN KEY (company_id) REFERENCES companies (id),
    FOREIGN KEY (base_version_id) REFERENCES function_blocks (id),
    FOREIGN KEY (prior_version_id) REFERENCES function_blocks (id)
) ENGINE=INNODB;

CREATE TABLE function_catalogs_function_blocks (
    id int unsigned NOT NULL PRIMARY KEY AUTO_INCREMENT,
    function_catalog_id int unsigned NOT NULL,
    function_block_id int unsigned NOT NULL,
    FOREIGN KEY (function_catalog_id) REFERENCES function_catalogs (id),
    FOREIGN KEY (function_block_id) REFERENCES function_blocks (id)
) ENGINE=INNODB;

CREATE TABLE interfaces (
    id int unsigned NOT NULL PRIMARY KEY AUTO_INCREMENT,
    most_id varchar(255) NOT NULL,
    name varchar(255) NOT NULL,
    description text NOT NULL,
    last_modified_date date NOT NULL,
    version varchar(255) NOT NULL,
    is_released boolean NOT NULL DEFAULT FALSE,
    base_version_id int unsigned NULL,
    prior_version_id int unsigned NULL,
    FOREIGN KEY (base_version_id) REFERENCES interfaces (id),
    FOREIGN KEY (prior_version_id) REFERENCES interfaces (id)
) ENGINE=INNODB;

CREATE TABLE function_blocks_interfaces (
    id int unsigned NOT NULL PRIMARY KEY AUTO_INCREMENT,
    function_block_id int unsigned NOT NULL,
    interface_id int unsigned NOT NULL,
    FOREIGN KEY (function_block_id) REFERENCES function_blocks (id),
    FOREIGN KEY (interface_id) REFERENCES interfaces (id)
) ENGINE=INNODB;

CREATE TABLE most_types (
    id int unsigned NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name varchar(255) NOT NULL,
    function_catalog_id int unsigned NULL DEFAULT NULL,
    FOREIGN KEY (function_catalog_id) REFERENCES function_catalogs (id)
) ENGINE=INNODB;

INSERT INTO most_types (name)
VALUES
        ('TBool'),
        ('TBitField'),
        ('TEnum'),
        ('TNumber'),
        ('TVoid'),
        ('TUByte'),
        ('TSByte'),
        ('TUWord'),
        ('TSWord'),
        ('TULong'),
        ('TSLong'),
        ('TString'),
        ('TStream'),
        ('TCStream'),
        ('TShortStream'),
        ('TArray'),
        ('TRecord');

CREATE TABLE function_categories (
    id int unsigned NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name varchar(255) NOT NULL UNIQUE
) ENGINE=INNODB;

INSERT INTO function_categories (name)
VALUES
        ('Property'),
        ('Method');

CREATE TABLE function_stereotypes (
    id int unsigned NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name varchar(255) NOT NULL,
    supports_notification boolean NOT NULL,
    category varchar(255) NOT NULL,
    FOREIGN KEY (category) REFERENCES function_categories (name)
) ENGINE=INNODB;

INSERT INTO function_stereotypes (id, name, supports_notification, category)
VALUES
        (1, 'Event',                       1, 'Property'),
        (2, 'ReadOnlyProperty',            0, 'Property'),
        (3, 'ReadOnlyPropertyWithEvent',   1, 'Property'),
        (4, 'PropertyWithEvent',           1, 'Property'),
        (5, 'CommandWithAck',              0, 'Method'),
        (6, 'Request/Response',            0, 'Method');

CREATE TABLE functions (
    id int unsigned NOT NULL PRIMARY KEY AUTO_INCREMENT,
    most_id varchar(255) NOT NULL,
    name varchar(255) NOT NULL,
    function_stereotype_id int unsigned NOT NULL,
    category varchar(255) NOT NULL,
    description text NOT NULL,
    release_version varchar(255) NOT NULL,
    account_id int unsigned NOT NULL,
    company_id int unsigned NOT NULL,
    return_type_id int unsigned NOT NULL,
    supports_notification boolean NOT NULL,
    is_released boolean NOT NULL DEFAULT FALSE,
    prior_version_id int unsigned NULL,
    FOREIGN KEY (function_stereotype_id) REFERENCES function_stereotypes (id),
    FOREIGN KEY (category) REFERENCES function_categories (name),
    FOREIGN KEY (account_id) REFERENCES accounts (id),
    FOREIGN KEY (company_id) REFERENCES companies (id),
    FOREIGN KEY (return_type_id) REFERENCES most_types (id),
    FOREIGN KEY (prior_version_id) REFERENCES functions (id)
) ENGINE=INNODB;

CREATE TABLE interfaces_functions (
    id int unsigned NOT NULL PRIMARY KEY AUTO_INCREMENT,
    interface_id int unsigned NOT NULL,
    function_id int unsigned NOT NULL,
    FOREIGN KEY (interface_id) REFERENCES interfaces (id),
    FOREIGN KEY (function_id) REFERENCES functions (id)
) ENGINE=INNODB;

CREATE TABLE function_parameters (
    id int unsigned NOT NULL PRIMARY KEY AUTO_INCREMENT,
    function_id int unsigned NOT NULL,
    parameter_index int unsigned NOT NULL,
    most_type_id int unsigned NOT NULL,
    FOREIGN KEY (function_id) REFERENCES functions (id),
    FOREIGN KEY (most_type_id) REFERENCES most_types (id)
) ENGINE=INNODB;

CREATE TABLE operations (
    id int unsigned NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name varchar(255) NOT NULL,
    opcode varchar(255) NOT NULL UNIQUE,
    is_input boolean NOT NULL
) ENGINE=INNODB;

INSERT INTO operations (id, name, opcode, is_input)
VALUES
        (1, 'Get',              '0x1', 1),
        (2, 'Set',              '0x0', 1),
        (3, 'StartResultAck',   '0x6', 1),
        (4, 'AbortAck',         '0x7', 1),
        (5, 'Status',           '0xC', 0),
        (6, 'Error',            '0xF', 0),
        (7, 'ResultAck',        '0xD', 0),
        (8, 'ProcessingAck',    '0xA', 0),
        (9, 'ErrorAck',         '0x9', 0);

CREATE TABLE function_stereotypes_operations (
    id int unsigned NOT NULL PRIMARY KEY AUTO_INCREMENT,
    function_stereotype_id int unsigned NOT NULL,
    operation_id int unsigned NOT NULL,
    FOREIGN KEY (function_stereotype_id) REFERENCES function_stereotypes (id),
    FOREIGN KEY (operation_id) REFERENCES operations (id)
) ENGINE=INNODB;

INSERT INTO function_stereotypes_operations (function_stereotype_id, operation_id)
VALUES
        -- Event (none)
        (1, 5),
        (1, 6),
        -- ReadOnlyProperty
        (2, 1),
        (2, 5),
        (2, 6),
        -- ReadOnlyPropertyWithEvent
        (3, 1),
        (3, 5),
        (3, 6),
        -- PropertyWithEvent
        (4, 1),
        (4, 2),
        (4, 5),
        (4, 6),
        -- CommandWithAck
        (5, 3),
        (5, 7),
        (5, 8),
        (5, 9),
        -- Request/Response
        (6, 3),
        (6, 4),
        (6, 7),
        (6, 8),
        (6, 9);

CREATE TABLE functions_operations (
    id int unsigned NOT NULL PRIMARY KEY AUTO_INCREMENT,
    function_id int unsigned NOT NULL,
    operation_id int unsigned NOT NULL,
    FOREIGN KEY (function_id) REFERENCES functions (id),
    FOREIGN KEY (operation_id) REFERENCES operations (id)
) ENGINE=INNODB;

