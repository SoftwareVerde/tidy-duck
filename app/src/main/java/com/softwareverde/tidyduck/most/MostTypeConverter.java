package com.softwareverde.tidyduck.most;

import com.softwareverde.mostadapter.*;
import com.softwareverde.mostadapter.type.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.softwareverde.mostadapter.Operation.OperationType;

public class MostTypeConverter {

    private static final String MOST_NULL = "#NULL#";

    public com.softwareverde.mostadapter.FunctionCatalog convertFunctionCatalog(final FunctionCatalog functionCatalog) {
        com.softwareverde.mostadapter.FunctionCatalog convertedFunctionCatalog = new com.softwareverde.mostadapter.FunctionCatalog();

        String name = functionCatalog.getName();
        String release = functionCatalog.getRelease();
        String author = functionCatalog.getAuthor().getName();
        String company = functionCatalog.getCompany().getName();

        convertedFunctionCatalog.setName(name);
        convertedFunctionCatalog.setRelease(release);
        convertedFunctionCatalog.setAuthor(author);
        convertedFunctionCatalog.setCompany(company);

        for (final Modification modification : functionCatalog.getModifications()) {
            convertedFunctionCatalog.addModification(modification);
        }

        for (final FunctionBlock functionBlock : functionCatalog.getFunctionBlocks()) {
            com.softwareverde.mostadapter.FunctionBlock convertedFunctionBlock = convertFunctionBlock(functionBlock);
            convertedFunctionCatalog.addFunctionBlock(convertedFunctionBlock);
        }

        return convertedFunctionCatalog;
    }

    protected com.softwareverde.mostadapter.FunctionBlock convertFunctionBlock(final FunctionBlock functionBlock) {
        com.softwareverde.mostadapter.FunctionBlock convertedFunctionBlock = new com.softwareverde.mostadapter.FunctionBlock();

        String mostId = functionBlock.getMostId();
        String name = functionBlock.getName();
        String kind = functionBlock.getKind();
        String description = functionBlock.getDescription();
        String release = functionBlock.getRelease();
        String access = functionBlock.getAccess();
        String author = functionBlock.getAuthor().getName();
        String company = functionBlock.getCompany().getName();
        Date lastModifiedDate = functionBlock.getLastModifiedDate();

        convertedFunctionBlock.setMostId(mostId);
        convertedFunctionBlock.setName(name);
        convertedFunctionBlock.setKind(kind);
        convertedFunctionBlock.setDescription(description);
        convertedFunctionBlock.setRelease(release);
        convertedFunctionBlock.setAccess(access);
        convertedFunctionBlock.setAuthor(author);
        convertedFunctionBlock.setCompany(company);
        convertedFunctionBlock.setLastModifiedDate(lastModifiedDate);

        for (final Modification modification : functionBlock.getModifications()) {
            convertedFunctionBlock.addModification(modification);
        }

        for (final MostInterface mostInterface : functionBlock.getMostInterfaces()) {
            for (final MostFunction mostFunction : mostInterface.getMostFunctions()) {
                com.softwareverde.mostadapter.MostFunction convertedMostFunction = convertMostFunction(mostFunction);
                convertedFunctionBlock.addMostFunction(convertedMostFunction);
            }
        }

        return convertedFunctionBlock;
    }

    protected com.softwareverde.mostadapter.MostFunction convertMostFunction(MostFunction mostFunction) {
        com.softwareverde.mostadapter.MostFunction convertedMostFunction = null;

        switch (mostFunction.getFunctionType()) {
            case "Property": {
                Property property = (Property) mostFunction;
                convertedMostFunction = createPropertyFunction(property);
            } break;
            case "Method": {
                Method method = (Method) mostFunction;
                convertedMostFunction = createMethodFunction(method);
            } break;
            default: {
                throw new IllegalArgumentException("Invalid function type: " + mostFunction.getFunctionType());
            }
        }

        String mostId = mostFunction.getMostId();
        String name = mostFunction.getName();
        String description = mostFunction.getDescription();
        String release = mostFunction.getRelease();
        String author = mostFunction.getAuthor().getName();
        String company = mostFunction.getCompany().getName();

        convertedMostFunction.setMostId(mostId);
        convertedMostFunction.setName(name);
        convertedMostFunction.setDescription(description);
        convertedMostFunction.setRelease(release);
        convertedMostFunction.setAuthor(author);
        convertedMostFunction.setCompany(company);

        return convertedMostFunction;
    }

