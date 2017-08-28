class Review {
    static fromJson(json) {
        this._id = json.id;
        this._functionCatalog = json.functionCatalogId;
        this._functionBlock = json.functionBlockId;
        this._mostInterface = json.mostInterfaceId;
        this._mostFunction = json.mostFunctionId;
        this._account = json.accountId;
        this._createdDate = json.createdDate;
    }

    static toJson(review) {
        const functionCatalogId = this._functionCatalog == null ? null : this._functionCatalog.getId();
        const functionBlockId = this._functionBlock == null ? null : this._functionBlock.getId();
        const mostInterfaceId = this._mostInterface == null ? null : this._mostInterface.getId();
        const mostFunctionId = this._mostFunction == null ? null : this._mostFunction.getId();
        const accountId = this._account.getId();

        return {
            id:                 this._id,
            functionCatalogId:  functionCatalogId,
            functionBlockId:    functionBlockId,
            mostInterfaceId:    mostInterface,
            mostFunctionId:     mostFunction,
            accountId:          accountId,
            createdDate:        this._createdDate
        };
    }

    constructor() {
        this._id = null;
        this._functionCatalog = null;
        this._functionBlock = null;
        this._mostInterface = null;
        this._mostFunction = null;
        this._account = null;
        this._createdDate = null;
    }
}