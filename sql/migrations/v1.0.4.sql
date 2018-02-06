
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
