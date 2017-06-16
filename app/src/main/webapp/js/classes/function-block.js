class FunctionBlock {
    static fromJson(json) {
        const functionBlock = new FunctionBlock();

        const author = new Account();
        author.setId(json.authorId);

        const company = new Company();
        company.setId(json.companyId);

        functionBlock.setId(json.id);
        functionBlock.setMostId(json.mostId);
        functionBlock.setKind(json.kind);
        functionBlock.setName(json.name);
        functionBlock.setDescription(json.description);
        functionBlock.setLastModifiedDate(json.lastModifiedDate);
        functionBlock.setReleaseVersion(json.releaseVersion);
        functionBlock.setAuthor(author);
        functionBlock.setCompany(company);

        return functionBlock;
    }

    constructor() {
        this._id                    = null;
        this._mostId                = null;
        this._kind                  = null;
        this._name                  = null;
        this._description           = null;
        this._lastModifiedDate      = null;
        this._releaseVersion        = null;
        this._author                = null;
        this._company               = null;
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

    setKind(kind) {
        this._kind = kind;
    }

    getKind() {
        return this._kind;
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

    setReleaseVersion(releaseVersion) {
        this._releaseVersion = releaseVersion;
    }

    getReleaseVersion() {
        return this._releaseVersion;
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
}
