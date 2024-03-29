package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.query.Query;
import com.softwareverde.database.row.Row;
import com.softwareverde.logging.Logger;
import com.softwareverde.tidyduck.AccountId;
import com.softwareverde.tidyduck.DateUtil;
import com.softwareverde.tidyduck.most.MostFunction;
import com.softwareverde.tidyduck.most.MostInterface;

import java.sql.Connection;
import java.util.*;

public class MostInterfaceInflater {
    private final DatabaseConnection<Connection> _databaseConnection;

    public MostInterfaceInflater(DatabaseConnection<Connection> databaseConnection) {
        _databaseConnection = databaseConnection;
    }

    public List<MostInterface> inflateMostInterfaces() throws DatabaseException {
        final Query query = new Query(
                "SELECT interfaces.*, COALESCE(SUM(function_blocks.is_approved AND NOT function_blocks.is_permanently_deleted) > 0, 0) AS has_approved_parent FROM interfaces\n" +
                        "LEFT JOIN function_blocks_interfaces ON interfaces.id = function_blocks_interfaces.interface_id\n" +
                        "LEFT JOIN function_blocks ON function_blocks.id = function_blocks_interfaces.function_block_id\n" +
                        "WHERE interfaces.is_permanently_deleted = 0 GROUP BY interfaces.id"
        );

        final List<MostInterface> mostInterfaces = new ArrayList<>();
        final List<Row> rows = _databaseConnection.query(query);
        for (final Row row : rows) {
            final MostInterface mostInterface = convertRowToMostInterface(row);
            mostInterfaces.add(mostInterface);
        }
        return mostInterfaces;
    }

    public List<MostInterface> inflateTrashedMostInterfaces() throws DatabaseException {
        final Query query = new Query(
                "SELECT interfaces.*, COALESCE(SUM(function_blocks.is_approved AND NOT function_blocks.is_permanently_deleted) > 0, 0) AS has_approved_parent FROM interfaces\n" +
                        "LEFT JOIN function_blocks_interfaces ON interfaces.id = function_blocks_interfaces.interface_id\n" +
                        "LEFT JOIN function_blocks ON function_blocks.id = function_blocks_interfaces.function_block_id\n" +
                        "WHERE interfaces.is_deleted = 1 AND interfaces.is_permanently_deleted = 0 GROUP BY interfaces.id"
        );

        final List<MostInterface> mostInterfaces = new ArrayList<>();
        final List<Row> rows = _databaseConnection.query(query);
        for (final Row row : rows) {
            final MostInterface mostInterface = convertRowToMostInterface(row);
            mostInterfaces.add(mostInterface);
        }
        return mostInterfaces;
    }

    public Map<Long, List<MostInterface>> inflateMostInterfacesGroupedByBaseVersionId() throws DatabaseException {
        final List<MostInterface> mostInterfaces = inflateMostInterfaces();
        return groupByBaseVersionId(mostInterfaces);
    }

    public Map<Long, List<MostInterface>> inflateTrashedMostInterfacesGroupedByBaseVersionId() throws DatabaseException {
        final List<MostInterface> mostInterfaces = inflateTrashedMostInterfaces();
        return groupByBaseVersionId(mostInterfaces);
    }

    private Map<Long,List<MostInterface>> groupByBaseVersionId(List<MostInterface> mostInterfaces) {
        final HashMap<Long, List<MostInterface>> groupedFunctionBlocks = new HashMap<>();

        for (final MostInterface functionBlock : mostInterfaces) {
            final Long baseVersionId = functionBlock.getBaseVersionId();
            if (!groupedFunctionBlocks.containsKey(baseVersionId)) {
                groupedFunctionBlocks.put(baseVersionId, new ArrayList<>());
            }
            groupedFunctionBlocks.get(baseVersionId).add(functionBlock);
        }

        return groupedFunctionBlocks;
    }

    public List<MostInterface> inflateMostInterfacesFromFunctionBlockId(final long functionBlockId) throws DatabaseException {
        return inflateMostInterfacesFromFunctionBlockId(functionBlockId, true, false);
    }

    public List<MostInterface> inflateMostInterfacesFromFunctionBlockId(final long functionBlockId, final boolean includeDeleted, final boolean inflateChildren) throws DatabaseException {
        final Query query = new Query(
            "SELECT interface_id FROM function_blocks_interfaces WHERE function_block_id = ?" + (includeDeleted ? "" : " AND is_deleted = 0")
        );
        query.setParameter(functionBlockId);

        final List<MostInterface> mostInterfaces = new ArrayList<>();
        final List<Row> rows = _databaseConnection.query(query);
        for (final Row row : rows) {
            final long mostInterfaceId = row.getLong("interface_id");
            MostInterface mostInterface = inflateMostInterface(mostInterfaceId, inflateChildren);
            mostInterfaces.add(mostInterface);
        }
        return mostInterfaces;
    }

