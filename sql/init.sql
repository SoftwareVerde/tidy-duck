-- (Re-)Create Tidy Duck tables

-- drop many-to-many tables
DROP TABLE IF EXISTS versions_function_catalogs;
DROP TABLE IF EXISTS function_catalogs_function_blocks;
DROP TABLE IF EXISTS function_blocks_interfaces;
DROP TABLE IF EXISTS interfaces_functions;
DROP TABLE IF EXISTS functions_operations;
-- drop data tables in reverse hierarchical order
DROP TABLE IF EXISTS function_stereotypes_operations;
DROP TABLE IF EXISTS operations;
DROP TABLE IF EXISTS function_parameters;
DROP TABLE IF EXISTS functions;
DROP TABLE IF EXISTS function_stereotypes;
DROP TABLE IF EXISTS function_categories;
DROP TABLE IF EXISTS bool_fields;
DROP TABLE IF EXISTS enum_values;
DROP TABLE IF EXISTS stream_case_parameters;
DROP TABLE IF EXISTS stream_case_signals;
DROP TABLE IF EXISTS stream_cases;
DROP TABLE IF EXISTS record_fields;
DROP TABLE IF EXISTS most_types;
DROP TABLE IF EXISTS primitive_types;
DROP TABLE IF EXISTS most_units;
DROP TABLE IF EXISTS interfaces;
DROP TABLE IF EXISTS function_blocks;
DROP TABLE IF EXISTS function_catalogs;
DROP TABLE IF EXISTS versions;
DROP TABLE IF EXISTS accounts;
DROP TABLE IF EXISTS companies;

CREATE TABLE companies (
    id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL
) ENGINE=INNODB;

CREATE TABLE accounts (
    id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    theme VARCHAR(255) NOT NULL DEFAULT 'Tidy',
    company_id INT UNSIGNED NOT NULL,
    FOREIGN KEY (company_id) REFERENCES companies (id)
) ENGINE=INNODB;

CREATE TABLE function_catalogs (
    id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    release_version VARCHAR(255) NOT NULL,
    account_id INT UNSIGNED NOT NULL,
    company_id INT UNSIGNED NOT NULL,
    is_released BOOLEAN NOT NULL DEFAULT FALSE,
    base_version_id INT UNSIGNED NULL,
    prior_version_id INT UNSIGNED NULL,
    FOREIGN KEY (account_id) REFERENCES accounts (id),
    FOREIGN KEY (company_id) REFERENCES companies (id),
    FOREIGN KEY (prior_version_id) REFERENCES function_catalogs (id)
) ENGINE=INNODB;

CREATE TABLE function_blocks (
    id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
    most_id VARCHAR(255) NOT NULL,
    kind VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    last_modified_date date NOT NULL,
    release_version VARCHAR(255) NOT NULL,
    account_id INT UNSIGNED NOT NULL,
    company_id INT UNSIGNED NOT NULL,
    access VARCHAR(255) NOT NULL,
    is_released BOOLEAN NOT NULL DEFAULT FALSE,
    base_version_id INT UNSIGNED NULL,
    prior_version_id INT UNSIGNED NULL,
    FOREIGN KEY (account_id) REFERENCES accounts (id),
    FOREIGN KEY (company_id) REFERENCES companies (id),
    FOREIGN KEY (prior_version_id) REFERENCES function_blocks (id)
) ENGINE=INNODB;

CREATE TABLE function_catalogs_function_blocks (
    id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
    function_catalog_id INT UNSIGNED NOT NULL,
    function_block_id INT UNSIGNED NOT NULL,
    FOREIGN KEY (function_catalog_id) REFERENCES function_catalogs (id),
    FOREIGN KEY (function_block_id) REFERENCES function_blocks (id)
) ENGINE=INNODB;

CREATE TABLE interfaces (
    id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
    most_id VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    last_modified_date date NOT NULL,
    version varchar(255) NOT NULL,
    is_released boolean NOT NULL DEFAULT FALSE,
    base_version_id int unsigned NULL,
    prior_version_id int unsigned NULL,
    FOREIGN KEY (prior_version_id) REFERENCES interfaces (id)
) ENGINE=INNODB;

