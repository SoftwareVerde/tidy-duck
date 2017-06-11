class FunctionCatalog {
    constructor() {
        this._id                = null;
        this._name              = null;
        this._releaseVersion    = null;
        this._releaseDate       = null;
        this._author            = null;
        this._company           = null;

        this._functionBlocks    = [];
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

    setReleaseVersion(releaseVersion) {
        this._releaseVersion = releaseVersion;
    }

    getReleaseVersion() {
        return this._releaseVersion;
    }

    setReleaseDate(releaseDate) {
        this._releaseDate = releaseDate;
    }

    getReleaseDate() {
        return this._releaseDate;
    }

    setAuthor(author) {
        this._author = author;
    }

    getAuthor() {
        return this._author;
    }

    setCompany(company) {
        this._company = company;
    }

    getCompany() {
        return this._company;
    }

    getFunctionBlocks() {
        return this._functionBlocks;
    }
}