    protected com.softwareverde.mostadapter.MostFunction createPropertyFunction(Property property) {
        com.softwareverde.mostadapter.Property convertedProperty = null;
        switch (property.getReturnType().getName()) {
            case "TBool": {
                SwitchProperty switchProperty = new SwitchProperty();
                convertedProperty = switchProperty;
            } break;
            case "TEnum": {
                EnumProperty enumProperty = new EnumProperty();
                convertedProperty = enumProperty;
            } break;
            case "TUByte":
            case "TSByte":
            case "TUWord":
            case "TSWord":
            case "TULong":
            case "TSLong":
            case "TNumber": {
                NumberProperty numberProperty = new NumberProperty();
                convertedProperty = numberProperty;
            } break;
            case "TString": {
                TextProperty textProperty = new TextProperty();
                convertedProperty = textProperty;
            } break;
            case "TStream":
            case "TShortStream": {
                ContainerProperty containerProperty = new ContainerProperty();
                convertedProperty = containerProperty;
            } break;
            default: {
                UnclassifiedProperty unclassifiedProperty = new UnclassifiedProperty();
                convertedProperty = unclassifiedProperty;
            }
        }

        convertedProperty.setSupportsNotification(property.supportsNotification());

        List<String> operationNames = getOperationNames(property.getOperations());

        // add return type parameter
        MostParameter returnTypeParameter = createReturnTypeParameter(property, operationNames, property.getName(), "1");
        convertedProperty.addMostParameter(returnTypeParameter);

        // add get parameter
        if (operationNames.contains("Get")) {
            MostParameter getParameter = new MostParameter();

            com.softwareverde.mostadapter.type.MostType getType = new VoidType();

            getParameter.setType(getType);
            addOperationAtIndex(getParameter, OperationType.GET, "1");

            convertedProperty.addMostParameter(getParameter);
        }

        // add error parameters
        if (operationNames.contains("Error")) {
            MostParameter errorCodeParameter = new MostParameter();
            errorCodeParameter.setName("ErrorCode");
            errorCodeParameter.setIndex(MOST_NULL);
            errorCodeParameter.setType(createErrorCodeType());
            addOperationAtIndex(errorCodeParameter, OperationType.PROPERTY_ERROR, "1");

            MostParameter errorInfoParameter = new MostParameter();
            errorInfoParameter.setName("ErrorInfo");
            errorInfoParameter.setIndex(MOST_NULL);
            errorInfoParameter.setType(createErrorInfoType());
            addOperationAtIndex(errorInfoParameter, OperationType.PROPERTY_ERROR, "2");

            convertedProperty.addMostParameter(errorCodeParameter);
            convertedProperty.addMostParameter(errorInfoParameter);
        }

        return convertedProperty;
    }

    private MostParameter createReturnTypeParameter(MostFunction mostFunction, List<String> operationNames, String parameterName, String parameterIndex) {

        MostType returnType = mostFunction.getReturnType();

        MostParameter returnTypeParameter = new MostParameter();
        returnTypeParameter.setName(parameterName);
        returnTypeParameter.setIndex(MOST_NULL);
        returnTypeParameter.setType(convertMostType(returnType));

        // Properties
        if (operationNames.contains("Set")) {
            addOperationAtIndex(returnTypeParameter, OperationType.SET, parameterIndex);
        }
        if (operationNames.contains("Status")) {
            addOperationAtIndex(returnTypeParameter, OperationType.STATUS, parameterIndex);
        }
        // Methods
        if (operationNames.contains("ResultAck")) {
            addOperationAtIndex(returnTypeParameter, OperationType.RESULT_ACK, parameterIndex);
        }

        return returnTypeParameter;
    }

