package com.softwareverde.tidyduck.api;

import com.softwareverde.database.Database;
import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.json.Json;
import com.softwareverde.tidyduck.Account;
import com.softwareverde.tidyduck.DateUtil;
import com.softwareverde.tidyduck.Review;
import com.softwareverde.tidyduck.ReviewVote;
import com.softwareverde.tidyduck.database.*;
import com.softwareverde.tidyduck.environment.Environment;
import com.softwareverde.tidyduck.most.*;
import com.softwareverde.tomcat.servlet.AuthenticatedJsonServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Date;

public class ReviewServlet extends AuthenticatedJsonServlet {
    private final Logger _logger = LoggerFactory.getLogger(this.getClass());

    public ReviewServlet() {
        super.defineEndpoint("reviews", HttpMethod.GET, new AuthenticatedJsonRoute() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Long accountId, final Environment environment) throws Exception {
                final String excludeOpenReviewsString = request.getParameter("excludeOpenReviews");
                final String excludeClosedReviewsString = request.getParameter("excludeClosedReviews");

                final boolean excludeOpenReviews = Boolean.parseBoolean(excludeOpenReviewsString);
                final boolean excludeClosedReviews = Boolean.parseBoolean(excludeClosedReviewsString);

                return listAllReviews(!excludeOpenReviews, !excludeClosedReviews, environment.getDatabase());
            }
        });

        super.defineEndpoint("reviews", HttpMethod.POST, new AuthenticatedJsonRoute() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Long accountId, final Environment environment) throws Exception {
                return _insertReview(request, environment.getDatabase());
            }
        });

        super.defineEndpoint("reviews/votes", HttpMethod.POST, new AuthenticatedJsonRoute() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Long accountId, final Environment environment) throws Exception {
                return _insertReviewVote(request, accountId, environment.getDatabase());
            }
        });
    }

    public Json listAllReviews(final boolean includeOpenReviews, final boolean includeClosedReviews, final Database database) {
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
            _logger.error(errorMessage);
            return super._generateErrorJson(errorMessage);
        }
    }

    private Json _insertReview(final HttpServletRequest request, final Database<Connection> database) throws Exception {
        final Json jsonRequest = _getRequestDataAsJson(request);
        final Json response = _generateSuccessJson();
        final Json reviewJson = jsonRequest.get("review");

        try {
            final Review review = _populateReviewFromJson(reviewJson, database);
            final DatabaseManager databaseManager = new DatabaseManager(database);
            databaseManager.insertReview(review);
            response.put("reviewId", review.getId());
        }
        catch (final Exception exception) {
            _logger.error("Unable to submit review.", exception);
            return super._generateErrorJson("Unable to submit review: " + exception.getMessage());
        }

        return response;
    }

    private Json _insertReviewVote(final HttpServletRequest request, final Long accountId, final Database<Connection> database) throws Exception {
        try {
            final Json response = new Json(false);
            final Json jsonRequest = _getRequestDataAsJson(request);
            final ReviewVote reviewVote = _populateReviewVoteFromJson(jsonRequest, accountId, database);

            DatabaseManager databaseManager = new DatabaseManager(database);
            databaseManager.insertReviewVote(reviewVote);

            super._setJsonSuccessFields(response);
            return response;
        }
        catch (DatabaseException e) {
            String errorMessage = "Unable insert review vote.";
            _logger.error(errorMessage, e);
            return _generateErrorJson(errorMessage);
        }
    }

    private Review _populateReviewFromJson(final Json reviewJson, final Database<Connection> database) throws Exception {
        final Long functionCatalogId = reviewJson.getLong("functionCatalogId");
        final Long functionBlockId = reviewJson.getLong("functionBlockId");
        final Long mostInterfaceId = reviewJson.getLong("mostInterfaceId");
        final Long mostFunctionId = reviewJson.getLong("mostFunctionId");
        final Long accountId = reviewJson.getLong("accountId");

        final Review review = new Review();

        // TODO: determine if createdDate should be populated from JSON or create new Date().
        final Date date = new Date();
        final Account account;

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
        review.setAccount(account);
        review.setCreatedDate(date);

        return review;
    }

    private ReviewVote _populateReviewVoteFromJson(final Json reviewVoteJson, final Long accountId, final Database<Connection> database) throws Exception {
        final Long reviewId = reviewVoteJson.getLong("reviewId");
        final boolean isUpvote = reviewVoteJson.getBoolean("isUpvote");

        final ReviewVote reviewVote = new ReviewVote();
        final Account account;

        // Inflate review object
        try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
            AccountInflater accountInflater = new AccountInflater(databaseConnection);
            account = accountInflater.inflateAccount(accountId);
        }
        catch (final DatabaseException exception) {
            _logger.error("Unable to inflate account for review vote.", exception);
            throw new Exception("Unable to inflate account for review vote.");
        }

        reviewVote.setReviewId(reviewId);
        reviewVote.setAccount(account);
        reviewVote.setIsUpvote(isUpvote);

        return reviewVote;
    }

    private Json _toJson(final Review review) {
        final Json json = new Json(false);

        final Long id = review.getId();
        final Long functionCatalogId = review.getFunctionCatalog() == null ? null : review.getFunctionCatalog().getId();
        final Long functionBlockId = review.getFunctionBlock() == null ? null : review.getFunctionBlock().getId();
        final Long mostInterfaceId = review.getMostInterface() == null ? null : review.getMostInterface().getId();
        final Long mostFunctionId = review.getMostFunction() == null ? null : review.getMostFunction().getId();
        final Long accountId = review.getAccount().getId();
        final String createdDate = DateUtil.dateToDateString(review.getCreatedDate());
        final List<ReviewVote> reviewVotes = review.getReviewVotes();

        final Json reviewVotesJson = new Json(true);
        for (final ReviewVote reviewVote : reviewVotes) {
            final Json reviewVoteJson = _toJson(reviewVote);
            reviewVotesJson.add(reviewVoteJson);
        }

        json.put("id", id);
        json.put("functionCatalogId", functionCatalogId);
        json.put("functionBlockId", functionBlockId);
        json.put("mostInterfaceId", mostInterfaceId);
        json.put("mostFunctionId", mostFunctionId);
        json.put("accountId", accountId);
        json.put("createdDate", createdDate);
        json.put("reviewVotes", reviewVotesJson);

        return json;
    }

    private Json _toJson(final ReviewVote reviewVote) {
        final Json json = new Json(false);

        final Long id = reviewVote.getId();
        final Long reviewId = reviewVote.getReviewId();
        final Long accountId = reviewVote.getAccount().getId();
        final String createdDate = DateUtil.dateToDateString(reviewVote.getCreatedDate());
        final boolean isUpvote = reviewVote.isUpvote();

        json.put("id", id);
        json.put("reviewId", reviewId);
        json.put("accountId", accountId);
        json.put("createdDate", createdDate);
        json.put("isUpvote", isUpvote);

        return json;
    }
}
