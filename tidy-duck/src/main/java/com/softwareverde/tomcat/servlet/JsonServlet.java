package com.softwareverde.tomcat.servlet;

import com.softwareverde.tidyduck.environment.Environment;
import com.softwareverde.json.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public abstract class JsonServlet extends BaseServlet {

    private Logger _logger = LoggerFactory.getLogger(getClass());

    private static final int BUFFER_SIZE = 8192;
    private static final String JSON_SUCCESS_FIELD = "wasSuccess";
    private static final String JSON_ERROR_FIELD = "errorMessage";

    protected abstract Json handleRequest(HttpServletRequest request, HttpMethod httpMethod, Environment environment) throws Exception;

    @Override
    protected final void handleRequest(HttpServletRequest req, HttpServletResponse resp, HttpMethod httpMethod, Environment environment) throws IOException {
        long startTime = System.currentTimeMillis();
        Json json = null;
        try {
            json = handleRequest(req, httpMethod, environment);
            // default to empty json object
            if (json == null) {
                json = generateErrorJson("Unable to determine response");
            }
        }
        catch (final Exception e) {
            String msg = "Unable to handle request.";
            _logger.error(msg, e);
            json = generateErrorJson(msg + ": " + e.getMessage());
        }
        PrintWriter writer = resp.getWriter();
        writer.append(json.toString());
        long endTime = System.currentTimeMillis();
        _logger.info("Request took " + (endTime-startTime) + "ms.");
    }

    protected static Json getRequestDataAsJson(HttpServletRequest request) throws IOException {
        BufferedReader reader = request.getReader();
        StringBuilder builder = new StringBuilder();
        char[] buffer = new char[BUFFER_SIZE];
        int length;
        while ((length = reader.read(buffer, 0, BUFFER_SIZE)) > 0) {
            builder.append(buffer, 0, length);
        }
        return Json.parse(builder.toString());
    }

    protected void setJsonSuccessFields(Json json) {
        json.put(JSON_SUCCESS_FIELD, true);
        json.put(JSON_ERROR_FIELD, null);
    }

    protected Json generateErrorJson(String errorMessage) {
        Json json = new Json();
        json.put(JSON_SUCCESS_FIELD, false);
        json.put(JSON_ERROR_FIELD, errorMessage);
        return json;
    }

}
