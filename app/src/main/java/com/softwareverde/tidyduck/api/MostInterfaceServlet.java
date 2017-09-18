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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.net.URLDecoder;

public class MostInterfaceServlet extends AuthenticatedJsonServlet {
    private final Logger _logger = LoggerFactory.getLogger(this.getClass());

    public MostInterfaceServlet() {
        super.defineEndpoint("most-interfaces", HttpMethod.GET, new AuthenticatedJsonRoute() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Long accountId, final Environment environment) throws Exception {
                final String requestFunctionBlockId = request.getParameter("function_block_id");

                if (Util.isBlank(requestFunctionBlockId)) {
                    return _listAllMostInterfaces(environment.getDatabase());
                }

                final long functionBlockId = Util.parseLong(Util.coalesce(requestFunctionBlockId));
                if (functionBlockId < 1) {
                    return _generateErrorJson("Invalid function block id.");
                }
                return _listMostInterfaces(functionBlockId, environment.getDatabase());
            }
        });

        super.defineEndpoint("most-interfaces", HttpMethod.POST, new AuthenticatedJsonRoute() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Long accountId, final Environment environment) throws Exception {
                return _insertMostInterface(request, environment.getDatabase());
            }
        });

        super.defineEndpoint("most-interfaces/search/<name>", HttpMethod.GET, new AuthenticatedJsonRoute() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Long accountId, final Environment environment) throws Exception {
                final String searchString = Util.coalesce(parameters.get("name"));
                if (searchString.length() < 2) {
                    return _generateErrorJson("Invalid search string for interface.");
                }
                return _listMostInterfacesMatchingSearchString(searchString, environment.getDatabase());
            }
        });

        super.defineEndpoint("most-interfaces/<mostInterfaceId>", HttpMethod.GET, new AuthenticatedJsonRoute() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Long accountId, final Environment environment) throws Exception {
                final Long mostInterfaceId = Util.parseLong(parameters.get("mostInterfaceId"));
                if (mostInterfaceId < 1) {
                    return _generateErrorJson("Invalid interface id.");
                }
                return _getMostInterface(mostInterfaceId, environment.getDatabase());
            }
        });

        super.defineEndpoint("most-interfaces/<mostInterfaceId>", HttpMethod.POST, new AuthenticatedJsonRoute() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Long accountId, final Environment environment) throws Exception {
                final Long mostInterfaceId = Util.parseLong(parameters.get("mostInterfaceId"));
                if (mostInterfaceId < 1) {
                    return _generateErrorJson("Invalid interface id.");
                }
                return _updateMostInterface(request, mostInterfaceId, environment.getDatabase());
            }
        });

        super.defineEndpoint("most-interfaces/<mostInterfaceId>", HttpMethod.DELETE, new AuthenticatedJsonRoute() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Long accountId, final Environment environment) throws Exception {
                final Long mostInterfaceId = Util.parseLong(parameters.get("mostInterfaceId"));
                if (mostInterfaceId < 1) {
                    return _generateErrorJson("Invalid interface id.");
                }
                return _deleteMostInterfaceFromFunctionBlock(request, mostInterfaceId, environment.getDatabase());
            }
        });

        super.defineEndpoint("most-interfaces/<mostInterfaceId>/function-blocks", HttpMethod.GET, new AuthenticatedJsonRoute() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Long accountId, final Environment environment) throws Exception {
                final Long mostInterfaceId = Util.parseLong(parameters.get("mostInterfaceId"));
                if (mostInterfaceId < 1) {
                    return _generateErrorJson("Invalid interface id.");
                }
                return _listFunctionBlocksContainingMostInterface(mostInterfaceId, environment.getDatabase());
            }
        });

        super.defineEndpoint("most-interfaces/<mostInterfaceId>/function-blocks", HttpMethod.POST, new AuthenticatedJsonRoute() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Long accountId, final Environment environment) throws Exception {
                final Long mostInterfaceId = Util.parseLong(parameters.get("mostInterfaceId"));
                if (mostInterfaceId < 1) {
                    return _generateErrorJson("Invalid interface id.");
                }
                return _associateInterfaceWithFunctionBlock(request, mostInterfaceId, environment.getDatabase());
            }
        });

        super.defineEndpoint("most-interfaces/<mostInterfaceId>/submit-for-review", HttpMethod.POST, new AuthenticatedJsonRoute() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Long accountId, final Environment environment) throws Exception {
                final Long mostInterfaceId = Util.parseLong(parameters.get("mostInterfaceId"));
                if (mostInterfaceId < 1) {
                    return _generateErrorJson("Invalid interface id.");
                }
                return _submitMostInterfaceForReview(mostInterfaceId, accountId, environment.getDatabase());
            }
        });
    }

    private Json _getMostInterface(final Long mostInterfaceId, final Database<Connection> database) {
        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            final MostInterfaceInflater mostInterfaceInflater = new MostInterfaceInflater(databaseConnection);
            final MostInterface mostInterface = mostInterfaceInflater.inflateMostInterface(mostInterfaceId);

            final Json response = new Json(false);

            response.put("mostInterface", _toJson(mostInterface));

            _setJsonSuccessFields(response);
            return response;
        }
        catch (final DatabaseException exception) {
            _logger.error("Unable to list interfaces", exception);
            return _generateErrorJson("Unable to list interfaces.");
        }
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
            if (! Util.isBlank(requestFunctionBlockID)) {
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
        final Json response = new Json(false);
        final String requestFunctionBlockId = request.getString("functionBlockId");


        final Json mostInterfaceJson = request.get("mostInterface");

        try {
            final MostInterface mostInterface = _populateMostInterfaceFromJson(mostInterfaceJson);
            mostInterface.setId(mostInterfaceId);

            final DatabaseManager databaseManager = new DatabaseManager(database);

            if (! Util.isBlank(requestFunctionBlockId)) {
                // Validate Inputs
                final Long functionBlockId = Util.parseLong(requestFunctionBlockId);
                if (functionBlockId < 1) {
                    _logger.error("Unable to parse Function Block ID: " + functionBlockId);
                    return _generateErrorJson("Invalid Function Block ID: " + functionBlockId);
                }
                databaseManager.updateMostInterface(functionBlockId, mostInterface);
            }
            else {
                databaseManager.updateMostInterface(0, mostInterface);
            }
            response.put("mostInterfaceId", mostInterface.getId());
        }
        catch (final Exception exception) {
            final String errorMessage = "Unable to update interface: " + exception.getMessage();
            _logger.error(errorMessage, exception);
            return _generateErrorJson(errorMessage);
        }

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

        try {
            final DatabaseManager databaseManager = new DatabaseManager(database);

            // Validate inputs. If null, send blockId of 0, which will disassociate interface from all fblocks.
            if (Util.isBlank(functionBlockIdString)) {
                databaseManager.deleteMostInterface(0, mostInterfaceId);
            }
            else {
                if (functionBlockId < 1) {
                    return _generateErrorJson(String.format("Invalid function block id: %s", functionBlockIdString));
                }
                databaseManager.deleteMostInterface(functionBlockId, mostInterfaceId);
            }
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
            final Map<Long, List<MostInterface>> mostInterfaces = mostInterfaceInflater.inflateMostInterfacesGroupedByBaseVersionId();

            final Json mostInterfacesJson = new Json(true);
            for (final Long baseVersionId : mostInterfaces.keySet()) {
                final Json versionSeriesJson = new Json();
                versionSeriesJson.put("baseVersionId", baseVersionId);

                final Json versionsJson = new Json();
                for (final MostInterface mostInterface : mostInterfaces.get(baseVersionId)) {
                    final Json mostInterfaceJson = _toJson(mostInterface);
                    versionsJson.add(mostInterfaceJson);
                }
                versionSeriesJson.put("versions", versionsJson);
                mostInterfacesJson.add(versionSeriesJson);
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
        final String decodedSearchString;
        try{ decodedSearchString = URLDecoder.decode(searchString, "UTF-8"); }
        catch (UnsupportedEncodingException e) {
            _logger.error("Unable to list interfaces from search", e);
            return _generateErrorJson("Unable to list interfaces from search.");
        }

        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            final Json response = new Json(false);

            final MostInterfaceInflater mostInterfaceInflater = new MostInterfaceInflater(databaseConnection);
            final Map<Long, List<MostInterface>> mostInterfaces = mostInterfaceInflater.inflateMostInterfacesMatchingSearchString(decodedSearchString);

            final Json mostInterfacesJson = new Json(true);
            for (final Long baseVersionId : mostInterfaces.keySet()) {
                final Json versionSeriesJson = new Json();
                versionSeriesJson.put("baseVersionId", baseVersionId);

                final Json versionsJson = new Json();
                for (final MostInterface mostInterface : mostInterfaces.get(baseVersionId)) {
                    final Json mostInterfaceJson = _toJson(mostInterface);
                    versionsJson.add(mostInterfaceJson);
                }
                versionSeriesJson.put("versions", versionsJson);
                mostInterfacesJson.add(versionSeriesJson);
            }
            response.put("mostInterfaces", mostInterfacesJson);

            super._setJsonSuccessFields(response);
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

    protected Json _submitMostInterfaceForReview(final Long mostInterfaceId, final Long accountId, final Database<Connection> database) {
        try {
            final Json response = new Json(false);

            final DatabaseManager databaseManager = new DatabaseManager(database);
            databaseManager.submitMostInterfaceForReview(mostInterfaceId, accountId);

            super._setJsonSuccessFields(response);
            return response;
        } catch (DatabaseException e) {
            String errorMessage = "Unable to submit interface for review.";
            _logger.error(errorMessage, e);
            return super._generateErrorJson(errorMessage);
        }
    }

    protected MostInterface _populateMostInterfaceFromJson(final Json mostInterfaceJson) throws Exception {
        final String mostId = mostInterfaceJson.getString("mostId");
        final String name = mostInterfaceJson.getString("name");
        final String description = mostInterfaceJson.getString("description");
        final String releaseVersion = mostInterfaceJson.getString("releaseVersion");

        { // Validate Inputs
            if (Util.isBlank(mostId)) {
                throw new Exception("Invalid Most ID");
            }
            if (!Util.isLong(mostId)) {
                throw new Exception("Interface MOST ID must be an integer.");
            }

            if (Util.isBlank(name)) {
                throw new Exception("Name field is required.");
            }
            /*
            if (Util.isBlank(description)) {
                throw new Exception("Description field is required.");
            }
            */
            if (Util.isBlank(releaseVersion)) {
                throw new Exception("Version field is required.");
            }
        }

        final MostInterface mostInterface = new MostInterface();
        mostInterface.setMostId(mostId);
        mostInterface.setName(name);
        mostInterface.setVersion(releaseVersion);
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
        mostInterfaceJson.put("releaseVersion", mostInterface.getVersion());
        mostInterfaceJson.put("isReleased", mostInterface.isReleased());
        mostInterfaceJson.put("isApproved", mostInterface.isApproved());
        mostInterfaceJson.put("baseVersionId", mostInterface.getBaseVersionId());
        mostInterfaceJson.put("priorVersionId", mostInterface.getPriorVersionId());
        return mostInterfaceJson;
    }
}
