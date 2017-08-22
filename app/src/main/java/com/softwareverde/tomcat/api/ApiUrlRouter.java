package com.softwareverde.tomcat.api;

import com.softwareverde.json.Json;
import com.softwareverde.tidyduck.environment.Environment;
import com.softwareverde.tomcat.servlet.BaseServlet;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class ApiUrlRouter {
    protected static String _cleanUrl(final String url) {
        return url.replaceAll("/[/]*", "/");
    }

    private final String _baseUrl;
    private final Map<ApiUrl, ApiRoute> _apiUrls = new HashMap<ApiUrl, ApiRoute>();
    private ApiRoute _errorApiRoute = null;

    public ApiUrlRouter(final String baseUrl) {
        _baseUrl = baseUrl;
    }

    public void defineEndpoint(final String endpointPattern, final BaseServlet.HttpMethod httpMethod, final ApiRoute apiRoute) {
        final String path = _cleanUrl(_baseUrl + endpointPattern);
        final String[] segments = path.split("/");

        final ApiUrl apiUrl = new ApiUrl(_baseUrl + endpointPattern, httpMethod);

        for (int i=0; i<segments.length; ++i) {
            final String segment = segments[i];

            Boolean isVariable = false;
            if ((segment.length() > 2)) {
                final Character firstCharacter = segment.charAt(0);
                final Character lastCharacter = segment.charAt(segment.length() - 1);

                if (firstCharacter.equals('<') && lastCharacter.equals('>')) {
                    final String variableName = segment.substring(1, segment.length() - 1);
                    apiUrl.appendParameter(variableName);
                    isVariable = true;
                }
            }

            if (! isVariable) {
                apiUrl.appendSegment(segment);
            }
        }

        _apiUrls.put(apiUrl, apiRoute);
    }

    public void setErrorRoute(final ApiRoute apiRoute) {
        _errorApiRoute = apiRoute;
    }

    public Json route(final HttpServletRequest request, final BaseServlet.HttpMethod httpMethod, final long accountId, final Environment environment) throws Exception {
        final String path = _cleanUrl(request.getRequestURI());

        for (final ApiUrl apiUrl : _apiUrls.keySet()) {
            if (apiUrl.matches(path, httpMethod)) {
                final Map<String, String> urlParameters = apiUrl.getParameters(path);

                final ApiRoute apiRoute = _apiUrls.get(apiUrl);
                return apiRoute.handleAuthenticatedRequest(urlParameters, request, httpMethod, accountId, environment);
            }
        }

        if (_errorApiRoute != null) {
            return _errorApiRoute.handleAuthenticatedRequest(new HashMap<String, String>(), request, httpMethod, accountId, environment);
        }

        return new Json();
    }
}
