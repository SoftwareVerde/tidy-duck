package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.Query;
import com.softwareverde.database.Row;
import com.softwareverde.logging.Logger;
import com.softwareverde.logging.slf4j.Slf4jLogger;
import com.softwareverde.tidyduck.DateUtil;
import com.softwareverde.tidyduck.MostInterface;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MostInterfaceInflater {

    protected final Logger _logger = new Slf4jLogger(this.getClass());
    private final DatabaseConnection<Connection> _databaseConnection;

    public MostInterfaceInflater(DatabaseConnection<Connection> databaseConnection) {
        _databaseConnection = databaseConnection;
    }

    public List<MostInterface> inflateMostInterfacesFromFunctionBlockId(long functionBlockId) throws DatabaseException {
        final Query query = new Query(
            "SELECT interface_id FROM function_blocks_interfaces WHERE function_block_id = ?"
        );
        query.setParameter(functionBlockId);

        List<MostInterface> mostInterfaces = new ArrayList<MostInterface>();
        final List<Row> rows = _databaseConnection.query(query);
        for (final Row row : rows) {
            final long mostInterfaceId = row.getLong("interface_id");
            MostInterface mostInterface = inflateMostInterface(mostInterfaceId);
            mostInterfaces.add(mostInterface);
        }
        return mostInterfaces;
    }

    public List<MostInterface> inflateMostInterfacesMatchingSearchString(String searchString, Long versionId) throws DatabaseException {
        // Recall that "LIKE" is case-insensitive for MySQL: https://stackoverflow.com/a/14007477/3025921
        final Query query = new Query ("SELECT DISTINCT interfaces.id\n" +
                                        "FROM interfaces\n" +
                                        " INNER JOIN function_blocks_interfaces ON function_blocks_interfaces.interface_id = interfaces.id\n" +
                                        " INNER JOIN function_catalogs_function_blocks ON function_catalogs_function_blocks.function_block_id = function_blocks_interfaces.function_block_id\n" +
                                        " INNER JOIN versions_function_catalogs ON versions_function_catalogs.function_catalog_id = function_catalogs_function_blocks.function_catalog_id\n" +
                                        "WHERE version_id = ?\n" +
                                        "and interfaces.name LIKE ?");
        query.setParameter(versionId);
        query.setParameter("%" + searchString + "%");

        List<MostInterface> mostInterfaces = new ArrayList<MostInterface>();
        final List<Row> rows = _databaseConnection.query(query);
        for (final Row row : rows) {
            final long mostInterfaceId = row.getLong("id");
            MostInterface mostInterface = inflateMostInterface(mostInterfaceId);
            mostInterfaces.add(mostInterface);
        }
        return mostInterfaces;
    }

    public MostInterface inflateMostInterface(final long mostInterfaceId) throws DatabaseException {
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

        final Long id = row.getLong("id");
        final String mostId = row.getString("most_id");
        final String name = row.getString("name");
        final String description = row.getString("description");
        final Date lastModifiedDate = DateUtil.dateFromDateString(row.getString("last_modified_date"));
        final String version = row.getString("version");
        final boolean isCommitted = row.getBoolean("is_committed");

        MostInterface mostInterface = new MostInterface();
        mostInterface.setId(id);
        mostInterface.setMostId(mostId);
        mostInterface.setName(name);
        mostInterface.setDescription(description);
        mostInterface.setLastModifiedDate(lastModifiedDate);
        mostInterface.setVersion(version);
        mostInterface.setCommitted(isCommitted);

        return mostInterface;
    }
}
