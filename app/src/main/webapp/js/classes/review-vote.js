class ReviewVote {
    static fromJson(json) {
        const reviewVoteAccount = new Account();
        reviewVoteAccount.setId(json.accountId);

        const reviewVote = new ReviewVote();
        reviewVote.setId(json.id);
        reviewVote.setAccount(reviewVoteAccount);
        reviewVote.setCreatedDate(json.createdDate);
        reviewVote.setIsUpvote(json.isUpvote);

        return reviewVote;
    }

    static toJson(reviewVote) {
        const id = reviewVote.getId();
        const accountId = reviewVote.getAccount().getId();
        const createdDate = reviewVote.getCreatedDate();
        const isUpvote = reviewVote.isUpvote();

        return {
            reviewId:       id,
            accountId:      accountId,
            createdDate:    createdDate,
            isUpvote:       isUpvote
        };
    }

    constructor() {
        this._id    = null;
        this._account = null;
        this._createdDate = null;
        this._isUpvote = null;
    }

    setId(id) {
        this._id = id;
    }

    getId() {
        return this._id;
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

    setIsUpvote(isUpvote) {
        this._isUpvote = isUpvote;
    }

    isUpvote() {
        return this._isUpvote;
    }
}