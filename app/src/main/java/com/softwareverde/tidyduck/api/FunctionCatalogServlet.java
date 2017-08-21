package com.softwareverde.tidyduck.api;

import com.softwareverde.database.Database;
import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.json.Json;
import com.softwareverde.tidyduck.Account;
import com.softwareverde.tidyduck.database.AccountInflater;
import com.softwareverde.tidyduck.database.DatabaseManager;
import com.softwareverde.tidyduck.database.FunctionCatalogInflater;
import com.softwareverde.tidyduck.environment.Environment;
import com.softwareverde.tidyduck.most.Author;
import com.softwareverde.tidyduck.most.Company;
import com.softwareverde.tidyduck.most.FunctionCatalog;
import com.softwareverde.tidyduck.util.Util;
import com.softwareverde.tomcat.servlet.AuthenticatedJsonServlet;
import com.softwareverde.tomcat.servlet.BaseServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

public class FunctionCatalogServlet extends AuthenticatedJsonServlet {
    private final Logger _logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected Json handleAuthenticatedRequest(final HttpServletRequest request, final HttpMethod httpMethod, final long accountId, final Environment environment) throws Exception {
        final Database<Connection> database = environment.getDatabase();

        String finalUrlSegment = BaseServlet.getFinalUrlSegment(request);
        if ("function-catalogs".equals(finalUrlSegment)) {
            if (httpMethod == HttpMethod.POST) {
                return _insertFunctionCatalog(request, accountId, database);
            }
            if (httpMethod == HttpMethod.GET) {
                return _listFunctionCatalogs(database);
            }
        } else {
            // not base function catalog, must have ID
            long functionCatalogId = Util.parseLong(finalUrlSegment);
            if (functionCatalogId < 1) {
                return super._generateErrorJson("Invalid function catalog id.");
            }
            if (httpMethod == HttpMethod.POST) {
                return _updateFunctionCatalog(request, functionCatalogId, accountId, database);
            }
            if (httpMethod == HttpMethod.DELETE) {
                return _deleteFunctionCatalog(request, functionCatalogId, database);
            }
        }
        return super._generateErrorJson("Unimplemented HTTP method in request.");
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
                    final Json catalogJson = new Json();
                    catalogJson.put("id", functionCatalog.getId());
                    catalogJson.put("name", functionCatalog.getName());
                    catalogJson.put("releaseVersion", functionCatalog.getRelease());
                    catalogJson.put("authorId", functionCatalog.getAuthor().getId());
                    catalogJson.put("authorName", functionCatalog.getAuthor().getName());
                    catalogJson.put("companyId", functionCatalog.getCompany().getId());
                    catalogJson.put("companyName", functionCatalog.getCompany().getName());
                    catalogJson.put("isReleased", functionCatalog.isReleased());
                    catalogJson.put("baseVersionId", functionCatalog.getBaseVersionId());
                    catalogJson.put("priorVersionId", functionCatalog.getPriorVersionId());
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
        final Json request = super._getRequestDataAsJson(httpRequest);
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
        final Json request = super._getRequestDataAsJson(httpRequest);
        final Json response = new Json(false);
        final Json functionCatalogJson = request.get("functionCatalog");
        final boolean shouldRelease = request.getBoolean("shouldRelease");

        try {
            FunctionCatalog functionCatalog = _populateFunctionCatalogFromJson(functionCatalogJson, accountId, database);
            functionCatalog.setId(functionCatalogId);
            DatabaseManager databaseManager = new DatabaseManager(database);

            if (shouldRelease) {
                databaseManager.releaseFunctionCatalog(functionCatalogId);
            }
            else {
                databaseManager.updateFunctionCatalog(functionCatalog);
            }

            response.put("functionCatalogId", functionCatalog.getId());
        } catch (final Exception exception) {
            String errorMessage = "Unable to update function catalog: " + exception.getMessage();
            _logger.error(errorMessage, exception);
            return super._generateErrorJson(errorMessage);
        }

        super._setJsonSuccessFields(response);
        return response;
    }

    protected Json _deleteFunctionCatalog(final HttpServletRequest request, final long functionCatalogId, final Database<Connection> database) {
        try {
            final DatabaseManager databaseManager = new DatabaseManager(database);
            databaseManager.deleteFunctionCatalog(functionCatalogId);
        } catch (final DatabaseException exception) {
            final String errorMessage = String.format("Unable to delete function catalog %d.", functionCatalogId);
            _logger.error(errorMessage, exception);
            return super._generateErrorJson(errorMessage);
        }

        final Json response = new Json(false);
        super._setJsonSuccessFields(response);
        return response;
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
}
