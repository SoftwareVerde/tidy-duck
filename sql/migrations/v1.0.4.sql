
ALTER TABLE functions_operations ADD COLUMN channel VARCHAR(255) NOT NULL DEFAULT 'Control' AFTER operation_id;

