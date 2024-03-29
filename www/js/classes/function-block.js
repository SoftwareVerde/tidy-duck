class FunctionBlock {
    static fromJson(json) {
        const functionBlock = new FunctionBlock();

        const author = new Author();
        author.setId(json.authorId);
        author.setName(json.authorName);

        const company = new Company();
        company.setId(json.companyId);
        company.setName(json.companyName);

        functionBlock.setId(json.id);
        functionBlock.setMostId(json.mostId);
        functionBlock.setKind(json.kind);
        functionBlock.setName(json.name);
        functionBlock.setDescription(json.description);
        functionBlock.setLastModifiedDate(json.lastModifiedDate);
        functionBlock.setReleaseVersion(json.releaseVersion);
        functionBlock.setBaseVersionId(json.baseVersionId);
        functionBlock.setPriorVersionId(json.priorVersionId);
        functionBlock.setIsReleased(json.isReleased);
        functionBlock.setIsApproved(json.isApproved);
        functionBlock.setApprovalReviewId(json.approvalReviewId);
        functionBlock.setHasApprovedParent(json.hasApprovedParent);
        functionBlock.setAuthor(author);
        functionBlock.setCompany(company);
        functionBlock.setCreatorAccountId(json.creatorAccountId);
        functionBlock.setAccess(json.access);
        functionBlock.setIsSource(json.isSource);
        functionBlock.setIsSink(json.isSink);
        functionBlock.setIsDeleted(json.isDeleted);
        functionBlock.setDeletedDate(json.deletedDate);

        return functionBlock;
    }

    static toJson(functionBlock) {
        const jsonFunctionBlock = {
            id:                 functionBlock.getId(),
            mostId:             formatHex(functionBlock.getMostId()),
            kind:               functionBlock.getKind(),
            name:               functionBlock.getName(),
            description:        functionBlock.getDescription(),
            lastModifiedDate:   functionBlock.getLastModifiedDate(),
            releaseVersion:     functionBlock.getReleaseVersion(),
            baseVersionId:      functionBlock.getBaseVersionId(),
            priorVersionId:     functionBlock.getPriorVersionId(),
            creatorAccountId:   functionBlock.getCreatorAccountId(),
            isReleased:         functionBlock.isReleased(),
            isApproved:         functionBlock.isApproved(),
            access:             functionBlock.getAccess(),
            isSource:           functionBlock.isSource(),
            isSink:             functionBlock.isSink(),
        };
        const author = (functionBlock.getAuthor() || new Author());
        const company = (functionBlock.getCompany() || new Company());
        if (author.getId() > 0) {
            jsonFunctionBlock.authorId = author.getId();
        }
        if (company.getId() > 0) {
            jsonFunctionBlock.companyId = company.getId();
        }
        return jsonFunctionBlock;
    }

    constructor() {
        this._id                    = null;
        this._mostId                = null;
        this._kind                  = "";
        this._name                  = "";
        this._description           = "";
        this._lastModifiedDate      = "";
        this._releaseVersion        = "";
        this._author                = null;
        this._company               = null;
        this._access                = "";
        this._isSource              = false;
        this._isSink                = false;
        this._versionsJson          = null;
        this._isReleased            = null;
        this._isApproved            = null;
        this._approvalReviewId      = null;
        this._hasApprovedParent     = false;
        this._priorVersionId        = null;
        this._baseVersionId         = null;
        this._creatorAccountId      = null;
        this._isDeleted             = false;
        this._deletedDate           = null;

        this._interfaces            = [];
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

    setKind(kind) {
        this._kind = kind;
    }

    getKind() {
        return this._kind;
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

    getInterfaces() {
        return this._interfaces;
    }

    setIsSource(isSource) {
        this._isSource = isSource;
    }

    isSource() {
        return this._isSource;
    }

    setIsSink(isSink) {
        this._isSink = isSink;
    }

    isSink() {
        return this._isSink;
    }

    setAccess(access) {
        this._access = access;
    }

    getAccess() {
        return this._access;
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

    setApprovalReviewId(approvalReviewId) {
        this._approvalReviewId = approvalReviewId;
    }

    getApprovalReviewId() {
        return this._approvalReviewId;
    }

    setHasApprovedParent(hasApprovedParent) {
        this._hasApprovedParent = hasApprovedParent;
    }

    hasApprovedParent() {
        return this._hasApprovedParent;
    }

    setCreatorAccountId(creatorAccountId) {
        this._creatorAccountId = creatorAccountId;
    }

    getCreatorAccountId() {
        return this._creatorAccountId;
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
