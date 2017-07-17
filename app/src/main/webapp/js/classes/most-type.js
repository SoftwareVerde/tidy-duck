class MostType {
    static fromJson(json) {
        const mostType = new MostType();

        mostType.setId(json.id);
        mostType.setName(json.name);

        return mostType;
    }

    static toJson(mostType) {
        return {
            id:     mostType.getId(),
            name:   mostType.getName()
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