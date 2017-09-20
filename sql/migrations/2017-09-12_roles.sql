
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

CREATE TABLE roles_permissions (
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
    FOREIGN KEY fk_roles_permissions_role_id (role_id) REFERENCES roles (id)
);

INSERT INTO roles_permissions
VALUES
        (1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        (2, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1),
        (3, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1),
        (4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1),
        (5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 1);

CREATE TABLE accounts_roles (
    account_id INT UNSIGNED NOT NULL,
    role_id INT UNSIGNED NOT NULL,
    FOREIGN KEY fk_accounts_roles_account_id (account_id) REFERENCES accounts (id),
    FOREIGN KEY fk_accounts_roles_role_id (role_id) REFERENCES roles(id)
) ENGINE=INNODB;

-- provide all existing accounts all roles
INSERT INTO accounts_roles SELECT accounts.id, roles.id FROM accounts, roles;

