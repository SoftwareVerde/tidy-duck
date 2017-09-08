
ALTER TABLE function_parameters ADD COLUMN parameter_name VARCHAR(255) NOT NULL AFTER function_id;
ALTER TABLE function_parameters ADD COLUMN parameter_description TEXT NULL AFTER parameter_name;

