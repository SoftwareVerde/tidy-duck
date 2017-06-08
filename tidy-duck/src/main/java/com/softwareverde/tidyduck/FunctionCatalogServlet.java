package com.softwareverde.tidyduck;

import com.softwareverde.Environment;
import com.softwareverde.json.Json;
import com.softwareverde.servlet.BaseServlet;
import com.softwareverde.servlet.JsonServlet;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class FunctionCatalogServlet extends JsonServlet {

    @Override
    protected Json handleRequest(HttpServletRequest request, BaseServlet.HttpMethod httpMethod, Environment environment) throws Exception {
        if (httpMethod == BaseServlet.HttpMethod.POST) {
            return addFunctionCatalog(request);
        }
        return null;
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
