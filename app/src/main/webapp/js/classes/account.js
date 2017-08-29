class Account {
    static fromJson(json) {
        const id = json.id;
        const name = json.name;
        const username = json.username;
        const company = Company.fromJson(json.company);
        const settings = Settings.fromJson(json.settings);

        const account = new Account();

        account.setId(id);
        account.setName(name);
        account.setUsername(username);
        account.setCompany(company);
        account.setSettings = settings;

        return account;
    }

    static toJson(account) {
        const company = Company.toJson(this._company);
        const settings = Settings.toJson(this._settings);

        return {
            id: this._id,
            name: this._name,
            username: this._username,
            company: company,
            settings: settings
        }
    }

    constructor() {
        this._id = null;
        this._name = null;
        this._username = null;
        this._company = null;
        this._settings = null;
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

    setUsername(username) {
        this._username = username;
    }

    getUsername() {
        return this._username;
    }

    setCompany(company) {
        this._company = company;
    }

    getCompany() {
        return this._company;
    }

    setSettings(settings) {
        this._settings = settings;
    }

    getSettings() {
        return this._settings;
    }
}