class FunctionCatalog {
    static fromJson(json) {
        const functionCatalog = new FunctionCatalog();

        const author = new Author();
        author.setId(json.authorId);
        author.setName(json.authorName);

        const company = new Company();
        company.setId(json.companyId);
        company.setName(json.companyName);

        functionCatalog.setId(json.id);
        functionCatalog.setName(json.name);
        functionCatalog.setReleaseVersion(json.releaseVersion);
        functionCatalog.setBaseVersionId(json.baseVersionId);
        functionCatalog.setPriorVersionId(json.priorVersionId);
        functionCatalog.setIsReleased(json.isReleased);
        functionCatalog.setIsApproved(json.isApproved);
        functionCatalog.setApprovalReviewId(json.approvalReviewId);
        functionCatalog.setAuthor(author);
        functionCatalog.setCompany(company);
        functionCatalog.setCreatorAccountId(json.creatorAccountId);
        functionCatalog.setIsDeleted(json.isDeleted);
        functionCatalog.setDeletedDate(json.deletedDate);

        return functionCatalog;
    }

    // Converts existing function catalog into JSON
    static toJson(functionCatalog) {
        const jsonFunctionCatalog = {
            id:                 functionCatalog.getId(),
            name:               functionCatalog.getName(),
            releaseVersion:     functionCatalog.getReleaseVersion(),
            baseVersionId:      functionCatalog.getBaseVersionId(),
            priorVersionId:     functionCatalog.getPriorVersionId(),
            isReleased:         functionCatalog.isReleased(),
            isApproved:         functionCatalog.isApproved(),
            creatorAccountId:          functionCatalog.getCreatorAccountId()
        };
        const author = (functionCatalog.getAuthor() || new Author());
        const company = (functionCatalog.getCompany() || new Company());
        if (author.getId() > 0) {
            jsonFunctionCatalog.authorId = author.getId();
        }
        if (company.getId() > 0) {
            jsonFunctionCatalog.companyId = company.getId();
        }
        return jsonFunctionCatalog;
    }

    constructor() {
        this._id                = null;
        this._name              = "";
        this._releaseVersion    = "";
        this._versionsJson      = null;
        this._isReleased        = null;
        this._isApproved        = null;
        this._priorVersionId    = null;
        this._baseVersionId     = null;
        this._author            = null;
        this._company           = null;
        this._creatorAccountId  = null;
        this._approvalReviewId  = null;
        this._isDeleted         = false;
        this._deletedDate       = null;

        this._functionBlocks    = [];
    };

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

    getFunctionBlocks() {
        return this._functionBlocks;
    }

    setVersionsJson(versionsJson) {
        this._versionsJson = versionsJson;
    }

    getVersionsJson() {
        return this._versionsJson;
    }

    setBaseVersionId(baseVersionId) {
        this._baseVersionId = baseVersionId;
    }

    getBaseVersionId() {
        return this._baseVersionId;
    }

    setPriorVersionId(priorVersionId) {
        this._priorVersionId = priorVersionId;
    }

    getPriorVersionId() {
        return this._priorVersionId;
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

    setCreatorAccountId(creatorAccountId) {
        this._creatorAccountId = creatorAccountId;
    }

    getCreatorAccountId() {
        return this._creatorAccountId;
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

    getDisplayVersion() {
        if (this._isReleased) {
            return this._releaseVersion;
        }
        return this._releaseVersion + "-" + this._id;
    }
}
