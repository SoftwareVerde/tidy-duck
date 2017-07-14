package com.softwareverde.tidyduck.api;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.json.Json;
import com.softwareverde.tidyduck.MostType;
import com.softwareverde.tidyduck.database.MostTypeInflater;
import com.softwareverde.tidyduck.environment.Environment;
import com.softwareverde.tomcat.servlet.AuthenticatedJsonServlet;
import com.softwareverde.tomcat.servlet.BaseServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.util.List;

public class MostTypeServlet extends AuthenticatedJsonServlet {
    private Logger _logger = LoggerFactory.getLogger(getClass());

    @Override
    protected Json handleAuthenticatedRequest(HttpServletRequest request, HttpMethod httpMethod, long accountId, Environment environment) throws Exception {
        String finalUrlSegment = BaseServlet.getFinalUrlSegment(request);
        if ("most-type".equals(finalUrlSegment)) {
            if (httpMethod == HttpMethod.GET) {
                return _listMostTypes(environment);
            }
        }
        return super._generateErrorJson("Unimplemented HTTP method in request.");
    }

    private Json _listMostTypes(final Environment environment) {
        try (final DatabaseConnection<Connection> databaseConnection = environment.getNewDatabaseConnection()) {
            Json response = new Json(false);

            MostTypeInflater mostTypeInflater = new MostTypeInflater(databaseConnection);
            List<MostType> mostTypes = mostTypeInflater.inflateMostTypes();
            Json mostTypesJson = new Json(true);
            for (MostType mostType : mostTypes) {
                Json mostTypeJson = _toJson(mostType);
                mostTypesJson.add(mostTypeJson);
            }
            response.put("mostTypes", mostTypesJson);

            super._setJsonSuccessFields(response);
            return response;
        } catch (DatabaseException e) {
            String msg = "Unable to inflate most types.";
            _logger.error(msg, e);
            return super._generateErrorJson(msg);
        }
    }

    private Json _toJson(final MostType mostType) {
        Json json = new Json(false);

        json.put("id", mostType.getId());
        json.put("name", mostType.getName());

        return json;
    }
}
