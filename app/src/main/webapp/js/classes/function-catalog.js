class FunctionCatalog {
    static fromJson(json) {
        const functionCatalog = new FunctionCatalog();

        const account = new Account();
        account.setId(json.accountId);

        const company = new Company();
        company.setId(json.companyId);

        functionCatalog.setId(json.id);
        functionCatalog.setName(json.name);
        functionCatalog.setReleaseVersion(json.releaseVersion);
        functionCatalog.setReleaseDate(json.releaseDate);
        functionCatalog.setAccount(account);
        functionCatalog.setCompany(company);

        return functionCatalog;
    }

    static toJson(functionCatalog) {
        const account = (functionCatalog.getAccount() || new Account());
        const company = (functionCatalog.getCompany() || new Company());

        return {
            name:           functionCatalog.getName(),
            releaseVersion: functionCatalog.getReleaseVersion(),
            releaseDate:    functionCatalog.getReleaseDate(),
            accountId:       account.getId(),
            companyId:      company.getId()
        };
    }

    constructor() {
        this._id                = null;
        this._name              = null;
        this._releaseVersion    = null;
        this._releaseDate       = null;
        this._account           = null;
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

    setAccount(account) {
        this._account = account;
    }

    getAccount() {
        return this._account;
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
