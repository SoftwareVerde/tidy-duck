package com.softwareverde.tidyduck.api;

import com.softwareverde.database.Database;
import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.mostadapter.MostAdapter;
import com.softwareverde.tidyduck.Account;
import com.softwareverde.tidyduck.AuthorizationException;
import com.softwareverde.tidyduck.Permission;
import com.softwareverde.tidyduck.database.AccountInflater;
import com.softwareverde.tidyduck.database.FunctionCatalogInflater;
import com.softwareverde.tidyduck.environment.Environment;
import com.softwareverde.tidyduck.most.FunctionCatalog;
import com.softwareverde.tidyduck.most.MostTypeConverter;
import com.softwareverde.tomcat.servlet.BaseServlet;
import com.softwareverde.tomcat.servlet.Session;
import com.softwareverde.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;

public class MostGeneratorServlet extends BaseServlet {
    private final Logger _logger = LoggerFactory.getLogger(getClass());

    @Override
    protected void handleRequest(final HttpServletRequest request, final HttpServletResponse response, final HttpMethod method, final Environment environment) throws ServletException, IOException {
        final Database<Connection> database = environment.getDatabase();

        if (! Session.isAuthenticated(request)) {
            _logger.error("Rejected unauthenticated user.");
            _authorizationError(response);
            return;
        }

        try {
            checkPermissions(request, environment.getDatabase());
        } catch (Exception e) {
            _logger.error("Unable to authorize user.", e);
            _authorizationError(response);
            return;
        }

        long functionCatalogId = Util.parseLong(request.getParameter("function_catalog_id"));
        if (functionCatalogId < 1) {
            _logger.error("Invalid function catalog id.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        _returnMostXmlAsAttachment(functionCatalogId, response, database);
    }

    private void checkPermissions(final HttpServletRequest request, final Database<Connection> database) throws DatabaseException, AuthorizationException {
        final long accountId = Session.getAccountId(request);
        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            final AccountInflater accountInflater = new AccountInflater(databaseConnection);
            final Account currentAccount = accountInflater.inflateAccount(accountId);

            currentAccount.requirePermission(Permission.MOST_COMPONENTS_VIEW);
        }
    }

    private void _returnMostXmlAsAttachment(final long functionCatalogId, final HttpServletResponse response, final Database<Connection> database) throws IOException {
        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            final FunctionCatalogInflater functionCatalogInflater = new FunctionCatalogInflater(databaseConnection);
            final FunctionCatalog functionCatalog = functionCatalogInflater.inflateFunctionCatalog(functionCatalogId, true);

            final MostAdapter mostAdapter = new MostAdapter();
            mostAdapter.setIndented(true);

            MostTypeConverter mostTypeConverter = new MostTypeConverter();
            final String mostXml = mostAdapter.getMostXml(mostTypeConverter.convertFunctionCatalog(functionCatalog));

            final int mostXmlLength = mostXml.getBytes().length;
            final String functionCatalogName = functionCatalog.getName();
            _logger.info(String.format("Generated %d bytes for %s (id: %d).", mostXmlLength, functionCatalogName, functionCatalogId));

            final String fileName = functionCatalog.getName()+".xml";
            response.setHeader("Content-Disposition", "attachment;filename="+fileName);
            final PrintWriter writer = response.getWriter();
            writer.write(mostXml);
        }
        catch (final Exception exception) {
            _logger.error("Problem generating MOST XML for function catalog " + functionCatalogId + ".", exception);
            _internalServerError(response);
            return;
        }
    }

    private void _internalServerError(final HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        final PrintWriter writer = response.getWriter();
        writer.write("Unable to generate MOST.");
    }

    private void _authorizationError(final HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        final PrintWriter writer = response.getWriter();
        writer.write("Not authorized.");
    }
}
