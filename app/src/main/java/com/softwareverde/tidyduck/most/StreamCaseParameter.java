package com.softwareverde.tidyduck.most;

public class StreamCaseParameter {
    private Long _id;
    private String _parameterName;
    private String _parameterIndex;
    private String _parameterDescription;
    private MostType _parameterType;

    public Long getId() {
        return _id;
    }

    public void setId(Long id) {
        _id = id;
    }

    public String getParameterName() {
        return _parameterName;
    }

    public void setParameterName(String parameterName) {
        _parameterName = parameterName;
    }

    public String getParameterIndex() {
        return _parameterIndex;
    }

    public void setParameterIndex(String parameterIndex) {
        _parameterIndex = parameterIndex;
    }

    public String getParameterDescription() {
        return _parameterDescription;
    }

    public void setParameterDescription(String parameterDescription) {
        _parameterDescription = parameterDescription;
    }

    public MostType getParameterType() {
        return _parameterType;
    }

    public void setParameterType(MostType parameterType) {
        _parameterType = parameterType;
    }
}
