
ALTER TABLE accounts ADD COLUMN login_permission BOOLEAN NOT NULL DEFAULT TRUE AFTER company_id;

