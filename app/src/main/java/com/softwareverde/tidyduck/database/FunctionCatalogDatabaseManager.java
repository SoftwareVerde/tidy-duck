package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.Query;
import com.softwareverde.database.Row;
import com.softwareverde.tidyduck.Review;
import com.softwareverde.tidyduck.most.FunctionBlock;
import com.softwareverde.tidyduck.most.FunctionCatalog;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

class FunctionCatalogDatabaseManager {
    private final DatabaseConnection<Connection> _databaseConnection;

    public FunctionCatalogDatabaseManager(final DatabaseConnection<Connection> databaseConnection) {
        _databaseConnection = databaseConnection;
    }

    /**
     * Stores the functionCatalog's release, releaseDate, accountId, and companyId via the databaseConnection.
     * Upon successful insert, the functionCatalog's Id is set to the database's insertId.
     */
    private void _insertFunctionCatalog(final FunctionCatalog functionCatalog, final FunctionCatalog priorFunctionCatalog) throws DatabaseException {
        final String name = functionCatalog.getName();
        final String release = functionCatalog.getRelease();
        final Long accountId = functionCatalog.getAuthor().getId();
        final Long companyId = functionCatalog.getCompany().getId();
        final Long priorVersionId = priorFunctionCatalog != null ? priorFunctionCatalog.getId() : null;

        final Query query = new Query("INSERT INTO function_catalogs (name, release_version, account_id, company_id, prior_version_id) VALUES (?, ?, ?, ?, ?)")
            .setParameter(name)
            .setParameter(release)
            .setParameter(accountId)
            .setParameter(companyId)
            .setParameter(priorVersionId)
        ;

        final long functionCatalogId = _databaseConnection.executeSql(query);
        functionCatalog.setId(functionCatalogId);

        if (priorFunctionCatalog == null) {
            _setBaseVersionId(functionCatalogId, functionCatalogId);
        } else {
            _setBaseVersionId(functionCatalogId, priorFunctionCatalog.getBaseVersionId());
        }
    }

    private void _insertFunctionCatalog(final FunctionCatalog functionCatalog) throws DatabaseException {
        _insertFunctionCatalog(functionCatalog, null);
    }

    private void _setBaseVersionId(long functionCatalogId, long baseVersionId) throws DatabaseException {
        final Query query = new Query("UPDATE function_catalogs SET base_version_id = ? WHERE id = ?")
            .setParameter(baseVersionId)
            .setParameter(functionCatalogId)
        ;

        _databaseConnection.executeSql(query);
    }

    private void _updateUnapprovedFunctionCatalog(FunctionCatalog proposedFunctionCatalog) throws DatabaseException {
        final String newName = proposedFunctionCatalog.getName();
        final String newReleaseVersion = proposedFunctionCatalog.getRelease();
        final long newAuthorId = proposedFunctionCatalog.getAuthor().getId();
        final long newCompanyId = proposedFunctionCatalog.getCompany().getId();
        final long functionCatalogId = proposedFunctionCatalog.getId();

        final Query query = new Query("UPDATE function_catalogs SET name = ?, release_version = ?, account_id = ?, company_id = ?, is_approved = ? WHERE id = ?")
            .setParameter(newName)
            .setParameter(newReleaseVersion)
            .setParameter(newAuthorId)
            .setParameter(newCompanyId)
            .setParameter(false)
            .setParameter(functionCatalogId)
        ;

        _databaseConnection.executeSql(query);
    }

    private void _deleteFunctionCatalogIfUnapproved(final long functionCatalogId) throws DatabaseException {
        final FunctionCatalogInflater functionCatalogInflater = new FunctionCatalogInflater(_databaseConnection);
        final FunctionCatalog functionCatalog = functionCatalogInflater.inflateFunctionCatalog(functionCatalogId);

        if (! functionCatalog.isReleased()) {
            // function catalog isn't approved, we can delete it
            _deleteFunctionBlocksFromFunctionCatalog(functionCatalogId);
            _deleteReviewForFunctionCatalog(functionCatalogId);
            _deleteFunctionCatalogFromDatabase(functionCatalogId);
        }
    }

    private void _deleteFunctionBlocksFromFunctionCatalog(final long functionCatalogId) throws DatabaseException {
        final FunctionBlockInflater functionBlockInflater = new FunctionBlockInflater(_databaseConnection);
        final List<FunctionBlock> functionBlocks = functionBlockInflater.inflateFunctionBlocksFromFunctionCatalogId(functionCatalogId);

        final FunctionBlockDatabaseManager functionBlockDatabaseManager = new FunctionBlockDatabaseManager(_databaseConnection);
        for (final FunctionBlock functionBlock : functionBlocks) {
            functionBlockDatabaseManager.deleteFunctionBlockFromFunctionCatalog(functionCatalogId, functionBlock.getId());
        }
    }

    private void _deleteFunctionCatalogFromDatabase(final long functionCatalogId) throws DatabaseException {
        final Query query = new Query("DELETE FROM function_catalogs WHERE id = ?")
            .setParameter(functionCatalogId)
        ;

        _databaseConnection.executeSql(query);
    }

