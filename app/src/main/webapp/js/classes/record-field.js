class RecordField {
    static fromJson(json) {
        if (json == null) {
            return null;
        }

        const fieldType = MostType.fromJson(json.fieldType);

        const recordField = new RecordField();

        recordField.setId(json.id);
        recordField.setFieldName(json.fieldName);
        recordField.setFieldIndex(json.fieldIndex);
        recordField.setFieldDescription(json.fieldDescription);
        recordField.setFieldType(fieldType);

        return recordField;
    }

    static toJson(recordField) {
        if (recordField == null) {
            return null;
        }
        return {
            id:                 recordField.getId(),
            fieldName:          recordField.getFieldName(),
            fieldIndex:         recordField.getFieldIndex(),
            fieldDescription:   recordField.getFieldDescription(),
            fieldType:          MostType.toJson(recordField.getFieldType())
        };
    }

    constructor() {
        this._id                = null;
        this._fieldName         = null;
        this._fieldIndex        = null;
        this._fieldDescription  = null;
        this._fieldType         = null;
    }


    setId(id) {
        this._id = id;
    }

    getId() {
        return this._id;
    }

    setFieldName(fieldName) {
        this._fieldName = fieldName;
    }

    getFieldName() {
        return this._fieldName;
    }

    setFieldIndex(fieldIndex) {
        this._fieldIndex = fieldIndex;
    }

    getFieldIndex() {
        return this._fieldIndex;
    }

    setFieldDescription(fieldDescription) {
        this._fieldDescription = fieldDescription;
    }

    getFieldDescription() {
        return this._fieldDescription;
    }

    setFieldType(fieldType) {
        this._fieldType = fieldType;
    }

    getFieldType() {
        return this._fieldType;
    }

}