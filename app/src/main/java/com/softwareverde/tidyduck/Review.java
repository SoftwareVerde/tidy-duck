package com.softwareverde.tidyduck;

import com.softwareverde.tidyduck.most.FunctionBlock;
import com.softwareverde.tidyduck.most.FunctionCatalog;
import com.softwareverde.tidyduck.most.MostFunction;
import com.softwareverde.tidyduck.most.MostInterface;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Review {
    private Long _id;
    private FunctionCatalog _functionCatalog = null;
    private FunctionBlock _functionBlock = null;
    private MostInterface _mostInterface = null;
    private MostFunction _mostFunction = null;
    private Account _account;
    private String ticketUrl;
    private Date _createdDate;
    private List<ReviewVote> _reviewVotes = new ArrayList<>();
    private List<ReviewComment> _reviewComments = new ArrayList<>();

    public Long getId() {
        return _id;
    }

    public void setId(final Long id) {
        _id = id;
    }

    public FunctionCatalog getFunctionCatalog() {
        return _functionCatalog;
    }

    public void setFunctionCatalog(final FunctionCatalog functionCatalog) {
        _functionCatalog = functionCatalog;
    }

    public FunctionBlock getFunctionBlock() {
        return _functionBlock;
    }

    public void setFunctionBlock(final FunctionBlock functionBlock) {
        _functionBlock = functionBlock;
    }

    public MostInterface getMostInterface() {
        return _mostInterface;
    }

    public void setMostInterface(final MostInterface mostInterface) {
        _mostInterface = mostInterface;
    }

    public MostFunction getMostFunction() {
        return _mostFunction;
    }

    public void setMostFunction(final MostFunction mostFunction) {
        _mostFunction = mostFunction;
    }

    public Account getAccount() {
        return _account;
    }

    public void setAccount(final Account account) {
        _account = account;
    }

    public String getTicketUrl() {
        return ticketUrl;
    }

    public void setTicketUrl(final String ticketUrl) {
        this.ticketUrl = ticketUrl;
    }

    public Date getCreatedDate() {
        return _createdDate;
    }

    public void setCreatedDate(final Date createdDate) {
        _createdDate = createdDate;
    }

    public List<ReviewVote> getReviewVotes() {
        return new ArrayList<>(_reviewVotes);
    }

    public void addReviewVote(final ReviewVote reviewVote) {
        _reviewVotes.add(reviewVote);
    }

    public void setReviewVotes(final List<ReviewVote> reviewVotes) {
        _reviewVotes = new ArrayList<>(reviewVotes);
    }

    public List<ReviewComment> getReviewComments() {
        return new ArrayList<>(_reviewComments);
    }

    public void addReviewComment(final ReviewComment reviewComment) {
        _reviewComments.add(reviewComment);
    }

    public void setReviewComments(final List<ReviewComment> reviewComments) {
        _reviewComments = new ArrayList<>(reviewComments);
    }
}
