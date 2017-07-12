class MostFunction {
    static fromJson(json) {
        const mostFunction = new MostFunction();

        const author = new Author();
        author.setId(json.authorId);
        author.setName(json.authorName);

        const company = new Company();
        company.setId(json.companyId);
        company.setName(json.companyName);

        mostFunction.setId(json.id);
        mostFunction.setMostId(json.mostId);
        mostFunction.setName(json.name);
        mostFunction.setDescription(json.description);
        mostFunction.setLastModifiedDate(json.lastModifiedDate);
        mostFunction.setReleaseVersion(json.releaseVersion);
        mostFunction.setAuthor(author);
        mostFunction.setCompany(company);
        mostFunction.setStereotypeId(json.stereotypeId);
        mostFunction.setStereotypeName(json.stereotypeName);
        mostFunction.setReturnTypeId(json.returnTypeId);
        mostFunction.setReturnTypeName(json.returnTypeName);
        mostFunction.setSupportsNotification(json.supportsNotification);
        mostFunction.setParameters(json.parameters);

        return mostFunction;
    }

    static toJson(mostFunction) {
        const jsonMostFunction = {
            id:                     mostFunction.getId(),
            mostId:                 mostFunction.getMostId(),
            name:                   mostFunction.getName(),
            description:            mostFunction.getDescription(),
            lastModifiedDate:       mostFunction.getLastModifiedDate(),
            releaseVersion:         mostFunction.getReleaseVersion(),
            stereotypeId:           mostFunction.getStereotypeId(),
            stereotypeName:         mostFunction.getStereotypeName(),
            returnTypeId:           mostFunction.getReturnTypeId(),
            returnTypeName:         mostFunction.getReturnTypeName(),
            supportsNotification:   mostFunction.getSupportsNotification(),
            operations:             mostFunction.getOperations(),
            parameters:             mostFunction.getParameters()
        };

        const author = (mostFunction.getAuthor() || new Author());
        const company = (mostFunction.getCompany() || new Company());
        if (author.getId() > 0) {
            jsonMostFunction.authorId = author.getId();
        }
        if (company.getId() > 0) {
            jsonMostFunction.companyId = company.getId();
        }
        return jsonMostFunction;
    }

    constructor() {
      this._id                    = null;
      this._mostId                = null;
      this._name                  = "";
      this._description           = "";
      this._lastModifiedDate      = "";
      this._releaseVersion        = "";
      this._author                = null;
      this._company               = null;

      this._stereotypeId          = null;
      this._stereotypeName        = "";
      this._returnTypeId          = "";
      this._returnTypeName        = "";
      this._supportsNotification  = null;

      this._parameters            = null;
      this._operations            = null;
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

    setLastModifiedDate(lastModifiedDate) {
        this._lastModifiedDate = lastModifiedDate;
    }

    getLastModifiedDate() {
        return this._lastModifiedDate;
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

    getOperations() {
        return this._operations;
    }

}