class MostInterface {
    static fromJson(json) {
        const mostInterface = new MostInterface();

        mostInterface.setId(json.id);
        mostInterface.setMostId(json.mostId);
        mostInterface.setName(json.name);
        mostInterface.setDescription(json.description);
        mostInterface.setLastModifiedDate(json.lastModifiedDate);
        mostInterface.setReleaseVersion(json.releaseVersion);
        mostInterface.setBaseVersionId(json.baseVersionId);
        mostInterface.setPriorVersionId(json.priorVersionId);
        mostInterface.setIsReleased(json.isReleased);
        mostInterface.setIsApproved(json.isApproved);
        mostInterface.setApprovalReviewId(json.approvalReviewId);
        mostInterface.setHasApprovedParent(json.hasApprovedParent);
        mostInterface.setCreatorAccountId(json.creatorAccountId);
        mostInterface.setIsDeleted(json.isDeleted);
        mostInterface.setDeletedDate(json.deletedDate);

        return mostInterface;
    }

    static toJson(mostInterface) {
        return {
            id:                 mostInterface.getId(),
            mostId:             mostInterface.getMostId(),
            name:               mostInterface.getName(),
            description:        mostInterface.getDescription(),
            lastModifiedDate:   mostInterface.getLastModifiedDate(),
            releaseVersion:     mostInterface.getReleaseVersion(),
            baseVersionId:      mostInterface.getBaseVersionId(),
            priorVersionId:     mostInterface.getPriorVersionId(),
            isReleased:         mostInterface.isReleased(),
            isApproved:         mostInterface.isApproved(),
            creatorAccountId:   mostInterface.getCreatorAccountId()
        };
    }

    constructor() {
        this._id                    = null;
        this._mostId                = null;
        this._name                  = "";
        this._description           = "";
        this._lastModifiedDate      = "";
        this._version               = "";
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

        this._functions             = [];
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

    setReleaseVersion(version) {
        this._version = version;
    }

    getReleaseVersion() {
        return this._version;
    }

    getFunctions() {
        return this._functions;
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
            return this._version;
        }
        return this._version + "-" + this._id;
    }
}
