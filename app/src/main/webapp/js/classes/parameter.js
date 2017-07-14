class Parameter {
    constructor() {
        this._parameterIndex    = null;
        this._type              = null;
    }

    setParameterIndex(parameterIndex) {
        this._parameterIndex = parameterIndex;
    }

    getParameterIndex() {
        return this._parameterIndex;
    }

    setType(type) {
        this._type = type;
    }

    getType() {
        return this._type;
    }
}