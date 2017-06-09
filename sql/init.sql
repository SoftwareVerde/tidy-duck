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
);
INSERT INTO companies (name) VALUES ('Software Verde, LLC');
INSERT INTO companies (name) VALUES ('Honda R+D');

CREATE TABLE authors (
    id int unsigned NOT NULL PRIMARY KEY auto_increment,
    name varchar(255) NOT NULL,
    company_id integer NOT NULL REFERENCES companies (id)
);

CREATE TABLE function_catalogs (
    id int unsigned NOT NULL PRIMARY KEY auto_increment,
    name varchar(255) NOT NULL,
    release_version varchar(255) NOT NULL,
    release_date date NOT NULL,
    author_id int unsigned NOT NULL REFERENCES authors (id),
    company_id int unsigned NOT NULL REFERENCES companies (id)
);

CREATE TABLE accounts (
    id int unsigned NOT NULL PRIMARY KEY auto_increment,
    name varchar(255) NOT NULL
);

CREATE TABLE versions (
    id int unsigned NOT NULL PRIMARY KEY auto_increment,
    name varchar(255) NOT NULL,
    is_committed boolean NOT NULL DEFAULT FALSE,
    owner_id int unsigned NOT NULL REFERENCES accounts (id)
);

CREATE TABLE versions_function_catalogs (
    id int unsigned NOT NULL PRIMARY KEY auto_increment,
    version_id int unsigned NOT NULL REFERENCES versions (id),
    function_catalog_id int unsigned NOT NULL REFERENCES function_catalogs (id),
    is_committed boolean NOT NULL DEFAULT FALSE
);

