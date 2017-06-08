package com.softwareverde.tidyduck;

import com.softwareverde.json.Json;
import com.softwareverde.json.Jsonable;
import com.softwareverde.servlet.JsonServlet;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class FunctionCatalogServlet extends JsonServlet {

    @Override
    protected Json handleRequest(HttpServletRequest request, JsonServlet.HttpMethod httpMethod) throws IOException {
        if (httpMethod == HttpMethod.POST) {
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
