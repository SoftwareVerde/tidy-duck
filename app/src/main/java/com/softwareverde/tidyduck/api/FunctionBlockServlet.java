package com.softwareverde.tidyduck.api;

import com.softwareverde.database.Database;
import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.json.Json;
import com.softwareverde.tidyduck.Account;
import com.softwareverde.tidyduck.DateUtil;
import com.softwareverde.tidyduck.database.AccountInflater;
import com.softwareverde.tidyduck.database.DatabaseManager;
import com.softwareverde.tidyduck.database.FunctionBlockInflater;
import com.softwareverde.tidyduck.environment.Environment;
import com.softwareverde.tidyduck.most.Author;
import com.softwareverde.tidyduck.most.Company;
import com.softwareverde.tidyduck.most.FunctionBlock;
import com.softwareverde.tidyduck.util.Util;
import com.softwareverde.tomcat.servlet.AuthenticatedJsonServlet;
import com.softwareverde.tomcat.servlet.BaseServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;

public class FunctionBlockServlet extends AuthenticatedJsonServlet {
    private final Logger _logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected Json handleAuthenticatedRequest(final HttpServletRequest request, final HttpMethod httpMethod, final long accountId, final Environment environment) throws Exception {
        final Database<Connection> database = environment.getDatabase();

        final String finalUrlSegment = BaseServlet.getFinalUrlSegment(request);
        if ("function-block".equals(finalUrlSegment)) {
            if (httpMethod == HttpMethod.POST) {
                return _insertFunctionBlock(request, accountId, database);
            }
            if (httpMethod == HttpMethod.GET) {
                long functionCatalogId = Util.parseLong(Util.coalesce(request.getParameter("function_catalog_id")));
                if (functionCatalogId < 1) {
                    return super._generateErrorJson("Invalid function catalog id.");
                }
                return _listFunctionBlocks(functionCatalogId, database);
            }
        }
        else if ("function-catalogs".equals(finalUrlSegment)) {
            // function-block/<id>/function-catalogs
            final long functionBlockId = Util.parseLong(getNthFromLastUrlSegment(request, 1));
            if (functionBlockId < 1) {
                return super._generateErrorJson("Invalid function block id.");
            }
            if (httpMethod == HttpMethod.GET) {
                return _listFunctionCatalogsForFunctionBlock(functionBlockId, database);
            }
            else if (httpMethod == HttpMethod.POST) {
                return _associateFunctionBlockWithFunctionCatalog(request, functionBlockId, database);
            }
        }
        else if ("search".equals(finalUrlSegment)){
            if (httpMethod == HttpMethod.GET) {
                final String searchString = Util.coalesce(request.getParameter("name"));
                if (searchString.length() < 1) {
                    return super._generateErrorJson("Invalid search string for function block.");
                }
                return _listFunctionBlocksMatchingSearchString(searchString, database);
            }
        }
        else if ("function-blocks".equals(finalUrlSegment)) {
            if (httpMethod == HttpMethod.POST) {
                return _insertOrphanedFunctionBlock(request, accountId, database);
            }
            if (httpMethod == HttpMethod.GET) {
                return _listAllFunctionBlocks(database);
            }
        }
        else {
            // not base function block, must have ID
            final long functionBlockId = Util.parseLong(finalUrlSegment);
            if (functionBlockId < 1) {
                return super._generateErrorJson("Invalid function block id.");
            }

            if (httpMethod == HttpMethod.POST) {
                return _updateFunctionBlock(request, functionBlockId, accountId, database);
            }
            else if (httpMethod == HttpMethod.DELETE) {
                return _deleteFunctionBlockFromCatalog(request, functionBlockId, database);
            }
        }
        return super._generateErrorJson("Unimplemented HTTP method in request.");
    }

