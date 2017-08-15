package com.softwareverde.tidyduck.most;

public class UnitDefinition {
    private final String _unitId;
    private final String _unitName;
    private final String _unitCode;
    private final String _unitGroup;

    public UnitDefinition(final String id, final String name, final String code, final String group) {
        _unitId = id;
        _unitName = name;
        _unitCode = code;
        _unitGroup = group;
    }

    public String getUnitId() {
        return _unitId;
    }

    public String getUnitName() {
        return _unitName;
    }

    public String getUnitCode() {
        return _unitCode;
    }

    public String getUnitGroup() {
        return _unitGroup;
    }
}
