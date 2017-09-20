class Account {
    static fromJson(json) {
        const id = json.id;
        const name = json.name;
        const username = json.username;
        const company = Company.fromJson(json.company);
        const settings = Settings.fromJson(json.settings);
        const roles = json.roles.map(Role.fromJson);

        const account = new Account();

        account.setId(id);
        account.setName(name);
        account.setUsername(username);
        account.setCompany(company);
        account.setSettings(settings);
        account.setRoles(roles);

        return account;
    }

    static toJson(account) {
        const companyJson = Company.toJson(account.getCompany());
        const settingsJson = Settings.toJson(account.getSettings());
        const rolesJson = account.getRoles().map(Role.toJson);

        return {
            id: account.getId(),
            name: account.getName(),
            username: account.getUsername(),
            company: companyJson,
            settings: settingsJson,
            roles: rolesJson
        }
    }

    constructor() {
        this._id = null;
        this._name = null;
        this._username = null;
        this._company = null;
        this._settings = null;
        this._roles = [];
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

    setRoles(roles) {
        this._roles = roles;
    }

    getRoles() {
        return this._roles;
    }

    hasRole(roleName) {
        for (let i in this._roles) {
            const role = this._roles[i];
            if (role.getName() == roleName) {
                return true;
            }
        }
        return false;
    }

    hasPermission(permission) {
        for (let i in this._roles) {
            const role = this._roles[i];
            if (role.getPermissions().includes(permission)) {
                return true;
            }
        }
        return false;
    }

}