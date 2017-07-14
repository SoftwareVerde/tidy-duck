class MostType {
    static fromJson(json) {
        const mostInterface = new MostInterface();

        mostInterface.setId(json.id);
        mostInterface.setName(json.name);

        return mostInterface;
    }

    static toJson(mostInterface) {
        return {
            id:     mostInterface.getId(),
            name:   mostInterface.getName()
        };
    }

    constructor() {
        this._id    = null;
        this._name  = "";
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