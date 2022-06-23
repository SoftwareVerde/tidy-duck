
ALTER TABLE enum_values ADD COLUMN description VARCHAR(255);


ALTER TABLE reviews ADD COLUMN ticket_url VARCHAR(255) NULL AFTER account_id;


ALTER TABLE function_parameters ADD COLUMN parameter_name VARCHAR(255) NOT NULL AFTER function_id;
ALTER TABLE function_parameters ADD COLUMN parameter_description TEXT NULL AFTER parameter_name;


CREATE TABLE roles (
    id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL
) ENGINE=INNODB;

INSERT INTO roles
VALUES
        (1, 'Admin'),
        (2, 'Release'),
        (3, 'Modify'),
        (4, 'Review'),
        (5, 'View');

CREATE TABLE role_permissions (
    role_id INT UNSIGNED NOT NULL,
    admin_create_users BOOLEAN NOT NULL DEFAULT FALSE,
    admin_modify_users BOOLEAN NOT NULL DEFAULT FALSE,
    admin_delete_users BOOLEAN NOT NULL DEFAULT FALSE,
    admin_reset_password BOOLEAN NOT NULL DEFAULT FALSE,
    most_components_release BOOLEAN NOT NULL DEFAULT FALSE,
    most_components_create BOOLEAN NOT NULL DEFAULT FALSE,
    most_components_modify BOOLEAN NOT NULL DEFAULT FALSE,
    most_components_view BOOLEAN NOT NULL DEFAULT FALSE,
    types_create BOOLEAN NOT NULL DEFAULT FALSE,
    types_modify BOOLEAN NOT NULL DEFAULT FALSE,
    reviews_approval BOOLEAN NOT NULL DEFAULT FALSE,
    reviews_comments BOOLEAN NOT NULL DEFAULT FALSE,
    reviews_voting BOOLEAN NOT NULL DEFAULT FALSE,
    reviews_view BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (role_id) REFERENCES roles (id)
) ENGINE=INNODB;

INSERT INTO role_permissions
VALUES
        (1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        (2, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1),
        (3, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1),
        (4, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 1, 1),
        (5, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 1);

CREATE TABLE accounts_roles (
    account_id INT UNSIGNED NOT NULL,
    role_id INT UNSIGNED NOT NULL,
    FOREIGN KEY (account_id) REFERENCES accounts (id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
) ENGINE=INNODB;

-- provide all existing accounts all roles
INSERT INTO accounts_roles (account_id, role_id)
SELECT accounts.id, roles.id
FROM accounts, roles;


ALTER TABLE functions ADD COLUMN return_parameter_name VARCHAR(255) NOT NULL AFTER company_id;
ALTER TABLE functions ADD COLUMN return_parameter_description TEXT NULL AFTER return_parameter_name;

-- add login permission column to role permissions
ALTER TABLE role_permissions ADD COLUMN login BOOLEAN NOT NULL DEFAULT FALSE AFTER role_id;

-- add login role
INSERT INTO roles VALUES (6, 'Login');

-- set login role permissions
INSERT INTO role_permissions
VALUES
        (6, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

-- provide all existing accounts all roles
INSERT INTO accounts_roles (account_id, role_id)
SELECT accounts.id, 6
FROM accounts;

ALTER TABLE most_types ADD COLUMN is_released BOOLEAN NOT NULL DEFAULT FALSE AFTER is_primary_type;


-- set all passwords to 'quack quack' (new hashing algorithm)
UPDATE accounts SET password = 'Argon2$2$19$12$16$2$F3EE5AB0A855880CDFCC44576D8F43F8$32$B534941D2FBBEA6EE6C460B7D10E5F8592BD39F23497C9ADAE6ED35BAF4BACA7';

ALTER TABLE accounts ADD COLUMN default_mode VARCHAR(255) NOT NULL DEFAULT 'Release' AFTER theme;
ALTER TABLE function_blocks CHANGE last_modified_date last_modified_date DATETIME NOT NULL;
ALTER TABLE interfaces CHANGE last_modified_date last_modified_date DATETIME NOT NULL;

