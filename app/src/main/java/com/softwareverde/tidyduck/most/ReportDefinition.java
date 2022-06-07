package com.softwareverde.tidyduck.most;

public class ReportDefinition {
    private final String _reportId;
    private final String _reportOperationType;
    private final String _reportName;
    private final String _reportDescription;

    public ReportDefinition(final String id, final String operationType, final String name, final String description) {
        _reportId = id;
        _reportOperationType = operationType;
        _reportName = name;
        _reportDescription = description;
    }

    public String getReportId() {
        return _reportId;
    }

    public String getReportOperationType() {
        return _reportOperationType;
    }

    public String getReportName() {
        return _reportName;
    }

    public String getReportDescription() {
        return _reportDescription;
    }
}
