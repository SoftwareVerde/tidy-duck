package com.softwareverde.mostadapter;

public class MethodReportDefinition extends ReportDefinition {

    @Override
    protected String _getTagName() {
        return "MReportDef";
    }

    @Override
    protected String _getIdAttributeName() {
        return "MReportID";
    }

    public MethodReportDefinition(final String id, final String operationType, final String name, final String description) {
        super(id, operationType, name, description);
    }
}
