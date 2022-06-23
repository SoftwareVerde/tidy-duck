package com.softwareverde.tidyduck.api;

import com.softwareverde.database.Database;
import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.http.HttpMethod;
import com.softwareverde.http.server.servlet.request.Request;
import com.softwareverde.http.server.servlet.routed.json.AuthenticatedJsonApplicationServlet;
import com.softwareverde.http.server.servlet.routed.json.JsonRequestHandler;
import com.softwareverde.http.server.servlet.session.SessionManager;
import com.softwareverde.json.Json;
import com.softwareverde.logging.Logger;
import com.softwareverde.tidyduck.*;
import com.softwareverde.tidyduck.authentication.TidyDuckAuthenticator;
import com.softwareverde.tidyduck.database.DatabaseManager;
import com.softwareverde.tidyduck.database.FunctionCatalogInflater;
import com.softwareverde.tidyduck.environment.TidyDuckEnvironment;
import com.softwareverde.tidyduck.most.Author;
import com.softwareverde.tidyduck.most.Company;
import com.softwareverde.tidyduck.most.FunctionCatalog;
import com.softwareverde.tidyduck.util.Util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FunctionCatalogServlet extends AuthenticatedJsonApplicationServlet<TidyDuckEnvironment> {
    public FunctionCatalogServlet(final TidyDuckEnvironment environment, final SessionManager sessionManager, final TidyDuckAuthenticator authenticator) {
        super(environment, sessionManager);

        super._defineEndpoint("function-catalogs", HttpMethod.GET, new TidyDuckRequestHandler(sessionManager, authenticator) {
            @Override
            protected Json handleRequest(final Account currentAccount, final Request request, final TidyDuckEnvironment environment, final Map<String, String> parameters) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_VIEW);

                return _listFunctionCatalogs(currentAccount, environment.getDatabase(), false);
            }
        });

        super._defineEndpoint("function-catalogs", HttpMethod.POST, new TidyDuckRequestHandler(sessionManager, authenticator) {

            @Override
            protected Json handleRequest(final Account currentAccount, final Request request, final TidyDuckEnvironment environment, final Map<String, String> parameters) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_CREATE);

                return _insertFunctionCatalog(request, currentAccount, environment.getDatabase());
            }
        });

        super._defineEndpoint("function-catalogs/<functionCatalogId>", HttpMethod.GET, new TidyDuckRequestHandler(sessionManager, authenticator) {

            @Override
            protected Json handleRequest(final Account currentAccount, final Request request, final TidyDuckEnvironment environment, final Map<String, String> parameters) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_VIEW);

                final Long functionCatalogId = Util.parseLong(parameters.get("functionCatalogId"));
                if (functionCatalogId < 1) {
                    throw new IllegalArgumentException("Invalid function catalog ID");
                }
                return _getFunctionCatalog(functionCatalogId, currentAccount, environment.getDatabase());
            }
        });

        super._defineEndpoint("function-catalogs/search/<name>", HttpMethod.GET, new TidyDuckRequestHandler(sessionManager, authenticator) {

            @Override
            protected Json handleRequest(final Account currentAccount, final Request request, final TidyDuckEnvironment environment, final Map<String, String> parameters) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_VIEW);

                final String searchString = Util.coalesce(parameters.get("name"));
                if (searchString.length() < 2) {
                    throw new IllegalArgumentException("Invalid search string for function catalog.");
                }
                // include deleted items unless requested not to
                final String includeDeleteString = request.getGetParameters().get("includeDeleted");
                boolean includeDeleted = !"false".equals(includeDeleteString);

                return _listFunctionCatalogsMatchingSearchString(searchString, includeDeleted, currentAccount, environment.getDatabase());
            }
        });

        super._defineEndpoint("function-catalogs/<functionCatalogId>", HttpMethod.POST, new TidyDuckRequestHandler(sessionManager, authenticator) {

            @Override
            protected Json handleRequest(final Account currentAccount, final Request request, final TidyDuckEnvironment environment, final Map<String, String> parameters) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_MODIFY);

                final Long functionCatalogId = Util.parseLong(parameters.get("functionCatalogId"));
                if (functionCatalogId < 1) {
                    throw new IllegalArgumentException("Invalid function catalog ID.");
                }
                return _updateFunctionCatalog(request, functionCatalogId, currentAccount, environment.getDatabase());

            }
        });

        super._defineEndpoint("function-catalogs/<functionCatalogId>/fork", HttpMethod.POST, new TidyDuckRequestHandler(sessionManager, authenticator) {

            @Override
            protected Json handleRequest(final Account currentAccount, final Request request, final TidyDuckEnvironment environment, final Map<String, String> parameters) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_CREATE);

                final Long functionCatalogId = Util.parseLong(parameters.get("functionCatalogId"));
                if (functionCatalogId < 1) {
                    throw new IllegalArgumentException("Invalid function catalog ID.");
                }
                return _forkFunctionCatalog(functionCatalogId, currentAccount, environment.getDatabase());
            }
        });

        super._defineEndpoint("function-catalogs/<functionCatalogId>/mark-as-deleted", HttpMethod.POST, new TidyDuckRequestHandler(sessionManager, authenticator) {

            @Override
            protected Json handleRequest(final Account currentAccount, final Request request, final TidyDuckEnvironment environment, final Map<String, String> parameters) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_MODIFY);

                final Long functionCatalogId = Util.parseLong(parameters.get("functionCatalogId"));
                if (functionCatalogId < 1) {
                    throw new IllegalArgumentException("Invalid function catalog ID.");
                }
                return _markFunctionCatalogAsDeleted(functionCatalogId, currentAccount, environment.getDatabase());

            }
        });

        super._defineEndpoint("function-catalogs/<functionCatalogId>/restore-from-trash", HttpMethod.POST, new TidyDuckRequestHandler(sessionManager, authenticator) {

            @Override
            protected Json handleRequest(final Account currentAccount, final Request request, final TidyDuckEnvironment environment, final Map<String, String> parameters) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_MODIFY);

                final Long functionCatalogId = Util.parseLong(parameters.get("functionCatalogId"));
                if (functionCatalogId < 1) {
                    throw new IllegalArgumentException("Invalid function catalog ID.");
                }
                return _restoreFunctionCatalogFromTrash(functionCatalogId, currentAccount, environment.getDatabase());
            }
        });

        super._defineEndpoint("function-catalogs/<functionCatalogId>", HttpMethod.DELETE, new TidyDuckRequestHandler(sessionManager, authenticator) {

            @Override
            protected Json handleRequest(final Account currentAccount, final Request request, final TidyDuckEnvironment environment, final Map<String, String> parameters) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_MODIFY);

                final Long functionCatalogId = Util.parseLong(parameters.get("functionCatalogId"));
                if (functionCatalogId < 1) {
                    throw new IllegalArgumentException("Invalid function catalog ID.");
                }
                return _deleteFunctionCatalog(functionCatalogId, currentAccount, environment.getDatabase());
            }
        });

        super._defineEndpoint("function-catalogs/<functionCatalogId>/submit-for-review", HttpMethod.POST, new TidyDuckRequestHandler(sessionManager, authenticator) {

            @Override
            protected Json handleRequest(final Account currentAccount, final Request request, final TidyDuckEnvironment environment, final Map<String, String> parameters) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_MODIFY);

                final Long functionCatalogId = Util.parseLong(parameters.get("functionCatalogId"));
                if (functionCatalogId < 1) {
                    throw new IllegalArgumentException("Invalid function catalog ID.");
                }
                return _submitFunctionCatalogForReview(functionCatalogId, currentAccount, environment.getDatabase());
            }
        });


        super._defineEndpoint("function-catalogs/<functionCatalogId>/release-item-list", HttpMethod.GET, new TidyDuckRequestHandler(sessionManager, authenticator) {

            @Override
            protected Json handleRequest(final Account currentAccount, final Request request, final TidyDuckEnvironment environment, final Map<String, String> parameters) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_RELEASE);

                final Long functionCatalogId = Util.parseLong(parameters.get("functionCatalogId"));
                if (functionCatalogId < 1) {
                    throw new IllegalArgumentException("Invalid function catalog ID.");
                }
                return _getReleaseItemList(functionCatalogId, environment.getDatabase());
            }
        });

        super._defineEndpoint("function-catalogs/<functionCatalogId>/release", HttpMethod.POST, new TidyDuckRequestHandler(sessionManager, authenticator) {

            @Override
            protected Json handleRequest(final Account currentAccount, final Request request, final TidyDuckEnvironment environment, final Map<String, String> parameters) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_RELEASE);

                final Long functionCatalogId = Util.parseLong(parameters.get("functionCatalogId"));
                if (functionCatalogId < 1) {
                    throw new IllegalArgumentException("Invalid function catalog ID.");
                }
                return _releaseFunctionCatalog(request, functionCatalogId, environment.getDatabase());

            }
        });

        super._defineEndpoint("function-catalogs/duplicate-check", HttpMethod.POST, new TidyDuckRequestHandler(sessionManager, authenticator) {

            @Override
            protected Json handleRequest(final Account currentAccount, final Request request, final TidyDuckEnvironment environment, final Map<String, String> parameters) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_MODIFY);

                return _checkForDuplicateFunctionCatalog(request, currentAccount, environment.getDatabase());
            }
        });

        super._defineEndpoint("function-catalogs/trashed", HttpMethod.GET, new TidyDuckRequestHandler(sessionManager, authenticator) {

            @Override
            protected Json handleRequest(final Account currentAccount, final Request request, final TidyDuckEnvironment environment, final Map<String, String> parameters) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_MODIFY);

                return _listFunctionCatalogs(currentAccount, environment.getDatabase(), true);
            }
        });
    }

    private Json _getFunctionCatalog(final Long functionCatalogId, final Account currentAccount, final Database<Connection> database) throws Exception {
        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            final FunctionCatalogInflater functionCatalogInflater = new FunctionCatalogInflater(databaseConnection);
            final FunctionCatalog functionCatalog = functionCatalogInflater.inflateFunctionCatalog(functionCatalogId);

            final Json response = new Json(false);

            response.put("functionCatalog", _toJson(functionCatalog));

            JsonRequestHandler.setJsonSuccessFields(response);
            return response;
        }
        catch (final DatabaseException exception) {
            throw new Exception("Unable to get function catalog.", exception);
        }
    }

    protected Json _listFunctionCatalogs(final Account currentAccount, final Database<Connection> database, final boolean onlyListDeleted) throws Exception {
        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            final Json response = new Json(false);

            final FunctionCatalogInflater functionCatalogInflater = new FunctionCatalogInflater(databaseConnection);
            final Map<Long, List<FunctionCatalog>> functionCatalogs;
            if (onlyListDeleted) {
                functionCatalogs = functionCatalogInflater.inflateTrashedFunctionCatalogsGroupedByBaseVersionId();
            }
            else {
                functionCatalogs = functionCatalogInflater.inflateFunctionCatalogsGroupedByBaseVersionId();
            }

            final Json catalogsJson = new Json();
            for (final Long baseVersionId : functionCatalogs.keySet()) {
                final Json versionSeriesJson = new Json();
                versionSeriesJson.put("baseVersionId", baseVersionId);

                final Json versionsJson = new Json();
                for (final FunctionCatalog functionCatalog : functionCatalogs.get(baseVersionId)) {
                    if (!functionCatalog.isPermanentlyDeleted()) {
                        if (canAccountViewFunctionCatalog(functionCatalog, currentAccount.getId()) == null) {
                            // can view this function catalog, add it to the results
                            final Json catalogJson = _toJson(functionCatalog);
                            versionsJson.add(catalogJson);
                        }
                    }
                }

                // Only add versionSeriesJson if its function catalogs have not all been filtered.
                if (versionsJson.length() > 0) {
                    versionSeriesJson.put("versions", versionsJson);
                    catalogsJson.add(versionSeriesJson);
                }
            }
            response.put("functionCatalogs", catalogsJson);

            JsonRequestHandler.setJsonSuccessFields(response);
            return response;
        }
        catch (final DatabaseException exception) {
            throw new Exception("Unable to list function catalogs.", exception);
        }
    }

    private Json _listFunctionCatalogsMatchingSearchString(final String searchString, final boolean includeDeleted, final Account currentAccount, final Database<Connection> database) throws Exception {
        final String decodedSearchString;
        try {
            decodedSearchString = URLDecoder.decode(searchString, "UTF-8");
        }
        catch (final UnsupportedEncodingException exception) {
            throw new Exception("Unable to list function catalogs from search", exception);
        }

        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            final Json response = new Json(false);

            final FunctionCatalogInflater functionCatalogInflater = new FunctionCatalogInflater(databaseConnection);
            final Map<Long, List<FunctionCatalog>> functionCatalogs = functionCatalogInflater.inflateFunctionCatalogsMatchingSearchString(decodedSearchString, includeDeleted, currentAccount.getId());

            final Json functionCatalogsJson = new Json(true);
            for (final Long baseVersionId : functionCatalogs.keySet()) {
                final Json versionSeriesJson = new Json();
                versionSeriesJson.put("baseVersionId", baseVersionId);

                final Json versionsJson = new Json();
                for (final FunctionCatalog functionCatalog : functionCatalogs.get(baseVersionId)) {
                    final Json functionCatalogJson = _toJson(functionCatalog);
                    versionsJson.add(functionCatalogJson);
                }
                versionSeriesJson.put("versions", versionsJson);
                functionCatalogsJson.add(versionSeriesJson);
            }
            response.put("functionCatalogs", functionCatalogsJson);

            JsonRequestHandler.setJsonSuccessFields(response);
            return response;
        }
        catch (final DatabaseException exception) {
            throw new Exception("Unable to list function catalogs from search", exception);
        }
    }

    protected Json _insertFunctionCatalog(final Request httpRequest, Account currentAccount, final Database<Connection> database) throws Exception {
        final Json request = JsonRequestHandler.getRequestDataAsJson(httpRequest);
        final Json response = new Json(false);

        final Json functionCatalogJson = request.get("functionCatalog");
        try {
            FunctionCatalog functionCatalog = _populateFunctionCatalogFromJson(functionCatalogJson, currentAccount, database);

            DatabaseManager databaseManager = new DatabaseManager(database);
            databaseManager.insertFunctionCatalog(functionCatalog);
            response.put("functionCatalogId", functionCatalog.getId());
        }
        catch (final Exception exception) {
            throw new Exception("Unable to store Function Catalog.", exception);
        }

        JsonRequestHandler.setJsonSuccessFields(response);
        return response;
    }

    protected Json _updateFunctionCatalog(final Request httpRequest, final long functionCatalogId, final Account currentAccount, final Database<Connection> database) throws Exception {
        final Json request = JsonRequestHandler.getRequestDataAsJson(httpRequest);
        final Json response = new Json(false);
        final Json functionCatalogJson = request.get("functionCatalog");

        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            final AccountId currentAccountId = currentAccount.getId();

            final String errorMessage = canAccountModifyFunctionCatalog(databaseConnection, functionCatalogId, currentAccountId);
            if (errorMessage != null) {
                Logger.error(errorMessage);
                throw new IllegalArgumentException(errorMessage);
            }

            final FunctionCatalog functionCatalog = _populateFunctionCatalogFromJson(functionCatalogJson, currentAccount, database);
            functionCatalog.setId(functionCatalogId);
            final DatabaseManager databaseManager = new DatabaseManager(database);
            databaseManager.updateFunctionCatalog(functionCatalog, currentAccountId);

            Logger.info("User " + currentAccount.getId() + " updated function catalog " + functionCatalog.getId() + ", which is currently owned by User " + functionCatalog.getCreatorAccountId());
            response.put("functionCatalogId", functionCatalog.getId());
        } catch (final Exception exception) {
            throw new Exception("Unable to update function catalog", exception);
        }

        JsonRequestHandler.setJsonSuccessFields(response);
        return response;
    }

    protected Json _forkFunctionCatalog(final long functionCatalogId, final Account currentAccount, final Database<Connection> database) throws Exception {
        final Json response = new Json(false);

        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            final AccountId currentAccountId = currentAccount.getId();

            final String errorMessage = canAccountViewFunctionCatalog(databaseConnection, functionCatalogId, currentAccountId);
            if (errorMessage != null) {
                Logger.error(errorMessage);
                throw new IllegalArgumentException(errorMessage);
            }

            final DatabaseManager databaseManager = new DatabaseManager(database);
            final long newFunctionCatalogId = databaseManager.forkFunctionCatalog(functionCatalogId, currentAccountId);

            Logger.info("User " + currentAccount.getId() + " forked function catalog " + functionCatalogId + " (new ID: " + newFunctionCatalogId + ").");
            response.put("functionCatalogId", newFunctionCatalogId);
        } catch (final Exception exception) {
            throw new Exception("Unable to fork function catalog", exception);
        }

        JsonRequestHandler.setJsonSuccessFields(response);
        return response;
    }

    protected Json _markFunctionCatalogAsDeleted(final long functionCatalogId, final Account currentAccount, final Database<Connection> database) throws Exception {
        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            final String errorMessage = canAccountViewFunctionCatalog(databaseConnection, functionCatalogId, currentAccount.getId());
            if (errorMessage != null) {
                throw new Exception(errorMessage);
            }

            final DatabaseManager databaseManager = new DatabaseManager(database);
            databaseManager.setIsDeletedForFunctionCatalog(functionCatalogId, true);

            Logger.info("User " + currentAccount.getId() + " marked Function Catalog " + functionCatalogId + " as deleted.");

            final Json response = new Json(false);
            JsonRequestHandler.setJsonSuccessFields(response);
            return response;
        }
        catch (final DatabaseException exception) {
            throw new Exception(String.format("Unable to move function catalog %d to trash.", functionCatalogId), exception);
        }
    }

    protected Json _restoreFunctionCatalogFromTrash(final long functionCatalogId, final Account currentAccount, final Database<Connection> database) throws Exception {
        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            final String errorMessage = canAccountViewFunctionCatalog(databaseConnection, functionCatalogId, currentAccount.getId());
            if (errorMessage != null) {
                throw new Exception(errorMessage);
            }

            final DatabaseManager databaseManager = new DatabaseManager(database);
            final long numberOfDeletedChildren = databaseManager.restoreFunctionCatalogFromTrash(functionCatalogId);

            Logger.info("User " + currentAccount.getId() + " restored Function Catalog " + functionCatalogId);
            if (numberOfDeletedChildren > 0) {
                Logger.info("Restored Function Catalog " + functionCatalogId + " contains " + numberOfDeletedChildren + " deleted Function Block relationships.");
            }

            final Json response = new Json(false);
            response.put("haveChildrenBeenDeleted", numberOfDeletedChildren);
            JsonRequestHandler.setJsonSuccessFields(response);
            return response;
        }
        catch (final DatabaseException exception) {
            throw new Exception(String.format("Unable to restore function catalog %d from trash.", functionCatalogId), exception);
        }
    }

    protected Json _deleteFunctionCatalog(final long functionCatalogId, final Account currentAccount, final Database<Connection> database) throws Exception {
        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            final String errorMessage = canAccountModifyFunctionCatalog(databaseConnection, functionCatalogId, currentAccount.getId());
            if (errorMessage != null) {
                throw new Exception(errorMessage);
            }

            final FunctionCatalogInflater functionCatalogInflater = new FunctionCatalogInflater(databaseConnection);
            final FunctionCatalog functionCatalog = functionCatalogInflater.inflateFunctionCatalog(functionCatalogId);
            if (!functionCatalog.isDeleted()) {
                final String error = "Function catalog must be moved to trash before deleting.";
                Logger.error(error);
                throw new IllegalArgumentException(error);
            }

            final DatabaseManager databaseManager = new DatabaseManager(database);
            databaseManager.deleteFunctionCatalog(functionCatalogId);

            final Json response = new Json(false);
            JsonRequestHandler.setJsonSuccessFields(response);
            return response;
        } catch (final DatabaseException exception) {
            throw new Exception(String.format("Unable to delete function catalog %d.", functionCatalogId), exception);
        }
    }

    protected Json _submitFunctionCatalogForReview(final Long functionCatalogId, final Account currentAccount, final Database<Connection> database) throws Exception {
        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            final String errorMessage = canAccountModifyFunctionCatalog(databaseConnection, functionCatalogId, currentAccount.getId());
            if (errorMessage != null) {
                throw new Exception(errorMessage);
            }

            final DatabaseManager databaseManager = new DatabaseManager(database);
            databaseManager.submitFunctionCatalogForReview(functionCatalogId, currentAccount.getId());

            final Json response = new Json(false);
            JsonRequestHandler.setJsonSuccessFields(response);
            return response;
        } catch (final DatabaseException exception) {
            throw new Exception("Unable to submit function catalog for review.", exception);
        }
    }

    private Json _getReleaseItemList(final long functionCatalogId, Database<Connection> database) throws Exception {
        try {
            final DatabaseManager databaseManager = new DatabaseManager(database);
            List<ReleaseItem> releaseItems = databaseManager.getReleaseItemList(functionCatalogId);

            final Json response = new Json(false);
            final Json releaseItemsJson = new Json(true);
            for (final ReleaseItem releaseItem : releaseItems) {
                // predict next version and set on release item
                // TODO: create smarter version predictor
                VersionPredictor versionPredictor = new IncrementLastIntegerVersionPredictor();
                final String predictedNextVersion = versionPredictor.predictedNextVersion(releaseItem);
                releaseItem.setPredictedNextVersion(predictedNextVersion);

                final Json releaseItemJson = _toJson(releaseItem);
                releaseItemsJson.add(releaseItemJson);
            }
            response.put("releaseItems", releaseItemsJson);

            JsonRequestHandler.setJsonSuccessFields(response);
            return response;
        } catch (final DatabaseException exception) {
            throw new Exception("Unable to get release items.", exception);
        }
    }

    private Json _releaseFunctionCatalog(final Request request, final Long functionCatalogId, final Database<Connection> database) throws Exception {
        try {
            DatabaseManager databaseManager = new DatabaseManager(database);

            if (!databaseManager.isFunctionCatalogApproved(functionCatalogId)) {
                throw new IllegalArgumentException("Function catalog " + functionCatalogId + " is not approved.");
            }

            // get release items
            final Json jsonRequest = JsonRequestHandler.getRequestDataAsJson(request);
            final Json releaseItemsJson = jsonRequest.get("releaseItems");
            List<ReleaseItem> releaseItems = new ArrayList<>();
            for (int i=0; i<releaseItemsJson.length(); i++) {
                final Json releaseItemJson = releaseItemsJson.get(i);
                final ReleaseItem releaseItem = _populateReleaseItemFromJson(releaseItemJson);
                _validateReleaseItem(releaseItem, database);
                releaseItems.add(releaseItem);
            }

            // verify that all expected release items are present
            _verifyReleaseItemList(releaseItems, functionCatalogId, database);

            // all conditions met, update all components
            databaseManager.releaseFunctionCatalog(functionCatalogId, releaseItems);

            final Json response = new Json(false);
            JsonRequestHandler.setJsonSuccessFields(response);
            return response;
        } catch (final Exception exception) {
            throw new Exception("Unable to release function catalog.", exception);
        }
    }

    private void _verifyReleaseItemList(final List<ReleaseItem> providedReleaseItems, final Long functionCatalogId, final Database<Connection> database) throws DatabaseException {
        final DatabaseManager databaseManager = new DatabaseManager(database);
        List<ReleaseItem> expectedReleaseItems = databaseManager.getReleaseItemList(functionCatalogId);

        if (providedReleaseItems.size() != expectedReleaseItems.size()) {
            throw new IllegalArgumentException("Invalid number of release items provided. " + expectedReleaseItems.size() + " expected.");
        }

        // check if all expected release items are present
        for (final ReleaseItem expectedReleaseItem : expectedReleaseItems) {
            // look for expectedReleaseItem in providedReleaseItems
            boolean matchFound = false;
            for (final ReleaseItem providedReleaseItem : providedReleaseItems) {
                if (providedReleaseItem.referencesSameObjectAs(expectedReleaseItem)) {
                    matchFound = true;
                    break;
                }
            }
            if (!matchFound) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(expectedReleaseItem.getItemType());
                stringBuilder.append(" ");
                stringBuilder.append(expectedReleaseItem.getItemId());
                stringBuilder.append(" (");
                stringBuilder.append(expectedReleaseItem.getItemName());
                stringBuilder.append(")  not provided.");
                throw new IllegalArgumentException(stringBuilder.toString());
            }
        }
        // same number of items and all have matches, provided list is valid
    }

    private void _validateReleaseItem(final ReleaseItem releaseItem, final Database<Connection> database) throws DatabaseException {
        final Long itemId = releaseItem.getItemId();
        final String itemType = releaseItem.getItemType();
        final String itemName = releaseItem.getItemName();
        final String itemVersion = releaseItem.getItemVersion();
        final String newVersion = releaseItem.getNewVersion();

        if (itemId == null || itemId < 1) {
            throw new IllegalArgumentException("Invalid release item ID: " + itemId);
        }
        if (Util.isBlank(itemType)) {
            throw new IllegalArgumentException("Invalid type (" + itemType + ") for item " + itemId);
        }
        if (Util.isBlank(newVersion)) {
            throw new IllegalArgumentException("Item ID: " + itemId + ". The new version (" + newVersion + ") is invalid for " + itemType.toLowerCase() + " \"" + itemName + "\" .");
        }

        if (newVersion.equals(releaseItem.getItemVersion())) {
            throw new IllegalArgumentException("Item ID: " + itemId + ". The new version for " + itemType.toLowerCase() + " \"" + itemName + "\" must be different from its previous version, " + itemVersion + ".");
        }

        final DatabaseManager databaseManager = new DatabaseManager(database);
        if (! databaseManager.isNewReleaseVersionUnique(itemType, itemId, newVersion)) {
            throw new IllegalArgumentException("Item ID: " + itemId + ". The new version (" + newVersion + ") for " + itemType.toLowerCase() + " \"" + itemName + "\" already exists as a released version.");
        }
    }

    private Json _checkForDuplicateFunctionCatalog(final Request httpRequest, final Account currentAccount, final Database<Connection> database) throws Exception {
        try {
            final Json request = JsonRequestHandler.getRequestDataAsJson(httpRequest);
            final String functionCatalogName = request.getString("functionCatalogName");
            final Long functionCatalogVersionSeries = request.getLong("functionCatalogVersionSeries");

            DatabaseManager databaseManager = new DatabaseManager(database);
            final FunctionCatalog matchedFunctionCatalog = databaseManager.checkForDuplicateFunctionCatalog(functionCatalogName, functionCatalogVersionSeries);

            final Json response = new Json(false);

            if (matchedFunctionCatalog == null) {
                response.put("matchFound", false);
            } else {
                response.put("matchFound", true);
                response.put("matchedFunctionCatalog", _toJson(matchedFunctionCatalog));
            }

            JsonRequestHandler.setJsonSuccessFields(response);
            return response;
        }
        catch (final Exception exception) {
            final String errorMessage = "Unable to check for duplicate Function Catalog.";
            Logger.error(errorMessage, exception);
            throw new Exception(errorMessage, exception);
        }
    }

    protected Json _toJson(final FunctionCatalog functionCatalog) {
        final Json catalogJson = new Json();

        String deletedDateString = null;
        if (functionCatalog.getDeletedDate() != null) {
            deletedDateString = DateUtil.dateToDateString(functionCatalog.getDeletedDate());
        }

        catalogJson.put("id", functionCatalog.getId());
        catalogJson.put("name", functionCatalog.getName());
        catalogJson.put("releaseVersion", functionCatalog.getRelease());
        catalogJson.put("authorId", functionCatalog.getAuthor().getId());
        catalogJson.put("authorName", functionCatalog.getAuthor().getName());
        catalogJson.put("companyId", functionCatalog.getCompany().getId());
        catalogJson.put("companyName", functionCatalog.getCompany().getName());
        catalogJson.put("isDeleted", functionCatalog.isDeleted());
        catalogJson.put("deletedDate", deletedDateString);
        catalogJson.put("isReleased", functionCatalog.isReleased());
        catalogJson.put("isApproved", functionCatalog.isApproved());
        catalogJson.put("approvalReviewId", functionCatalog.getApprovalReviewId());
        catalogJson.put("baseVersionId", functionCatalog.getBaseVersionId());
        catalogJson.put("priorVersionId", functionCatalog.getPriorVersionId());
        catalogJson.put("creatorAccountId", functionCatalog.getCreatorAccountId());
        return catalogJson;
    }

    private Json _toJson(ReleaseItem releaseItem) {
        final Json json = new Json(false);

        json.put("itemType", releaseItem.getItemType());
        json.put("itemId", releaseItem.getItemId());
        json.put("itemName", releaseItem.getItemName());
        json.put("itemVersion", releaseItem.getItemVersion());
        json.put("predictedNextVersion", releaseItem.getPredictedNextVersion());

        return json;
    }

    protected FunctionCatalog _populateFunctionCatalogFromJson(final Json functionCatalogJson, final Account currentAccount, final Database<Connection> database) throws Exception {
        final String name = functionCatalogJson.getString("name");
        final String release = functionCatalogJson.getString("releaseVersion");
        final AccountId authorId = AccountId.wrap(functionCatalogJson.getLong("authorId"));
        final Long companyId = functionCatalogJson.getLong("companyId");
        final AccountId creatorAccountId = AccountId.wrap(functionCatalogJson.getLong("creatorAccountId"));

        { // Validate Inputs
            if (Util.isBlank(name)) {
                throw new Exception("Invalid Name: " + name);
            }

            if (Util.isBlank(release)) {
                throw new Exception("Invalid Release: " + release);
            }
            if (!release.matches("[0-9]+\\.[0-9]+(\\.[0-9]+)?")) {
                throw new Exception("Release version must be in the form 'Major.Minor(.Patch)'.");
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

        final FunctionCatalog functionCatalog = new FunctionCatalog();
        functionCatalog.setName(name);
        functionCatalog.setRelease(release);
        functionCatalog.setAuthor(author);
        functionCatalog.setCompany(company);
        functionCatalog.setCreatorAccountId(creatorAccountId.longValue() > 0 ? creatorAccountId : null);

        return functionCatalog;
    }

    private ReleaseItem _populateReleaseItemFromJson(final Json releaseItemJson) {
        final String itemType = releaseItemJson.getString("itemType");
        final Long itemId = releaseItemJson.getLong("itemId");
        final String itemName = releaseItemJson.getString("itemName");
        final String itemVersion = releaseItemJson.getString("itemVersion");
        final String newVersion = releaseItemJson.getString("newVersion");

        final ReleaseItem releaseItem = new ReleaseItem();

        releaseItem.setItemType(itemType);
        releaseItem.setItemId(itemId);
        releaseItem.setItemName(itemName);
        releaseItem.setItemVersion(itemVersion);
        releaseItem.setNewVersion(newVersion);

        return releaseItem;
    }

    public static String canAccountViewFunctionCatalog(final DatabaseConnection<Connection> databaseConnection, final Long functionCatalogId, final AccountId currentAccountId) throws DatabaseException {
        final FunctionCatalogInflater functionCatalogInflater = new FunctionCatalogInflater(databaseConnection);
        final FunctionCatalog originalFunctionCatalog = functionCatalogInflater.inflateFunctionCatalog(functionCatalogId);

        return canAccountViewFunctionCatalog(originalFunctionCatalog, currentAccountId);
    }

    public static String canAccountViewFunctionCatalog(final FunctionCatalog originalFunctionCatalog, final AccountId currentAccountId) {
        final String ownerCheckResult = ownerCheck(originalFunctionCatalog, currentAccountId);
        if (ownerCheckResult != null) {
            return ownerCheckResult;
        }

        return null;
    }

    public static String canAccountModifyFunctionCatalog(final DatabaseConnection<Connection> databaseConnection, final Long functionCatalogId, final AccountId currentAccountId) throws DatabaseException {
        final FunctionCatalogInflater functionCatalogInflater = new FunctionCatalogInflater(databaseConnection);
        final FunctionCatalog originalFunctionCatalog = functionCatalogInflater.inflateFunctionCatalog(functionCatalogId);

        return canAccountModifyFunctionCatalog(originalFunctionCatalog, currentAccountId);
    }

    public static String canAccountModifyFunctionCatalog(final FunctionCatalog originalFunctionCatalog, final AccountId currentAccountId) {
        final String ownerCheckResult = ownerCheck(originalFunctionCatalog, currentAccountId);
        if (ownerCheckResult != null) {
            return ownerCheckResult;
        }

        if (originalFunctionCatalog.isReleased()) {
            return "Released function catalogs cannot be modified.";
        }
        if (originalFunctionCatalog.isApproved()) {
            return "Approved function catalogs cannot be modified.";
        }

        // good to go
        return null;
    }

    private static String ownerCheck(final FunctionCatalog functionCatalog, final AccountId currentAccountId) {
        if (functionCatalog.getCreatorAccountId() != null && !functionCatalog.isApproved()) {
            if (!functionCatalog.getCreatorAccountId().equals(currentAccountId)) {
                return "The function catalog is owned by another account and cannot be modified.";
            }
        }

        return null;
    }
}
