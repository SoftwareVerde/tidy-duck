package com.softwareverde.tidyduck.api;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.json.Json;
import com.softwareverde.tidyduck.database.MostTypeInflater;
import com.softwareverde.tidyduck.environment.Environment;
import com.softwareverde.tidyduck.most.*;
import com.softwareverde.tomcat.servlet.AuthenticatedJsonServlet;
import com.softwareverde.tomcat.servlet.BaseServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
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

    private Json _insertType(HttpServletRequest request, Environment environment) {
        Json response = new Json(false);

        try (final DatabaseConnection<Connection> databaseConnection = environment.getDatabase().newConnection()) {
            // TODO: implement

            super._setJsonSuccessFields(response);
        } catch (DatabaseException e) {
            String msg = "Unable to inflate most types.";
            _logger.error(msg, e);
            return super._generateErrorJson(msg);
        }

        return response;
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

    private Json _toJson(final MostType mostType) {
        final Json json = new Json(false);

        final Long id = mostType.getId();
        final String name = mostType.getName();
        final boolean isPrimaryType = mostType.isPrimaryType();
        final Long primitiveTypeId = mostType.getPrimitiveType().getId();
        final String primitiveTypeName = mostType.getPrimitiveType().getName();

        json.put("id", id);
        json.put("name", name);
        json.put("isPrimaryType", isPrimaryType);
        json.put("primitiveTypeId", primitiveTypeId);
        json.put("primitiveTypeName", primitiveTypeName);

        switch (primitiveTypeName) {
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
                final MostType numberBaseType = mostType.getNumberBaseType();
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
        final boolean isPreloadedType = primitiveType.isPreloadedType();
        final boolean isNumberBaseType = primitiveType.isNumberBaseType();
        final boolean isStreamParameterType = primitiveType.isStreamParameterType();
        final boolean isArrayType = primitiveType.isArrayType();
        final boolean isRecordType = primitiveType.isRecordType();

        json.put("id", id);
        json.put("name", name);
        json.put("isPreloadedType", isPreloadedType);
        json.put("isNumberBaseType", isNumberBaseType);
        json.put("isStreamParameterType", isStreamParameterType);
        json.put("isArrayType", isArrayType);
        json.put("isRecordType", isRecordType);

        return json;
    }
}
