package com.softwareverde.mostadapter;

public class PropertyCommandDefinition extends CommandDefinition {
    @Override
    protected String _getTagName() {
        return "PCmdDef";
    }

    @Override
    protected String _getIdAttributeName() {
        return "PCmdID";
    }

    public PropertyCommandDefinition(final String id, final String operationType, final String name, final String description) {
        super(id, operationType, name, description);
    }
}
