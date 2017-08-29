class Review {
    static fromJson(json) {
        let functionCatalog = null;
        let functionBlock = null;
        let mostInterface = null;
        let mostFunction = null;

        if (json.functionCatalogId) {
            functionCatalog = new FunctionCatalog();
            functionCatalog.setId(json.functionCatalogId);
        }
        if (json.functionBlockId) {
            functionBlock = new FunctionBlock();
            functionBlock.setId(json.functionBlockId);
        }
        if (json.mostInterfaceId) {
            mostInterface = new MostInterface();
            mostInterface.setId(json.mostInterfaceId);
        }
        if (json.mostFunctionId) {
            mostFunction = new MostFunction();
            mostFunction.setId(json.mostFunctionId);
        }

        const account = new Account();
        account.setId(json.accountId);

        const review = new Review();

        review.setId(json.id);
        review.setFunctionCatalog(functionCatalog);
        review.setFunctionBlock(functionBlock);
        review.setMostInterface(mostInterface);
        review.setMostFunction(mostFunction);
        review.setCreatedDate(json.createdDate);
        review.setAccount(account);

        return review;
    }

    static toJson(review) {
        const functionCatalogId = this._functionCatalog ? this._functionCatalog.getId() : null;
        const functionBlockId = this._functionBlock ? this._functionBlock.getId() : null;
        const mostInterfaceId = this._mostInterface ? this._mostInterface.getId() : null;
        const mostFunctionId = this._mostFunction ? this._mostFunction.getId() : null;
        const accountId = this._account.getId();

        return {
            id:                 this._id,
            functionCatalogId:  functionCatalogId,
            functionBlockId:    functionBlockId,
            mostInterfaceId:    mostInterfaceId,
            mostFunctionId:     mostFunctionId,
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

    getReviewObject() {
        return this._functionCatalog || this._functionBlock || this._mostInterface || this._mostFunction
    }

    getReviewName() {
        const reviewObject = this.getReviewObject();
        return reviewObject.getName();
    }

    setId(id) {
        this._id = id;
    }

    getId() {
        return this._id;
    }

    setFunctionCatalog(functionCatalog) {
        this._functionCatalog = functionCatalog;
    }

    getFunctionCatalog() {
        return this._functionCatalog;
    }

    setFunctionBlock(functionBlock) {
        this._functionBlock = functionBlock;
    }

    getFunctionBlock() {
        return this._functionBlock;
    }

    setMostInterface(mostInterface) {
        this._mostInterface = mostInterface;
    }

    getMostInterface() {
        return this._mostInterface;
    }

    setMostFunction(mostFunction) {
        this._mostFunction = mostFunction;
    }

    getMostFunction() {
        return this._mostFunction;
    }

    setAccount(account) {
        this._account = account;
    }

    getAccount() {
        return this._account;
    }

    setCreatedDate(createdDate) {
        this._createdDate = createdDate;
    }

    getCreatedDate() {
        return this._createdDate;
    }
}