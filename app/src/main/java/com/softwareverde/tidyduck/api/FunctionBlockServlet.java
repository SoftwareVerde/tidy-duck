package com.softwareverde.tidyduck.api;

import com.softwareverde.json.Json;
import com.softwareverde.tidyduck.Account;
import com.softwareverde.tidyduck.Company;
import com.softwareverde.tidyduck.FunctionBlock;
import com.softwareverde.tidyduck.database.DatabaseManager;
import com.softwareverde.tidyduck.environment.Environment;
import com.softwareverde.tidyduck.util.Util;
import com.softwareverde.tomcat.servlet.JsonServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

public class FunctionBlockServlet extends JsonServlet {
    private final Logger _logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected Json handleRequest(HttpServletRequest request, HttpMethod httpMethod, Environment environment) throws Exception {
        String finalUrlSegment = super.getFinalUrlSegment(request);
        if ("function-block".equals(finalUrlSegment)) {
            if (httpMethod == HttpMethod.POST) {
                return storeFunctionBlock(request, environment);
            }
//            if (httpMethod == HttpMethod.GET) {
//                long functionCatalogId = Util.parseLong(Util.coalesce(request.getParameter("function_catalog_id")));
//                if (functionCatalogId < 1) {
//                    return super.generateErrorJson("Invalid function catalog id.");
//                }
//                return listFunctionBlocks(functionCatalogId, environment);
//            }
//        } else {
//            // not base function block, must have ID
//            long functionBlockId = Util.parseLong(finalUrlSegment);
//            if (functionBlockId < 1) {
//                return super.generateErrorJson("Invalid function block id.");
//            }
//            if (httpMethod == HttpMethod.POST) {
//                return updateFunctionBlock(request, functionBlockId, environment);
//            }
//            if (httpMethod == HttpMethod.DELETE) {
//                return deleteFunctionBlockFromCatalog(request, functionBlockId, environment);
//            }
        }
        return super.generateErrorJson("Unimplemented HTTP method in request.");
    }

    private Json storeFunctionBlock(HttpServletRequest request, Environment environment) throws Exception {
        Json jsonRequest = JsonServlet.getRequestDataAsJson(request);
        Json response = new Json();


        final Long functionCatalogId = Util.parseLong(jsonRequest.getString("functionCatalogId"));

        { // Validate Inputs
            if (functionCatalogId < 1) {
                _logger.error("Unable to parse Function Catalog ID: " + functionCatalogId);
                return super.generateErrorJson("Invalid Function Catalog ID: " + functionCatalogId);
            }
        }

        final Json functionBlockJson = jsonRequest.get("functionBlock");
        try {
            FunctionBlock functionBlock = populateFunctionBlockFromJson(functionBlockJson);

            DatabaseManager databaseManager = new DatabaseManager(environment);
            databaseManager.insertFunctionBlock(functionCatalogId, functionBlock);
            response.put("functionBlockId", functionBlock.getId());
        }
        catch (final Exception exception) {
            _logger.error("Unable to store Function Block.", exception);
            return super.generateErrorJson("Unable to store Function Block: " + exception.getMessage());
        }

        return response;
    }


    protected FunctionBlock populateFunctionBlockFromJson(Json functionBlockJson) throws Exception {
        final String kindString = functionBlockJson.getString("kind");
        final String name = functionBlockJson.getString("name");
        final String description = functionBlockJson.getString("description");
        final String release = functionBlockJson.getString("releaseVersion");
        final Integer authorId = functionBlockJson.getInteger("authorId");
        final Integer companyId = functionBlockJson.getInteger("companyId");

        FunctionBlock.Kind kind = FunctionBlock.Kind.PROPRIETARY;

        { // Validate Inputs
            if (Util.isNotBlank(kindString)) {
                // will throw an exception if invalid
                kind = FunctionBlock.Kind.valueOf(kindString);
            }

            if (Util.isBlank(name)) {
                throw new Exception("Invalid Name: " + name);
            }

            if (Util.isBlank(description)) {
                throw new Exception("Invalid description: " + description);
            }

            if (Util.isBlank(release)) {
                throw new Exception("Invalid Release: " + release);
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

        final Account account = new Account();
        account.setId(authorId);

        FunctionBlock functionBlock = new FunctionBlock();
        functionBlock.setKind(kind);
        functionBlock.setName(name);
        functionBlock.setRelease(release);
        functionBlock.setDescription(description);
        functionBlock.setAccount(account);
        functionBlock.setCompany(company);

        return functionBlock;
    }
}