CREATE TABLE function_blocks_interfaces (
    id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
    function_block_id INT UNSIGNED NOT NULL,
    interface_id INT UNSIGNED NOT NULL,
    FOREIGN KEY (function_block_id) REFERENCES function_blocks (id),
    FOREIGN KEY (interface_id) REFERENCES interfaces (id)
) ENGINE=INNODB;

CREATE TABLE most_units (
    id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
    reference_name VARCHAR(255) NOT NULL,
    definition_name VARCHAR(255) NOT NULL,
    definition_code VARCHAR(255) NOT NULL,
    definition_group VARCHAR(255) NOT NULL
) ENGINE=INNODB;

-- Units extracted from XML sample using:
--
-- Search:  <UnitDef UnitID="([^"]*)">\n<UnitDefName>([^<]*)</UnitDefName>\n<UnitDefCode>([^<]*)</UnitDefCode>\n<UnitDefGroup>([^<]*)</UnitDefGroup>\n</UnitDef>
-- Replace: ('\1', '\2', '\3', '\4'),
--
INSERT INTO most_units (reference_name, definition_name, definition_code, definition_group)
VALUES
    ('unit_none', 'none', '0x00', ''),
    ('unit_1_min', '1/min', '0x20', 'Frequency'),
    ('unit_360_degree_2pow32', '360_deg/2pow32', '0xA3', 'Angle'),
    ('unit_360_degree_2pow8', '360_deg/2pow8', '0xA4', 'Angle'),
    ('unit_a', 'a', '0x17', 'Time'),
    ('unit_A', 'A', '0x91', 'Current'),
    ('unit_bar', 'bar', '0x63', 'Temperature and Pressure'),
    ('unit_bit', 'bit', '0xC5', 'Data'),
    ('unit_bps', 'bps', '0xD0', 'Data Rate'),
    ('unit_Byte_s', 'Bps', '0xD3', 'Data Rate'),
    ('unit_Byte', 'Byte', '0xC0', 'Data'),
    ('unit_C', 'C', '0x60', 'Temperature and Pressure'),
    ('unit_ccm', 'ccm', '0x33', 'Volume'),
    ('unit_cm', 'cm', '0x01', 'Distance'),
    ('unit_cm_s', 'cm/s', '0x53', 'Speed and Acceleration'),
    ('unit_d', 'd', '0x15', 'Time'),
    ('unit_dB', 'dB', '0x70', 'Miscellaneous'),
    ('unit_deg_s', 'deg/s', '0x54', 'Speed and Acceleration'),
    ('unit_degrees', 'degrees', '0xA0', 'Angle'),
    ('unit_F', 'F', '0x61', 'Temperature and Pressure'),
    ('unit_gal_UK', 'gal_UK', '0x31', 'Volume'),
    ('unit_gal_US', 'gal_US', '0x32', 'Volume'),
    ('unit_GByte', 'GByte', '0xC3', 'Data'),
    ('unit_h', 'h', '0x14', 'Time'),
    ('unit_Hz', 'Hz', '0x21', 'Frequency'),
    ('unit_K', 'K', '0x62', 'Temperature and Pressure'),
    ('unit_kByte_s', 'kBps', '0xD4', 'Data Rate'),
    ('unit_kbps', 'kbps', '0xD1', 'Data Rate'),
    ('unit_kByte', 'kByte', '0xC1', 'Data'),
    ('unit_kHz', 'kHz', '0x22', 'Frequency'),
    ('unit_km', 'km', '0x03', 'Distance'),
    ('unit_km_h', 'km/h', '0x50', 'Speed and Acceleration'),
    ('unit_km_l', 'km/l', '0x42', 'Consumption'),
    ('unit_l', 'l', '0x30', 'Volume'),
    ('unit_l_100km', 'l/100km', '0x40', 'Consumption'),
    ('unit_m', 'm', '0x02', 'Distance'),
    ('unit_m_s', 'm/s', '0x52', 'Speed and Acceleration'),
    ('unit_m_s_2', 'm/s_2', '0x55', 'Speed and Acceleration'),
    ('unit_mA', 'mA', '0x90', 'Current'),
    ('unit_Mbps', 'Mbps', '0xD2', 'Data Rate'),
    ('unit_MByte_s', 'MBps', '0xD5', 'Data Rate'),
    ('unit_MByte', 'MByte', '0xC2', 'Data'),
    ('unit_MHz', 'MHz', '0x23', 'Frequency'),
    ('unit_min', 'min', '0x13', 'Time'),
    ('unit_minutes', 'minutes', '0xA1', 'Angle'),
    ('unit_mls', 'mls', '0x04', 'Distance'),
    ('unit_mls_gal', 'mls/gal', '0x41', 'Consumption'),
    ('unit_mls_h', 'mls/h', '0x51', 'Speed and Acceleration'),
    ('unit_mon', 'mon', '0x16', 'Time'),
    ('unit_ms', 'ms', '0x11', 'Time'),
    ('unit_mV', 'mV', '0x80', 'Voltage'),
    ('unit_percent', 'percent', '0x71', 'Miscellaneous'),
    ('unit_pixel', 'pixel', '0xB0', 'Resolution'),
    ('unit_psi', 'psi', '0x64', 'Temperature and Pressure'),
    ('unit_s', 's', '0x12', 'Time'),
    ('unit_seconds', 'seconds', '0xA2', 'Angle'),
    ('unit_TByte', 'TByte', '0xC4', 'Data'),
    ('unit_us', 'us', '0x10', 'Time'),
    ('unit_V', 'V', '0x81', 'Voltage');

