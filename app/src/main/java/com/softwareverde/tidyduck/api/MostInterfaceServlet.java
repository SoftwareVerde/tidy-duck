package com.softwareverde.tidyduck.api;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.json.Json;
import com.softwareverde.tidyduck.Author;
import com.softwareverde.tidyduck.Company;
import com.softwareverde.tidyduck.DateUtil;
import com.softwareverde.tidyduck.MostInterface;
import com.softwareverde.tidyduck.database.DatabaseManager;
import com.softwareverde.tidyduck.database.MostInterfaceInflater;
import com.softwareverde.tidyduck.environment.Environment;
import com.softwareverde.tidyduck.util.Util;
import com.softwareverde.tomcat.servlet.AuthenticatedJsonServlet;
import com.softwareverde.tomcat.servlet.BaseServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.util.List;

public class MostInterfaceServlet extends AuthenticatedJsonServlet {
    private final Logger _logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected Json handleAuthenticatedRequest(HttpServletRequest request, HttpMethod httpMethod, final long accountId, Environment environment) throws Exception {
        String finalUrlSegment = BaseServlet.getFinalUrlSegment(request);
        if ("most-interface".equals(finalUrlSegment)) {
            if (httpMethod == HttpMethod.POST) {
                //return _insertMostInterface(request, environment);
            }
            if (httpMethod == HttpMethod.GET) {
                long functionBlockId = Util.parseLong(Util.coalesce(request.getParameter("function_block_id")));
                if (functionBlockId < 1) {
                    return super._generateErrorJson("Invalid function block id.");
                }
                return _listMostInterfaces(functionBlockId, environment);
            }
        } else {
            // not base interface, must have ID
            long mostInterfaceId = Util.parseLong(finalUrlSegment);
            if (mostInterfaceId < 1) {
                return super._generateErrorJson("Invalid interface id.");
            }

            if (httpMethod == HttpMethod.POST) {
                //return _updateMostInterface(request, mostInterfaceId, environment);
            }
            else if (httpMethod == HttpMethod.DELETE) {
                //return _deleteMostInterfaceFromFunctionBlock(request, mostInterfaceId, environment);
            }
        }
        return super._generateErrorJson("Unimplemented HTTP method in request.");
    }

    protected Json _listMostInterfaces(long functionBlockId, Environment environment) {
        try {
            final Json response = new Json(false);

            final DatabaseConnection<Connection> databaseConnection = environment.getNewDatabaseConnection();
            final MostInterfaceInflater mostInterfaceInflater = new MostInterfaceInflater(databaseConnection);
            final List<MostInterface> mostInterfaces = mostInterfaceInflater.inflateMostInterfacesFromFunctionBlockId(functionBlockId);

            final Json mostInterfacesJson = new Json(true);
            for (final MostInterface mostInterface : mostInterfaces) {
                final Json mostInterfaceJson = new Json(false);
                mostInterfaceJson.put("id", mostInterface.getId());
                mostInterfaceJson.put("mostId", mostInterface.getMostId());
                mostInterfaceJson.put("name", mostInterface.getName());
                mostInterfaceJson.put("description", mostInterface.getDescription());
                mostInterfaceJson.put("lastModifiedDate", DateUtil.dateToDateString(mostInterface.getLastModifiedDate()));
                mostInterfaceJson.put("version", mostInterface.getVersion());
                mostInterfacesJson.add(mostInterfaceJson);
            }
            response.put("mostInterfaces", mostInterfacesJson);

            super._setJsonSuccessFields(response);
            return response;
        }
        catch (final DatabaseException exception) {
            _logger.error("Unable to list interfaces", exception);
            return super._generateErrorJson("Unable to list interfaces.");
        }
    }
}