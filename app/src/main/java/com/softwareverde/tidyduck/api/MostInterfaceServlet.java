package com.softwareverde.tidyduck.api;

import com.softwareverde.database.Database;
import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.json.Json;
import com.softwareverde.tidyduck.DateUtil;
import com.softwareverde.tidyduck.database.DatabaseManager;
import com.softwareverde.tidyduck.database.MostInterfaceInflater;
import com.softwareverde.tidyduck.environment.Environment;
import com.softwareverde.tidyduck.most.MostInterface;
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
    protected Json handleAuthenticatedRequest(final HttpServletRequest request, final HttpMethod httpMethod, final long accountId, final Environment environment) throws Exception {
        final Database<Connection> database = environment.getDatabase();
        final String finalUrlSegment = BaseServlet.getFinalUrlSegment(request);
        if ("most-interfaces".equals(finalUrlSegment)) {
            if (httpMethod == HttpMethod.POST) {
                return _insertMostInterface(request, database);
            }
            if (httpMethod == HttpMethod.GET) {
                final String requestFunctionBlockId = request.getParameter("function_block_id");

                if (Util.isBlank(requestFunctionBlockId)) {
                    return _listAllMostInterfaces(database);
                }

                final long functionBlockId = Util.parseLong(Util.coalesce(requestFunctionBlockId));
                if (functionBlockId < 1) {
                    return _generateErrorJson("Invalid function block id.");
                }
                return _listMostInterfaces(functionBlockId, database);
            }
        }
        else if ("search".equals(finalUrlSegment)){
            if (httpMethod == HttpMethod.GET) {
                final String searchString = Util.coalesce(request.getParameter("name"));
                if (searchString.length() < 1) {
                    return _generateErrorJson("Invalid search string for interface.");
                }
                return _listMostInterfacesMatchingSearchString(searchString, database);
            }
        }
        else if ("function-blocks".equals(finalUrlSegment)) {
            // most-interface/<id>/function-blocks
            final long mostInterfaceId = Util.parseLong(getNthFromLastUrlSegment(request, 1));
            if (mostInterfaceId < 1) {
                return _generateErrorJson("Invalid function block id.");
            }
            if (httpMethod == HttpMethod.GET) {
                return _listFunctionBlocksContainingMostInterface(mostInterfaceId, database);
            }
            if (httpMethod == HttpMethod.POST) {
                return _associateInterfaceWithFunctionBlock(request, mostInterfaceId, database);
            }
        }
        else {
            // not base interface, must have ID
            final long mostInterfaceId = Util.parseLong(finalUrlSegment);
            if (mostInterfaceId < 1) {
                return _generateErrorJson("Invalid interface id.");
            }

            if (httpMethod == HttpMethod.POST) {
                return _updateMostInterface(request, mostInterfaceId, database);
            }
            else if (httpMethod == HttpMethod.DELETE) {
                return _deleteMostInterfaceFromFunctionBlock(request, mostInterfaceId, database);
            }
        }
        return _generateErrorJson("Unimplemented HTTP method in request.");
    }

    protected Json _insertMostInterface(final HttpServletRequest request, final Database database) throws Exception {
        final Json jsonRequest = _getRequestDataAsJson(request);
        final Json response = _generateSuccessJson();
        final Json mostInterfaceJson = jsonRequest.get("mostInterface");
        final String requestFunctionBlockID = jsonRequest.getString("functionBlockId");

        try {
            final MostInterface mostInterface = _populateMostInterfaceFromJson(mostInterfaceJson);
            final DatabaseManager databaseManager = new DatabaseManager(database);

            // If function block ID isn't null, insert interface for function block
            if (!requestFunctionBlockID.equals("null")) {
                final Long functionBlockId = Util.parseLong(requestFunctionBlockID);
                if (functionBlockId < 1) {
                    _logger.error("Unable to parse Function Block ID: " + functionBlockId);
                    return _generateErrorJson("Invalid Function Block ID: " + functionBlockId);
                }
                databaseManager.insertMostInterface(functionBlockId, mostInterface);
            }
            else {
                databaseManager.insertOrphanedMostInterface(mostInterface);
            }

            response.put("mostInterfaceId", mostInterface.getId());
        }
        catch (final Exception exception) {
            _logger.error("Unable to insert Interface.", exception);
            return _generateErrorJson("Unable to insert Interface: " + exception.getMessage());
        }

        return response;
    }

    protected Json _updateMostInterface(final HttpServletRequest httpRequest, final long mostInterfaceId, final Database<Connection> database) throws Exception {
        final Json request = _getRequestDataAsJson(httpRequest);

        final Long functionBlockId = Util.parseLong(request.getString("functionBlockId"));

        final Json mostInterfaceJson = request.get("mostInterface");

        { // Validate Inputs
            if (functionBlockId < 1) {
                _logger.error("Unable to parse Function Block ID: " + functionBlockId);
                return _generateErrorJson("Invalid Function Block ID: " + functionBlockId);
            }
        }

        try {
            MostInterface mostInterface = _populateMostInterfaceFromJson(mostInterfaceJson);
            mostInterface.setId(mostInterfaceId);

            DatabaseManager databaseManager = new DatabaseManager(database);
            databaseManager.updateMostInterface(functionBlockId, mostInterface);
        }
        catch (final Exception exception) {
            final String errorMessage = "Unable to update interface: " + exception.getMessage();
            _logger.error(errorMessage, exception);
            return _generateErrorJson(errorMessage);
        }

        final Json response = new Json(false);
        _setJsonSuccessFields(response);
        return response;
    }

    private Json _associateInterfaceWithFunctionBlock(final HttpServletRequest request, final long mostInterfaceId, final Database<Connection> database) throws IOException {
        final Json jsonRequest = _getRequestDataAsJson(request);
        final Json response = _generateSuccessJson();

        final Long functionBlockId = Util.parseLong(jsonRequest.getString("functionBlockId"));

        { // Validate Inputs
            if (functionBlockId < 1) {
                _logger.error("Unable to parse Function Block ID: " + functionBlockId);
                return _generateErrorJson("Invalid Function Block ID: " + functionBlockId);
            }
        }

        try {
            DatabaseManager databaseManager = new DatabaseManager(database);
            databaseManager.associateMostInterfaceWithFunctionBlock(functionBlockId, mostInterfaceId);
        }
        catch (final Exception exception) {
            _logger.error("Unable to insert Interface.", exception);
            return _generateErrorJson("Unable to insert Interface: " + exception.getMessage());
        }

        return response;
    }

    protected Json _deleteMostInterfaceFromFunctionBlock(final HttpServletRequest request, final long mostInterfaceId, final Database<Connection> database) {
        final String functionBlockIdString = request.getParameter("functionBlockId");
        final Long functionBlockId = Util.parseLong(functionBlockIdString);

        { // Validate Inputs
            if (functionBlockId == null || functionBlockId < 1) {
                return _generateErrorJson(String.format("Invalid function block id: %s", functionBlockIdString));
            }
        }

        try {
            final DatabaseManager databaseManager = new DatabaseManager(database);
            databaseManager.deleteMostInterface(functionBlockId, mostInterfaceId);
        }
        catch (final DatabaseException exception) {
            final String errorMessage = String.format("Unable to delete interface %d from function block %d.", mostInterfaceId, functionBlockId);
            _logger.error(errorMessage, exception);
            return _generateErrorJson(errorMessage);
        }

        final Json response = new Json(false);
        _setJsonSuccessFields(response);
        return response;
    }

    protected Json _listMostInterfaces(final long functionBlockId, final Database<Connection> database) {
        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            final Json response = new Json(false);

            final MostInterfaceInflater mostInterfaceInflater = new MostInterfaceInflater(databaseConnection);
            final List<MostInterface> mostInterfaces = mostInterfaceInflater.inflateMostInterfacesFromFunctionBlockId(functionBlockId);

            final Json mostInterfacesJson = new Json(true);
            for (final MostInterface mostInterface : mostInterfaces) {
                final Json mostInterfaceJson = _toJson(mostInterface);
                mostInterfacesJson.add(mostInterfaceJson);
            }
            response.put("mostInterfaces", mostInterfacesJson);

            _setJsonSuccessFields(response);
            return response;
        }
        catch (final DatabaseException exception) {
            _logger.error("Unable to list interfaces", exception);
            return _generateErrorJson("Unable to list interfaces.");
        }
    }

    protected Json _listAllMostInterfaces(final Database<Connection> database) {
        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            final Json response = new Json(false);

            final MostInterfaceInflater mostInterfaceInflater = new MostInterfaceInflater(databaseConnection);
            final List<MostInterface> mostInterfaces = mostInterfaceInflater.inflateAllMostInterfaces();

            final Json mostInterfacesJson = new Json(true);
            for (final MostInterface mostInterface : mostInterfaces) {
                final Json mostInterfaceJson = _toJson(mostInterface);
                mostInterfacesJson.add(mostInterfaceJson);
            }
            response.put("mostInterfaces", mostInterfacesJson);

            super._setJsonSuccessFields(response);
            return response;
        }
        catch (final DatabaseException exception) {
            _logger.error("Unable to list all interfaces.", exception);
            return super._generateErrorJson("Unable to list all interfaces.");
        }
    }

    protected Json _listMostInterfacesMatchingSearchString(final String searchString, final Database<Connection> database) {
        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            final Json response = new Json(false);

            final MostInterfaceInflater mostInterfaceInflater = new MostInterfaceInflater(databaseConnection);
            final List<MostInterface> mostInterfaces = mostInterfaceInflater.inflateMostInterfacesMatchingSearchString(searchString);

            final Json mostInterfacesJson = new Json(true);
            for (final MostInterface mostInterface : mostInterfaces) {
                final Json mostInterfaceJson = _toJson(mostInterface);
                mostInterfacesJson.add(mostInterfaceJson);
            }
            response.put("mostInterfaces", mostInterfacesJson);

            _setJsonSuccessFields(response);
            return response;
        }
        catch (final DatabaseException exception) {
            _logger.error("Unable to list interfaces from search", exception);
            return _generateErrorJson("Unable to list interfaces from search.");
        }
    }

    protected Json _listFunctionBlocksContainingMostInterface(final long mostInterfaceId, final Database<Connection> database) {
        try {
            final Json response = new Json(false);

            final DatabaseManager databaseManager = new DatabaseManager(database);
            final List<Long> functionBlockIds = databaseManager.listFunctionBlocksContainingMostInterface(mostInterfaceId);

            final Json functionBlockIdsJson = new Json(true);
            for (Long functionBlockId : functionBlockIds) {
                functionBlockIdsJson.add(functionBlockId);
            }
            response.put("functionBlockIds", functionBlockIdsJson);

            _setJsonSuccessFields(response);
            return response;
        }
        catch (final DatabaseException exception) {
            _logger.error("Unable to list function blocks for interface " + mostInterfaceId, exception);
            return _generateErrorJson("Unable to list function blocks.");
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

        final MostInterface mostInterface = new MostInterface();
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