    private void addOperationAtIndex(MostParameter mostParameter, OperationType operationType, String parameterIndex) {
        com.softwareverde.mostadapter.Operation operation = new com.softwareverde.mostadapter.Operation();
        operation.setOperationType(operationType);
        operation.setParameterPosition(parameterIndex);
        mostParameter.addOperation(operation);
    }

    protected com.softwareverde.mostadapter.type.MostType convertPrimitiveType(final PrimitiveType primitiveType) {
        com.softwareverde.mostadapter.type.MostType convertedMostType = null;

        switch (primitiveType.getName()) {
            case "TVoid": {
                convertedMostType = new VoidType();
            } break;
            case "TUByte": {
                convertedMostType = new UnsignedByteType();
            } break;
            case "TSByte": {
                convertedMostType = new SignedByteType();
            } break;
            case "TUWord": {
                convertedMostType = new UnsignedWordType();
            } break;
            case "TSWord": {
                convertedMostType = new SignedWordType();
            } break;
            case "TULong": {
                convertedMostType = new UnsignedLongType();
            } break;
            case "TSLong": {
                convertedMostType = new SignedLongType();
            } break;
        }

        return convertedMostType;
    }

    protected com.softwareverde.mostadapter.type.MostType convertMostType(final MostType mostType) {
        com.softwareverde.mostadapter.type.MostType convertedMostType = null;

        switch (mostType.getPrimitiveType().getName()) {
            case "TBool": {
                BoolType boolType = new BoolType();

                for (final BooleanField booleanField : mostType.getBooleanFields()) {
                    BoolField boolField = new BoolField();
                    boolField.setBitPosition(booleanField.getBitPosition());
                    boolField.setTrueDescription(booleanField.getTrueDescription());
                    boolField.setFalseDescription(booleanField.getFalseDescription());

                    boolType.addBoolField(boolField);
                }

                convertedMostType = boolType;
            } break;
            case "TBitField": {
                BitFieldType bitFieldType = new BitFieldType();

                for (final BooleanField booleanField : mostType.getBooleanFields()) {
                    BoolField boolField = new BoolField();
                    boolField.setBitPosition(booleanField.getBitPosition());
                    boolField.setTrueDescription(booleanField.getTrueDescription());
                    boolField.setFalseDescription(booleanField.getFalseDescription());

                    bitFieldType.addBoolField(boolField);
                }

                convertedMostType = bitFieldType;
            } break;
            case "TNumber": {
                NumberType numberType = new NumberType();

                com.softwareverde.mostadapter.type.MostType basisDataType = convertPrimitiveType(mostType.getNumberBaseType());

                numberType.setBasisDataType(basisDataType);
                numberType.setExponent(mostType.getNumberExponent());
                numberType.setRangeMin(mostType.getNumberRangeMinimum());
                numberType.setRangeMax(mostType.getNumberRangeMaximum());
                numberType.setStep(mostType.getNumberStep());
                numberType.setUnit(mostType.getNumberUnit().getReferenceName());

                convertedMostType = numberType;
            } break;
            case "TEnum": {
                EnumType enumType = new EnumType();

                for (final EnumValue enumValue : mostType.getEnumValues()) {
                    com.softwareverde.mostadapter.type.EnumValue mostEnumValue = new com.softwareverde.mostadapter.type.EnumValue();
                    mostEnumValue.setName(enumValue.getName());
                    mostEnumValue.setCode(enumValue.getCode());

                    enumType.addEnumValue(mostEnumValue);
                }

                convertedMostType = enumType;
            } break;
            case "TVoid": {
                convertedMostType = new VoidType();
            } break;
            case "TString": {
                StringType stringType = new StringType();

                if (mostType.getStringMaxSize() != null) {
                    stringType.setMaxSize(mostType.getStringMaxSize());
                }

                convertedMostType = stringType;
            } break;
            case "TStream": {
                StreamType streamType = new StreamType();

                streamType.setLength(mostType.getStreamLength());

                for (final StreamCase streamCase : mostType.getStreamCases()) {
                    final com.softwareverde.mostadapter.type.StreamCase mostStreamCase = new com.softwareverde.mostadapter.type.StreamCase();

                    final PositionDescription positionDescription = new PositionDescription();
                    positionDescription.setPositionX(streamCase.getStreamPositionX());
                    positionDescription.setPositionY(streamCase.getStreamPositionY());

                    mostStreamCase.setPositionDescription(positionDescription);

                    for (final StreamCaseParameter streamCaseParameter : streamCase.getStreamCaseParameters()) {
                        final com.softwareverde.mostadapter.type.MostType parameterType = convertMostType(streamCaseParameter.getParameterType());

                        final StreamParameter streamParameter = new StreamParameter();
                        streamParameter.setName(streamCaseParameter.getParameterName());
                        streamParameter.setIndex(streamCaseParameter.getParameterIndex());
                        streamParameter.setDescription(streamCaseParameter.getParameterDescription());
                        streamParameter.setType(parameterType);

                        mostStreamCase.addStreamParameter(streamParameter);
                    }
                    for (final StreamCaseSignal streamCaseSignal : streamCase.getStreamCaseSignals()) {
                        final StreamSignal streamSignal = new StreamSignal();
                        streamSignal.setName(streamCaseSignal.getSignalName());
                        streamSignal.setIndex(streamCaseSignal.getSignalIndex());
                        streamSignal.setDescription(streamCaseSignal.getSignalDescription());
                        streamSignal.setBitLength(streamCaseSignal.getSignalBitLength());

                        mostStreamCase.addStreamSignal(streamSignal);
                    }
                    streamType.addStreamCase(mostStreamCase);
                }

                convertedMostType = streamType;
            } break;
            case "TCStream": {
                ClassifiedStreamType classifiedStreamType = new ClassifiedStreamType();

                if (mostType.getStreamMaxLength() != null) {
                    classifiedStreamType.setMaxLength(mostType.getStreamMaxLength());
                }
                if (mostType.getStreamMediaType() != null) {
                    classifiedStreamType.setMediaType(mostType.getStreamMediaType());
                }

                convertedMostType = classifiedStreamType;
            } break;
            case "TShortStream": {
                ShortStreamType shortStreamType = new ShortStreamType();

                if (mostType.getStreamMaxLength() != null) {
                    shortStreamType.setMaxLength(mostType.getStreamMaxLength());
                }

                convertedMostType = shortStreamType;
            } break;
            case "TArray": {
                ArrayType arrayType = new ArrayType();

                com.softwareverde.mostadapter.type.MostType arrayElementType = convertMostType(mostType.getArrayElementType());

                arrayType.setName(mostType.getArrayName());
                arrayType.setDescription(mostType.getArrayDescription());
                arrayType.setElementType(arrayElementType);
                arrayType.setMaxSize(mostType.getArraySize());

                convertedMostType = arrayType;
            } break;
            case "TRecord": {
                com.softwareverde.mostadapter.type.RecordType recordType = new com.softwareverde.mostadapter.type.RecordType();

                recordType.setName(mostType.getRecordName());
                recordType.setDescription(mostType.getRecordDescription());

                for (final RecordField recordField : mostType.getRecordFields()) {
                    com.softwareverde.mostadapter.type.MostType recordFieldType = convertMostType(recordField.getFieldType());

                    com.softwareverde.mostadapter.type.RecordField mostRecordField = new com.softwareverde.mostadapter.type.RecordField();
                    mostRecordField.setName(recordField.getFieldName());
                    mostRecordField.setIndex(recordField.getFieldIndex());
                    mostRecordField.setDescription(recordField.getFieldDescription());
                    mostRecordField.setType(recordFieldType);
                    recordType.addRecordField(mostRecordField);
                }

                convertedMostType = recordType;
            } break;
            default: {
                throw new IllegalArgumentException("Invalid most type provided: " + mostType.getName());
            }
        }

        return convertedMostType;
    }

