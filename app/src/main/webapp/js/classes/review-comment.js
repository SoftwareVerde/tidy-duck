class ReviewComment {
    static fromJson(json) {
        const reviewCommentAccount = new Account();
        reviewCommentAccount.setId(json.accountId);

        const reviewComment = new ReviewComment();

        reviewComment.setId(json.id);
        reviewComment.setAccount(reviewCommentAccount);
        reviewComment.setCreatedDate(json.createdDate);
        reviewComment.setCommentText(json.commentText);

        return reviewComment;
    }

    static toJson(reviewComment) {
        const id = reviewComment.getId();
        const accountId = reviewComment.getAccount().getId();
        const createdDate = reviewComment.getCreatedDate();
        const commentText = reviewComment.getCommentText();

        return {
            id:             id,
            accountId:      accountId,
            createdDate:    createdDate,
            commentText:    commentText
        };
    }

    constructor() {
        this._id    = null;
        this._account = null;
        this._createdDate = null;
        this._commentText = null;
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

    setCommentText(commentText) {
        this._commentText = commentText;
    }

    getCommentText() {
        return this._commentText;
    }
}