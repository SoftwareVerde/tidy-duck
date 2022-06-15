class StreamCaseSignal {
    static fromJson(json) {
        if (json == null) {
            return null;
        }

        const streamCaseSignal = new StreamCaseSignal();

        streamCaseSignal.setId(json.id);
        streamCaseSignal.setSignalName(json.signalName);
        streamCaseSignal.setSignalIndex(json.signalIndex);
        streamCaseSignal.setSignalDescription(json.signalDescription);
        streamCaseSignal.setSignalBitLength(json.signalBitLength);

        return streamCaseSignal;
    }

    static toJson(streamCaseSignal) {
        if (streamCaseSignal == null) {
            return null;
        }
        return {
            id:                 streamCaseSignal.getId(),
            signalName:         streamCaseSignal.getSignalName(),
            signalIndex:        streamCaseSignal.getSignalIndex(),
            signalDescription:  streamCaseSignal.getSignalDescription(),
            signalBitLength:    streamCaseSignal.getSignalBitLength()
        };
    }

    constructor() {
        this._id                = null;
        this._signalName        = null;
        this._signalIndex       = null;
        this._signalDescription = null;
        this._signalBitLength   = null;
    }

    setId(id) {
        this._id = id;
    }

    getId() {
        return this._id;
    }

    setSignalName(signalName) {
        this._signalName = signalName;
    }

    getSignalName() {
        return this._signalName;
    }

    setSignalIndex(signalIndex) {
        this._signalIndex = signalIndex;
    }

    getSignalIndex() {
        return this._signalIndex;
    }

    setSignalDescription(signalDescription) {
        this._signalDescription = signalDescription;
    }

    getSignalDescription() {
        return this._signalDescription;
    }

    setSignalBitLength(signalBitLength) {
        this._signalBitLength = signalBitLength;
    }

    getSignalBitLength() {
        return this._signalBitLength;
    }

}