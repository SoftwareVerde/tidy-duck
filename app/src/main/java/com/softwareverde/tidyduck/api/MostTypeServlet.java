package com.softwareverde.tidyduck.api;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.json.Json;
import com.softwareverde.tidyduck.database.DatabaseManager;
import com.softwareverde.tidyduck.database.MostTypeInflater;
import com.softwareverde.tidyduck.environment.Environment;
import com.softwareverde.tidyduck.most.*;
import com.softwareverde.tomcat.servlet.AuthenticatedJsonServlet;
import com.softwareverde.tomcat.servlet.BaseServlet;
import com.softwareverde.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;

public class MostTypeServlet extends AuthenticatedJsonServlet {
    private Logger _logger = LoggerFactory.getLogger(getClass());

    @Override
    protected Json handleAuthenticatedRequest(HttpServletRequest request, HttpMethod httpMethod, long accountId, Environment environment) throws Exception {
        String finalUrlSegment = BaseServlet.getFinalUrlSegment(request);
        if ("most-types".equals(finalUrlSegment)) {
            if (httpMethod == HttpMethod.GET) {
                return _listMostTypes(environment);
            }
            if (httpMethod == HttpMethod.POST) {
                return _insertType(request, environment);
            }
        }
        if ("primitive-types".equals(finalUrlSegment)) {
            if (httpMethod == HttpMethod.GET) {
                return _listPrimitiveTypes(environment);
            }
        }
        if ("most-units".equals(finalUrlSegment)) {
            if (httpMethod == HttpMethod.GET) {
                return _listUnits(environment);
            }
        }
        else { // not base mostType, must have ID
            final long mostTypeId = Util.parseLong(finalUrlSegment);
            if (mostTypeId < 1) {
                return _generateErrorJson("Invalid Most Type id.");
            }

            if (httpMethod == HttpMethod.POST) {
                return _updateMostType(request, mostTypeId, environment);
            }
        }
        return super._generateErrorJson("Unimplemented HTTP method in request.");
    }

    private Json _listMostTypes(final Environment environment) {
        try (final DatabaseConnection<Connection> databaseConnection = environment.getDatabase().newConnection()) {
            Json response = new Json(false);

            MostTypeInflater mostTypeInflater = new MostTypeInflater(databaseConnection);
            List<MostType> mostTypes = mostTypeInflater.inflateMostTypes();
            Json mostTypesJson = new Json(true);
            for (MostType mostType : mostTypes) {
                Json mostTypeJson = _toJson(mostType);
                mostTypesJson.add(mostTypeJson);
            }
            response.put("mostTypes", mostTypesJson);

            super._setJsonSuccessFields(response);
            return response;
        } catch (DatabaseException e) {
            String msg = "Unable to inflate most types.";
            _logger.error(msg, e);
            return super._generateErrorJson(msg);
        }
    }

    private Json _insertType(HttpServletRequest request, Environment environment) throws IOException {
        Json response = new Json(false);
        final Json jsonRequest = _getRequestDataAsJson(request);

        try {
            DatabaseManager databaseManager = new DatabaseManager(environment.getDatabase());

            final MostType mostType = _populateMostTypeFromJson(jsonRequest);
            databaseManager.insertMostType(mostType);

            response.put("mostTypeId", mostType.getId());
            super._setJsonSuccessFields(response);
        } catch (DatabaseException e) {
            String msg = "Unable to create type.";
            _logger.error(msg, e);
            return super._generateErrorJson(msg);
        }

        return response;
    }

    protected Json _updateMostType(final HttpServletRequest request, final long mostTypeId, Environment environment) throws IOException {
        final Json jsonRequest = _getRequestDataAsJson(request);
        final Json mostTypeJson = jsonRequest.get("mostType");

        try {
            MostType mostType = _populateMostTypeFromJson(mostTypeJson);
            mostType.setId(mostTypeId);

            DatabaseManager databaseManager = new DatabaseManager(environment.getDatabase());
            databaseManager.updateMostType(mostType);

        }
        catch (final Exception exception) {
            final String errorMessage = "Unable to update Most Type: " + exception.getMessage();
            _logger.error(errorMessage, exception);
            return _generateErrorJson(errorMessage);
        }

        final Json response = new Json(false);
        _setJsonSuccessFields(response);
        return response;
    }

