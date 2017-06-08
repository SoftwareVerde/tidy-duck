package com.softwareverde.tidyduck.api;

import com.softwareverde.tidyduck.environment.Environment;
import com.softwareverde.json.Json;
import com.softwareverde.tomcat.servlet.BaseServlet;
import com.softwareverde.tomcat.servlet.JsonServlet;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class FunctionCatalogServlet extends JsonServlet {

    @Override
    protected Json handleRequest(final HttpServletRequest request, final BaseServlet.HttpMethod httpMethod, final Environment environment) throws Exception {
        if (httpMethod == BaseServlet.HttpMethod.POST) {
            return addFunctionCatalog(request);
        }
        return new Json(false);
    }

    private Json addFunctionCatalog(HttpServletRequest httpRequest) throws IOException {
        Json request = super.getRequestDataAsJson(httpRequest);

        // TODO: add function catalog to DB

        Json response = new Json();
        response.put("wasSuccess", "true");
        response.put("errorMessage", null);
        return response;
    }
}
