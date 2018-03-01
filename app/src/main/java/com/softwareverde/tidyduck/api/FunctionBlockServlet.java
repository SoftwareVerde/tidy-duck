package com.softwareverde.tidyduck.api;

import com.softwareverde.database.Database;
import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.json.Json;
import com.softwareverde.tidyduck.Account;
import com.softwareverde.tidyduck.DateUtil;
import com.softwareverde.tidyduck.Permission;
import com.softwareverde.tidyduck.database.DatabaseManager;
import com.softwareverde.tidyduck.database.FunctionBlockInflater;
import com.softwareverde.tidyduck.environment.Environment;
import com.softwareverde.tidyduck.most.Author;
import com.softwareverde.tidyduck.most.Company;
import com.softwareverde.tidyduck.most.FunctionBlock;
import com.softwareverde.tidyduck.util.Util;
import com.softwareverde.tomcat.servlet.AuthenticatedJsonServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

public class FunctionBlockServlet extends AuthenticatedJsonServlet {
    private final Logger _logger = LoggerFactory.getLogger(this.getClass());

    public FunctionBlockServlet() {
        super._defineEndpoint("function-blocks", HttpMethod.GET, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_VIEW);

                final String requestFunctionCatalogId = request.getParameter("function_catalog_id");

                if (Util.isBlank(requestFunctionCatalogId)) {
                    return _listAllFunctionBlocks(currentAccount, environment.getDatabase(), false);
                }

                final long functionCatalogId = Util.parseLong(Util.coalesce(requestFunctionCatalogId));
                if (functionCatalogId < 1) {
                    return _generateErrorJson("Invalid function catalog id: " + functionCatalogId);
                }

                return _listFunctionBlocks(functionCatalogId, currentAccount, environment.getDatabase());
            }
        });

        super._defineEndpoint("function-blocks", HttpMethod.POST, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_CREATE);

                return _insertFunctionBlock(request, currentAccount, environment.getDatabase());
            }
        });

        super._defineEndpoint("function-blocks/search/<name>", HttpMethod.GET, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_VIEW);

                final String searchString = Util.coalesce(parameters.get("name"));
                if (searchString.length() < 2) {
                    return _generateErrorJson("Invalid search string for function block.");
                }
                // include deleted items unless requested not to
                final String includeDeleteString = request.getParameter("includeDeleted");
                boolean includeDeleted = !"false".equals(includeDeleteString);

                return _listFunctionBlocksMatchingSearchString(searchString, includeDeleted, currentAccount, environment.getDatabase());
            }
        });

        super._defineEndpoint("function-blocks/<functionBlockId>", HttpMethod.GET, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_VIEW);

                final long functionBlockId = Util.parseLong(parameters.get("functionBlockId"));
                if (functionBlockId < 1) {
                    return _generateErrorJson("Invalid function block id: " + functionBlockId);
                }
                return _getFunctionBlock(functionBlockId, currentAccount, environment.getDatabase());
            }
        });

        super._defineEndpoint("function-blocks/<functionBlockId>", HttpMethod.POST, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_MODIFY);

                final long functionBlockId = Util.parseLong(parameters.get("functionBlockId"));
                if (functionBlockId < 1) {
                    return _generateErrorJson("Invalid function block id: " + functionBlockId);
                }
                return _updateFunctionBlock(request, functionBlockId, currentAccount, environment.getDatabase());
            }
        });

        super._defineEndpoint("function-blocks/<functionBlockId>/fork", HttpMethod.POST, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_CREATE);

                final long functionBlockId = Util.parseLong(parameters.get("functionBlockId"));
                if (functionBlockId < 1) {
                    return _generateErrorJson("Invalid function block id: " + functionBlockId);
                }
                return _forkFunctionBlock(request, functionBlockId, currentAccount, environment.getDatabase());
            }
        });

        super._defineEndpoint("function-blocks/<functionBlockId>/mark-as-deleted", HttpMethod.DELETE, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_MODIFY);

                final long functionBlockId = Util.parseLong(parameters.get("functionBlockId"));
                if (functionBlockId < 1) {
                    return _generateErrorJson("Invalid function block id: " + functionBlockId);
                }
                return _markFunctionBlockAsDeleted(functionBlockId, currentAccount, environment.getDatabase());
            }
        });

        super._defineEndpoint("function-blocks/<functionBlockId>/restore-from-trash", HttpMethod.POST, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_MODIFY);

                final long functionBlockId = Util.parseLong(parameters.get("functionBlockId"));
                if (functionBlockId < 1) {
                    return _generateErrorJson("Invalid function block id: " + functionBlockId);
                }
                return _restoreFunctionBlockFromTrash(functionBlockId, currentAccount, environment.getDatabase());
            }
        });

        super._defineEndpoint("function-blocks/<functionBlockId>", HttpMethod.DELETE, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_MODIFY);

                final long functionBlockId = Util.parseLong(parameters.get("functionBlockId"));
                if (functionBlockId < 1) {
                    return _generateErrorJson("Invalid function block id: " + functionBlockId);
                }
                return _deleteFunctionBlock(functionBlockId, currentAccount, environment.getDatabase());
            }
        });

        super._defineEndpoint("function-blocks/<functionBlockId>/function-catalogs", HttpMethod.GET, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_VIEW);

                final long functionBlockId = Util.parseLong(getNthFromLastUrlSegment(request, 1));
                if (functionBlockId < 1) {
                    return _generateErrorJson("Invalid function block id: " + functionBlockId);
                }
                return _listFunctionCatalogsForFunctionBlock(functionBlockId, environment.getDatabase());
            }
        });

        super._defineEndpoint("function-blocks/<functionBlockId>/function-catalogs", HttpMethod.POST, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_MODIFY);

                final long functionBlockId = Util.parseLong(getNthFromLastUrlSegment(request, 1));
                if (functionBlockId < 1) {
                    return _generateErrorJson("Invalid function block id: " + functionBlockId);
                }
                return _associateFunctionBlockWithFunctionCatalog(request, functionBlockId, currentAccount, environment.getDatabase());
            }
        });

        super._defineEndpoint("function-blocks/<functionBlockId>/function-catalogs/<functionCatalogId>", HttpMethod.DELETE, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_MODIFY);

                final long functionBlockId = Util.parseLong(parameters.get("functionBlockId"));
                if (functionBlockId < 1) {
                    return _generateErrorJson("Invalid function block id: " + functionBlockId);
                }

                final long functionCatalogId = Util.parseLong(parameters.get("functionCatalogId"));
                if (functionBlockId < 1) {
                    return _generateErrorJson("Invalid function catalog id: " + functionCatalogId);
                }

                return _disassociateFunctionBlockFromCatalog(functionCatalogId, functionBlockId, currentAccount, environment.getDatabase());
            }
        });

        super._defineEndpoint("function-blocks/<functionBlockId>/submit-for-review", HttpMethod.POST, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_MODIFY);

                final long functionBlockId = Util.parseLong(getNthFromLastUrlSegment(request, 1));
                if (functionBlockId < 1) {
                    return _generateErrorJson("Invalid function block id: " + functionBlockId);
                }
                return _submitFunctionBlockForReview(functionBlockId, currentAccount, environment.getDatabase());
            }
        });

        super._defineEndpoint("function-block-duplicate-check", HttpMethod.POST, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_MODIFY);

                return _checkForDuplicateFunctionBlock(request, environment.getDatabase());
            }
        });

        super._defineEndpoint("trashed-function-blocks", HttpMethod.GET, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_MODIFY);

                return _listAllFunctionBlocks(currentAccount, environment.getDatabase(), true);
            }
        });
    }

    private Json _getFunctionBlock(final long functionBlockId, final Account currentAccount, final Database<Connection> database) {
        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {

            final FunctionBlockInflater functionBlockInflater = new FunctionBlockInflater(databaseConnection);
            final FunctionBlock functionBlock = functionBlockInflater.inflateFunctionBlock(functionBlockId);

            final Json response = new Json(false);

            response.put("functionBlock", _toJson(functionBlock));

            super._setJsonSuccessFields(response);
            return response;
        }
        catch (final DatabaseException exception) {
            _logger.error("Unable to get function block.", exception);
            return super._generateErrorJson("Unable to get function block.");
        }
    }

    protected Json _insertFunctionBlock(final HttpServletRequest request, final Account currentAccount, final Database<Connection> database) throws Exception {
        final Json jsonRequest = _getRequestDataAsJson(request);
        final Json response = _generateSuccessJson();
        final Json functionBlockJson = jsonRequest.get("functionBlock");
        final String requestFunctionCatalogId = jsonRequest.getString("functionCatalogId");
        final Long currentAccountId = currentAccount.getId();

        try {
            final FunctionBlock functionBlock = _populateFunctionBlockFromJson(functionBlockJson, currentAccount, database);
            final DatabaseManager databaseManager = new DatabaseManager(database);

            // If function catalog ID isn't null, insert function block for function catalog
            if (!Util.isBlank(requestFunctionCatalogId)) {
                final Long functionCatalogId = Util.parseLong(requestFunctionCatalogId);
                if (functionCatalogId < 1) {
                    _logger.error("Unable to parse Function Catalog ID: " + functionCatalogId);
                    return super._generateErrorJson("Invalid Function Catalog ID: " + functionCatalogId);
                }

                final DatabaseConnection<Connection> databaseConnection = database.newConnection();
                String errorMessage = FunctionCatalogServlet.canAccountModifyFunctionCatalog(databaseConnection, functionCatalogId, currentAccountId);
                if (errorMessage != null) {
                    errorMessage = "Unable to create function block under function catalog: " + errorMessage;
                    _logger.error(errorMessage);
                    return super._generateErrorJson(errorMessage);
                }

                databaseManager.insertFunctionBlock(functionCatalogId, functionBlock, currentAccountId);
            }
            else {
                databaseManager.insertOrphanedFunctionBlock(functionBlock);
            }

            response.put("functionBlockId", functionBlock.getId());
        }
        catch (final Exception exception) {
            _logger.error("Unable to insert Function Block.", exception);
            return super._generateErrorJson("Unable to insert Function Block: " + exception.getMessage());
        }

        return response;
    }

    protected Json _updateFunctionBlock(final HttpServletRequest httpRequest, final long functionBlockId, final Account currentAccount, final Database<Connection> database) throws Exception {
        final Json response = new Json(false);
        final Json request = _getRequestDataAsJson(httpRequest);
        final Json functionBlockJson = request.get("functionBlock");

        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            final Long currentAccountId = currentAccount.getId();

            String errorMessage = canAccountModifyFunctionBlock(databaseConnection, functionBlockId, currentAccountId);
            if (errorMessage != null) {
                errorMessage = "Unable to update function block: " + errorMessage;
                _logger.error(errorMessage);
                return super._generateErrorJson(errorMessage);
            }

            final FunctionBlock functionBlock = _populateFunctionBlockFromJson(functionBlockJson, currentAccount, database);
            functionBlock.setId(functionBlockId);

            final DatabaseManager databaseManager = new DatabaseManager(database);
            databaseManager.updateFunctionBlock(functionBlock, currentAccountId);

            _logger.info("User " + currentAccountId + " updated function block " + functionBlock.getId() + ", which is currently owned by User " + functionBlock.getCreatorAccountId());
            response.put("functionBlockId", functionBlock.getId());
        }
        catch (final Exception exception) {
            final String errorMessage = "Unable to update function block: " + exception.getMessage();
            _logger.error(errorMessage, exception);
            return super._generateErrorJson(errorMessage);
        }

        super._setJsonSuccessFields(response);
        return response;
    }

    private Json _forkFunctionBlock(final HttpServletRequest httpRequest, final long functionBlockId, final Account currentAccount, final Database<Connection> database) throws IOException {
        final Json response = new Json(false);
        final Json request = _getRequestDataAsJson(httpRequest);
        final String parentFunctionCatalogIdString = request.getString("functionCatalogId");

        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            final Long currentAccountId = currentAccount.getId();

            String errorMessage = canAccountViewFunctionBlock(databaseConnection, functionBlockId, currentAccountId);
            if (errorMessage != null) {
                errorMessage = "Unable to fork function block: " + errorMessage;
                _logger.error(errorMessage);
                return super._generateErrorJson(errorMessage);
            }

            Long parentFunctionCatalogId = Util.parseLong(parentFunctionCatalogIdString);
            if (parentFunctionCatalogId < 1) {
                parentFunctionCatalogId = null;
            }

            final DatabaseManager databaseManager = new DatabaseManager(database);
            final long newFunctionBlockId = databaseManager.forkFunctionBlock(functionBlockId, parentFunctionCatalogId, currentAccountId);

            _logger.info("User " + currentAccountId + " forked function block " + functionBlockId + " (new ID: " + newFunctionBlockId + ").");
            response.put("functionBlockId", newFunctionBlockId);
        }
        catch (final Exception exception) {
            final String errorMessage = "Unable to fork function block: " + exception.getMessage();
            _logger.error(errorMessage, exception);
            return super._generateErrorJson(errorMessage);
        }

        super._setJsonSuccessFields(response);
        return response;
    }

    private Json _checkForDuplicateFunctionBlock(final HttpServletRequest httpRequest, final Database<Connection> database) {
        try {
            final Json request = _getRequestDataAsJson(httpRequest);
            final String functionBlockMostId = request.getString("functionBlockMostId");
            final String functionBlockName = request.getString("functionBlockName");
            final Long functionBlockVersionSeriesId = request.getLong("functionBlockVersionSeriesId");

            DatabaseManager databaseManager = new DatabaseManager(database);
            final FunctionBlock matchedFunctionBlock;

            if (! Util.isBlank(functionBlockMostId)) {
                matchedFunctionBlock = databaseManager.checkForDuplicateFunctionBlockMostId(functionBlockMostId, functionBlockVersionSeriesId);
            }
            else {
                matchedFunctionBlock = databaseManager.checkForDuplicateFunctionBlockName(functionBlockName, functionBlockVersionSeriesId);
            }

            final Json response = new Json(false);

            if (matchedFunctionBlock == null) {
                response.put("matchFound", false);
            } else {
                response.put("matchFound", true);
                response.put("matchedFunctionBlock", _toJson(matchedFunctionBlock));
            }

            super._setJsonSuccessFields(response);
            return response;
        }
        catch (final Exception exception) {
            _logger.error("Unable to check for duplicate Function Block.", exception);
            return super._generateErrorJson("Unable to check for duplicate Function Block: " + exception.getMessage());
        }
    }

    private Json _associateFunctionBlockWithFunctionCatalog(final HttpServletRequest request, final long functionBlockId, final Account currentAccount, final Database<Connection> database) throws IOException {
        final Json jsonRequest = _getRequestDataAsJson(request);
        final Json response = _generateSuccessJson();

        final Long functionCatalogId = Util.parseLong(jsonRequest.getString("functionCatalogId"));

        { // Validate Inputs
            if (functionCatalogId < 1) {
                _logger.error("Unable to parse Function Catalog ID: " + functionCatalogId);
                return super._generateErrorJson("Invalid Function Catalog ID: " + functionCatalogId);
            }
        }

        try {
            final DatabaseManager databaseManager = new DatabaseManager(database);
            final DatabaseConnection<Connection> databaseConnection = database.newConnection();

            String errorMessage = FunctionCatalogServlet.canAccountModifyFunctionCatalog(databaseConnection, functionCatalogId, currentAccount.getId());
            if (errorMessage != null) {
                errorMessage = "Unable to add function block to function catalog: " + errorMessage;
                _logger.error(errorMessage);
                return super._generateErrorJson(errorMessage);
            }

            databaseManager.associateFunctionBlockWithFunctionCatalog(functionCatalogId, functionBlockId);
        }
        catch (final Exception exception) {
            _logger.error("Unable to insert Function Block.", exception);
            return super._generateErrorJson("Unable to insert Function Block: " + exception.getMessage());
        }

        return response;
    }

    protected Json _disassociateFunctionBlockFromCatalog(final long functionCatalogId, final long functionBlockId, final Account currentAccount, final Database<Connection> database) {
        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            // only need to check parent
            String parentErrorMessage = FunctionCatalogServlet.canAccountModifyFunctionCatalog(databaseConnection, functionCatalogId, currentAccount.getId());
            if (parentErrorMessage != null) {
                parentErrorMessage = "Unable to remove function block from parent function catalog: " + parentErrorMessage;
                _logger.error(parentErrorMessage);
                return super._generateErrorJson(parentErrorMessage);
            }

            final DatabaseManager databaseManager = new DatabaseManager(database);
            databaseManager.disassociateFunctionBlockFromFunctionCatalog(functionCatalogId, functionBlockId);
        }
        catch (final DatabaseException exception) {
            final String errorMessage = String.format("Unable to delete function block %d from function catalog %d.", functionBlockId, functionCatalogId);
            _logger.error(errorMessage, exception);
            return super._generateErrorJson(errorMessage);
        }

        final Json response = new Json(false);
        super._setJsonSuccessFields(response);
        return response;
    }

    protected Json _markFunctionBlockAsDeleted(final long functionBlockId, final Account currentAccount, final Database<Connection> database) {
        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            String errorMessage = canAccountViewFunctionBlock(databaseConnection, functionBlockId, currentAccount.getId());
            if (errorMessage != null) {
                errorMessage = "Unable to move function block to trash: " + errorMessage;
                _logger.error(errorMessage);
                return super._generateErrorJson(errorMessage);
            }

            final DatabaseManager databaseManager = new DatabaseManager(database);
            if (databaseManager.functionBlockHasApprovedParents(functionBlockId)) {
                errorMessage = "Unable to move function block to trash: the function block is associated with approved function catalogs.";
                _logger.error(errorMessage);
                return super._generateErrorJson(errorMessage);
            }

            databaseManager.markFunctionBlockAsDeleted(functionBlockId);

            _logger.info("User " + currentAccount.getId() + " marked Function Block " + functionBlockId + " as deleted.");

            final Json response = new Json(false);
            super._setJsonSuccessFields(response);
            return response;
        }
        catch (final DatabaseException exception) {
            final String errorMessage = String.format("Unable to move function block %d to trash", functionBlockId);
            _logger.error(errorMessage, exception);
            return super._generateErrorJson(errorMessage);
        }
    }

    protected Json _restoreFunctionBlockFromTrash(final long functionBlockId, final Account currentAccount, final Database<Connection> database) {
        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            String errorMessage = canAccountModifyFunctionBlock(databaseConnection, functionBlockId, currentAccount.getId());
            if (errorMessage != null) {
                errorMessage = "Unable to restore function block: " + errorMessage;
                _logger.error(errorMessage);
                return super._generateErrorJson(errorMessage);
            }

            final DatabaseManager databaseManager = new DatabaseManager(database);
            final long numberOfDeletedChildren = databaseManager.restoreFunctionBlockFromTrash(functionBlockId);

            _logger.info("User " + currentAccount.getId() + " restored Function Block " + functionBlockId);
            if (numberOfDeletedChildren > 0) {
                _logger.info("Restored Function Block " + functionBlockId + " contains " + numberOfDeletedChildren + " deleted Interface relationships.");
            }

            final Json response = new Json(false);
            response.put("haveChildrenBeenDeleted", numberOfDeletedChildren);
            super._setJsonSuccessFields(response);
            return response;
        }
        catch (final DatabaseException exception) {
            final String errorMessage = String.format("Unable to restore function block %d from trash.", functionBlockId);
            _logger.error(errorMessage, exception);
            return super._generateErrorJson(errorMessage);
        }
    }

    protected Json _deleteFunctionBlock(final long functionBlockId, final Account currentAccount, final Database<Connection> database) {
        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            final Long currentAccountId = currentAccount.getId();
            String errorMessage = canAccountViewFunctionBlock(databaseConnection, functionBlockId, currentAccountId);
            if (errorMessage != null) {
                errorMessage = "Unable to remove function block: " + errorMessage;
                _logger.error(errorMessage);
                return super._generateErrorJson(errorMessage);
            }

            final FunctionBlockInflater functionBlockInflater = new FunctionBlockInflater(databaseConnection);
            final FunctionBlock functionBlock = functionBlockInflater.inflateFunctionBlock(functionBlockId);
            if (!functionBlock.isDeleted()) {
                final String error = "Function block must be moved to trash before deleting.";
                _logger.error(error);
                return super._generateErrorJson(error);
            }

            final DatabaseManager databaseManager = new DatabaseManager(database);
            databaseManager.deleteFunctionBlock(functionBlockId);
        }
        catch (final DatabaseException exception) {
            final String errorMessage = String.format("Unable to delete function block %d: " + exception.getMessage(), functionBlockId);
            _logger.error(errorMessage, exception);
            return super._generateErrorJson(errorMessage);
        }

        final Json response = new Json(false);
        super._setJsonSuccessFields(response);
        return response;
    }

    protected Json _listFunctionBlocks(final long functionCatalogId, final Account currentAccount, final Database<Connection> database) {
        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            final Json response = new Json(false);

            final FunctionBlockInflater functionBlockInflater = new FunctionBlockInflater(databaseConnection);
            final List<FunctionBlock> functionBlocks = functionBlockInflater.inflateFunctionBlocksFromFunctionCatalogId(functionCatalogId);

            final Json functionBlocksJson = new Json(true);
            for (final FunctionBlock functionBlock : functionBlocks) {
                final Json functionBlockJson = _toJson(functionBlock);
                functionBlocksJson.add(functionBlockJson);
            }
            response.put("functionBlocks", functionBlocksJson);

            super._setJsonSuccessFields(response);
            return response;
        }
        catch (final DatabaseException exception) {
            _logger.error("Unable to list function blocks.", exception);
            return super._generateErrorJson("Unable to list function blocks.");
        }
    }

    protected Json _listAllFunctionBlocks(final Account currentAccount, final Database<Connection> database, final boolean onlyListDeleted) {
        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            final Json response = new Json(false);

            final FunctionBlockInflater functionBlockInflater = new FunctionBlockInflater(databaseConnection);
            final Map<Long, List<FunctionBlock>> functionBlocks;

            if (onlyListDeleted) {
                functionBlocks = functionBlockInflater.inflateTrashedFunctionBlocksGroupedByBaseVersionId();
            }
            else {
                functionBlocks = functionBlockInflater.inflateFunctionBlocksGroupedByBaseVersionId();
            }

            final Json functionBlocksJson = new Json(true);
            for (final Long baseVersionId : functionBlocks.keySet()) {
                final Json versionSeriesJson = new Json();
                versionSeriesJson.put("baseVersionId", baseVersionId);

                final Json versionsJson = new Json();
                for (final FunctionBlock functionBlock : functionBlocks.get(baseVersionId)) {
                    if (! functionBlock.isApproved()) {
                        if (functionBlock.getCreatorAccountId() != null) {
                            if (! functionBlock.getCreatorAccountId().equals(currentAccount.getId())) {
                                // Skip adding this function block to the JSON because it is not approved, not unowned, and not owned by the current user.
                                continue;
                            }
                        }
                    }

                    final Json functionBlockJson = _toJson(functionBlock);
                    versionsJson.add(functionBlockJson);
                }

                // Only add versionSeriesJson if its function blocks have not all been filtered.
                if (versionsJson.length() > 0) {
                    versionSeriesJson.put("versions", versionsJson);
                    functionBlocksJson.add(versionSeriesJson);
                }
            }
            response.put("functionBlocks", functionBlocksJson);

            super._setJsonSuccessFields(response);
            return response;
        }
        catch (final DatabaseException exception) {
            _logger.error("Unable to list function blocks.", exception);
            return super._generateErrorJson("Unable to list function blocks.");
        }
    }

    private Json _listFunctionBlocksMatchingSearchString(final String searchString, final boolean includeDeleted, final Account currentAccount, final Database<Connection> database) {
        final String decodedSearchString;
        try {
            decodedSearchString = URLDecoder.decode(searchString, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            _logger.error("Unable to list function blocks from search", e);
            return super._generateErrorJson("Unable to list function blocks from search.");
        }

        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            final Json response = new Json(false);

            final FunctionBlockInflater functionBlockInflater = new FunctionBlockInflater(databaseConnection);
            final Map<Long, List<FunctionBlock>> functionBlocks = functionBlockInflater.inflateFunctionBlocksMatchingSearchString(decodedSearchString, includeDeleted, currentAccount.getId());

            final Json functionBlocksJson = new Json(true);
            for (final Long baseVersionId : functionBlocks.keySet()) {
                final Json versionSeriesJson = new Json();
                versionSeriesJson.put("baseVersionId", baseVersionId);

                final Json versionsJson = new Json();
                for (final FunctionBlock functionBlock : functionBlocks.get(baseVersionId)) {
                    final Json functionBlockJson = _toJson(functionBlock);
                    versionsJson.add(functionBlockJson);
                }
                versionSeriesJson.put("versions", versionsJson);
                functionBlocksJson.add(versionSeriesJson);
            }
            response.put("functionBlocks", functionBlocksJson);

            super._setJsonSuccessFields(response);
            return response;
        }
        catch (final DatabaseException exception) {
            _logger.error("Unable to list function blocks from search", exception);
            return super._generateErrorJson("Unable to list function blocks from search.");
        }
    }

    protected Json _listFunctionCatalogsForFunctionBlock(final long functionBlockId, final Database<Connection> database) {
        try {
            final Json response = new Json(false);

            DatabaseManager databaseManager = new DatabaseManager(database);
            final List<Long> functionCatalogIds = databaseManager.listFunctionCatalogsContainingFunctionBlock(functionBlockId);

            Json functionCatalogIdsJson = new Json(true);
            for (Long functionCatalogId : functionCatalogIds) {
                functionCatalogIdsJson.add(functionCatalogId);
            }
            response.put("functionCatalogIds", functionCatalogIdsJson);

            super._setJsonSuccessFields(response);
            return response;
        }
        catch (final DatabaseException exception) {
            _logger.error("Unable to list function catalogs for function block " + functionBlockId, exception);
            return super._generateErrorJson("Unable to list function catalogs.");
        }
    }

    protected Json _submitFunctionBlockForReview(final long functionBlockId, final Account currentAccount, final Database<Connection> database) {
        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            final Json response = new Json(false);

            String errorMessage = canAccountModifyFunctionBlock(databaseConnection, functionBlockId, currentAccount.getId());
            if (errorMessage != null) {
                errorMessage = "Unable to submit function block for review: " + errorMessage;
                _logger.error(errorMessage);
                return super._generateErrorJson(errorMessage);
            }

            final DatabaseManager databaseManager = new DatabaseManager(database);
            databaseManager.submitFunctionBlockForReview(functionBlockId, currentAccount.getId());

            super._setJsonSuccessFields(response);
            return response;
        } catch (DatabaseException e) {
            String errorMessage = "Unable to submit function block for review.";
            _logger.error(errorMessage, e);
            return _generateErrorJson(errorMessage);
        }
    }

    protected FunctionBlock _populateFunctionBlockFromJson(final Json functionBlockJson, final Account currentAccount, final Database database) throws Exception {
        final String mostId = functionBlockJson.getString("mostId");
        final String kind = functionBlockJson.getString("kind");
        final String name = functionBlockJson.getString("name");
        final String description = functionBlockJson.getString("description");
        final String release = functionBlockJson.getString("releaseVersion");
        final Long authorId = functionBlockJson.getLong("authorId");
        final Long companyId = functionBlockJson.getLong("companyId");
        final Long creatorAccountId = functionBlockJson.getLong("creatorAccountId");
        final String access = functionBlockJson.getString("access");
        final Boolean isSource = functionBlockJson.getBoolean("isSource");
        final Boolean isSink = functionBlockJson.getBoolean("isSink");

        { // Validate Inputs
            if (Util.isBlank(mostId)) {
                throw new Exception("Invalid Most ID");
            }
            if (!mostId.matches("0x[0-9A-F]{2}")) {
                throw new Exception("Function block MOST ID must be between '0x00' and '0xFF'.");
            }

            if (Util.isBlank(kind)) {
                throw new Exception("Invalid Kind value: "+ kind);
            }

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
                throw new Exception("Release field is required.");
            }
            if (!release.matches("[0-9]+\\.[0-9]+(\\.[0-9]+)?")) {
                throw new Exception("Release version must be in the form 'Major.Minor(.Patch)'.");
            }

            if (Util.isBlank(access)) {
                throw new Exception("Access field is required.");
            }

            if (isSource == null) {
                throw new Exception("isSource is required.");
            }

            if (isSink == null) {
                throw new Exception("isSink is required.");
            }
        }

        final Company company;
        final Author author;

        if (authorId >= 1) {
            // use supplied author/account ID
            company = new Company();
            company.setId(companyId);
            author = new Author();
            author.setId(authorId);
        }
        else {
            // use users's account ID
            company = currentAccount.getCompany();
            author = currentAccount.toAuthor();
        }

        FunctionBlock functionBlock = new FunctionBlock();
        functionBlock.setMostId(mostId);
        functionBlock.setKind(kind);
        functionBlock.setName(name);
        functionBlock.setRelease(release);
        functionBlock.setDescription(description);
        functionBlock.setAuthor(author);
        functionBlock.setCompany(company);
        functionBlock.setCreatorAccountId(creatorAccountId > 0 ? creatorAccountId : null);
        functionBlock.setAccess(access);
        functionBlock.setIsSource(isSource);
        functionBlock.setIsSink(isSink);

        return functionBlock;
    }

    private Json _toJson(final FunctionBlock functionBlock) {
        final Json blockJson = new Json(false);

        String deletedDateString = null;
        if (functionBlock.getDeletedDate() != null) {
            deletedDateString = DateUtil.dateToDateString(functionBlock.getDeletedDate());
        }

        blockJson.put("id", functionBlock.getId());
        blockJson.put("mostId", functionBlock.getMostId());
        blockJson.put("kind", functionBlock.getKind());
        blockJson.put("name", functionBlock.getName());
        blockJson.put("description", functionBlock.getDescription());
        blockJson.put("lastModifiedDate", DateUtil.dateToDateString(functionBlock.getLastModifiedDate()));
        blockJson.put("releaseVersion", functionBlock.getRelease());
        blockJson.put("isDeleted", functionBlock.isDeleted());
        blockJson.put("deletedDate", deletedDateString);
        blockJson.put("isReleased", functionBlock.isReleased());
        blockJson.put("isApproved", functionBlock.isApproved());
        blockJson.put("baseVersionId", functionBlock.getBaseVersionId());
        blockJson.put("priorVersionId", functionBlock.getPriorVersionId());
        blockJson.put("authorId", functionBlock.getAuthor().getId());
        blockJson.put("authorName", functionBlock.getAuthor().getName());
        blockJson.put("companyId", functionBlock.getCompany().getId());
        blockJson.put("companyName", functionBlock.getCompany().getName());
        blockJson.put("creatorAccountId", functionBlock.getCreatorAccountId());
        blockJson.put("access", functionBlock.getAccess());
        blockJson.put("isSource", functionBlock.isSource());
        blockJson.put("isSink", functionBlock.isSink());
        return blockJson;
    }

    public static String canAccountViewFunctionBlock(final DatabaseConnection<Connection> databaseConnection, final long functionBlockId, final Long currentAccountId) throws DatabaseException {
        final FunctionBlockInflater functionBlockInflater = new FunctionBlockInflater(databaseConnection);
        final FunctionBlock originalFunctionBlock = functionBlockInflater.inflateFunctionBlock(functionBlockId);

        final String ownerCheckResult = ownerCheck(originalFunctionBlock, currentAccountId);
        if (ownerCheckResult != null) {
            return ownerCheckResult;
        }

        return null;
    }

    public static String canAccountModifyFunctionBlock(final DatabaseConnection<Connection> databaseConnection, final Long functionBlockId, final Long currentAccountId) throws DatabaseException {
        final FunctionBlockInflater functionBlockInflater = new FunctionBlockInflater(databaseConnection);
        final FunctionBlock originalFunctionBlock = functionBlockInflater.inflateFunctionBlock(functionBlockId);

        final String ownerCheckResult = ownerCheck(originalFunctionBlock, currentAccountId);
        if (ownerCheckResult != null) {
            return ownerCheckResult;
        }

        if (originalFunctionBlock.isReleased()) {
            return "Released function blocks cannot be modified.";
        }
        if (originalFunctionBlock.isApproved()) {
            return "Approved function blocks cannot be modified.";
        }

        // good to go
        return null;
    }

    private static String ownerCheck(final FunctionBlock functionBlock, final Long currentAccountId) {
        if (functionBlock.getCreatorAccountId() != null && !functionBlock.isApproved()) {
            if (!functionBlock.getCreatorAccountId().equals(currentAccountId)) {
                return "The function block is owned by another account and cannot be modified.";
            }
        }

        return null;
    }
}
