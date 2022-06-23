class MostFunctionStereotype {
    static fromJson(json) {
        const mostFunctionStereotype = new MostFunctionStereotype();

        const operations = [];
        const operationsJson = json.operations;
        for (let i in operationsJson) {
            const operationJson = operationsJson[i];
            const operation = Operation.fromJson(operationJson);
            operations.push(operation);
        }

        mostFunctionStereotype.setId(json.id);
        mostFunctionStereotype.setName(json.name);
        mostFunctionStereotype.setSupportsNotification(json.supportsNotification);
        mostFunctionStereotype.setCategory(json.category);
        mostFunctionStereotype.setOperations(operations);

        return mostFunctionStereotype;
    }

    static toJson(mostFunctionStereotype) {
        const jsonMostFunctionStereotype = {
            id:                     mostFunctionStereotype.getId(),
            name:                   mostFunctionStereotype.getName(),
            supportsNotification:   mostFunctionStereotype.getSupportsNotification(),
            category:               mostFunctionStereotype.getCategory(),
        };

        // Jsonify operations array, which simply contains ids for operations.
        const operations = mostFunctionStereotype.getOperations();
        const operationsJson = [];
        for (let i in operations) {
            const operationJson = Operation.toJson(operations[i]);
            operationsJson.push(operationJson);
        }
        jsonMostFunctionStereotype.operations = operationsJson;

        return jsonMostFunctionStereotype;
    }

    constructor() {
        this._id                        = null;
        this._name                      = "";
        this._supportsNotification      = null;
        this._category                  = "";
        this._operations                = [];
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

    setSupportsNotification(supportsNotification) {
        this._supportsNotification = supportsNotification;
    }

    getSupportsNotification() {
        return this._supportsNotification;
    }

    setCategory(category) {
        this._category = category;
    }

    getCategory() {
        return this._category;
    }

    setOperations(operations) {
        this._operations = operations;
    }

    getOperations() {
        return this._operations;
    }
}