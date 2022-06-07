package com.softwareverde.mostadapter.type;

public class EnumValue {
    private String _code;
    private String _name;
    private String _description;

    public EnumValue() {}

    public EnumValue(final String code, final String name, final String description) {
        _code = code;
        _name = name;
        _description = description;
    }

    public String getCode() {
        return _code;
    }

    public void setCode(String code) {
        _code = code;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public String getDescription() {
        return _description;
    }

    public void setDescription(final String description) {
        _description = description;
    }
}
