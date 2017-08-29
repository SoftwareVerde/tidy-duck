class Review {
    static fromJson(json) {
        let functionCatalog = null;
        let functionBlock = null;
        let mostInterface = null;
        let mostFunction = null;
        let account = null;

        if (json.functionCatalogId) {
            getFunctionCatalog(json.functionCatalogId, function (newFunctionCatalog) {
                functionCatalog = FunctionCatalog.fromJson(newFunctionCatalog);
            });
        }
        if (json.functionBlockId) {
            getFunctionBlock(json.functionBlockId, function (newFunctionBlock) {
                functionBlock = FunctionBlock.fromJson(newFunctionBlock);
            });
        }
        if (json.mostInterfaceId) {
            getMostInterface(json.mostInterfaceId, function (newMostInterface) {
                mostInterface = MostInterface.fromJson(newMostInterface);
            });
        }
        if (json.mostFunctionId) {
            getMostFunction(json.mostFunctionId, function (newMostFunction) {
                mostFunction = MostFunction.fromJson(newMostFunction);
            });
        }
        getAccount(json.accountId, function (newAccount) {
            account = Account.fromJson(newAccount);
        });

        const review = new Review();
        review.setId(json.id);
        review.setFunctionCatalog(functionCatalog);
        review.setFunctionBlock(functionBlock);
        review.setMostInterface(mostInterface);
        review.setMostFunction(mostFunction);
        review.setAccount(account);
        review.setCreatedDate(json.createdDate);
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

    getReviewName() {
        const reviewObject = this._functionCatalog || this._functionBlock || this._mostInterface || this._mostFunction;
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