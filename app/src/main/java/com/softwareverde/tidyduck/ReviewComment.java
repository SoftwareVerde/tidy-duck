package com.softwareverde.tidyduck;

import java.util.Date;

public class ReviewComment {
    private Long _id;
    private Account _account;
    private Date _createdDate;
    private String _commentText;

    public Long getId() {
        return _id;
    }

    public void setId(final Long id) {
        _id = id;
    }

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

    public String getCommentText() {
        return _commentText;
    }

    public void setCommentText(final String commentText) {
        _commentText = commentText;
    }
}
