class Account {
    static fromJson(json) {
        const id = json.id;
        const name = json.name;
        const username = json.username;
        const company = Company.fromJson(json.company);
        const settings = Settings.fromJson(json.settings);
        const permissions = json.permissions;

        const account = new Account();

        account.setId(id);
        account.setName(name);
        account.setUsername(username);
        account.setCompany(company);
        account.setSettings(settings);
        account.setPermissions(permissions);

        return account;
    }

    static toJson(account) {
        const company = Company.toJson(account.getCompany());
        const settings = Settings.toJson(account.getSettings());

        return {
            id: account.getId(),
            name: account.getName(),
            username: account.getUsername(),
            company: company,
            settings: settings,
            permissions: account.getPermissions()
        }
    }

    constructor() {
        this._id = null;
        this._name = null;
        this._username = null;
        this._company = null;
        this._settings = null;
        this._permissions = [];
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

    setPermissions(permissions) {
        this._permissions = permissions;
    }

    hasPermission(permission) {
        return this._permissions.includes(permission);
    }

    getPermissions() {
        return this._permissions;
    }

}