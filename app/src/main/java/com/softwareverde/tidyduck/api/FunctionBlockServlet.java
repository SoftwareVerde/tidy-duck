package com.softwareverde.tidyduck.api;

import com.softwareverde.json.Json;
import com.softwareverde.tidyduck.environment.Environment;
import com.softwareverde.tomcat.servlet.JsonServlet;

import javax.servlet.http.HttpServletRequest;

public class FunctionBlockServlet extends JsonServlet {

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

    private Json storeFunctionBlock(HttpServletRequest request, Environment environment) {
        Json response = new Json();

        return response;
    }

}
