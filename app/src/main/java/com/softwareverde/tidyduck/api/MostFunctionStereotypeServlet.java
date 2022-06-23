package com.softwareverde.tidyduck.api;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.http.HttpMethod;
import com.softwareverde.http.server.servlet.request.Request;
import com.softwareverde.http.server.servlet.routed.json.AuthenticatedJsonApplicationServlet;
import com.softwareverde.http.server.servlet.routed.json.JsonRequestHandler;
import com.softwareverde.http.server.servlet.session.SessionManager;
import com.softwareverde.json.Json;
import com.softwareverde.tidyduck.Account;
import com.softwareverde.tidyduck.Permission;
import com.softwareverde.tidyduck.authentication.TidyDuckAuthenticator;
import com.softwareverde.tidyduck.database.MostFunctionStereotypeInflater;
import com.softwareverde.tidyduck.environment.TidyDuckEnvironment;
import com.softwareverde.tidyduck.most.MostFunctionStereotype;
import com.softwareverde.tidyduck.most.Operation;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

public class MostFunctionStereotypeServlet extends AuthenticatedJsonApplicationServlet<TidyDuckEnvironment> {
    

    public MostFunctionStereotypeServlet(final TidyDuckEnvironment environment, final SessionManager sessionManager, final TidyDuckAuthenticator authenticator) {
        super(environment, sessionManager);

        super._defineEndpoint("most-function-stereotypes", HttpMethod.GET, new TidyDuckRequestHandler(sessionManager, authenticator) {
            @Override
            public Json handleRequest(final Account currentAccount, final Request request, final TidyDuckEnvironment environment, final Map<String, String> parameters) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_VIEW);

                return _listMostFunctionStereotypes(environment);
            }
        });
    }

    private Json _listMostFunctionStereotypes(final TidyDuckEnvironment environment) throws Exception {
        try (final DatabaseConnection<Connection> databaseConnection = environment.getDatabase().newConnection()) {
            Json response = new Json(false);

            final MostFunctionStereotypeInflater mostFunctionStereotypeInflater = new MostFunctionStereotypeInflater(databaseConnection);
            final List<MostFunctionStereotype> mostFunctionStereotypes = mostFunctionStereotypeInflater.inflateMostFunctionStereotypes();
            final Json mostFunctionStereotypesJson = new Json(true);
            for (MostFunctionStereotype mostFunctionStereotype : mostFunctionStereotypes) {
                Json mostFunctionStereotypeJson = _toJson(mostFunctionStereotype);
                mostFunctionStereotypesJson.add(mostFunctionStereotypeJson);
            }
            response.put("mostFunctionStereotypes", mostFunctionStereotypesJson);

            JsonRequestHandler.setJsonSuccessFields(response);
            return response;
        } catch (final DatabaseException exception) {
            String msg = "Unable to inflate most stereotypes.";
            throw new Exception(msg, exception);
        }
    }

    private Json _toJson(final MostFunctionStereotype mostFunctionStereotype) {
        final Json json = new Json(false);

        json.put("id", mostFunctionStereotype.getId());
        json.put("name", mostFunctionStereotype.getName());
        json.put("supportsNotification", mostFunctionStereotype.supportsNotification());
        json.put("category", mostFunctionStereotype.getCategory());

        // operations
        final Json operationsJson = new Json(true);
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