CREATE TABLE primitive_types (
    id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    is_base_type BOOLEAN NOT NULL,
    is_number_base_type BOOLEAN NOT NULL,
    is_stream_param_type BOOLEAN NOT NULL,
    is_array_type BOOLEAN NOT NULL,
    is_record_type BOOLEAN NOT NULL
) ENGINE=INNODB;

-- Primitive type details manually pulled from DTD
INSERT INTO primitive_types (id, name, is_base_type, is_number_base_type, is_stream_param_type, is_array_type, is_record_type)
VALUES
        (1,  'TBool',        1, 0, 1, 1, 1),
        (2,  'TBitField',    1, 0, 1, 1, 1),
        (3,  'TEnum',        1, 0, 1, 1, 1),
        (4,  'TNumber',      1, 0, 1, 1, 1),
        (5,  'TVoid',        0, 1, 0, 0, 0),
        (6,  'TUByte',       0, 1, 0, 0, 0),
        (7,  'TSByte',       0, 1, 0, 0, 0),
        (8,  'TUWord',       0, 1, 0, 0, 0),
        (9,  'TSWord',       0, 1, 0, 0, 0),
        (10, 'TULong',       0, 1, 0, 0, 0),
        (11, 'TSLong',       0, 1, 0, 0, 0),
        (12, 'TString',      1, 0, 1, 1, 1),
        (13, 'TStream',      1, 0, 1, 1, 1),
        (14, 'TCStream',     1, 0, 1, 1, 1),
        (15, 'TShortStream', 1, 0, 1, 1, 1),
        (16, 'TArray',       1, 0, 0, 1, 1),
        (17, 'TRecord',      1, 0, 0, 0, 1);

CREATE TABLE most_types (
    id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL UNIQUE,
    primitive_type_id INT UNSIGNED NOT NULL,
    is_primary_type BOOLEAN NOT NULL,
    bitfield_length VARCHAR(255) NULL DEFAULT NULL,
    enum_max VARCHAR(255) NULL DEFAULT NULL,
    number_base_type_id INT UNSIGNED NULL DEFAULT NULL,
    number_exponent VARCHAR(255) NULL DEFAULT NULL,
    number_range_min VARCHAR(255) NULL DEFAULT NULL,
    number_range_max VARCHAR(255) NULL DEFAULT NULL,
    number_step VARCHAR(255) NULL DEFAULT NULL,
    number_unit_id INT UNSIGNED NULL DEFAULT NULL,
    string_max_size VARCHAR(255) NULL DEFAULT NULL,
    stream_length VARCHAR(255) NULL DEFAULT NULL,
    stream_max_length VARCHAR(255) NULL DEFAULT NULL,
    stream_media_type VARCHAR(255) NULL DEFAULT NULL,
    array_name VARCHAR(255) NULL DEFAULT NULL,
    array_description TEXT NULL DEFAULT NULL,
    array_element_type_id INT UNSIGNED NULL DEFAULT NULL,
    array_size VARCHAR(255) NULL DEFAULT NULL,
    record_name VARCHAR(255) NULL DEFAULT NULL,
    record_description TEXT NULL DEFAULT NULL,
    record_size VARCHAR(255) NULL DEFAULT NULL,
    FOREIGN KEY (primitive_type_id) REFERENCES primitive_types (id),
    FOREIGN KEY (number_base_type_id) REFERENCES primitive_types (id),
    FOREIGN KEY (number_unit_id) REFERENCES most_units (id),
    FOREIGN KEY (array_element_type_id) REFERENCES most_types (id)
) ENGINE=INNODB;

