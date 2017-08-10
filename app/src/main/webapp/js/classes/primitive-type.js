class PrimitiveType {

    static fromJson(json) {
        if (json == null) {
            return null;
        }
        const primitiveType = new PrimitiveType();

        primitiveType.setId(json.id);
        primitiveType.setName(json.name);
        primitiveType.setIsBaseType(json.isBaseType);
        primitiveType.setIsNumberBaseType(json.isNumberBaseType);
        primitiveType.setIsStreamParamType(json.isStreamParamType);
        primitiveType.setIsArrayType(json.isArrayType);
        primitiveType.setIsRecordType(json.isRecordType);

        return primitiveType;
    }

    constructor() {
        this._id    = null;
        this._name  = null;
        this._isBaseType = false;
        this._isNumberBaseType = false;
        this._isStreamParamType = false;
        this._isArrayType = false;
        this._isRecordType = false;
    };

    setId(id) {
        this._id = id;
    }

    getId() {
        return this._id;
    }

    setName(name) {
        this._name = name;
    }

    getName() {
        return this._name;
    }

    setIsBaseType(isBaseType) {
        this._isBaseType = isBaseType;
    }

    isBaseType() {
        return this._isBaseType;
    }

    setIsNumberBaseType(isNumberBaseType) {
        this._isNumberBaseType = isNumberBaseType;
    }

    isNumberBaseType() {
        return this._isNumberBaseType;
    }

    setIsStreamParamType(isStreamParamType) {
        this._isStreamParamType = isStreamParamType;
    }

    isStreamParamType() {
        return this._isStreamParamType;
    }

    setIsArrayType(isArrayType) {
        this._isArrayType = isArrayType;
    }

    isArrayType() {
        return this._isArrayType;
    }

    setIsRecordType(isRecordType) {
        this._isRecordType = isRecordType;
    }

    isRecordType() {
        return this._isRecordType;
    }
}