    private MostType _populateMostTypeFromJson(final Json jsonRequest) {
        final Long id = jsonRequest.getLong("id");
        final String name = jsonRequest.getString("name");
        final Long primitiveTypeId = jsonRequest.getLong("primitiveTypeId");
        final Boolean isPrimaryType = jsonRequest.getBoolean("isPrimaryType");
        final String bitfieldLength = jsonRequest.getString("bitfieldLength");
        final String enumMax = jsonRequest.getString("enumMax");
        final Long numberBaseTypeId = jsonRequest.getLong("numberBaseTypeId");
        final String numberExponent = jsonRequest.getString("numberExponent");
        final String numberRangeMin = jsonRequest.getString("numberRangeMin");
        final String numberRangeMax = jsonRequest.getString("numberRangeMax");
        final String numberStep = jsonRequest.getString("numberStep");
        final Long numberUnitId = jsonRequest.getLong("numberUnitId");
        final String stringMaxSize = jsonRequest.getString("stringMaxSize");
        final String streamLength = jsonRequest.getString("streamLength");
        final String streamMaxLength = jsonRequest.getString("streamMaxLength");
        final String streamMediaType = jsonRequest.getString("streamMediaType");
        final String arrayName = jsonRequest.getString("arrayName");
        final String arrayDescription = jsonRequest.getString("arrayDescription");
        final Long arrayElementTypeId = jsonRequest.getLong("arrayElementTypeId");
        final String arraySize = jsonRequest.getString("arraySize");
        final String recordName = jsonRequest.getString("recordName");
        final String recordDescription = jsonRequest.getString("recordDescription");
        final String recordSize = jsonRequest.getString("recordSize");

        final PrimitiveType primitiveType = new PrimitiveType();
        primitiveType.setId(primitiveTypeId);

        PrimitiveType numberBaseType = null;
        _logger.info("numberBaseTypeID: " + numberBaseTypeId);
        if (numberBaseTypeId > 0) {
            numberBaseType = new PrimitiveType();
            numberBaseType.setId(numberBaseTypeId);
        }

        MostUnit numberUnit = null;
        if (numberUnitId > 0) {
            numberUnit = new MostUnit();
            numberUnit.setId(numberUnitId);
        }

        MostType arrayElementType = null;
        if (arrayElementTypeId > 0) {
            arrayElementType = new MostType();
            arrayElementType.setId(arrayElementTypeId);
        }

        final MostType mostType = new MostType();
        mostType.setId(id);
        mostType.setName(name);
        mostType.setPrimitiveType(primitiveType);
        mostType.setIsPrimaryType(isPrimaryType);
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

        Json booleanFieldsJson = jsonRequest.get("booleanFields");
        for (int i=0; i<booleanFieldsJson.length(); i++) {
            final BooleanField booleanField = _populateBooleanFieldFromJson(booleanFieldsJson.get(i));
            mostType.addBooleanField(booleanField);
        }

        Json enumValuesJson = jsonRequest.get("enumValues");
        for (int i=0; i<enumValuesJson.length(); i++) {
            final EnumValue enumValue = _populateEnumValueFromJson(enumValuesJson.get(i));
            mostType.addEnumValue(enumValue);
        }

        Json streamCasesJson = jsonRequest.get("streamCases");
        for (int i=0; i<streamCasesJson.length(); i++) {
            final StreamCase streamCase = _populateStreamCaseFromJson(streamCasesJson.get(i));
            mostType.addStreamCase(streamCase);
        }

        Json recordFieldsJson = jsonRequest.get("recordFields");
        for (int i=0; i<recordFieldsJson.length(); i++) {
            final RecordField recordField = _populateRecordFieldFromJson(recordFieldsJson.get(i));
            mostType.addRecordField(recordField);
        }

        return mostType;
    }