CREATE INDEX most_types_primitive_type_id_index ON most_types (primitive_type_id);
CREATE INDEX most_types_is_primary_type_index ON most_types (is_primary_type);

CREATE TABLE bool_fields (
    id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
    type_id INT UNSIGNED NOT NULL,
    bit_position VARCHAR(255) NOT NULL,
    true_description TEXT NOT NULL,
    false_description TEXT NOT NULL,
    FOREIGN KEY (type_id) REFERENCES most_types (id)
) ENGINE=INNODB;

CREATE INDEX bool_fields_type_id_index ON bool_fields (type_id);

CREATE TABLE enum_values (
    id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
    type_id INT UNSIGNED NOT NULL,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(255) NOT NULL,
    FOREIGN KEY (type_id) REFERENCES most_types (id)
) ENGINE=INNODB;

CREATE INDEX enum_values_type_id_index ON enum_values (type_id);

CREATE TABLE stream_cases (
    id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
    type_id INT UNSIGNED NOT NULL,
    stream_position_x VARCHAR(255) NOT NULL,
    stream_position_y VARCHAR(255) NOT NULL,
    FOREIGN KEY (type_id) REFERENCES most_types (id)
) ENGINE=INNODB;

CREATE INDEX stream_cases_type_id_index ON stream_cases (type_id);

CREATE TABLE stream_case_parameters (
    id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
    stream_case_id INT UNSIGNED NOT NULL,
    parameter_name VARCHAR(255) NOT NULL,
    parameter_index VARCHAR(255) NOT NULL,
    parameter_description TEXT NOT NULL,
    parameter_type_id INT UNSIGNED NOT NULL,
    FOREIGN KEY (stream_case_id) REFERENCES stream_cases (id),
    FOREIGN KEY (parameter_type_id) REFERENCES most_types (id)
) ENGINE=INNODB;

CREATE INDEX stream_case_parameters_stream_case_id_index ON stream_case_parameters (stream_case_id);

CREATE TABLE stream_case_signals (
    id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
    stream_case_id INT UNSIGNED NOT NULL,
    signal_name VARCHAR(255) NOT NULL,
    signal_index VARCHAR(255) NOT NULL,
    signal_description TEXT NOT NULL,
    signal_bit_length VARCHAR(255) NOT NULL,
    FOREIGN KEY (stream_case_id) REFERENCES stream_cases (id)
) ENGINE=INNODB;

CREATE INDEX stream_case_signals_stream_case_id_index ON stream_case_signals (stream_case_id);

CREATE TABLE record_fields (
    id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
    type_id INT UNSIGNED NOT NULL,
    field_name VARCHAR(255) NOT NULL,
    field_index VARCHAR(255) NOT NULL,
    field_description TEXT NOT NULL,
    field_type_id INT UNSIGNED NOT NULL,
    FOREIGN KEY (type_id) REFERENCES most_types (id),
    FOREIGN KEY (field_type_id) REFERENCES most_types (id)
) ENGINE=INNODB;

CREATE INDEX records_fields_type_id_index ON record_fields (type_id);

CREATE TABLE function_categories (
    id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL UNIQUE
) ENGINE=INNODB;

INSERT INTO function_categories (name)
VALUES
        ('Property'),
        ('Method');

CREATE TABLE function_stereotypes (
    id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    supports_notification BOOLEAN NOT NULL,
    category VARCHAR(255) NOT NULL,
    FOREIGN KEY (category) REFERENCES function_categories (name)
) ENGINE=INNODB;

