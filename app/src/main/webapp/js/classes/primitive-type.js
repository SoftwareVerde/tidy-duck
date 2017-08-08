class PrimitiveType {

    static fromJson(json) {
        const primitiveType = new PrimitiveType();

        primitiveType.setId(json.id);
        primitiveType.setName(json.name);
        primitiveType.setPreloadedType(json.isPreloadedType);
        primitiveType.setNumberBaseType(json.isNumberBaseType);
        primitiveType.setStreamParamType(json.isStreamParamType);
        primitiveType.setArrayType(json.isArrayType);
        primitiveType.setRecordType(json.isRecordType);

        return primitiveType;
    }

    constructor() {
        this._id    = null;
        this._name  = null;
        this._isPreloadedType = false;
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

    setIsPreloadedType(isPreloadedType) {
        this._isPreloadedType = isPreloadedType;
    }

    isPreloadedType() {
        return this._isPreloadedType;
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