    protected List<String> getOperationNames(final List<Operation> operations) {
        ArrayList<String> operationNames = new ArrayList<>();
        for (Operation operation : operations) {
            operationNames.add(operation.getName());
        }
        return operationNames;
    }

    protected com.softwareverde.mostadapter.MostFunction createMethodFunction(Method method) {
        com.softwareverde.mostadapter.MostFunction convertedMethod = null;

        switch (method.getFunctionStereotype().getName()) {
            case "CommandWithAck": {
                if (method.getInputParameters().size() == 0) {
                    // CommandWithAck with no parameters -> Trigger
                    convertedMethod = new TriggerMethod();
                } else {
                    // CommandWithAck with parameters -> Sequence
                    convertedMethod = new SequenceMethod();
                }
            } break;
            default: {
                // not a trigger or sequence
                convertedMethod = new UnclassifiedMethod();
            }
        }

        List<String> operationNames = getOperationNames(method.getOperations());

        // add sender handle parameter
        MostParameter senderHandleParameter = new MostParameter();

        com.softwareverde.mostadapter.type.MostType senderHandleType = createSenderHandleType();

        senderHandleParameter.setName("SenderHandle");
        senderHandleParameter.setIndex(MOST_NULL);
        senderHandleParameter.setType(senderHandleType);

        if (operationNames.contains("StartResultAck")) {
            addOperationAtIndex(senderHandleParameter, OperationType.START_RESULT_ACK, "1");
        }
        if (operationNames.contains("AbortAck")) {
            addOperationAtIndex(senderHandleParameter, OperationType.ABORT_ACK, "1");
        }
        if (operationNames.contains("ProcessingAck")) {
            addOperationAtIndex(senderHandleParameter, OperationType.PROCESSING_ACK, "1");
        }
        if (operationNames.contains("ErrorAck")) {
            addOperationAtIndex(senderHandleParameter, OperationType.ERROR_ACK, "1");
        }
        convertedMethod.addMostParameter(senderHandleParameter);

        // add return type parameter
        MostParameter returnTypeParameter = createReturnTypeParameter(method, operationNames, "ReturnValue", "2");
        convertedMethod.addMostParameter(returnTypeParameter);

        // add input parameters
        addInputParameterOperations(convertedMethod, method.getInputParameters(), operationNames);

        // add error parameters
        if (operationNames.contains("ErrorAck")) {
            MostParameter errorCodeParameter = new MostParameter();
            errorCodeParameter.setName("ErrorCode");
            errorCodeParameter.setIndex(MOST_NULL);
            errorCodeParameter.setType(createErrorCodeType());
            addOperationAtIndex(errorCodeParameter, OperationType.METHOD_ERROR, "2");

            MostParameter errorInfoParameter = new MostParameter();
            errorInfoParameter.setName("ErrorInfo");
            errorInfoParameter.setIndex(MOST_NULL);
            errorInfoParameter.setType(createErrorInfoType());
            addOperationAtIndex(errorInfoParameter, OperationType.METHOD_ERROR, "3");

            convertedMethod.addMostParameter(errorCodeParameter);
            convertedMethod.addMostParameter(errorInfoParameter);
        }

        return convertedMethod;
    }

