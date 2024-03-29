package com.softwareverde.tidyduck.api;

import com.softwareverde.database.Database;
import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.http.HttpMethod;
import com.softwareverde.http.server.servlet.request.Request;
import com.softwareverde.http.server.servlet.routed.json.AuthenticatedJsonApplicationServlet;
import com.softwareverde.http.server.servlet.routed.json.JsonApplicationServlet;
import com.softwareverde.http.server.servlet.routed.json.JsonRequestHandler;
import com.softwareverde.http.server.servlet.session.SessionManager;
import com.softwareverde.json.Json;
import com.softwareverde.logging.Logger;
import com.softwareverde.tidyduck.Account;
import com.softwareverde.tidyduck.AccountId;
import com.softwareverde.tidyduck.DateUtil;
import com.softwareverde.tidyduck.Permission;
import com.softwareverde.tidyduck.authentication.TidyDuckAuthenticator;
import com.softwareverde.tidyduck.database.DatabaseManager;
import com.softwareverde.tidyduck.database.FunctionBlockInflater;
import com.softwareverde.tidyduck.environment.TidyDuckEnvironment;
import com.softwareverde.tidyduck.most.Author;
import com.softwareverde.tidyduck.most.Company;
import com.softwareverde.tidyduck.most.FunctionBlock;
import com.softwareverde.tidyduck.util.Util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import static com.softwareverde.tidyduck.util.Util.getNthFromLastUrlSegment;

public class FunctionBlockServlet extends AuthenticatedJsonApplicationServlet<TidyDuckEnvironment> {
    