    private BooleanField _populateBooleanFieldFromJson(final Json json) {
        final Long id = json.getLong("id");
        final String bitPosition = json.getString("bitPosition");
        final String trueDescription = json.getString("trueDescription");
        final String falseDescription = json.getString("falseDescription");

        final BooleanField booleanField = new BooleanField();
        booleanField.setId(id);
        booleanField.setBitPosition(bitPosition);
        booleanField.setTrueDescription(trueDescription);
        booleanField.setFalseDescription(falseDescription);
        return booleanField;
    }

    private EnumValue _populateEnumValueFromJson(final Json json) {
        final Long id = json.getLong("id");
        final String name = json.getString("name");
        final String code = json.getString("code");

        final EnumValue enumValue = new EnumValue();
        enumValue.setId(id);
        enumValue.setName(name);
        enumValue.setCode(code);
        return enumValue;
    }

    private StreamCase _populateStreamCaseFromJson(final Json json) {
        final Long id = json.getLong("id");
        final String streamPositionX = json.getString("streamPositionX");
        final String streamPositionY = json.getString("streamPositionY");

        final StreamCase streamCase = new StreamCase();
        streamCase.setId(id);
        streamCase.setStreamPositionX(streamPositionX);
        streamCase.setStreamPositionY(streamPositionY);

        final Json streamParametersJson = json.get("streamParameters");
        for (int i=0; i<streamParametersJson.length(); i++) {
            final StreamCaseParameter streamCaseParameter = _populateStreamCaseParameterFromJson(streamParametersJson.get(i));
            streamCase.addStreamCaseParameter(streamCaseParameter);
        }

        final Json streamSignalsJson = json.get("streamSignals");
        for (int i=0; i<streamSignalsJson.length(); i++) {
            final StreamCaseSignal streamCaseSignal = _populateStreamCaseSignalFromJson(streamSignalsJson.get(i));
            streamCase.addStreamCaseSignal(streamCaseSignal);
        }
        return streamCase;
    }

    private StreamCaseParameter _populateStreamCaseParameterFromJson(final Json json) {
        final Long id = json.getLong("id");
        final String parameterName = json.getString("parameterName");
        final String parameterIndex = json.getString("parameterIndex");
        final String parameterDescription = json.getString("parameterDescription");
        final Json parameterTypeJson = json.get("parameterType");
        final Long parameterTypeId = parameterTypeJson.getLong("id");

        final MostType parameterType = new MostType();
        parameterType.setId(parameterTypeId);

        final StreamCaseParameter streamCaseParameter = new StreamCaseParameter();
        streamCaseParameter.setId(id);
        streamCaseParameter.setParameterName(parameterName);
        streamCaseParameter.setParameterIndex(parameterIndex);
        streamCaseParameter.setParameterDescription(parameterDescription);
        streamCaseParameter.setParameterType(parameterType);
        return streamCaseParameter;
    }

    private StreamCaseSignal _populateStreamCaseSignalFromJson(final Json json) {
        final Long id = json.getLong("id");
        final String signalName = json.getString("signalName");
        final String signalIndex = json.getString("signalIndex");
        final String signalDescription = json.getString("signalDescription");
        final String signalBitLength = json.getString("signalBitLength");

        final StreamCaseSignal streamCaseSignal = new StreamCaseSignal();
        streamCaseSignal.setId(id);
        streamCaseSignal.setSignalName(signalName);
        streamCaseSignal.setSignalIndex(signalIndex);
        streamCaseSignal.setSignalDescription(signalDescription);
        streamCaseSignal.setSignalBitLength(signalBitLength);
        return streamCaseSignal;
    }

    private RecordField _populateRecordFieldFromJson(final Json json) {
        final Long id = json.getLong("id");
        final String fieldName = json.getString("fieldName");
        final String fieldIndex = json.getString("fieldIndex");
        final String fieldDescription = json.getString("fieldDescription");
        final Json fieldTypeJson = json.get("fieldType");
        final Long fieldTypeId = fieldTypeJson.getLong("id");

        final MostType fieldType = new MostType();
        fieldType.setId(fieldTypeId);

        final RecordField recordField = new RecordField();
        recordField.setId(id);
        recordField.setFieldName(fieldName);
        recordField.setFieldIndex(fieldIndex);
        recordField.setFieldDescription(fieldDescription);
        recordField.setFieldType(fieldType);
        return recordField;
    }

