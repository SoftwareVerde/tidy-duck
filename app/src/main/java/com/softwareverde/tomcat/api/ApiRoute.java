package com.softwareverde.tomcat.api;

import java.util.Map;

public class ApiRoute<T> {
    private Map<String, String> _parameters;
    private T _route;

    protected ApiRoute(final T route, final Map<String, String> parameters) {
        this._route = route;
        this._parameters = parameters;
    }

    public Map<String, String> getParameters() {
        return _parameters;
    }

    public void setParameters(final Map<String, String> parameters) {
        this._parameters = parameters;
    }

    public T getRoute() {
        return _route;
    }

    public void setRoute(final T route) {
        this._route = route;
    }
}
