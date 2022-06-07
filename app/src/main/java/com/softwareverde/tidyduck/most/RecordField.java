package com.softwareverde.tidyduck.most;

public class RecordField {
    private Long _id;
    private String _fieldName;
    private String _fieldIndex;
    private String _fieldDescription;
    private MostType _fieldType;

    public Long getId() {
        return _id;
    }

    public void setId(Long id) {
        _id = id;
    }

    public String getFieldName() {
        return _fieldName;
    }

    public void setFieldName(String fieldName) {
        _fieldName = fieldName;
    }

    public String getFieldIndex() {
        return _fieldIndex;
    }

    public void setFieldIndex(String fieldIndex) {
        _fieldIndex = fieldIndex;
    }

    public String getFieldDescription() {
        return _fieldDescription;
    }

    public void setFieldDescription(String fieldDescription) {
        _fieldDescription = fieldDescription;
    }

    public MostType getFieldType() {
        return _fieldType;
    }

    public void setFieldType(MostType fieldType) {
        _fieldType = fieldType;
    }
}
