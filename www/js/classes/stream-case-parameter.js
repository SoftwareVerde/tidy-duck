class StreamCaseParameter {
    static fromJson(json) {
        if (json == null) {
            return null;
        }

        const parameterType = MostType.fromJson(json.parameterType);

        const streamCaseParameter = new StreamCaseParameter();

        streamCaseParameter.setId(json.id);
        streamCaseParameter.setParameterName(json.parameterName);
        streamCaseParameter.setParameterIndex(json.parameterIndex);
        streamCaseParameter.setParameterDescription(json.parameterDescription);
        streamCaseParameter.setParameterType(parameterType);

        return streamCaseParameter;
    }

    static toJson(streamCaseParameter) {
        if (streamCaseParameter == null) {
            return null;
        }
        return {
            id:                     streamCaseParameter.getId(),
            parameterName:          streamCaseParameter.getParameterName(),
            parameterIndex:         streamCaseParameter.getParameterIndex(),
            parameterDescription:   streamCaseParameter.getParameterDescription(),
            parameterType:          MostType.toJson(streamCaseParameter.getParameterType())
        };
    }

    constructor() {
        this._id                    = null;
        this._parameterName         = null;
        this._parameterIndex        = null;
        this._parameterDescription  = null;
        this._parameterType         = null;
    }

    setId(id) {
        this._id = id;
    }

    getId() {
        return this._id;
    }

    setParameterName(parameterName) {
        this._parameterName = parameterName;
    }

    getParameterName() {
        return this._parameterName;
    }

    setParameterIndex(parameterIndex) {
        this._parameterIndex = parameterIndex;
    }

    getParameterIndex() {
        return this._parameterIndex;
    }

    setParameterDescription(parameterDescription) {
        this._parameterDescription = parameterDescription;
    }

    getParameterDescription() {
        return this._parameterDescription;
    }

    setParameterType(parameterType) {
        this._parameterType = parameterType;
    }

    getParameterType() {
        return this._parameterType;
    }

}