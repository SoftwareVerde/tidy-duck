class Parameter {
    constructor() {
        this._name              = null;
        this._description       = null;
        this._parameterIndex    = null;
        this._type              = null;
    }

    setName(name) {
        this._name = name;
    }

    getName() {
        return this._name;
    }

    setDescription(description) {
        this._description = description;
    }

    getDescription() {
        return this._description;
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