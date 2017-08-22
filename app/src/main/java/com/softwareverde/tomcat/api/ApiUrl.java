package com.softwareverde.tomcat.api;

import com.softwareverde.tomcat.servlet.BaseServlet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ApiUrl {
    private enum SegmentType {
        SEGMENT, PARAMETER
    }

    private final String _path;
    private final BaseServlet.HttpMethod _httpMethod;
    private final List<SegmentType> _segmentTypes = new ArrayList<SegmentType>();
    private final List<String> _segmentIdentifiers = new ArrayList<String>();

    public ApiUrl(final String path, final BaseServlet.HttpMethod httpMethod) {
        _path = path;
        _httpMethod = httpMethod;
    }

    public void appendParameter(final String parameter) {
        _segmentTypes.add(SegmentType.PARAMETER);
        _segmentIdentifiers.add(parameter);
    }

    public void appendSegment(final String segment) {
        _segmentTypes.add(SegmentType.SEGMENT);
        _segmentIdentifiers.add(segment);
    }

    public Boolean matches(final String path, final BaseServlet.HttpMethod httpMethod) {
        if (httpMethod != _httpMethod) { return false; }

        final String cleanedPath = ApiUrlRouter._cleanUrl(path);
        final String[] segments = cleanedPath.split("/");
        if (_segmentTypes.size() != segments.length) { return false; }

        for (int i=0; i<segments.length; ++i) {
            final String segment = segments[i];

            final Boolean isSegment = (_segmentTypes.get(i) == SegmentType.SEGMENT);
            if (isSegment) {
                 final String segmentIdentifier = _segmentIdentifiers.get(i);
                 if (! segmentIdentifier.equalsIgnoreCase(segment)) {
                     return false;
                 }
            }
        }

        return true;
    }

    public Map<String, String> getParameters(final String path) {
        final Map<String, String> parameters = new HashMap<String, String>();

        final String cleanedPath = ApiUrlRouter._cleanUrl(path);

        final String[] segments = cleanedPath.split("/");
        if (_segmentTypes.size() != segments.length) { return null; }

        for (int i=0; i<segments.length; ++i) {
            final String parameterValue = segments[i];

            final Boolean isParameter = (_segmentTypes.get(i) == SegmentType.PARAMETER);
            if (isParameter) {
                final String parameterKey = _segmentIdentifiers.get(i);
                parameters.put(parameterKey, parameterValue);
            }
        }

        return parameters;
    }

    @Override
    public boolean equals(final Object object) {
        if (! (object instanceof ApiUrl)) { return false; }
        return _path.equals(((ApiUrl) object)._path);
    }

    @Override
    public int hashCode() {
        return _path.hashCode();
    }
}
