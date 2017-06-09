package com.softwareverde.tidyduck.sql;

import com.softwareverde.database.Row;
import com.softwareverde.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnection {
    protected final Logger _logger = LoggerFactory.getLogger(this.getClass());

    protected Connection _connection;
    protected String _lastInsertId = "-1";

    protected String _extractInsertId(final PreparedStatement preparedStatement) {

        try {
            final ResultSet resultSet = preparedStatement.getGeneratedKeys();

            final Integer insertId;
            {
                if (resultSet.next()) {
                    insertId = resultSet.getInt(1);
                }
                else {
                    insertId = null;
                }
            }

            resultSet.close();
            return Util.coalesce(insertId, "-1").toString();
        }
        catch (final SQLException exception) {
            _logger.error("Error discerning insertId.", exception);
            return null;
        }
    }

    protected PreparedStatement _prepareStatement(final String query, final String[] parameters) throws SQLException {
        final Boolean isInsert = (query.trim().regionMatches(true, 0, "INSERT", 0, 6));
        final PreparedStatement preparedStatement = _connection.prepareStatement(query, (isInsert ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS));
        if (parameters != null) {
            for (int i = 0; i < parameters.length; ++i) {
                preparedStatement.setString(i+1, parameters[i]);
            }
        }
        return preparedStatement;
    }

    public DatabaseConnection(final Connection connection) {
        _connection = connection;
    }

    /**
     * Returns true if the DDL was executed successfully.
     *  Upon error, False is returned and the error is logged.
     */
    public synchronized Boolean executeDdl(final String query) {
        try {
            final Statement statement = _connection.createStatement();
            statement.execute(query);
            statement.close();
            return true;
        }
        catch (final SQLException exception) {
            _logger.error("Error executing DDL statement.", exception);
            return false;
        }
    }

    /**
     * If an insertId is generated, it is returned upon success.
     * If an update occurred successfully, 0 is returned.
     *  Upon failure, the event is logged and returns a value less than 0.
     */
    public synchronized Long executeSql(final String query, final String[] parameters) {
        try {
            final PreparedStatement preparedStatement = _prepareStatement(query, parameters);
            preparedStatement.execute();
            _lastInsertId = _extractInsertId(preparedStatement);
            preparedStatement.close();
            return Util.parseLong(_lastInsertId);
        }
        catch (final SQLException exception) {
            _lastInsertId = null;
            _logger.error("Error executing SQL.", exception);
            return -1L;
        }
    }

    /**
     * Returns a list of rows upon success.
     *  Upon failure, the event is logged an null is returned.
     */
    public synchronized List<Row> query(final String query, final String[] parameters) {
        try {
            final PreparedStatement preparedStatement = _prepareStatement(query, parameters);
            final ResultSet resultSet = preparedStatement.executeQuery();

            final List<Row> results = new ArrayList<Row>();
            while (resultSet.next()) {
                // results.add(Row.fromResultSet(resultSet));
            }
            resultSet.close();
            preparedStatement.close();

            return results;
        }
        catch (final SQLException exception) {
            _logger.error("Error executing query.", exception);
            return null;
        }
    }

    /**
     * Returns true if the connection was disconnected.
     *  Upon failure, the error is logged and false is returned.
     */
    public Boolean disconnect() {
        try {
            _connection.close();
            return true;
        }
        catch (final SQLException exception) {
            _logger.error("Error closing connection.", exception);
            return false;
        }
    }

    /**
     * Returns the underlying Sql Connection.
     */
    public Connection getRawConnection() {
        return _connection;
    }

    private static final Class[] UNUSED = {
        // org.postgresql.Driver.class
    };
}