package com.softwareverde.tidyduck.api;

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
    protected void handleRequest(HttpServletRequest request, HttpServletResponse response, HttpMethod method, Environment environment) throws ServletException, IOException {
        if (! Session.isAuthenticated(request)) {
            authenticationError(response);
            return;
        }

        long functionCatalogId = Util.parseLong(request.getParameter("function_catalog_id"));
        if (functionCatalogId < 1) {
            _logger.error("Invalid function catalog id.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        returnMostAsAttachment(functionCatalogId, response, environment);
    }

    private void returnMostAsAttachment(long functionCatalogId, HttpServletResponse response, Environment environment) throws IOException {
        FunctionCatalog functionCatalog = null;
        try (DatabaseConnection<Connection> databaseConnection = environment.getNewDatabaseConnection()) {
            FunctionCatalogInflater functionCatalogInflater = new FunctionCatalogInflater(databaseConnection);
            functionCatalog = functionCatalogInflater.inflateFunctionCatalog(functionCatalogId, true);
        } catch (Exception e) {
            _logger.error("Unable to inflate function catalog.", e);
            internalServerError(response);
            return;
        }

        String mostXml = null;
        try {
            MostAdapter mostAdapter = new MostAdapter();
            mostXml = mostAdapter.getMostXml(functionCatalog);

            final int mostXmlLength = mostXml.getBytes().length;
            final String functionCatalogName = functionCatalog.getName();
            _logger.info(String.format("Generated %d bytes for %s (id: %d).", mostXmlLength, functionCatalogName, functionCatalogId));
        } catch (MostAdapterException e) {
            _logger.error("Problem generating MOST XML for function catalog " + functionCatalog + " (" + functionCatalog.getName() + ").");
            internalServerError(response);
            return;
        }

        String fileName = functionCatalog.getName()+".xml";
        response.setHeader("Content-Disposition", "attachment;filename="+fileName);
        PrintWriter writer = response.getWriter();
        writer.write(mostXml);
    }

    private void internalServerError(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        PrintWriter writer = response.getWriter();
        writer.write("Unable to generate MOST.");
    }

    private void authenticationError(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        PrintWriter writer = response.getWriter();
        writer.write("Not authorized.");
    }
}
