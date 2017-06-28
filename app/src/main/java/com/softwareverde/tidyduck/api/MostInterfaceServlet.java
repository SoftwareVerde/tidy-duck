package com.softwareverde.tidyduck.api;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.json.Json;
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
import java.io.IOException;
import java.sql.Connection;
import java.util.List;

public class MostInterfaceServlet extends AuthenticatedJsonServlet {
    private final Logger _logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected Json handleAuthenticatedRequest(HttpServletRequest request, HttpMethod httpMethod, final long accountId, Environment environment) throws Exception {
        String finalUrlSegment = BaseServlet.getFinalUrlSegment(request);
        if ("most-interface".equals(finalUrlSegment)) {
            if (httpMethod == HttpMethod.POST) {
                return _insertMostInterface(request, environment);
            }
            if (httpMethod == HttpMethod.GET) {
                long functionBlockId = Util.parseLong(Util.coalesce(request.getParameter("function_block_id")));
                if (functionBlockId < 1) {
                    return super._generateErrorJson("Invalid function block id.");
                }
                return _listMostInterfaces(functionBlockId, environment);
            }
        } else if ("search".equals(finalUrlSegment)){
            if (httpMethod == HttpMethod.GET) {
                String searchString = Util.coalesce(request.getParameter("name"));
                if (searchString.length() < 1) {
                    return super._generateErrorJson("Invalid search string for interface.");
                }
                return _listMostInterfacesMatchingSearchString(searchString, environment);
            }
        } else if ("function-blocks".equals(finalUrlSegment)) {
            // most-interface/<id>/function-blocks
            if (httpMethod == HttpMethod.POST) {
                final long mostInterfaceId = Util.parseLong(getNthFromLastUrlSegment(request, 1));
                if (mostInterfaceId < 1) {
                    return super._generateErrorJson("Invalid function block id.");
                }
                return _associateInterfaceWithFunctionBlock(request, mostInterfaceId, environment);
            }
        } else {
            // not base interface, must have ID
            long mostInterfaceId = Util.parseLong(finalUrlSegment);
            if (mostInterfaceId < 1) {
                return super._generateErrorJson("Invalid interface id.");
            }

            if (httpMethod == HttpMethod.POST) {
                return _updateMostInterface(request, mostInterfaceId, environment);
            }
            else if (httpMethod == HttpMethod.DELETE) {
                return _deleteMostInterfaceFromFunctionBlock(request, mostInterfaceId, environment);
            }
        }
        return super._generateErrorJson("Unimplemented HTTP method in request.");
    }

    protected Json _insertMostInterface(HttpServletRequest request, Environment environment) throws Exception {
        final Json jsonRequest = _getRequestDataAsJson(request);
        final Json response = _generateSuccessJson();

        final Long functionBlockId = Util.parseLong(jsonRequest.getString("functionBlockId"));

        { // Validate Inputs
            if (functionBlockId < 1) {
                _logger.error("Unable to parse Function Block ID: " + functionBlockId);
                return super._generateErrorJson("Invalid Function Block ID: " + functionBlockId);
            }
        }

        final Json mostInterfaceJson = jsonRequest.get("mostInterface");
        try {
            MostInterface mostInterface = _populateMostInterfaceFromJson(mostInterfaceJson);

            DatabaseManager databaseManager = new DatabaseManager(environment);
            databaseManager.insertMostInterface(functionBlockId, mostInterface);
            response.put("mostInterfaceId", mostInterface.getId());
        }
        catch (final Exception exception) {
            _logger.error("Unable to insert Interface.", exception);
            return super._generateErrorJson("Unable to insert Interface: " + exception.getMessage());
        }

        return response;
    }

    protected Json _updateMostInterface(HttpServletRequest httpRequest, long mostInterfaceId, Environment environment) throws Exception {
        final Json request = super._getRequestDataAsJson(httpRequest);

        final Long functionBlockId = Util.parseLong(request.getString("functionBlockId"));

        final Json mostInterfaceJson = request.get("mostInterface");

        { // Validate Inputs
            if (functionBlockId < 1) {
                _logger.error("Unable to parse Function Block ID: " + functionBlockId);
                return super._generateErrorJson("Invalid Function Block ID: " + functionBlockId);
            }
        }

        try {
            MostInterface mostInterface = _populateMostInterfaceFromJson(mostInterfaceJson);
            mostInterface.setId(mostInterfaceId);

            DatabaseManager databaseManager = new DatabaseManager(environment);
            databaseManager.updateMostInterface(mostInterfaceId, mostInterface);
        } catch (final Exception exception) {
            String errorMessage = "Unable to update interface: " + exception.getMessage();
            _logger.error(errorMessage, exception);
            return super._generateErrorJson(errorMessage);
        }

        Json response = new Json(false);
        super._setJsonSuccessFields(response);
        return response;
    }

