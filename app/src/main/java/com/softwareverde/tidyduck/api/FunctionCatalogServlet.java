package com.softwareverde.tidyduck.api;

import com.softwareverde.database.Database;
import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.json.Json;
import com.softwareverde.tidyduck.*;
import com.softwareverde.tidyduck.database.DatabaseManager;
import com.softwareverde.tidyduck.database.FunctionCatalogInflater;
import com.softwareverde.tidyduck.environment.Environment;
import com.softwareverde.tidyduck.most.Author;
import com.softwareverde.tidyduck.most.Company;
import com.softwareverde.tidyduck.most.FunctionCatalog;
import com.softwareverde.tidyduck.util.Util;
import com.softwareverde.tomcat.servlet.AuthenticatedJsonServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FunctionCatalogServlet extends AuthenticatedJsonServlet {
    private final Logger _logger = LoggerFactory.getLogger(this.getClass());

    public FunctionCatalogServlet() {
        super.defineEndpoint("function-catalogs", HttpMethod.GET, new AuthenticatedJsonRoute() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_VIEW);

                return _listFunctionCatalogs(environment.getDatabase());
            }
        });

        super.defineEndpoint("function-catalogs", HttpMethod.POST, new AuthenticatedJsonRoute() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_CREATE);

                return _insertFunctionCatalog(request, currentAccount, environment.getDatabase());
            }
        });

        super.defineEndpoint("function-catalogs/<functionCatalogId>", HttpMethod.GET, new AuthenticatedJsonRoute() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_VIEW);

                final Long functionCatalogId = Util.parseLong(parameters.get("functionCatalogId"));
                if (functionCatalogId < 1) {
                    return _generateErrorJson("Invalid function catalog ID.");
                }
                return _getFunctionCatalog(functionCatalogId, environment.getDatabase());
            }
        });

        super.defineEndpoint("function-catalogs/<functionCatalogId>", HttpMethod.POST, new AuthenticatedJsonRoute() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_MODIFY);

                final Long functionCatalogId = Util.parseLong(parameters.get("functionCatalogId"));
                if (functionCatalogId < 1) {
                    return _generateErrorJson("Invalid function catalog ID.");
                }
                return _updateFunctionCatalog(request, functionCatalogId, currentAccount, environment.getDatabase());
            }
        });

        super.defineEndpoint("function-catalogs/<functionCatalogId>", HttpMethod.DELETE, new AuthenticatedJsonRoute() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_MODIFY);

                final Long functionCatalogId = Util.parseLong(parameters.get("functionCatalogId"));
                if (functionCatalogId < 1) {
                    return _generateErrorJson("Invalid function catalog ID.");
                }
                return _deleteFunctionCatalog(functionCatalogId, environment.getDatabase());
            }
        });

        super.defineEndpoint("function-catalogs/<functionCatalogId>/submit-for-review", HttpMethod.POST, new AuthenticatedJsonRoute() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_MODIFY);

                final Long functionCatalogId = Util.parseLong(parameters.get("functionCatalogId"));
                if (functionCatalogId < 1) {
                    return _generateErrorJson("Invalid function catalog ID.");
                }
                return _submitFunctionCatalogForReview(functionCatalogId, currentAccount, environment.getDatabase());
            }
        });

        super.defineEndpoint("function-catalogs/<functionCatalogId>/release-item-list", HttpMethod.GET, new AuthenticatedJsonRoute() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_RELEASE);

                final Long functionCatalogId = Util.parseLong(parameters.get("functionCatalogId"));
                if (functionCatalogId < 1) {
                    return _generateErrorJson("Invalid function catalog ID.");
                }
                return _getReleaseItemList(functionCatalogId, environment.getDatabase());
            }
        });

        super.defineEndpoint("function-catalogs/<functionCatalogId>/release", HttpMethod.POST, new AuthenticatedJsonRoute() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_RELEASE);

                final Long functionCatalogId = Util.parseLong(parameters.get("functionCatalogId"));
                if (functionCatalogId < 1) {
                    return _generateErrorJson("Invalid function catalog ID.");
                }
                return _releaseFunctionCatalog(request, functionCatalogId, environment.getDatabase());
            }
        });
        
        super.defineEndpoint("function-catalog-duplicate-check", HttpMethod.POST, new AuthenticatedJsonRoute() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                return _checkForDuplicateFunctionCatalog(request, currentAccount, environment.getDatabase());
            }
        });
    }

    private Json _getFunctionCatalog(final Long functionCatalogId, final Database<Connection> database) {
        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            final FunctionCatalogInflater functionCatalogInflater = new FunctionCatalogInflater(databaseConnection);
            final FunctionCatalog functionCatalog = functionCatalogInflater.inflateFunctionCatalog(functionCatalogId);

            final Json response = new Json(false);

            response.put("functionCatalog", _toJson(functionCatalog));

            super._setJsonSuccessFields(response);
            return response;
        }
        catch (final DatabaseException exception) {
            _logger.error("Unable to get function catalog.", exception);
            return super._generateErrorJson("Unable to get function catalog.");
        }
    }

    protected Json _listFunctionCatalogs(final Database<Connection> database) {
        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            final Json response = new Json(false);

            final FunctionCatalogInflater functionCatalogInflater = new FunctionCatalogInflater(databaseConnection);
            final Map<Long, List<FunctionCatalog>> functionCatalogs = functionCatalogInflater.inflateFunctionCatalogsGroupedByBaseVersionId();

            final Json catalogsJson = new Json();
            for (final Long baseVersionId : functionCatalogs.keySet()) {
                final Json versionSeriesJson = new Json();
                versionSeriesJson.put("baseVersionId", baseVersionId);

                final Json versionsJson = new Json();
                for (final FunctionCatalog functionCatalog : functionCatalogs.get(baseVersionId)) {
                    final Json catalogJson = _toJson(functionCatalog);
                    versionsJson.add(catalogJson);
                }
                versionSeriesJson.put("versions", versionsJson);
                catalogsJson.add(versionSeriesJson);
            }
            response.put("functionCatalogs", catalogsJson);

            super._setJsonSuccessFields(response);
            return response;
        }
        catch (final DatabaseException exception) {
            _logger.error("Unable to list function catalogs.", exception);
            return super._generateErrorJson("Unable to list function catalogs.");
        }
    }

    protected Json _insertFunctionCatalog(final HttpServletRequest httpRequest, Account currentAccount, final Database<Connection> database) throws IOException {
        final Json request = _getRequestDataAsJson(httpRequest);
        final Json response = new Json(false);

        final Json functionCatalogJson = request.get("functionCatalog");
        try {
            FunctionCatalog functionCatalog = _populateFunctionCatalogFromJson(functionCatalogJson, currentAccount, database);

            DatabaseManager databaseManager = new DatabaseManager(database);
            databaseManager.insertFunctionCatalog(functionCatalog);
            response.put("functionCatalogId", functionCatalog.getId());
        }
        catch (final Exception exception) {
            _logger.error("Unable to store Function Catalog.", exception);
            return super._generateErrorJson("Unable to store Function Catalog: " + exception.getMessage());
        }

        super._setJsonSuccessFields(response);
        return response;
    }

    protected Json _updateFunctionCatalog(final HttpServletRequest httpRequest, final long functionCatalogId, final Account currentAccount, final Database<Connection> database) throws IOException {
        final Json request = _getRequestDataAsJson(httpRequest);
        final Json response = new Json(false);
        final Json functionCatalogJson = request.get("functionCatalog");

        try {
            FunctionCatalog functionCatalog = _populateFunctionCatalogFromJson(functionCatalogJson, currentAccount, database);
            functionCatalog.setId(functionCatalogId);
            DatabaseManager databaseManager = new DatabaseManager(database);

            databaseManager.updateFunctionCatalog(functionCatalog);

            response.put("functionCatalogId", functionCatalog.getId());
        } catch (final Exception exception) {
            String errorMessage = "Unable to update function catalog: " + exception.getMessage();
            _logger.error(errorMessage, exception);
            return super._generateErrorJson(errorMessage);
        }

        super._setJsonSuccessFields(response);
        return response;
    }

    protected Json _deleteFunctionCatalog(final long functionCatalogId, final Database<Connection> database) {
        try {
            final DatabaseManager databaseManager = new DatabaseManager(database);
            databaseManager.deleteFunctionCatalog(functionCatalogId);

            final Json response = new Json(false);
            super._setJsonSuccessFields(response);
            return response;
        } catch (final DatabaseException exception) {
            final String errorMessage = String.format("Unable to delete function catalog %d.", functionCatalogId);
            _logger.error(errorMessage, exception);
            return super._generateErrorJson(errorMessage);
        }
    }

    protected Json _submitFunctionCatalogForReview(final Long functionCatalogId, final Account currentAccount, final Database<Connection> database) {
        try {
            final DatabaseManager databaseManager = new DatabaseManager(database);
            databaseManager.submitFunctionCatalogForReview(functionCatalogId, currentAccount.getId());

            final Json response = new Json(false);
            _setJsonSuccessFields(response);
            return response;
        } catch (DatabaseException e) {
            String errorMessage = "Unable to submit function catalog for review.";
            _logger.error(errorMessage, e);
            return super._generateErrorJson(errorMessage);
        }
    }

    private Json _getReleaseItemList(final long functionCatalogId, Database<Connection> database) {
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

            _setJsonSuccessFields(response);
            return response;
        } catch (DatabaseException e) {
            String errorMessage = "Unable to get release items.";
            _logger.error(errorMessage, e);
            return super._generateErrorJson(errorMessage);
        }
    }

    private Json _releaseFunctionCatalog(final HttpServletRequest request, final Long functionCatalogId, final Database<Connection> database) {
        try {
            DatabaseManager databaseManager = new DatabaseManager(database);

            if (!databaseManager.isFunctionCatalogApproved(functionCatalogId)) {
                throw new IllegalArgumentException("Function catalog " + functionCatalogId + " is not approved.");
            }

            // get release items
            final Json jsonRequest = _getRequestDataAsJson(request);
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
            super._setJsonSuccessFields(response);
            return response;
        } catch (Exception e) {
            String errorMessage = "Unable to release function catalog.";
            _logger.error(errorMessage, e);
            return super._generateErrorJson("Unable to release Function Catalog: " + e.getMessage());
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

    private Json _checkForDuplicateFunctionCatalog(final HttpServletRequest httpRequest, final Account currentAccount, final Database<Connection> database) throws IOException {
        try {
            final Json request = _getRequestDataAsJson(httpRequest);
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

            super._setJsonSuccessFields(response);
            return response;
        }
        catch (final Exception exception) {
            _logger.error("Unable to check for duplicate Function Catalog.", exception);
            return super._generateErrorJson("Unable to check for duplicate Function Catalog: " + exception.getMessage());
        }
    }

    protected Json _toJson(final FunctionCatalog functionCatalog) {
        final Json catalogJson = new Json();
        catalogJson.put("id", functionCatalog.getId());
        catalogJson.put("name", functionCatalog.getName());
        catalogJson.put("releaseVersion", functionCatalog.getRelease());
        catalogJson.put("authorId", functionCatalog.getAuthor().getId());
        catalogJson.put("authorName", functionCatalog.getAuthor().getName());
        catalogJson.put("companyId", functionCatalog.getCompany().getId());
        catalogJson.put("companyName", functionCatalog.getCompany().getName());
        catalogJson.put("isReleased", functionCatalog.isReleased());
        catalogJson.put("isApproved", functionCatalog.isApproved());
        catalogJson.put("baseVersionId", functionCatalog.getBaseVersionId());
        catalogJson.put("priorVersionId", functionCatalog.getPriorVersionId());
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
        final Long authorId = functionCatalogJson.getLong("authorId");
        final Long companyId = functionCatalogJson.getLong("companyId");

        { // Validate Inputs
            if (Util.isBlank(name)) {
                throw new Exception("Invalid Name: " + name);
            }
            if (!name.matches("[A-z0-9]+")) {
                throw new Exception("Name must contain only alpha-numeric characters.");
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

        final FunctionCatalog functionCatalog = new FunctionCatalog();
        functionCatalog.setName(name);
        functionCatalog.setRelease(release);
        functionCatalog.setAuthor(author);
        functionCatalog.setCompany(company);

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
}