    public Map<Long, List<MostInterface>> inflateMostInterfacesMatchingSearchString(final String searchString, final boolean includeDeleted, final AccountId accountId) throws DatabaseException {
        // Recall that "LIKE" is case-insensitive for MySQL: https://stackoverflow.com/a/14007477/3025921
        final Query query = new Query (
                "SELECT interfaces.*, COALESCE(SUM(function_blocks.is_approved AND NOT function_blocks.is_permanently_deleted) > 0, 0) AS has_approved_parent FROM interfaces\n" +
                        "LEFT JOIN function_blocks_interfaces ON interfaces.id = function_blocks_interfaces.interface_id\n" +
                        "LEFT JOIN function_blocks ON function_blocks.id = function_blocks_interfaces.function_block_id\n" +
                            "WHERE interfaces.base_version_id IN (" +
                                    "SELECT DISTINCT interfaces.base_version_id\n" +
                                    "FROM interfaces\n" +
                                        "WHERE interfaces.name LIKE ?\n" +
                                        "AND (is_approved = 1 OR creator_account_id = ? OR creator_account_id IS NULL)\n" +
                            ")\n" +
                        "AND (interfaces.is_approved = 1 OR interfaces.creator_account_id = ? OR interfaces.creator_account_id IS NULL)\n" +
                        (includeDeleted ? "" : "AND interfaces.is_deleted = 0\n") +
                        "AND interfaces.is_permanently_deleted = 0 GROUP BY interfaces.id");
        query.setParameter("%" + searchString + "%");
        query.setParameter(accountId);
        query.setParameter(accountId);

        final List<MostInterface> mostInterfaces = new ArrayList<>();
        final List<Row> rows = _databaseConnection.query(query);
        for (final Row row : rows) {
            final MostInterface mostInterface = convertRowToMostInterface(row);
            mostInterfaces.add(mostInterface);
        }
        return groupByBaseVersionId(mostInterfaces);
    }

    public MostInterface inflateMostInterface(final long mostInterfaceId) throws DatabaseException {
        return inflateMostInterface(mostInterfaceId, false);
    }

    public MostInterface inflateMostInterface(final long mostInterfaceId, final boolean inflateChildren) throws DatabaseException {
        final Query query = new Query(
                "SELECT interfaces.*, COALESCE(SUM(function_blocks.is_approved AND NOT function_blocks.is_permanently_deleted) > 0, 0) AS has_approved_parent FROM interfaces\n" +
                        "LEFT JOIN function_blocks_interfaces ON interfaces.id = function_blocks_interfaces.interface_id\n" +
                        "LEFT JOIN function_blocks ON function_blocks.id = function_blocks_interfaces.function_block_id\n" +
                        "WHERE interfaces.id = ? GROUP BY interfaces.id"
        );
        query.setParameter(mostInterfaceId);

        final List<Row> rows = _databaseConnection.query(query);
        if (rows.size() == 0) {
            Logger.error("Interface ID " + mostInterfaceId + " not found.");
            return null;
        }

        final Row row = rows.get(0);
        final MostInterface mostInterface = convertRowToMostInterface(row);

        if (inflateChildren) {
            inflateChildren(mostInterface);
        }

        return mostInterface;
    }

    private void inflateChildren(final MostInterface mostInterface) throws DatabaseException {
        MostFunctionInflater mostFunctionInflater = new MostFunctionInflater(_databaseConnection);
        List<MostFunction> mostFunctions = mostFunctionInflater.inflateMostFunctionsFromMostInterfaceId(mostInterface.getId(), false);
        mostInterface.setMostFunctions(mostFunctions);
    }

    private MostInterface convertRowToMostInterface(final Row row) {
        final Long id = row.getLong("id");
        final String mostId = row.getString("most_id");
        final String name = row.getString("name");
        final String description = row.getString("description");
        final Date lastModifiedDate = DateUtil.dateFromDateString(row.getString("last_modified_date"));
        final String version = row.getString("version");
        final boolean isDeleted = row.getBoolean("is_deleted");
        final String deletedDateString = row.getString("deleted_date");
        Date deletedDate = null;
        if (deletedDateString != null) {
            deletedDate = DateUtil.dateFromDateTimeString(deletedDateString);
        }
        final boolean isPermanentlyDeleted = row.getBoolean("is_permanently_deleted");
        final String permanentlyDeletedDateString = row.getString("permanently_deleted_date");
        Date permanentlyDeletedDate = null;
        if (permanentlyDeletedDateString != null) {
            permanentlyDeletedDate = DateUtil.dateFromDateTimeString(permanentlyDeletedDateString);
        }
        final boolean isApproved = row.getBoolean("is_approved");
        final Long approvalReviewId = row.getLong("approval_review_id");
        final boolean hasApprovedParent = row.getBoolean("has_approved_parent");
        final boolean isReleased = row.getBoolean("is_released");
        final Long baseVersionId = row.getLong("base_version_id");
        final Long priorVersionId = row.getLong("prior_version_id");
        final AccountId creatorAccountId = AccountId.wrap(row.getLong("creator_account_id"));

        final MostInterface mostInterface = new MostInterface();
        mostInterface.setId(id);
        mostInterface.setMostId(mostId);
        mostInterface.setName(name);
        mostInterface.setDescription(description);
        mostInterface.setLastModifiedDate(lastModifiedDate);
        mostInterface.setVersion(version);
        mostInterface.setIsDeleted(isDeleted);
        mostInterface.setDeletedDate(deletedDate);
        mostInterface.setIsPermanentlyDeleted(isPermanentlyDeleted);
        mostInterface.setPermanentlyDeletedDate(permanentlyDeletedDate);
        mostInterface.setIsApproved(isApproved);
        mostInterface.setApprovalReviewId(approvalReviewId);
        mostInterface.setHasApprovedParent(hasApprovedParent);
        mostInterface.setIsReleased(isReleased);
        mostInterface.setBaseVersionId(baseVersionId);
        mostInterface.setPriorVersionId(priorVersionId);
        mostInterface.setCreatorAccountId(creatorAccountId);

        return mostInterface;
    }
}
