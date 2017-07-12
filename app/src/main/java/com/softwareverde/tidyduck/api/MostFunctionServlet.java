package com.softwareverde.tidyduck.api;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.json.Json;
import com.softwareverde.tidyduck.*;
import com.softwareverde.tidyduck.database.AccountInflater;
import com.softwareverde.tidyduck.database.DatabaseManager;
import com.softwareverde.tidyduck.database.MostFunctionInflater;
import com.softwareverde.tidyduck.environment.Environment;
import com.softwareverde.tidyduck.util.Util;
import com.softwareverde.tomcat.servlet.AuthenticatedJsonServlet;
import com.softwareverde.tomcat.servlet.BaseServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;

public class MostFunctionServlet extends AuthenticatedJsonServlet {
    private final Logger _logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected Json handleAuthenticatedRequest(final HttpServletRequest request, final HttpMethod httpMethod, final long accountId, final Environment environment) throws Exception {
        String finalUrlSegment = BaseServlet.getFinalUrlSegment(request);
        if ("most-function".equals(finalUrlSegment)) {
            if (httpMethod == HttpMethod.POST) {
                return _insertMostFunction(request, accountId, environment);
            }
            if (httpMethod == HttpMethod.GET) {
                final long mostInterfaceId = Util.parseLong(Util.coalesce(request.getParameter("most_interface_id")));
                if (mostInterfaceId < 1) {
                    return super._generateErrorJson("Invalid interface id.");
                }
                return _listMostFunctions(mostInterfaceId, environment);
            }
        }
        return super._generateErrorJson("Unimplemented HTTP method in request.");
    }

    protected Json _insertMostFunction(final HttpServletRequest request, final long accountId, final Environment environment) throws Exception {
        final Json jsonRequest = _getRequestDataAsJson(request);
        final Json response = _generateSuccessJson();

        final Long mostInterfaceId = Util.parseLong(jsonRequest.getString("mostInterfaceId"));

        { // Validate Inputs
            if (mostInterfaceId < 1) {
                _logger.error("Unable to parse interface ID: " + mostInterfaceId);
                return super._generateErrorJson("Invalid interface ID: " + mostInterfaceId);
            }
        }

        final Json mostFunctionJson = jsonRequest.get("mostFunction");
        try {
            final MostFunction mostFunction = _populateMostFunctionFromJson(mostFunctionJson, accountId, environment);

            DatabaseManager databaseManager = new DatabaseManager(environment);
            databaseManager.insertMostFunction(mostInterfaceId, mostFunction);
            response.put("mostInterfaceId", mostFunction.getId());
        }
        catch (final Exception exception) {
            _logger.error("Unable to insert function.", exception);
            return super._generateErrorJson("Unable to insert function: " + exception.getMessage());
        }

        return response;
    }

    protected Json _listMostFunctions(long mostInterfaceId, Environment environment) {
        try(final DatabaseConnection<Connection> databaseConnection = environment.getNewDatabaseConnection()) {
            final Json response = new Json(false);

            final MostFunctionInflater mostFunctionInflater = new MostFunctionInflater(databaseConnection);
            final List<MostFunction> mostFunctions = mostFunctionInflater.inflateMostFunctionsFromMostInterfaceId(mostInterfaceId);

            final Json mostFunctionsJson = new Json(true);
            for (MostFunction mostFunction : mostFunctions) {
                final Json mostFunctionJson = _toJson(mostFunction);
                mostFunctionsJson.add(mostFunctionJson);
            }
            response.put("mostFunctions", mostFunctionsJson);

            super._setJsonSuccessFields(response);
            return response;

        } catch (final DatabaseException exception) {
            _logger.error("Unable to list functions.", exception);
            return super._generateErrorJson("Unable to list functions.");
        }
    }

