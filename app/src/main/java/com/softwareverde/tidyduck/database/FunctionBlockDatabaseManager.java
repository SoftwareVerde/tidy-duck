package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.Query;
import com.softwareverde.database.Row;
import com.softwareverde.tidyduck.DateUtil;
import com.softwareverde.tidyduck.Review;
import com.softwareverde.tidyduck.most.FunctionBlock;
import com.softwareverde.tidyduck.most.MostFunction;
import com.softwareverde.tidyduck.most.MostInterface;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FunctionBlockDatabaseManager {

    private final DatabaseConnection<Connection> _databaseConnection;

    public FunctionBlockDatabaseManager(final DatabaseConnection<Connection> databaseConnection) {
        _databaseConnection = databaseConnection;
    }

    public void insertFunctionBlockForFunctionCatalog(final Long functionCatalogId, final FunctionBlock functionBlock, final Long accountId) throws DatabaseException {
        _insertFunctionBlock(functionBlock);
        _associateFunctionBlockWithFunctionCatalog(functionCatalogId, functionBlock.getId());
    }

    public void insertOrphanedFunctionBlock(final FunctionBlock functionBlock) throws DatabaseException {
        _insertFunctionBlock(functionBlock);
    }

    public long forkFunctionBlock(final long functionBlockId, final Long parentFunctionCatalogId, final long currentAccountId) throws DatabaseException {
        final FunctionBlockInflater functionBlockInflater = new FunctionBlockInflater(_databaseConnection);
        final FunctionBlock functionBlock = functionBlockInflater.inflateFunctionBlock(functionBlockId);

        final long newFunctionBlockId = _forkFunctionBlock(functionBlock, currentAccountId);
        _copyFunctionBlockInterfacesAssociations(functionBlockId, newFunctionBlockId);
        if (parentFunctionCatalogId != null) {
            _associateFunctionBlockWithFunctionCatalog(parentFunctionCatalogId, functionBlockId);
        }
        return newFunctionBlockId;
    }

    private void _insertFunctionBlock(final FunctionBlock functionBlock, final FunctionBlock priorFunctionBlock) throws DatabaseException {
        final String mostId = functionBlock.getMostId();
        final String kind = functionBlock.getKind();
        final String name = functionBlock.getName();
        final String description = functionBlock.getDescription();
        final String release = functionBlock.getRelease();
        final Long authorId = functionBlock.getAuthor().getId();
        final Long companyId = functionBlock.getCompany().getId();
        final String access = functionBlock.getAccess();
        final boolean isSource = functionBlock.isSource();
        final boolean isSink = functionBlock.isSink();
        final Long priorVersionId = priorFunctionBlock != null ? priorFunctionBlock.getId() : null;
        final Long creatorAccountId = functionBlock.getCreatorAccountId();

        final Query query = new Query("INSERT INTO function_blocks (most_id, kind, name, description, last_modified_date, release_version, account_id, company_id, access, is_source, is_sink, prior_version_id, creator_account_id) VALUES (?, ?, ?, ?, NOW(), ?, ?, ?, ?, ?, ?, ?, ?)")
            .setParameter(mostId)
            .setParameter(kind)
            .setParameter(name)
            .setParameter(description)
            .setParameter(release)
            .setParameter(authorId)
            .setParameter(companyId)
            .setParameter(access)
            .setParameter(isSource)
            .setParameter(isSink)
            .setParameter(priorVersionId)
            .setParameter(creatorAccountId)
        ;

        final long functionBlockId = _databaseConnection.executeSql(query);
        functionBlock.setId(functionBlockId);
        _setBaseVersionId(functionBlockId, functionBlockId);
    }

    private void _insertFunctionBlock(final FunctionBlock functionBlock) throws DatabaseException {
        _insertFunctionBlock(functionBlock, null);
    }

    private long _forkFunctionBlock(final FunctionBlock functionBlock, final Long currentAccountId) throws DatabaseException {
        final String mostId = functionBlock.getMostId();
        final String kind = functionBlock.getKind();
        final String name = functionBlock.getName();
        final String description = functionBlock.getDescription();
        final String release = functionBlock.getRelease();
        final Long authorId = functionBlock.getAuthor().getId();
        final Long companyId = functionBlock.getCompany().getId();
        final String access = functionBlock.getAccess();
        final boolean isSource = functionBlock.isSource();
        final boolean isSink = functionBlock.isSink();
        final Long priorVersionId = functionBlock.getId();
        final Long creatorAccountId = currentAccountId;
        final Long baseVersionId = functionBlock.getBaseVersionId();

        final Query query = new Query("INSERT INTO function_blocks (most_id, kind, name, description, last_modified_date, release_version, account_id, company_id, access, is_source, is_sink, prior_version_id, creator_account_id, base_version_id) VALUES (?, ?, ?, ?, NOW(), ?, ?, ?, ?, ?, ?, ?, ?, ?)")
                .setParameter(mostId)
                .setParameter(kind)
                .setParameter(name)
                .setParameter(description)
                .setParameter(release)
                .setParameter(authorId)
                .setParameter(companyId)
                .setParameter(access)
                .setParameter(isSource)
                .setParameter(isSink)
                .setParameter(priorVersionId)
                .setParameter(creatorAccountId)
                .setParameter(baseVersionId)
                ;

        final long functionBlockId = _databaseConnection.executeSql(query);
        return functionBlockId;
    }

    private void _setBaseVersionId(long functionBlockId, long baseVersionId) throws DatabaseException {
        final Query query = new Query("UPDATE function_blocks SET base_version_id = ? WHERE id = ?")
            .setParameter(baseVersionId)
            .setParameter(functionBlockId)
        ;

        _databaseConnection.executeSql(query);
    }

    private void _setCreatorAccountId(long functionBlockId, long accountId) throws DatabaseException {
        final Query query = new Query("UPDATE function_blocks SET creator_account_id = ? WHERE id = ?")
                .setParameter(accountId)
                .setParameter(functionBlockId)
        ;

        _databaseConnection.executeSql(query);
    }

    private Long _associateFunctionBlockWithFunctionCatalog(final long functionCatalogId, final long functionBlockId) throws DatabaseException {
        final Query query = new Query("INSERT INTO function_catalogs_function_blocks (function_catalog_id, function_block_id) VALUES (?, ?)")
            .setParameter(functionCatalogId)
            .setParameter(functionBlockId)
        ;

        return _databaseConnection.executeSql(query);
    }

    private boolean _isAssociatedWithFunctionCatalog(final long functionCatalogId, final long functionBlockId) throws DatabaseException {
        final Query query = new Query("SELECT id FROM function_catalogs_function_blocks WHERE function_catalog_id = ? AND function_block_id = ?")
            .setParameter(functionCatalogId)
            .setParameter(functionBlockId)
        ;

        List<Row> rows = _databaseConnection.query(query);

        return rows.size() > 0;
    }

    private void _updateUnapprovedFunctionBlock(final FunctionBlock proposedFunctionBlock) throws DatabaseException {
        final String newMostId = proposedFunctionBlock.getMostId();
        final String newKind = proposedFunctionBlock.getKind();
        final String newName = proposedFunctionBlock.getName();
        final String newReleaseVersion = proposedFunctionBlock.getRelease();
        final String newDescription = proposedFunctionBlock.getDescription();
        final String newAccess = proposedFunctionBlock.getAccess();
        final boolean isSource = proposedFunctionBlock.isSource();
        final boolean isSink = proposedFunctionBlock.isSink();
        final long newAuthorId = proposedFunctionBlock.getAuthor().getId();
        final long newCompanyId = proposedFunctionBlock.getCompany().getId();
        final long functionBlockId = proposedFunctionBlock.getId();
        final Long creatorAccountId = proposedFunctionBlock.getCreatorAccountId();

        final Query query = new Query("UPDATE function_blocks SET most_id = ?, kind = ?, name = ?, description = ?, last_modified_date = NOW(), release_version = ?, account_id = ?, company_id = ?, access = ?, is_source = ?, is_sink = ?, is_approved = ?, creator_account_id = ? WHERE id = ?")
            .setParameter(newMostId)
            .setParameter(newKind)
            .setParameter(newName)
            .setParameter(newDescription)
            .setParameter(newReleaseVersion)
            .setParameter(newAuthorId)
            .setParameter(newCompanyId)
            .setParameter(newAccess)
            .setParameter(isSource)
            .setParameter(isSink)
            .setParameter(false)
            .setParameter(creatorAccountId)
            .setParameter(functionBlockId)
        ;

        _databaseConnection.executeSql(query);
    }

    private void _disassociateFunctionBlockWithFunctionCatalog(final long functionCatalogId, final long functionBlockId) throws DatabaseException {
        final Query query = new Query("DELETE FROM function_catalogs_function_blocks WHERE function_catalog_id = ? and function_block_id = ?")
            .setParameter(functionCatalogId)
            .setParameter(functionBlockId)
        ;

        _databaseConnection.executeSql(query);
    }

    private void _disassociateFunctionBlockFromAllUnapprovedFunctionCatalogs(final long functionBlockId) throws DatabaseException {
        final Query query = new Query("DELETE FROM function_catalogs_function_blocks WHERE function_block_id = ? and function_catalog_id IN (" +
                                        "SELECT DISTINCT function_catalogs.id\n" +
                                                "FROM function_catalogs\n" +
                                                "WHERE function_catalogs.is_approved=0)")
                .setParameter(functionBlockId);

        _databaseConnection.executeSql(query);

    }

    public void setIsDeletedForFunctionBlock(final long functionBlockId, final boolean isDeleted) throws DatabaseException {
        final Query query = new Query("UPDATE function_blocks SET is_deleted = ?, deleted_date = ? WHERE id = ?")
                .setParameter(isDeleted)
                .setParameter(isDeleted ? DateUtil.dateToDateString(new Date()) : null)
                .setParameter(functionBlockId)
                ;

        _databaseConnection.executeSql(query);

        _setIsDeletedForFunctionBlockParentAssociations(functionBlockId, isDeleted);
    }

    private void _setIsDeletedForFunctionBlockParentAssociations(final long functionBlockId, final boolean isDeleted) throws DatabaseException {
        final Query query = new Query("UPDATE function_catalogs_function_blocks SET is_deleted = ? WHERE function_block_id = ?")
                .setParameter(isDeleted)
                .setParameter(functionBlockId)
                ;

        _databaseConnection.executeSql(query);
    }

    public long restoreFunctionBlockFromTrash(final long functionBlockId) throws DatabaseException {
        setIsDeletedForFunctionBlock(functionBlockId, false);
        final long numberOfDeletedChildren = _getNumberOfDeletedChildren();

        if (numberOfDeletedChildren > 0) {
            _clearDeletedChildAssociations();
        }

        return numberOfDeletedChildren;
    }

    private void _nullifyFunctionBlockParentRelationships(final long functionBlockId) throws DatabaseException {
        final Query query = new Query("UPDATE function_catalogs_function_blocks SET function_block_id = ? WHERE function_block_id = ?")
                .setParameter(null)
                .setParameter(functionBlockId)
                ;

        _databaseConnection.executeSql(query);
    }

    private long _getNumberOfDeletedChildren() throws DatabaseException {
        final Query query = new Query("SELECT COUNT(*) AS deletions FROM function_blocks_interfaces WHERE interface_id IS NULL");

        final List<Row> rows = _databaseConnection.query(query);
        final Row row = rows.get(0);
        return row.getLong("deletions");
    }

    private void _clearDeletedChildAssociations() throws DatabaseException {
        final Query query = new Query("DELETE FROM function_blocks_interfaces WHERE interface_id IS NULL");
        _databaseConnection.executeSql(query);

    }

    private void _deleteFunctionBlock(final long functionBlockId) throws DatabaseException {
        final FunctionBlockInflater functionBlockInflater = new FunctionBlockInflater(_databaseConnection);
        final FunctionBlock functionBlock = functionBlockInflater.inflateFunctionBlock(functionBlockId);

        if (functionBlock.isReleased()) {
            throw new IllegalStateException("Released function blocks cannot be deleted.");
        }

        if (!functionBlock.isDeleted()) {
            throw new IllegalStateException("Only trashed items can be deleted.");
        }

        if (functionBlock.isApproved()) {
            // approved, be careful
            _markAsPermanentlyDeleted(functionBlockId);
        }
        else {
            // not approved, fully delete
            _deleteInterfacesFromFunctionBlock(functionBlockId);
            _disassociateFunctionBlockFromAllUnapprovedFunctionCatalogs(functionBlockId);
            _deleteReviewForFunctionBlock(functionBlockId);
            _deleteFunctionBlockFromDatabase(functionBlockId);
        }
    }

    private void _markAsPermanentlyDeleted(final long functionBlockId) throws DatabaseException {
        final Query query = new Query("UPDATE function_blocks SET is_permanently_deleted = 1, permanently_deleted_date = NOW() WHERE id = ?")
                .setParameter(functionBlockId)
                ;

        _databaseConnection.executeSql(query);
    }

    private void _deleteInterfacesFromFunctionBlock(final long functionBlockId) throws DatabaseException {
        final MostInterfaceInflater mostInterfaceInflater = new MostInterfaceInflater(_databaseConnection);
        final List<MostInterface> mostInterfaces = mostInterfaceInflater.inflateMostInterfacesFromFunctionBlockId(functionBlockId);

        final MostInterfaceDatabaseManager mostInterfaceDatabaseManager = new MostInterfaceDatabaseManager(_databaseConnection);
        for (final MostInterface mostInterface : mostInterfaces) {
            // disassociate from function block
            mostInterfaceDatabaseManager.disassociateMostInterfaceFromFunctionBlock(functionBlockId, mostInterface.getId());
        }
    }

    private void _deleteFunctionBlockFromDatabase(final long functionBlockId) throws DatabaseException {
        final Query query = new Query("DELETE FROM function_blocks WHERE id = ?")
            .setParameter(functionBlockId)
        ;

        _databaseConnection.executeSql(query);
    }

    /**
     * Returns true if and only if the function block with the given idea is not associated with any function catalog.
     * @return
     */
    private boolean _isOrphaned(final long functionBlockId) throws DatabaseException {
        final Query query = new Query("SELECT COUNT(*) AS association_count FROM function_catalogs_function_blocks WHERE function_block_id = ?")
            .setParameter(functionBlockId)
        ;

        final List<Row> rows = _databaseConnection.query(query);

        final Row row = rows.get(0);
        final long associationCount = row.getLong("association_count");
        return (associationCount == 0);
    }

    public void associateFunctionBlockWithFunctionCatalog(final Long functionCatalogId, final long functionBlockId) throws DatabaseException {
        if (! _isAssociatedWithFunctionCatalog(functionCatalogId, functionBlockId)) {
            _associateFunctionBlockWithFunctionCatalog(functionCatalogId, functionBlockId);
        }
    }

    public void disassociateFunctionBlockFromFunctionCatalog(final long functionCatalogId, final long functionBlockId) throws DatabaseException {
        _disassociateFunctionBlockWithFunctionCatalog(functionCatalogId, functionBlockId);
    }

    public void updateFunctionBlock(final FunctionBlock updatedFunctionBlock, final Long accountId) throws DatabaseException {
        _updateUnapprovedFunctionBlock(updatedFunctionBlock);
    }

    private void _copyFunctionBlockInterfacesAssociations(final long originalFunctionBlockId, final long newFunctionBlockId) throws DatabaseException {
        final MostInterfaceInflater mostInterfaceInflater = new MostInterfaceInflater(_databaseConnection);
        final List<MostInterface> mostInterfaces = mostInterfaceInflater.inflateMostInterfacesFromFunctionBlockId(originalFunctionBlockId);

        final MostInterfaceDatabaseManager mostInterfaceDatabaseManager = new MostInterfaceDatabaseManager(_databaseConnection);
        for (final MostInterface mostInterface : mostInterfaces) {
            mostInterfaceDatabaseManager.associateMostInterfaceWithFunctionBlock(newFunctionBlockId, mostInterface.getId());
        }
    }

    public void deleteFunctionBlock(final long functionBlockId) throws DatabaseException {
        _deleteFunctionBlock(functionBlockId);
    }

    public List<Long> listFunctionCatalogIdsContainingFunctionBlock(final long functionBlockId) throws DatabaseException {
        final Query query = new Query("SELECT DISTINCT function_catalogs_function_blocks.function_catalog_id\n" +
                                        "FROM function_catalogs_function_blocks\n" +
                                        "WHERE function_catalogs_function_blocks.function_block_id = ?"
        );
        query.setParameter(functionBlockId);

        final List<Row> rows =_databaseConnection.query(query);
        final ArrayList<Long> functionCatalogIds = new ArrayList<>();
        for (final Row row : rows) {
            final Long functionCatalogId = row.getLong("function_catalog_id");
            functionCatalogIds.add(functionCatalogId);
        }
        return functionCatalogIds;
    }

    public void submitFunctionBlockForReview(final long functionBlockId, final Long accountId) throws DatabaseException {
        if (_functionBlockHasReview(functionBlockId)) {
            // already present, return
            return;
        }
        _submitFunctionBlockForReview(functionBlockId, accountId);
    }

    private boolean _functionBlockHasReview(final long functionBlockId) throws DatabaseException {
        final Query query = new Query("SELECT * FROM reviews WHERE function_block_id = ?");
        query.setParameter(functionBlockId);

        List<Row> rows = _databaseConnection.query(query);
        return rows.size() > 0;
    }

    private void _submitFunctionBlockForReview(final long functionBlockId, final Long accountId) throws DatabaseException {
        final Query query = new Query("INSERT INTO reviews (function_block_id, account_id, created_date) VALUES (?, ?, NOW())");
        query.setParameter(functionBlockId);
        query.setParameter(accountId);

        _databaseConnection.executeSql(query);
    }

    public void approveFunctionBlock(final long functionBlockId, final long reviewId) throws DatabaseException {
        final Query query = new Query("UPDATE function_blocks SET is_approved = ?, approval_review_id = ? WHERE id = ?")
                .setParameter(true)
                .setParameter(reviewId)
                .setParameter(functionBlockId);

        _databaseConnection.executeSql(query);

        _approveMostInterfacesForFunctionBlockId(functionBlockId, reviewId);
    }

    private void _approveMostInterfacesForFunctionBlockId(final long functionBlockId, final long reviewId) throws DatabaseException {
        final MostInterfaceInflater mostInterfaceInflater = new MostInterfaceInflater(_databaseConnection);
        final List<MostInterface> mostInterfaces = mostInterfaceInflater.inflateMostInterfacesFromFunctionBlockId(functionBlockId);

        final MostInterfaceDatabaseManager mostInterfaceDatabaseManager = new MostInterfaceDatabaseManager(_databaseConnection);
        for (final MostInterface mostInterface : mostInterfaces) {
            mostInterfaceDatabaseManager.approveMostInterface(mostInterface.getId(), reviewId);
        }
    }

    private void _deleteReviewForFunctionBlock(final long functionBlockId) throws DatabaseException {
        final Query query = new Query("SELECT * FROM reviews WHERE function_block_id = ?")
            .setParameter(functionBlockId);

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

    public FunctionBlock checkForDuplicateFunctionBlockName(final String functionBlockName, final Long functionBlockVersionSeriesId) throws DatabaseException {
        return _checkForDuplicateFunctionBlockName(functionBlockName, functionBlockVersionSeriesId);
    }

    private FunctionBlock _checkForDuplicateFunctionBlockName(final String functionBlockName, final Long functionBlockVersionSeriesId) throws DatabaseException {
        final Query query = new Query("SELECT id FROM function_blocks WHERE name = ?");
        query.setParameter(functionBlockName);

        final List<Row> rows = _databaseConnection.query(query);
        final FunctionBlockInflater functionBlockInflater = new FunctionBlockInflater(_databaseConnection);

        FunctionBlock matchedFunctionBlock = null;
        for (final Row row : rows) {
            final long functionBlockId = row.getLong("id");
            final FunctionBlock rowFunctionBlock = functionBlockInflater.inflateFunctionBlock(functionBlockId);

            if (!rowFunctionBlock.getBaseVersionId().equals(functionBlockVersionSeriesId)) {
                matchedFunctionBlock = rowFunctionBlock;
                break;
            }
        }

        return matchedFunctionBlock;
    }

    public FunctionBlock checkForDuplicateFunctionBlockMostId(final String functionBlockMostId, final Long functionBlockVersionSeriesId) throws DatabaseException {
        return _checkForDuplicateFunctionBlockMostId(functionBlockMostId, functionBlockVersionSeriesId);
    }

    private FunctionBlock _checkForDuplicateFunctionBlockMostId(final String functionBlockMostId, final Long functionBlockVersionSeriesId) throws DatabaseException {
        final Query query = new Query("SELECT id FROM function_blocks WHERE most_id = ?");
        query.setParameter(functionBlockMostId);

        final List<Row> rows = _databaseConnection.query(query);
        final FunctionBlockInflater functionBlockInflater = new FunctionBlockInflater(_databaseConnection);

        FunctionBlock matchedFunctionBlock = null;
        for (final Row row : rows) {
            final long functionBlockId = row.getLong("id");
            final FunctionBlock rowFunctionBlock = functionBlockInflater.inflateFunctionBlock(functionBlockId);

            if (!rowFunctionBlock.getBaseVersionId().equals(functionBlockVersionSeriesId)) {
                matchedFunctionBlock = rowFunctionBlock;
                break;
            }
        }

        return matchedFunctionBlock;
    }

    public List<MostFunction> listAssociatedFunctions(final long functionBlockId) throws DatabaseException {
        return _getAssociatedFunctions(functionBlockId);
    }

    private List<MostFunction> _getAssociatedFunctions(final long functionBlockId) throws DatabaseException {
        final List<MostFunction> functions = new ArrayList<>();

        final Query query = new Query("SELECT functions.id FROM functions INNER JOIN interfaces_functions ON functions.id = interfaces_functions.function_id INNER JOIN function_blocks_interfaces ON function_blocks_interfaces.interface_id = interfaces_functions.interface_id WHERE function_block_id = ?");
        query.setParameter(functionBlockId);

        final List<Row> rows = _databaseConnection.query(query);
        MostFunctionInflater mostFunctionInflater = new MostFunctionInflater(_databaseConnection);
        for (final Row row : rows) {
            final long mostFunctionId = row.getLong("id");
            final MostFunction mostFunction = mostFunctionInflater.inflateMostFunction(mostFunctionId);
            functions.add(mostFunction);
        }

        return functions;
    }

    public boolean hasApprovedParents(final long functionBlockId) throws DatabaseException {
        return _hasApprovedParents(functionBlockId);
    }

    private boolean _hasApprovedParents(final long functionBlockId) throws DatabaseException {
        // general check for all parents, even those owned by other users
        final Query query = new Query("SELECT 1 FROM function_catalogs INNER JOIN function_catalogs_function_blocks ON function_catalogs.id = function_catalogs_function_blocks.function_catalog_id WHERE function_block_id = ? and is_approved = 1")
                .setParameter(functionBlockId)
                ;

        List<Row> rows = _databaseConnection.query(query);
        return rows.size() > 0;
    }

}
