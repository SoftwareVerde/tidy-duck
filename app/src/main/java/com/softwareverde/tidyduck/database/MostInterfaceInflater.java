package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.Query;
import com.softwareverde.database.Row;
import com.softwareverde.logging.Logger;
import com.softwareverde.logging.slf4j.Slf4jLogger;
import com.softwareverde.tidyduck.DateUtil;
import com.softwareverde.tidyduck.most.MostFunction;
import com.softwareverde.tidyduck.most.MostInterface;

import java.sql.Connection;
import java.util.*;

public class MostInterfaceInflater {

    protected final Logger _logger = new Slf4jLogger(this.getClass());
    private final DatabaseConnection<Connection> _databaseConnection;

    public MostInterfaceInflater(DatabaseConnection<Connection> databaseConnection) {
        _databaseConnection = databaseConnection;
    }

    public List<MostInterface> inflateMostInterfaces() throws DatabaseException {
        final Query query = new Query(
                "SELECT * FROM interfaces"
        );

        List<MostInterface> mostInterfaces = new ArrayList<>();
        final List<Row> rows = _databaseConnection.query(query);
        for (final Row row : rows) {
            final MostInterface mostInterface = convertRowToMostInterface(row);
            mostInterfaces.add(mostInterface);
        }
        return mostInterfaces;
    }

    public List<MostInterface> inflateTrashedMostInterfaces() throws DatabaseException {
        final Query query = new Query("SELECT * FROM interfaces WHERE is_deleted = 1 and is_permanently_deleted = 0");

        List<MostInterface> mostInterfaces = new ArrayList<>();
        final List<Row> rows = _databaseConnection.query(query);
        for (final Row row : rows) {
            final MostInterface mostInterface = convertRowToMostInterface(row);
            mostInterfaces.add(mostInterface);
        }
        return mostInterfaces;
    }

    public Map<Long, List<MostInterface>> inflateMostInterfacesGroupedByBaseVersionId() throws DatabaseException {
        List<MostInterface> mostInterfaces = inflateMostInterfaces();
        return groupByBaseVersionId(mostInterfaces);
    }

    public Map<Long, List<MostInterface>> inflateTrashedMostInterfacesGroupedByBaseVersionId() throws DatabaseException {
        List<MostInterface> mostInterfaces = inflateTrashedMostInterfaces();
        return groupByBaseVersionId(mostInterfaces);
    }

    private Map<Long,List<MostInterface>> groupByBaseVersionId(List<MostInterface> mostInterfaces) {
        final HashMap<Long, List<MostInterface>> groupedFunctionBlocks = new HashMap<>();

        for (final MostInterface functionBlock : mostInterfaces) {
            Long baseVersionId = functionBlock.getBaseVersionId();
            if (!groupedFunctionBlocks.containsKey(baseVersionId)) {
                groupedFunctionBlocks.put(baseVersionId, new ArrayList<MostInterface>());
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
            "SELECT interface_id FROM function_blocks_interfaces WHERE function_block_id = ?" + (includeDeleted ? "" : " and is_deleted = 0")
        );
        query.setParameter(functionBlockId);

        List<MostInterface> mostInterfaces = new ArrayList<MostInterface>();
        final List<Row> rows = _databaseConnection.query(query);
        for (final Row row : rows) {
            final long mostInterfaceId = row.getLong("interface_id");
            MostInterface mostInterface = inflateMostInterface(mostInterfaceId, inflateChildren);
            mostInterfaces.add(mostInterface);
        }
        return mostInterfaces;
    }

    public Map<Long, List<MostInterface>> inflateMostInterfacesMatchingSearchString(final String searchString, final boolean includeDeleted, final Long accountId) throws DatabaseException {
        // Recall that "LIKE" is case-insensitive for MySQL: https://stackoverflow.com/a/14007477/3025921
        final Query query = new Query ("SELECT * FROM interfaces\n" +
                                        "WHERE base_version_id IN (" +
                                            "SELECT DISTINCT interfaces.base_version_id\n" +
                                            "FROM interfaces\n" +
                                            "WHERE interfaces.name LIKE ?" +
                                        ")\n" +
                                        "AND (is_approved = ? OR creator_account_id = ? OR creator_account_id IS NULL)\n" +
                                        (includeDeleted ? "" : "AND is_deleted = 0"));
        query.setParameter("%" + searchString + "%");
        query.setParameter(true);
        query.setParameter(accountId);

        List<MostInterface> mostInterfaces = new ArrayList<MostInterface>();
        final List<Row> rows = _databaseConnection.query(query);
        for (final Row row : rows) {
            final long mostInterfaceId = row.getLong("id");
            MostInterface mostInterface = inflateMostInterface(mostInterfaceId);
            mostInterfaces.add(mostInterface);
        }
        return groupByBaseVersionId(mostInterfaces);
    }

    public MostInterface inflateMostInterface(final long mostInterfaceId) throws DatabaseException {
        return inflateMostInterface(mostInterfaceId, false);
    }

    public MostInterface inflateMostInterface(final long mostInterfaceId, final boolean inflateChildren) throws DatabaseException {
        final Query query = new Query(
            "SELECT * FROM interfaces WHERE id = ?"
        );
        query.setParameter(mostInterfaceId);

        final List<Row> rows = _databaseConnection.query(query);
        if (rows.size() == 0) {
            _logger.error("Interface ID " + mostInterfaceId + " not found.");
            return null;
        }

        final Row row = rows.get(0);
        MostInterface mostInterface = convertRowToMostInterface(row);

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
        final boolean isReleased = row.getBoolean("is_released");
        final Long baseVersionId = row.getLong("base_version_id");
        final Long priorVersionId = row.getLong("prior_version_id");
        final Long creatorAccountId = row.getLong("creator_account_id");

        MostInterface mostInterface = new MostInterface();
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
        mostInterface.setIsReleased(isReleased);
        mostInterface.setBaseVersionId(baseVersionId);
        mostInterface.setPriorVersionId(priorVersionId);
        mostInterface.setCreatorAccountId(creatorAccountId);

        return mostInterface;
    }
}