    public void insertFunctionCatalog(final FunctionCatalog functionCatalog) throws DatabaseException {
        _insertFunctionCatalog(functionCatalog);
    }

    public void updateFunctionCatalog(final FunctionCatalog functionCatalog) throws DatabaseException {
        final long inputFunctionCatalogId = functionCatalog.getId();

        final FunctionCatalogInflater functionCatalogInflater = new FunctionCatalogInflater(_databaseConnection);
        final FunctionCatalog originalFunctionCatalog = functionCatalogInflater.inflateFunctionCatalog(inputFunctionCatalogId);
        if (originalFunctionCatalog.isApproved()) {
            // need to insert a new function catalog replace this one
            _insertFunctionCatalog(functionCatalog, originalFunctionCatalog);
            final long newFunctionCatalogId = functionCatalog.getId();
            _copyFunctionCatalogFunctionBlocksAssociations(inputFunctionCatalogId, newFunctionCatalogId);
        }
        else {
            _updateUnapprovedFunctionCatalog(functionCatalog);
        }
    }

    public void deleteFunctionCatalog(final long functionCatalogId) throws DatabaseException {
        _deleteFunctionCatalogIfUnapproved(functionCatalogId);
    }

    private void _copyFunctionCatalogFunctionBlocksAssociations(final long originalFunctionCatalogId, final long newFunctionCatalogId) throws DatabaseException {
        final FunctionBlockInflater functionBlockInflater = new FunctionBlockInflater(_databaseConnection);
        final List<FunctionBlock> functionBlocks = functionBlockInflater.inflateFunctionBlocksFromFunctionCatalogId(originalFunctionCatalogId);

        final FunctionBlockDatabaseManager functionBlockDatabaseManager = new FunctionBlockDatabaseManager(_databaseConnection);
        for (final FunctionBlock functionBlock : functionBlocks) {
            functionBlockDatabaseManager.associateFunctionBlockWithFunctionCatalog(newFunctionCatalogId, functionBlock.getId());
        }
    }

    public void submitFunctionCatalogForReview(final Long functionCatalogId, final Long accountId) throws DatabaseException {
        if (_functionCatalogHasReview(functionCatalogId)) {
            // already present, return
            return;
        }
        _submitFunctionCatalogForReview(functionCatalogId, accountId);
    }

    private boolean _functionCatalogHasReview(final long functionCatalogId) throws DatabaseException {
        final Query query = new Query("SELECT * FROM reviews WHERE function_catalog_id = ?");
        query.setParameter(functionCatalogId);

        List<Row> rows = _databaseConnection.query(query);
        return rows.size() > 0;
    }

    private void _submitFunctionCatalogForReview(final Long functionCatalogId, final Long accountId) throws DatabaseException {
        final Query query = new Query("INSERT INTO reviews (function_catalog_id, account_id, created_date) VALUES (?, ?, NOW())");
        query.setParameter(functionCatalogId);
        query.setParameter(accountId);

        _databaseConnection.executeSql(query);
    }

    public void approveFunctionCatalog(final long functionCatalogId) throws DatabaseException {
        final Query query = new Query("UPDATE function_catalogs SET is_approved = ? WHERE id = ?")
                .setParameter(true)
                .setParameter(functionCatalogId);

        _databaseConnection.executeSql(query);

        _approveFunctionBlocksForFunctionCatalogId(functionCatalogId);
    }

    private void _approveFunctionBlocksForFunctionCatalogId(final long functionCatalogId) throws DatabaseException {
        final FunctionBlockInflater functionBlockInflater = new FunctionBlockInflater(_databaseConnection);
        final List<FunctionBlock> functionBlocks = functionBlockInflater.inflateFunctionBlocksFromFunctionCatalogId(functionCatalogId);

        final FunctionBlockDatabaseManager functionBlockDatabaseManager = new FunctionBlockDatabaseManager(_databaseConnection);
        for (final FunctionBlock functionBlock : functionBlocks) {
            functionBlockDatabaseManager.approveFunctionBlock(functionBlock.getId());
        }
    }

    private void _deleteReviewForFunctionCatalog(final long functionCatalogId) throws DatabaseException {
        final Query query = new Query("SELECT * FROM reviews WHERE function_catalog_id = ?")
                .setParameter(functionCatalogId);

        final List<Row> rows = _databaseConnection.query(query);
        final List<Review> reviews = new ArrayList<>();

        // Inflate reviews
        final ReviewInflater reviewInflater = new ReviewInflater(_databaseConnection);
        for (Row row : rows) {
            final Review review = reviewInflater._convertRowToReview(row);
            reviews.add(review);
        }

        final ReviewDatabaseManager reviewDatabaseManager = new ReviewDatabaseManager(_databaseConnection);
        for (Review review: reviews) {
            reviewDatabaseManager.deleteReview(review);
        }
    }

}