    private Json _listPrimitiveTypes(Environment environment) {
        try (final DatabaseConnection<Connection> databaseConnection = environment.getDatabase().newConnection()) {
            Json response = new Json(false);

            MostTypeInflater mostTypeInflater = new MostTypeInflater(databaseConnection);
            List<PrimitiveType> primitiveTypes = mostTypeInflater.inflatePrimitiveTypes();
            Json primitiveTypesJson = new Json(true);
            for (PrimitiveType primitiveType : primitiveTypes) {
                Json primitiveTypeJson = _toJson(primitiveType);
                primitiveTypesJson.add(primitiveTypeJson);
            }
            response.put("primitiveTypes", primitiveTypesJson);

            super._setJsonSuccessFields(response);
            return response;
        } catch (DatabaseException e) {
            String msg = "Unable to inflate primitive types.";
            _logger.error(msg, e);
            return super._generateErrorJson(msg);
        }
    }

    private Json _listUnits(final Environment environment) {
        try (final DatabaseConnection<Connection> databaseConnection = environment.getDatabase().newConnection()) {
            Json response = new Json(false);

            MostTypeInflater mostTypeInflater = new MostTypeInflater(databaseConnection);
            List<MostUnit> units = mostTypeInflater.inflateMostUnits();
            Json unitsJson = new Json(true);
            for (MostUnit mostUnit : units) {
                Json primitiveTypeJson = _toJson(mostUnit);
                unitsJson.add(primitiveTypeJson);
            }
            response.put("units", unitsJson);

            super._setJsonSuccessFields(response);
            return response;
        } catch (DatabaseException e) {
            String msg = "Unable to inflate units.";
            _logger.error(msg, e);
            return super._generateErrorJson(msg);
        }
    }