    private Json _associateInterfaceWithFunctionBlock(final HttpServletRequest request, final long mostInterfaceId, final Environment environment) throws IOException {
        final Json jsonRequest = super._getRequestDataAsJson(request);
        final Json response = _generateSuccessJson();

        final Long functionBlockId = Util.parseLong(jsonRequest.getString("functionBlockId"));

        { // Validate Inputs
            if (functionBlockId < 1) {
                _logger.error("Unable to parse Function Block ID: " + functionBlockId);
                return super._generateErrorJson("Invalid Function Block ID: " + functionBlockId);
            }
        }

        try {
            DatabaseManager databaseManager = new DatabaseManager(environment);
            databaseManager.associateMostInterfaceWithFunctionBlock(functionBlockId, mostInterfaceId);
        }
        catch (final Exception exception) {
            _logger.error("Unable to insert Interface.", exception);
            return super._generateErrorJson("Unable to insert Interface: " + exception.getMessage());
        }

        return response;
    }

    protected Json _deleteMostInterfaceFromFunctionBlock(HttpServletRequest request, long mostInterfaceId, Environment environment) {
        final String functionBlockIdString = request.getParameter("functionBlockId");
        final Long functionBlockId = Util.parseLong(functionBlockIdString);

        { // Validate Inputs
            if (functionBlockId == null || functionBlockId < 1) {
                return super._generateErrorJson(String.format("Invalid function block id: %s", functionBlockIdString));
            }
        }

        try {
            final DatabaseManager databaseManager = new DatabaseManager(environment);
            databaseManager.deleteMostInterface(functionBlockId, mostInterfaceId);
        } catch (final DatabaseException exception) {
            final String errorMessage = String.format("Unable to delete interface %d from function block %d.", mostInterfaceId, functionBlockId);
            _logger.error(errorMessage, exception);
            return super._generateErrorJson(errorMessage);
        }

        final Json response = new Json(false);
        super._setJsonSuccessFields(response);
        return response;
    }

    protected Json _listMostInterfaces(long functionBlockId, Environment environment) {
        try (final DatabaseConnection<Connection> databaseConnection = environment.getNewDatabaseConnection()) {
            final Json response = new Json(false);

            final MostInterfaceInflater mostInterfaceInflater = new MostInterfaceInflater(databaseConnection);
            final List<MostInterface> mostInterfaces = mostInterfaceInflater.inflateMostInterfacesFromFunctionBlockId(functionBlockId);

            final Json mostInterfacesJson = new Json(true);
            for (final MostInterface mostInterface : mostInterfaces) {
                final Json mostInterfaceJson = _toJson(mostInterface);
                mostInterfacesJson.add(mostInterfaceJson);
            }
            response.put("mostInterfaces", mostInterfacesJson);

            super._setJsonSuccessFields(response);
            return response;
        } catch (final DatabaseException exception) {
            _logger.error("Unable to list interfaces", exception);
            return super._generateErrorJson("Unable to list interfaces.");
        }
    }

    protected Json _listMostInterfacesMatchingSearchString(String searchString, Environment environment) {
        try (final DatabaseConnection<Connection> databaseConnection = environment.getNewDatabaseConnection()) {
            final Json response = new Json(false);

            final MostInterfaceInflater mostInterfaceInflater = new MostInterfaceInflater(databaseConnection);
            final List<MostInterface> mostInterfaces = mostInterfaceInflater.inflateMostInterfacesMatchingSearchString(searchString);

            final Json mostInterfacesJson = new Json(true);
            for (final MostInterface mostInterface : mostInterfaces) {
                final Json mostInterfaceJson = _toJson(mostInterface);
                mostInterfacesJson.add(mostInterfaceJson);
            }
            response.put("mostInterfaces", mostInterfacesJson);

            super._setJsonSuccessFields(response);
            return response;
        } catch (final DatabaseException exception) {
            _logger.error("Unable to list interfaces from search", exception);
            return super._generateErrorJson("Unable to list interfaces from search.");
        }
    }

    protected MostInterface _populateMostInterfaceFromJson(final Json mostInterfaceJson) throws Exception {
        final String mostId = mostInterfaceJson.getString("mostId");
        final String name = mostInterfaceJson.getString("name");
        final String description = mostInterfaceJson.getString("description");
        final String version = mostInterfaceJson.getString("version");

        { // Validate Inputs
            if (Util.isBlank(mostId)) {
                throw new Exception("Invalid Most ID");
            }

            if (Util.isBlank(name)) {
                throw new Exception("Name field is required.");
            }

            if (Util.isBlank(description)) {
                throw new Exception("Description field is required.");
            }

            if (Util.isBlank(version)) {
                throw new Exception("Version field is required.");
            }

        }

        MostInterface mostInterface = new MostInterface();
        mostInterface.setMostId(mostId);
        mostInterface.setName(name);
        mostInterface.setVersion(version);
        mostInterface.setDescription(description);

        return mostInterface;
    }

    private Json _toJson(final MostInterface mostInterface) {
        final Json mostInterfaceJson = new Json(false);
        mostInterfaceJson.put("id", mostInterface.getId());
        mostInterfaceJson.put("mostId", mostInterface.getMostId());
        mostInterfaceJson.put("name", mostInterface.getName());
        mostInterfaceJson.put("description", mostInterface.getDescription());
        mostInterfaceJson.put("lastModifiedDate", DateUtil.dateToDateString(mostInterface.getLastModifiedDate()));
        mostInterfaceJson.put("version", mostInterface.getVersion());
        return mostInterfaceJson;
    }
}
