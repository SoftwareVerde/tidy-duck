package com.softwareverde.tidyduck.most;

import com.softwareverde.logging.Logger;
import com.softwareverde.logging.slf4j.Slf4jLogger;
import com.softwareverde.mostadapter.*;

import java.util.Date;

public class MostTypeConverter {

    private final Logger _logger = new Slf4jLogger(getClass());

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

    private com.softwareverde.mostadapter.MostFunction createPropertyFunction(Property property) {
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
            case "TEnum": {
                EnumProperty enumProperty = new EnumProperty();

                convertedProperty = enumProperty;
            }
            default: {
                throw new IllegalArgumentException("Unable to determine Property function class for function " + property.getName() + " with return type " + property.getReturnType().getName());
            }
        }

        convertedProperty.setSupportsNotification(property.supportsNotification());

        return convertedProperty;
    }

    private com.softwareverde.mostadapter.MostFunction createMethodFunction(Method method) {
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
}
