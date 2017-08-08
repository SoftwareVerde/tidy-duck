class EnumValue {
    static fromJson(json) {
        if (json == null) {
            return null;
        }

        const enumValue = new EnumValue();

        enumValue.setId(json.id);
        enumValue.setName(json.name);
        enumValue.setCode(json.code);

        return enumValue;
    }

    static toJson(enumValue) {
        if (enumValue == null) {
            return null;
        }
        return {
            id:     enumValue.getId(),
            name:   enumValue.getName(),
            code:   enumValue.getCode()
        };
    }

    constructor() {
        this._id    = null;
        this._name  = null;
        this._code  = null;
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

}