package com.softwareverde.tidyduck.api;

import com.softwareverde.database.Database;
import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.json.Json;
import com.softwareverde.tidyduck.Account;
import com.softwareverde.tidyduck.IncrementLastIntegerVersionPredictor;
import com.softwareverde.tidyduck.ReleaseItem;
import com.softwareverde.tidyduck.VersionPredictor;
import com.softwareverde.tidyduck.database.AccountInflater;
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
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Long accountId, final Environment environment) throws Exception {
                return _listFunctionCatalogs(environment.getDatabase());
            }
        });

        super.defineEndpoint("function-catalogs", HttpMethod.POST, new AuthenticatedJsonRoute() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Long accountId, final Environment environment) throws Exception {
                return _insertFunctionCatalog(request, accountId, environment.getDatabase());
            }
        });

        super.defineEndpoint("function-catalogs/<functionCatalogId>", HttpMethod.GET, new AuthenticatedJsonRoute() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Long accountId, final Environment environment) throws Exception {
                final Long functionCatalogId = Util.parseLong(parameters.get("functionCatalogId"));
                if (functionCatalogId < 1) {
                    return _generateErrorJson("Invalid function catalog ID.");
                }
                return _getFunctionCatalog(functionCatalogId, environment.getDatabase());
            }
        });

        super.defineEndpoint("function-catalogs/<functionCatalogId>", HttpMethod.POST, new AuthenticatedJsonRoute() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Long accountId, final Environment environment) throws Exception {
                final Long functionCatalogId = Util.parseLong(parameters.get("functionCatalogId"));
                if (functionCatalogId < 1) {
                    return _generateErrorJson("Invalid function catalog ID.");
                }
                return _updateFunctionCatalog(request, functionCatalogId, accountId, environment.getDatabase());
            }
        });

        super.defineEndpoint("function-catalogs/<functionCatalogId>", HttpMethod.DELETE, new AuthenticatedJsonRoute() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Long accountId, final Environment environment) throws Exception {
                final Long functionCatalogId = Util.parseLong(parameters.get("functionCatalogId"));
                if (functionCatalogId < 1) {
                    return _generateErrorJson("Invalid function catalog ID.");
                }
                return _deleteFunctionCatalog(functionCatalogId, environment.getDatabase());
            }
        });

        super.defineEndpoint("function-catalogs/<functionCatalogId>/submit-for-review", HttpMethod.POST, new AuthenticatedJsonRoute() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Long accountId, final Environment environment) throws Exception {
                final Long functionCatalogId = Util.parseLong(parameters.get("functionCatalogId"));
                if (functionCatalogId < 1) {
                    return _generateErrorJson("Invalid function catalog ID.");
                }
                return _submitFunctionCatalogForReview(functionCatalogId, accountId, environment.getDatabase());
            }
        });

        super.defineEndpoint("function-catalogs/<functionCatalogId>/release-item-list", HttpMethod.GET, new AuthenticatedJsonRoute() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Long accountId, final Environment environment) throws Exception {
                final Long functionCatalogId = Util.parseLong(parameters.get("functionCatalogId"));
                if (functionCatalogId < 1) {
                    return _generateErrorJson("Invalid function catalog ID.");
                }
                return _getReleaseItemList(functionCatalogId, environment.getDatabase());
            }
        });

        super.defineEndpoint("function-catalogs/<functionCatalogId>/release", HttpMethod.POST, new AuthenticatedJsonRoute() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Long accountId, final Environment environment) throws Exception {
                final Long functionCatalogId = Util.parseLong(parameters.get("functionCatalogId"));
                if (functionCatalogId < 1) {
                    return _generateErrorJson("Invalid function catalog ID.");
                }
                return _releaseFunctionCatalog(request, functionCatalogId, environment.getDatabase());
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

    protected Json _insertFunctionCatalog(final HttpServletRequest httpRequest, long accountId, final Database<Connection> database) throws IOException {
        final Json request = _getRequestDataAsJson(httpRequest);
        final Json response = new Json(false);

        final Json functionCatalogJson = request.get("functionCatalog");
        try {
            FunctionCatalog functionCatalog = _populateFunctionCatalogFromJson(functionCatalogJson, accountId, database);

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

    protected Json _updateFunctionCatalog(final HttpServletRequest httpRequest, final long functionCatalogId, final long accountId, final Database<Connection> database) throws IOException {
        final Json request = _getRequestDataAsJson(httpRequest);
        final Json response = new Json(false);
        final Json functionCatalogJson = request.get("functionCatalog");

        try {
            FunctionCatalog functionCatalog = _populateFunctionCatalogFromJson(functionCatalogJson, accountId, database);
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

    protected Json _submitFunctionCatalogForReview(final Long functionCatalogId, final Long accountId, final Database<Connection> database) {
        try {
            final DatabaseManager databaseManager = new DatabaseManager(database);
            databaseManager.submitFunctionCatalogForReview(functionCatalogId, accountId);

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
                _validateReleaseItem(releaseItem);
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
            return super._generateErrorJson(errorMessage);
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

    private void _validateReleaseItem(final ReleaseItem releaseItem) {
        final Long itemId = releaseItem.getItemId();
        final String itemType = releaseItem.getItemType();
        final String itemVersion = releaseItem.getItemVersion();
        final String newVersion = releaseItem.getNewVersion();

        if (itemId == null || itemId < 1) {
            throw new IllegalArgumentException("Invalid release item ID: " + itemId);
        }
        if (Util.isBlank(itemType)) {
            throw new IllegalArgumentException("Invalid type (" + itemType + ") for item " + itemId);
        }
        if (Util.isBlank(newVersion)) {
            throw new IllegalArgumentException("New version (" + newVersion + ") is invalid for item " + itemId);
        }
        if (newVersion.equals(releaseItem.getItemVersion())) {
            throw new IllegalArgumentException("New version (" + newVersion + ") must be different from old version (" + itemVersion + "), item " + itemId);
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

    protected FunctionCatalog _populateFunctionCatalogFromJson(final Json functionCatalogJson, final long accountId, final Database<Connection> database) throws Exception {
        final String name = functionCatalogJson.getString("name");
        final String release = functionCatalogJson.getString("releaseVersion");
        final Long authorId = functionCatalogJson.getLong("authorId");
        final Long companyId = functionCatalogJson.getLong("companyId");

        { // Validate Inputs
            if (Util.isBlank(name)) {
                throw new Exception("Invalid Name: " + name);
            }

            if (Util.isBlank(release)) {
                throw new Exception("Invalid Release: " + release);
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
            try (DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
                AccountInflater accountInflater = new AccountInflater(databaseConnection);

                Account account = accountInflater.inflateAccount(accountId);

                company = account.getCompany();
                author = account.toAuthor();
            }
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
