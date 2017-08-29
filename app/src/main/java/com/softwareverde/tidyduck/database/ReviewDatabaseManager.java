package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.Query;
import com.softwareverde.tidyduck.Account;
import com.softwareverde.tidyduck.most.*;
import com.softwareverde.tidyduck.Review;

import java.text.SimpleDateFormat;
import java.util.Date;

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

        final Query query = new Query("INSERT INTO reviews (function_catalog_id, function_block_id, interface_id, function_id, account_id, created_date) VALUES (?, ?, ?, ?, ?, ?)")
                .setParameter(functionCatalogId)
                .setParameter(functionBlockId)
                .setParameter(mostInterfaceId)
                .setParameter(mostFunctionId)
                .setParameter(accountId)
                .setParameter(createdDateString)
        ;

        final long reviewId = _databaseConnection.executeSql(query);
        review.setId(reviewId);
    }
}
