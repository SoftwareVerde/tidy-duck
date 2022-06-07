class EnumValue {
    static fromJson(json) {
        if (json == null) {
            return null;
        }

        const enumValue = new EnumValue();

        enumValue.setId(json.id);
        enumValue.setName(json.name);
        enumValue.setCode(json.code);
        enumValue.setDescription(json.description);

        return enumValue;
    }

    static toJson(enumValue) {
        if (enumValue == null) {
            return null;
        }
        return {
            id:             enumValue.getId(),
            name:           enumValue.getName(),
            code:           enumValue.getCode(),
            description:    enumValue.getDescription()
        };
    }

    constructor() {
        this._id            = null;
        this._name          = null;
        this._code          = null;
        this._description   = null;
        this._valueIndex    = null;
    }

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

    setCode(code) {
        this._code = code;
    }

    getCode() {
        return this._code;
    }

    setDescription(description) {
        this._description = description;
    }

    getDescription() {
        return this._description;
    }

    setValueIndex(valueIndex) {
        this._valueIndex = valueIndex;
    }

    getValueIndex() {
        return this._valueIndex;
    }

}