    protected MostFunction _populateMostFunctionFromJson(final Json mostFunctionJson, final long accountId, final Environment environment) throws Exception {
        final String mostId = mostFunctionJson.getString("mostId");
        final String name = mostFunctionJson.getString("name");
        final String release = mostFunctionJson.getString("releaseVersion");
        final String description = mostFunctionJson.getString("description");
        final String functionType = mostFunctionJson.getString("functionType");
        final Long returnTypeId = mostFunctionJson.getLong("returnTypeId");
        final Long authorId = mostFunctionJson.getLong("authorId");
        final Long companyId = mostFunctionJson.getLong("companyId");

        { // Validate Inputs
            if (Util.isBlank(mostId)) {
                throw new Exception("Invalid Most ID");
            }

            if (Util.isBlank(name)) {
                throw new Exception("Name field is required.");
            }

            if (Util.isBlank(description)) {
                throw new Exception("Description field is required.");
            }

            if (Util.isBlank(functionType)) {
                throw new Exception("Version field is required.");
            }

            if (returnTypeId == null) {
                throw new Exception("Return type is required.");
            }
        }

        Company company;
        Author author;

        if (authorId >= 1) {
            // use supplied author/account ID
            company = new Company();
            company.setId(companyId);
            author = new Author();
            author.setId(authorId);
        } else {
            // use users's account ID
            try (DatabaseConnection<Connection> databaseConnection = environment.getNewDatabaseConnection()) {
                AccountInflater accountInflater = new AccountInflater(databaseConnection);

                Account account = accountInflater.inflateAccount(accountId);

                company = account.getCompany();
                author = account.toAuthor();
            }
        }

        final MostType mostReturnType = new MostType();
        mostReturnType.setId(returnTypeId);

        MostFunction mostFunction;
        switch (functionType) {
            case "Property": {
                Property property = new Property();
                final boolean supportsNotification = mostFunctionJson.getBoolean("supportsNotification");
                property.setSupportsNotification(supportsNotification);

                mostFunction = property;
                break;
            }
            case "Method": {
                Method method = new Method();
                // get parameters
                Json inputParametersJson = mostFunctionJson.get("inputParameters");
                for (int i=0; i<inputParametersJson.length(); i++) {
                    Json inputParameterJson = inputParametersJson.get(i);

                    MostType mostType = new MostType();
                    mostType.setId(inputParameterJson.getLong("typeId"));

                    MostFunctionParameter mostFunctionParameter = new MostFunctionParameter();
                    mostFunctionParameter.setParameterIndex(inputParameterJson.getInteger("id"));
                    mostFunctionParameter.setMostType(mostType);

                    method.addInputParameter(mostFunctionParameter);
                }
                mostFunction = method;
                break;
            }
            default: {
                throw new Exception("Invalid function type: " + functionType);
            }
        }

        mostFunction.setMostId(mostId);
        mostFunction.setName(name);
        mostFunction.setRelease(release);
        mostFunction.setDescription(description);
        mostFunction.setReturnType(mostReturnType);
        mostFunction.setAuthor(author);
        mostFunction.setCompany(company);

        Json operationsJson = mostFunctionJson.get("operations");
        for (int i=0; i<operationsJson.length(); i++) {
            long operationId = operationsJson.getLong(i);

            final Operation operation = new Operation();
            operation.setId(operationId);

            mostFunction.addOperation(operation);
        }

        return mostFunction;
    }

    private Json _toJson(final MostFunction mostFunction) {
        final Json mostFunctionJson = new Json(false);

        mostFunctionJson.put("id", mostFunction.getId());
        mostFunctionJson.put("mostId", mostFunction.getMostId());
        mostFunctionJson.put("name", mostFunction.getName());
        mostFunctionJson.put("releaseVersion", mostFunction.getRelease());
        mostFunctionJson.put("description", mostFunction.getDescription());
        mostFunctionJson.put("functionType", mostFunction.getFunctionType());
        mostFunctionJson.put("returnTypeId", mostFunction.getReturnType().getId());
        mostFunctionJson.put("returnTypeName", mostFunction.getReturnType().getName());
        mostFunctionJson.put("authorId", mostFunction.getAuthor().getId());
        mostFunctionJson.put("authorName", mostFunction.getAuthor().getName());
        mostFunctionJson.put("companyId", mostFunction.getCompany().getId());
        mostFunctionJson.put("companyName", mostFunction.getAuthor().getName());

        // function type-specific properties
        switch (mostFunction.getFunctionType()) {
            case "Property": {
                Property property = (Property) mostFunction;
                mostFunctionJson.put("supportsNotification", property.supportsNotification());
                break;
            }
            case "Method": {
                Method method = (Method) mostFunction;
                Json inputParametersJson = new Json(true);
                for (MostFunctionParameter parameter : method.getInputParameters()) {
                    Json parameterJson = new Json();
                    parameterJson.put("index", parameter.getParameterIndex());
                    parameterJson.put("typeId", parameter.getMostType().getId());
                    parameterJson.put("typeName", parameter.getMostType().getName());
                    inputParametersJson.add(parameterJson);
                }
                mostFunctionJson.put("inputParameters", inputParametersJson);
                break;
            }
        }

        // operations
        Json operationsJson = new Json(true);
        for (final Operation operation : mostFunction.getOperations()) {
            Json operationJson = new Json(false);
            operationJson.put("id", operation.getId());
            operationJson.put("name", operation.getName());
            operationsJson.add(operationJson);
        }
        mostFunctionJson.put("operations", operationsJson);

        return mostFunctionJson;
    }
}