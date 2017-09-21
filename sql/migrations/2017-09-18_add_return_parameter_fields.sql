
ALTER TABLE functions ADD COLUMN return_parameter_name VARCHAR(255) NOT NULL AFTER company_id;
ALTER TABLE functions ADD COLUMN return_parameter_description TEXT NULL AFTER return_parameter_name;

