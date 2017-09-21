class Company {
    static fromJson(json) {
        if (json == null) {
            return null;
        }
        const company = new Company();

        company.setId(json.id);
        company.setName(json.name);

        return company;
    }

    static toJson(company) {
        return {
            id: company.getId(),
            name: company.getName()
        };
    }

    constructor() {
        this._id = null;
        this._name  = null;
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
}
