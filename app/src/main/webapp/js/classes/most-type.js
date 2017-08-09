class MostType {
    static fromJson(json) {
        if (json == null) {
            return null;
        }

        const mostType = new MostType();

        const primitiveType = PrimitiveType.fromJson(json.primitiveType);
        const numberBaseType = PrimitiveType.fromJson(json.numberBaseType);
        const numberUnit = MostUnit.fromJson(json.numberUnit);
        const arrayElementType = MostType.fromJson(json.arrayElementType);

        mostType.setId(json.id);
        mostType.setName(json.name);
        mostType.setPrimitiveType(primitiveType);
        mostType.setIsPrimaryType(json.isPrimaryType);
        mostType.setBitFieldLength(json.bitfieldLength);
        mostType.setEnumMax(json.enumMax);
        mostType.setNumberBaseType(json.numberBaseType);
        mostType.setNumberExponent(json.numberExponent);
        mostType.setNumberRangeMin(json.numberRangeMin);
        mostType.setNumberRangeMax(json.numberRangeMax);
        mostType.setNumberStep(json.numberStep);
        mostType.setNumberUnit(numberUnit);
        mostType.setStringMaxSize(json.stringMaxSize);
        mostType.setStreamLength(json.streamLength);
        mostType.setStreamMaxLength(json.streamMaxLength);
        mostType.setStreamMediaType(json.streamMediaType);
        mostType.setArrayName(json.arrayName);
        mostType.setArrayDescription(json.arrayDescription);
        mostType.setArrayElementType(arrayElementType);
        mostType.setRecordName(json.recordName);
        mostType.setRecordDescription(json.recordDescription);
        mostType.setRecordSize(json.recordSize);

        for (let i in json.booleanFields) {
            let booleanField = BooleanField.fromJson(json.booleanFields[i]);
            mostType.addBooleanField(booleanField);
        }

        for (let i in json.enumValues) {
            let enumValue = EnumValue.fromJson(json.enumValues[i]);
            mostType.addEnumValue(enumValue);
        }

        for (let i in json.streamCases) {
            let streamCase = StreamCase.fromJson(json.streamCases[i]);
            mostType.addStreamCase(streamCase);
        }

        for (let i in json.recordFields) {
            let recordField = RecordField.fromJson(json.recordFields[i]);
            mostType.addRecordField(recordField);
        }

        return mostType;
    }

    static toJson(mostType) {
        if (mostType == null) {
            return null;
        }

        const jsonMostType = {
            id:                 mostType.getId(),
            name:               mostType.getName(),
            primitiveTypeId:    mostType.getPrimitiveType().getId(),
            isPrimaryType:      mostType.isPrimaryType(),
            bitfieldLength:     mostType.getBitFieldLength(),
            enumMax:            mostType.getEnumMax(),
            numberBaseTypeId:   mostType.getNumberBaseType() == null ? null : mostType.getNumberBaseType().getId(),
            numberExponent:     mostType.getNumberExponent(),
            numberRangeMin:     mostType.getNumberRangeMin(),
            numberRangeMax:     mostType.getNumberRangeMax(),
            numberStep:         mostType.getNumberStep(),
            numberUnitId:       mostType.getNumberUnit() == null ? null : mostType.getNumberUnit().getId(),
            stringMaxSize:      mostType.getStringMaxSize(),
            streamLength:       mostType.getStreamLength(),
            streamMaxLength:    mostType.getStreamMaxLength(),
            streamMediaType:    mostType.getStreamMediaType(),
            arrayName:          mostType.getArrayName(),
            arraySize:          mostType.getArraySize(),
            recordName:         mostType.getRecordName(),
            recordDescription:  mostType.getRecordDescription(),
            recordSize:         mostType.getRecordSize()
        };

        addConvertedJsonArray(jsonMostType, "booleanFields",    mostType.getBooleanFields(),    BooleanField.toJson);
        addConvertedJsonArray(jsonMostType, "enumValues",       mostType.getEnumValues(),       EnumValue.toJson);
        addConvertedJsonArray(jsonMostType, "streamCases",      mostType.getStreamCases(),      StreamCase.toJson);
        addConvertedJsonArray(jsonMostType, "recordFields",     mostType.getRecordFields(),     RecordField.toJson);
    }

    constructor() {
        this._id    = null;
        this._name  = "";
        this._primitiveType = null;
        this._isPrimaryType = false;
        this._bitfieldLength = null;
        this._enumMax = null;
        this._numberBaseType = null;
        this._numberExponent = null;
        this._numberRangeMin = null;
        this._numberRangeMax = null;
        this._numberStep = null;
        this._numberUnit = null;
        this._stringMaxSize = null;
        this._streamLength = null;
        this._streamMaxLength = null;
        this._streamMediaType = null;
        this._arrayName = null;
        this._arrayDescription = null;
        this._arrayElementType = null;
        this._arraySize = null;
        this._recordName = null;
        this._recordDescription = null;
        this._recordSize = null;
        this._booleanFields = [];
        this._enumValues = [];
        this._streamCases = [];
        this._recordFields = [];
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

    setPrimitiveType(primitiveType) {
        this._primitiveType = primitiveType;
    }

    getPrimitiveType() {
        return this._primitiveType;
    }

    setIsPrimaryType(isPrimaryType) {
        this._isPrimaryType = isPrimaryType;
    }

    isPrimaryType() {
        return this._isPrimaryType;
    }

    setBitFieldLength(bitfieldLength) {
        this._bitfieldLength = bitfieldLength;
    }

    getBitFieldLength() {
        return this._bitfieldLength;
    }

    setEnumMax(enumMax) {
        this._enumMax = enumMax;
    }

    getEnumMax() {
        return this._enumMax;
    }

    setNumberBaseType(numberBaseType) {
        this._numberBaseType = numberBaseType;
    }

    getNumberBaseType() {
        return this._numberBaseType;
    }

    setNumberExponent(numberExponent) {
        this._numberExponent = numberExponent;
    }

    getNumberExponent() {
        return this._numberExponent;
    }

    setNumberRangeMin(numberRangeMin) {
        this._numberRangeMin = numberRangeMin;
    }

    getNumberRangeMin() {
        return this._numberRangeMin;
    }

    setNumberRangeMax(numberRangeMax) {
        this._numberRangeMax = numberRangeMax;
    }

    getNumberRangeMax() {
        return this._numberRangeMax;
    }

    setNumberStep(numberStep) {
        this._numberStep = numberStep;
    }

    getNumberStep() {
        return this._numberStep;
    }

    setNumberUnit(numberUnit) {
        this._numberUnit = numberUnit;
    }

    getNumberUnit() {
        return this._numberUnit;
    }

    setStringMaxSize(stringMaxSize) {
        this._stringMaxSize = stringMaxSize;
    }

    getStringMaxSize() {
        return this._stringMaxSize;
    }

    setStreamLength(streamLength) {
        this._streamLength = streamLength;
    }

    getStreamLength() {
        return this._streamLength;
    }

    setStreamMaxLength(streamMaxLength) {
        this._streamMaxLength = streamMaxLength;
    }

    getStreamMaxLength() {
        return this._streamMaxLength;
    }

    setStreamMediaType(streamMediaType) {
        this._streamMediaType = streamMediaType;
    }

    getStreamMediaType() {
        return this._streamMediaType;
    }

    setArrayName(arrayName) {
        this._arrayName = arrayName;
    }

    getArrayName() {
        return this._arrayName;
    }

    setArrayDescription(arrayDescription) {
        this._arrayDescription = arrayDescription;
    }

    getArrayDescription() {
        return this._arrayDescription;
    }

    setArrayElementType(arrayElementType) {
        this._arrayElementType = arrayElementType;
    }

    getArrayElementType() {
        return this._arrayElementType;
    }

    setArraySize(arraySize) {
        this._arraySize = arraySize;
    }

    getArraySize() {
        return this._arraySize;
    }

    setRecordName(recordName) {
        this._recordName = recordName;
    }

    getRecordName() {
        return this._recordName;
    }

    setRecordDescription(recordDescription) {
        this._recordDescription = recordDescription;
    }

    getRecordDescription() {
        return this._recordDescription;
    }

    setRecordSize(recordSize) {
        this._recordSize = recordSize;
    }

    getRecordSize() {
        return this._recordSize;
    }

    setBooleanFields(booleanFields) {
        this._booleanFields = booleanFields;
    }

    addBooleanField(booleanField) {
        this._booleanField.push(booleanField);
    }

    getBooleanFields() {
        return this._booleanFields;
    }

    setEnumValues(enumValues) {
        this._enumValues = enumValues;
    }

    addEnumValue(enumValue) {
        this._enumValue.push(enumValue);
    }

    getEnumValues() {
        return this._enumValues;
    }

    setStreamCases(streamCases) {
        this._streamCases = streamCases;
    }

    addStreamCase(streamCase) {
        this._streamCase.push(streamCase);
    }

    getStreamCases() {
        return this._streamCases;
    }

    setRecordFields(recordFields) {
        this._recordFields = recordFields;
    }

    addRecordField(recordField) {
        this._recordField.push(recordField);
    }

    getRecordFields() {
        return this._recordFields;
    }
}