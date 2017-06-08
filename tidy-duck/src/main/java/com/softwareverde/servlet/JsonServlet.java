package com.softwareverde.servlet;

import com.softwareverde.Environment;
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

    protected abstract Json handleRequest(HttpServletRequest request, HttpMethod httpMethod, Environment environment) throws Exception;

    @Override
    protected final void handleRequest(HttpServletRequest req, HttpServletResponse resp, HttpMethod httpMethod, Environment environment) throws IOException {
        Json json = null;
        try {
            json = handleRequest(req, httpMethod, environment);
            // default to empty json object
            if (json == null) {
                json = getErrorJson("Unable to determine response");
            }
        } catch (Exception e) {
            String msg = "Unable to handle request.";
            _logger.error(msg, e);
            json = getErrorJson(msg + ": " + e.getMessage());
        }
        PrintWriter writer = resp.getWriter();
        writer.append(json.toString());
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

    private static Json getErrorJson(String errorMessage) {
        Json json = new Json();
        json.put("wasSuccess", "false");
        json.put("errorMessage", errorMessage);
        return json;
    }

}
