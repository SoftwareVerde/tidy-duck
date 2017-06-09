
INSERT INTO companies (name) VALUES ('Software Verde, LLC');

INSERT INTO authors (name, company_id) VALUES ('Josh Green', 1);

INSERT INTO accounts (name, company_id) VALUES ('Josh Green', 1);

INSERT INTO function_catalogs (name, release_version, release_date, author_id, company_id)
VALUES ('Test 1', '1.0.0', NOW(), 1, 1);

INSERT INTO versions (name, owner_id)
VALUES ('Version 1', 1);

INSERT INTO versions_function_catalogs (version_id, function_catalog_id)
VALUES (1, 1);

