package com.softwareverde.tidyduck;

import java.util.Date;

public class ReviewVote {
    private Long _id;
    private Long _reviewId;
    private Account _account;
    private Date _createdDate;
    private boolean _isUpvote;

    public Long getId() {
        return _id;
    }

    public void setId(final Long id) {
        _id = id;
    }

    public Long getReviewId() { return _reviewId; }

    public void setReviewId(final Long reviewId) { _reviewId = reviewId; }

    public Account getAccount() {
        return _account;
    }

    public void setAccount(final Account account) {
        _account = account;
    }

    public Date getCreatedDate() {
        return _createdDate;
    }

    public void setCreatedDate(final Date createdDate) {
        _createdDate = createdDate;
    }

    public boolean isUpvote() { return _isUpvote; }

    public void setIsUpvote(final boolean isUpvote) { _isUpvote = isUpvote; }
}
