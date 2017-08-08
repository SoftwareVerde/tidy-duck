package com.softwareverde.tidyduck.most;

public class PrimitiveType {
    private long _id;
    private String _name;
    private boolean _isPreloadedType;
    private boolean _isNumberBaseType;
    private boolean _isStreamParameterType;
    private boolean _isArrayType;
    private boolean _isRecordType;

    public long getId() {
        return _id;
    }

    public void setId(long id) {
        _id = id;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public boolean isPreloadedType() {
        return _isPreloadedType;
    }

    public void setIsPreloadedType(boolean preloadedType) {
        _isPreloadedType = preloadedType;
    }

    public boolean isNumberBaseType() {
        return _isNumberBaseType;
    }

    public void setIsNumberBaseType(boolean numberBaseType) {
        _isNumberBaseType = numberBaseType;
    }

    public boolean isStreamParameterType() {
        return _isStreamParameterType;
    }

    public void setIsStreamParameterType(boolean streamParameterType) {
        _isStreamParameterType = streamParameterType;
    }

    public boolean isArrayType() {
        return _isArrayType;
    }

    public void setIsArrayType(boolean arrayType) {
        _isArrayType = arrayType;
    }

    public boolean isRecordType() {
        return _isRecordType;
    }

    public void setIsRecordType(boolean recordType) {
        _isRecordType = recordType;
    }
}
