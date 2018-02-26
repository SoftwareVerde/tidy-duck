package com.softwareverde.tidyduck.api;

import com.softwareverde.database.Database;
import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.json.Json;
import com.softwareverde.tidyduck.Account;
import com.softwareverde.tidyduck.DateUtil;
import com.softwareverde.tidyduck.Permission;
import com.softwareverde.tidyduck.database.DatabaseManager;
import com.softwareverde.tidyduck.database.MostFunctionInflater;
import com.softwareverde.tidyduck.database.MostInterfaceInflater;
import com.softwareverde.tidyduck.environment.Environment;
import com.softwareverde.tidyduck.most.*;
import com.softwareverde.tidyduck.util.Util;
import com.softwareverde.tomcat.servlet.AuthenticatedJsonServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

public class MostFunctionServlet extends AuthenticatedJsonServlet {
    private final Logger _logger = LoggerFactory.getLogger(this.getClass());

    public MostFunctionServlet() {
        super._defineEndpoint("most-functions", HttpMethod.GET, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_VIEW);

                final long mostInterfaceId = Util.parseLong(Util.coalesce(request.getParameter("most_interface_id")));
                if (mostInterfaceId < 1) {
                    return _generateErrorJson("Invalid interface id.");
                }
                return _listMostFunctions(mostInterfaceId, environment.getDatabase(), false);
            }
        });

        super._defineEndpoint("most-functions", HttpMethod.POST, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_CREATE);

                return _insertMostFunction(request, currentAccount, environment.getDatabase());
            }
        });

        super._defineEndpoint("most-functions/<mostFunctionId>", HttpMethod.GET, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_VIEW);

                final Long mostFunctionId = Util.parseLong(parameters.get("mostFunctionId"));
                if (mostFunctionId < 1) {
                    return _generateErrorJson("Invalid function id.");
                }
                return _getMostFunction(mostFunctionId, environment.getDatabase());
            }
        });
        
        super._defineEndpoint("most-functions/<mostFunctionId>", HttpMethod.POST, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_MODIFY);

                final Long mostFunctionId = Util.parseLong(parameters.get("mostFunctionId"));
                if (mostFunctionId < 1) {
                    return _generateErrorJson("Invalid function id.");
                }
                return _updateMostFunction(request, mostFunctionId, currentAccount, environment.getDatabase());
            }
        });

        super._defineEndpoint("most-functions/<mostFunctionId>/mark-as-deleted", HttpMethod.DELETE, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_MODIFY);

                final long mostFunctionId = Util.parseLong(parameters.get("mostFunctionId"));
                if (mostFunctionId < 1) {
                    return _generateErrorJson("Invalid function id.");
                }
                return _markMostFunctionAsDeleted(request, mostFunctionId, currentAccount, environment.getDatabase());
            }
        });

        super._defineEndpoint("most-functions/<mostFunctionId>/restore-from-trash", HttpMethod.POST, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_MODIFY);

                final long mostFunctionId = Util.parseLong(parameters.get("mostFunctionId"));
                if (mostFunctionId < 1) {
                    return _generateErrorJson("Invalid function id.");
                }
                return _restoreMostFunctionFromTrash(request, mostFunctionId, currentAccount, environment.getDatabase());
            }
        });

        super._defineEndpoint("most-functions/<mostFunctionId>", HttpMethod.DELETE, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_MODIFY);

                final long mostFunctionId = Util.parseLong(parameters.get("mostFunctionId"));
                if (mostFunctionId < 1) {
                    return _generateErrorJson("Invalid function id.");
                }
                return _deleteMostFunctionFromMostInterface(request, mostFunctionId, currentAccount, environment.getDatabase());
            }
        });

        super._defineEndpoint("trashed-most-functions", HttpMethod.GET, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_VIEW);

                final long mostInterfaceId = Util.parseLong(Util.coalesce(request.getParameter("most_interface_id")));
                if (mostInterfaceId < 1) {
                    return _generateErrorJson("Invalid interface id.");
                }
                return _listMostFunctions(mostInterfaceId, environment.getDatabase(), true);
            }
        });
    }

    protected Json _getMostFunction(final Long mostFunctionId, final Database<Connection> database) {
        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            final MostFunctionInflater mostFunctionInflater = new MostFunctionInflater(databaseConnection);
            final MostFunction mostFunction = mostFunctionInflater.inflateMostFunction(mostFunctionId);

            final Json response = new Json(false);

            response.put("mostFunction", _toJson(mostFunction));

            super._setJsonSuccessFields(response);
            return response;

        } catch (final DatabaseException exception) {
            _logger.error("Unable to get function.", exception);
            return super._generateErrorJson("Unable to get function.");
        }
    }

    protected Json _insertMostFunction(final HttpServletRequest request, final Account currentAccount, final Database<Connection> database) throws Exception {
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
            final MostFunction mostFunction = _populateMostFunctionFromJson(mostFunctionJson, currentAccount, database);

            final DatabaseManager databaseManager = new DatabaseManager(database);

            final Json errorJson = _checkForFunctionIdCollisions(databaseManager, mostFunction, mostInterfaceId);
            if (errorJson != null) {
                return errorJson;
            }

            final DatabaseConnection<Connection> databaseConnection = database.newConnection();
            if (! _canCurrentAccountModifyParentMostInterface(databaseConnection, mostInterfaceId, currentAccount.getId())) {
                final String errorMessage = "Unable to insert Function: current account does not own its parent Interface " + mostInterfaceId;
                _logger.error(errorMessage);
                return super._generateErrorJson(errorMessage);
            }

            databaseManager.insertMostFunction(mostInterfaceId, mostFunction);
            response.put("mostFunctionId", mostFunction.getId());
        }
        catch (final Exception exception) {
            _logger.error("Unable to insert function.", exception);
            return super._generateErrorJson("Unable to insert function: " + exception.getMessage());
        }

        return response;
    }

    protected Json _updateMostFunction(final HttpServletRequest httpRequest, final long mostFunctionId, final Account currentAccount, final Database<Connection> database) throws Exception {
        final Json request = _getRequestDataAsJson(httpRequest);

        final Long mostInterfaceId = Util.parseLong(request.getString("mostInterfaceId"));

        final Json mostFunctionJson = request.get("mostFunction");

        { // Validate Inputs
            if (mostInterfaceId < 1) {
                _logger.error("Unable to parse Interface ID: " + mostInterfaceId);
                return _generateErrorJson("Invalid Interface ID: " + mostInterfaceId);
            }
        }

        try {
            final MostFunction mostFunction = _populateMostFunctionFromJson(mostFunctionJson, currentAccount, database);
            mostFunction.setId(mostFunctionId);

            final DatabaseManager databaseManager = new DatabaseManager(database);

            final Json errorJson = _checkForFunctionIdCollisions(databaseManager, mostFunction, mostInterfaceId);
            if (errorJson != null) {
                return errorJson;
            }

            final DatabaseConnection<Connection> databaseConnection = database.newConnection();
            if (! _canCurrentAccountModifyParentMostInterface(databaseConnection, mostInterfaceId, currentAccount.getId())) {
                final String errorMessage = "Unable to update Function: current account does not own its parent Interface " + mostInterfaceId;
                _logger.error(errorMessage);
                return super._generateErrorJson(errorMessage);
            }

            databaseManager.updateMostFunction(mostInterfaceId, mostFunction);
        }
        catch (final Exception exception) {
            final String errorMessage = "Unable to update function: " + exception.getMessage();
            _logger.error(errorMessage, exception);
            return _generateErrorJson(errorMessage);
        }

        final Json response = new Json(false);
        _setJsonSuccessFields(response);
        return response;
    }

    /**
     * <p>Check for possible function ID collisions and, if one is found, returns an appropriate error Json object.</p>
     * @param databaseManager
     * @param mostFunction
     * @param mostInterfaceId
     * @return
     * @throws DatabaseException
     */
    private Json _checkForFunctionIdCollisions(final DatabaseManager databaseManager, final MostFunction mostFunction, final Long mostInterfaceId) throws DatabaseException {
        // check for duplicate function ID in parent interface
        List<MostFunction> mostInterfaceFunctions = databaseManager.listFunctionsAssociatedWithMostInterface(mostInterfaceId);
        if (_hasConflictingFunction(mostInterfaceFunctions, mostFunction)) {
            final String errorMessage = "A function with ID " + mostFunction.getMostId() + " already exists on interface " + mostInterfaceId;
            _logger.error(errorMessage);
            return _generateErrorJson(errorMessage);
        }
        // check for duplicate function ID in parent function blocks
        List<Long> functionBlockIds = databaseManager.listFunctionBlocksContainingMostInterface(mostInterfaceId);
        for (final Long functionBlockId : functionBlockIds) {
            if (_functionBlockHasFunctionId(databaseManager, functionBlockId, mostFunction)) {
                final String errorMessage = "A function with ID " + mostFunction.getMostId() + " already exists on function block " + functionBlockId;
                _logger.error(errorMessage);
                return _generateErrorJson(errorMessage);
            }
        }
        // no collisions, don't return an error
        return null;
    }

    private boolean _hasConflictingFunction(final List<MostFunction> functions, final MostFunction mostFunction) {
        for (final MostFunction listFunction : functions) {
            // has same Most ID but a different database ID (or supplied mostFunction is new and doesn't have one)
            if (listFunction.getMostId().equals(mostFunction.getMostId()) && !listFunction.getId().equals(mostFunction.getId())) {
                return true;
            }
        }
        return false;
    }

    private boolean _functionBlockHasFunctionId(final DatabaseManager databaseManager, final Long functionBlockId, final MostFunction mostFunction) throws DatabaseException {
        List<MostFunction> functionBlockMostIds = databaseManager.listFunctionsAssociatedWithFunctionBlock(functionBlockId);
        return _hasConflictingFunction(functionBlockMostIds, mostFunction);
    }

    protected Json _markMostFunctionAsDeleted(final HttpServletRequest httpRequest, final long mostFunctionId, final Account currentAccount, final Database<Connection> database) throws IOException {
        final Json request = _getRequestDataAsJson(httpRequest);
        final Long mostInterfaceId = Util.parseLong(request.getString("mostInterfaceId"));

        // Validate Inputs
        if (mostInterfaceId == null || mostInterfaceId < 1) {
            return super._generateErrorJson(String.format("Invalid interface id: %s", mostInterfaceId));
        }

        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            if (! _canCurrentAccountModifyParentMostInterface(databaseConnection, mostInterfaceId, currentAccount.getId())) {
                final String errorMessage = "Unable to move function to trash: current account does not own its parent Interface: " + mostFunctionId;
                _logger.error(errorMessage);
                return super._generateErrorJson(errorMessage);
            }

            final DatabaseManager databaseManager = new DatabaseManager(database);
            databaseManager.markMostFunctionAsDeleted(mostFunctionId);

            _logger.info("User " + currentAccount.getId() + " marked Function " + mostFunctionId + " as deleted.");

            final Json response = new Json(false);
            super._setJsonSuccessFields(response);
            return response;
        }
        catch (final DatabaseException exception) {
            final String errorMessage = String.format("Unable to move function %d to trash", mostFunctionId);
            _logger.error(errorMessage, exception);
            return super._generateErrorJson(errorMessage);
        }
    }

    protected Json _restoreMostFunctionFromTrash(final HttpServletRequest httpRequest, final long mostFunctionId, final Account currentAccount, final Database<Connection> database) throws IOException {
        final Json request = _getRequestDataAsJson(httpRequest);
        final Long mostInterfaceId = Util.parseLong(request.getString("mostInterfaceId"));

        // Validate Inputs
        if (mostInterfaceId == null || mostInterfaceId < 1) {
            return super._generateErrorJson(String.format("Invalid interface id: %s", mostInterfaceId));
        }

        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            if (! _canCurrentAccountModifyParentMostInterface(databaseConnection, mostInterfaceId, currentAccount.getId())) {
                final String errorMessage = "Unable to restore function from trash: current account does not own its parent Interface: " + mostFunctionId;
                _logger.error(errorMessage);
                return super._generateErrorJson(errorMessage);
            }

            final DatabaseManager databaseManager = new DatabaseManager(database);
            databaseManager.restoreMostFunctionFromTrash(mostFunctionId);

            _logger.info("User " + currentAccount.getId() + " restored Function " + mostFunctionId);

            final Json response = new Json(false);
            super._setJsonSuccessFields(response);
            return response;
        }
        catch (final DatabaseException exception) {
            final String errorMessage = String.format("Unable to restore function %d from trash", mostFunctionId);
            _logger.error(errorMessage, exception);
            return super._generateErrorJson(errorMessage);
        }
    }

    protected Json _deleteMostFunctionFromMostInterface(final HttpServletRequest request, final long mostFunctionId, final Account currentAccount, final Database<Connection> database) {
        final String mostInterfaceString = request.getParameter("most_interface_id");
        final Long mostInterfaceId = Util.parseLong(mostInterfaceString);

        // Validate Inputs
        if (mostInterfaceId == null || mostInterfaceId < 1) {
            return super._generateErrorJson(String.format("Invalid interface id: %s", mostInterfaceString));
        }

        try {
            final DatabaseConnection<Connection> databaseConnection = database.newConnection();
            if (! _canCurrentAccountModifyParentMostInterface(databaseConnection, mostInterfaceId, currentAccount.getId())) {
                final String errorMessage = "Unable to delete Function: current account does not own its parent Interface " + mostInterfaceId;
                _logger.error(errorMessage);
                return super._generateErrorJson(errorMessage);
            }

            final DatabaseManager databaseManager = new DatabaseManager(database);
            databaseManager.deleteMostFunction(mostInterfaceId, mostFunctionId);
        }
        catch (final DatabaseException exception) {
            final String errorMessage = String.format("Unable to delete function %d from interface %d.", mostFunctionId, mostInterfaceId);
            _logger.error(errorMessage, exception);
            return super._generateErrorJson(errorMessage);
        }

        final Json response = new Json(false);
        super._setJsonSuccessFields(response);
        return response;
    }

    protected Json _listMostFunctions(final long mostInterfaceId, final Database<Connection> database, final boolean onlyListDeleted) {
        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            final Json response = new Json(false);

            final MostFunctionInflater mostFunctionInflater = new MostFunctionInflater(databaseConnection);
            final List<MostFunction> mostFunctions;
            if (onlyListDeleted) {
                mostFunctions = mostFunctionInflater.inflateTrashedMostFunctionsFromMostInterfaceId(mostInterfaceId);
            }
            else {
                mostFunctions = mostFunctionInflater.inflateMostFunctionsFromMostInterfaceId(mostInterfaceId);
            }


            final Json mostFunctionsJson = new Json(true);
            for (final MostFunction mostFunction : mostFunctions) {
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

    private void validateMostFunctionId(final String mostId) throws Exception {
        if (Util.isBlank(mostId)) {
            throw new Exception("Invalid Most ID");
        }
        if (!mostId.matches("0x[0-9A-F]{3}") || "0xFFF".equals(mostId)) {
            throw new Exception("Function MOST ID must be between 0x000 and 0xFFE");
        }
        // matches regex and is not 0xFFF - passes validation
    }

    protected MostFunction _populateMostFunctionFromJson(final Json mostFunctionJson, final Account currentAccount, final Database<Connection> database) throws Exception {
        final String mostId = mostFunctionJson.getString("mostId");
        final String name = mostFunctionJson.getString("name");
        final String release = mostFunctionJson.getString("releaseVersion");
        final String description = mostFunctionJson.getString("description");
        final String functionType = mostFunctionJson.getString("functionType");
        final String returnParameterName = mostFunctionJson.getString("returnParameterName");
        final String returnParameterDescription = mostFunctionJson.getString("returnParameterDescription");
        final Long returnTypeId = mostFunctionJson.getLong("returnTypeId");
        final Long stereotypeId = mostFunctionJson.getLong("stereotypeId");
        final Long authorId = mostFunctionJson.getLong("authorId");
        final Long companyId = mostFunctionJson.getLong("companyId");

        { // Validate Inputs
            validateMostFunctionId(mostId);

            if (Util.isBlank(name)) {
                throw new Exception("Name field is required.");
            }
            if (!name.matches("[A-z0-9]+")) {
                throw new Exception("Name must contain only alpha-numeric characters.");
            }

            /*
            if (Util.isBlank(description)) {
                throw new Exception("Description field is required.");
            }
            */

            if (Util.isBlank(release)) {
                throw new Exception("Version field is required.");
            }
            if (!release.matches("[0-9]+\\.[0-9]+(\\.[0-9]+)?")) {
                throw new Exception("Release version must be in the form 'Major.Minor(.Patch)'.");
            }

            if (Util.isBlank(returnParameterName)) {
                throw new Exception("Return parameter name is required.");
            }

            if (returnTypeId < 1) {
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
            company = currentAccount.getCompany();
            author = currentAccount.toAuthor();
        }

        final MostType mostReturnType = new MostType();
        mostReturnType.setId(returnTypeId);

        final MostFunctionStereotype mostFunctionStereotype = new MostFunctionStereotype();
        mostFunctionStereotype.setId(stereotypeId);

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

                    final String parameterString = inputParameterJson.getString("name");
                    final String parameterDescription = inputParameterJson.getString("description");
                    final Integer parameterIndex = inputParameterJson.getInteger("parameterIndex");

                    // Validate Inputs
                    if (Util.isBlank(parameterString)) {
                        throw new Exception("Parameter name field is required.");
                    }
                    /*
                    if (Util.isBlank(parameterDescription)) {
                        throw new Exception("Parameter description field is required.");
                    }
                    */
                    if (parameterIndex < 1) {
                        throw new Exception("Invalid parameter index.");
                    }

                    MostFunctionParameter mostFunctionParameter = new MostFunctionParameter();
                    mostFunctionParameter.setName(parameterString);
                    mostFunctionParameter.setDescription(parameterDescription);
                    mostFunctionParameter.setParameterIndex(parameterIndex);
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
        mostFunction.setReturnParameterName(returnParameterName);
        mostFunction.setReturnParameterDescription(returnParameterDescription);
        mostFunction.setReturnType(mostReturnType);
        mostFunction.setFunctionStereotype(mostFunctionStereotype);
        mostFunction.setAuthor(author);
        mostFunction.setCompany(company);

        Json operationsJson = mostFunctionJson.get("operations");
        for (int i=0; i<operationsJson.length(); i++) {
            Json operationJson = operationsJson.get(i);

            long operationId = operationJson.getLong("id");
            String operationName = operationJson.getString("name");
            String operationChannel = operationJson.getString("channel");

            final Operation operation = new Operation();
            operation.setId(operationId);
            operation.setName(operationName);
            operation.setChannel(operationChannel);

            mostFunction.addOperation(operation);
        }

        return mostFunction;
    }

    private Json _toJson(final MostFunction mostFunction) {
        final Json mostFunctionJson = new Json(false);

        String deletedDateString = null;
        if (mostFunction.getDeletedDate() != null) {
            deletedDateString = DateUtil.dateToDateString(mostFunction.getDeletedDate());
        }

        mostFunctionJson.put("id", mostFunction.getId());
        mostFunctionJson.put("mostId", mostFunction.getMostId());
        mostFunctionJson.put("name", mostFunction.getName());
        mostFunctionJson.put("releaseVersion", mostFunction.getRelease());
        mostFunctionJson.put("isDeleted", mostFunction.isDeleted());
        mostFunctionJson.put("deletedDate", deletedDateString);
        mostFunctionJson.put("isReleased", mostFunction.isReleased());
        mostFunctionJson.put("isApproved", mostFunction.isApproved());
        mostFunctionJson.put("description", mostFunction.getDescription());
        mostFunctionJson.put("functionType", mostFunction.getFunctionType());
        mostFunctionJson.put("returnParameterName", mostFunction.getReturnParameterName());
        mostFunctionJson.put("returnParameterDescription", mostFunction.getReturnParameterDescription());
        mostFunctionJson.put("returnTypeId", mostFunction.getReturnType().getId());
        mostFunctionJson.put("returnTypeName", mostFunction.getReturnType().getName());
        mostFunctionJson.put("stereotypeId", mostFunction.getFunctionStereotype().getId());
        mostFunctionJson.put("stereotypeName", mostFunction.getFunctionStereotype().getName());
        mostFunctionJson.put("authorId", mostFunction.getAuthor().getId());
        mostFunctionJson.put("authorName", mostFunction.getAuthor().getName());
        mostFunctionJson.put("companyId", mostFunction.getCompany().getId());
        mostFunctionJson.put("companyName", mostFunction.getCompany().getName());

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
                    parameterJson.put("name", parameter.getName());
                    parameterJson.put("description", parameter.getDescription());
                    parameterJson.put("parameterIndex", parameter.getParameterIndex());
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
            operationJson.put("channel", operation.getChannel());
            operationsJson.add(operationJson);
        }
        mostFunctionJson.put("operations", operationsJson);

        return mostFunctionJson;
    }

    private boolean _canCurrentAccountModifyParentMostInterface(final DatabaseConnection<Connection> databaseConnection, final Long mostInterfaceId, final Long currentAccountId) throws DatabaseException {
        final MostInterfaceInflater mostInterfaceInflater = new MostInterfaceInflater(databaseConnection);
        final MostInterface mostInterface = mostInterfaceInflater.inflateMostInterface(mostInterfaceId);

        if (mostInterface.getCreatorAccountId() != null) {
            return mostInterface.getCreatorAccountId().equals(currentAccountId);
        }

        return true;
    }
}
