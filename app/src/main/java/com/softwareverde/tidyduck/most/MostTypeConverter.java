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
        MostParameter returnTypeParameter = createReturnTypeParameter(property, operationNames, "1");
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

    private MostParameter createReturnTypeParameter(MostFunction mostFunction, List<String> operationNames, String parameterIndex) {

        MostType returnType = mostFunction.getReturnType();

        MostParameter returnTypeParameter = new MostParameter();
        returnTypeParameter.setName("ReturnValue"); // TODO: name should be passed in?
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

    protected com.softwareverde.mostadapter.type.MostType convertMostType(MostType mostType) {
        com.softwareverde.mostadapter.type.MostType convertedMostType = null;

        switch (mostType.getName()) {
            case "TBool": {
                BoolType boolType = new BoolType();

                // TODO: allow other bool fields
                BoolField boolField = new BoolField();
                boolField.setBitPosition("0");
                boolField.setTrueDescription("True.");
                boolField.setFalseDescription("False.");
                boolType.addBoolField(boolField);

                convertedMostType = boolType;
            } break;
            case "TBitField": {
                BitFieldType bitFieldType = new BitFieldType();

                // TODO: allow other bool fields
                BoolField boolField = new BoolField();
                boolField.setBitPosition("0");
                boolField.setTrueDescription("On.");
                boolField.setFalseDescription("Off.");
                bitFieldType.addBoolField(boolField);

                convertedMostType = bitFieldType;
            } break;
            case "TEnum": {
                EnumType enumType = new EnumType();

                // TODO: populate with supplied enum values
                EnumValue enumValue = new EnumValue("0x01", "Test Value");
                enumType.addEnumValue(enumValue);

                convertedMostType = enumType;
            } break;
            case "TVoid": {
                convertedMostType = new VoidType();
            }
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
            case "TString": {
                StringType stringType = new StringType();

                // TODO: optionally set max size

                convertedMostType = stringType;
            } break;
            case "TStream": {
                StreamType streamType = new StreamType();

                // TODO: add stream cases

                convertedMostType = streamType;
            } break;
            case "TCStream": {
                ClassifiedStreamType classifiedStreamType = new ClassifiedStreamType();

                // TODO: optionally set max length and media type

                convertedMostType = classifiedStreamType;
            } break;
            case "TShortStream": {
                ShortStreamType shortStreamType = new ShortStreamType();

                // TODO: optionally set max length

                convertedMostType = shortStreamType;
            } break;
            case "TArray": {
                ArrayType arrayType = new ArrayType();

                // TODO: populate with supplied values
                arrayType.setName("Test Array");
                arrayType.setDescription("Test array of unsigned long.");
                arrayType.setElementType(new UnsignedLongType());

                convertedMostType = arrayType;
            } break;
            case "TRecord": {
                RecordType recordType = new RecordType();

                // TODO: populate with supplied values
                recordType.setName("Test Record");
                recordType.setDescription("Test Record with two elements.");

                RecordField recordField1 = new RecordField();
                recordField1.setName("Field 1");
                recordField1.setDescription("First field in test record.");
                recordField1.setIndex("1");
                recordField1.setType(new UnsignedLongType());

                RecordField recordField2 = new RecordField();
                recordField2.setName("Field 2");
                recordField2.setDescription("Second field in test record.");
                recordField2.setIndex("2");
                recordField2.setType(new StringType());

                recordType.addRecordField(recordField1);
                recordType.addRecordField(recordField2);

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
        MostParameter returnTypeParameter = createReturnTypeParameter(method, operationNames, "2");
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

        StreamCase streamCase = new StreamCase();
        PositionDescription positionDescription = new PositionDescription();
        positionDescription.setPositionX(PositionDescription.NULL);
        positionDescription.setPositionY(PositionDescription.NULL);

        streamCase.setPositionDescription(positionDescription);

        return errorInfo;
    }

    protected com.softwareverde.mostadapter.type.MostType createErrorCodeType() {
        EnumType errorCode = new EnumType();

        errorCode.setEnumMax("67");

        EnumValue value1 = new EnumValue("0x1", "FBlockIdNotAvailable");
        EnumValue value3 = new EnumValue("0x3", "FunctionIdNotAvailable");
        EnumValue value4 = new EnumValue("0x4", "OpTypeNotAvailable");
        EnumValue value5 = new EnumValue("0x5", "InvalidLength");
        EnumValue value6 = new EnumValue("0x6", "WrongParameter");
        EnumValue value7 = new EnumValue("0x7", "ParameterNotAvailable");
        EnumValue valueB = new EnumValue("0xB", "DeviceMalfunction");
        EnumValue valueC = new EnumValue("0xC", "SegmentationError");
        EnumValue value40 = new EnumValue("0x40", "Busy");
        EnumValue value41 = new EnumValue("0x41", "FunctionTemporaryNotAvailable");
        EnumValue value42 = new EnumValue("0x42", "ProcessingError");
        EnumValue value43 = new EnumValue("0x43", "MethodAborted");
        EnumValue valueC0 = new EnumValue("0xC0", "FunctionSignatureInvalid");
        EnumValue valueC1 = new EnumValue("0xC1", "FunctionNotImplemented");
        EnumValue valueC2 = new EnumValue("0xC2", "InsufficientAccess");

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
