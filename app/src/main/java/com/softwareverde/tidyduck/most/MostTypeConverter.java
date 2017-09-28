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
        final com.softwareverde.mostadapter.FunctionCatalog convertedFunctionCatalog = new com.softwareverde.mostadapter.FunctionCatalog();

        final String name = functionCatalog.getName();
        final String release = functionCatalog.getRelease();
        final String author = functionCatalog.getAuthor().getName();
        final String company = functionCatalog.getCompany().getName();

        convertedFunctionCatalog.setName(name);
        convertedFunctionCatalog.setRelease(release);
        convertedFunctionCatalog.setAuthor(author);
        convertedFunctionCatalog.setCompany(company);

        for (final Modification modification : functionCatalog.getModifications()) {
            convertedFunctionCatalog.addModification(modification);
        }

        for (final FunctionBlock functionBlock : functionCatalog.getFunctionBlocks()) {
            final com.softwareverde.mostadapter.FunctionBlock convertedFunctionBlock = _convertFunctionBlock(functionBlock);
            convertedFunctionCatalog.addFunctionBlock(convertedFunctionBlock);
        }

        for (final ClassDefinition classDefinition : functionCatalog.getClassDefinitions()) {
            final com.softwareverde.mostadapter.ClassDefinition convertedClassDefinition = _convertClassDefinition(classDefinition);
            convertedFunctionCatalog.addClassDefinition(convertedClassDefinition);
        }

        for (final PropertyCommandDefinition commandDefinition : functionCatalog.getPropertyCommandDefinitions()) {
            final com.softwareverde.mostadapter.PropertyCommandDefinition convertedCommandDefinition = _convertPropertyCommandDefinition(commandDefinition);
            convertedFunctionCatalog.addPropertyCommandDefinition(convertedCommandDefinition);
        }

        for (final MethodCommandDefinition commandDefinition : functionCatalog.getMethodCommandDefinitions()) {
            final com.softwareverde.mostadapter.MethodCommandDefinition convertedCommandDefinition = _convertMethodCommandDefinition(commandDefinition);
            convertedFunctionCatalog.addMethodCommandDefinition(convertedCommandDefinition);
        }

        for (final PropertyReportDefinition commandDefinition : functionCatalog.getPropertyReportDefinitions()) {
            final com.softwareverde.mostadapter.PropertyReportDefinition convertedReportDefinition = _convertPropertyReportDefinition(commandDefinition);
            convertedFunctionCatalog.addPropertyReportDefinition(convertedReportDefinition);
        }

        for (final MethodReportDefinition commandDefinition : functionCatalog.getMethodReportDefinitions()) {
            final com.softwareverde.mostadapter.MethodReportDefinition convertedReportDefinition = _convertMethodReportDefinition(commandDefinition);
            convertedFunctionCatalog.addMethodReportDefinition(convertedReportDefinition);
        }

        for (final TypeDefinition typeDefinition : functionCatalog.getTypeDefinitions()) {
            final com.softwareverde.mostadapter.TypeDefinition convertedTypeDefinition = _convertTypeDefinition(typeDefinition);
            convertedFunctionCatalog.addTypeDefinition(convertedTypeDefinition);
        }

        for (final UnitDefinition unitDefinition : functionCatalog.getUnitDefinitions()) {
            final com.softwareverde.mostadapter.UnitDefinition convertedUnitDefinition = _convertUnitDefinition(unitDefinition);
            convertedFunctionCatalog.addUnitDefinition(convertedUnitDefinition);
        }

        for (final ErrorDefinition errorDefinition : functionCatalog.getErrorDefinitions()) {
            final com.softwareverde.mostadapter.ErrorDefinition convertedErrorDefinition = _convertErrorDefinition(errorDefinition);
            convertedFunctionCatalog.addErrorDefinition(convertedErrorDefinition);
        }

        return convertedFunctionCatalog;
    }

    private com.softwareverde.mostadapter.ClassDefinition _convertClassDefinition(final ClassDefinition classDefinition) {
        return new com.softwareverde.mostadapter.ClassDefinition(
                classDefinition.getClassId(),
                classDefinition.getClassName(),
                classDefinition.getClassDescription()
        );
    }

    private com.softwareverde.mostadapter.PropertyCommandDefinition _convertPropertyCommandDefinition(final PropertyCommandDefinition commandDefinition) {
        return new com.softwareverde.mostadapter.PropertyCommandDefinition(
            commandDefinition.getCommandId(),
            commandDefinition.getCommandOperationType(),
            commandDefinition.getCommandName(),
            commandDefinition.getCommandDescription()
        );
    }

    private com.softwareverde.mostadapter.MethodCommandDefinition _convertMethodCommandDefinition(final MethodCommandDefinition commandDefinition) {
        return new com.softwareverde.mostadapter.MethodCommandDefinition(
            commandDefinition.getCommandId(),
            commandDefinition.getCommandOperationType(),
            commandDefinition.getCommandName(),
            commandDefinition.getCommandDescription()
        );
    }

    private com.softwareverde.mostadapter.PropertyReportDefinition _convertPropertyReportDefinition(final PropertyReportDefinition commandDefinition) {
        return new com.softwareverde.mostadapter.PropertyReportDefinition(
            commandDefinition.getReportId(),
            commandDefinition.getReportOperationType(),
            commandDefinition.getReportName(),
            commandDefinition.getReportDescription()
        );
    }

    private com.softwareverde.mostadapter.MethodReportDefinition _convertMethodReportDefinition(final MethodReportDefinition commandDefinition) {
        return new com.softwareverde.mostadapter.MethodReportDefinition(
            commandDefinition.getReportId(),
            commandDefinition.getReportOperationType(),
            commandDefinition.getReportName(),
            commandDefinition.getReportDescription()
        );
    }

    private com.softwareverde.mostadapter.TypeDefinition _convertTypeDefinition(final TypeDefinition typeDefinition) {
        return new com.softwareverde.mostadapter.TypeDefinition(
            typeDefinition.getTypeId(),
            typeDefinition.getTypeName(),
            typeDefinition.getTypeSize(),
            typeDefinition.getTypeDescription()
        );
    }

    private com.softwareverde.mostadapter.UnitDefinition _convertUnitDefinition(final UnitDefinition unitDefinition) {
        return new com.softwareverde.mostadapter.UnitDefinition(
                unitDefinition.getUnitId(),
                unitDefinition.getUnitName(),
                unitDefinition.getUnitCode(),
                unitDefinition.getUnitGroup()
        );
    }

    private com.softwareverde.mostadapter.ErrorDefinition _convertErrorDefinition(final ErrorDefinition errorDefinition) {
        return new com.softwareverde.mostadapter.ErrorDefinition(
            errorDefinition.getErrorId(),
            errorDefinition.getErrorCode(),
            errorDefinition.getErrorDescription(),
            errorDefinition.getInfo(),
            errorDefinition.getInfoDescription()
        );
    }

    protected com.softwareverde.mostadapter.FunctionBlock _convertFunctionBlock(final FunctionBlock functionBlock) {
        final com.softwareverde.mostadapter.FunctionBlock convertedFunctionBlock = new com.softwareverde.mostadapter.FunctionBlock();

        final String mostId = functionBlock.getMostId();
        final String name = functionBlock.getName();
        final String kind = functionBlock.getKind();
        final String description = functionBlock.getDescription();
        final String release = functionBlock.getRelease();
        final String access = functionBlock.getAccess();
        final String author = functionBlock.getAuthor().getName();
        final String company = functionBlock.getCompany().getName();
        final Date lastModifiedDate = functionBlock.getLastModifiedDate();

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
                final com.softwareverde.mostadapter.MostFunction convertedMostFunction = _convertMostFunction(mostFunction);
                convertedFunctionBlock.addMostFunction(convertedMostFunction);
            }
        }

        return convertedFunctionBlock;
    }

    protected com.softwareverde.mostadapter.MostFunction _convertMostFunction(MostFunction mostFunction) {
        com.softwareverde.mostadapter.MostFunction convertedMostFunction = null;

        switch (mostFunction.getFunctionType()) {
            case "Property": {
                Property property = (Property) mostFunction;
                convertedMostFunction = _createPropertyFunction(property);
            } break;
            case "Method": {
                Method method = (Method) mostFunction;
                convertedMostFunction = _createMethodFunction(method);
            } break;
            default: {
                throw new IllegalArgumentException("Invalid function type: " + mostFunction.getFunctionType());
            }
        }

        final String mostId = mostFunction.getMostId();
        final String name = mostFunction.getName();
        final String description = mostFunction.getDescription();
        final String release = mostFunction.getRelease();
        final String author = mostFunction.getAuthor().getName();
        final String company = mostFunction.getCompany().getName();

        convertedMostFunction.setMostId(mostId);
        convertedMostFunction.setName(name);
        convertedMostFunction.setDescription(description);
        convertedMostFunction.setRelease(release);
        convertedMostFunction.setAuthor(author);
        convertedMostFunction.setCompany(company);

        return convertedMostFunction;
    }

    protected com.softwareverde.mostadapter.MostFunction _createPropertyFunction(Property property) {
        com.softwareverde.mostadapter.Property convertedProperty = null;
        switch (property.getReturnType().getName()) {
            case "TBool": {
                final SwitchProperty switchProperty = new SwitchProperty();
                convertedProperty = switchProperty;
            } break;
            case "TEnum": {
                final EnumProperty enumProperty = new EnumProperty();
                convertedProperty = enumProperty;
            } break;
            case "TNumber": {
                final NumberProperty numberProperty = new NumberProperty();
                convertedProperty = numberProperty;
            } break;
            case "TString": {
                final TextProperty textProperty = new TextProperty();
                convertedProperty = textProperty;
            } break;
            case "TCStream": {
                final ContainerProperty containerProperty = new ContainerProperty();
                convertedProperty = containerProperty;
            } break;
            // commented because these are not used
//            case "TArray": {
//                final ArrayProperty arrayProperty =  new ArrayProperty();
//                convertedProperty = arrayProperty;
//            } break;
//            case "TRecord": {
//                final RecordProperty recordProperty = new RecordProperty();
//                convertedProperty = recordProperty;
//            } break;
            case "TShortStream": {
                final SequenceProperty sequenceProperty = new SequenceProperty();
                convertedProperty = sequenceProperty;
            } break;
            default: {
                final UnclassifiedProperty unclassifiedProperty = new UnclassifiedProperty();
                convertedProperty = unclassifiedProperty;
            }
        }

        convertedProperty.setSupportsNotification(property.supportsNotification());

        final List<String> operationNames = _getOperationNames(property.getOperations());

        // add return type parameter
        final MostParameter returnTypeParameter = _createReturnTypeParameter(property, operationNames, "1");
        convertedProperty.addMostParameter(returnTypeParameter);

        // add get parameter
        if (operationNames.contains("Get")) {
            final MostParameter getParameter = new MostParameter();

            final com.softwareverde.mostadapter.type.MostType getType = new VoidType();

            getParameter.setType(getType);
            _addOperationAtIndex(getParameter, OperationType.GET, "1");

            convertedProperty.addMostParameter(getParameter);
        }

        // add error parameters
        if (operationNames.contains("Error")) {
            final MostParameter errorCodeParameter = new MostParameter();
            errorCodeParameter.setName("ErrorCode");
            errorCodeParameter.setIndex(MOST_NULL);
            errorCodeParameter.setType(_createErrorCodeType());
            _addOperationAtIndex(errorCodeParameter, OperationType.PROPERTY_ERROR, "1");

            final MostParameter errorInfoParameter = new MostParameter();
            errorInfoParameter.setName("ErrorInfo");
            errorInfoParameter.setIndex(MOST_NULL);
            errorInfoParameter.setType(_createErrorInfoType());
            _addOperationAtIndex(errorInfoParameter, OperationType.PROPERTY_ERROR, "2");

            convertedProperty.addMostParameter(errorCodeParameter);
            convertedProperty.addMostParameter(errorInfoParameter);
        }

        return convertedProperty;
    }

    private MostParameter _createReturnTypeParameter(MostFunction mostFunction, List<String> operationNames, String parameterIndex) {

        final MostType returnType = mostFunction.getReturnType();

        final MostParameter returnTypeParameter = new MostParameter();
        returnTypeParameter.setName(mostFunction.getReturnParameterName());
        returnTypeParameter.setDescription(mostFunction.getReturnParameterDescription());
        returnTypeParameter.setIndex(MOST_NULL);
        returnTypeParameter.setType(_convertMostType(returnType));

        // Properties
        if (operationNames.contains("Set")) {
            _addOperationAtIndex(returnTypeParameter, OperationType.SET, parameterIndex);
        }
        if (operationNames.contains("Status")) {
            _addOperationAtIndex(returnTypeParameter, OperationType.STATUS, parameterIndex);
        }
        // Methods
        if (operationNames.contains("ResultAck")) {
            _addOperationAtIndex(returnTypeParameter, OperationType.RESULT_ACK, parameterIndex);
        }

        return returnTypeParameter;
    }

    private void _addOperationAtIndex(MostParameter mostParameter, OperationType operationType, String parameterIndex) {
        final com.softwareverde.mostadapter.Operation operation = new com.softwareverde.mostadapter.Operation();
        operation.setOperationType(operationType);
        operation.setParameterPosition(parameterIndex);
        mostParameter.addOperation(operation);
    }

    protected com.softwareverde.mostadapter.type.MostType _convertPrimitiveType(final PrimitiveType primitiveType) {
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

    protected com.softwareverde.mostadapter.type.MostType _convertMostType(final MostType mostType) {
        com.softwareverde.mostadapter.type.MostType convertedMostType = null;

        switch (mostType.getPrimitiveType().getName()) {
            case "TBool": {
                final BoolType boolType = new BoolType();

                for (final BooleanField booleanField : mostType.getBooleanFields()) {
                    final BoolField boolField = new BoolField();
                    boolField.setBitPosition(booleanField.getBitPosition());
                    boolField.setTrueDescription(booleanField.getTrueDescription());
                    boolField.setFalseDescription(booleanField.getFalseDescription());

                    boolType.addBoolField(boolField);
                }

                convertedMostType = boolType;
            } break;
            case "TBitField": {
                final BitFieldType bitFieldType = new BitFieldType();

                for (final BooleanField booleanField : mostType.getBooleanFields()) {
                    final BoolField boolField = new BoolField();
                    boolField.setBitPosition(booleanField.getBitPosition());
                    boolField.setTrueDescription(booleanField.getTrueDescription());
                    boolField.setFalseDescription(booleanField.getFalseDescription());

                    bitFieldType.addBoolField(boolField);
                }

                convertedMostType = bitFieldType;
            } break;
            case "TNumber": {
                final NumberType numberType = new NumberType();

                final com.softwareverde.mostadapter.type.MostType basisDataType = _convertPrimitiveType(mostType.getNumberBaseType());

                numberType.setBasisDataType(basisDataType);
                numberType.setExponent(mostType.getNumberExponent());
                numberType.setRangeMin(mostType.getNumberRangeMinimum());
                numberType.setRangeMax(mostType.getNumberRangeMaximum());
                numberType.setStep(mostType.getNumberStep());
                numberType.setUnit(mostType.getNumberUnit().getReferenceName());

                convertedMostType = numberType;
            } break;
            case "TEnum": {
                final EnumType enumType = new EnumType();

                enumType.setEnumMax(mostType.getEnumMax());

                for (final EnumValue enumValue : mostType.getEnumValues()) {
                    final com.softwareverde.mostadapter.type.EnumValue mostEnumValue = new com.softwareverde.mostadapter.type.EnumValue();
                    mostEnumValue.setName(enumValue.getName());
                    mostEnumValue.setCode(enumValue.getCode());
                    mostEnumValue.setDescription(enumValue.getDescription());

                    enumType.addEnumValue(mostEnumValue);
                }

                convertedMostType = enumType;
            } break;
            case "TVoid": {
                convertedMostType = new VoidType();
            } break;
            case "TString": {
                final StringType stringType = new StringType();

                if (mostType.getStringMaxSize() != null) {
                    stringType.setMaxSize(mostType.getStringMaxSize());
                }

                convertedMostType = stringType;
            } break;
            case "TStream": {
                final StreamType streamType = new StreamType();

                streamType.setLength(mostType.getStreamLength());

                for (final StreamCase streamCase : mostType.getStreamCases()) {
                    final com.softwareverde.mostadapter.type.StreamCase mostStreamCase = new com.softwareverde.mostadapter.type.StreamCase();

                    final PositionDescription positionDescription = new PositionDescription();
                    positionDescription.setPositionX(streamCase.getStreamPositionX());
                    positionDescription.setPositionY(streamCase.getStreamPositionY());

                    mostStreamCase.setPositionDescription(positionDescription);

                    for (final StreamCaseParameter streamCaseParameter : streamCase.getStreamCaseParameters()) {
                        final com.softwareverde.mostadapter.type.MostType parameterType = _convertMostType(streamCaseParameter.getParameterType());

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
                final ClassifiedStreamType classifiedStreamType = new ClassifiedStreamType();

                if (mostType.getStreamMaxLength() != null) {
                    classifiedStreamType.setMaxLength(mostType.getStreamMaxLength());
                }
                if (mostType.getStreamMediaType() != null) {
                    classifiedStreamType.setMediaType(mostType.getStreamMediaType());
                }

                convertedMostType = classifiedStreamType;
            } break;
            case "TShortStream": {
                final ShortStreamType shortStreamType = new ShortStreamType();

                if (mostType.getStreamMaxLength() != null) {
                    shortStreamType.setMaxLength(mostType.getStreamMaxLength());
                }

                convertedMostType = shortStreamType;
            } break;
            case "TArray": {
                final ArrayType arrayType = new ArrayType();

                com.softwareverde.mostadapter.type.MostType arrayElementType = _convertMostType(mostType.getArrayElementType());

                arrayType.setName(mostType.getArrayName());
                arrayType.setDescription(mostType.getArrayDescription());
                arrayType.setElementType(arrayElementType);
                arrayType.setMaxSize(mostType.getArraySize());

                convertedMostType = arrayType;
            } break;
            case "TRecord": {
                final com.softwareverde.mostadapter.type.RecordType recordType = new com.softwareverde.mostadapter.type.RecordType();

                recordType.setName(mostType.getRecordName());
                recordType.setDescription(mostType.getRecordDescription());

                for (final RecordField recordField : mostType.getRecordFields()) {
                    final com.softwareverde.mostadapter.type.MostType recordFieldType = _convertMostType(recordField.getFieldType());

                    final com.softwareverde.mostadapter.type.RecordField mostRecordField = new com.softwareverde.mostadapter.type.RecordField();
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

    protected List<String> _getOperationNames(final List<Operation> operations) {
        final ArrayList<String> operationNames = new ArrayList<>();
        for (final Operation operation : operations) {
            operationNames.add(operation.getName());
        }
        return operationNames;
    }

    protected com.softwareverde.mostadapter.MostFunction _createMethodFunction(Method method) {
        com.softwareverde.mostadapter.MostFunction convertedMethod = null;

        switch (method.getFunctionStereotype().getName()) {
            case "CommandWithAck": {
                if (method.getInputParameters().size() == 0) {
                    // CommandWithAck with no parameters -> Trigger
                    convertedMethod = new TriggerMethod();
                }
                else {
                    if (_isSequenceMethod(method)) {
                        convertedMethod = new SequenceMethod();
                    } else {
                        convertedMethod = new UnclassifiedMethod();
                    }
                }
            } break;
            default: {
                // not a trigger or sequence
                convertedMethod = new UnclassifiedMethod();
            }
        }

        final List<String> operationNames = _getOperationNames(method.getOperations());

        // add sender handle parameter
        final MostParameter senderHandleParameter = new MostParameter();

        final com.softwareverde.mostadapter.type.MostType senderHandleType = _createSenderHandleType();

        senderHandleParameter.setName("SenderHandle");
        senderHandleParameter.setIndex(MOST_NULL);
        senderHandleParameter.setType(senderHandleType);

        if (operationNames.contains("AbortAck")) {
            _addOperationAtIndex(senderHandleParameter, OperationType.ABORT_ACK, "1");
        }
        if (operationNames.contains("StartResultAck")) {
            _addOperationAtIndex(senderHandleParameter, OperationType.START_RESULT_ACK, "1");
        }
        if (operationNames.contains("ResultAck")) {
            _addOperationAtIndex(senderHandleParameter, OperationType.RESULT_ACK, "1");
        }
        if (operationNames.contains("ErrorAck")) {
            _addOperationAtIndex(senderHandleParameter, OperationType.ERROR_ACK, "1");
        }
        if (operationNames.contains("ProcessingAck")) {
            _addOperationAtIndex(senderHandleParameter, OperationType.PROCESSING_ACK, "1");
        }
        convertedMethod.addMostParameter(senderHandleParameter);

        // add return type parameter
        final MostParameter returnTypeParameter = _createReturnTypeParameter(method, operationNames, "2");
        convertedMethod.addMostParameter(returnTypeParameter);

        // add input parameters
        _addInputParameterOperations(convertedMethod, method.getInputParameters(), operationNames);

        // add error parameters
        if (operationNames.contains("ErrorAck")) {
            final MostParameter errorCodeParameter = new MostParameter();
            errorCodeParameter.setName("ErrorCode");
            errorCodeParameter.setIndex(MOST_NULL);
            errorCodeParameter.setType(_createErrorCodeType());
            _addOperationAtIndex(errorCodeParameter, OperationType.ERROR_ACK, "2");

            final MostParameter errorInfoParameter = new MostParameter();
            errorInfoParameter.setName("ErrorInfo");
            errorInfoParameter.setIndex(MOST_NULL);
            errorInfoParameter.setType(_createErrorInfoType());
            _addOperationAtIndex(errorInfoParameter, OperationType.ERROR_ACK, "3");

            convertedMethod.addMostParameter(errorCodeParameter);
            convertedMethod.addMostParameter(errorInfoParameter);
        }

        return convertedMethod;
    }

    private boolean _isSequenceMethod(final Method method) {
        // CommandWithAck with parameters (TBool, TBitField, TNumber, TEnum, TString, TCStream, TShortStream, TVoid) -> Sequence
        for (final MostFunctionParameter parameter : method.getInputParameters()) {
            switch (parameter.getMostType().getPrimitiveType().getName()) {
                case "TBool":
                case "TBitField":
                case "TNumber":
                case "TEnum":
                case "TString":
                case "TCStream":
                case "TShortStream":
                case "TVoid": {
                    // pass
                } break;
                default: {
                    return false;
                }
            }
        }
        return true;
    }

    private void _addInputParameterOperations(com.softwareverde.mostadapter.MostFunction convertedMethod, List<MostFunctionParameter> inputParameters, List<String> operationNames) {
        if (operationNames.contains("StartResultAck")) {
            for (final MostFunctionParameter mostFunctionParameter : inputParameters) {
                final MostParameter mostParameter = new MostParameter();

                final com.softwareverde.mostadapter.type.MostType parameterType = _convertMostType(mostFunctionParameter.getMostType());
                // add one to parameter index because of sender handle parameter
                final String parameterName = mostFunctionParameter.getName();
                final String parameterDescription = mostFunctionParameter.getDescription();
                final int parameterIndex = mostFunctionParameter.getParameterIndex()+1;
                final String parameterIndexString = Integer.toString(parameterIndex);

                mostParameter.setName(parameterName);
                mostParameter.setDescription(parameterDescription);
                mostParameter.setIndex(MOST_NULL);
                mostParameter.setType(parameterType);
                _addOperationAtIndex(mostParameter, OperationType.START_RESULT_ACK, parameterIndexString);

                convertedMethod.addMostParameter(mostParameter);
            }
        }
    }

    protected com.softwareverde.mostadapter.type.MostType _createErrorInfoType() {
        final StreamType errorInfo = new StreamType();

        final com.softwareverde.mostadapter.type.StreamCase streamCase = new com.softwareverde.mostadapter.type.StreamCase();
        final PositionDescription positionDescription = new PositionDescription();
        positionDescription.setPositionX(PositionDescription.NULL);
        positionDescription.setPositionY(PositionDescription.NULL);

        streamCase.setPositionDescription(positionDescription);

        return errorInfo;
    }

    protected com.softwareverde.mostadapter.type.MostType _createErrorCodeType() {
        final EnumType errorCode = new EnumType();

        errorCode.setEnumMax("67");

        com.softwareverde.mostadapter.type.EnumValue value1 = new com.softwareverde.mostadapter.type.EnumValue("0x1", "FBlockIdNotAvailable", "");
        com.softwareverde.mostadapter.type.EnumValue value3 = new com.softwareverde.mostadapter.type.EnumValue("0x3", "FunctionIdNotAvailable", "");
        com.softwareverde.mostadapter.type.EnumValue value4 = new com.softwareverde.mostadapter.type.EnumValue("0x4", "OpTypeNotAvailable", "");
        com.softwareverde.mostadapter.type.EnumValue value5 = new com.softwareverde.mostadapter.type.EnumValue("0x5", "InvalidLength", "");
        com.softwareverde.mostadapter.type.EnumValue value6 = new com.softwareverde.mostadapter.type.EnumValue("0x6", "WrongParameter", "");
        com.softwareverde.mostadapter.type.EnumValue value7 = new com.softwareverde.mostadapter.type.EnumValue("0x7", "ParameterNotAvailable", "");
        com.softwareverde.mostadapter.type.EnumValue valueB = new com.softwareverde.mostadapter.type.EnumValue("0xB", "DeviceMalfunction", "");
        com.softwareverde.mostadapter.type.EnumValue valueC = new com.softwareverde.mostadapter.type.EnumValue("0xC", "SegmentationError", "");
        com.softwareverde.mostadapter.type.EnumValue value40 = new com.softwareverde.mostadapter.type.EnumValue("0x40", "Busy", "");
        com.softwareverde.mostadapter.type.EnumValue value41 = new com.softwareverde.mostadapter.type.EnumValue("0x41", "FunctionTemporaryNotAvailable", "");
        com.softwareverde.mostadapter.type.EnumValue value42 = new com.softwareverde.mostadapter.type.EnumValue("0x42", "ProcessingError", "");
        com.softwareverde.mostadapter.type.EnumValue value43 = new com.softwareverde.mostadapter.type.EnumValue("0x43", "MethodAborted", "");
        com.softwareverde.mostadapter.type.EnumValue valueC0 = new com.softwareverde.mostadapter.type.EnumValue("0xC0", "FunctionSignatureInvalid", "");
        com.softwareverde.mostadapter.type.EnumValue valueC1 = new com.softwareverde.mostadapter.type.EnumValue("0xC1", "FunctionNotImplemented", "");
        com.softwareverde.mostadapter.type.EnumValue valueC2 = new com.softwareverde.mostadapter.type.EnumValue("0xC2", "InsufficientAccess", "");

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

    protected com.softwareverde.mostadapter.type.MostType _createSenderHandleType() {
        final NumberType senderHandle = new NumberType();

        final UnsignedWordType basisDateType = new UnsignedWordType();
        final String exponent = "0";
        final String rangeMin = "0";
        final String rangeMax = "65535";
        final String step = "1";
        final String unit = "unit_none";

        senderHandle.setBasisDataType(basisDateType);
        senderHandle.setExponent(exponent);
        senderHandle.setRangeMin(rangeMin);
        senderHandle.setRangeMax(rangeMax);
        senderHandle.setStep(step);
        senderHandle.setUnit(unit);

        return senderHandle;
    }
}
