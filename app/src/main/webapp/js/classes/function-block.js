class FunctionBlock {
    constructor() {
        this._id    = null;
        this._name  = null;
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
}
