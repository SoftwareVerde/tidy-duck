ALTER TABLE accounts ADD COLUMN requires_password_reset BOOLEAN NOT NULL DEFAULT 0;
ALTER TABLE accounts ADD COLUMN two_factor_secret BLOB NULL;

ALTER TABLE function_parameters DROP COLUMN name;
ALTER TABLE function_parameters DROP COLUMN description;