    private Json _toJson(final MostType mostType) {
        if (mostType == null) {
            return null;
        }
        final Json json = new Json(false);

        final Long id = mostType.getId();
        final String name = mostType.getName();
        final boolean isPrimaryType = mostType.isPrimaryType();
        final PrimitiveType primitiveType = mostType.getPrimitiveType();

        json.put("id", id);
        json.put("name", name);
        json.put("isPrimaryType", isPrimaryType);
        json.put("primitiveType", _toJson(primitiveType));

        switch (primitiveType.getName()) {
            case "TBitField": {
                final String bitfieldLength = mostType.getBitfieldLength();
                json.put("bitfieldLength", bitfieldLength);
            } // fall through
            case "TBool": {
                // add boolean fields
                Json booleanFieldsJson = new Json(true);
                for (final BooleanField booleanField : mostType.getBooleanFields()) {
                    Json booleanFieldJson = new Json(false);

                    final Long booleanFieldId = booleanField.getId();
                    final String bitPosition = booleanField.getBitPosition();
                    final String trueDescription = booleanField.getTrueDescription();
                    final String falseDescription = booleanField.getFalseDescription();

                    booleanFieldJson.put("id", booleanFieldId);
                    booleanFieldJson.put("bitPosition", bitPosition);
                    booleanFieldJson.put("trueDescription", trueDescription);
                    booleanFieldJson.put("falseDescription", falseDescription);

                    booleanFieldsJson.add(booleanFieldJson);
                }
                json.put("booleanFields", booleanFieldsJson);
            } break;

            case "TNumber": {
                final PrimitiveType numberBaseType = mostType.getNumberBaseType();
                final String numberExponent = mostType.getNumberExponent();
                final String numberRangeMin = mostType.getNumberRangeMinimum();
                final String numberRangeMax = mostType.getNumberRangeMaximum();
                final String numberStep = mostType.getNumberStep();
                final MostUnit numberUnit = mostType.getNumberUnit();

                json.put("numberBaseType", _toJson(numberBaseType));
                json.put("numberExponent", numberExponent);
                json.put("numberRangeMin", numberRangeMin);
                json.put("numberRangeMax", numberRangeMax);
                json.put("numberStep", numberStep);
                json.put("numberUnit", _toJson(numberUnit));
            } break;

            case "TEnum": {
                final String enumMax = mostType.getEnumMax();
                json.put("enumMax", enumMax);

                // add enum values
                final Json enumValuesJson = new Json(true);
                for (final EnumValue enumValue : mostType.getEnumValues()) {
                    final Json enumValueJson = _toJson(enumValue);
                    enumValuesJson.add(enumValueJson);
                }
                json.put("enumValues", enumValuesJson);
            } break;

            case "TString": {
                final String stringMaxSize = mostType.getStringMaxSize();

                json.put("stringMaxSize", stringMaxSize);
            } break;

            case "TStream": {
                final String streamLength = mostType.getStreamLength();
                json.put("streamLength", streamLength);

                // handle stream cases
                final Json streamCasesJson = new Json(true);
                for (final StreamCase streamCase : mostType.getStreamCases()) {
                    final Json streamCaseJson = _toJson(streamCase);
                    streamCasesJson.add(streamCaseJson);
                }
                json.put("streamCases", streamCasesJson);
            } break;

            case "TCStream": {
                final String streamMaxLength = mostType.getStreamMaxLength();
                final String streamMediaType = mostType.getStreamMediaType();

                json.put("streamMaxLength", streamMaxLength);
                json.put("streamMediaType", streamMediaType);
            } break;

            case "TShortStream": {
                final String streamMaxLength = mostType.getStreamMaxLength();
                json.put("streamMaxLength", streamMaxLength);
            } break;

            case "TArray": {
                final String arrayName = mostType.getArrayName();
                final String arrayDescription = mostType.getArrayDescription();
                final MostType arrayElementType = mostType.getArrayElementType();
                final String arraySize = mostType.getArraySize();

                json.put("arrayName", arrayName);
                json.put("arrayDescription", arrayDescription);
                json.put("arrayElementType", _toJson(arrayElementType));
                json.put("arraySize", arraySize);
            } break;

            case "TRecord": {
                final String recordName = mostType.getRecordName();
                final String recordDescription = mostType.getRecordDescription();
                final String recordSize = mostType.getRecordSize();

                json.put("recordName", recordName);
                json.put("recordDescription", recordDescription);
                json.put("recordSize", recordSize);

                // handle record fields
                final Json recordFieldsJson = new Json(true);
                for (final RecordField recordField : mostType.getRecordFields()) {
                    Json recordFieldJson = _toJson(recordField);
                    recordFieldsJson.add(recordFieldJson);
                }
                json.put("recordFields", recordFieldsJson);
            } break;

            default: {
                // type requires no special data.
            }
        }

        return json;
    }

    private Json _toJson(EnumValue enumValue) {
        final Json enumValueJson = new Json(false);

        final Long enumValueId = enumValue.getId();
        final String enumValueName = enumValue.getName();
        final String enumValueCode = enumValue.getCode();

        enumValueJson.put("id", enumValueId);
        enumValueJson.put("name", enumValueName);
        enumValueJson.put("code", enumValueCode);

        return enumValueJson;
    }

