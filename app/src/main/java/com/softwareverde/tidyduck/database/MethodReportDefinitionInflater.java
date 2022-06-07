package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.query.Query;
import com.softwareverde.database.row.Row;
import com.softwareverde.tidyduck.DateUtil;
import com.softwareverde.tidyduck.most.*;

import java.sql.Connection;
import java.util.*;

public class MethodReportDefinitionInflater {

    private final DatabaseConnection<Connection> _databaseConnection;

    public MethodReportDefinitionInflater(DatabaseConnection<Connection> databaseConnection) {
        _databaseConnection = databaseConnection;
    }

    public List<MethodReportDefinition> inflateReportDefinitions() throws DatabaseException {
        final Query query = new Query(
            "SELECT * FROM method_report_definitions"
        );

        final List<MethodReportDefinition> reportDefinitions = new ArrayList<MethodReportDefinition>();
        final List<Row> rows = _databaseConnection.query(query);
        for (final Row row : rows) {
            final MethodReportDefinition classDefinition = _convertToReportDefinition(row);
            reportDefinitions.add(classDefinition);
        }
        return reportDefinitions;
    }

    private MethodReportDefinition _convertToReportDefinition(final Row row) throws DatabaseException {
        final Long id = row.getLong("id");

        final String reportId = row.getString("report_id");
        final String reportOperationType = row.getString("report_operation_type");
        final String reportName = row.getString("report_name");
        final String reportDescription = row.getString("report_description");

        return new MethodReportDefinition(reportId, reportOperationType, reportName, reportDescription);
    }
}
