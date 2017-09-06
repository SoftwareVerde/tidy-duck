package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.Query;
import com.softwareverde.tidyduck.most.*;

import java.sql.Connection;

class MostTypeDatabaseManager {

    private final DatabaseConnection<Connection> _databaseConnection;

    public MostTypeDatabaseManager(final DatabaseConnection<Connection> databaseConnection) {
        _databaseConnection = databaseConnection;
    }

    public void insertMostType(final MostType mostType) throws DatabaseException {
        _insertMostType(mostType);
    }

    private void _insertMostType(final MostType mostType) throws DatabaseException {
        final String name = mostType.getName();
        final long primitiveTypeId = mostType.getPrimitiveType().getId();
        final boolean isPrimaryType = mostType.isPrimaryType();
        final String bitfieldLength = mostType.getBitfieldLength();
        final String enumMax = mostType.getEnumMax();
        final Long numberBaseTypeId = mostType.getNumberBaseType() == null ? null : mostType.getNumberBaseType().getId();
        final String numberExponent = mostType.getNumberExponent();
        final String numberRangeMin = mostType.getNumberRangeMinimum();
        final String numberRangeMax = mostType.getNumberRangeMaximum();
        final String numberStep = mostType.getNumberStep();
        final Long numberUnitId = mostType.getNumberUnit() == null ? null : mostType.getNumberUnit().getId();
        final String stringMaxSize = mostType.getStringMaxSize();
        final String streamLength = mostType.getStreamLength();
        final String streamMaxLength = mostType.getStreamMaxLength();
        final String streamMediaType = mostType.getStreamMediaType();
        final String arrayName = mostType.getArrayName();
        final String arrayDescription = mostType.getArrayDescription();
        final Long arrayElementTypeId = mostType.getArrayElementType() == null ? null : mostType.getArrayElementType().getId();
        final String arraySize = mostType.getArraySize();
        final String recordName = mostType.getRecordName();
        final String recordDescription = mostType.getRecordDescription();
        final String recordSize = mostType.getRecordSize();

        final Query query = new Query("INSERT INTO most_types (name, primitive_type_id, is_primary_type, bitfield_length, enum_max, " +
                                                    "number_base_type_id, number_exponent, number_range_min, number_range_max, number_step, " +
                                                    "number_unit_id, string_max_size, stream_length, stream_max_length, stream_media_type, " +
                                                    "array_name, array_description, array_element_type_id, array_size, record_name, " +
                                                    "record_description, record_size) " +
                                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")
            .setParameter(name)
            .setParameter(primitiveTypeId)
            .setParameter(isPrimaryType)
            .setParameter(bitfieldLength)
            .setParameter(enumMax)
            .setParameter(numberBaseTypeId)
            .setParameter(numberExponent)
            .setParameter(numberRangeMin)
            .setParameter(numberRangeMax)
            .setParameter(numberStep)
            .setParameter(numberUnitId)
            .setParameter(stringMaxSize)
            .setParameter(streamLength)
            .setParameter(streamMaxLength)
            .setParameter(streamMediaType)
            .setParameter(arrayName)
            .setParameter(arrayDescription)
            .setParameter(arrayElementTypeId)
            .setParameter(arraySize)
            .setParameter(recordName)
            .setParameter(recordDescription)
            .setParameter(recordSize)
        ;

        final long mostTypeId = _databaseConnection.executeSql(query);
        mostType.setId(mostTypeId);

        // fields with other tables
        for (final BooleanField booleanField : mostType.getBooleanFields()) {
            _addBooleanField(mostTypeId, booleanField);
        }
        for (final EnumValue enumValue : mostType.getEnumValues()) {
            _addEnumValue(mostTypeId, enumValue);
        }
        for (final StreamCase streamCase : mostType.getStreamCases()) {
            _addStreamCase(mostTypeId, streamCase);
        }
        for (final RecordField recordField : mostType.getRecordFields()) {
            _addRecordField(mostTypeId, recordField);
        }
    }

    public void updateMostType (final MostType mostType) throws DatabaseException {
        final String name = mostType.getName();
        final long primitiveTypeId = mostType.getPrimitiveType().getId();
        final boolean isPrimaryType = mostType.isPrimaryType();
        final String bitfieldLength = mostType.getBitfieldLength();
        final String enumMax = mostType.getEnumMax();
        final Long numberBaseTypeId = mostType.getNumberBaseType() == null ? null : mostType.getNumberBaseType().getId();
        final String numberExponent = mostType.getNumberExponent();
        final String numberRangeMin = mostType.getNumberRangeMinimum();
        final String numberRangeMax = mostType.getNumberRangeMaximum();
        final String numberStep = mostType.getNumberStep();
        final Long numberUnitId = mostType.getNumberUnit() == null ? null : mostType.getNumberUnit().getId();
        final String stringMaxSize = mostType.getStringMaxSize();
        final String streamLength = mostType.getStreamLength();
        final String streamMaxLength = mostType.getStreamMaxLength();
        final String streamMediaType = mostType.getStreamMediaType();
        final String arrayName = mostType.getArrayName();
        final String arrayDescription = mostType.getArrayDescription();
        final Long arrayElementTypeId = mostType.getArrayElementType() == null ? null : mostType.getArrayElementType().getId();
        final String arraySize = mostType.getArraySize();
        final String recordName = mostType.getRecordName();
        final String recordDescription = mostType.getRecordDescription();
        final String recordSize = mostType.getRecordSize();
        final long mostTypeId = mostType.getId();

        final Query query = new Query("UPDATE most_types SET name = ?, primitive_type_id = ?, is_primary_type = ?, bitfield_length = ?, " +
                                            "enum_max = ?, number_base_type_id = ?, number_exponent = ?, number_range_min = ?, number_range_max =?, " +
                                            "number_step = ?, number_unit_id = ?, string_max_size = ?, stream_length = ?, stream_max_length = ?, stream_media_type = ?, " +
                                            "array_name = ?, array_description = ?, array_element_type_id = ?, array_size = ?, record_name = ?, record_description = ?, " +
                                            "record_size = ? WHERE id = ?")
                .setParameter(name)
                .setParameter(primitiveTypeId)
                .setParameter(isPrimaryType)
                .setParameter(bitfieldLength)
                .setParameter(enumMax)
                .setParameter(numberBaseTypeId)
                .setParameter(numberExponent)
                .setParameter(numberRangeMin)
                .setParameter(numberRangeMax)
                .setParameter(numberStep)
                .setParameter(numberUnitId)
                .setParameter(stringMaxSize)
                .setParameter(streamLength)
                .setParameter(streamMaxLength)
                .setParameter(streamMediaType)
                .setParameter(arrayName)
                .setParameter(arrayDescription)
                .setParameter(arrayElementTypeId)
                .setParameter(arraySize)
                .setParameter(recordName)
                .setParameter(recordDescription)
                .setParameter(recordSize)
                .setParameter(mostTypeId)
        ;

        _databaseConnection.executeSql(query);

        // Clear out type bool fields, enum values, stream cases, and record fields, then add current ones if necessary.
        _removeBooleanFieldsFromMostType(mostTypeId);
        _removeEnumValuesFromMostType(mostTypeId);
        _removeStreamCasesFromMostType(mostTypeId);
        _removeRecordFieldsFromMostType(mostTypeId);

        // fields with other tables
        for (final BooleanField booleanField : mostType.getBooleanFields()) {
            _addBooleanField(mostTypeId, booleanField);
        }
        for (final EnumValue enumValue : mostType.getEnumValues()) {
            _addEnumValue(mostTypeId, enumValue);
        }
        for (final StreamCase streamCase : mostType.getStreamCases()) {
            _addStreamCase(mostTypeId, streamCase);
        }
        for (final RecordField recordField : mostType.getRecordFields()) {
            _addRecordField(mostTypeId, recordField);
        }
    }

    protected void _addBooleanField(final long mostTypeId, final BooleanField booleanField) throws DatabaseException {
        final String bitPosition = booleanField.getBitPosition();
        final String trueDescription = booleanField.getTrueDescription();
        final String falseDescription = booleanField.getFalseDescription();

        final Query query = new Query("INSERT INTO bool_fields (type_id, bit_position, true_description, false_description) VALUES (?, ?, ?, ?)")
            .setParameter(mostTypeId)
            .setParameter(bitPosition)
            .setParameter(trueDescription)
            .setParameter(falseDescription)
        ;

        final long booleanFieldId = _databaseConnection.executeSql(query);
        booleanField.setId(booleanFieldId);
    }

    private void _removeBooleanFieldsFromMostType(final long mostTypeId) throws DatabaseException {
        final Query query = new Query("DELETE FROM bool_fields WHERE type_id = ?")
                .setParameter(mostTypeId)
        ;

        _databaseConnection.executeSql(query);
    }

    protected void _addEnumValue(final long mostTypeId, final EnumValue enumValue) throws DatabaseException {
        final String name = enumValue.getName();
        final String code = enumValue.getCode();
        final String description = enumValue.getDescription();

        final Query query = new Query("INSERT INTO enum_values (type_id, name, code, description) VALUES (?, ?, ?, ?)")
            .setParameter(mostTypeId)
            .setParameter(name)
            .setParameter(code)
            .setParameter(description)
        ;

        final long enumValueId = _databaseConnection.executeSql(query);
        enumValue.setId(enumValueId);
    }

    private void _removeEnumValuesFromMostType(final long mostTypeId) throws DatabaseException {
        final Query query = new Query("DELETE FROM enum_values WHERE type_id = ?")
                .setParameter(mostTypeId)
                ;

        _databaseConnection.executeSql(query);
    }

    protected void _addStreamCase(final long mostTypeId, final StreamCase streamCase) throws DatabaseException {
        final String streamPositionX = streamCase.getStreamPositionX();
        final String streamPositionY = streamCase.getStreamPositionY();

        final Query query = new Query("INSERT INTO stream_cases (type_id, stream_position_x, stream_position_y) VALUES (?, ?, ?)")
            .setParameter(mostTypeId)
            .setParameter(streamPositionX)
            .setParameter(streamPositionY)
        ;

        final long streamCaseId = _databaseConnection.executeSql(query);
        streamCase.setId(streamCaseId);

        for (final StreamCaseParameter streamCaseParameter : streamCase.getStreamCaseParameters()) {
            _addStreamCaseParameter(streamCaseId, streamCaseParameter);
        }
        for (final StreamCaseSignal streamCaseSignal : streamCase.getStreamCaseSignals()) {
            _addStreamCaseSignal(streamCaseId, streamCaseSignal);
        }
    }

    private void _removeStreamCasesFromMostType(final long mostTypeId) throws DatabaseException {
        // Remove signals and parameters first.
        _removeStreamCaseParametersFromStreamCase(mostTypeId);
        _removeStreamCaseSignalsFromStreamCase(mostTypeId);

        final Query query = new Query("DELETE FROM stream_cases WHERE type_id = ?")
                .setParameter(mostTypeId)
                ;

        _databaseConnection.executeSql(query);
    }

    private void _addStreamCaseParameter(final long streamCaseId, final StreamCaseParameter streamCaseParameter) throws DatabaseException {
        final String parameterName = streamCaseParameter.getParameterName();
        final String parameterIndex = streamCaseParameter.getParameterIndex();
        final String parameterDescription = streamCaseParameter.getParameterDescription();
        final long parameterTypeId = streamCaseParameter.getParameterType().getId();

        final Query query = new Query("INSERT INTO stream_case_parameters (stream_case_id, parameter_name, parameter_index, parameter_description, parameter_type_id) VALUES (?, ?, ?, ?, ?)")
            .setParameter(streamCaseId)
            .setParameter(parameterName)
            .setParameter(parameterIndex)
            .setParameter(parameterDescription)
            .setParameter(parameterTypeId)
        ;

        final long streamCaseParameterId = _databaseConnection.executeSql(query);
        streamCaseParameter.setId(streamCaseParameterId);
    }

    private void _removeStreamCaseParametersFromStreamCase(final long mostTypeId) throws DatabaseException {
        final Query query = new Query("DELETE FROM stream_case_parameters WHERE stream_case_id IN (" +
                                            "SELECT DISTINCT stream_cases.id\n" +
                                            "FROM stream_cases\n" +
                                            "WHERE stream_cases.type_id = ?)");
        query.setParameter(mostTypeId);

        _databaseConnection.executeSql(query);
    }

    private void _addStreamCaseSignal(final long streamCaseId, final StreamCaseSignal streamCaseSignal) throws DatabaseException {
        final String signalName = streamCaseSignal.getSignalName();
        final String signalIndex = streamCaseSignal.getSignalIndex();
        final String signalDescription = streamCaseSignal.getSignalDescription();
        final String signalBitLength = streamCaseSignal.getSignalBitLength();

        final Query query = new Query("INSERT INTO stream_case_signals (stream_case_id, signal_name, signal_index, signal_description, signal_bit_length) VALUES (?, ?, ?, ?, ?)")
            .setParameter(streamCaseId)
            .setParameter(signalName)
            .setParameter(signalIndex)
            .setParameter(signalDescription)
            .setParameter(signalBitLength)
        ;

        final long streamCaseSignalId = _databaseConnection.executeSql(query);
        streamCaseSignal.setId(streamCaseSignalId);
    }

    private void _removeStreamCaseSignalsFromStreamCase(final long mostTypeId) throws DatabaseException {
        final Query query = new Query("DELETE FROM stream_case_signals WHERE stream_case_id IN (" +
                "SELECT DISTINCT stream_cases.id\n" +
                "FROM stream_cases\n" +
                "WHERE stream_cases.type_id = ?)");
        query.setParameter(mostTypeId);

        _databaseConnection.executeSql(query);
    }

    protected void _addRecordField(final long mostTypeId, final RecordField recordField) throws DatabaseException {
        final String fieldName = recordField.getFieldName();
        final String fieldIndex = recordField.getFieldIndex();
        final String fieldDescription = recordField.getFieldDescription();
        final long fieldTypeId = recordField.getFieldType().getId();

        final Query query = new Query("INSERT INTO record_fields (type_id, field_name, field_index, field_description, field_type_id) VALUES (?, ?, ?, ?, ?)")
            .setParameter(mostTypeId)
            .setParameter(fieldName)
            .setParameter(fieldIndex)
            .setParameter(fieldDescription)
            .setParameter(fieldTypeId)
        ;

        final long recordFieldId = _databaseConnection.executeSql(query);
        recordField.setId(recordFieldId);
    }

    private void _removeRecordFieldsFromMostType(final long mostTypeId) throws DatabaseException {
        final Query query = new Query("DELETE FROM record_fields WHERE type_id = ?")
                .setParameter(mostTypeId)
                ;

        _databaseConnection.executeSql(query);
    }
}
