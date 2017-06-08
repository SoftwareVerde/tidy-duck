package com.softwareverde.tidyduck;

import com.softwareverde.Environment;
import com.softwareverde.json.Json;
import com.softwareverde.servlet.BaseServlet;
import com.softwareverde.servlet.JsonServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FunctionCatalogServlet extends JsonServlet {

    private static final String INSERT_FUNCTION_CATALOG_SQL = "INSERT INTO function_catalogs (release, release_date, author_id, company_id) VALUES (?, ?, ?, ?)";
    private static final String ADD_FUNCTION_CATALOG_TO_VERSION_SQL = "INSERT INTO versions_function_catalogs (version_id, function_catalog_id) VALUES (?, ?)";

    private final Logger _logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected Json handleRequest(HttpServletRequest request, BaseServlet.HttpMethod httpMethod, Environment environment) throws Exception {
        if (httpMethod == BaseServlet.HttpMethod.POST) {
            return addFunctionCatalog(request, environment.getDatabase());
        }
        return null;
    }

    private Json addFunctionCatalog(HttpServletRequest httpRequest, Connection connection) throws IOException {
        Json request = super.getRequestDataAsJson(httpRequest);
        Json response = new Json();

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
            return super.getErrorJson("Unable to parse release date: " + releaseDateString);
        }
        Author author = new Author();
        author.setId(request.getInteger("authorId"));
        functionCatalog.setAuthor(author);
        Company company = new Company();
        company.setId(request.getInteger("companyId"));
        functionCatalog.setCompany(company);

        try {
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
            return super.getErrorJson("Unable to add function catalog: " + e.getMessage());
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
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
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
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
        }
    }
}
