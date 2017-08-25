package com.softwareverde.tidyduck.api;

import com.softwareverde.database.Database;
import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.json.Json;
import com.softwareverde.tidyduck.Review;
import com.softwareverde.tidyduck.database.ReviewInflater;
import com.softwareverde.tidyduck.environment.Environment;
import com.softwareverde.tomcat.servlet.AuthenticatedJsonServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

public class ReviewServlet extends AuthenticatedJsonServlet {
    private final Logger _logger = LoggerFactory.getLogger(this.getClass());

    public ReviewServlet() {
        super.defineEndpoint("reviews", HttpMethod.GET, new AuthenticatedJsonRoute() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final HttpMethod httpMethod, final Long accountId, final Environment environment) throws Exception {
                // TODO: list all reviews
                return null;
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
                reviewJson.add(reviewJson);
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

    private Json _toJson(final Review review) {
        final Json json = new Json(false);

        String

        return json;
    }
}
