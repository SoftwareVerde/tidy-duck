class ReleaseItem {
    static fromJson(json) {
        const releaseItem = new ReleaseItem();

        releaseItem.setItemType(json.itemType);
        releaseItem.setItemId(json.itemId);
        releaseItem.setItemName(json.itemName);
        releaseItem.setItemVersion(json.itemVersion);
        releaseItem.setPredictedNextVersion(json.predictedNextVersion);

        return releaseItem;
    }

    static toJson(releaseItem) {
        return {
            itemType:       this._itemType,
            itemId:         this._itemId,
            itemName:       this._itemName,
            itemVersion:    this._itemVersion,
            newVersion:     this._newVersion
        };
    }

    constructor() {
        this._itemType = null;
        this._itemId = null;
        this._itemName = null;
        this._itemVersion = null;
        this._newVersion = null;
        this._predictedNextVersion = null;
    }

    setItemType(itemType) {
        this._itemType = itemType;
    }

    getItemType() {
        return this._itemType;
    }

    setItemId(itemId) {
        this._itemId = itemId;
    }

    getItemId() {
        return this._itemId;
    }

    setItemName(itemName) {
        this._itemName = itemName;
    }

    getItemName() {
        return this._itemName;
    }

    setItemVersion(itemVersion) {
        this._itemVersion = itemVersion;
    }

    getItemVersion() {
        return this._itemVersion;
    }

    setNewVersion(newVersion) {
        this._newVersion = newVersion;
    }

    getNewVersion() {
        return this._newVersion;
    }

    setPredictedNextVersion(predictedNextVersion) {
        this._predictedNextVersion = predictedNextVersion;
    }

    getPredictedNextVersion() {
        return this._predictedNextVersion;
    }
}