package com.softwareverde.mostadapter.type;

public class EnumValue {
    private String _code;
    private String _name;

    public EnumValue() {}

    public EnumValue(final String code, final String name) {
        _code = code;
        _name = name;
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

    public void setName(String value) {
        _name = value;
    }
}
