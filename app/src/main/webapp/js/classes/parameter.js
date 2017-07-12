class Parameter {
    constructor() {
        this._parameterIndex    = null;
        this._typeId            = null;
        this._typeName          = "";
    }

    setParameterIndex(parameterIndex) {
        this._parameterIndex = parameterIndex;
    }

    getParameterIndex() {
        return this._parameterIndex;
    }

    setTypeId(typeId) {
        this._typeId = typeId;
    }

    getTypeId() {
        return this._typeId;
    }

    setTypeName(typeName) {
        this._typeName = typeName;
    }

    getTypeName() {
        return this._typeName;
    }
}