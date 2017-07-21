package com.softwareverde.tidyduck.most;

import com.softwareverde.logging.Logger;
import com.softwareverde.logging.slf4j.Slf4jLogger;
import com.softwareverde.mostadapter.EnumProperty;
import com.softwareverde.mostadapter.Modification;
import com.softwareverde.mostadapter.NumberProperty;
import com.softwareverde.mostadapter.TextProperty;

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

        // TODO: determine method function class

        if ("Property".equals(mostFunction.getFunctionType())) {
            switch (convertedMostFunction.getReturnType().getName()) {
                case "TUByte":
                case "TSByte":
                case "TUWord":
                case "TSWord":
                case "TULong":
                case "TSLong": {
                    convertedMostFunction = new NumberProperty();
                    NumberProperty numberProperty = (NumberProperty) convertedMostFunction;
                } break;
                case "TString": {
                    convertedMostFunction = new TextProperty();
                    TextProperty textProperty = (TextProperty) convertedMostFunction;
                } break;
                case "TEnum": {
                    convertedMostFunction = new EnumProperty();
                    EnumProperty enumProperty = (EnumProperty) convertedMostFunction;
                }
                default: {
                    throw new IllegalArgumentException("Unable to determine Property function class for function " + mostFunction.getName() + " with return type " + mostFunction.getReturnType().getName());
                }
            }

            Property property = (Property) mostFunction;
            com.softwareverde.mostadapter.Property convertedProperty = (com.softwareverde.mostadapter.Property) convertedMostFunction;
            convertedProperty.setSupportsNotification(property.supportsNotification());
        }

        return convertedMostFunction;
    }
}
