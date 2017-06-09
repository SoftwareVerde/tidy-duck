package com.softwareverde.tidyduck.api;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.json.Json;
import com.softwareverde.tidyduck.Author;
import com.softwareverde.tidyduck.Company;
import com.softwareverde.tidyduck.DateUtil;
import com.softwareverde.tidyduck.FunctionCatalog;
import com.softwareverde.tidyduck.database.DatabaseManager;
import com.softwareverde.tidyduck.database.MostCatalogInflater;
import com.softwareverde.tidyduck.environment.Environment;
import com.softwareverde.tomcat.servlet.JsonServlet;
import com.softwareverde.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class FunctionCatalogServlet extends JsonServlet {
    private final Logger _logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected Json handleRequest(final HttpServletRequest request, final HttpMethod httpMethod, final Environment environment) throws Exception {
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
        return super.generateErrorJson("Unimplemented HTTP method in request.");
    }

    private Json listFunctionCatalogs(final long versionId, final Environment environment) {
        try {
            final Json response = new Json(false);

            final DatabaseConnection databaseConnection = environment.getNewDatabaseConnection();
            final MostCatalogInflater mostCatalogInflater = new MostCatalogInflater(databaseConnection);
            final List<FunctionCatalog> functionCatalogs = mostCatalogInflater.inflateFunctionCatalogsFromVersionId(versionId);

            final Json catalogsJson = new Json();
            for (final FunctionCatalog functionCatalog : functionCatalogs) {
                final Json catalogJson = new Json();
                catalogJson.put("release", functionCatalog.getRelease());
                catalogJson.put("releaseDate", DateUtil.timestampToDatetimeString(functionCatalog.getReleaseDate().getTime()));
                catalogJson.put("authorId", functionCatalog.getAuthor().getId());
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

        final long versionId = Long.parseLong(request.getString("versionId"));

        final String release = request.getString("release");
        final String releaseDateString = request.getString("releaseDate");
        final Integer authorId = request.getInteger("authorId");
        final Integer companyId = request.getInteger("companyId");
        final Date releaseDate = DateUtil.dateFromDateString(releaseDateString);

        { // Validate Inputs
            if (releaseDate == null) {
                _logger.error(String.format("Unable to parse Release-Date: %s", releaseDateString));
                return super.generateErrorJson("Invalid Release Date: " + releaseDateString);
            }

            if (authorId < 1) {
                _logger.error(String.format("Invalid Author ID: %s", authorId));
                return super.generateErrorJson("Invalid Author ID: " + authorId);
            }

            if (companyId < 1) {
                _logger.error(String.format("Invalid Company ID: %s", companyId));
                return super.generateErrorJson("Invalid Company ID: " + companyId);
            }
        }

        final Company company = new Company();
        company.setId(companyId);

        final Author author = new Author();
        author.setId(authorId);

        final FunctionCatalog functionCatalog = new FunctionCatalog();
        functionCatalog.setRelease(release);
        functionCatalog.setReleaseDate(releaseDate);
        functionCatalog.setAuthor(author);
        functionCatalog.setCompany(company);

        try {
            DatabaseManager databaseManager = new DatabaseManager(environment);
            databaseManager.insertFunctionCatalog(functionCatalog, versionId);
        }
        catch (final DatabaseException exception) {
            _logger.error("Unable to store Function Catalog.", exception);
            return super.generateErrorJson("Unable to store Function Catalog: " + exception.getMessage());
        }

        super.setJsonSuccessFields(response);
        return response;
    }
}
