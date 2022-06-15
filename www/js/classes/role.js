class Role {
    static fromJson(json) {
        const role = new Role();

        role.setName(json.name);
        role.setPermissions(json.permissions);

        return role;
    }

    static toJson(role) {
        return {
            name:           role.getName(),
            permissions:    role.getPermissions()
        };
    }

    constructor() {
        this._name = null;
        this._permissions = [];
    }

    setName(name) {
        this._name = name;
    }

    getName() {
        return this._name;
    }

    setPermissions(permissions) {
        this._permissions = permissions;
    }

    getPermissions() {
        return this._permissions;
    }
}