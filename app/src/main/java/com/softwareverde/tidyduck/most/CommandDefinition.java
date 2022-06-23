package com.softwareverde.tidyduck.most;

public class CommandDefinition {
    private final String _commandId;
    private final String _commandOperationType;
    private final String _commandName;
    private final String _commandDescription;

    public CommandDefinition(final String id, final String operationType, final String name, final String description) {
        _commandId = id;
        _commandOperationType = operationType;
        _commandName = name;
        _commandDescription = description;
    }

    public String getCommandId() {
        return _commandId;
    }

    public String getCommandOperationType() {
        return _commandOperationType;
    }

    public String getCommandName() {
        return _commandName;
    }

    public String getCommandDescription() {
        return _commandDescription;
    }
}