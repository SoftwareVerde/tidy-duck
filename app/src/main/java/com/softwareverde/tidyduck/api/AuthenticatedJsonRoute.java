package com.softwareverde.tidyduck.api;

import com.softwareverde.json.Json;
import com.softwareverde.tidyduck.Account;
import com.softwareverde.tidyduck.environment.Environment;
import com.softwareverde.tomcat.servlet.BaseServlet;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface AuthenticatedJsonRoute {
    Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final BaseServlet.HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception;
}
