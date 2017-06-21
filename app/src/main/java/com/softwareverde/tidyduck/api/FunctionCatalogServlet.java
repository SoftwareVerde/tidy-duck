package com.softwareverde.tidyduck.api;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.json.Json;
import com.softwareverde.tidyduck.Account;
import com.softwareverde.tidyduck.Author;
import com.softwareverde.tidyduck.Company;
import com.softwareverde.tidyduck.FunctionCatalog;
import com.softwareverde.tidyduck.database.AccountInflater;
import com.softwareverde.tidyduck.database.AuthorInflater;
import com.softwareverde.tidyduck.database.DatabaseManager;
import com.softwareverde.tidyduck.database.FunctionCatalogInflater;
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

public class FunctionCatalogServlet extends AuthenticatedJsonServlet {
    private final Logger _logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected Json handleAuthenticatedRequest(final HttpServletRequest request, final HttpMethod httpMethod, final long accountId, final Environment environment) throws Exception {
        String finalUrlSegment = BaseServlet.getFinalUrlSegment(request);
        if ("function-catalog".equals(finalUrlSegment)) {
            if (httpMethod == HttpMethod.POST) {
                return _insertFunctionCatalog(request, accountId, environment);
            }
            if (httpMethod == HttpMethod.GET) {
                long versionId = Util.parseLong(Util.coalesce(request.getParameter("version_id")));
                if (versionId < 1) {
                    return super._generateErrorJson("Invalid version id.");
                }

                return _listFunctionCatalogs(versionId, environment);
            }
        } else {
            // not base function catalog, must have ID
            long functionCatalogId = Util.parseLong(finalUrlSegment);
            if (functionCatalogId < 1) {
                return super._generateErrorJson("Invalid function catalog id.");
            }
            if (httpMethod == HttpMethod.POST) {
                return _updateFunctionCatalog(request, functionCatalogId, accountId, environment);
            }
            if (httpMethod == HttpMethod.DELETE) {
                return _deleteFunctionCatalogFromVersion(request, functionCatalogId, environment);
            }
        }
        return super._generateErrorJson("Unimplemented HTTP method in request.");
    }

    protected Json _listFunctionCatalogs(final long versionId, final Environment environment) {
        try (final DatabaseConnection<Connection> databaseConnection = environment.getNewDatabaseConnection()) {
            final Json response = new Json(false);

            final FunctionCatalogInflater functionCatalogInflater = new FunctionCatalogInflater(databaseConnection);
            final List<FunctionCatalog> functionCatalogs = functionCatalogInflater.inflateFunctionCatalogsFromVersionId(versionId);

            final Json catalogsJson = new Json();
            for (final FunctionCatalog functionCatalog : functionCatalogs) {
                final Json catalogJson = new Json();
                catalogJson.put("id", functionCatalog.getId());
                catalogJson.put("name", functionCatalog.getName());
                catalogJson.put("releaseVersion", functionCatalog.getRelease());
                catalogJson.put("authorId", functionCatalog.getAuthor().getId());
                catalogJson.put("authorName", functionCatalog.getAuthor().getName());
                catalogJson.put("companyId", functionCatalog.getCompany().getId());
                catalogJson.put("companyName", functionCatalog.getCompany().getName());
                catalogsJson.add(catalogJson);
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

    protected Json _insertFunctionCatalog(final HttpServletRequest httpRequest, long accountId, final Environment environment) throws IOException {
        final Json request = super._getRequestDataAsJson(httpRequest);
        final Json response = new Json(false);

        final Long versionId = Util.parseLong(request.getString("versionId"));

        { // Validate Inputs
            if (versionId < 1) {
                _logger.error("Unable to parse Version ID: " + versionId);
                return super._generateErrorJson("Invalid Version ID: " + versionId);
            }
        }

        final Json functionCatalogJson = request.get("functionCatalog");
        try {
            FunctionCatalog functionCatalog = _populateFunctionCatalogFromJson(functionCatalogJson, accountId, environment);

            DatabaseManager databaseManager = new DatabaseManager(environment);
            databaseManager.insertFunctionCatalog(versionId, functionCatalog);
            response.put("functionCatalogId", functionCatalog.getId());
        }
        catch (final Exception exception) {
            _logger.error("Unable to store Function Catalog.", exception);
            return super._generateErrorJson("Unable to store Function Catalog: " + exception.getMessage());
        }

        super._setJsonSuccessFields(response);
        return response;
    }

    protected Json _updateFunctionCatalog(HttpServletRequest httpRequest, long functionCatalogId, long accountId, Environment environment) throws IOException {
        final Json request = super._getRequestDataAsJson(httpRequest);

        final Long versionId = Util.parseLong(request.getString("versionId"));

        final Json functionCatalogJson = request.get("functionCatalog");

        { // Validate Inputs
            if (versionId < 1) {
                _logger.error("Unable to parse Version ID: " + versionId);
                return super._generateErrorJson("Invalid Version ID: " + versionId);
            }
        }

        try {
            FunctionCatalog functionCatalog = _populateFunctionCatalogFromJson(functionCatalogJson, accountId, environment);
            functionCatalog.setId(functionCatalogId);

            DatabaseManager databaseManager = new DatabaseManager(environment);
            databaseManager.updateFunctionCatalog(versionId, functionCatalog);
        } catch (final Exception exception) {
            String errorMessage = "Unable to update function catalog: " + exception.getMessage();
            _logger.error(errorMessage, exception);
            return super._generateErrorJson(errorMessage);
        }

        Json response = new Json(false);
        super._setJsonSuccessFields(response);
        return response;
    }

    protected Json _deleteFunctionCatalogFromVersion(HttpServletRequest request, long functionCatalogId, Environment environment) {
        final String versionIdString = request.getParameter("versionId");
        final Long versionId = Util.parseLong(versionIdString);

        { // Validate Inputs
            if (versionId == null || versionId < 1) {
                return super._generateErrorJson(String.format("Invalid version id: %s", versionIdString));
            }
        }

        try {
            final DatabaseManager databaseManager = new DatabaseManager(environment);
            databaseManager.deleteFunctionCatalog(versionId, functionCatalogId);
        } catch (final DatabaseException exception) {
            final String errorMessage = String.format("Unable to delete function catalog %d from version %d.", functionCatalogId, versionId);
            _logger.error(errorMessage, exception);
            return super._generateErrorJson(errorMessage);
        }

        final Json response = new Json(false);
        super._setJsonSuccessFields(response);
        return response;
    }

    protected FunctionCatalog _populateFunctionCatalogFromJson(final Json functionCatalogJson, final long accountId, final Environment environment) throws Exception {
        final String name = functionCatalogJson.getString("name");
        final String release = functionCatalogJson.getString("releaseVersion");
        final Integer authorId = functionCatalogJson.getInteger("authorId");
        final Integer companyId = functionCatalogJson.getInteger("companyId");

        { // Validate Inputs
            if (Util.isBlank(name)) {
                throw new Exception("Invalid Name: " + name);
            }

            if (Util.isBlank(release)) {
                throw new Exception("Invalid Release: " + release);
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

        final FunctionCatalog functionCatalog = new FunctionCatalog();
        functionCatalog.setName(name);
        functionCatalog.setRelease(release);
        functionCatalog.setAuthor(author);
        functionCatalog.setCompany(company);

        return functionCatalog;
    }
}