INSERT INTO function_stereotypes (id, name, supports_notification, category)
VALUES
        (1, 'Event',                       1, 'Property'),
        (2, 'ReadOnlyProperty',            0, 'Property'),
        (3, 'ReadOnlyPropertyWithEvent',   1, 'Property'),
        (4, 'PropertyWithEvent',           1, 'Property'),
        (5, 'CommandWithAck',              0, 'Method'),
        (6, 'Request/Response',            0, 'Method');

CREATE TABLE functions (
    id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
    most_id VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    function_stereotype_id INT UNSIGNED NOT NULL,
    category VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    release_version VARCHAR(255) NOT NULL,
    account_id INT UNSIGNED NOT NULL,
    company_id INT UNSIGNED NOT NULL,
    return_type_id INT UNSIGNED NOT NULL,
    supports_notification BOOLEAN NOT NULL,
    is_released BOOLEAN NOT NULL DEFAULT FALSE,
    prior_version_id INT UNSIGNED NULL,
    FOREIGN KEY (function_stereotype_id) REFERENCES function_stereotypes (id),
    FOREIGN KEY (category) REFERENCES function_categories (name),
    FOREIGN KEY (account_id) REFERENCES accounts (id),
    FOREIGN KEY (company_id) REFERENCES companies (id),
    FOREIGN KEY (return_type_id) REFERENCES most_types (id),
    FOREIGN KEY (prior_version_id) REFERENCES functions (id)
) ENGINE=INNODB;

CREATE TABLE interfaces_functions (
    id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
    interface_id INT UNSIGNED NOT NULL,
    function_id INT UNSIGNED NOT NULL,
    FOREIGN KEY (interface_id) REFERENCES interfaces (id),
    FOREIGN KEY (function_id) REFERENCES functions (id)
) ENGINE=INNODB;

CREATE TABLE function_parameters (
    id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
    function_id INT UNSIGNED NOT NULL,
    parameter_index INT UNSIGNED NOT NULL,
    most_type_id INT UNSIGNED NOT NULL,
    FOREIGN KEY (function_id) REFERENCES functions (id),
    FOREIGN KEY (most_type_id) REFERENCES most_types (id)
) ENGINE=INNODB;

CREATE TABLE operations (
    id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    opcode VARCHAR(255) NOT NULL UNIQUE,
    is_input BOOLEAN NOT NULL
) ENGINE=INNODB;

INSERT INTO operations (id, name, opcode, is_input)
VALUES
        (1, 'Get',              '0x1', 1),
        (2, 'Set',              '0x0', 1),
        (3, 'StartResultAck',   '0x6', 1),
        (4, 'AbortAck',         '0x7', 1),
        (5, 'Status',           '0xC', 0),
        (6, 'Error',            '0xF', 0),
        (7, 'ResultAck',        '0xD', 0),
        (8, 'ProcessingAck',    '0xA', 0),
        (9, 'ErrorAck',         '0x9', 0);

CREATE TABLE function_stereotypes_operations (
    id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
    function_stereotype_id INT UNSIGNED NOT NULL,
    operation_id INT UNSIGNED NOT NULL,
    FOREIGN KEY (function_stereotype_id) REFERENCES function_stereotypes (id),
    FOREIGN KEY (operation_id) REFERENCES operations (id)
) ENGINE=INNODB;

INSERT INTO function_stereotypes_operations (function_stereotype_id, operation_id)
VALUES
        -- Event (none)
        (1, 5),
        (1, 6),
        -- ReadOnlyProperty
        (2, 1),
        (2, 5),
        (2, 6),
        -- ReadOnlyPropertyWithEvent
        (3, 1),
        (3, 5),
        (3, 6),
        -- PropertyWithEvent
        (4, 1),
        (4, 2),
        (4, 5),
        (4, 6),
        -- CommandWithAck
        (5, 3),
        (5, 7),
        (5, 8),
        (5, 9),
        -- Request/Response
        (6, 3),
        (6, 4),
        (6, 7),
        (6, 8),
        (6, 9);

CREATE TABLE functions_operations (
    id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
    function_id INT UNSIGNED NOT NULL,
    operation_id INT UNSIGNED NOT NULL,
    FOREIGN KEY (function_id) REFERENCES functions (id),
    FOREIGN KEY (operation_id) REFERENCES operations (id)
) ENGINE=INNODB;

