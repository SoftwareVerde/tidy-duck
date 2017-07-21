package com.softwareverde.mostadapter.type;

public class EnumValue {
    private String _code;
    private String _value;

    public EnumValue() {}

    public EnumValue(final String code, final String value) {
        _code = code;
        _value = value;
    }

    public String getCode() {
        return _code;
    }

    public void setCode(String code) {
        _code = code;
    }

    public String getValue() {
        return _value;
    }

    public void setValue(String value) {
        _value = value;
    }
}