    protected Json _insertFunctionBlock(final HttpServletRequest request, final long accountId, final Database<Connection> database) throws Exception {
        final Json jsonRequest = _getRequestDataAsJson(request);
        final Json response = _generateSuccessJson();

        final Long functionCatalogId = Util.parseLong(jsonRequest.getString("functionCatalogId"));

        { // Validate Inputs
            if (functionCatalogId < 1) {
                _logger.error("Unable to parse Function Catalog ID: " + functionCatalogId);
                return super._generateErrorJson("Invalid Function Catalog ID: " + functionCatalogId);
            }
        }

        final Json functionBlockJson = jsonRequest.get("functionBlock");
        try {
            FunctionBlock functionBlock = _populateFunctionBlockFromJson(functionBlockJson, accountId, database);

            DatabaseManager databaseManager = new DatabaseManager(database);
            databaseManager.insertFunctionBlock(functionCatalogId, functionBlock);
            response.put("functionBlockId", functionBlock.getId());
        }
        catch (final Exception exception) {
            _logger.error("Unable to insert Function Block.", exception);
            return super._generateErrorJson("Unable to insert Function Block: " + exception.getMessage());
        }

        return response;
    }

    protected Json _insertOrphanedFunctionBlock(final HttpServletRequest request, final long accountId, final Database<Connection> database) throws Exception {
        final Json jsonRequest = _getRequestDataAsJson(request);
        final Json response = _generateSuccessJson();
        final Json functionBlockJson = jsonRequest.get("functionBlock");

        try {
            FunctionBlock functionBlock = _populateFunctionBlockFromJson(functionBlockJson, accountId, database);

            DatabaseManager databaseManager = new DatabaseManager(database);
            databaseManager.insertOrphanedFunctionBlock(functionBlock);
            response.put("functionBlockId", functionBlock.getId());
        }
        catch (final Exception exception) {
            _logger.error("Unable to insert orphaned Function Block.", exception);
            return super._generateErrorJson("Unable to insert orphaned Function Block: " + exception.getMessage());
        }

        return response;
    }

    protected Json _updateFunctionBlock(final HttpServletRequest httpRequest, final long functionBlockId, final long accountId, final Database<Connection> database) throws Exception {
        final Json request = _getRequestDataAsJson(httpRequest);

        final Long functionCatalogId = Util.parseLong(request.getString("functionCatalogId"));

        final Json functionBlockJson = request.get("functionBlock");

        { // Validate Inputs
            if (functionCatalogId < 1) {
                _logger.error("Unable to parse Function Catalog ID: " + functionCatalogId);
                return super._generateErrorJson("Invalid Function Catalog ID: " + functionCatalogId);
            }
        }

        try {
            final FunctionBlock functionBlock = _populateFunctionBlockFromJson(functionBlockJson, accountId, database);
            functionBlock.setId(functionBlockId);

            final DatabaseManager databaseManager = new DatabaseManager(database);
            databaseManager.updateFunctionBlock(functionCatalogId, functionBlock);
        }
        catch (final Exception exception) {
            final String errorMessage = "Unable to update function block: " + exception.getMessage();
            _logger.error(errorMessage, exception);
            return super._generateErrorJson(errorMessage);
        }

        final Json response = new Json(false);
        super._setJsonSuccessFields(response);
        return response;
    }

    private Json _associateFunctionBlockWithFunctionCatalog(final HttpServletRequest request, final long functionBlockId, final Database<Connection> database) throws IOException {
        final Json jsonRequest = _getRequestDataAsJson(request);
        final Json response = _generateSuccessJson();

        final Long functionCatalogId = Util.parseLong(jsonRequest.getString("functionCatalogId"));

        { // Validate Inputs
            if (functionCatalogId < 1) {
                _logger.error("Unable to parse Function Catalog ID: " + functionCatalogId);
                return super._generateErrorJson("Invalid Function Catalog ID: " + functionCatalogId);
            }
        }

        try {
            DatabaseManager databaseManager = new DatabaseManager(database);
            databaseManager.associateFunctionBlockWithFunctionCatalog(functionCatalogId, functionBlockId);
        }
        catch (final Exception exception) {
            _logger.error("Unable to insert Interface.", exception);
            return super._generateErrorJson("Unable to insert Interface: " + exception.getMessage());
        }

        return response;
    }

