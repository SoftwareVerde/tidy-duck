package com.softwareverde.mostadapter;

public class PropertyReportDefinition extends ReportDefinition {

    @Override
    protected String _getTagName() {
        return "PReportDef";
    }

    @Override
    protected String _getIdAttributeName() {
        return "PReportID";
    }

    public PropertyReportDefinition(final String id, final String operationType, final String name, final String description) {
        super(id, operationType, name, description);
    }
}
