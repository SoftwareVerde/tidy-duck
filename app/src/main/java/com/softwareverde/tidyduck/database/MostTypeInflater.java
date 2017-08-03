package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.Query;
import com.softwareverde.database.Row;
import com.softwareverde.tidyduck.most.MostType;
import com.softwareverde.tidyduck.most.MostUnit;
import com.softwareverde.tidyduck.most.PrimitiveType;

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
        final String bitfieldLength = row.getString("bitfield_length");
        final String enumMax = row.getString("enum_max");
        final Long numberBaseTypeId = row.getLong("number_base_type_id");
        final String numberExponent = row.getString("number_exponent");
        final String numberRangeMin = row.getString("number_range_min");
        final String numberRangeMax = row.getString("number_range_max");
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

        // TODO: inflate boolean fields
        // TODO: inflate enum values
        // TODO: inflate stream cases
        // TODO: inflate record fields

        PrimitiveType primitiveType = inflatePrimitiveType(primitiveTypeId);

        MostType numberBaseType = null;
        if (numberBaseTypeId != null) {
            numberBaseType = inflateMostType(numberBaseTypeId);
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
        mostType.setBitfieldLength(bitfieldLength);
        mostType.setBitfieldLength(bitfieldLength);
        mostType.setEnumMax(enumMax);
        mostType.setNumberBaseType(numberBaseType);
        mostType.setNumberExponent(numberExponent);
        mostType.setNumberRangeMinimum(numberRangeMin);
        mostType.setNumberRangeMaximum(numberRangeMax);
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
        return mostType;
    }

    protected PrimitiveType convertRowToPrimitiveType(Row row) {
        final Long id = row.getLong("id");
        final String name = row.getString("name");
        final boolean isPreloadedType = row.getBoolean("is_preloaded_type");
        final boolean isArrayType = row.getBoolean("is_array_type");
        final boolean isStreamParameterType = row.getBoolean("is_stream_param_type");
        final boolean isNumberBaseType = row.getBoolean("is_number_base_type");
        final boolean isRecordType = row.getBoolean("is_record_type");

        final PrimitiveType primitiveType = new PrimitiveType();
        primitiveType.setId(id);
        primitiveType.setName(name);
        primitiveType.setPreloadedType(isPreloadedType);
        primitiveType.setArrayType(isArrayType);
        primitiveType.setStreamParameterType(isStreamParameterType);
        primitiveType.setNumberBaseType(isNumberBaseType);
        primitiveType.setRecordType(isRecordType);
        return primitiveType;
    }

    private MostUnit convertRowToMostUnit(Row row) {
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
}