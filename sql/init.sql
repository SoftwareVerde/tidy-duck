-- CREATE Tidy Duck tables

-- drop many-to-many tables
DROP TABLE IF EXISTS versions_function_catalogs;
-- drop tables in reverse hierarchical order
DROP TABLE IF EXISTS versions;
DROP TABLE IF EXISTS accounts;
DROP TABLE IF EXISTS function_catalogs;
DROP TABLE IF EXISTS authors;
DROP TABLE IF EXISTS companies;

CREATE TABLE companies (
    id int unsigned NOT NULL PRIMARY KEY auto_increment,
    name varchar(255) NOT NULL
) ENGINE=INNODB CHARACTER SET UTF8;

CREATE TABLE authors (
    id int unsigned NOT NULL PRIMARY KEY auto_increment,
    name varchar(255) NOT NULL,
    company_id int unsigned NOT NULL,
    FOREIGN KEY (company_id) REFERENCES companies (id)
) ENGINE=INNODB CHARACTER SET UTF8;

CREATE TABLE function_catalogs (
    id int unsigned NOT NULL PRIMARY KEY auto_increment,
    name varchar(255) NOT NULL,
    release_version varchar(255) NOT NULL,
    release_date date NOT NULL,
    author_id int unsigned NOT NULL,
    company_id int unsigned NOT NULL,
    FOREIGN KEY (author_id) REFERENCES authors (id),
    FOREIGN KEY (company_id) REFERENCES companies (id)
) ENGINE=INNODB CHARACTER SET UTF8;

CREATE TABLE accounts (
    id int unsigned NOT NULL PRIMARY KEY auto_increment,
    name varchar(255) NOT NULL
) ENGINE=INNODB CHARACTER SET UTF8;

CREATE TABLE versions (
    id int unsigned NOT NULL PRIMARY KEY auto_increment,
    name varchar(255) NOT NULL,
    is_committed boolean NOT NULL DEFAULT FALSE,
    owner_id int unsigned NOT NULL,
    FOREIGN KEY (owner_id) REFERENCES accounts (id)
) ENGINE=INNODB CHARACTER SET UTF8;

CREATE TABLE versions_function_catalogs (
    id int unsigned NOT NULL PRIMARY KEY auto_increment,
    version_id int unsigned NOT NULL,
    function_catalog_id int unsigned NOT NULL,
    is_committed boolean NOT NULL DEFAULT FALSE,
    FOREIGN KEY (version_id) REFERENCES versions (id),
    FOREIGN KEY (function_catalog_id) REFERENCES function_catalogs (id)
) ENGINE=INNODB CHARACTER SET UTF8;

