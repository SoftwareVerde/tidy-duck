package com.softwareverde.tidyduck.most;

public class ErrorDefinition {
    private final String _errorId;
    private final String _errorCode;
    private final String _errorDescription;
    private final String _info;
    private final String _infoDescription;

    public ErrorDefinition(final String id, final String code, final String errorDescription, final String info, final String infoDescription) {
        _errorId = id;
        _errorCode = code;
        _errorDescription = errorDescription;
        _info = info;
        _infoDescription = infoDescription;
    }

    public String getErrorId() {
        return _errorId;
    }

    public String getErrorCode() {
        return _errorCode;
    }

    public String getErrorDescription() {
        return _errorDescription;
    }

    public String getInfo() {
        return _info;
    }

    public String getInfoDescription() {
        return _infoDescription;
    }
}
