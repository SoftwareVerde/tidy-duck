class FunctionCatalog {
    static fromJson(json) {
        const functionCatalog = new FunctionCatalog();

        const author = new Author();
        author.setId(json.authorId);
        author.setName(json.authorName);

        const company = new Company();
        company.setId(json.companyId);
        company.setName(json.companyName);

        functionCatalog.setId(json.id);
        functionCatalog.setName(json.name);
        functionCatalog.setReleaseVersion(json.releaseVersion);
        functionCatalog.setAuthor(author);
        functionCatalog.setCompany(company);

        return functionCatalog;
    }

    // Converts existing function catalog into JSON
    static toJson(functionCatalog) {
        const jsonFunctionCatalog = {
            id:             functionCatalog.getId(),
            name:           functionCatalog.getName(),
            releaseVersion: functionCatalog.getReleaseVersion()
        };
        const author = (functionCatalog.getAuthor() || new Author());
        const company = (functionCatalog.getCompany() || new Company());
        if (author.getId() > 0) {
            jsonFunctionCatalog.authorId = author.getId();
        }
        if (company.getId() > 0) {
            jsonFunctionCatalog.companyId = company.getId();
        }
        return jsonFunctionCatalog;
    }

    constructor() {
        this._id                = null;
        this._name              = "";
        this._releaseVersion    = "";
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
