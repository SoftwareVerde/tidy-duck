package com.softwareverde.tidyduck.most;

public class MostFunctionParameter {
    private String _name;
    private String _description;
    private int _parameterIndex;
    private MostType _mostType;


    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public String getDescription() {
        return _description;
    }

    public void setDescription(String description) {
        _description = description;
    }

    public int getParameterIndex() { return _parameterIndex; }

    public void setParameterIndex(int parameterIndex) {
        _parameterIndex = parameterIndex;
    }

    public MostType getMostType() {
        return _mostType;
    }

    public void setMostType(MostType mostType) {
        _mostType = mostType;
    }
}