    private void addInputParameterOperations(com.softwareverde.mostadapter.MostFunction convertedMethod, List<MostFunctionParameter> inputParameters, List<String> operationNames) {
        if (operationNames.contains("StartResultAck")) {
            for (MostFunctionParameter mostFunctionParameter : inputParameters) {
                MostParameter mostParameter = new MostParameter();

                com.softwareverde.mostadapter.type.MostType parameterType = convertMostType(mostFunctionParameter.getMostType());
                // add one to parameter index because of sender handle parameter
                int parameterIndex = mostFunctionParameter.getParameterIndex()+1;
                String parameterIndexString = Integer.toString(parameterIndex);

                // TODO: add parameter names/description
                mostParameter.setIndex(MOST_NULL);
                mostParameter.setType(parameterType);
                addOperationAtIndex(mostParameter, OperationType.START_RESULT_ACK, parameterIndexString);

                convertedMethod.addMostParameter(mostParameter);
            }
        }
    }

    protected com.softwareverde.mostadapter.type.MostType createErrorInfoType() {
        StreamType errorInfo = new StreamType();

        com.softwareverde.mostadapter.type.StreamCase streamCase = new com.softwareverde.mostadapter.type.StreamCase();
        PositionDescription positionDescription = new PositionDescription();
        positionDescription.setPositionX(PositionDescription.NULL);
        positionDescription.setPositionY(PositionDescription.NULL);

        streamCase.setPositionDescription(positionDescription);

        return errorInfo;
    }

