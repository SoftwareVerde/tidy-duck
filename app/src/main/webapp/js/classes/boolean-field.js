class BooleanField {
    static fromJson(json) {
        if (json == null) {
            return null;
        }

        const booleanField = new BooleanField();

        booleanField.setId(json.id);
        booleanField.setBitPosition(json.bitPosition);
        booleanField.setTrueDescription(json.trueDescription);
        booleanField.setFalseDescription(json.falseDescription);

        return booleanField;
    }

    static toJson(booleanField) {
        return {
            id:                 booleanField.getId(),
            bitPosition:        booleanField.getBitPosition(),
            trueDescription:    booleanField.getTrueDescription(),
            falseDescription:   booleanField.getFalseDescription()
        };
    }

    constructor() {
        this._id                = null;
        this._bitPosition       = null;
        this._trueDescription   = null;
        this._falseDescription  = null;
        this._fieldIndex        = null;
    }

    setId(id) {
        this._id = id;
    }

    getId() {
        return this._id;
    }

    setBitPosition(bitPosition) {
        this._bitPosition = bitPosition;
    }

    getBitPosition() {
        return this._bitPosition;
    }

    setTrueDescription(trueDescription) {
        this._trueDescription = trueDescription;
    }

    getTrueDescription() {
        return this._trueDescription;
    }

    setFalseDescription(falseDescription) {
        this._falseDescription = falseDescription;
    }

    getFalseDescription() {
        return this._falseDescription;
    }

    setFieldIndex(fieldIndex) {
        this._fieldIndex = fieldIndex;
    }

    getFieldIndex() {
        return this._fieldIndex;
    }
}