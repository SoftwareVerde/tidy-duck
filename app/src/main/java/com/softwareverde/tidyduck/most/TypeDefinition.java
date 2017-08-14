package com.softwareverde.tidyduck.most;

public class TypeDefinition {
    private final String _typeId;
    private final Integer _typeSize;
    private final String _typeName;
    private final String _typeDescription;

    public TypeDefinition(final String id, final Integer typeSize, final String name, final String description) {
        _typeId = id;
        _typeSize = typeSize;
        _typeName = name;
        _typeDescription = description;
    }

    public String getTypeId() {
        return _typeId;
    }

    public Integer getTypeSize() {
        return _typeSize;
    }

    public String getTypeName() {
        return _typeName;
    }

    public String getTypeDescription() {
        return _typeDescription;
    }
}