    protected Json _deleteFunctionBlockFromCatalog(final HttpServletRequest request, final long functionBlockId, final Database<Connection> database) {
        final String functionCatalogIdString = request.getParameter("functionCatalogId");
        final Long functionCatalogId = Util.parseLong(functionCatalogIdString);

        { // Validate Inputs
            if (functionCatalogId == null || functionCatalogId < 1) {
                return super._generateErrorJson(String.format("Invalid function catalog id: %s", functionCatalogIdString));
            }
        }

        try {
            final DatabaseManager databaseManager = new DatabaseManager(database);
            databaseManager.deleteFunctionBlock(functionCatalogId, functionBlockId);
        }
        catch (final DatabaseException exception) {
            final String errorMessage = String.format("Unable to delete function block %d from function catalog %d.", functionBlockId, functionCatalogId);
            _logger.error(errorMessage, exception);
            return super._generateErrorJson(errorMessage);
        }

        final Json response = new Json(false);
        super._setJsonSuccessFields(response);
        return response;
    }

    protected Json _listFunctionBlocks(final long functionCatalogId, final Database<Connection> database) {
        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            final Json response = new Json(false);

            final FunctionBlockInflater functionBlockInflater = new FunctionBlockInflater(databaseConnection);
            final List<FunctionBlock> functionBlocks = functionBlockInflater.inflateFunctionBlocksFromFunctionCatalogId(functionCatalogId);

            final Json functionBlocksJson = new Json(true);
            for (final FunctionBlock functionBlock : functionBlocks) {
                final Json functionBlockJson = _toJson(functionBlock);
                functionBlocksJson.add(functionBlockJson);
            }
            response.put("functionBlocks", functionBlocksJson);

            super._setJsonSuccessFields(response);
            return response;
        }
        catch (final DatabaseException exception) {
            _logger.error("Unable to list function blocks.", exception);
            return super._generateErrorJson("Unable to list function blocks.");
        }
    }

    protected Json _listAllFunctionBlocks(final Database<Connection> database) {
        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            final Json response = new Json(false);

            final FunctionBlockInflater functionBlockInflater = new FunctionBlockInflater(databaseConnection);
            final List<FunctionBlock> functionBlocks = functionBlockInflater.inflateAllFunctionBlocks();

            final Json functionBlocksJson = new Json(true);
            for (final FunctionBlock functionBlock : functionBlocks) {
                final Json functionBlockJson = _toJson(functionBlock);
                functionBlocksJson.add(functionBlockJson);
            }
            response.put("functionBlocks", functionBlocksJson);

            super._setJsonSuccessFields(response);
            return response;
        }
        catch (final DatabaseException exception) {
            _logger.error("Unable to list function blocks.", exception);
            return super._generateErrorJson("Unable to list function blocks.");
        }
    }

    private Json _listFunctionBlocksMatchingSearchString(final String searchString, final Database<Connection> database) {
        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            final Json response = new Json(false);

            final FunctionBlockInflater functionBlockInflater = new FunctionBlockInflater(databaseConnection);
            final List<FunctionBlock> functionBlocks = functionBlockInflater.inflateFunctionBlocksMatchingSearchString(searchString);

            final Json functionBlocksJson = new Json(true);
            for (final FunctionBlock functionBlock : functionBlocks) {
                final Json functionBlockJson = _toJson(functionBlock);
                functionBlocksJson.add(functionBlockJson);
            }
            response.put("functionBlocks", functionBlocksJson);

            super._setJsonSuccessFields(response);
            return response;
        }
        catch (final DatabaseException exception) {
            _logger.error("Unable to list function blocks from search", exception);
            return super._generateErrorJson("Unable to list function blocks from search.");
        }
    }

    protected Json _listFunctionCatalogsForFunctionBlock(final long functionBlockId, final Database<Connection> database) {
        try {
            final Json response = new Json(false);

            DatabaseManager databaseManager = new DatabaseManager(database);
            final List<Long> functionCatalogIds = databaseManager.listFunctionCatalogsContainingFunctionBlock(functionBlockId);

            Json functionCatalogIdsJson = new Json(true);
            for (Long functionCatalogId : functionCatalogIds) {
                functionCatalogIdsJson.add(functionCatalogId);
            }
            response.put("functionCatalogIds", functionCatalogIdsJson);

            super._setJsonSuccessFields(response);
            return response;
        }
        catch (final DatabaseException exception) {
            _logger.error("Unable to list function catalogs for function block " + functionBlockId, exception);
            return super._generateErrorJson("Unable to list function catalogs.");
        }
    }

    protected FunctionBlock _populateFunctionBlockFromJson(final Json functionBlockJson, final long accountId, final Database database) throws Exception {
        final String mostId = functionBlockJson.getString("mostId");
        final String kind = functionBlockJson.getString("kind");
        final String name = functionBlockJson.getString("name");
        final String description = functionBlockJson.getString("description");
        final String release = functionBlockJson.getString("releaseVersion");
        final Long authorId = functionBlockJson.getLong("authorId");
        final Long companyId = functionBlockJson.getLong("companyId");
        final String access = functionBlockJson.getString("access");

        { // Validate Inputs
            if (Util.isBlank(mostId)) {
                throw new Exception("Invalid Most ID");
            }

            if (Util.isBlank(kind)) {
                throw new Exception("Invalid Kind value: "+ kind);
            }

            if (Util.isBlank(name)) {
                throw new Exception("Name field is required.");
            }

            if (Util.isBlank(description)) {
                throw new Exception("Description field is required.");
            }

            if (Util.isBlank(release)) {
                throw new Exception("Release field is required.");
            }

            if (Util.isBlank(access)) {
                throw new Exception("Access field is required.");
            }
        }

        final Company company;
        final Author author;

        if (authorId >= 1) {
            // use supplied author/account ID
            company = new Company();
            company.setId(companyId);
            author = new Author();
            author.setId(authorId);
        }
        else {
            // use users's account ID
            try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
                AccountInflater accountInflater = new AccountInflater(databaseConnection);

                Account account = accountInflater.inflateAccount(accountId);

                company = account.getCompany();
                author = account.toAuthor();
            }
        }

        FunctionBlock functionBlock = new FunctionBlock();
        functionBlock.setMostId(mostId);
        functionBlock.setKind(kind);
        functionBlock.setName(name);
        functionBlock.setRelease(release);
        functionBlock.setDescription(description);
        functionBlock.setAuthor(author);
        functionBlock.setCompany(company);
        functionBlock.setAccess(access);

        return functionBlock;
    }

    private Json _toJson(final FunctionBlock functionBlock) {
        final Json blockJson = new Json(false);
        blockJson.put("id", functionBlock.getId());
        blockJson.put("mostId", functionBlock.getMostId());
        blockJson.put("kind", functionBlock.getKind());
        blockJson.put("name", functionBlock.getName());
        blockJson.put("description", functionBlock.getDescription());
        blockJson.put("lastModifiedDate", DateUtil.dateToDateString(functionBlock.getLastModifiedDate()));
        blockJson.put("releaseVersion", functionBlock.getRelease());
        blockJson.put("authorId", functionBlock.getAuthor().getId());
        blockJson.put("authorName", functionBlock.getAuthor().getName());
        blockJson.put("companyId", functionBlock.getCompany().getId());
        blockJson.put("companyName", functionBlock.getCompany().getName());
        blockJson.put("access", functionBlock.getAccess());
        return blockJson;
    }
}
