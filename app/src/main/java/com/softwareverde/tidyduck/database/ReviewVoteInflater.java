package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.Query;
import com.softwareverde.database.Row;
import com.softwareverde.tidyduck.Account;
import com.softwareverde.tidyduck.DateUtil;
import com.softwareverde.tidyduck.ReviewVote;

import java.sql.Connection;
import java.util.*;

public class ReviewVoteInflater {
    protected final DatabaseConnection<Connection> _databaseConnection;

    public ReviewVoteInflater(DatabaseConnection<Connection> connection) { _databaseConnection = connection; }

    public ReviewVote inflateReviewVote(final long reviewVoteId) throws DatabaseException {
        final Query query = new Query("SELECT review_id, account_id, created_date, is_upvote FROM review_votes WHERE id = ?");
        query.setParameter(reviewVoteId);

        final List<Row> rows = _databaseConnection.query(query);
        if (rows.size() == 0) {
            throw new DatabaseException("Review Vote ID " + reviewVoteId + " not found.");
        }
        final Row row = rows.get(0);

        final ReviewVote reviewVote = new ReviewVote();
        final Date createdDate = DateUtil.dateFromDateString(row.getString("created_date"));
        final Long accountId = row.getLong("account_id");

        // Inflate account
        Account account = null;
        if (accountId != null) {
            final AccountInflater accountInflater = new AccountInflater(_databaseConnection);
            account = accountInflater.inflateAccount(accountId);
        }

        reviewVote.setId(reviewVoteId);
        reviewVote.setReviewId(row.getLong("review_id"));
        reviewVote.setAccount(account);
        reviewVote.setCreatedDate(createdDate);
        reviewVote.setIsUpvote(row.getBoolean("is_upvote"));
        return reviewVote;
    }
}
