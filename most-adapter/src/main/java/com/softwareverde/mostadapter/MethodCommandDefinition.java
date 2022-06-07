package com.softwareverde.mostadapter;

public class MethodCommandDefinition extends CommandDefinition {
    @Override
    protected String _getTagName() {
        return "MCmdDef";
    }

    @Override
    protected String _getIdAttributeName() {
        return "MCmdID";
    }

    public MethodCommandDefinition(final String id, final String operationType, final String name, final String description) {
        super(id, operationType, name, description);
    }
}
