
INSERT INTO companies (name) VALUES ('Software Verde, LLC');

INSERT INTO accounts (username, name, password, company_id) VALUES ('josh@softwareverde.com', 'Josh Green', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', 1);
INSERT INTO accounts (username, name, password, company_id) VALUES ('groot@softwareverde.com', 'Andrew Groot', '2628baba2a77860376d77bbcba431f4dbc22517bdcd82daf0c90341d78d6ca4f', 1);
INSERT INTO accounts (username, name, password, company_id) VALUES ('test@softwareverde.com', 'Test User', '2628baba2a77860376d77bbcba431f4dbc22517bdcd82daf0c90341d78d6ca4f', 1);

INSERT INTO versions (name, owner_id)
VALUES ('Version 1', 1);

INSERT INTO function_catalogs (name, release_version, account_id, company_id)
VALUES ('Test Function Catalog', '1.1', 1, 1);

INSERT INTO versions_function_catalogs (version_id, function_catalog_id)
VALUES (1, 1);

INSERT INTO function_blocks (most_id, kind, name, description, last_modified_date, release_version, account_id, company_id)
VALUES ('0x01', 'Proprietary', 'Test Function Block', 'This is a test fblock.', '2017-06-06', '1.1', 1, 1);

INSERT INTO function_catalogs_function_blocks (function_catalog_id, function_block_id)
VALUES (1, 1);

INSERT INTO interfaces (most_id, name, description, last_modified_date, version)
VALUES ('1', 'Test Interface', 'This is a test interface.', '2017-06-06', '1');

INSERT INTO function_blocks_interfaces (function_block_id, interface_id)
VALUES (1, 1);

INSERT INTO functions (most_id, name, function_stereotype_id, category, description, release_version, account_id, company_id, return_type_id)
VALUES (1, 'Test Function', 2, 'Property', 'Description', '1.1', 1, 1, 1);

INSERT INTO functions_operations (function_id, operation_id)
VALUES
        (1, 1),
        (1, 5),
        (1, 6);

INSERT INTO interfaces_functions (interface_id, function_id)
VALUES (1, 1);

