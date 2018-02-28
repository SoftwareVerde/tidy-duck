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
            const operation = Operation.fromJson(operationJson);
            operations.push(operation);
        }

        const functionType = json.functionType;

        const returnType = new MostType();
        returnType.setId(json.returnTypeId);
        returnType.setName(json.returnTypeName);

        // Only grabbing name and id from JSON. Create and Update Forms will provide all stereotype info from api.
        const mostFunctionStereotype = new MostFunctionStereotype();
        mostFunctionStereotype.setId(json.stereotypeId);
        mostFunctionStereotype.setName(json.stereotypeName);

        mostFunction.setId(json.id);
        mostFunction.setMostId(json.mostId);
        mostFunction.setName(json.name);
        mostFunction.setDescription(json.description);
        mostFunction.setFunctionType(functionType);
        mostFunction.setReleaseVersion(json.releaseVersion);
        mostFunction.setIsReleased(json.isReleased);
        mostFunction.setIsApproved(json.isApproved);
        mostFunction.setApprovalReviewId(json.approvalReviewId);
        mostFunction.setAuthor(author);
        mostFunction.setCompany(company);
        mostFunction.setStereotype(mostFunctionStereotype);
        mostFunction.setReturnParameterName(json.returnParameterName);
        mostFunction.setReturnParameterDescription(json.returnParameterDescription);
        mostFunction.setReturnType(returnType);
        mostFunction.setOperations(operations);
        mostFunction.setIsDeleted(json.isDeleted);
        mostFunction.setDeletedDate(json.deletedDate);

        if (functionType === "Property") {
            mostFunction.setSupportsNotification(json.supportsNotification);
        } else {
            const parameters = [];
            const parametersJson = json.inputParameters;

            for (let i in parametersJson) {
                const parameterJson = parametersJson[i];
                const parameter = Parameter.fromJson(parameterJson);
                parameters.push(parameter);
            }
            mostFunction.setParameters(parameters);
        }

        return mostFunction;
    }

    static toJson(mostFunction) {
        const jsonMostFunction = {
            id:                         mostFunction.getId(),
            mostId:                     formatHex(mostFunction.getMostId()),
            name:                       mostFunction.getName(),
            description:                mostFunction.getDescription(),
            releaseVersion:             mostFunction.getReleaseVersion(),
            isReleased:                 mostFunction.isReleased(),
            isApproved:                 mostFunction.isApproved(),
            functionType:               mostFunction.getFunctionType(),
            supportsNotification:       mostFunction.getSupportsNotification(),
            returnParameterName:        mostFunction.getReturnParameterName(),
            returnParameterDescription: mostFunction.getReturnParameterDescription()
        };

        const returnType = (mostFunction.getReturnType() || new MostType());
        jsonMostFunction.returnTypeId = returnType.getId();
        jsonMostFunction.returnTypeName = returnType.getName();

        const stereotype = (mostFunction.getStereotype() || new MostFunctionStereotype());
        jsonMostFunction.stereotypeId = stereotype.getId();
        jsonMostFunction.stereotypeName = stereotype.getName();

        const author = (mostFunction.getAuthor() || new Author());
        const company = (mostFunction.getCompany() || new Company());
        if (author.getId() > 0) {
            jsonMostFunction.authorId = author.getId();
        }
        if (company.getId() > 0) {
            jsonMostFunction.companyId = company.getId();
        }

        // Jsonify parameters array
        const parameters = mostFunction.getParameters();
        const parametersJson = [];
        for (let i in parameters) {
            const parameterJson = Parameter.toJson(parameters[i]);
            parametersJson.push(parameterJson);
        }
        jsonMostFunction.inputParameters = parametersJson;

        // Jsonify operations array
        const operations = mostFunction.getOperations();
        const operationsJson = [];
        for (let i in operations) {
            const operationJson = Operation.toJson(operations[i]);
            operationsJson.push(operationJson);
        }
        jsonMostFunction.operations = operationsJson;

        return jsonMostFunction;
    }

    constructor() {
      this._id                    = null;
      this._mostId                = "";
      this._name                  = "";
      this._description           = "";
      this._functionType          = "";
      this._releaseVersion        = "";
      this._author                = null;
      this._company               = null;
      this._isReleased            = null;
      this._isApproved            = null;
      this._approvalReviewId      = null;
      this._isDeleted             = false;
      this._deletedDate           = null;

      this._stereotype                  = null;
      this._returnParameterName         = null;
      this._returnParameterDescription  = null;
      this._returnType                  = null;
      this._supportsNotification        = false;

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

    setStereotype(stereotype) {
        this._stereotype = stereotype;
    }

    getStereotype() {
        return this._stereotype;
    }

    setReturnParameterName(returnParameterName) {
        this._returnParameterName = returnParameterName;
    }

    getReturnParameterName() {
        return this._returnParameterName;
    }

    setReturnParameterDescription(returnParameterDescription) {
        this._returnParameterDescription = returnParameterDescription;
    }

    getReturnParameterDescription() {
        return this._returnParameterDescription;
    }

    setReturnType(returnType) {
        this._returnType = returnType;
    }

    getReturnType() {
        return this._returnType;
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

    setIsReleased(isReleased) {
        this._isReleased = isReleased;
    }

    isReleased() {
        return this._isReleased;
    }

    setIsApproved(isApproved) {
        this._isApproved = isApproved;
    }

    isApproved() {
        return this._isApproved;
    }

    setApprovalReviewId(approvalReviewId) {
        this._approvalReviewId = approvalReviewId;
    }

    getApprovalReviewId() {
        return this._approvalReviewId;
    }

    setIsDeleted(isDeleted) {
        this._isDeleted = isDeleted;
    }

    isDeleted() {
        return this._isDeleted;
    }

    setDeletedDate(deletedDate) {
        this._deletedDate = deletedDate;
    }

    getDeletedDate() {
        return this._deletedDate;
    }

}