    public FunctionBlockServlet(final TidyDuckEnvironment environment, final SessionManager sessionManager, final TidyDuckAuthenticator authenticator) {
        super(environment, sessionManager);
        
        super._defineEndpoint("function-blocks", HttpMethod.GET, new TidyDuckRequestHandler(sessionManager, authenticator) {
            @Override
            public Json handleRequest(final Account currentAccount, final Request request, final TidyDuckEnvironment environment, final Map<String, String> parameters) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_VIEW);

                final String requestFunctionCatalogId = request.getGetParameters().get("function_catalog_id");

                if (Util.isBlank(requestFunctionCatalogId)) {
                    return _listAllFunctionBlocks(currentAccount, environment.getDatabase(), false);
                }

                final long functionCatalogId = Util.parseLong(Util.coalesce(requestFunctionCatalogId));
                if (functionCatalogId < 1) {
                    throw new IllegalArgumentException("Invalid function catalog id: " + functionCatalogId);
                }

                return _listFunctionBlocks(functionCatalogId, currentAccount, environment.getDatabase());
            }
        });

        super._defineEndpoint("function-blocks", HttpMethod.POST, new TidyDuckRequestHandler(sessionManager, authenticator) {
            @Override
            public Json handleRequest(final Account currentAccount, final Request request, final TidyDuckEnvironment environment, final Map<String, String> parameters) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_CREATE);

                return _insertFunctionBlock(request, currentAccount, environment.getDatabase());
            }
        });

        super._defineEndpoint("function-blocks/search/<name>", HttpMethod.GET, new TidyDuckRequestHandler(sessionManager, authenticator) {
            @Override
            public Json handleRequest(final Account currentAccount, final Request request, final TidyDuckEnvironment environment, final Map<String, String> parameters) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_VIEW);

                final String searchString = Util.coalesce(parameters.get("name"));
                if (searchString.length() < 2) {
                    throw new IllegalArgumentException("Invalid search string for function block.");
                }
                // include deleted items unless requested not to
                final String includeDeleteString = request.getGetParameters().get("includeDeleted");
                boolean includeDeleted = !"false".equals(includeDeleteString);

                return _listFunctionBlocksMatchingSearchString(searchString, includeDeleted, currentAccount, environment.getDatabase());
            }
        });

        super._defineEndpoint("function-blocks/<functionBlockId>", HttpMethod.GET, new TidyDuckRequestHandler(sessionManager, authenticator) {
            @Override
            public Json handleRequest(final Account currentAccount, final Request request, final TidyDuckEnvironment environment, final Map<String, String> parameters) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_VIEW);

                final long functionBlockId = Util.parseLong(parameters.get("functionBlockId"));
                if (functionBlockId < 1) {
                    throw new IllegalArgumentException("Invalid function block id: " + functionBlockId);
                }
                return _getFunctionBlock(functionBlockId, currentAccount, environment.getDatabase());
            }
        });

        super._defineEndpoint("function-blocks/<functionBlockId>", HttpMethod.POST, new TidyDuckRequestHandler(sessionManager, authenticator) {
            @Override
            public Json handleRequest(final Account currentAccount, final Request request, final TidyDuckEnvironment environment, final Map<String, String> parameters) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_MODIFY);

                final long functionBlockId = Util.parseLong(parameters.get("functionBlockId"));
                if (functionBlockId < 1) {
                    throw new IllegalArgumentException("Invalid function block id: " + functionBlockId);
                }
                return _updateFunctionBlock(request, functionBlockId, currentAccount, environment.getDatabase());
            }
        });

        super._defineEndpoint("function-blocks/<functionBlockId>/fork", HttpMethod.POST, new TidyDuckRequestHandler(sessionManager, authenticator) {
            @Override
            public Json handleRequest(final Account currentAccount, final Request request, final TidyDuckEnvironment environment, final Map<String, String> parameters) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_CREATE);

                final long functionBlockId = Util.parseLong(parameters.get("functionBlockId"));
                if (functionBlockId < 1) {
                    throw new IllegalArgumentException("Invalid function block id: " + functionBlockId);
                }
                return _forkFunctionBlock(request, functionBlockId, currentAccount, environment.getDatabase());
            }
        });

        super._defineEndpoint("function-blocks/<functionBlockId>/mark-as-deleted", HttpMethod.POST, new TidyDuckRequestHandler(sessionManager, authenticator) {
            @Override
            public Json handleRequest(final Account currentAccount, final Request request, final TidyDuckEnvironment environment, final Map<String, String> parameters) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_MODIFY);

                final long functionBlockId = Util.parseLong(parameters.get("functionBlockId"));
                if (functionBlockId < 1) {
                    throw new IllegalArgumentException("Invalid function block id: " + functionBlockId);
                }
                return _markFunctionBlockAsDeleted(functionBlockId, currentAccount, environment.getDatabase());
            }
        });

        super._defineEndpoint("function-blocks/<functionBlockId>/restore-from-trash", HttpMethod.POST, new TidyDuckRequestHandler(sessionManager, authenticator) {
            @Override
            public Json handleRequest(final Account currentAccount, final Request request, final TidyDuckEnvironment environment, final Map<String, String> parameters) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_MODIFY);

                final long functionBlockId = Util.parseLong(parameters.get("functionBlockId"));
                if (functionBlockId < 1) {
                    throw new IllegalArgumentException("Invalid function block id: " + functionBlockId);
                }
                return _restoreFunctionBlockFromTrash(functionBlockId, currentAccount, environment.getDatabase());
            }
        });

        super._defineEndpoint("function-blocks/<functionBlockId>", HttpMethod.DELETE, new TidyDuckRequestHandler(sessionManager, authenticator) {
            @Override
            public Json handleRequest(final Account currentAccount, final Request request, final TidyDuckEnvironment environment, final Map<String, String> parameters) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_MODIFY);

                final long functionBlockId = Util.parseLong(parameters.get("functionBlockId"));
                if (functionBlockId < 1) {
                    throw new IllegalArgumentException("Invalid function block id: " + functionBlockId);
                }
                return _deleteFunctionBlock(functionBlockId, currentAccount, environment.getDatabase());
            }
        });

        super._defineEndpoint("function-blocks/<functionBlockId>/function-catalogs", HttpMethod.GET, new TidyDuckRequestHandler(sessionManager, authenticator) {
            @Override
            public Json handleRequest(final Account currentAccount, final Request request, final TidyDuckEnvironment environment, final Map<String, String> parameters) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_VIEW);

                final long functionBlockId = Util.parseLong(getNthFromLastUrlSegment(request, 1));
                if (functionBlockId < 1) {
                    throw new IllegalArgumentException("Invalid function block id: " + functionBlockId);
                }
                return _listFunctionCatalogsForFunctionBlock(functionBlockId, environment.getDatabase());
            }
        });

        super._defineEndpoint("function-blocks/<functionBlockId>/function-catalogs", HttpMethod.POST, new TidyDuckRequestHandler(sessionManager, authenticator) {
            @Override
            public Json handleRequest(final Account currentAccount, final Request request, final TidyDuckEnvironment environment, final Map<String, String> parameters) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_MODIFY);

                final long functionBlockId = Util.parseLong(getNthFromLastUrlSegment(request, 1));
                if (functionBlockId < 1) {
                    throw new IllegalArgumentException("Invalid function block id: " + functionBlockId);
                }
                return _associateFunctionBlockWithFunctionCatalog(request, functionBlockId, currentAccount, environment.getDatabase());
            }
        });

        super._defineEndpoint("function-blocks/<functionBlockId>/function-catalogs/<functionCatalogId>", HttpMethod.DELETE, new TidyDuckRequestHandler(sessionManager, authenticator) {
            @Override
            public Json handleRequest(final Account currentAccount, final Request request, final TidyDuckEnvironment environment, final Map<String, String> parameters) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_MODIFY);

                final long functionBlockId = Util.parseLong(parameters.get("functionBlockId"));
                if (functionBlockId < 1) {
                    throw new IllegalArgumentException("Invalid function block id: " + functionBlockId);
                }

                final long functionCatalogId = Util.parseLong(parameters.get("functionCatalogId"));
                if (functionCatalogId < 1) {
                    throw new IllegalArgumentException("Invalid function catalog id: " + functionCatalogId);
                }

                return _disassociateFunctionBlockFromCatalog(functionCatalogId, functionBlockId, currentAccount, environment.getDatabase());
            }
        });

        super._defineEndpoint("function-blocks/<functionBlockId>/submit-for-review", HttpMethod.POST, new TidyDuckRequestHandler(sessionManager, authenticator) {
            @Override
            public Json handleRequest(final Account currentAccount, final Request request, final TidyDuckEnvironment environment, final Map<String, String> parameters) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_MODIFY);

                final long functionBlockId = Util.parseLong(getNthFromLastUrlSegment(request, 1));
                if (functionBlockId < 1) {
                    throw new IllegalArgumentException("Invalid function block id: " + functionBlockId);
                }
                return _submitFunctionBlockForReview(functionBlockId, currentAccount, environment.getDatabase());
            }
        });

        super._defineEndpoint("function-blocks/duplicate-check", HttpMethod.POST, new TidyDuckRequestHandler(sessionManager, authenticator) {
            @Override
            public Json handleRequest(final Account currentAccount, final Request request, final TidyDuckEnvironment environment, final Map<String, String> parameters) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_MODIFY);

                return _checkForDuplicateFunctionBlock(request, environment.getDatabase());
            }
        });

        super._defineEndpoint("function-blocks/trashed", HttpMethod.GET, new TidyDuckRequestHandler(sessionManager, authenticator) {
            @Override
            public Json handleRequest(final Account currentAccount, final Request request, final TidyDuckEnvironment environment, final Map<String, String> parameters) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_MODIFY);

                return _listAllFunctionBlocks(currentAccount, environment.getDatabase(), true);
            }
        });
    }

    private Json _getFunctionBlock(final long functionBlockId, final Account currentAccount, final Database<Connection> database) throws Exception {
        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {

            final FunctionBlockInflater functionBlockInflater = new FunctionBlockInflater(databaseConnection);
            final FunctionBlock functionBlock = functionBlockInflater.inflateFunctionBlock(functionBlockId);

            final Json response = new Json(false);

            response.put("functionBlock", _toJson(functionBlock));

            JsonRequestHandler.setJsonSuccessFields(response);
            return response;
        }
        catch (final DatabaseException exception) {
            throw new Exception("Unable to get function block.", exception);
        }
    }

    protected Json _insertFunctionBlock(final Request request, final Account currentAccount, final Database<Connection> database) throws Exception {
        final Json jsonRequest = JsonRequestHandler.getRequestDataAsJson(request);
        final Json response = JsonRequestHandler.generateSuccessJson();
        final Json functionBlockJson = jsonRequest.get("functionBlock");
        final String requestFunctionCatalogId = jsonRequest.getString("functionCatalogId");
        final AccountId currentAccountId = currentAccount.getId();

        try {
            final FunctionBlock functionBlock = _populateFunctionBlockFromJson(functionBlockJson, currentAccount, database);
            final DatabaseManager databaseManager = new DatabaseManager(database);

            // If function catalog ID isn't null, insert function block for function catalog
            if (!Util.isBlank(requestFunctionCatalogId)) {
                final Long functionCatalogId = Util.parseLong(requestFunctionCatalogId);
                if (functionCatalogId < 1) {
                    Logger.error("Unable to parse Function Catalog ID: " + functionCatalogId);
                    throw new IllegalArgumentException("Invalid Function Catalog ID: " + functionCatalogId);
                }

                try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
                    String errorMessage = FunctionCatalogServlet.canAccountModifyFunctionCatalog(databaseConnection, functionCatalogId, currentAccountId);
                    if (errorMessage != null) {
                        errorMessage = "Unable to create function block under function catalog: " + errorMessage;
                        throw new Exception(errorMessage);
                    }

                    databaseManager.insertFunctionBlock(functionCatalogId, functionBlock, currentAccountId);
                }
            }
            else {
                databaseManager.insertOrphanedFunctionBlock(functionBlock);
            }

            response.put("functionBlockId", functionBlock.getId());
        }
        catch (final Exception exception) {
            throw new Exception("Unable to insert Function Block.", exception);
        }

        return response;
    }

    protected Json _updateFunctionBlock(final Request httpRequest, final long functionBlockId, final Account currentAccount, final Database<Connection> database) throws Exception {
        final Json response = new Json(false);
        final Json request = JsonRequestHandler.getRequestDataAsJson(httpRequest);
        final Json functionBlockJson = request.get("functionBlock");

        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            final AccountId currentAccountId = currentAccount.getId();

            String errorMessage = canAccountModifyFunctionBlock(databaseConnection, functionBlockId, currentAccountId);
            if (errorMessage != null) {
                errorMessage = "Unable to update function block: " + errorMessage;
                throw new Exception(errorMessage);
            }

            final FunctionBlock functionBlock = _populateFunctionBlockFromJson(functionBlockJson, currentAccount, database);
            functionBlock.setId(functionBlockId);

            final DatabaseManager databaseManager = new DatabaseManager(database);
            databaseManager.updateFunctionBlock(functionBlock, currentAccountId);

            Logger.info("User " + currentAccountId + " updated function block " + functionBlock.getId() + ", which is currently owned by User " + functionBlock.getCreatorAccountId());
            response.put("functionBlockId", functionBlock.getId());
        }
        catch (final Exception exception) {
            final String errorMessage = "Unable to update function block: " + exception.getMessage();
            throw new Exception(errorMessage, exception);
        }

        JsonRequestHandler.setJsonSuccessFields(response);
        return response;
    }

    private Json _forkFunctionBlock(final Request httpRequest, final long functionBlockId, final Account currentAccount, final Database<Connection> database) throws Exception {
        final Json response = new Json(false);
        final Json request = JsonRequestHandler.getRequestDataAsJson(httpRequest);
        final String parentFunctionCatalogIdString = request.getString("functionCatalogId");

        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            final AccountId currentAccountId = currentAccount.getId();

            String errorMessage = canAccountViewFunctionBlock(databaseConnection, functionBlockId, currentAccountId);
            if (errorMessage != null) {
                errorMessage = "Unable to fork function block: " + errorMessage;
                throw new Exception(errorMessage);
            }


            Long parentFunctionCatalogId = Util.parseLong(parentFunctionCatalogIdString);
            if (parentFunctionCatalogId < 1) {
                parentFunctionCatalogId = null;
            }

            if (parentFunctionCatalogId != null) {
                String parentErrorMessage = FunctionCatalogServlet.canAccountModifyFunctionCatalog(databaseConnection, parentFunctionCatalogId, currentAccountId);
                if (parentErrorMessage != null) {
                    parentErrorMessage = "Unable to fork function block: " + parentErrorMessage;
                    throw new Exception(parentErrorMessage);
                }
            }

            final DatabaseManager databaseManager = new DatabaseManager(database);
            final long newFunctionBlockId = databaseManager.forkFunctionBlock(functionBlockId, parentFunctionCatalogId, currentAccountId);

            Logger.info("User " + currentAccountId + " forked function block " + functionBlockId + " (new ID: " + newFunctionBlockId + ").");
            response.put("functionBlockId", newFunctionBlockId);
        }
        catch (final Exception exception) {
            final String errorMessage = "Unable to fork function block: " + exception.getMessage();
            throw new Exception(errorMessage, exception);
        }

        JsonRequestHandler.setJsonSuccessFields(response);
        return response;
    }

    private Json _checkForDuplicateFunctionBlock(final Request httpRequest, final Database<Connection> database) throws Exception {
        try {
            final Json request = JsonRequestHandler.getRequestDataAsJson(httpRequest);
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

            JsonRequestHandler.setJsonSuccessFields(response);
            return response;
        }
        catch (final Exception exception) {
            throw new Exception("Unable to check for duplicate Function Block.", exception);
        }
    }

    private Json _associateFunctionBlockWithFunctionCatalog(final Request request, final long functionBlockId, final Account currentAccount, final Database<Connection> database) throws Exception {
        final Json jsonRequest = JsonRequestHandler.getRequestDataAsJson(request);
        final Json response = JsonRequestHandler.generateSuccessJson();

        final Long functionCatalogId = Util.parseLong(jsonRequest.getString("functionCatalogId"));

        { // Validate Inputs
            if (functionCatalogId < 1) {
                Logger.error("Unable to parse Function Catalog ID: " + functionCatalogId);
                throw new IllegalArgumentException("Invalid Function Catalog ID: " + functionCatalogId);
            }
        }

        final DatabaseManager databaseManager = new DatabaseManager(database);
        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            String errorMessage = FunctionCatalogServlet.canAccountModifyFunctionCatalog(databaseConnection, functionCatalogId, currentAccount.getId());
            if (errorMessage != null) {
                errorMessage = "Unable to add function block to function catalog: " + errorMessage;
                throw new Exception(errorMessage);
            }

            databaseManager.associateFunctionBlockWithFunctionCatalog(functionCatalogId, functionBlockId);
        }
        catch (final Exception exception) {
            throw new Exception("Unable to insert Function Block.", exception);
        }

        return response;
    }

    protected Json _disassociateFunctionBlockFromCatalog(final long functionCatalogId, final long functionBlockId, final Account currentAccount, final Database<Connection> database) throws Exception {
        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            // only need to check parent
            String parentErrorMessage = FunctionCatalogServlet.canAccountModifyFunctionCatalog(databaseConnection, functionCatalogId, currentAccount.getId());
            if (parentErrorMessage != null) {
                parentErrorMessage = "Unable to remove function block from parent function catalog: " + parentErrorMessage;
                throw new Exception(parentErrorMessage);
            }

            final DatabaseManager databaseManager = new DatabaseManager(database);
            databaseManager.disassociateFunctionBlockFromFunctionCatalog(functionCatalogId, functionBlockId);
        }
        catch (final DatabaseException exception) {
            final String errorMessage = String.format("Unable to delete function block %d from function catalog %d.", functionBlockId, functionCatalogId);
            throw new Exception(errorMessage);
        }

        final Json response = new Json(false);
        JsonRequestHandler.setJsonSuccessFields(response);
        return response;
    }

    protected Json _markFunctionBlockAsDeleted(final long functionBlockId, final Account currentAccount, final Database<Connection> database) throws Exception {
        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            String errorMessage = canAccountViewFunctionBlock(databaseConnection, functionBlockId, currentAccount.getId());
            if (errorMessage != null) {
                errorMessage = "Unable to move function block to trash: " + errorMessage;
                throw new Exception(errorMessage);
            }

            final DatabaseManager databaseManager = new DatabaseManager(database);
            if (databaseManager.functionBlockHasApprovedParents(functionBlockId)) {
                errorMessage = "Unable to move function block to trash: the function block is associated with approved function catalogs.";
                throw new Exception(errorMessage);
            }

            databaseManager.markFunctionBlockAsDeleted(functionBlockId);

            Logger.info("User " + currentAccount.getId() + " marked Function Block " + functionBlockId + " as deleted.");

            final Json response = new Json(false);
            JsonRequestHandler.setJsonSuccessFields(response);
            return response;
        }
        catch (final DatabaseException exception) {
            final String errorMessage = String.format("Unable to move function block %d to trash", functionBlockId);
            throw new Exception(errorMessage, exception);
        }
    }

    protected Json _restoreFunctionBlockFromTrash(final long functionBlockId, final Account currentAccount, final Database<Connection> database) throws Exception {
        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            String errorMessage = canAccountViewFunctionBlock(databaseConnection, functionBlockId, currentAccount.getId());
            if (errorMessage != null) {
                errorMessage = "Unable to restore function block: " + errorMessage;
                throw new Exception(errorMessage);
            }

            final DatabaseManager databaseManager = new DatabaseManager(database);
            final long numberOfDeletedChildren = databaseManager.restoreFunctionBlockFromTrash(functionBlockId);

            Logger.info("User " + currentAccount.getId() + " restored Function Block " + functionBlockId);
            if (numberOfDeletedChildren > 0) {
                Logger.info("Restored Function Block " + functionBlockId + " contains " + numberOfDeletedChildren + " deleted Interface relationships.");
            }

            final Json response = new Json(false);
            response.put("haveChildrenBeenDeleted", numberOfDeletedChildren);
            JsonRequestHandler.setJsonSuccessFields(response);
            return response;
        }
        catch (final DatabaseException exception) {
            final String errorMessage = String.format("Unable to restore function block %d from trash.", functionBlockId);
            throw new Exception(errorMessage, exception);
        }
    }

    protected Json _deleteFunctionBlock(final long functionBlockId, final Account currentAccount, final Database<Connection> database) throws Exception {
        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            final AccountId currentAccountId = currentAccount.getId();
            String errorMessage = canAccountViewFunctionBlock(databaseConnection, functionBlockId, currentAccountId);
            if (errorMessage != null) {
                errorMessage = "Unable to remove function block: " + errorMessage;
                throw new Exception(errorMessage);
            }

            final FunctionBlockInflater functionBlockInflater = new FunctionBlockInflater(databaseConnection);
            final FunctionBlock functionBlock = functionBlockInflater.inflateFunctionBlock(functionBlockId);
            if (!functionBlock.isDeleted()) {
                final String error = "Function block must be moved to trash before deleting.";
                throw new Exception(error);
            }

            final DatabaseManager databaseManager = new DatabaseManager(database);
            databaseManager.deleteFunctionBlock(functionBlockId);
        }
        catch (final DatabaseException exception) {
            final String errorMessage = String.format("Unable to delete function block %d: " + exception.getMessage(), functionBlockId);
            throw new Exception(errorMessage, exception);
        }

        final Json response = new Json(false);
        JsonRequestHandler.setJsonSuccessFields(response);
        return response;
    }

    protected Json _listFunctionBlocks(final long functionCatalogId, final Account currentAccount, final Database<Connection> database) throws Exception {
        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            final Json response = new Json(false);

            final FunctionBlockInflater functionBlockInflater = new FunctionBlockInflater(databaseConnection);
            final List<FunctionBlock> functionBlocks = functionBlockInflater.inflateFunctionBlocksFromFunctionCatalogId(functionCatalogId);

            final Json functionBlocksJson = new Json(true);
            for (final FunctionBlock functionBlock : functionBlocks) {
                if (!functionBlock.isPermanentlyDeleted()) {
                    final Json functionBlockJson = _toJson(functionBlock);
                    functionBlocksJson.add(functionBlockJson);
                }
            }
            response.put("functionBlocks", functionBlocksJson);

            JsonRequestHandler.setJsonSuccessFields(response);
            return response;
        }
        catch (final DatabaseException exception) {
            throw new Exception("Unable to list function blocks.", exception);
        }
    }

    protected Json _listAllFunctionBlocks(final Account currentAccount, final Database<Connection> database, final boolean onlyListDeleted) throws Exception {
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
                    if (!functionBlock.isPermanentlyDeleted()) {
                        if (canAccountViewFunctionBlock(functionBlock, currentAccount.getId()) == null) {
                            // can view this function block, add it to the results
                            final Json functionBlockJson = _toJson(functionBlock);
                            versionsJson.add(functionBlockJson);
                        }
                    }
                }

                // Only add versionSeriesJson if its function blocks have not all been filtered.
                if (versionsJson.length() > 0) {
                    versionSeriesJson.put("versions", versionsJson);
                    functionBlocksJson.add(versionSeriesJson);
                }
            }
            response.put("functionBlocks", functionBlocksJson);

            JsonRequestHandler.setJsonSuccessFields(response);
            return response;
        }
        catch (final DatabaseException exception) {
            throw new Exception("Unable to list function blocks.", exception);
        }
    }

    private Json _listFunctionBlocksMatchingSearchString(final String searchString, final boolean includeDeleted, final Account currentAccount, final Database<Connection> database) throws Exception {
        final String decodedSearchString;
        try {
            decodedSearchString = URLDecoder.decode(searchString, "UTF-8");
        }
        catch (final UnsupportedEncodingException exception) {
            throw new Exception("Unable to list function blocks from search", exception);
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
                    if (!functionBlock.isPermanentlyDeleted()) {
                        final Json functionBlockJson = _toJson(functionBlock);
                        versionsJson.add(functionBlockJson);
                    }
                }
                versionSeriesJson.put("versions", versionsJson);
                functionBlocksJson.add(versionSeriesJson);
            }
            response.put("functionBlocks", functionBlocksJson);

            JsonRequestHandler.setJsonSuccessFields(response);
            return response;
        }
        catch (final DatabaseException exception) {
            throw new Exception("Unable to list function blocks from search", exception);
        }
    }

    protected Json _listFunctionCatalogsForFunctionBlock(final long functionBlockId, final Database<Connection> database) throws Exception {
        try {
            final Json response = new Json(false);

            DatabaseManager databaseManager = new DatabaseManager(database);
            final List<Long> functionCatalogIds = databaseManager.listFunctionCatalogsContainingFunctionBlock(functionBlockId);

            Json functionCatalogIdsJson = new Json(true);
            for (Long functionCatalogId : functionCatalogIds) {
                functionCatalogIdsJson.add(functionCatalogId);
            }
            response.put("functionCatalogIds", functionCatalogIdsJson);

            JsonRequestHandler.setJsonSuccessFields(response);
            return response;
        }
        catch (final DatabaseException exception) {
            throw new Exception("Unable to list function catalogs for function block " + functionBlockId, exception);
        }
    }

    protected Json _submitFunctionBlockForReview(final long functionBlockId, final Account currentAccount, final Database<Connection> database) throws Exception {
        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            final Json response = new Json(false);

            String errorMessage = canAccountModifyFunctionBlock(databaseConnection, functionBlockId, currentAccount.getId());
            if (errorMessage != null) {
                errorMessage = "Unable to submit function block for review: " + errorMessage;
                throw new Exception(errorMessage);
            }

            final DatabaseManager databaseManager = new DatabaseManager(database);
            databaseManager.submitFunctionBlockForReview(functionBlockId, currentAccount.getId());

            JsonRequestHandler.setJsonSuccessFields(response);
            return response;
        } catch (final DatabaseException exception) {
            String errorMessage = "Unable to submit function block for review.";
            throw new Exception(errorMessage);
        }
    }

    protected FunctionBlock _populateFunctionBlockFromJson(final Json functionBlockJson, final Account currentAccount, final Database database) throws Exception {
        final String mostId = functionBlockJson.getString("mostId");
        final String kind = functionBlockJson.getString("kind");
        final String name = functionBlockJson.getString("name");
        final String description = functionBlockJson.getString("description");
        final String release = functionBlockJson.getString("releaseVersion");
        final AccountId authorId = AccountId.wrap(functionBlockJson.getLong("authorId"));
        final Long companyId = functionBlockJson.getLong("companyId");
        final AccountId creatorAccountId = AccountId.wrap(functionBlockJson.getLong("creatorAccountId"));
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

        if (authorId.longValue() >= 1) {
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
        functionBlock.setCreatorAccountId(creatorAccountId.longValue() > 0 ? creatorAccountId : null);
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
        blockJson.put("approvalReviewId", functionBlock.getApprovalReviewId());
        blockJson.put("hasApprovedParent", functionBlock.getHasApprovedParent());
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

    public static String canAccountViewFunctionBlock(final DatabaseConnection<Connection> databaseConnection, final long functionBlockId, final AccountId currentAccountId) throws DatabaseException {
        final FunctionBlockInflater functionBlockInflater = new FunctionBlockInflater(databaseConnection);
        final FunctionBlock originalFunctionBlock = functionBlockInflater.inflateFunctionBlock(functionBlockId);

        return canAccountViewFunctionBlock(originalFunctionBlock, currentAccountId);
    }

    public static String canAccountViewFunctionBlock(final FunctionBlock originalFunctionBlock, final AccountId currentAccountId) {
        return ownerCheck(originalFunctionBlock, currentAccountId);
    }

    public static String canAccountModifyFunctionBlock(final DatabaseConnection<Connection> databaseConnection, final Long functionBlockId, final AccountId currentAccountId) throws DatabaseException {
        final FunctionBlockInflater functionBlockInflater = new FunctionBlockInflater(databaseConnection);
        final FunctionBlock originalFunctionBlock = functionBlockInflater.inflateFunctionBlock(functionBlockId);

        return canAccountModifyFunctionBlock(originalFunctionBlock, currentAccountId);
    }

    public static String canAccountModifyFunctionBlock(final FunctionBlock originalFunctionBlock, final AccountId currentAccountId) {
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

    private static String ownerCheck(final FunctionBlock functionBlock, final AccountId currentAccountId) {
        if (functionBlock.getCreatorAccountId() != null && !functionBlock.isApproved()) {
            if (!functionBlock.getCreatorAccountId().equals(currentAccountId)) {
                return "The function block is owned by another account and cannot be modified.";
            }
        }

        return null;
    }
}
