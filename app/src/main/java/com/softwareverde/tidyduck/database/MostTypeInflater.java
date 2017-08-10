package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.Query;
import com.softwareverde.database.Row;
import com.softwareverde.tidyduck.most.*;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class MostTypeInflater {

    protected final DatabaseConnection<Connection> _databaseConnection;

    public MostTypeInflater(DatabaseConnection<Connection> connection) {
        _databaseConnection = connection;
    }

    public List<MostType> inflateMostTypes() throws DatabaseException {
        final Query query = new Query("SELECT * FROM most_types");

        List<Row> rows = _databaseConnection.query(query);
        ArrayList<MostType> types = new ArrayList<>();
        for (final Row row : rows) {
            MostType mostType = convertRowToMostType(row);
            types.add(mostType);
        }
        return types;
    }

    public MostType inflateMostType(final long mostTypeId) throws DatabaseException {
        final Query query = new Query(
                "SELECT * FROM most_types WHERE id = ?"
        );
        query.setParameter(mostTypeId);

        final List<Row> rows = _databaseConnection.query(query);
        if (rows.size() == 0) {
            throw new DatabaseException("Most Type ID " + mostTypeId + " not found.");
        }
        final Row row = rows.get(0);
        final MostType mostType = convertRowToMostType(row);
        return mostType;
    }

    public List<PrimitiveType> inflatePrimitiveTypes() throws DatabaseException {
        final Query query = new Query("SELECT * FROM primitive_types");

        List<Row> rows = _databaseConnection.query(query);
        ArrayList<PrimitiveType> types = new ArrayList<>();
        for (final Row row : rows) {
            PrimitiveType primitiveType = convertRowToPrimitiveType(row);
            types.add(primitiveType);
        }
        return types;
    }

    public PrimitiveType inflatePrimitiveType(final long primitiveTypeId) throws DatabaseException {
        final Query query = new Query(
                "SELECT * FROM primitive_types WHERE id = ?"
        );
        query.setParameter(primitiveTypeId);

        final List<Row> rows = _databaseConnection.query(query);
        if (rows.size() == 0) {
            throw new DatabaseException("Primitive Type ID " + primitiveTypeId + " not found.");
        }
        final Row row = rows.get(0);
        final PrimitiveType primitiveType = convertRowToPrimitiveType(row);
        return primitiveType;
    }

    public List<MostUnit> inflateMostUnits() throws DatabaseException {
        final Query query = new Query(
                "SELECT * FROM most_units"
        );

        List<Row> rows = _databaseConnection.query(query);
        ArrayList<MostUnit> mostUnits = new ArrayList<>();
        for (final Row row : rows) {
            MostUnit mostUnit = convertRowToMostUnit(row);
            mostUnits.add(mostUnit);
        }
        return mostUnits;
    }

    public MostUnit inflateMostUnit(final long mostUnitId) throws DatabaseException {
        final Query query = new Query(
                "SELECT * FROM most_units WHERE id = ?"
        );
        query.setParameter(mostUnitId);

        final List<Row> rows = _databaseConnection.query(query);
        if (rows.size() == 0) {
            throw new DatabaseException("Most Unit ID " + mostUnitId + " not found.");
        }
        final Row row = rows.get(0);
        final MostUnit mostUnit = convertRowToMostUnit(row);
        return mostUnit;
    }

    protected MostType convertRowToMostType(final Row row) throws DatabaseException {
        final Long id = row.getLong("id");
        final String name = row.getString("name");
        final long primitiveTypeId = row.getLong("primitive_type_id");
        final boolean isPrimaryType = row.getBoolean("is_primary_type");
        final String bitfieldLength = row.getString("bitfield_length");
        final String enumMax = row.getString("enum_max");
        final Long numberBaseTypeId = row.getLong("number_base_type_id");
        final String numberExponent = row.getString("number_exponent");
        final String numberRangeMin = row.getString("number_range_min");
        final String numberRangeMax = row.getString("number_range_max");
        final String numberStep = row.getString("number_step");
        final Long numberUnitId = row.getLong("number_unit_id");
        final String stringMaxSize = row.getString("string_max_size");
        final String streamLength = row.getString("stream_length");
        final String streamMaxLength = row.getString("stream_max_length");
        final String streamMediaType = row.getString("stream_media_type");
        final String arrayName = row.getString("array_name");
        final String arrayDescription = row.getString("array_description");
        final Long arrayElementTypeId = row.getLong("array_element_type_id");
        final String arraySize = row.getString("array_size");
        final String recordName = row.getString("record_name");
        final String recordDescription = row.getString("record_description");
        final String recordSize = row.getString("record_size");

        List<BooleanField> booleanFields = getBooleanFields(id);
        List<EnumValue> enumValues = getEnumValues(id);
        List<StreamCase> streamCases = getStreamCases(id);
        List<RecordField> recordFields = getRecordFields(id);

        PrimitiveType primitiveType = inflatePrimitiveType(primitiveTypeId);

        PrimitiveType numberBaseType = null;
        if (numberBaseTypeId != null) {
            numberBaseType = inflatePrimitiveType(numberBaseTypeId);
        }

        MostUnit numberUnit = null;
        if (numberUnitId != null) {
            numberUnit = inflateMostUnit(numberUnitId);
        }

        MostType arrayElementType = null;
        if (arrayElementTypeId != null) {
            arrayElementType = inflateMostType(arrayElementTypeId);
        }

        final MostType mostType = new MostType();
        mostType.setId(id);
        mostType.setName(name);
        mostType.setPrimitiveType(primitiveType);
        mostType.setIsPrimaryType(isPrimaryType);
        mostType.setBitfieldLength(bitfieldLength);
        mostType.setBitfieldLength(bitfieldLength);
        mostType.setEnumMax(enumMax);
        mostType.setNumberBaseType(numberBaseType);
        mostType.setNumberExponent(numberExponent);
        mostType.setNumberRangeMinimum(numberRangeMin);
        mostType.setNumberRangeMaximum(numberRangeMax);
        mostType.setNumberStep(numberStep);
        mostType.setNumberUnit(numberUnit);
        mostType.setStringMaxSize(stringMaxSize);
        mostType.setStreamLength(streamLength);
        mostType.setStreamMaxLength(streamMaxLength);
        mostType.setStreamMediaType(streamMediaType);
        mostType.setArrayName(arrayName);
        mostType.setArrayDescription(arrayDescription);
        mostType.setArrayElementType(arrayElementType);
        mostType.setArraySize(arraySize);
        mostType.setRecordName(recordName);
        mostType.setRecordDescription(recordDescription);
        mostType.setRecordSize(recordSize);

        mostType.setBooleanFields(booleanFields);
        mostType.setEnumValues(enumValues);
        mostType.setStreamCases(streamCases);
        mostType.setRecordFields(recordFields);
        return mostType;
    }

    private List<BooleanField> getBooleanFields(final long typeId) throws DatabaseException {
        final Query query = new Query(
                "SELECT * FROM bool_fields WHERE type_id = ?"
        );
        query.setParameter(typeId);

        final List<Row> rows = _databaseConnection.query(query);

        final ArrayList<BooleanField> booleanFields = new ArrayList<>();
        for (final Row row : rows) {
            BooleanField booleanField = convertRowToBooleanField(row);
            booleanFields.add(booleanField);
        }
        return booleanFields;
    }

    private List<EnumValue> getEnumValues(final long typeId) throws DatabaseException {
        final Query query = new Query(
                "SELECT * FROM enum_values WHERE type_id = ?"
        );
        query.setParameter(typeId);

        final List<Row> rows = _databaseConnection.query(query);

        final ArrayList<EnumValue> enumValues = new ArrayList<>();
        for (final Row row : rows) {
            EnumValue enumValue = convertRowToEnumValue(row);
            enumValues.add(enumValue);
        }

        return enumValues;
    }

    private List<StreamCase> getStreamCases(final long typeId) throws DatabaseException {
        final Query query = new Query(
                "SELECT * FROM stream_cases WHERE type_id = ?"
        );
        query.setParameter(typeId);

        final List<Row> rows = _databaseConnection.query(query);

        final ArrayList<StreamCase> streamCases = new ArrayList<>();
        for (final Row row : rows) {
            final StreamCase streamCase = new StreamCase();

            final Long id = row.getLong("id");
            final String streamPositionX = row.getString("stream_position_x");
            final String streamPositionY = row.getString("stream_position_y");

            streamCase.setId(id);
            streamCase.setStreamPositionX(streamPositionX);
            streamCase.setStreamPositionY(streamPositionY);

            // stream parameters
            List<StreamCaseParameter> streamCaseParameters = getStreamCaseParameters(id);
            streamCase.setStreamCaseParameters(streamCaseParameters);

            // stream signals
            List<StreamCaseSignal> streamCaseSignals = getStreamCaseSignals(id);
            streamCase.setStreamCaseSignals(streamCaseSignals);

            streamCases.add(streamCase);
        }
        return streamCases;
    }

    private List<StreamCaseParameter> getStreamCaseParameters(final long streamCaseId) throws DatabaseException {
        final Query query = new Query(
                "SELECT * FROM stream_case_parameters WHERE stream_case_id = ?"
        );
        query.setParameter(streamCaseId);

        final List<Row> rows = _databaseConnection.query(query);

        final ArrayList<StreamCaseParameter> streamCaseParameters = new ArrayList<>();
        for (final Row row : rows) {
            final StreamCaseParameter streamCaseParameter = new StreamCaseParameter();

            final Long id = row.getLong("id");
            final String parameterName = row.getString("parameter_name");
            final String parameterIndex = row.getString("parameter_index");
            final String parameterDescription = row.getString("parameter_description");
            final Long parameterTypeId = row.getLong("parameter_type_id");

            final MostType parameterType = inflateMostType(parameterTypeId);

            streamCaseParameter.setId(id);
            streamCaseParameter.setParameterName(parameterName);
            streamCaseParameter.setParameterIndex(parameterIndex);
            streamCaseParameter.setParameterDescription(parameterDescription);
            streamCaseParameter.setParameterType(parameterType);

            streamCaseParameters.add(streamCaseParameter);
        }
        return streamCaseParameters;
    }

    private List<StreamCaseSignal> getStreamCaseSignals(final long streamCaseId) throws DatabaseException {
        final Query query = new Query(
                "SELECT * FROM stream_case_signals WHERE stream_case_id = ?"
        );
        query.setParameter(streamCaseId);

        final List<Row> rows = _databaseConnection.query(query);

        final ArrayList<StreamCaseSignal> streamCaseSignals = new ArrayList<>();
        for (final Row row : rows) {
            final StreamCaseSignal streamCaseSignal = new StreamCaseSignal();

            final Long id = row.getLong("id");
            final String signalName = row.getString("signal_name");
            final String signalIndex = row.getString("signal_index");
            final String signalDescription = row.getString("signal_description");
            final String signalBitLength = row.getString("signal_bit_length");

            streamCaseSignal.setId(id);
            streamCaseSignal.setSignalName(signalName);
            streamCaseSignal.setSignalIndex(signalIndex);
            streamCaseSignal.setSignalDescription(signalDescription);
            streamCaseSignal.setSignalBitLength(signalBitLength);

            streamCaseSignals.add(streamCaseSignal);
        }
        return streamCaseSignals;
    }

    private List<RecordField> getRecordFields(final long typeId) throws DatabaseException {
        final Query query = new Query(
                "SELECT * FROM record_fields WHERE type_id = ?"
        );
        query.setParameter(typeId);

        final List<Row> rows = _databaseConnection.query(query);

        final ArrayList<RecordField> recordFields = new ArrayList<>();
        for (final Row row : rows) {
            final RecordField recordField = new RecordField();

            final Long id = row.getLong("id");
            final String fieldName = row.getString("field_name");
            final String fieldIndex = row.getString("field_index");
            final String fieldDescription = row.getString("field_description");
            final Long fieldTypeId = row.getLong("field_type_id");
            final MostType fieldType = inflateMostType(fieldTypeId);

            recordField.setId(id);
            recordField.setFieldName(fieldName);
            recordField.setFieldIndex(fieldIndex);
            recordField.setFieldDescription(fieldDescription);
            recordField.setFieldType(fieldType);

            recordFields.add(recordField);
        }
        return recordFields;
    }

    protected PrimitiveType convertRowToPrimitiveType(final Row row) {
        final Long id = row.getLong("id");
        final String name = row.getString("name");
        final boolean isBaseType = row.getBoolean("is_base_type");
        final boolean isArrayType = row.getBoolean("is_array_type");
        final boolean isStreamParameterType = row.getBoolean("is_stream_param_type");
        final boolean isNumberBaseType = row.getBoolean("is_number_base_type");
        final boolean isRecordType = row.getBoolean("is_record_type");

        final PrimitiveType primitiveType = new PrimitiveType();
        primitiveType.setId(id);
        primitiveType.setName(name);
        primitiveType.setIsBaseType(isBaseType);
        primitiveType.setIsArrayType(isArrayType);
        primitiveType.setIsStreamParameterType(isStreamParameterType);
        primitiveType.setIsNumberBaseType(isNumberBaseType);
        primitiveType.setIsRecordType(isRecordType);
        return primitiveType;
    }

    private MostUnit convertRowToMostUnit(final Row row) {
        final Long id = row.getLong("id");
        final String referenceName = row.getString("reference_name");
        final String definitionName = row.getString("definition_name");
        final String definitionCode = row.getString("definition_code");
        final String definitionGroup = row.getString("definition_group");

        MostUnit mostUnit = new MostUnit();
        mostUnit.setId(id);
        mostUnit.setReferenceName(referenceName);
        mostUnit.setDefinitionName(definitionName);
        mostUnit.setDefinitionCode(definitionCode);
        mostUnit.setDefinitionGroup(definitionGroup);
        return mostUnit;
    }

    private BooleanField convertRowToBooleanField(final Row row) {
        final Long id = row.getLong("id");
        final String bitPosition = row.getString("bit_position");
        final String trueDescription = row.getString("true_description");
        final String falseDescription = row.getString("false_description");

        final BooleanField booleanField = new BooleanField();
        booleanField.setId(id);
        booleanField.setBitPosition(bitPosition);
        booleanField.setTrueDescription(trueDescription);
        booleanField.setFalseDescription(falseDescription);

        return booleanField;
    }

    private EnumValue convertRowToEnumValue(final Row row) {
        final Long id = row.getLong("id");
        final String name = row.getString("name");
        final String code = row.getString("code");

        final EnumValue enumValue = new EnumValue();
        enumValue.setId(id);
        enumValue.setName(name);
        enumValue.setCode(code);

        return enumValue;
    }
}