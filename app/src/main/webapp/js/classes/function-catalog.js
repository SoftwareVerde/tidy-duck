class FunctionCatalog {
    static fromJson(json) {
        const functionCatalog = new FunctionCatalog();

        const author = new Author();
        author.setId(json.authorId);

        const company = new Company();
        company.setId(json.companyId);

        functionCatalog.setId(json.id);
        functionCatalog.setName(json.name);
        functionCatalog.setReleaseVersion(json.releaseVersion);
        functionCatalog.setReleaseDate(json.releaseDate);
        functionCatalog.setAuthor(author);
        functionCatalog.setCompany(company);

        return functionCatalog;
    }

    //Converts existing function catalog into JSON
    static toJson(functionCatalog) {
        const author = (functionCatalog.getAuthor() || new Author());
        const company = (functionCatalog.getCompany() || new Company());

        return {
            id:             functionCatalog.getId(),
            name:           functionCatalog.getName(),
            releaseVersion: functionCatalog.getReleaseVersion(),
            releaseDate:    functionCatalog.getReleaseDate(),
            authorId:       author.getId(),
            companyId:      company.getId()
        };
    }

    constructor() {
        this._id                = null;
        this._name              = null;
        this._releaseVersion    = null;
        this._releaseDate       = null;
        this._author           = null;
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
