package com.softwareverde.servlet;

import com.softwareverde.json.Json;
import com.softwareverde.json.Jsonable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public abstract class JsonServlet extends HttpServlet {

    private Logger _logger = LoggerFactory.getLogger(getClass());

    private static final int BUFFER_SIZE = 8192;

    protected enum HttpMethod {
        GET,
        POST,
        HEAD,
        PUT,
        DELETE,
        OPTIONS,
        TRACE
    }

    protected abstract Json handleRequest(HttpServletRequest request, HttpMethod httpMethod) throws Exception;

    private void processJsonRequest(HttpServletRequest req, HttpServletResponse resp, HttpMethod httpMethod) throws IOException {
        Json json = null;
        try {
            json = handleRequest(req, httpMethod);
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

    protected Json getRequestDataAsJson(HttpServletRequest request) throws IOException {
        BufferedReader reader = request.getReader();
        StringBuilder builder = new StringBuilder();
        char[] buffer = new char[BUFFER_SIZE];
        int length = 0;
        while ((length = reader.read(buffer, 0, BUFFER_SIZE)) > 0) {
            builder.append(buffer, 0, length);
        }
        return Json.parse(builder.toString());
    }

    private Json getErrorJson(String errorMessage) {
        Json json = new Json();
        json.put("wasSuccess", "false");
        json.put("errorMessage", errorMessage);
        return json;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processJsonRequest(req, resp, HttpMethod.GET);
    }

    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processJsonRequest(req, resp, HttpMethod.HEAD);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processJsonRequest(req, resp, HttpMethod.POST);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processJsonRequest(req, resp, HttpMethod.PUT);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processJsonRequest(req, resp, HttpMethod.DELETE);
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processJsonRequest(req, resp, HttpMethod.OPTIONS);
    }

    @Override
    protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processJsonRequest(req, resp, HttpMethod.TRACE);
    }
}
