package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.Query;
import com.softwareverde.tidyduck.most.*;

import java.sql.Connection;

class MostFunctionDatabaseManager {

    private final DatabaseConnection<Connection> _databaseConnection;

    public MostFunctionDatabaseManager(final DatabaseConnection<Connection> databaseConnection) {
        _databaseConnection = databaseConnection;
    }


    public void insertMostFunctionForMostInterface(final long mostInterfaceId, final MostFunction mostFunction) throws DatabaseException {
        _insertMostFunction(mostFunction);
        _associateMostFunctionWithMostInterface(mostInterfaceId, mostFunction.getId());
    }

    // TODO: update committed MostFunction method.
    private void _updateUncommittedMostFunction(MostFunction proposedMostFunction) throws DatabaseException {
        final String name = proposedMostFunction.getName();
        final String mostId = proposedMostFunction.getMostId();
        final long functionStereotypeId = proposedMostFunction.getFunctionStereotype().getId();
        final String description = proposedMostFunction.getDescription();
        final String release = proposedMostFunction.getRelease();
        final long authorId = proposedMostFunction.getAuthor().getId();
        final long companyId = proposedMostFunction.getCompany().getId();
        final long returnTypeId = proposedMostFunction.getReturnType().getId();
        final long mostFunctionId = proposedMostFunction.getId();

        final Query query = new Query("UPDATE functions SET (name, most_id, category, function_stereotype_id, description, release_version, account_id, company_id, return_type_id, supports_notification) WHERE id = ? VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")
                .setParameter(name)
                .setParameter(mostId)
                .setParameter(mostFunction.getFunctionType())
                .setParameter(functionStereotypeId)
                .setParameter(description)
                .setParameter(release)
                .setParameter(authorId)
                .setParameter(companyId)
                .setParameter(returnTypeId)
                .setParameter(supportsNotification ? 1 : 0)
                .setParameter(mostFunctionId)
        ;

        _databaseConnection.executeSql(query);

        // Clear out operations and parameters, then add current ones if necessary.
        _removeInputParametersFromFunction(mostFunctionId);
        _removeOperationsFromFunction(mostFunctionId);

        if ("Method".equals(mostFunction.getFunctionType())) {
            Method method = (Method) mostFunction;
            for (final MostFunctionParameter parameter : method.getInputParameters()) {
                _addInputParameterToFunction(mostFunctionId, parameter);
            }
        }

        for (final Operation operation : mostFunction.getOperations()) {
            _addOperationToFunction(mostFunctionId, operation);
        }
    }

    private void _insertMostFunction(MostFunction mostFunction) throws DatabaseException {
        final String name = mostFunction.getName();
        final String mostId = mostFunction.getMostId();
        final long functionStereotypeId = mostFunction.getFunctionStereotype().getId();
        final String description = mostFunction.getDescription();
        final String release = mostFunction.getRelease();
        final long authorId = mostFunction.getAuthor().getId();
        final long companyId = mostFunction.getCompany().getId();
        final long returnTypeId = mostFunction.getReturnType().getId();

        boolean supportsNotification = false;
        if ("Property".equals(mostFunction.getFunctionType())) {
            Property property = (Property) mostFunction;
            supportsNotification = property.supportsNotification();
        }

        final Query query = new Query("INSERT INTO functions (name, most_id, category, function_stereotype_id, description, release_version, account_id, company_id, return_type_id, supports_notification) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")
            .setParameter(name)
            .setParameter(mostId)
            .setParameter(mostFunction.getFunctionType())
            .setParameter(functionStereotypeId)
            .setParameter(description)
            .setParameter(release)
            .setParameter(authorId)
            .setParameter(companyId)
            .setParameter(returnTypeId)
            .setParameter(supportsNotification ? 1 : 0)
        ;

        final long newFunctionId = _databaseConnection.executeSql(query);
        mostFunction.setId(newFunctionId);

        if ("Method".equals(mostFunction.getFunctionType())) {
            Method method = (Method) mostFunction;
            for (final MostFunctionParameter parameter : method.getInputParameters()) {
                _addInputParameterToFunction(newFunctionId, parameter);
            }
        }

        for (final Operation operation : mostFunction.getOperations()) {
            _addOperationToFunction(newFunctionId, operation);
        }
    }

    private void _addInputParameterToFunction(final long newFunctionId, final MostFunctionParameter parameter) throws DatabaseException {
        final Query query = new Query("INSERT INTO function_parameters (function_id, parameter_index, most_type_id) VALUES (?, ?, ?)")
            .setParameter(newFunctionId)
            .setParameter(parameter.getParameterIndex())
            .setParameter(parameter.getMostType().getId())
        ;

        _databaseConnection.executeSql(query);
    }

    private void _removeInputParametersFromFunction(final long mostFunctionId) throws DatabaseException {
        final Query query = new Query("DELETE * FROM function_parameters WHERE function_id = ?")
                .setParameter(mostFunctionId)
                ;

        _databaseConnection.executeSql(query);
    }

    private void _addOperationToFunction(final long newFunctionId, final Operation operation) throws DatabaseException {
        final Query query = new Query("INSERT INTO functions_operations (function_id, operation_id) VALUES (?, ?)")
            .setParameter(newFunctionId)
            .setParameter(operation.getId())
        ;

        _databaseConnection.executeSql(query);
    }

    private void _removeOperationsFromFunction(final long mostFunctionId) throws DatabaseException {
        final Query query = new Query("DELETE * FROM functions_operations WHERE function_id = ?")
                .setParameter(mostFunctionId)
                ;

        _databaseConnection.executeSql(query);
    }

    private void _associateMostFunctionWithMostInterface(final long mostInterfaceId, final long mostFunctionId) throws DatabaseException {
        final Query query = new Query("INSERT INTO interfaces_functions (interface_id, function_id) VALUES (?, ?)")
            .setParameter(mostInterfaceId)
            .setParameter(mostFunctionId)
        ;

        _databaseConnection.executeSql(query);
    }
}
