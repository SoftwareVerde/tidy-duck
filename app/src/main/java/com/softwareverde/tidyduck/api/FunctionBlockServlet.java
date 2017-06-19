package com.softwareverde.tidyduck.api;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.json.Json;
import com.softwareverde.tidyduck.Author;
import com.softwareverde.tidyduck.Company;
import com.softwareverde.tidyduck.DateUtil;
import com.softwareverde.tidyduck.FunctionBlock;
import com.softwareverde.tidyduck.database.DatabaseManager;
import com.softwareverde.tidyduck.database.FunctionBlockInflater;
import com.softwareverde.tidyduck.environment.Environment;
import com.softwareverde.tidyduck.util.Util;
import com.softwareverde.tomcat.servlet.AuthenticatedJsonServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.util.List;

public class FunctionBlockServlet extends AuthenticatedJsonServlet {
    private final Logger _logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected Json handleAuthenticatedRequest(HttpServletRequest request, HttpMethod httpMethod, final long accountId, Environment environment) throws Exception {
        String finalUrlSegment = super.getFinalUrlSegment(request);
        if ("function-block".equals(finalUrlSegment)) {
            if (httpMethod == HttpMethod.POST) {
                return _storeFunctionBlock(request, environment);
            }
            if (httpMethod == HttpMethod.GET) {
                long functionCatalogId = Util.parseLong(Util.coalesce(request.getParameter("function_catalog_id")));
                if (functionCatalogId < 1) {
                    return super._generateErrorJson("Invalid function catalog id.");
                }
                return _listFunctionBlocks(functionCatalogId, environment);
            }
        } else {
            // not base function block, must have ID
            long functionBlockId = Util.parseLong(finalUrlSegment);
            if (functionBlockId < 1) {
                return super._generateErrorJson("Invalid function block id.");
            }

            if (httpMethod == HttpMethod.POST) {
                // return _updateFunctionBlock(request, functionBlockId, environment);
            }
            else if (httpMethod == HttpMethod.DELETE) {
                return _deleteFunctionBlockFromCatalog(request, functionBlockId, environment);
            }
        }
        return super._generateErrorJson("Unimplemented HTTP method in request.");
    }

    protected Json _storeFunctionBlock(HttpServletRequest request, Environment environment) throws Exception {
        Json jsonRequest = super._getRequestDataAsJson(request);
        Json response = new Json(false);

        final Long functionCatalogId = Util.parseLong(jsonRequest.getString("functionCatalogId"));

        { // Validate Inputs
            if (functionCatalogId < 1) {
                _logger.error("Unable to parse Function Catalog ID: " + functionCatalogId);
                return super._generateErrorJson("Invalid Function Catalog ID: " + functionCatalogId);
            }
        }

        final Json functionBlockJson = jsonRequest.get("functionBlock");
        try {
            FunctionBlock functionBlock = _populateFunctionBlockFromJson(functionBlockJson);

            DatabaseManager databaseManager = new DatabaseManager(environment);
            databaseManager.insertFunctionBlock(functionCatalogId, functionBlock);
            response.put("functionBlockId", functionBlock.getId());
        }
        catch (final Exception exception) {
            _logger.error("Unable to store Function Block.", exception);
            return super._generateErrorJson("Unable to store Function Block: " + exception.getMessage());
        }

        return response;
    }

    protected Json _deleteFunctionBlockFromCatalog(HttpServletRequest request, long functionBlockId, Environment environment) {
        final String functionCatalogIdString = request.getParameter("functionCatalogId");
        final Long functionCatalogId = Util.parseLong(functionCatalogIdString);

        { // Validate Inputs
            if (functionCatalogId == null || functionCatalogId < 1) {
                return super._generateErrorJson(String.format("Invalid function catalog id: %s", functionCatalogIdString));
            }
        }

        try {
            final DatabaseManager databaseManager = new DatabaseManager(environment);
            databaseManager.deleteFunctionBlock(functionCatalogId, functionBlockId);
        } catch (final DatabaseException exception) {
            final String errorMessage = String.format("Unable to delete function block %d from function catalog %d.", functionBlockId, functionCatalogId);
            _logger.error(errorMessage, exception);
            return super._generateErrorJson(errorMessage);
        }

        final Json response = new Json(false);
        super._setJsonSuccessFields(response);
        return response;
    }

    protected Json _listFunctionBlocks(long functionCatalogId, Environment environment) {
        try {
            final Json response = new Json(false);

            final DatabaseConnection<Connection> databaseConnection = environment.getNewDatabaseConnection();
            final FunctionBlockInflater functionBlockInflater = new FunctionBlockInflater(databaseConnection);
            final List<FunctionBlock> functionBlocks = functionBlockInflater.inflateFunctionBlocksFromFunctionCatalogId(functionCatalogId);

            final Json blocksJson = new Json(true);
            for (final FunctionBlock functionBlock : functionBlocks) {
                final Json blockJson = new Json(false);
                blockJson.put("id", functionBlock.getId());
                blockJson.put("mostId", functionBlock.getMostId());
                blockJson.put("kind", functionBlock.getKind().getXmlText());
                blockJson.put("name", functionBlock.getName());
                blockJson.put("description", functionBlock.getDescription());
                blockJson.put("lastModifiedDate", DateUtil.dateToDateString(functionBlock.getLastModifiedDate()));
                blockJson.put("releaseVersion", functionBlock.getRelease());
                blockJson.put("authorId", functionBlock.getAuthor().getId());
                blockJson.put("companyId", functionBlock.getCompany().getId());
                blockJson.put("access", functionBlock.getAccess());
                blocksJson.add(blockJson);
            }
            response.put("functionBlocks", blocksJson);

            super._setJsonSuccessFields(response);
            return response;
        }
        catch (final DatabaseException exception) {
            _logger.error("Unable to list function blocks.", exception);
            return super._generateErrorJson("Unable to list function blocks.");
        }
    }

    protected FunctionBlock _populateFunctionBlockFromJson(final Json functionBlockJson) throws Exception {
        final String mostId = functionBlockJson.getString("mostId");
        final String kindString = functionBlockJson.getString("kind");
        final String name = functionBlockJson.getString("name");
        final String description = functionBlockJson.getString("description");
        final String release = functionBlockJson.getString("releaseVersion");
        final Integer authorId = functionBlockJson.getInteger("authorId");
        final Integer companyId = functionBlockJson.getInteger("companyId");
        final String access = functionBlockJson.getString("access");

        FunctionBlock.Kind kind = FunctionBlock.Kind.PROPRIETARY;

        { // Validate Inputs
            if (Util.isBlank(mostId)) {
                throw new Exception("Invalid Most ID");
            }

            if (! Util.isBlank(kindString)) {
                // will throw an exception if invalid
                kind = FunctionBlock.Kind.valueOf(kindString);
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

            if (authorId < 1) {
                throw new Exception("Invalid Account ID: " + authorId);
            }

            if (companyId < 1) {
                throw new Exception("Invalid Company ID: " + companyId);
            }
        }

        final Company company = new Company();
        company.setId(companyId);

        final Author author = new Author();
        author.setId(authorId);

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
}
