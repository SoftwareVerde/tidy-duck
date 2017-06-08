package com.softwareverde.tidyduck.api;

import com.softwareverde.json.Json;
import com.softwareverde.tidyduck.Author;
import com.softwareverde.tidyduck.Company;
import com.softwareverde.tidyduck.FunctionCatalog;
import com.softwareverde.tidyduck.environment.Environment;
import com.softwareverde.tomcat.servlet.BaseServlet;
import com.softwareverde.tomcat.servlet.JsonServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FunctionCatalogServlet extends JsonServlet {

    private static final String INSERT_FUNCTION_CATALOG_SQL = "INSERT INTO function_catalogs (release, release_date, author_id, company_id) VALUES (?, ?, ?, ?)";
    private static final String ADD_FUNCTION_CATALOG_TO_VERSION_SQL = "INSERT INTO versions_function_catalogs (version_id, function_catalog_id) VALUES (?, ?)";

    private static final String GET_FUNCTION_CATALOGS_SQL = "SELECT function_catalog_id, release, release_date, author_id, company_id" +
                                                            " FROM function_catalogs INNER JOIN versions_function_catalogs" +
                                                            " ON function_catalogs.id = versions_function_catalogs.function_catalog_id" +
                                                            " WHERE version_id = ?";

    private final Logger _logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected Json handleRequest(final HttpServletRequest request, final HttpMethod httpMethod, final Environment environment) throws Exception {
        if (httpMethod == HttpMethod.POST) {
            return addFunctionCatalog(request, environment);
        }
        if (httpMethod == HttpMethod.GET) {
            long versionId = Long.parseLong(request.getParameter("versionId"));
            return listFunctionCatalogs(versionId, environment);
        }
        return super.generateErrorJson("Unimplemented HTTP method in request.");
    }

    private Json listFunctionCatalogs(long versionId, Environment environment) {
        Json response = new Json(false);

        Connection connection = null;
        try {
            connection = environment.getNewDatabaseConnection();
            List<FunctionCatalog> functionCatalogs = getFunctionCatalogs(versionId, connection);
            Json catalogs = new Json(true);
            for (FunctionCatalog functionCatalog : functionCatalogs) {
                Json catalog = new Json(false);
                catalog.put("release", functionCatalog.getRelease());
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                catalog.put("releaseDate", format.format(functionCatalog.getReleaseDate()));
                catalog.put("authorId", functionCatalog.getAuthor().getId());
                catalog.put("companyId", functionCatalog.getCompany().getId());
                catalogs.add(catalog);
            }
            response.put("functionCatalogs", catalogs);
        } catch (Exception e) {
            _logger.error("Unable to list function catalogs.", e);
            return super.generateErrorJson("Unable to list function catalogs.");
        } finally {
            Environment.close(connection, null, null);
        }

        super.setJsonSuccessFields(response);
        return response;
    }

    private List<FunctionCatalog> getFunctionCatalogs(long versionId, Connection connection) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement(GET_FUNCTION_CATALOGS_SQL);
            ps.setLong(1, versionId);
            rs = ps.executeQuery();
            ArrayList<FunctionCatalog> functionCatalogs = new ArrayList<FunctionCatalog>();
            while (rs.next()) {
                FunctionCatalog functionCatalog = new FunctionCatalog();
                functionCatalog.setRelease(rs.getString("release"));
                functionCatalog.setReleaseDate(rs.getDate("releaseDate"));
                Author author = new Author();
                author.setId(rs.getLong("author_id"));
                functionCatalog.setAuthor(author);
                Company company = new Company();
                company.setId(rs.getLong("company_id"));
                functionCatalog.setCompany(company);
                functionCatalogs.add(functionCatalog);
            }
            return functionCatalogs;
        } finally {
            Environment.close(null, ps, rs);
        }
    }

    private Json addFunctionCatalog(HttpServletRequest httpRequest, Environment environment) throws IOException {
        Json request = super.getRequestDataAsJson(httpRequest);
        Json response = new Json(false);

        long versionId = Long.parseLong(request.getString("versionId"));
        FunctionCatalog functionCatalog = new FunctionCatalog();
        functionCatalog.setRelease(request.getString("release"));
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String releaseDateString = request.getString("releaseDate");
        Date releaseDate = null;
        try {
            releaseDate = format.parse(releaseDateString);
            functionCatalog.setReleaseDate(releaseDate);
        } catch (ParseException e) {
            _logger.error("Unable to parse release date", e);
            return super.generateErrorJson("Unable to parse release date: " + releaseDateString);
        }
        Author author = new Author();
        author.setId(request.getInteger("authorId"));
        functionCatalog.setAuthor(author);
        Company company = new Company();
        company.setId(request.getInteger("companyId"));
        functionCatalog.setCompany(company);

        Connection connection = null;
        try {
            connection = environment.getNewDatabaseConnection();
            connection.setAutoCommit(false);
            long functionCatalogId = addFunctionCatalog(connection, functionCatalog);
            associateFunctionCatalogWithVersion(connection, versionId, functionCatalogId);
            connection.commit();
            response.put("functionCatalogId", functionCatalogId);
        } catch (Exception e) {
            _logger.error("Problem adding function catalog.", e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                _logger.error("Unable to roll back changes.");
            }
            return super.generateErrorJson("Unable to add function catalog: " + e.getMessage());
        } finally {
            Environment.close(connection, null, null);
        }

        super.setJsonSuccessFields(response);
        return response;
    }

    private long addFunctionCatalog(Connection connection, FunctionCatalog functionCatalog) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement(INSERT_FUNCTION_CATALOG_SQL, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, functionCatalog.getRelease());
            ps.setDate(2, new java.sql.Date(functionCatalog.getReleaseDate().getTime()));
            ps.setLong(3, functionCatalog.getAuthor().getId());
            ps.setLong(4, functionCatalog.getCompany().getId());
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                // return generated ID
                return rs.getLong(1);
            }
            throw new SQLException("Unable to determine new function catalog ID.");
        } finally {
            Environment.close(null, ps, rs);
        }
    }

    private void associateFunctionCatalogWithVersion(Connection connection, long versionId, long functionCatalogId) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement(ADD_FUNCTION_CATALOG_TO_VERSION_SQL);
            ps.setLong(1, versionId);
            ps.setLong(2, functionCatalogId);
            ps.executeUpdate();
        } finally {
            Environment.close(null, ps, rs);
        }
    }
}
