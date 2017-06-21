class MostInterface {
    static fromJson(json) {
        const mostInterface = new MostInterface();

        mostInterface.setId(json.id);
        mostInterface.setMostId(json.mostId);
        mostInterface.setName(json.name);
        mostInterface.setDescription(json.description);
        mostInterface.setLastModifiedDate(json.lastModifiedDate);
        mostInterface.setVersion(json.version);

        return mostInterface;
    }

    static toJson(mostInterface) {
        return {
            id:                 mostInterface.getId(),
            mostId:             mostInterface.getMostId(),
            name:               mostInterface.getName(),
            description:        mostInterface.getDescription(),
            lastModifiedDate:   mostInterface.getLastModifiedDate(),
            version:            mostInterface.getVersion(),
        };
    }

    constructor() {
        this._id                    = null;
        this._mostId                = null;
        this._name                  = "";
        this._description           = "";
        this._lastModifiedDate      = "";
        this._version               = "";

        this._functions             = [];
    };

    setId(id) {
        this._id = id;
    }

    getId() {
        return this._id;
    }

    setMostId(mostId) {
        this._mostId = mostId;
    }

    getMostId() {
        return this._mostId;
    }

    setName(name) {
        this._name = name;
    }

    getName() {
        return this._name;
    }

    setDescription(description) {
        this._description = description;
    }

    getDescription() {
        return this._description;
    }

    setLastModifiedDate(lastModifiedDate) {
        this._lastModifiedDate = lastModifiedDate;
    }

    getLastModifiedDate() {
        return this._lastModifiedDate;
    }

    setVersion(version) {
        this._version = version;
    }

    getVersion() {
        return this._version;
    }

    getFunctions() {
        return this._functions;
    }
}
