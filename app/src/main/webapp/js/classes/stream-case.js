class StreamCase {
    static fromJson(json) {
        if (json == null) {
            return null;
        }

        const streamCase = new StreamCase();

        streamCase.setId(json.id);
        streamCase.setStreamPositionX(json.streamPositionX);
        streamCase.setStreamPositionY(json.streamPositionY);

        for (let i in json.streamParameters) {
            let streamParameter = StreamCaseParameter.fromJson(json.streamParameters[i]);
            streamCase.addStreamParameter(streamParameter);
        }

        for (let i in json.streamSignals) {
            let streamSignal = StreamCaseSignal.fromJson(json.streamSignals[i]);
            streamCase.addStreamSignal(streamSignal);
        }

        return streamCase;
    }

    static toJson(streamCase) {
        if (streamCase == null) {
            return null;
        }
        const streamCaseJson = {
            id:                 streamCase.getId(),
            streamPositionX:    streamCase.getStreamPositionX(),
            streamPositionY:    streamCase.getStreamPositionY()
        }

        addConvertedJsonArray(streamCaseJson, "streamParameters",   streamCase.getStreamParameters(),   StreamCaseParameter.toJson);
        addConvertedJsonArray(streamCaseJson, "streamSignals",      streamCase.getStreamSignals(),      StreamCaseSignal.toJson);
    }

    constructor() {
        this._id                = null;
        this._streamPositionX   = null;
        this._streamPositionY   = null;
        this._streamParameters  = [];
        this._streamSignals     = [];
    }

    setId(id) {
        this._id = id;
    }

    getId() {
        return this._id;
    }

    setStreamPositionX(streamPositionX) {
        this._streamPositionX = streamPositionX;
    }

    getStreamPositionX() {
        return this._streamPositionX;
    }

    setStreamPositionY(streamPositionY) {
        this._streamPositionY = streamPositionY;
    }

    getStreamPositionY() {
        return this._streamPositionY;
    }

    setStreamParameters(streamParameters) {
        this._streamParameters = streamParameters;
    }

    addStreamParameter(streamParameter) {
        this._streamParameter.push(streamParameter);
    }

    getStreamParameters() {
        return this._streamParameters;
    }

    setStreamSignals(streamSignals) {
        this._streamSignals = streamSignals;
    }

    addStreamSignal(streamSignal) {
        this._streamSignal.push(streamSignal);
    }

    getStreamSignals() {
        return this._streamSignals;
    }

}