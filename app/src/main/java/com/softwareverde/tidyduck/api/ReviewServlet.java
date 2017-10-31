package com.softwareverde.tidyduck.api;

import com.softwareverde.database.Database;
import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.json.Json;
import com.softwareverde.tidyduck.*;
import com.softwareverde.tidyduck.database.*;
import com.softwareverde.tidyduck.environment.Environment;
import com.softwareverde.tidyduck.most.FunctionBlock;
import com.softwareverde.tidyduck.most.FunctionCatalog;
import com.softwareverde.tidyduck.most.MostFunction;
import com.softwareverde.tidyduck.most.MostInterface;
import com.softwareverde.tidyduck.util.Util;
import com.softwareverde.tomcat.servlet.AuthenticatedJsonServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.Connection;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ReviewServlet extends AuthenticatedJsonServlet {
    private final Logger _logger = LoggerFactory.getLogger(this.getClass());

    public ReviewServlet() {
        super._defineEndpoint("reviews", HttpMethod.GET, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.REVIEWS_VIEW);

                final String excludeOpenReviewsString = request.getParameter("excludeOpenReviews");
                final String excludeClosedReviewsString = request.getParameter("excludeClosedReviews");

                final boolean excludeOpenReviews = Boolean.parseBoolean(excludeOpenReviewsString);
                final boolean excludeClosedReviews = Boolean.parseBoolean(excludeClosedReviewsString);

                return _listAllReviews(!excludeOpenReviews, !excludeClosedReviews, environment.getDatabase());
            }
        });

        super._defineEndpoint("reviews", HttpMethod.POST, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_MODIFY);

                return _insertReview(request, environment.getDatabase());
            }
        });

        super._defineEndpoint("reviews/<reviewId>", HttpMethod.POST, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_MODIFY);

                return _updateReview(request, environment.getDatabase());
            }
        });

        super._defineEndpoint("reviews/<reviewId>/votes", HttpMethod.POST, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.REVIEWS_VIEW);

                final long reviewId = Util.parseLong(parameters.get("reviewId"));
                if (reviewId < 1) {
                    return _generateErrorJson("Invalid review ID: " + reviewId);
                }
                return _insertReviewVote(request, reviewId, currentAccount, environment.getDatabase());
            }
        });

        super._defineEndpoint("reviews/<reviewId>/comments", HttpMethod.GET, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.REVIEWS_COMMENTS);

                final long reviewId = Util.parseLong(parameters.get("reviewId"));
                if (reviewId < 1) {
                    return _generateErrorJson("Invalid review id: " + reviewId);
                }
                return _listReviewComments(reviewId, environment.getDatabase());
            }
        });

        super._defineEndpoint("reviews/<reviewId>/comments", HttpMethod.POST, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.REVIEWS_COMMENTS);

                final long reviewId = Util.parseLong(parameters.get("reviewId"));
                if (reviewId < 1) {
                    return _generateErrorJson("Invalid review id: " + reviewId);
                }
                return _addReviewComment(request, reviewId, currentAccount, environment.getDatabase());
            }
        });

        super._defineEndpoint("reviews/<reviewId>/approve", HttpMethod.POST, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.REVIEWS_APPROVAL);

                final long reviewId = Util.parseLong(parameters.get("reviewId"));
                if (reviewId < 1) {
                    return _generateErrorJson("Invalid review id: " + reviewId);
                }
                return _approveReview(reviewId, currentAccount.getId(), environment.getDatabase());
            }
        });

        super._defineEndpoint("review-votes/<reviewVoteId>", HttpMethod.POST, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.REVIEWS_VOTING);

                final long reviewVoteId = Util.parseLong(parameters.get("reviewVoteId"));
                if (reviewVoteId < 1) {
                    return _generateErrorJson("Invalid review vote ID: " + reviewVoteId);
                }
                return _updateReviewVote(request, reviewVoteId, currentAccount, environment.getDatabase());
            }
        });

        super._defineEndpoint("review-votes/<reviewVoteId>", HttpMethod.DELETE, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                currentAccount.requirePermission(Permission.REVIEWS_VOTING);

                final long reviewVoteId = Util.parseLong(parameters.get("reviewVoteId"));
                if (reviewVoteId < 1) {
                    return _generateErrorJson("Invalid review vote id: " + reviewVoteId);
                }
                return _deleteReviewVote(reviewVoteId, environment.getDatabase());
            }
        });
    }

    private Json _listAllReviews(final boolean includeOpenReviews, final boolean includeClosedReviews, final Database<Connection> database) {
        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            final ReviewInflater reviewInflater = new ReviewInflater(databaseConnection);
            List<Review> reviews = reviewInflater.inflateReviews(includeOpenReviews, includeClosedReviews);

            Json reviewsJson = new Json(true);
            for (final Review review : reviews) {
                final Json reviewJson = _toJson(review);
                reviewsJson.add(reviewJson);
            }
            final Json response = new Json(false);
            response.put("reviews", reviewsJson);

            super._setJsonSuccessFields(response);
            return response;
        } catch (DatabaseException e) {
            String errorMessage = "Unable to inflate reviews.";
            _logger.error(errorMessage, e);
            return super._generateErrorJson(errorMessage);
        }
    }

    private Json _insertReview(final HttpServletRequest request, final Database<Connection> database) {
        try {
            final Json jsonRequest = _getRequestDataAsJson(request);
            final Json response = _generateSuccessJson();
            final Json reviewJson = jsonRequest.get("review");

            final Review review = _populateReviewFromJson(reviewJson, database);
            final DatabaseManager databaseManager = new DatabaseManager(database);
            databaseManager.insertReview(review);
            response.put("reviewId", review.getId());

            return response;
        }
        catch (final Exception exception) {
            _logger.error("Unable to submit review.", exception);
            return super._generateErrorJson("Unable to submit review: " + exception.getMessage());
        }
    }

    private Json _updateReview(final HttpServletRequest request, final Database<Connection> database) throws IOException {
        final Json jsonRequest = _getRequestDataAsJson(request);
        final Json response = _generateSuccessJson();
        final Json reviewJson = jsonRequest.get("review");

        try {
            final Review review = _populateReviewFromJson(reviewJson, database);
            final DatabaseManager databaseManager = new DatabaseManager(database);
            databaseManager.updateReview(review);
            response.put("reviewId", review.getId());
        }
        catch (final Exception exception) {
            _logger.error("Unable to submit review.", exception);
            return super._generateErrorJson("Unable to submit review: " + exception.getMessage());
        }

        return response;
    }

    private Json _approveReview(final long reviewId, final long accountId, final Database<Connection> database) throws Exception {
        final Json response = new Json(false);
        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            final ReviewInflater reviewInflater = new ReviewInflater(databaseConnection);
            final Review review = reviewInflater.inflateReview(reviewId);
            final long reviewAccountId = review.getAccount().getId();

            if (reviewAccountId == accountId) {
                final String errorMessage = "Unable approve review: a review cannot be approved by its creator.";
                _logger.error(errorMessage);
                return _generateErrorJson(errorMessage);
            }

            // Check review votes for at least one upvote from someone other than the review's creator.
            final List<ReviewVote> reviewVotes = review.getReviewVotes();
            long voteCounter = 0;
            for (ReviewVote reviewVote : reviewVotes) {
                if (reviewVote.isUpvote()) {
                    final long reviewVoteAccountId = reviewVote.getAccount().getId();
                    if (reviewVoteAccountId != reviewAccountId) {
                        voteCounter++;
                    }
                }

            }

            if (voteCounter == 0) {
                final String errorMessage = "Unable approve review: a review must be upvoted by at least one person other than the review's creator.";
                _logger.error(errorMessage);
                return _generateErrorJson(errorMessage);
            }

            final DatabaseManager databaseManager = new DatabaseManager(database);
            databaseManager.approveReview(review);
        }
        catch (DatabaseException e) {
            String errorMessage = "Unable approve review.";
            _logger.error(errorMessage, e);
            return _generateErrorJson(errorMessage);
        }

        super._setJsonSuccessFields(response);
        return response;
    }

    private Json _insertReviewVote(final HttpServletRequest request, final long reviewId, final Account currentAccount, final Database<Connection> database) throws Exception {
        try {
            final Json response = new Json(false);
            final Json jsonRequest = _getRequestDataAsJson(request);
            final Json reviewVoteJson = jsonRequest.get("reviewVote");
            final ReviewVote reviewVote = _populateReviewVoteFromJson(reviewVoteJson, currentAccount, database);

            DatabaseManager databaseManager = new DatabaseManager(database);
            databaseManager.insertReviewVote(reviewVote, reviewId);

            response.put("reviewVoteId", reviewVote.getId());
            super._setJsonSuccessFields(response);
            return response;
        }
        catch (DatabaseException e) {
            String errorMessage = "Unable insert review vote.";
            _logger.error(errorMessage, e);
            return _generateErrorJson(errorMessage);
        }
    }

    private Json _updateReviewVote(final HttpServletRequest httpRequest, final long reviewVoteId, final Account currentAccount, final Database<Connection> database) {
        try {
            final Json request = _getRequestDataAsJson(httpRequest);
            final Json reviewVoteJson = request.get("reviewVote");
            final Json response = new Json(false);

            final ReviewVote reviewVote = _populateReviewVoteFromJson(reviewVoteJson, currentAccount, database);
            reviewVote.setId(reviewVoteId);

            final DatabaseManager databaseManager = new DatabaseManager(database);

            if (reviewVoteId < 1) {
                _logger.error("Invalid review vote ID:  " + reviewVoteId);
                return super._generateErrorJson("Invalid review vote ID: " + reviewVoteId);
            }

            databaseManager.updateReviewVote(reviewVote);

            super._setJsonSuccessFields(response);
            return response;
        }
        catch (final Exception exception) {
            final String errorMessage = "Unable to update review vote: " + exception.getMessage();
            _logger.error(errorMessage, exception);
            return super._generateErrorJson(errorMessage);
        }
    }

    private Json _deleteReviewVote(final long reviewVoteId, final Database<Connection> database) throws Exception {
        try {
            // Validate input
            if (reviewVoteId < 1) {
                _logger.error("Unable to parse review vote ID: " + reviewVoteId);
                return super._generateErrorJson("Invalid review vote ID: " + reviewVoteId);
            }

            final DatabaseManager databaseManager = new DatabaseManager(database);
            databaseManager.deleteReviewVote(reviewVoteId);
        }
        catch (final Exception exception) {
            _logger.error("Unable to insert Interface.", exception);
            return super._generateErrorJson("Unable to insert Interface: " + exception.getMessage());
        }

        final Json response = new Json(false);
        super._setJsonSuccessFields(response);
        return response;
    }


    private Json _listReviewComments(final long reviewId, final Database<Connection> database) {
        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            final ReviewInflater reviewInflater = new ReviewInflater(databaseConnection);

            List<ReviewComment> reviewComments = reviewInflater.inflateReviewCommentsFromReviewId(reviewId);

            final Json response = new Json(false);

            final Json reviewCommentsJson = new Json(true);
            for (final ReviewComment reviewComment : reviewComments) {
                final Json reviewCommentJson = _toJson(reviewComment);
                reviewCommentsJson.add(reviewCommentJson);
            }
            response.put("reviewComments", reviewCommentsJson);

            super._setJsonSuccessFields(response);
            return response;

        } catch (DatabaseException e) {
            String errorMessage = "Unable to list review comments.";
            _logger.error(errorMessage, e);
            return _generateErrorJson(errorMessage);
        }
    }

    private Json _addReviewComment(final HttpServletRequest request, final long reviewId, final Account currentAccount, final Database<Connection> database) {
        try {
            final Json requestJson = _getRequestDataAsJson(request);
            final Json reviewCommentJson = requestJson.get("reviewComment");
            final ReviewComment reviewComment = _populateReviewCommentFromJson(reviewCommentJson, currentAccount, database);

            DatabaseManager databaseManager = new DatabaseManager(database);
            databaseManager.insertReviewComment(reviewComment, reviewId);

            final Json response = new Json(false);
            response.put("reviewCommentId", reviewComment.getId());
            super._setJsonSuccessFields(response);
            return response;

        } catch (Exception e) {
            String errorMessage = "Unable to add review comment.";
            _logger.error(errorMessage, e);
            return _generateErrorJson(errorMessage + " " + e);
        }
    }


    private Review _populateReviewFromJson(final Json reviewJson, final Database<Connection> database) throws Exception {
        Long id = reviewJson.getLong("id");

        final Long functionCatalogId = reviewJson.getLong("functionCatalogId");
        final Long functionBlockId = reviewJson.getLong("functionBlockId");
        final Long mostInterfaceId = reviewJson.getLong("mostInterfaceId");
        final Long mostFunctionId = reviewJson.getLong("mostFunctionId");

        final Long accountId = reviewJson.getLong("accountId");

        final String ticketUrl = reviewJson.getString("ticketUrl");

        final Review review = new Review();

        // TODO: determine if createdDate should be populated from JSON or create new Date().
        final Date date = new Date();
        Account account = null;

        // Inflate review's object.
        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            AccountInflater accountInflater = new AccountInflater(databaseConnection);
            account = accountInflater.inflateAccount(accountId);

            if (functionCatalogId >= 1) {
                final FunctionCatalogInflater functionCatalogInflater = new FunctionCatalogInflater(databaseConnection);
                final FunctionCatalog functionCatalog = functionCatalogInflater.inflateFunctionCatalog(functionCatalogId);
                review.setFunctionCatalog(functionCatalog);
            }
            else if (functionBlockId >= 1) {
                final FunctionBlockInflater functionBlockInflater = new FunctionBlockInflater(databaseConnection);
                final FunctionBlock functionBlock = functionBlockInflater.inflateFunctionBlock(functionBlockId);
                review.setFunctionBlock(functionBlock);
            }
            else if (mostInterfaceId >= 1) {
                final MostInterfaceInflater mostInterfaceInflater = new MostInterfaceInflater(databaseConnection);
                final MostInterface mostInterface = mostInterfaceInflater.inflateMostInterface(mostInterfaceId);
                review.setMostInterface(mostInterface);
            }
            else if (mostFunctionId >= 1) {
                final MostFunctionInflater mostFunctionInflater = new MostFunctionInflater(databaseConnection);
                final MostFunction mostFunction = mostFunctionInflater.inflateMostFunction(mostFunctionId);
                review.setMostFunction(mostFunction);
            }
        }
        catch (final DatabaseException exception) {
            _logger.error("Unable to get the object for review.", exception);
            throw new Exception("Unable to get the object for review.");
        }

        review.setId(id);
        review.setAccount(account);
        review.setCreatedDate(date);
        review.setTicketUrl(ticketUrl);

        return review;
    }

    private ReviewVote _populateReviewVoteFromJson(final Json reviewVoteJson, final Account currentAccount, final Database<Connection> database) throws DatabaseException {
        final long id = reviewVoteJson.getLong("id");
        final boolean isUpvote = reviewVoteJson.getBoolean("isUpvote");

        final ReviewVote reviewVote = new ReviewVote();

        reviewVote.setId(id);
        reviewVote.setAccount(currentAccount);
        reviewVote.setIsUpvote(isUpvote);

        return reviewVote;
    }

    private ReviewComment _populateReviewCommentFromJson(final Json reviewCommentJson, final Account currentAccount, final Database<Connection> database) throws DatabaseException {
        final long id = reviewCommentJson.getLong("id");
        final String commentText = reviewCommentJson.getString("commentText");

        final ReviewComment reviewComment = new ReviewComment();

        reviewComment.setId(id);
        reviewComment.setAccount(currentAccount);
        reviewComment.setCommentText(commentText);

        return reviewComment;
    }

    private Json _toJson(final Review review) {
        final Json json = new Json(false);

        final Long id = review.getId();
        final Long functionCatalogId = review.getFunctionCatalog() == null ? null : review.getFunctionCatalog().getId();
        final Long functionBlockId = review.getFunctionBlock() == null ? null : review.getFunctionBlock().getId();
        final Long mostInterfaceId = review.getMostInterface() == null ? null : review.getMostInterface().getId();
        final Long mostFunctionId = review.getMostFunction() == null ? null : review.getMostFunction().getId();
        final Long accountId = review.getAccount().getId();
        final String ticketUrl = review.getTicketUrl();
        final String createdDate = DateUtil.dateToDateString(review.getCreatedDate());
        final List<ReviewVote> reviewVotes = review.getReviewVotes();
        final List<ReviewComment> reviewComments = review.getReviewComments();

        final Json reviewVotesJson = new Json(true);
        for (final ReviewVote reviewVote : reviewVotes) {
            final Json reviewVoteJson = _toJson(reviewVote);
            reviewVotesJson.add(reviewVoteJson);
        }

        final Json reviewCommentsJson = new Json(true);
        for (final ReviewComment reviewComment : reviewComments) {
            final Json reviewCommentJson = _toJson(reviewComment);
            reviewCommentsJson.add(reviewCommentJson);
        }

        json.put("id", id);
        json.put("functionCatalogId", functionCatalogId);
        json.put("functionBlockId", functionBlockId);
        json.put("mostInterfaceId", mostInterfaceId);
        json.put("mostFunctionId", mostFunctionId);
        json.put("accountId", accountId);
        json.put("ticketUrl", ticketUrl);
        json.put("createdDate", createdDate);
        json.put("reviewVotes", reviewVotesJson);
        json.put("reviewComments", reviewCommentsJson);

        return json;
    }

    private Json _toJson(final ReviewVote reviewVote) {
        final Json json = new Json(false);

        final Long id = reviewVote.getId();
        final Long accountId = reviewVote.getAccount().getId();
        final String createdDate = DateUtil.dateToDateTimeString(reviewVote.getCreatedDate());
        final boolean isUpvote = reviewVote.isUpvote();

        json.put("id", id);
        json.put("accountId", accountId);
        json.put("createdDate", createdDate);
        json.put("isUpvote", isUpvote);

        return json;
    }

    private Json _toJson(final ReviewComment reviewComment) {
        final Json json = new Json(false);

        final Long id = reviewComment.getId();
        final Long accountId = reviewComment.getAccount().getId();
        final String createdDate = DateUtil.dateToDateTimeString(reviewComment.getCreatedDate());
        final String commentText = reviewComment.getCommentText();

        json.put("id", id);
        json.put("accountId", accountId);
        json.put("createdDate", createdDate);
        json.put("commentText", commentText);

        return json;
    }
}