    private Json _toJson(StreamCase streamCase) {
        final Json streamCaseJson = new Json(false);

        final Long streamCaseId = streamCase.getId();
        final String streamPositionX = streamCase.getStreamPositionX();
        final String streamPositionY = streamCase.getStreamPositionY();

        streamCaseJson.put("id", streamCaseId);
        streamCaseJson.put("streamPositionX", streamPositionX);
        streamCaseJson.put("streamPositionY", streamPositionY);

        // stream parameters
        final Json streamParametersJson = new Json(true);
        for (final StreamCaseParameter streamCaseParameter : streamCase.getStreamCaseParameters()) {
            final Json streamParameterJson = new Json(false);

            final Long streamParameterId = streamCaseParameter.getId();
            final String streamParameterName = streamCaseParameter.getParameterName();
            final String streamParameterIndex = streamCaseParameter.getParameterIndex();
            final String streamParameterDescription = streamCaseParameter.getParameterDescription();
            final MostType streamParameterType = streamCaseParameter.getParameterType();

            streamParameterJson.put("id", streamParameterId);
            streamParameterJson.put("parameterName", streamParameterName);
            streamParameterJson.put("parameterIndex", streamParameterIndex);
            streamParameterJson.put("parameterDescription", streamParameterDescription);
            streamParameterJson.put("parameterType", _toJson(streamParameterType));

            streamParametersJson.add(streamParameterJson);
        }
        streamCaseJson.put("streamParameters", streamParametersJson);

        // stream signals
        final Json streamSignalsJson = new Json(true);
        for (final StreamCaseSignal streamCaseSignal : streamCase.getStreamCaseSignals()) {
            final Json streamSignalJson = new Json(false);

            final Long streamSignalId = streamCaseSignal.getId();
            final String streamSignalName = streamCaseSignal.getSignalName();
            final String streamSignalIndex = streamCaseSignal.getSignalIndex();
            final String streamSignalDescription = streamCaseSignal.getSignalDescription();
            final String streamSignalBitLength = streamCaseSignal.getSignalBitLength();

            streamSignalJson.put("id", streamSignalId);
            streamSignalJson.put("signalName", streamSignalName);
            streamSignalJson.put("signalIndex", streamSignalIndex);
            streamSignalJson.put("signalDescription", streamSignalDescription);
            streamSignalJson.put("signalBitLength", streamSignalBitLength);

            streamSignalsJson.add(streamSignalJson);
        }
        streamCaseJson.put("streamSignals", streamSignalsJson);

        return streamCaseJson;
    }

    private Json _toJson(RecordField recordField) {
        final Json recordFieldJson = new Json(false);

        final Long recordFieldId = recordField.getId();
        final String recordFieldName = recordField.getFieldName();
        final String recordFieldIndex = recordField.getFieldIndex();
        final String recordFieldDescription = recordField.getFieldDescription();
        final MostType recordFieldType = recordField.getFieldType();

        recordFieldJson.put("id", recordFieldId);
        recordFieldJson.put("fieldName", recordFieldName);
        recordFieldJson.put("fieldIndex", recordFieldIndex);
        recordFieldJson.put("fieldDescription", recordFieldDescription);
        recordFieldJson.put("fieldType", _toJson(recordFieldType));

        return recordFieldJson;
    }

    public Json _toJson(final MostUnit mostUnit) {
        final Json json = new Json(false);

        final Long id = mostUnit.getId();
        final String referenceName = mostUnit.getReferenceName();
        final String definitionName = mostUnit.getDefinitionName();
        final String definitionCode = mostUnit.getDefinitionCode();
        final String definitionGroup = mostUnit.getDefinitionGroup();

        json.put("id", id);
        json.put("referenceName", referenceName);
        json.put("definitionName", definitionName);
        json.put("definitionCode", definitionCode);
        json.put("definitionGroup", definitionGroup);

        return json;
    }

    private Json _toJson(final PrimitiveType primitiveType) {
        final Json json = new Json(false);

        final long id = primitiveType.getId();
        final String name = primitiveType.getName();
        final boolean isBaseType = primitiveType.isBaseType();
        final boolean isNumberBaseType = primitiveType.isNumberBaseType();
        final boolean isStreamParameterType = primitiveType.isStreamParameterType();
        final boolean isArrayType = primitiveType.isArrayType();
        final boolean isRecordType = primitiveType.isRecordType();

        json.put("id", id);
        json.put("name", name);
        json.put("isBaseType", isBaseType);
        json.put("isNumberBaseType", isNumberBaseType);
        json.put("isStreamParameterType", isStreamParameterType);
        json.put("isArrayType", isArrayType);
        json.put("isRecordType", isRecordType);

        return json;
    }
}
