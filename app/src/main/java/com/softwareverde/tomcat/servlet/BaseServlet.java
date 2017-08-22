package com.softwareverde.tomcat.servlet;

import com.softwareverde.tidyduck.environment.Environment;
import com.softwareverde.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseServlet extends HttpServlet {
    public static final String BASE_API_URL = "/api/v1/";

    private final Logger _logger = LoggerFactory.getLogger(this.getClass());

    public enum HttpMethod {
        GET,
        POST,
        HEAD,
        PUT,
        DELETE,
        OPTIONS,
        TRACE
    }

    public static String getFinalUrlSegment(final HttpServletRequest request) {
        final String path = request.getRequestURI();
        final int finalSlash = path.lastIndexOf('/');
        return path.substring(finalSlash+1);
    }

    /**
     * Returns the API Path as a Key/Value pairing.
     *  If the API Path is invalid, null is returned.
     *  An API Path is considered invalid if a numeric value is attempted to be used as what would be considered a key.
     *      e.x.:   /api/v1/objects/1/2             - Invalid path.
     *              /api/v1/objects/1               - Valid path. {api: null, v1: null, objects: 1}
     *              /api/v1/objects/1/children/2    - Valid path. {api: null, v1: null, objects: 1, children: 2}
     *  Segments that do not have a value, but have a string key are defined within the map as the String Key pointing to null.
     *      e.x.:   /api/v1/objects                 - Valid path. {api: null, v1: null, objects: null}
     *  Segments delimited by consecutive slashes are treated as if there were only a single slash.
     *      e.x.:   /api/v1/objects///1             - Valid path. {api: null, v1: null, objects: 1}
     */
    public static Map<String, Long> getApiPath(final HttpServletRequest request) {
        final String path = request.getRequestURI().replaceAll("/[/]*", "/");
        final String[] segments = path.split("/");

        final Map<String, Long> apiPath = new HashMap<String, Long>();
        for (int i=0; i<segments.length; ++i) {
            final String segment = segments[i];
            final String previousSegment = (i > 0 ? segments[i-1] : null);

            final Boolean segmentIsAnInteger = Util.isLong(segment);
            final Boolean previousSegmentIsAnInteger = Util.isLong(previousSegment);
            if (segmentIsAnInteger) {
                if ((previousSegment != null) && (! previousSegmentIsAnInteger)) {
                    apiPath.put(previousSegment, Util.parseLong(segment));
                }
                else {
                    return null;
                }
            }
            else {
                apiPath.put(segment, null);
            }
        }

        return apiPath;
    }


    public static String getNthFromLastUrlSegment(final HttpServletRequest request, final int index) {
        final String path = request.getRequestURI();
        String[] segments = path.split("/");
        return segments[segments.length-1-index];
    }

    protected void handleRequest(HttpServletRequest request, HttpServletResponse response, HttpMethod method) throws ServletException, IOException {
        try {
            Environment environment = Environment.getInstance();
            this.handleRequest(request, response, method, environment);
        } catch (final Exception e) {
            _logger.error("Unable to handle request.", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            PrintWriter writer = response.getWriter();
            writer.append("Server error.");
        }
    }

    protected abstract void handleRequest(HttpServletRequest request, HttpServletResponse response, HttpMethod method, Environment environment) throws  ServletException, IOException;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleRequest(req, resp, HttpMethod.GET);
    }

    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleRequest(req, resp, HttpMethod.HEAD);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleRequest(req, resp, HttpMethod.POST);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleRequest(req, resp, HttpMethod.PUT);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleRequest(req, resp, HttpMethod.DELETE);
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleRequest(req, resp, HttpMethod.OPTIONS);
    }

    @Override
    protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleRequest(req, resp, HttpMethod.TRACE);
    }
}
