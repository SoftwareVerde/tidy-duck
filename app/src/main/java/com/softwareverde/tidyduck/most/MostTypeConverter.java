package com.softwareverde.tidyduck.most;

import com.softwareverde.mostadapter.*;
import com.softwareverde.mostadapter.type.EnumType;
import com.softwareverde.mostadapter.type.EnumValue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.softwareverde.mostadapter.Operation.OperationType;

public class MostTypeConverter {

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
            case "TUByte":
            case "TSByte":
            case "TUWord":
            case "TSWord":
            case "TULong":
            case "TSLong": {
                NumberProperty numberProperty = new NumberProperty();
                convertedProperty = numberProperty;
            } break;
            case "TString": {
                TextProperty textProperty = new TextProperty();
                convertedProperty = textProperty;
            } break;
            default: {
                UnclassifiedProperty unclassifiedProperty = new UnclassifiedProperty();
                convertedProperty = unclassifiedProperty;
            }
        }

        convertedProperty.setSupportsNotification(property.supportsNotification());

        MostParameter returnTypeParameter = createReturnTypeParameter(property);
        convertedProperty.addMostParameter(returnTypeParameter);

        // TODO: add void parameter(s)

        return convertedProperty;
    }

    private MostParameter createReturnTypeParameter(MostFunction mostFunction) {
        List<String> operationNames = getOperationNames(mostFunction.getOperations());

        MostType returnType = mostFunction.getReturnType();

        MostParameter returnTypeParameter = new MostParameter();
        // TODO: parameter name and description
        returnTypeParameter.setIndex("1");
        returnTypeParameter.setType(convertMostType(returnType));

        // Properties
        if (operationNames.contains("Set")) {
            returnTypeParameter.addOperation(createOperation(OperationType.SET));
        }
        if (operationNames.contains("Status")) {
            returnTypeParameter.addOperation(createOperation(OperationType.STATUS));
        }
        // Methods
        if (operationNames.contains("ResultAck")) {
            returnTypeParameter.addOperation(createOperation(OperationType.RESULT_ACK));
        }

        return returnTypeParameter;
    }

    private com.softwareverde.mostadapter.Operation createOperation(OperationType operationType) {
        com.softwareverde.mostadapter.Operation operation = new com.softwareverde.mostadapter.Operation();
        operation.setOperationType(operationType);

        return operation;
    }

    protected com.softwareverde.mostadapter.type.MostType convertMostType(MostType mostType) {
        com.softwareverde.mostadapter.type.MostType convertedMostType = null;

        switch (mostType.getName()) {
            case "TEnum": {
                convertedMostType = new EnumType();
            } break;
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

        for (MostFunctionParameter mostFunctionParameter : method.getInputParameters()) {
            // TODO: handle parameters
        }

        return convertedMethod;
    }

    protected MostType createErrorInfo() {
        // TODO: implement
        return null;
    }

    protected com.softwareverde.mostadapter.type.MostType createErrorCode() {
        EnumType errorCode = new EnumType();

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
}
