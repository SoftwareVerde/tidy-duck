class MostFunction {
    static fromJson(json) {
        const mostFunction = new MostFunction();

        const author = new Author();
        author.setId(json.authorId);
        author.setName(json.authorName);

        const company = new Company();
        company.setId(json.companyId);
        company.setName(json.companyName);

        const operations = [];
        const operationsJson = json.operations;
        for (let i in operationsJson) {
            const operationJson = operationsJson[i];
            const operation = new Operation();
            operation.setId(operationJson.id);
            operation.setName(operationJson.name);

            operations.push(operation);
        }

        const functionType = json.functionType;

        mostFunction.setId(json.id);
        mostFunction.setMostId(json.mostId);
        mostFunction.setName(json.name);
        mostFunction.setDescription(json.description);
        mostFunction.setFunctionType(functionType);
        mostFunction.setReleaseVersion(json.releaseVersion);
        mostFunction.setAuthor(author);
        mostFunction.setCompany(company);
        mostFunction.setStereotypeId(json.stereotypeId);
        mostFunction.setStereotypeName(json.stereotypeName);
        mostFunction.setReturnTypeId(json.returnTypeId);
        mostFunction.setReturnTypeName(json.returnTypeName);
        mostFunction.setOperations(operations);

        if(functionType === "Property") {
            mostFunction.setSupportsNotification(json.supportsNotification);
        }
        else {
            const parameters = [];
            const parametersJson = json.inputParameters;

            for (let i in parametersJson) {
                const parameterJson = parametersJson[i];
                const parameter = new Parameter();
                parameter.setParameterIndex(parameterJson.index);
                parameter.setTypeId(parameterJson.typeId);
                parameter.setTypeName(parameterJson.typeName);

                parameters.push(parameter);
            }
            mostFunction.setParameters(parameters);
        }

        return mostFunction;
    }

    static toJson(mostFunction) {
        const jsonMostFunction = {
            id:                     mostFunction.getId(),
            mostId:                 mostFunction.getMostId(),
            name:                   mostFunction.getName(),
            description:            mostFunction.getDescription(),
            releaseVersion:         mostFunction.getReleaseVersion(),
            functionType:           mostFunction.getFunctionType(),
            stereotypeId:           mostFunction.getStereotypeId(),
            stereotypeName:         mostFunction.getStereotypeName(),
            returnTypeId:           mostFunction.getReturnTypeId(),
            returnTypeName:         mostFunction.getReturnTypeName(),
            supportsNotification:   mostFunction.getSupportsNotification(),
        };

        const author = (mostFunction.getAuthor() || new Author());
        const company = (mostFunction.getCompany() || new Company());
        if (author.getId() > 0) {
            jsonMostFunction.authorId = author.getId();
        }
        if (company.getId() > 0) {
            jsonMostFunction.companyId = company.getId();
        }

        // Jsonify parameters array, which contains parameter objects
        const parameters = mostFunction.getParameters();
        const parametersJson = {};
        for (let i in parameters) {
            const parameterJson = {
                parameterIndex:     parameters[i].getParameterIndex(),
                typeId:             parameters[i].getTypeId()
            };

            parametersJson.i = parameterJson;
        }
        jsonMostFunction.inputParameters = parametersJson;

        // Jsonify operations array, which simply contains ids for operations.
        const operations = mostFunction.getOperations();
        const operationsJson = {};
        for (let i in operations) {
            operationsJson.i = operations[i].getId();
        }
        jsonMostFunction.operations = operationsJson;

        return jsonMostFunction;
    }

    constructor() {
      this._id                    = null;
      this._mostId                = null;
      this._name                  = "";
      this._description           = "";
      this._functionType          = "";
      this._releaseVersion        = "";
      this._author                = null;
      this._company               = null;

      this._stereotypeId          = null;
      this._stereotypeName        = "";
      this._returnTypeId          = "";
      this._returnTypeName        = "";
      this._supportsNotification  = false;

      this._parameters            = [];
      this._operations            = [];
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

    setFunctionType(functionType) {
        this._functionType = functionType;
    }

    getFunctionType() {
        return this._functionType;
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

    setStereotypeId(stereotypeId) {
        this._stereotypeId = stereotypeId;
    }

    getStereotypeId() {
        return this._stereotypeId;
    }

    setStereotypeName(stereotypeName) {
        this._stereotypeId = stereotypeName;

        switch (stereotypeName) {
            case 'Event':
                this.setStereotypeId(1);
                break;
            case 'ReadOnlyProperty':
                this.setStereotypeId(2);
                break;
            case 'ReadOnlyPropertyWithEvent':
                this.setStereotypeId(3);
                break;
            case 'PropertyWithEvent':
                this.setStereotypeId(4);
                break;
            case 'CommandWithAck':
                this.setStereotypeId(5);
                break;
            case 'Request/Response':
                this.setStereotypeId(6);
                break;
        }
    }

    getStereotypeName() {
        return this._stereotypeName;
    }

    setReturnTypeId(returnTypeId) {
        this._returnTypeId = returnTypeId;
    }

    getReturnTypeId() {
        return this._returnTypeId;
    }

    setReturnTypeName(returnTypeName) {
        this._returnTypeId = returnTypeName;
    }

    getReturnTypeName() {
        return this._returnTypeName;
    }

    setSupportsNotification(supportsNotification) {
        this._supportsNotification = supportsNotification;
    }

    getSupportsNotification() {
        return this._supportsNotification;
    }


    setParameters(parameters) {
        this._parameters = parameters;
    }

    getParameters() {
        return this._parameters;
    }

    setOperations(operations) {
        this._operations = operations;
    }

    getOperations() {
        return this._operations;
    }

}