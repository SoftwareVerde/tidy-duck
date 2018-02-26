package com.softwareverde.tidyduck.api;

import com.softwareverde.database.Database;
import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.json.Json;
import com.softwareverde.tidyduck.Account;
import com.softwareverde.tidyduck.DateUtil;
import com.softwareverde.tidyduck.Permission;
import com.softwareverde.tidyduck.database.DatabaseManager;
import com.softwareverde.tidyduck.database.MostInterfaceInflater;
import com.softwareverde.tidyduck.environment.Environment;
import com.softwareverde.tidyduck.most.MostFunction;
import com.softwareverde.tidyduck.most.MostInterface;
import com.softwareverde.tidyduck.util.Util;
import com.softwareverde.tomcat.servlet.AuthenticatedJsonServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MostInterfaceServlet extends AuthenticatedJsonServlet {
    private final Logger _logger = LoggerFactory.getLogger(this.getClass());

    public MostInterfaceServlet() {
        super._defineEndpoint("most-interfaces", HttpMethod.GET, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_VIEW);

                final String requestFunctionBlockId = request.getParameter("function_block_id");

                if (Util.isBlank(requestFunctionBlockId)) {
                    return _listAllMostInterfaces(currentAccount, environment.getDatabase(), false);
                }

                final long functionBlockId = Util.parseLong(Util.coalesce(requestFunctionBlockId));
                if (functionBlockId < 1) {
                    return _generateErrorJson("Invalid function block id.");
                }
                return _listMostInterfaces(functionBlockId, currentAccount, environment.getDatabase());
            }
        });

        super._defineEndpoint("most-interfaces", HttpMethod.POST, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_CREATE);

                return _insertMostInterface(request, currentAccount, environment.getDatabase());
            }
        });

        super._defineEndpoint("most-interfaces/search/<name>", HttpMethod.GET, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_VIEW);

                final String searchString = Util.coalesce(parameters.get("name"));
                if (searchString.length() < 2) {
                    return _generateErrorJson("Invalid search string for interface.");
                }
                // include deleted items unless requested not to
                final String includeDeleteString = request.getParameter("includeDeleted");
                boolean includeDeleted = !"false".equals(includeDeleteString);

                return _listMostInterfacesMatchingSearchString(searchString, includeDeleted, currentAccount, environment.getDatabase());
            }
        });

        super._defineEndpoint("most-interfaces/<mostInterfaceId>", HttpMethod.GET, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_VIEW);

                final Long mostInterfaceId = Util.parseLong(parameters.get("mostInterfaceId"));
                if (mostInterfaceId < 1) {
                    return _generateErrorJson("Invalid interface id.");
                }
                return _getMostInterface(mostInterfaceId, environment.getDatabase());
            }
        });

        super._defineEndpoint("most-interfaces/<mostInterfaceId>", HttpMethod.POST, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_MODIFY);

                final Long mostInterfaceId = Util.parseLong(parameters.get("mostInterfaceId"));
                if (mostInterfaceId < 1) {
                    return _generateErrorJson("Invalid interface id.");
                }
                return _updateMostInterface(request, mostInterfaceId, currentAccount, environment.getDatabase());
            }
        });

        super._defineEndpoint("most-interfaces/<mostInterfaceId>/fork", HttpMethod.POST, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_CREATE);

                final Long mostInterfaceId = Util.parseLong(parameters.get("mostInterfaceId"));
                if (mostInterfaceId < 1) {
                    return _generateErrorJson("Invalid interface id.");
                }
                return _forkMostInterface(request, mostInterfaceId, currentAccount, environment.getDatabase());
            }
        });

        super._defineEndpoint("most-interfaces/<mostInterfaceId>/mark-as-deleted", HttpMethod.DELETE, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_MODIFY);

                final Long mostInterfaceId = Util.parseLong(parameters.get("mostInterfaceId"));
                if (mostInterfaceId < 1) {
                    return _generateErrorJson("Invalid interface id.");
                }
                return _markMostInterfaceAsDeleted(mostInterfaceId, currentAccount, environment.getDatabase());
            }
        });

        super._defineEndpoint("most-interfaces/<mostInterfaceId>/restore-from-trash", HttpMethod.POST, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_MODIFY);

                final Long mostInterfaceId = Util.parseLong(parameters.get("mostInterfaceId"));
                if (mostInterfaceId < 1) {
                    return _generateErrorJson("Invalid interface id.");
                }
                return _restoreMostInterfaceFromTrash(mostInterfaceId, currentAccount, environment.getDatabase());
            }
        });

        super._defineEndpoint("most-interfaces/<mostInterfaceId>", HttpMethod.DELETE, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_MODIFY);

                final Long mostInterfaceId = Util.parseLong(parameters.get("mostInterfaceId"));
                if (mostInterfaceId < 1) {
                    return _generateErrorJson("Invalid interface id.");
                }
                return _deleteMostInterfaceFromFunctionBlock(request, mostInterfaceId, currentAccount, environment.getDatabase());
            }
        });

        super._defineEndpoint("most-interfaces/<mostInterfaceId>/function-blocks", HttpMethod.GET, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_VIEW);

                final Long mostInterfaceId = Util.parseLong(parameters.get("mostInterfaceId"));
                if (mostInterfaceId < 1) {
                    return _generateErrorJson("Invalid interface id.");
                }
                return _listFunctionBlocksContainingMostInterface(mostInterfaceId, environment.getDatabase());
            }
        });

        super._defineEndpoint("most-interfaces/<mostInterfaceId>/function-blocks", HttpMethod.POST, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_MODIFY);

                final Long mostInterfaceId = Util.parseLong(parameters.get("mostInterfaceId"));
                if (mostInterfaceId < 1) {
                    return _generateErrorJson("Invalid interface id.");
                }
                return _associateInterfaceWithFunctionBlock(request, mostInterfaceId, currentAccount, environment.getDatabase());
            }
        });

        super._defineEndpoint("most-interfaces/<mostInterfaceId>/submit-for-review", HttpMethod.POST, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_MODIFY);

                final Long mostInterfaceId = Util.parseLong(parameters.get("mostInterfaceId"));
                if (mostInterfaceId < 1) {
                    return _generateErrorJson("Invalid interface id.");
                }
                return _submitMostInterfaceForReview(mostInterfaceId, currentAccount, environment.getDatabase());
            }
        });

        super._defineEndpoint("most-interface-duplicate-check", HttpMethod.POST, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_MODIFY);

                return _checkForDuplicateMostInterface(request, environment.getDatabase());
            }
        });

        super._defineEndpoint("trashed-most-interfaces", HttpMethod.GET, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_MODIFY);

                return _listAllMostInterfaces(currentAccount, environment.getDatabase(), true);
            }
        });
    }

    private Json _getMostInterface(final Long mostInterfaceId, final Database<Connection> database) {
        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            final MostInterfaceInflater mostInterfaceInflater = new MostInterfaceInflater(databaseConnection);
            final MostInterface mostInterface = mostInterfaceInflater.inflateMostInterface(mostInterfaceId);

            final Json response = new Json(false);

            response.put("mostInterface", _toJson(mostInterface));

            _setJsonSuccessFields(response);
            return response;
        }
        catch (final DatabaseException exception) {
            _logger.error("Unable to list interfaces", exception);
            return _generateErrorJson("Unable to list interfaces.");
        }
    }

    protected Json _insertMostInterface(final HttpServletRequest request, final Account currentAccount, final Database database) throws Exception {
        final Json jsonRequest = _getRequestDataAsJson(request);
        final Json response = _generateSuccessJson();
        final Json mostInterfaceJson = jsonRequest.get("mostInterface");
        final String requestFunctionBlockID = jsonRequest.getString("functionBlockId");

        try {
            final MostInterface mostInterface = _populateMostInterfaceFromJson(mostInterfaceJson);
            final DatabaseManager databaseManager = new DatabaseManager(database);

            // If function block ID isn't null, insert interface for function block
            if (! Util.isBlank(requestFunctionBlockID)) {
                final Long functionBlockId = Util.parseLong(requestFunctionBlockID);
                if (functionBlockId < 1) {
                    _logger.error("Unable to parse Function Block ID: " + functionBlockId);
                    return _generateErrorJson("Invalid Function Block ID: " + functionBlockId);
                }

                final DatabaseConnection<Connection> databaseConnection = database.newConnection();
                String errorMessage = FunctionBlockServlet.canAccountModifyFunctionBlock(databaseConnection, functionBlockId, currentAccount.getId());
                if (errorMessage != null) {
                    errorMessage = "Unable to insert interface: " + errorMessage;
                    _logger.error(errorMessage);
                    return super._generateErrorJson(errorMessage);
                }

                databaseManager.insertMostInterface(functionBlockId, mostInterface);
            }
            else {
                databaseManager.insertOrphanedMostInterface(mostInterface);
            }

            response.put("mostInterfaceId", mostInterface.getId());
        }
        catch (final Exception exception) {
            _logger.error("Unable to insert Interface.", exception);
            return _generateErrorJson("Unable to insert Interface: " + exception.getMessage());
        }

        return response;
    }

    protected Json _updateMostInterface(final HttpServletRequest httpRequest, final long mostInterfaceId, final Account currentAccount, final Database<Connection> database) throws Exception {
        final Json request = _getRequestDataAsJson(httpRequest);
        final Json response = new Json(false);

        final Json mostInterfaceJson = request.get("mostInterface");

        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            final Long currentAccountId = currentAccount.getId();

            String errorMessage = canAccountModifyMostInterface(databaseConnection, mostInterfaceId, currentAccountId);
            if (errorMessage != null) {
                errorMessage = "Unable to update interface: " + errorMessage;
                _logger.error(errorMessage);
                return super._generateErrorJson(errorMessage);
            }

            final MostInterface mostInterface = _populateMostInterfaceFromJson(mostInterfaceJson);
            mostInterface.setId(mostInterfaceId);

            final DatabaseManager databaseManager = new DatabaseManager(database);
            databaseManager.updateMostInterface(mostInterface, currentAccountId);

            _logger.info("User " + currentAccountId + " updated interface " + mostInterface.getId() + ", which is currently owned by User " + mostInterface.getCreatorAccountId());
            response.put("mostInterfaceId", mostInterface.getId());
        }
        catch (final Exception exception) {
            final String errorMessage = "Unable to update interface: " + exception.getMessage();
            _logger.error(errorMessage, exception);
            return _generateErrorJson(errorMessage);
        }

        _setJsonSuccessFields(response);
        return response;
    }

    protected Json _forkMostInterface(final HttpServletRequest httpRequest, final long mostInterfaceId, final Account currentAccount, final Database<Connection> database) throws Exception {
        final Json request = _getRequestDataAsJson(httpRequest);
        final Json response = new Json(false);
        final String parentFunctionBlockIdString = request.getString("functionBlockId");

        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            final Long currentAccountId = currentAccount.getId();

            String errorMessage = canAccountModifyMostInterface(databaseConnection, mostInterfaceId, currentAccountId);
            if (errorMessage != null) {
                errorMessage = "Unable to update interface: " + errorMessage;
                _logger.error(errorMessage);
                return super._generateErrorJson(errorMessage);
            }

            final Long parentFunctionBlockId = Util.parseLong(parentFunctionBlockIdString);

            final DatabaseManager databaseManager = new DatabaseManager(database);
            final long newMostInterfaceId = databaseManager.forkMostInterface(mostInterfaceId, parentFunctionBlockId, currentAccountId);

            _logger.info("User " + currentAccountId + " updated interface " + mostInterfaceId + " (new ID: " + newMostInterfaceId + ")");
            response.put("mostInterfaceId", newMostInterfaceId);
        }
        catch (final Exception exception) {
            final String errorMessage = "Unable to update interface: " + exception.getMessage();
            _logger.error(errorMessage, exception);
            return _generateErrorJson(errorMessage);
        }

        _setJsonSuccessFields(response);
        return response;
    }

    private Json _checkForDuplicateMostInterface(final HttpServletRequest httpRequest, final Database<Connection> database) {
        try {
            final Json request = _getRequestDataAsJson(httpRequest);
            final String mostInterfaceMostId = request.getString("mostInterfaceMostId");
            final String mostInterfaceName = request.getString("mostInterfaceName");
            final Long mostInterfaceVersionSeriesId = request.getLong("mostInterfaceVersionSeriesId");

            DatabaseManager databaseManager = new DatabaseManager(database);
            final MostInterface matchedMostInterface;

            if (! Util.isBlank(mostInterfaceMostId)) {
                matchedMostInterface = databaseManager.checkForDuplicateMostInterfaceMostId(mostInterfaceMostId, mostInterfaceVersionSeriesId);
            }
            else {
                matchedMostInterface = databaseManager.checkForDuplicateMostInterfaceName(mostInterfaceName, mostInterfaceVersionSeriesId);
            }

            final Json response = new Json(false);

            if (matchedMostInterface == null) {
                response.put("matchFound", false);
            } else {
                response.put("matchFound", true);
                response.put("matchedMostInterface", _toJson(matchedMostInterface));
            }

            super._setJsonSuccessFields(response);
            return response;
        }
        catch (final Exception exception) {
            _logger.error("Unable to check for duplicate Function Block.", exception);
            return super._generateErrorJson("Unable to check for duplicate Function Block: " + exception.getMessage());
        }
    }

    private Json _associateInterfaceWithFunctionBlock(final HttpServletRequest request, final long mostInterfaceId, final Account currentAccount, final Database<Connection> database) throws IOException {
        final Json jsonRequest = _getRequestDataAsJson(request);
        final Json response = _generateSuccessJson();

        final Long functionBlockId = Util.parseLong(jsonRequest.getString("functionBlockId"));

        { // Validate Inputs
            if (functionBlockId < 1) {
                _logger.error("Unable to parse Function Block ID: " + functionBlockId);
                return _generateErrorJson("Invalid Function Block ID: " + functionBlockId);
            }
        }

        try {
            final DatabaseManager databaseManager = new DatabaseManager(database);

            final List<MostFunction> functionBlockFunctions = databaseManager.listFunctionsAssociatedWithFunctionBlock(functionBlockId);
            final List<MostFunction> mostInterfaceFunctions = databaseManager.listFunctionsAssociatedWithMostInterface(mostInterfaceId);
            final List<String> conflictingMostIds = getConflictingMostIds(functionBlockFunctions, mostInterfaceFunctions);
            if (conflictingMostIds.size() > 0) {
                final String errorMessage = "Conflicting function IDs found: " + conflictingMostIds.toString();
                _logger.error(errorMessage);
                return _generateErrorJson(errorMessage);
            }

            final DatabaseConnection<Connection> databaseConnection = database.newConnection();
            String errorMessage = FunctionBlockServlet.canAccountModifyFunctionBlock(databaseConnection, functionBlockId, currentAccount.getId());
            if (errorMessage != null) {
                errorMessage = "Unable to associate interface with function block: " + errorMessage;
                _logger.error(errorMessage);
                return super._generateErrorJson(errorMessage);
            }

            databaseManager.associateMostInterfaceWithFunctionBlock(functionBlockId, mostInterfaceId);
        }
        catch (final Exception exception) {
            _logger.error("Unable to insert Interface.", exception);
            return _generateErrorJson("Unable to insert Interface: " + exception.getMessage());
        }

        return response;
    }

    private List<String> getConflictingMostIds(final List<MostFunction> functionBlockFunctions, final List<MostFunction> mostInterfaceFunctions) {
        List<String> conflictingMostIds = new ArrayList<>();
        for (final MostFunction functionBlockMostFunction : functionBlockFunctions) {
            for (final MostFunction mostInterfaceMostFunction : mostInterfaceFunctions) {
                final String functionBlockFunctionId = functionBlockMostFunction.getMostId();
                final String mostInterfaceFunctionId = mostInterfaceMostFunction.getMostId();
                if (functionBlockFunctionId.equals(mostInterfaceFunctionId)) {
                    conflictingMostIds.add(functionBlockFunctionId);
                }
            }
        }
        return conflictingMostIds;
    }

    protected Json _markMostInterfaceAsDeleted(final long mostInterfaceId, final Account currentAccount, final Database<Connection> database) {
        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            String errorMessage = canAccountModifyMostInterface(databaseConnection, mostInterfaceId, currentAccount.getId());
            if (errorMessage != null) {
                errorMessage = "Unable to move interface to trash: " + errorMessage;
                _logger.error(errorMessage);
                return super._generateErrorJson(errorMessage);
            }

            final DatabaseManager databaseManager = new DatabaseManager(database);
            databaseManager.markMostInterfaceAsDeleted(mostInterfaceId);

            _logger.info("User " + currentAccount.getId() + " marked Interface " + mostInterfaceId + " as deleted.");

            final Json response = new Json(false);
            super._setJsonSuccessFields(response);
            return response;
        }
        catch (final DatabaseException exception) {
            final String errorMessage = String.format("Unable to move interface %d to trash", mostInterfaceId);
            _logger.error(errorMessage, exception);
            return super._generateErrorJson(errorMessage);
        }
    }

    protected Json _restoreMostInterfaceFromTrash(final long mostInterfaceId, final Account currentAccount, final Database<Connection> database) {
        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            String errorMessage = canAccountModifyMostInterface(databaseConnection, mostInterfaceId, currentAccount.getId());
            if (errorMessage != null) {
                errorMessage = "Unable to restore interface from trash: " + errorMessage;
                _logger.error(errorMessage);
                return super._generateErrorJson(errorMessage);
            }

            final DatabaseManager databaseManager = new DatabaseManager(database);
            databaseManager.restoreMostInterfaceFromTrash(mostInterfaceId);

            _logger.info("User " + currentAccount.getId() + " restored Interface " + mostInterfaceId);

            final Json response = new Json(false);
            super._setJsonSuccessFields(response);
            return response;
        }
        catch (final DatabaseException exception) {
            final String errorMessage = String.format("Unable to restore interface %d from trash", mostInterfaceId);
            _logger.error(errorMessage, exception);
            return super._generateErrorJson(errorMessage);
        }
    }

    protected Json _deleteMostInterfaceFromFunctionBlock(final HttpServletRequest request, final long mostInterfaceId, final Account currentAccount, final Database<Connection> database) {
        final String functionBlockIdString = request.getParameter("functionBlockId");
        final Long functionBlockId = Util.parseLong(functionBlockIdString);
        final Long currentAccountId = currentAccount.getId();

        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {

            String errorMessage = canAccountModifyMostInterface(databaseConnection, mostInterfaceId, currentAccountId);
            if (errorMessage != null) {
                errorMessage = "Unable to remove interface: " + errorMessage;
                _logger.error(errorMessage);
                return super._generateErrorJson(errorMessage);
            }

            final DatabaseManager databaseManager = new DatabaseManager(database);

            // Validate inputs. If null, send blockId of 0, which will disassociate interface from all fblocks.
            if (Util.isBlank(functionBlockIdString)) {
                databaseManager.deleteMostInterface(0, mostInterfaceId);
            }
            else {
                if (functionBlockId < 1) {
                    return _generateErrorJson(String.format("Invalid function block id: %s", functionBlockIdString));
                }

                String parentErrorMessage = FunctionBlockServlet.canAccountModifyFunctionBlock(databaseConnection, functionBlockId, currentAccountId);
                if (parentErrorMessage != null) {
                    parentErrorMessage = "Unable to remove interface from parent function block: " + parentErrorMessage;
                    _logger.error(parentErrorMessage);
                    return super._generateErrorJson(parentErrorMessage);
                }

                databaseManager.deleteMostInterface(functionBlockId, mostInterfaceId);
            }
        }
        catch (final DatabaseException exception) {
            final String errorMessage = String.format("Unable to delete interface %d from function block %d.", mostInterfaceId, functionBlockId);
            _logger.error(errorMessage, exception);
            return _generateErrorJson(errorMessage);
        }

        final Json response = new Json(false);
        _setJsonSuccessFields(response);
        return response;
    }

    protected Json _listMostInterfaces(final long functionBlockId, final Account currentAcount, final Database<Connection> database) {
        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            final Json response = new Json(false);

            final MostInterfaceInflater mostInterfaceInflater = new MostInterfaceInflater(databaseConnection);
            final List<MostInterface> mostInterfaces = mostInterfaceInflater.inflateMostInterfacesFromFunctionBlockId(functionBlockId);

            final Json mostInterfacesJson = new Json(true);
            for (final MostInterface mostInterface : mostInterfaces) {
                final Json mostInterfaceJson = _toJson(mostInterface);
                mostInterfacesJson.add(mostInterfaceJson);
            }
            response.put("mostInterfaces", mostInterfacesJson);

            _setJsonSuccessFields(response);
            return response;
        }
        catch (final DatabaseException exception) {
            _logger.error("Unable to list interfaces", exception);
            return _generateErrorJson("Unable to list interfaces.");
        }
    }

    protected Json _listAllMostInterfaces(final Account currentAccount, final Database<Connection> database, final boolean onlyListDeleted) {
        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            final Json response = new Json(false);

            final MostInterfaceInflater mostInterfaceInflater = new MostInterfaceInflater(databaseConnection);
            final Map<Long, List<MostInterface>> mostInterfaces;

            if (onlyListDeleted) {
                mostInterfaces = mostInterfaceInflater.inflateTrashedMostInterfacesGroupedByBaseVersionId();
            }
            else {
                mostInterfaces = mostInterfaceInflater.inflateMostInterfacesGroupedByBaseVersionId();
            }

            final Json mostInterfacesJson = new Json(true);
            for (final Long baseVersionId : mostInterfaces.keySet()) {
                final Json versionSeriesJson = new Json();
                versionSeriesJson.put("baseVersionId", baseVersionId);

                final Json versionsJson = new Json();
                for (final MostInterface mostInterface : mostInterfaces.get(baseVersionId)) {
                    if (! mostInterface.isApproved()) {
                        if (mostInterface.getCreatorAccountId() != null) {
                            if (! mostInterface.getCreatorAccountId().equals(currentAccount.getId())) {
                                // Skip adding this interface to the JSON because it is not approved, not unowned, and not owned by the current user.
                                continue;
                            }
                        }
                    }

                    final Json mostInterfaceJson = _toJson(mostInterface);
                    versionsJson.add(mostInterfaceJson);
                }

                // Only add versionSeriesJson if its interfaces have not all been filtered.
                if (versionsJson.length() > 0) {
                    versionSeriesJson.put("versions", versionsJson);
                    mostInterfacesJson.add(versionSeriesJson);
                }
            }
            response.put("mostInterfaces", mostInterfacesJson);

            super._setJsonSuccessFields(response);
            return response;
        }
        catch (final DatabaseException exception) {
            _logger.error("Unable to list all interfaces.", exception);
            return super._generateErrorJson("Unable to list all interfaces.");
        }
    }

    protected Json _listMostInterfacesMatchingSearchString(final String searchString, final boolean includeDeleted, final Account currentAccount, final Database<Connection> database) {
        final String decodedSearchString;
        try {
            decodedSearchString = URLDecoder.decode(searchString, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            _logger.error("Unable to list interfaces from search", e);
            return _generateErrorJson("Unable to list interfaces from search.");
        }

        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            final Json response = new Json(false);

            final MostInterfaceInflater mostInterfaceInflater = new MostInterfaceInflater(databaseConnection);
            final Map<Long, List<MostInterface>> mostInterfaces = mostInterfaceInflater.inflateMostInterfacesMatchingSearchString(decodedSearchString, includeDeleted, currentAccount.getId());

            final Json mostInterfacesJson = new Json(true);
            for (final Long baseVersionId : mostInterfaces.keySet()) {
                final Json versionSeriesJson = new Json();
                versionSeriesJson.put("baseVersionId", baseVersionId);

                final Json versionsJson = new Json();
                for (final MostInterface mostInterface : mostInterfaces.get(baseVersionId)) {
                    final Json mostInterfaceJson = _toJson(mostInterface);
                    versionsJson.add(mostInterfaceJson);
                }
                versionSeriesJson.put("versions", versionsJson);
                mostInterfacesJson.add(versionSeriesJson);
            }
            response.put("mostInterfaces", mostInterfacesJson);

            super._setJsonSuccessFields(response);
            return response;
        }
        catch (final DatabaseException exception) {
            _logger.error("Unable to list interfaces from search", exception);
            return _generateErrorJson("Unable to list interfaces from search.");
        }
    }

    protected Json _listFunctionBlocksContainingMostInterface(final long mostInterfaceId, final Database<Connection> database) {
        try {
            final Json response = new Json(false);

            final DatabaseManager databaseManager = new DatabaseManager(database);
            final List<Long> functionBlockIds = databaseManager.listFunctionBlocksContainingMostInterface(mostInterfaceId);

            final Json functionBlockIdsJson = new Json(true);
            for (Long functionBlockId : functionBlockIds) {
                functionBlockIdsJson.add(functionBlockId);
            }
            response.put("functionBlockIds", functionBlockIdsJson);

            _setJsonSuccessFields(response);
            return response;
        }
        catch (final DatabaseException exception) {
            _logger.error("Unable to list function blocks for interface " + mostInterfaceId, exception);
            return _generateErrorJson("Unable to list function blocks.");
        }
    }

    protected Json _submitMostInterfaceForReview(final Long mostInterfaceId, final Account currentAccount, final Database<Connection> database) {
        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            String errorMessage = canAccountModifyMostInterface(databaseConnection, mostInterfaceId, currentAccount.getId());
            if (errorMessage != null) {
                errorMessage = "Unable to submit interface for review: " + errorMessage;
                _logger.error(errorMessage);
                return super._generateErrorJson(errorMessage);
            }
            final Json response = new Json(false);

            final DatabaseManager databaseManager = new DatabaseManager(database);
            databaseManager.submitMostInterfaceForReview(mostInterfaceId, currentAccount.getId());

            super._setJsonSuccessFields(response);
            return response;
        } catch (DatabaseException e) {
            String errorMessage = "Unable to submit interface for review.";
            _logger.error(errorMessage, e);
            return super._generateErrorJson(errorMessage);
        }
    }

    protected MostInterface _populateMostInterfaceFromJson(final Json mostInterfaceJson) throws Exception {
        final String mostId = mostInterfaceJson.getString("mostId");
        final String name = mostInterfaceJson.getString("name");
        final String description = mostInterfaceJson.getString("description");
        final String releaseVersion = mostInterfaceJson.getString("releaseVersion");
        final Long creatorAccountId = mostInterfaceJson.getLong("creatorAccountId");

        { // Validate Inputs
            if (Util.isBlank(mostId)) {
                throw new Exception("Invalid Most ID");
            }
            if (!Util.isLong(mostId)) {
                throw new Exception("Interface MOST ID must be an integer.");
            }

            if (Util.isBlank(name)) {
                throw new Exception("Name field is required.");
            }
            if (!name.matches("I[A-z0-9]+")) {
                throw new Exception("Name must contain only alpha-numeric characters and start with an 'I'.");
            }

            /*
            if (Util.isBlank(description)) {
                throw new Exception("Description field is required.");
            }
            */
            if (Util.isBlank(releaseVersion)) {
                throw new Exception("Version field is required.");
            }
            if (!Util.isLong(releaseVersion)) {
                throw new Exception("Interface version must be an integer.");
            }
        }

        final MostInterface mostInterface = new MostInterface();
        mostInterface.setMostId(mostId);
        mostInterface.setName(name);
        mostInterface.setVersion(releaseVersion);
        mostInterface.setDescription(description);
        mostInterface.setCreatorAccountId(creatorAccountId > 0 ? creatorAccountId : null);

        return mostInterface;
    }

    private Json _toJson(final MostInterface mostInterface) {
        final Json mostInterfaceJson = new Json(false);

        String deletedDateString = null;
        if (mostInterface.getDeletedDate() != null) {
            deletedDateString = DateUtil.dateToDateString(mostInterface.getDeletedDate());
        }

        mostInterfaceJson.put("id", mostInterface.getId());
        mostInterfaceJson.put("mostId", mostInterface.getMostId());
        mostInterfaceJson.put("name", mostInterface.getName());
        mostInterfaceJson.put("description", mostInterface.getDescription());
        mostInterfaceJson.put("lastModifiedDate", DateUtil.dateToDateString(mostInterface.getLastModifiedDate()));
        mostInterfaceJson.put("releaseVersion", mostInterface.getVersion());
        mostInterfaceJson.put("isDeleted", mostInterface.isDeleted());
        mostInterfaceJson.put("deletedDate", deletedDateString);
        mostInterfaceJson.put("isReleased", mostInterface.isReleased());
        mostInterfaceJson.put("isApproved", mostInterface.isApproved());
        mostInterfaceJson.put("baseVersionId", mostInterface.getBaseVersionId());
        mostInterfaceJson.put("priorVersionId", mostInterface.getPriorVersionId());
        mostInterfaceJson.put("creatorAccountId", mostInterface.getCreatorAccountId());
        return mostInterfaceJson;
    }

    public static String canAccountModifyMostInterface(final DatabaseConnection<Connection> databaseConnection, final Long mostInterfaceId, final Long currentAccountId) throws DatabaseException {
        final MostInterfaceInflater mostInterfaceInflater = new MostInterfaceInflater(databaseConnection);
        final MostInterface originalMostInterface = mostInterfaceInflater.inflateMostInterface(mostInterfaceId);

        if (originalMostInterface.getCreatorAccountId() != null) {
            if (!originalMostInterface.getCreatorAccountId().equals(currentAccountId)) {
                return "The interface is owned by another account and cannot be modified.";
            }
        }

        if (originalMostInterface.isReleased()) {
            return "Released interfaces cannot be modified.";
        }
        if (originalMostInterface.isApproved()) {
            return "Approved interfaces cannot be modified.";
        }

        // good to go
        return null;
    }
}
