class ReviewVote {
    constructor() {
        this._id    = null;
        this._reviewId  = null;
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

    setReviewId(reviewId) {
        this._reviewId = reviewId;
    }

    getReviewId() {
        return this._reviewId;
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