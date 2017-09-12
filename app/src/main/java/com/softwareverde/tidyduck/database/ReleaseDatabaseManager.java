package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.Query;
import com.softwareverde.database.Row;
import com.softwareverde.tidyduck.ReleaseItem;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class ReleaseDatabaseManager {
    private final DatabaseConnection<Connection> _databaseConnection;

    public ReleaseDatabaseManager(final DatabaseConnection<Connection> databaseConnection) {
        _databaseConnection = databaseConnection;
    }

    public List<ReleaseItem> getReleaseItemList(long functionCatalogId) throws DatabaseException {
        final List<ReleaseItem> releaseItems = new ArrayList<>();

        // add this function catalog
        releaseItems.addAll(_getFunctionCatalogReleaseItem(functionCatalogId));

        // add unreleased function blocks
        releaseItems.addAll(_getUnreleasedChildFunctionBlocks(functionCatalogId));

        // add unreleased interfaces
        releaseItems.addAll(_getUnreleasedChildInterfaces(functionCatalogId));

        // add unreleased functions
        releaseItems.addAll(_getUnreleasedChildFunctions(functionCatalogId));

        return releaseItems;
    }

    private List<ReleaseItem> _getFunctionCatalogReleaseItem(final long functionCatalogId) throws DatabaseException {
        final Query query = new Query("SELECT 'FUNCTION CATALOG' AS type, id, name, release_version AS version " +
                "FROM function_catalogs " +
                "WHERE id = ?");
        query.setParameter(functionCatalogId);

        return _executeReleaseItemQuery(query);
    }

    private List<ReleaseItem> _getUnreleasedChildFunctionBlocks(long functionCatalogId) throws DatabaseException {
        final Query query = new Query("SELECT 'FUNCTION BLOCK' AS type, function_blocks.id, name, release_version AS version " +
                "FROM function_catalogs_function_blocks " +
                "INNER JOIN function_blocks ON function_catalogs_function_blocks.function_block_id = function_blocks.id " +
                "WHERE function_catalog_id = ? and is_released = 0");
        query.setParameter(functionCatalogId);

        return _executeReleaseItemQuery(query);
    }

    private List<ReleaseItem> _getUnreleasedChildInterfaces(long functionCatalogId) throws DatabaseException {
        final Query query = new Query("SELECT 'INTERFACE' AS type, interfaces.id, name, version AS version " +
                "FROM function_catalogs_function_blocks " +
                "INNER JOIN function_blocks_interfaces ON function_blocks_interfaces.function_block_id = function_catalogs_function_blocks.function_block_id " +
                "INNER JOIN interfaces ON function_blocks_interfaces.interface_id = interfaces.id " +
                "WHERE function_catalog_id = ? and interfaces.is_released = 0");
        query.setParameter(functionCatalogId);

        return _executeReleaseItemQuery(query);
    }

    private List<ReleaseItem> _getUnreleasedChildFunctions(long functionCatalogId) throws DatabaseException {
        final Query query = new Query("SELECT 'FUNCTION' AS type, functions.id, name, release_version AS version " +
                "FROM function_catalogs_function_blocks " +
                "INNER JOIN function_blocks_interfaces ON function_blocks_interfaces.function_block_id = function_catalogs_function_blocks.function_block_id " +
                "INNER JOIN interfaces_functions ON interfaces_functions.interface_id = function_blocks_interfaces.interface_id " +
                "INNER JOIN functions ON interfaces_functions.function_id = functions.id " +
                "WHERE function_catalog_id = ? and functions.is_released = 0");
        query.setParameter(functionCatalogId);

        return _executeReleaseItemQuery(query);
    }

    private List<ReleaseItem> _executeReleaseItemQuery(Query query) throws DatabaseException {
        List<Row> rows = _databaseConnection.query(query);

        ArrayList<ReleaseItem> releaseItems = new ArrayList<>();
        for (final Row row : rows) {
            final ReleaseItem releaseItem = _convertRowToReleaseItem(row);
            releaseItems.add(releaseItem);
        }
        return releaseItems;
    }

    private ReleaseItem _convertRowToReleaseItem(final Row row) {
        final String itemType = row.getString("type");
        final Long itemId = row.getLong("id");
        final String itemName = row.getString("name");
        final String itemVersion = row.getString("version");

        final ReleaseItem releaseItem = new ReleaseItem();

        releaseItem.setItemType(itemType);
        releaseItem.setItemId(itemId);
        releaseItem.setItemName(itemName);
        releaseItem.setItemVersion(itemVersion);

        return releaseItem;
    }

    public void releaseFunctionCatalog(final long functionCatalogId, final List<ReleaseItem> releaseItems) throws DatabaseException {
        // release each release item
        for (final ReleaseItem releaseItem : releaseItems) {
            final String queryString = getReleaseQuery(releaseItem);
            executeReleaseQuery(queryString, releaseItem);
        }
    }

    private void executeReleaseQuery(final String queryString, final ReleaseItem releaseItem) throws DatabaseException {
        final Query query = new Query(queryString);
        query.setParameter(releaseItem.getNewVersion());
        query.setParameter(releaseItem.getItemId());

        _databaseConnection.executeSql(query);
    }

    private String getReleaseQuery(final ReleaseItem releaseItem) {
        String query;
        switch (releaseItem.getItemType()) {
            case "FUNCTION CATALOG": {
                query = "UPDATE function_catalogs SET release_version = ?, is_released = 1 WHERE id = ?";
            } break;
            case "FUNCTION BLOCK": {
                query = "UPDATE function_blocks SET release_version = ?, is_released = 1 WHERE id = ?";
            } break;
            case "INTERFACE": {
                query = "UPDATE interfaces SET version = ?, is_released = 1 WHERE id = ?";
            } break;
            case "FUNCTION": {
                query = "UPDATE functions SET release_version = ?, is_released = 1 WHERE id = ?";
            } break;
            default: {
                throw new IllegalArgumentException("Invalid release item type '" + releaseItem.getItemType() + "' found.");
            }
        }
        return query;
    }
}
