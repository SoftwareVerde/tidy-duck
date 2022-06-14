package com.softwareverde.tidyduck.api;

import com.softwareverde.database.Database;
import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.http.HttpMethod;
import com.softwareverde.http.server.servlet.request.Request;
import com.softwareverde.http.server.servlet.routed.json.AuthenticatedJsonApplicationServlet;
import com.softwareverde.http.server.servlet.routed.json.JsonRequestHandler;
import com.softwareverde.http.server.servlet.session.SessionManager;
import com.softwareverde.json.Json;
import com.softwareverde.logging.Logger;
import com.softwareverde.tidyduck.*;
import com.softwareverde.tidyduck.authentication.TidyDuckAuthenticator;
import com.softwareverde.tidyduck.database.*;
import com.softwareverde.tidyduck.environment.TidyDuckEnvironment;
import com.softwareverde.tidyduck.most.FunctionBlock;
import com.softwareverde.tidyduck.most.FunctionCatalog;
import com.softwareverde.tidyduck.most.MostFunction;
import com.softwareverde.tidyduck.most.MostInterface;
import com.softwareverde.tidyduck.util.Util;

import java.sql.Connection;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ReviewServlet extends AuthenticatedJsonApplicationServlet<TidyDuckEnvironment> {
    public ReviewServlet(final TidyDuckEnvironment environment, final SessionManager sessionManager, final TidyDuckAuthenticator authenticator) {
        super(environment, sessionManager);
        
        super._defineEndpoint("reviews", HttpMethod.GET, new TidyDuckRequestHandler(sessionManager, authenticator) {
            @Override
            public Json handleRequest(final Account currentAccount, final Request request, final TidyDuckEnvironment environment, final Map<String, String> parameters) throws Exception {
                currentAccount.requirePermission(Permission.REVIEWS_VIEW);

                final String excludeOpenReviewsString = request.getGetParameters().get("excludeOpenReviews");
                final String excludeClosedReviewsString = request.getGetParameters().get("excludeClosedReviews");

                final boolean excludeOpenReviews = Boolean.parseBoolean(excludeOpenReviewsString);
                final boolean excludeClosedReviews = Boolean.parseBoolean(excludeClosedReviewsString);

                return _listAllReviews(!excludeOpenReviews, !excludeClosedReviews, environment.getDatabase());
            }
        });

        super._defineEndpoint("reviews", HttpMethod.POST, new TidyDuckRequestHandler(sessionManager, authenticator) {
            @Override
            public Json handleRequest(final Account currentAccount, final Request request, final TidyDuckEnvironment environment, final Map<String, String> parameters) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_MODIFY);

                return _insertReview(request, environment.getDatabase());
            }
        });

        super._defineEndpoint("reviews/<reviewId>", HttpMethod.GET, new TidyDuckRequestHandler(sessionManager, authenticator) {
            @Override
            public Json handleRequest(final Account currentAccount, final Request request, final TidyDuckEnvironment environment, final Map<String, String> parameters) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_MODIFY);

                final long reviewId = Util.parseLong(parameters.get("reviewId"));
                if (reviewId < 1) {
                    throw new IllegalArgumentException("Invalid review id: " + reviewId);
                }
                return _getReview(reviewId, environment.getDatabase());
            }
        });

        super._defineEndpoint("reviews/<reviewId>", HttpMethod.POST, new TidyDuckRequestHandler(sessionManager, authenticator) {
            @Override
            public Json handleRequest(final Account currentAccount, final Request request, final TidyDuckEnvironment environment, final Map<String, String> parameters) throws Exception {
                currentAccount.requirePermission(Permission.MOST_COMPONENTS_MODIFY);

                return _updateReview(request, environment.getDatabase());
            }
        });

        super._defineEndpoint("reviews/<reviewId>/votes", HttpMethod.POST, new TidyDuckRequestHandler(sessionManager, authenticator) {
            @Override
            public Json handleRequest(final Account currentAccount, final Request request, final TidyDuckEnvironment environment, final Map<String, String> parameters) throws Exception {
                currentAccount.requirePermission(Permission.REVIEWS_VIEW);

                final long reviewId = Util.parseLong(parameters.get("reviewId"));
                if (reviewId < 1) {
                    throw new IllegalArgumentException("Invalid review ID: " + reviewId);
                }
                return _insertReviewVote(request, reviewId, currentAccount, environment.getDatabase());
            }
        });

        super._defineEndpoint("reviews/<reviewId>/comments", HttpMethod.GET, new TidyDuckRequestHandler(sessionManager, authenticator) {
            @Override
            public Json handleRequest(final Account currentAccount, final Request request, final TidyDuckEnvironment environment, final Map<String, String> parameters) throws Exception {
                currentAccount.requirePermission(Permission.REVIEWS_COMMENTS);

                final long reviewId = Util.parseLong(parameters.get("reviewId"));
                if (reviewId < 1) {
                    throw new IllegalArgumentException("Invalid review id: " + reviewId);
                }
                return _listReviewComments(reviewId, environment.getDatabase());
            }
        });

        super._defineEndpoint("reviews/<reviewId>/comments", HttpMethod.POST, new TidyDuckRequestHandler(sessionManager, authenticator) {
            @Override
            public Json handleRequest(final Account currentAccount, final Request request, final TidyDuckEnvironment environment, final Map<String, String> parameters) throws Exception {
                currentAccount.requirePermission(Permission.REVIEWS_COMMENTS);

                final long reviewId = Util.parseLong(parameters.get("reviewId"));
                if (reviewId < 1) {
                    throw new IllegalArgumentException("Invalid review id: " + reviewId);
                }
                return _addReviewComment(request, reviewId, currentAccount, environment.getDatabase());
            }
        });

        super._defineEndpoint("reviews/<reviewId>/approve", HttpMethod.POST, new TidyDuckRequestHandler(sessionManager, authenticator) {
            @Override
            public Json handleRequest(final Account currentAccount, final Request request, final TidyDuckEnvironment environment, final Map<String, String> parameters) throws Exception {
                currentAccount.requirePermission(Permission.REVIEWS_APPROVAL);

                final long reviewId = Util.parseLong(parameters.get("reviewId"));
                if (reviewId < 1) {
                    throw new IllegalArgumentException("Invalid review id: " + reviewId);
                }
                return _approveReview(reviewId, currentAccount.getId(), environment.getDatabase());
            }
        });

        super._defineEndpoint("review-votes/<reviewVoteId>", HttpMethod.POST, new TidyDuckRequestHandler(sessionManager, authenticator) {
            @Override
            public Json handleRequest(final Account currentAccount, final Request request, final TidyDuckEnvironment environment, final Map<String, String> parameters) throws Exception {
                currentAccount.requirePermission(Permission.REVIEWS_VOTING);

                final long reviewVoteId = Util.parseLong(parameters.get("reviewVoteId"));
                if (reviewVoteId < 1) {
                    throw new IllegalArgumentException("Invalid review vote ID: " + reviewVoteId);
                }
                return _updateReviewVote(request, reviewVoteId, currentAccount, environment.getDatabase());
            }
        });

        super._defineEndpoint("review-votes/<reviewVoteId>", HttpMethod.DELETE, new TidyDuckRequestHandler(sessionManager, authenticator) {
            @Override
            public Json handleRequest(final Account currentAccount, final Request request, final TidyDuckEnvironment environment, final Map<String, String> parameters) throws Exception {
                currentAccount.requirePermission(Permission.REVIEWS_VOTING);

                final long reviewVoteId = Util.parseLong(parameters.get("reviewVoteId"));
                if (reviewVoteId < 1) {
                    throw new IllegalArgumentException("Invalid review vote id: " + reviewVoteId);
                }
                return _deleteReviewVote(reviewVoteId, environment.getDatabase());
            }
        });
    }

    private Json _listAllReviews(final boolean includeOpenReviews, final boolean includeClosedReviews, final Database<Connection> database) throws Exception {
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
            
            JsonRequestHandler.setJsonSuccessFields(response);
            return response;
        } catch (final DatabaseException exception) {
            final String errorMessage = "Unable to inflate reviews.";
            throw new Exception(errorMessage, exception);
        }
    }

    private Json _getReview(final long reviewId, final Database<Connection> database) throws Exception {
        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            final ReviewInflater reviewInflater = new ReviewInflater(databaseConnection);
            final Review review = reviewInflater.inflateReview(reviewId);

            final Json response = new Json(false);
            final Json reviewJson = _toJson(review);
            response.put("review", reviewJson);

            JsonRequestHandler.setJsonSuccessFields(response);
            return response;
        }
        catch (final DatabaseException databaseException) {
            final String errorMessage = "Unable to inflate review ID: " + reviewId;
            throw new Exception(errorMessage, databaseException);
        }
    }

    private Json _insertReview(final Request request, final Database<Connection> database) throws Exception {
        try {
            final Json jsonRequest = JsonRequestHandler.getRequestDataAsJson(request);
            final Json response = JsonRequestHandler.generateSuccessJson();
            final Json reviewJson = jsonRequest.get("review");

            final Review review = _populateReviewFromJson(reviewJson, database);
            final DatabaseManager databaseManager = new DatabaseManager(database);
            databaseManager.insertReview(review);
            response.put("reviewId", review.getId());

            return response;
        }
        catch (final Exception exception) {
            throw new Exception("Unable to submit review: " + exception.getMessage(), exception);
        }
    }

    private Json _updateReview(final Request request, final Database<Connection> database) throws Exception {
        final Json jsonRequest = JsonRequestHandler.getRequestDataAsJson(request);
        final Json response = JsonRequestHandler.generateSuccessJson();
        final Json reviewJson = jsonRequest.get("review");

        try {
            final Review review = _populateReviewFromJson(reviewJson, database);
            final DatabaseManager databaseManager = new DatabaseManager(database);
            databaseManager.updateReview(review);
            response.put("reviewId", review.getId());
        }
        catch (final Exception exception) {
            throw new Exception("Unable to submit review: " + exception.getMessage(), exception);
        }

        return response;
    }

    private Json _approveReview(final long reviewId, final AccountId accountId, final Database<Connection> database) throws Exception {
        final Json response = new Json(false);
        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            final ReviewInflater reviewInflater = new ReviewInflater(databaseConnection);
            final Review review = reviewInflater.inflateReview(reviewId);
            final AccountId reviewAccountId = review.getAccount().getId();

            if (reviewAccountId == accountId) {
                final String errorMessage = "Unable approve review: a review cannot be approved by its creator.";
                Logger.error(errorMessage);
                throw new IllegalArgumentException(errorMessage);
            }

            // Check review object for any deleted children (they must be restored/disassociated/deleted)
            String deletedChildrenErrorMessage = _checkReviewObjectForDeletedChildren(database, review);
            if (deletedChildrenErrorMessage != null) {
                deletedChildrenErrorMessage = "Unable approve review: " + deletedChildrenErrorMessage;
                Logger.error(deletedChildrenErrorMessage);
                throw new IllegalArgumentException(deletedChildrenErrorMessage);
            }

            // Check review votes for at least one upvote from someone other than the review's creator.
            final List<ReviewVote> reviewVotes = review.getReviewVotes();
            long voteCounter = 0;
            for (ReviewVote reviewVote : reviewVotes) {
                if (reviewVote.isUpvote()) {
                    final AccountId reviewVoteAccountId = reviewVote.getAccount().getId();
                    if (reviewVoteAccountId != reviewAccountId) {
                        voteCounter++;
                    }
                }

            }

            final ApplicationSettingsInflater applicationSettingsInflater = new ApplicationSettingsInflater(databaseConnection);
            final String minimumRequiredUpvotesString = applicationSettingsInflater.inflateSetting(ApplicationSetting.REVIEW_APPROVAL_MINIMUM_UPVOTES);
            final Long minimumRequiredUpvotes = Util.parseLong(minimumRequiredUpvotesString);
            if (minimumRequiredUpvotes == null) {
                final String errorMessage = "Unable to determine minimum required upvotes.";
                Logger.error(errorMessage);
                throw new IllegalArgumentException(errorMessage);
            }
            if (voteCounter < minimumRequiredUpvotes) {
                final String errorMessage = "Unable approve review: a review must be upvoted by at least " + minimumRequiredUpvotesString + " people other than the review's creator.";
                Logger.error(errorMessage);
                throw new IllegalArgumentException(errorMessage);
            }

            final DatabaseManager databaseManager = new DatabaseManager(database);
            databaseManager.approveReview(review);
        }
        catch (final DatabaseException exception) {
            final String errorMessage = "Unable approve review: " + exception.getMessage();
            throw new Exception(errorMessage, exception);
        }

        JsonRequestHandler.setJsonSuccessFields(response);
        return response;
    }

    protected String _checkReviewObjectForDeletedChildren(final Database<Connection> database, final Review review) {
        final FunctionCatalog functionCatalog = review.getFunctionCatalog();
        final FunctionBlock functionBlock = review.getFunctionBlock();
        final MostInterface mostInterface = review.getMostInterface();
        final MostFunction mostFunction = review.getMostFunction();

        try {
            DatabaseManager databaseManager = new DatabaseManager(database);
            boolean hasDeletedChildren = false;
            if (functionCatalog != null) {
                hasDeletedChildren = databaseManager.functionCatalogHasDeletedChildren(functionCatalog.getId());
            } else if (functionBlock != null) {
                hasDeletedChildren = databaseManager.functionBlockHasDeletedChildren(functionBlock.getId());
            } else if (mostInterface != null) {
                hasDeletedChildren = databaseManager.mostInterfaceHasDeletedChildren(mostInterface.getId());
            } else if (mostFunction != null) {
                // most functions have no children, nothing to check
            } else {
                return "Invalid review - no associated object.";
            }
            if (hasDeletedChildren) {
                return "Component under review has deleted children, these must be removed or restored before merging.";
            } else {
                return null;
            }
        } catch (Exception e) {
            return "Failed check for deleted children: " + e.getMessage();
        }
    }

    private Json _insertReviewVote(final Request request, final long reviewId, final Account currentAccount, final Database<Connection> database) throws Exception {
        try {
            final Json response = new Json(false);
            final Json jsonRequest = JsonRequestHandler.getRequestDataAsJson(request);
            final Json reviewVoteJson = jsonRequest.get("reviewVote");
            final ReviewVote reviewVote = _populateReviewVoteFromJson(reviewVoteJson, currentAccount, database);

            DatabaseManager databaseManager = new DatabaseManager(database);
            databaseManager.insertReviewVote(reviewVote, reviewId);

            response.put("reviewVoteId", reviewVote.getId());
            JsonRequestHandler.setJsonSuccessFields(response);
            return response;
        }
        catch (final DatabaseException exception) {
            final String errorMessage = "Unable insert review vote.";
            throw new Exception(errorMessage, exception);
        }
    }

    private Json _updateReviewVote(final Request httpRequest, final long reviewVoteId, final Account currentAccount, final Database<Connection> database) throws Exception {
        try {
            final Json request = JsonRequestHandler.getRequestDataAsJson(httpRequest);
            final Json reviewVoteJson = request.get("reviewVote");
            final Json response = new Json(false);

            final ReviewVote reviewVote = _populateReviewVoteFromJson(reviewVoteJson, currentAccount, database);
            reviewVote.setId(reviewVoteId);

            final DatabaseManager databaseManager = new DatabaseManager(database);

            if (reviewVoteId < 1) {
                throw new IllegalArgumentException("Invalid review vote ID: " + reviewVoteId);
            }

            databaseManager.updateReviewVote(reviewVote);

            JsonRequestHandler.setJsonSuccessFields(response);
            return response;
        }
        catch (final Exception exception) {
            final String errorMessage = "Unable to update review vote: " + exception.getMessage();
            throw new Exception(errorMessage, exception);
        }
    }

    private Json _deleteReviewVote(final long reviewVoteId, final Database<Connection> database) throws Exception {
        try {
            // Validate input
            if (reviewVoteId < 1) {
                Logger.error("Unable to parse review vote ID: " + reviewVoteId);
                throw new IllegalArgumentException("Invalid review vote ID: " + reviewVoteId);
            }

            final DatabaseManager databaseManager = new DatabaseManager(database);
            databaseManager.deleteReviewVote(reviewVoteId);
        }
        catch (final Exception exception) {
            throw new Exception("Unable to insert Interface: " + exception.getMessage(), exception);
        }

        final Json response = new Json(false);
        JsonRequestHandler.setJsonSuccessFields(response);
        return response;
    }


    private Json _listReviewComments(final long reviewId, final Database<Connection> database) throws Exception {
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

            JsonRequestHandler.setJsonSuccessFields(response);
            return response;

        } catch (final DatabaseException e) {
            final String errorMessage = "Unable to list review comments.";
            throw new Exception(errorMessage, e);
        }
    }

    private Json _addReviewComment(final Request request, final long reviewId, final Account currentAccount, final Database<Connection> database) throws Exception {
        try {
            final Json requestJson = JsonRequestHandler.getRequestDataAsJson(request);
            final Json reviewCommentJson = requestJson.get("reviewComment");
            final ReviewComment reviewComment = _populateReviewCommentFromJson(reviewCommentJson, currentAccount, database);

            DatabaseManager databaseManager = new DatabaseManager(database);
            databaseManager.insertReviewComment(reviewComment, reviewId);

            final Json response = new Json(false);
            response.put("reviewCommentId", reviewComment.getId());
            JsonRequestHandler.setJsonSuccessFields(response);
            return response;

        } catch (final Exception exception) {
            final String errorMessage = "Unable to add review comment.";
            throw new Exception(errorMessage, exception);
        }
    }


    private Review _populateReviewFromJson(final Json reviewJson, final Database<Connection> database) throws Exception {
        Long id = reviewJson.getLong("id");

        final Long functionCatalogId = reviewJson.getLong("functionCatalogId");
        final Long functionBlockId = reviewJson.getLong("functionBlockId");
        final Long mostInterfaceId = reviewJson.getLong("mostInterfaceId");
        final Long mostFunctionId = reviewJson.getLong("mostFunctionId");

        final AccountId accountId = AccountId.wrap(reviewJson.getLong("accountId"));

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
            Logger.error("Unable to get the object for review.", exception);
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
        final Long id = reviewCommentJson.getLong("id");
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
        final AccountId accountId = review.getAccount().getId();
        final String ticketUrl = review.getTicketUrl();
        final String createdDate = DateUtil.dateToDateString(review.getCreatedDate());
        String approvalDateString = null;
        final Date approvalDate = review.getApprovalDate();

        if (approvalDate != null) {
            approvalDateString = DateUtil.dateToDateString(approvalDate);
        }

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
        json.put("approvalDate", approvalDateString);
        json.put("reviewVotes", reviewVotesJson);
        json.put("reviewComments", reviewCommentsJson);

        return json;
    }

    private Json _toJson(final ReviewVote reviewVote) {
        final Json json = new Json(false);

        final Long id = reviewVote.getId();
        final AccountId accountId = reviewVote.getAccount().getId();
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
        final AccountId accountId = reviewComment.getAccount().getId();
        final String createdDate = DateUtil.dateToDateTimeString(reviewComment.getCreatedDate());
        final String commentText = reviewComment.getCommentText();

        json.put("id", id);
        json.put("accountId", accountId);
        json.put("createdDate", createdDate);
        json.put("commentText", commentText);

        return json;
    }
}
