package com.softwareverde.tomcat.api;

import com.softwareverde.tomcat.servlet.BaseServlet;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class ApiUrlRouter<T> {
    protected static String _cleanUrl(final String url) {
        return url.replaceAll("/[/]+", "/");
    }

    private final String _baseUrl;
    private final Map<ApiUrl, T> _apiUrls = new HashMap<>();
    private final T _errorApiRoute;

    public ApiUrlRouter(final String baseUrl, final T errorApiRoute) {
        _baseUrl = baseUrl;
        _errorApiRoute = errorApiRoute;
    }

    public void defineEndpoint(final String endpointPattern, final BaseServlet.HttpMethod httpMethod, final T apiRoute) {
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

    public ApiRoute<T> route(final HttpServletRequest request, final BaseServlet.HttpMethod httpMethod) throws RouteNotFoundException {
        final String path = _cleanUrl(request.getRequestURI());

        for (final ApiUrl apiUrl : _apiUrls.keySet()) {
            if (apiUrl.matches(path, httpMethod)) {
                final Map<String, String> urlParameters = apiUrl.getParameters(path);

                final T apiRoute = _apiUrls.get(apiUrl);

                return new ApiRoute<T>(apiRoute, urlParameters);
            }
        }

        return new ApiRoute<T>(_errorApiRoute, null);
    }
}
