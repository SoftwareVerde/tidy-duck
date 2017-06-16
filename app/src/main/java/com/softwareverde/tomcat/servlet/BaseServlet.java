package com.softwareverde.tomcat.servlet;

import com.softwareverde.tidyduck.environment.Environment;
import com.softwareverde.util.HashUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

public abstract class BaseServlet extends HttpServlet {

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
        final String path = request.getServletPath();
        final int finalSlash = path.lastIndexOf('/');
        return path.substring(finalSlash+1);
    }

    protected void handleRequest(HttpServletRequest request, HttpServletResponse response, HttpMethod method) throws ServletException, IOException {
        if (! AuthenticatedJsonServlet.hasSessionCookie(request)) {
            final Cookie userCookie = new Cookie(AuthenticatedJsonServlet.COOKIE_SESSION_NAME, HashUtil.sha256(""+ Math.random()));
            userCookie.setPath("/");
            userCookie.setHttpOnly(true);
            userCookie.setMaxAge(60 * 24 * 265);
            response.addCookie(userCookie);
        }

        try {
            Environment environment = Environment.getInstance();
            this.handleRequest(request, response, method, environment);
        } catch (Exception e) {
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
