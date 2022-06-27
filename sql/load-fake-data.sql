INSERT INTO companies (name) VALUES ('Software Verde, LLC');

INSERT INTO function_catalogs (name, release_version, account_id, company_id, base_version_id)
VALUES ('Test Function Catalog', '1.1', 1, 1, 1);

INSERT INTO function_blocks (most_id, kind, name, description, access, last_modified_date, release_version, account_id, company_id, base_version_id)
VALUES ('0x01', 'Proprietary', 'Test Function Block', 'This is a test fblock.', 'public', '2017-06-06', '1.1', 1, 1, 1);

INSERT INTO function_catalogs_function_blocks (function_catalog_id, function_block_id)
VALUES (1, 1);

INSERT INTO interfaces (most_id, name, description, last_modified_date, version, base_version_id)
VALUES ('1', 'Test Interface', 'This is a test interface.', '2017-06-06', '1', 1);

INSERT INTO function_blocks_interfaces (function_block_id, interface_id)
VALUES (1, 1);

INSERT INTO most_types (name, primitive_type_id, is_primary_type, string_max_size)
VALUES ('String', 12, 1, '256');

INSERT INTO functions (most_id, name, function_stereotype_id, category, description, release_version, account_id, company_id, return_type_id, supports_notification)
VALUES (1, 'Test Function', 2, 'Property', 'Description', '1.1', 1, 1, 1, 0);

INSERT INTO functions_operations (function_id, operation_id)
VALUES (1, 1), (1, 5), (1, 6);

INSERT INTO interfaces_functions (interface_id, function_id)
VALUES (1, 1);

