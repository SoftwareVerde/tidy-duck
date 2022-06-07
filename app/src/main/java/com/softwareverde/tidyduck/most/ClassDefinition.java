package com.softwareverde.tidyduck.most;

public class ClassDefinition {
    private final String _classId;
    private final String _className;
    private final String _classDescription;

    public ClassDefinition(final String id, final String name, final String description) {
        _classId = id;
        _className = name;
        _classDescription = description;
    }

    public String getClassId() {
        return _classId;
    }

    public String getClassName() {
        return _className;
    }

    public String getClassDescription() {
        return _classDescription;
    }
}
