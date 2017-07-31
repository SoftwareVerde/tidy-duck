package com.softwareverde.tidyduck.api;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.json.Json;
import com.softwareverde.tidyduck.database.MostFunctionStereotypeInflater;
import com.softwareverde.tidyduck.environment.Environment;
import com.softwareverde.tidyduck.most.MostFunctionStereotype;
import com.softwareverde.tidyduck.most.Operation;
import com.softwareverde.tomcat.servlet.AuthenticatedJsonServlet;
import com.softwareverde.tomcat.servlet.BaseServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.util.List;

public class MostFunctionStereotypeServlet extends AuthenticatedJsonServlet {
    private Logger _logger = LoggerFactory.getLogger(getClass());

    @Override
    protected Json handleAuthenticatedRequest(HttpServletRequest request, HttpMethod httpMethod, long accountId, Environment environment) throws Exception {
        String finalUrlSegment = BaseServlet.getFinalUrlSegment(request);
        if ("most-function-stereotypes".equals(finalUrlSegment)) {
            if (httpMethod == HttpMethod.GET) {
                return _listMostFunctionStereotypes(environment);
            }
        }
        return super._generateErrorJson("Unimplemented HTTP method in request.");
    }

    private Json _listMostFunctionStereotypes(final Environment environment) {
        try (final DatabaseConnection<Connection> databaseConnection = environment.getDatabase().newConnection()) {
            Json response = new Json(false);

            MostFunctionStereotypeInflater mostFunctionStereotypeInflater = new MostFunctionStereotypeInflater(databaseConnection);
            List<MostFunctionStereotype> mostFunctionStereotypes = mostFunctionStereotypeInflater.inflateMostFunctionStereotypes();
            Json mostFunctionStereotypesJson = new Json(true);
            for (MostFunctionStereotype mostFunctionStereotype : mostFunctionStereotypes) {
                Json mostFunctionStereotypeJson = _toJson(mostFunctionStereotype);
                mostFunctionStereotypesJson.add(mostFunctionStereotypeJson);
            }
            response.put("mostFunctionStereotypes", mostFunctionStereotypesJson);

            super._setJsonSuccessFields(response);
            return response;
        } catch (DatabaseException e) {
            String msg = "Unable to inflate most stereotypes.";
            _logger.error(msg, e);
            return super._generateErrorJson(msg);
        }
    }

    private Json _toJson(final MostFunctionStereotype mostFunctionStereotype) {
        Json json = new Json(false);

        json.put("id", mostFunctionStereotype.getId());
        json.put("name", mostFunctionStereotype.getName());
        json.put("supportsNotification", mostFunctionStereotype.supportsNotification());
        json.put("category", mostFunctionStereotype.getCategory());

        // operations
        Json operationsJson = new Json(true);
        for (final Operation operation : mostFunctionStereotype.getOperations()) {
            Json operationJson = new Json(false);
            operationJson.put("id", operation.getId());
            operationJson.put("name", operation.getName());
            operationsJson.add(operationJson);
        }
        json.put("operations", operationsJson);

        return json;
    }
}
