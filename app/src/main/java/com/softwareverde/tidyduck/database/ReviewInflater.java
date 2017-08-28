package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.Query;
import com.softwareverde.database.Row;
import com.softwareverde.tidyduck.Account;
import com.softwareverde.tidyduck.DateUtil;
import com.softwareverde.tidyduck.Review;
import com.softwareverde.tidyduck.most.FunctionBlock;
import com.softwareverde.tidyduck.most.FunctionCatalog;
import com.softwareverde.tidyduck.most.MostFunction;
import com.softwareverde.tidyduck.most.MostInterface;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReviewInflater {

    private final DatabaseConnection<Connection> _databaseConnection;

    private static final String LIST_REVIEWS_QUERY = "SELECT * FROM (" +
                                                        "SELECT reviews.id, function_catalog_id, function_block_id, interface_id, function_id, reviews.account_id, created_date, COALESCE(function_catalogs.is_approved, function_blocks.is_approved, interfaces.is_approved, functions.is_approved) = 1 is_approved\n" +
                                                        "FROM reviews\n" +
                                                        "LEFT OUTER JOIN function_catalogs ON function_catalogs.id = reviews.function_catalog_id\n" +
                                                        "LEFT OUTER JOIN function_blocks ON function_blocks.id = reviews.function_block_id\n" +
                                                        "LEFT OUTER JOIN interfaces ON interfaces.id = reviews.interface_id\n" +
                                                        "LEFT OUTER JOIN functions ON functions.id = reviews.function_id) A";
    private static final String OPEN_REVIEWS_WHERE_CLAUSE = "\nWHERE is_approved = 0";
    private static final String CLOSED_REVIEWS_WHERE_CLAUSE = "\nWHERE is_approved = 1";

    public ReviewInflater(final DatabaseConnection<Connection> databaseConnection) {
        _databaseConnection = databaseConnection;
    }

    public List<Review> inflateReviews() throws DatabaseException {
        return inflateReviews(true, true);
    }

    public List<Review> inflateReviews(final boolean includeOpenReviews, final boolean includeClosedReviews) throws DatabaseException {
        // not including either, return nothing
        if (!includeOpenReviews && !includeClosedReviews) {
            return new ArrayList<Review>();
        }
        // get reviews from database
        String reviewsQuery = LIST_REVIEWS_QUERY;
        if (includeOpenReviews && !includeClosedReviews) {
            reviewsQuery += OPEN_REVIEWS_WHERE_CLAUSE;
        }
        if (includeClosedReviews && !includeOpenReviews) {
            reviewsQuery += CLOSED_REVIEWS_WHERE_CLAUSE;
        }

        final ArrayList<Review> reviews = new ArrayList<>();

        final Query query = new Query(reviewsQuery);
        final List<Row> rows = _databaseConnection.query(query);
        for (final Row row : rows) {
            final Review review = _convertRowToReview(row);
            reviews.add(review);
        }

        return reviews;
    }

    protected Review _convertRowToReview(final Row row) throws DatabaseException {
        final Long id = row.getLong("id");
        final Long functionCatalogId = row.getLong("function_catalog_id");
        final Long functionBlockId = row.getLong("function_block_id");
        final Long mostInterfaceId = row.getLong("interface_id");
        final Long mostFunctionId = row.getLong("function_id");
        final Long accountId = row.getLong("account_id");
        final Date createdDate = DateUtil.dateFromDateString(row.getString("created_date"));

        // inflate function catalog
        FunctionCatalog functionCatalog = null;
        if (functionCatalogId != null) {
            final FunctionCatalogInflater functionCatalogInflater = new FunctionCatalogInflater(_databaseConnection);
            functionCatalog = functionCatalogInflater.inflateFunctionCatalog(functionCatalogId);
        }
        // inflate function block
        FunctionBlock functionBlock = null;
        if (functionBlockId != null) {
            final FunctionBlockInflater functionBlockInflater = new FunctionBlockInflater(_databaseConnection);
            functionBlock = functionBlockInflater.inflateFunctionBlock(functionBlockId);
        }
        // inflate interface
        MostInterface mostInterface = null;
        if (mostInterfaceId != null) {
            final MostInterfaceInflater mostInterfaceInflater = new MostInterfaceInflater(_databaseConnection);
            mostInterface = mostInterfaceInflater.inflateMostInterface(mostInterfaceId);
        }
        // inflate function
        MostFunction mostFunction = null;
        if (mostFunctionId != null) {
            final MostFunctionInflater mostFunctionInflater = new MostFunctionInflater(_databaseConnection);
            mostFunction = mostFunctionInflater.inflateMostFunction(mostFunctionId);
        }
        // inflate account
        Account account = null;
        if (accountId != null) {
            final AccountInflater accountInflater = new AccountInflater(_databaseConnection);
            account = accountInflater.inflateAccount(accountId);
        }

        // create and return review
        final Review review = new Review();
        review.setId(id);
        review.setFunctionCatalog(functionCatalog);
        review.setFunctionBlock(functionBlock);
        review.setMostInterface(mostInterface);
        review.setMostFunction(mostFunction);
        review.setAccount(account);
        review.setCreatedDate(createdDate);
        return review;
    }
}
