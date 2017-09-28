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
