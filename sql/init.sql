-- (Re-)Create Tidy Duck tables

-- drop many-to-many tables
DROP TABLE IF EXISTS versions_function_catalogs;
DROP TABLE IF EXISTS function_catalogs_function_blocks;
-- drop data tables in reverse hierarchical order
DROP TABLE IF EXISTS function_blocks;
DROP TABLE IF EXISTS function_catalogs;
DROP TABLE IF EXISTS versions;
DROP TABLE IF EXISTS accounts;
DROP TABLE IF EXISTS authors;
DROP TABLE IF EXISTS companies;

CREATE TABLE companies (
    id int unsigned NOT NULL PRIMARY KEY auto_increment,
    name varchar(255) NOT NULL
) ENGINE=INNODB;

CREATE TABLE accounts (
    id int unsigned NOT NULL PRIMARY KEY auto_increment,
    username varchar(255) NOT NULL,
    password varchar(255) NOT NULL,
    name varchar(255) NOT NULL,
    company_id int unsigned NOT NULL,
    FOREIGN KEY (company_id) REFERENCES companies (id)
) ENGINE=INNODB;

CREATE TABLE function_catalogs (
    id int unsigned NOT NULL PRIMARY KEY auto_increment,
    name varchar(255) NOT NULL,
    release_version varchar(255) NOT NULL,
    release_date date NOT NULL,
    account_id int unsigned NOT NULL,
    company_id int unsigned NOT NULL,
    is_committed boolean NOT NULL DEFAULT FALSE,
    FOREIGN KEY (account_id) REFERENCES accounts (id),
    FOREIGN KEY (company_id) REFERENCES companies (id)
) ENGINE=INNODB;

CREATE TABLE versions (
    id int unsigned NOT NULL PRIMARY KEY auto_increment,
    name varchar(255) NOT NULL,
    is_committed boolean NOT NULL DEFAULT FALSE,
    owner_id int unsigned NOT NULL,
    FOREIGN KEY (owner_id) REFERENCES accounts (id)
) ENGINE=INNODB;

CREATE TABLE versions_function_catalogs (
    id int unsigned NOT NULL PRIMARY KEY auto_increment,
    version_id int unsigned NOT NULL,
    function_catalog_id int unsigned NOT NULL,
    FOREIGN KEY (version_id) REFERENCES versions (id),
    FOREIGN KEY (function_catalog_id) REFERENCES function_catalogs (id)
) ENGINE=INNODB;

CREATE TABLE function_blocks (
    id int unsigned NOT NULL PRIMARY KEY auto_increment,
    most_id varchar(255) NOT NULL,
    kind varchar(255) NOT NULL,
    name varchar(255) NOT NULL,
    description varchar(255) NOT NULL,
    last_modified_date date NOT NULL,
    release_version varchar(255) NOT NULL,
    account_id int unsigned NOT NULL,
    company_id int unsigned NOT NULL,
    access varchar(255) NOT NULL,
    is_committed boolean NOT NULL DEFAULT FALSE,
    FOREIGN KEY (account_id) REFERENCES accounts (id),
    FOREIGN KEY (company_id) REFERENCES companies (id)
) ENGINE=INNODB;

CREATE TABLE function_catalogs_function_blocks (
    id int unsigned NOT NULL PRIMARY KEY auto_increment,
    function_catalog_id int unsigned NOT NULL,
    function_block_id int unsigned NOT NULL,
    FOREIGN KEY (function_catalog_id) REFERENCES function_catalogs (id),
    FOREIGN KEY (function_block_id) REFERENCES function_blocks (id)
) ENGINE=INNODB;

