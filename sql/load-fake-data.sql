
INSERT INTO companies (name) VALUES ('Software Verde, LLC');

INSERT INTO accounts (name, company_id) VALUES ('Josh Green', 1);

INSERT INTO versions (name, owner_id)
VALUES ('Version 1', 1);

INSERT INTO function_catalogs (name, release_version, release_date, author_id, company_id)
VALUES ('Test Function Catalog', '1.1', '2017-06-06', 1, 1);

INSERT INTO versions_function_catalogs (version_id, function_catalog_id)
VALUES (1, 1);

INSERT INTO function_blocks (most_id, kind, name, description, last_modified_date, release_version, author_id, company_id)
VALUES ('0x01', 'Proprietary', 'Test Function Block', 'This is a test fblock.', '2017-06-06', '1.1', 1, 1);

INSERT INTO function_catalogs_function_blocks (function_catalog_id, function_block_id)
VALUES (1, 1);

