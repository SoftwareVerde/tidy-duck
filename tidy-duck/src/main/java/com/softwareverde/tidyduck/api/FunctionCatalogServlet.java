package com.softwareverde.tidyduck.api;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.Query;
import com.softwareverde.database.Row;
import com.softwareverde.json.Json;
import com.softwareverde.tidyduck.Author;
import com.softwareverde.tidyduck.Company;
import com.softwareverde.tidyduck.DateUtil;
import com.softwareverde.tidyduck.FunctionCatalog;
import com.softwareverde.tidyduck.environment.Environment;
import com.softwareverde.tomcat.servlet.JsonServlet;
import com.softwareverde.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.sql.Connection;
import java.util.ArrayList;
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
            long versionId = Util.parseLong(Util.coalesce(request.getParameter("versionId")));
            if (versionId < 1) {
                return super.generateErrorJson("Invalid versionId.");
            }

            return listFunctionCatalogs(versionId, environment);
        }
        return super.generateErrorJson("Unimplemented HTTP method in request.");
    }

    private Json listFunctionCatalogs(final long versionId, final Environment environment) {
        try {
            final Json response = new Json(false);

            final DatabaseConnection databaseConnection = environment.getNewDatabaseConnection();
            final List<FunctionCatalog> functionCatalogs = _loadFunctionCatalogsByVersion(versionId, databaseConnection);

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

    private List<FunctionCatalog> _loadFunctionCatalogsByVersion(final long versionId, final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
        final Query query = new Query(
            "SELECT function_catalog_id, release, release_date, author_id, company_id"
            + " FROM function_catalogs INNER JOIN versions_function_catalogs"
            + " ON function_catalogs.id = versions_function_catalogs.function_catalog_id"
            + " WHERE version_id = ?"
        );
        query.setParameter(versionId);

        final ArrayList<FunctionCatalog> functionCatalogs = new ArrayList<>();
        final List<Row> rows = databaseConnection.query(query);
        for (final Row row : rows) {
            final Author author = new Author();
            author.setId(row.getLong("author_id"));

            final Company company = new Company();
            company.setId(row.getLong("company_id"));

            final FunctionCatalog functionCatalog = new FunctionCatalog();
            functionCatalog.setRelease(row.getString("release"));
            functionCatalog.setReleaseDate(DateUtil.dateFromDateString(row.getString("releaseDate")));
            functionCatalog.setAuthor(author);
            functionCatalog.setCompany(company);

            functionCatalogs.add(functionCatalog);
        }
        return functionCatalogs;
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
            final DatabaseConnection<Connection> databaseConnection = environment.getNewDatabaseConnection();
            _storeFunctionCatalog(databaseConnection, functionCatalog);
            _associateFunctionCatalogWithVersion(databaseConnection, versionId, functionCatalog);
        }
        catch (final DatabaseException exception) {
            _logger.error("Unable to store Function Catalog.", exception);
            return super.generateErrorJson("Unable to store Function Catalog: " + exception.getMessage());
        }

        super.setJsonSuccessFields(response);
        return response;
    }

    /**
     * Stores the functionCatalog's release, releaseDate, authorId, and companyId via the databaseConnection.
     * Upon successful insert, the functionCatalog's Id is set to the database's insertId.
     */
    protected void _storeFunctionCatalog(final DatabaseConnection databaseConnection, final FunctionCatalog functionCatalog) throws DatabaseException {
        final String release = functionCatalog.getRelease();
        final String releaseDate = DateUtil.timestampToDatetimeString(functionCatalog.getReleaseDate().getTime());
        final Long authorId = functionCatalog.getAuthor().getId();
        final Long companyId = functionCatalog.getCompany().getId();

        final Query query = new Query("INSERT INTO function_catalogs (release, release_date, author_id, company_id) VALUES (?, ?, ?, ?)")
            .setParameter(release)
            .setParameter(releaseDate)
            .setParameter(authorId)
            .setParameter(companyId)
        ;

        final long functionCatalogId = databaseConnection.executeSql(query);
        functionCatalog.setId(functionCatalogId);
    }

    protected long _associateFunctionCatalogWithVersion(final DatabaseConnection databaseConnection, final long versionId, final FunctionCatalog functionCatalog) throws DatabaseException {
        if (functionCatalog == null) {
            throw new InvalidParameterException("Attempted to associate Version and Catalog with a null object.");
        }

        final long functionCatalogId = functionCatalog.getId();

        final Query query = new Query("INSERT INTO versions_function_catalogs (version_id, function_catalog_id) VALUES (?, ?)")
            .setParameter(versionId)
            .setParameter(functionCatalogId)
        ;

        return databaseConnection.executeSql(query);
    }
}
