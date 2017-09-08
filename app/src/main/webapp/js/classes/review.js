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

        const ticketUrl = json.ticketUrl;

        const reviewVotes = [];
        const reviewVotesJson = json.reviewVotes;
        for (let i in reviewVotesJson) {
            const reviewVoteJson = reviewVotesJson[i];
            const reviewVote = ReviewVote.fromJson(reviewVoteJson);
            reviewVotes.push(reviewVote);
        }

        const reviewComments = [];
        const reviewCommentsJson = json.reviewComments;
        for (let i in reviewCommentsJson) {
            const reviewCommentJson = reviewCommentsJson[i];
            const reviewComment = ReviewComment.fromJson(reviewCommentJson);
            reviewComments.push(reviewComment);
        }

        const review = new Review();

        review.setId(json.id);
        review.setFunctionCatalog(functionCatalog);
        review.setFunctionBlock(functionBlock);
        review.setMostInterface(mostInterface);
        review.setMostFunction(mostFunction);
        review.setCreatedDate(json.createdDate);
        review.setAccount(account);
        review.setTicketUrl(ticketUrl);
        review.setReviewVotes(reviewVotes);
        review.setReviewComments(reviewComments);

        return review;
    }

    static toJson(review) {
        const id = review.getId();
        const functionCatalogId = review.getFunctionCatalog() ? review.getFunctionCatalog().getId() : null;
        const functionBlockId = review.getFunctionBlock() ? review.getFunctionBlock().getId() : null;
        const mostInterfaceId = review.getMostInterface() ? review.getMostInterface().getId() : null;
        const mostFunctionId = review.getMostFunction() ? review.getMostFunction().getId() : null;
        const accountId = review.getAccount().getId();
        const ticketUrl = review.getTicketUrl();
        const createdDate = review.getCreatedDate();

        return {
            id:                 id,
            functionCatalogId:  functionCatalogId,
            functionBlockId:    functionBlockId,
            mostInterfaceId:    mostInterfaceId,
            mostFunctionId:     mostFunctionId,
            accountId:          accountId,
            ticketUrl:          ticketUrl,
            createdDate:        createdDate
        };
    }

    constructor() {
        this._id = null;
        this._functionCatalog = null;
        this._functionBlock = null;
        this._mostInterface = null;
        this._mostFunction = null;
        this._account = null;
        this._ticketUrl = null;
        this._createdDate = null;
        this._reviewVotes = [];
        this._reviewComments = [];
    }

    getReviewObject() {
        return this._functionCatalog || this._functionBlock || this._mostInterface || this._mostFunction;
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

    setTicketUrl(ticketUrl) {
        this._ticketUrl = ticketUrl;
    }

    getTicketUrl() {
        return this._ticketUrl;
    }

    setCreatedDate(createdDate) {
        this._createdDate = createdDate;
    }

    getCreatedDate() {
        return this._createdDate;
    }

    setReviewVotes(reviewVotes) {
        this._reviewVotes = reviewVotes;
    }

    addReviewVote(reviewVote) {
        this._reviewVotes.push(reviewVote);
    }

    getReviewVotes() {
        return this._reviewVotes;
    }

    setReviewComments(reviewComments) {
        this._reviewComments = reviewComments;
    }

    addReviewComment(reviewComment) {
        this._reviewComments.push(reviewComment);
    }

    getReviewComments() {
        return this._reviewComments;
    }
}