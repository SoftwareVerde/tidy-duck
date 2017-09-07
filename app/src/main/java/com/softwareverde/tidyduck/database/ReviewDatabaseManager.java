package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.Query;
import com.softwareverde.tidyduck.Account;
import com.softwareverde.tidyduck.Review;
import com.softwareverde.tidyduck.ReviewComment;
import com.softwareverde.tidyduck.ReviewVote;
import com.softwareverde.tidyduck.most.FunctionBlock;
import com.softwareverde.tidyduck.most.FunctionCatalog;
import com.softwareverde.tidyduck.most.MostFunction;
import com.softwareverde.tidyduck.most.MostInterface;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ReviewDatabaseManager {
    private final DatabaseConnection _databaseConnection;

    public ReviewDatabaseManager(final DatabaseConnection databaseConnection) {
        _databaseConnection = databaseConnection;
    }

    public void insertReview(final Review review) throws DatabaseException {
        final FunctionCatalog functionCatalog = review.getFunctionCatalog();
        final FunctionBlock functionBlock = review.getFunctionBlock();
        final MostInterface mostInterface = review.getMostInterface();
        final MostFunction mostFunction = review.getMostFunction();
        final Account account = review.getAccount();
        final String ticketUrl = review.getTicketUrl();
        final Date createdDate = review.getCreatedDate();
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final String createdDateString = simpleDateFormat.format(createdDate);

        Long functionCatalogId = null;
        Long functionBlockId = null;
        Long mostInterfaceId = null;
        Long mostFunctionId = null;
        final long accountId = account.getId();

        if (functionCatalog != null) {
            functionCatalogId = functionCatalog.getId();
        }
        else if (functionBlock != null) {
            functionBlockId = functionBlock.getId();
        }
        else if (mostInterface != null) {
            mostInterfaceId = mostInterface.getId();
        }
        else if (mostFunction != null) {
            mostFunctionId = mostFunction.getId();
        }

        final Query query = new Query("INSERT INTO reviews (function_catalog_id, function_block_id, interface_id, function_id, account_id, ticket_url, created_date) VALUES (?, ?, ?, ?, ?, ?, ?)")
                .setParameter(functionCatalogId)
                .setParameter(functionBlockId)
                .setParameter(mostInterfaceId)
                .setParameter(mostFunctionId)
                .setParameter(accountId)
                .setParameter(ticketUrl)
                .setParameter(createdDateString)
        ;

        final long reviewId = _databaseConnection.executeSql(query);
        review.setId(reviewId);
    }

    public void approveReview(final Review review) throws DatabaseException {
        final FunctionCatalog functionCatalog = review.getFunctionCatalog();
        final FunctionBlock functionBlock = review.getFunctionBlock();
        final MostInterface mostInterface = review.getMostInterface();
        final MostFunction mostFunction = review.getMostFunction();

        Long functionCatalogId = null;
        Long functionBlockId = null;
        Long mostInterfaceId = null;
        Long mostFunctionId = null;

        if (functionCatalog != null) {
            functionCatalogId = functionCatalog.getId();
            FunctionCatalogDatabaseManager functionCatalogDatabaseManager = new FunctionCatalogDatabaseManager(_databaseConnection);
            functionCatalogDatabaseManager.approveFunctionCatalog(functionCatalogId);
        }
        else if (functionBlock != null) {
            functionBlockId = functionBlock.getId();
            FunctionBlockDatabaseManager functionBlockDatabaseManager = new FunctionBlockDatabaseManager(_databaseConnection);
            functionBlockDatabaseManager.approveFunctionBlock(functionBlockId);
        }
        else if (mostInterface != null) {
            mostInterfaceId = mostInterface.getId();
            MostInterfaceDatabaseManager mostInterfaceDatabaseManager = new MostInterfaceDatabaseManager(_databaseConnection);
            mostInterfaceDatabaseManager.approveMostInterface(mostInterfaceId);
        }
        else if (mostFunction != null) {
            mostFunctionId = mostFunction.getId();
            MostFunctionDatabaseManager mostFunctionDatabaseManager = new MostFunctionDatabaseManager(_databaseConnection);
            mostFunctionDatabaseManager.approveMostFunction(mostFunctionId);
        }
    }

    public void insertReviewVote(final ReviewVote reviewVote, final long reviewId) throws DatabaseException {
        final long accountId = reviewVote.getAccount().getId();
        final boolean isUpvote = reviewVote.isUpvote();

        final Query query = new Query("INSERT INTO review_votes (review_id, account_id, created_date, is_upvote) VALUES (?, ?, NOW(), ?)")
                .setParameter(reviewId)
                .setParameter(accountId)
                .setParameter(isUpvote)
        ;
        final long reviewVoteId = _databaseConnection.executeSql(query);

        reviewVote.setId(reviewVoteId);
    }

    public void updateReviewVote(final ReviewVote reviewVote) throws DatabaseException {
        final long reviewVoteId = reviewVote.getId();
        final boolean isUpvote = reviewVote.isUpvote();

        final Query query = new Query("UPDATE review_votes SET is_upvote = ?, created_date = NOW() WHERE id = ?")
                .setParameter(isUpvote)
                .setParameter(reviewVoteId)
        ;

        _databaseConnection.executeSql(query);
    }

    public void insertReviewComment(final ReviewComment reviewComment, final long reviewId) throws DatabaseException {
        final long accountId = reviewComment.getAccount().getId();
        final String commentText = reviewComment.getCommentText();

        final Query query = new Query("INSERT INTO review_comments (review_id, account_id, created_date, comment) VALUES (?, ?, NOW(), ?)");
        query.setParameter(reviewId);
        query.setParameter(accountId);
        query.setParameter(commentText);

        final long reviewCommentId = _databaseConnection.executeSql(query);

        reviewComment.setId(reviewCommentId);
    }

    public void deleteReview(final Review review) throws DatabaseException {
        final long reviewId = review.getId();
        final List<ReviewVote> reviewVotes = review.getReviewVotes();
        final List<ReviewComment> reviewComments = review.getReviewComments();

        for (ReviewVote reviewVote : reviewVotes) {
            final long reviewVoteId = reviewVote.getId();
            deleteReviewVote(reviewVoteId);
        }

        for (ReviewComment reviewComment : reviewComments) {
            final long reviewCommentId = reviewComment.getId();
            deleteReviewComment(reviewCommentId);
        }

        final Query query = new Query("DELETE FROM reviews WHERE id = ?")
                .setParameter(reviewId)
                ;

        _databaseConnection.executeSql(query);
    }

    public void deleteReviewVote(final long reviewVoteId) throws DatabaseException {
        final Query query = new Query("DELETE FROM review_votes WHERE id = ?")
                .setParameter(reviewVoteId)
                ;

        _databaseConnection.executeSql(query);
    }

    public void deleteReviewComment(final long reviewCommentId) throws DatabaseException {
        final Query query = new Query("DELETE FROM review_comments WHERE id = ?")
                .setParameter(reviewCommentId)
                ;

        _databaseConnection.executeSql(query);
    }
}
