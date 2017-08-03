package com.softwareverde.tidyduck.most;

public class MostUnit {
    private long _id;
    private String _referenceName;
    private String _definitionName;
    private String _definitionCode;
    private String _definitionGroup;

    public long getId() {
        return _id;
    }

    public void setId(long id) {
        _id = id;
    }

    public String getReferenceName() {
        return _referenceName;
    }

    public void setReferenceName(String referenceName) {
        _referenceName = referenceName;
    }

    public String getDefinitionName() {
        return _definitionName;
    }

    public void setDefinitionName(String definitionName) {
        _definitionName = definitionName;
    }

    public String getDefinitionCode() {
        return _definitionCode;
    }

    public void setDefinitionCode(String definitionCode) {
        _definitionCode = definitionCode;
    }

    public String getDefinitionGroup() {
        return _definitionGroup;
    }

    public void setDefinitionGroup(String definitionGroup) {
        _definitionGroup = definitionGroup;
    }
}
