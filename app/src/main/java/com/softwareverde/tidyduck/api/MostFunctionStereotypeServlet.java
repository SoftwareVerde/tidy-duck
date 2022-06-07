package com.softwareverde.tidyduck.api;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.json.Json;
import com.softwareverde.logging.Logger;
import com.softwareverde.tidyduck.Account;
import com.softwareverde.tidyduck.Permission;
import com.softwareverde.tidyduck.database.MostFunctionStereotypeInflater;
import com.softwareverde.tidyduck.environment.Environment;
import com.softwareverde.tidyduck.most.MostFunctionStereotype;
import com.softwareverde.tidyduck.most.Operation;
import com.softwareverde.tomcat.servlet.AuthenticatedJsonServlet;


import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

public class MostFunctionStereotypeServlet extends AuthenticatedJsonServlet {
    

    public MostFunctionStereotypeServlet() {
        super._defineEndpoint("most-function-stereotypes", HttpMethod.GET, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_VIEW);

                return _listMostFunctionStereotypes(environment);
            }
        });
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
            Logger.error(msg, e);
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
            operationJson.put("channel", operation.getChannel());
            operationsJson.add(operationJson);
        }
        json.put("operations", operationsJson);

        return json;
    }
}
