class ReviewComment {
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