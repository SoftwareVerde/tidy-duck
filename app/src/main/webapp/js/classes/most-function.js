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
        mostFunction.setStereotype(json.stereotype);
        mostFunction.setReturnType(json.returnType);
        mostFunction.setParameters(json.parameters);

        return mostFunction;
    }

    static toJson(mostFunction) {
        const jsonMostFunction = {
            id:                 mostFunction.getId(),
            mostId:             mostFunction.getMostId(),
            name:               mostFunction.getName(),
            description:        mostFunction.getDescription(),
            lastModifiedDate:   mostFunction.getLastModifiedDate(),
            releaseVersion:     mostFunction.getReleaseVersion(),
            stereotype:         mostFunction.getStereotype(),
            returnType:         mostFunction.getReturnType(),
            parameters:         mostFunction.getParameters()
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

      this._stereotype            = "";
      this._returnType            = "";
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

    setStereotype(stereotype) {
        this._stereotype = stereotype;
    }

    getStereotype() {
        return this._stereotype;
    }

    setReturnType(returnType) {
        this._returnType = returnType;
    }

    getReturnType() {
        return this._returnType;
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