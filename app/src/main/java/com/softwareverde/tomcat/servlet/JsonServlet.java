package com.softwareverde.tomcat.servlet;

import com.softwareverde.json.Json;
import com.softwareverde.tidyduck.environment.Environment;
import com.softwareverde.tidyduck.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public abstract class JsonServlet extends BaseServlet {

    private Logger _logger = LoggerFactory.getLogger(getClass());

    private static final String JSON_SUCCESS_FIELD = "wasSuccess";
    private static final String JSON_ERROR_FIELD = "errorMessage";

    protected abstract Json handleRequest(HttpServletRequest request, HttpMethod httpMethod, Environment environment) throws Exception;

    @Override
    protected final void handleRequest(HttpServletRequest req, HttpServletResponse resp, HttpMethod httpMethod, Environment environment) throws IOException {
        long startTime = System.currentTimeMillis();

        resp.setContentType("application/json");

        Json json;
        try {
            json = handleRequest(req, httpMethod, environment);

            // default to empty json object
            if (json == null) {
                json = _generateErrorJson("Unable to determine response");
            }
        }
        catch (final Exception e) {
            String msg = "Unable to handle request";
            _logger.error(msg, e);
            json = _generateErrorJson(msg + ": " + e.getMessage());
        }
        PrintWriter writer = resp.getWriter();
        writer.append(json.toString());
        long endTime = System.currentTimeMillis();
        _logger.info(httpMethod.name() + " request to " + req.getRequestURI() + " took " + (endTime-startTime) + "ms.");
    }

    protected static Json _getRequestDataAsJson(HttpServletRequest request) throws IOException {
        String messageBody = Util.getInputStreamAsString(request.getInputStream());
        return Json.parse(messageBody);
    }

    protected void _setJsonSuccessFields(Json json) {
        json.put(JSON_SUCCESS_FIELD, true);
        json.put(JSON_ERROR_FIELD, null);
    }

    protected Json _generateSuccessJson() {
        Json json = new Json();
        json.put(JSON_SUCCESS_FIELD, true);
        json.put(JSON_ERROR_FIELD, null);
        return json;
    }

    protected Json _generateErrorJson(String errorMessage) {
        Json json = new Json();
        json.put(JSON_SUCCESS_FIELD, false);
        json.put(JSON_ERROR_FIELD, errorMessage);
        return json;
    }

}
