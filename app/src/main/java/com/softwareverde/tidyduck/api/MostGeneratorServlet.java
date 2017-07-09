package com.softwareverde.tidyduck.api;

import com.softwareverde.database.Database;
import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.tidyduck.FunctionCatalog;
import com.softwareverde.tidyduck.database.FunctionCatalogInflater;
import com.softwareverde.tidyduck.environment.Environment;
import com.softwareverde.tidyduck.mostadapter.MostAdapter;
import com.softwareverde.tidyduck.mostadapter.MostAdapterException;
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
            _authenticationError(response);
            return;
        }

        long functionCatalogId = Util.parseLong(request.getParameter("function_catalog_id"));
        if (functionCatalogId < 1) {
            _logger.error("Invalid function catalog id.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        returnMostAsAttachment(functionCatalogId, response, database);
    }

    private void returnMostAsAttachment(final long functionCatalogId, final HttpServletResponse response, final Database<Connection> database) throws IOException {
        FunctionCatalog functionCatalog = null;
        try (DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            final FunctionCatalogInflater functionCatalogInflater = new FunctionCatalogInflater(databaseConnection);
            functionCatalog = functionCatalogInflater.inflateFunctionCatalog(functionCatalogId, true);
        }
        catch (final Exception e) {
            _logger.error("Unable to inflate function catalog.", e);
            _internalServerError(response);
            return;
        }

        String mostXml = null;
        try {
            final MostAdapter mostAdapter = new MostAdapter();
            mostAdapter.setIndented(true);
            mostXml = mostAdapter.getMostXml(functionCatalog);

            final int mostXmlLength = mostXml.getBytes().length;
            final String functionCatalogName = functionCatalog.getName();
            _logger.info(String.format("Generated %d bytes for %s (id: %d).", mostXmlLength, functionCatalogName, functionCatalogId));
        }
        catch (final MostAdapterException e) {
            _logger.error("Problem generating MOST XML for function catalog " + functionCatalog + " (" + functionCatalog.getName() + ").");
            _internalServerError(response);
            return;
        }

        final String fileName = functionCatalog.getName()+".xml";
        response.setHeader("Content-Disposition", "attachment;filename="+fileName);
        final PrintWriter writer = response.getWriter();
        writer.write(mostXml);
    }

    private void _internalServerError(final HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        final PrintWriter writer = response.getWriter();
        writer.write("Unable to generate MOST.");
    }

    private void _authenticationError(final HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        final PrintWriter writer = response.getWriter();
        writer.write("Not authorized.");
    }
}