    protected com.softwareverde.mostadapter.type.MostType createErrorCodeType() {
        EnumType errorCode = new EnumType();

        errorCode.setEnumMax("67");

        com.softwareverde.mostadapter.type.EnumValue value1 = new com.softwareverde.mostadapter.type.EnumValue("0x1", "FBlockIdNotAvailable");
        com.softwareverde.mostadapter.type.EnumValue value3 = new com.softwareverde.mostadapter.type.EnumValue("0x3", "FunctionIdNotAvailable");
        com.softwareverde.mostadapter.type.EnumValue value4 = new com.softwareverde.mostadapter.type.EnumValue("0x4", "OpTypeNotAvailable");
        com.softwareverde.mostadapter.type.EnumValue value5 = new com.softwareverde.mostadapter.type.EnumValue("0x5", "InvalidLength");
        com.softwareverde.mostadapter.type.EnumValue value6 = new com.softwareverde.mostadapter.type.EnumValue("0x6", "WrongParameter");
        com.softwareverde.mostadapter.type.EnumValue value7 = new com.softwareverde.mostadapter.type.EnumValue("0x7", "ParameterNotAvailable");
        com.softwareverde.mostadapter.type.EnumValue valueB = new com.softwareverde.mostadapter.type.EnumValue("0xB", "DeviceMalfunction");
        com.softwareverde.mostadapter.type.EnumValue valueC = new com.softwareverde.mostadapter.type.EnumValue("0xC", "SegmentationError");
        com.softwareverde.mostadapter.type.EnumValue value40 = new com.softwareverde.mostadapter.type.EnumValue("0x40", "Busy");
        com.softwareverde.mostadapter.type.EnumValue value41 = new com.softwareverde.mostadapter.type.EnumValue("0x41", "FunctionTemporaryNotAvailable");
        com.softwareverde.mostadapter.type.EnumValue value42 = new com.softwareverde.mostadapter.type.EnumValue("0x42", "ProcessingError");
        com.softwareverde.mostadapter.type.EnumValue value43 = new com.softwareverde.mostadapter.type.EnumValue("0x43", "MethodAborted");
        com.softwareverde.mostadapter.type.EnumValue valueC0 = new com.softwareverde.mostadapter.type.EnumValue("0xC0", "FunctionSignatureInvalid");
        com.softwareverde.mostadapter.type.EnumValue valueC1 = new com.softwareverde.mostadapter.type.EnumValue("0xC1", "FunctionNotImplemented");
        com.softwareverde.mostadapter.type.EnumValue valueC2 = new com.softwareverde.mostadapter.type.EnumValue("0xC2", "InsufficientAccess");

        errorCode.addEnumValue(value1);
        errorCode.addEnumValue(value3);
        errorCode.addEnumValue(value4);
        errorCode.addEnumValue(value5);
        errorCode.addEnumValue(value6);
        errorCode.addEnumValue(value7);
        errorCode.addEnumValue(valueB);
        errorCode.addEnumValue(valueC);
        errorCode.addEnumValue(value40);
        errorCode.addEnumValue(value41);
        errorCode.addEnumValue(value42);
        errorCode.addEnumValue(value43);
        errorCode.addEnumValue(valueC0);
        errorCode.addEnumValue(valueC1);
        errorCode.addEnumValue(valueC2);

        return errorCode;
    }

    protected com.softwareverde.mostadapter.type.MostType createSenderHandleType() {
        NumberType senderHandle = new NumberType();

        UnsignedWordType basisDateType = new UnsignedWordType();
        String exponent = "0";
        String rangeMin = "0";
        String rangeMax = "65535";
        String step = "1";
        String unit = "unit_none";

        senderHandle.setBasisDataType(basisDateType);
        senderHandle.setExponent(exponent);
        senderHandle.setRangeMin(rangeMin);
        senderHandle.setRangeMax(rangeMax);
        senderHandle.setStep(step);
        senderHandle.setUnit(unit);

        return senderHandle;
    }
}
