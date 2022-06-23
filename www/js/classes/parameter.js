class Parameter {
    static fromJson(json) {
        const parameterType = new MostType();
        parameterType.setId(json.typeId);
        parameterType.setName(json.typeName);

        const parameter = new Parameter();
        parameter.setName(json.name);
        parameter.setDescription(json.description);
        parameter.setParameterIndex(json.parameterIndex);
        parameter.setType(parameterType);

        return parameter;
    }

    static toJson(parameter) {
        return {
            name:               parameter.getName(),
            description:        parameter.getDescription(),
            parameterIndex:     parameter.getParameterIndex(),
            typeId:             parameter.getType().getId(),
            typeName:           parameter.getType().getName()
        };
    }

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