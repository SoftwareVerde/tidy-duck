package com.softwareverde.tidyduck.api;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.json.Json;
import com.softwareverde.tidyduck.Account;
import com.softwareverde.tidyduck.Company;
import com.softwareverde.tidyduck.DateUtil;
import com.softwareverde.tidyduck.FunctionCatalog;
import com.softwareverde.tidyduck.database.DatabaseManager;
import com.softwareverde.tidyduck.database.MostCatalogInflater;
import com.softwareverde.tidyduck.environment.Environment;
import com.softwareverde.tidyduck.util.Util;
import com.softwareverde.tomcat.servlet.JsonServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.Connection;
import java.util.Date;
import java.util.List;

public class FunctionCatalogServlet extends JsonServlet {
    private final Logger _logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected Json handleRequest(final HttpServletRequest request, final HttpMethod httpMethod, final Environment environment) throws Exception {
        String finalUrlSegment = super.getFinalUrlSegment(request);
        if ("function-catalog".equals(finalUrlSegment)) {
            if (httpMethod == HttpMethod.POST) {
                return storeFunctionCatalog(request, environment);
            }
            if (httpMethod == HttpMethod.GET) {
                long versionId = Util.parseLong(Util.coalesce(request.getParameter("version_id")));
                if (versionId < 1) {
                    return super.generateErrorJson("Invalid version id.");
                }

                return listFunctionCatalogs(versionId, environment);
            }
        } else {
            // not base function catalog, must have ID
            long functionCatalogId = Util.parseLong(finalUrlSegment);
            if (functionCatalogId < 1) {
                return super.generateErrorJson("Invalid function catalog id.");
            }
            if (httpMethod == HttpMethod.POST) {
                return updateFunctionCatalog(request, environment);
            }
            if (httpMethod == HttpMethod.DELETE) {
                return deleteFunctionCatalogFromVersion(request, environment);
            }
        }
        return super.generateErrorJson("Unimplemented HTTP method in request.");
    }

    private Json listFunctionCatalogs(final long versionId, final Environment environment) {
        try {
            final Json response = new Json(false);

            final DatabaseConnection<Connection> databaseConnection = environment.getNewDatabaseConnection();
            final MostCatalogInflater mostCatalogInflater = new MostCatalogInflater(databaseConnection);
            final List<FunctionCatalog> functionCatalogs = mostCatalogInflater.inflateFunctionCatalogsFromVersionId(versionId);

            final Json catalogsJson = new Json();
            for (final FunctionCatalog functionCatalog : functionCatalogs) {
                final Json catalogJson = new Json();
                catalogJson.put("id", functionCatalog.getId());
                catalogJson.put("name", functionCatalog.getName());
                catalogJson.put("releaseVersion", functionCatalog.getRelease());
                catalogJson.put("releaseDate", DateUtil.dateToDateString(functionCatalog.getReleaseDate()));
                catalogJson.put("authorId", functionCatalog.getAccount().getId());
                catalogJson.put("companyId", functionCatalog.getCompany().getId());
                catalogsJson.add(catalogJson);
            }
            response.put("functionCatalogs", catalogsJson);

            super.setJsonSuccessFields(response);
            return response;
        }
        catch (final DatabaseException exception) {
            _logger.error("Unable to list function catalogs.", exception);
            return super.generateErrorJson("Unable to list function catalogs.");
        }
    }

    private Json storeFunctionCatalog(final HttpServletRequest httpRequest, final Environment environment) throws IOException {
        final Json request = super.getRequestDataAsJson(httpRequest);
        final Json response = new Json(false);

        final Long versionId = Util.parseLong(request.getString("versionId"));

        final Json functionCatalogJson = request.get("functionCatalog");
        final String name = functionCatalogJson.getString("name");
        final String release = functionCatalogJson.getString("releaseVersion");
        final String releaseDateString = functionCatalogJson.getString("releaseDate");
        final Integer authorId = functionCatalogJson.getInteger("authorId");
        final Integer companyId = functionCatalogJson.getInteger("companyId");
        final Date releaseDate = DateUtil.dateFromDateString(releaseDateString);

        { // Validate Inputs
            if (versionId < 1) {
                _logger.error("Unable to parse Version ID: " + versionId);
                return super.generateErrorJson("Invalid Version ID: " + versionId);
            }

            if (Util.isBlank(name)) {
                _logger.error("Unable to parse Name: " + name);
                return super.generateErrorJson("Invalid Name: " + name);
            }

            if (Util.isBlank(release)) {
                _logger.error("Unable to parse Release: " + release);
                return super.generateErrorJson("Invalid Release: " + release);
            }

            if (releaseDate == null) {
                _logger.error("Unable to parse Release Date: " + releaseDateString);
                return super.generateErrorJson("Invalid Release Date: " + releaseDateString);
            }

            if (authorId < 1) {
                _logger.error("Invalid Account ID: " + authorId);
                return super.generateErrorJson("Invalid Account ID: " + authorId);
            }

            if (companyId < 1) {
                _logger.error("Invalid Company ID: " + companyId);
                return super.generateErrorJson("Invalid Company ID: " + companyId);
            }
        }

        final Company company = new Company();
        company.setId(companyId);

        final Account account = new Account();
        account.setId(authorId);

        final FunctionCatalog functionCatalog = new FunctionCatalog();
        functionCatalog.setName(name);
        functionCatalog.setRelease(release);
        functionCatalog.setReleaseDate(releaseDate);
        functionCatalog.setAccount(account);
        functionCatalog.setCompany(company);

        try {
            DatabaseManager databaseManager = new DatabaseManager(environment);
            databaseManager.insertFunctionCatalog(versionId, functionCatalog);
            response.put("functionCatalogId", functionCatalog.getId());
        }
        catch (final DatabaseException exception) {
            _logger.error("Unable to store Function Catalog.", exception);
            return super.generateErrorJson("Unable to store Function Catalog: " + exception.getMessage());
        }

        super.setJsonSuccessFields(response);
        return response;
    }

    private Json updateFunctionCatalog(HttpServletRequest request, Environment environment) {

        return super.generateErrorJson("Unimplemented.");
    }

    private Json deleteFunctionCatalogFromVersion(HttpServletRequest request, Environment environment) {
        Long versionId = Util.parseLong(request.getParameter("versionId"));
        Long functionCatalogId = Util.parseLong(request.getParameter("functionCatalogId"));

        // TODO: implement

        return super.generateErrorJson("Unimplemented.");
    }
}
