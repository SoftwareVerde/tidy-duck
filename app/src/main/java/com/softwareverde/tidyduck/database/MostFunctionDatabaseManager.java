package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.query.Query;
import com.softwareverde.tidyduck.AccountId;
import com.softwareverde.tidyduck.most.*;

import java.sql.Connection;

class MostFunctionDatabaseManager {

    private final DatabaseConnection<Connection> _databaseConnection;

    public MostFunctionDatabaseManager(final DatabaseConnection<Connection> databaseConnection) {
        _databaseConnection = databaseConnection;
    }


    public void insertMostFunctionForMostInterface(final Long mostInterfaceId, final MostFunction mostFunction) throws DatabaseException {
        _insertMostFunction(mostFunction);
        _associateMostFunctionWithMostInterface(mostInterfaceId, mostFunction.getId());
    }

    /**
     * <p>This method updates a function, provided its interface is not approved.  If the interface is approved, a new function is added
     * and associated with the interface.</p>
     *
     * <p>This relies on the following assumptions:</p>
     * <ol>
     *     <li>When interfaces are approved, their functions are also marked approved</li>
     *     <li>When a approved interface is changed, a new version of that interface is created with references to the same functions</li>
     *     <li>When a approved function on an approved interface is changed, a new function should be added to contain the changes</li>
     *     <li>No attempts will be made to update a function on a approved interface</li>
     * </ol>
     * @param mostInterfaceId
     * @param proposedMostFunction
     * @throws DatabaseException
     */
    public void updateMostFunctionForMostInterface(final long mostInterfaceId, final MostFunction proposedMostFunction) throws DatabaseException {
        final long inputMostFunctionId = proposedMostFunction.getId();

        MostFunctionInflater mostFunctionInflater = new MostFunctionInflater(_databaseConnection);
        MostFunction databaseMostFunction = mostFunctionInflater.inflateMostFunction(inputMostFunctionId);

        if (!databaseMostFunction.isApproved()) {
            // not approved, can update existing function
            _updateUnapprovedMostFunction(proposedMostFunction);
        }
        else {
            // current function is approved to an interface
            // need to insert a new function to replace this one
            _insertMostFunction(proposedMostFunction);
            final long newMostFunctionId = proposedMostFunction.getId();
            // change association with interface
            _disassociateMostFunctionWithMostInterface(mostInterfaceId, inputMostFunctionId);
            _associateMostFunctionWithMostInterface(mostInterfaceId, newMostFunctionId);
        }
    }

    private void _updateUnapprovedMostFunction(final MostFunction proposedMostFunction) throws DatabaseException {
        final String name = proposedMostFunction.getName();
        final String mostId = proposedMostFunction.getMostId();
        final long functionStereotypeId = proposedMostFunction.getFunctionStereotype().getId();
        final String description = proposedMostFunction.getDescription();
        final String release = proposedMostFunction.getRelease();
        final AccountId authorId = proposedMostFunction.getAuthor().getId();
        final long companyId = proposedMostFunction.getCompany().getId();
        final String returnParameterName = proposedMostFunction.getReturnParameterName();
        final String returnParameterDescription = proposedMostFunction.getReturnParameterDescription();
        final long returnTypeId = proposedMostFunction.getReturnType().getId();
        final long mostFunctionId = proposedMostFunction.getId();

        boolean supportsNotification = false;
        if ("Property".equals(proposedMostFunction.getFunctionType())) {
            Property property = (Property) proposedMostFunction;
            supportsNotification = property.supportsNotification();
        }

        final Query query = new Query("UPDATE functions SET name = ?, most_id = ?, category = ?, function_stereotype_id = ?, description = ?, release_version = ?, account_id = ?, company_id = ?, return_parameter_name = ?, return_parameter_description = ?, return_type_id = ?, supports_notification = ?, is_approved = ? WHERE id = ? ")
                .setParameter(name)
                .setParameter(mostId)
                .setParameter(proposedMostFunction.getFunctionType())
                .setParameter(functionStereotypeId)
                .setParameter(description)
                .setParameter(release)
                .setParameter(authorId)
                .setParameter(companyId)
                .setParameter(returnParameterName)
                .setParameter(returnParameterDescription)
                .setParameter(returnTypeId)
                .setParameter(supportsNotification ? 1 : 0)
                .setParameter(false)
                .setParameter(mostFunctionId)
        ;

        _databaseConnection.executeSql(query);

        // Clear out operations and parameters, then add current ones if necessary.
        _removeInputParametersFromFunction(mostFunctionId);
        _removeOperationsFromFunction(mostFunctionId);

        if ("Method".equals(proposedMostFunction.getFunctionType())) {
            Method method = (Method) proposedMostFunction;
            for (final MostFunctionParameter parameter : method.getInputParameters()) {
                _addInputParameterToFunction(mostFunctionId, parameter);
            }
        }

        for (final Operation operation : proposedMostFunction.getOperations()) {
            _addOperationToFunction(mostFunctionId, operation);
        }
    }

    private void _insertMostFunction(final MostFunction mostFunction) throws DatabaseException {
        final String name = mostFunction.getName();
        final String mostId = mostFunction.getMostId();
        final Long functionStereotypeId = mostFunction.getFunctionStereotype().getId();
        final String description = mostFunction.getDescription();
        final String release = mostFunction.getRelease();
        final AccountId authorId = mostFunction.getAuthor().getId();
        final Long companyId = mostFunction.getCompany().getId();
        final String returnParameterName = mostFunction.getReturnParameterName();
        final String returnParameterDescription = mostFunction.getReturnParameterDescription();
        final Long returnTypeId = mostFunction.getReturnType().getId();

        boolean supportsNotification = false;
        if ("Property".equals(mostFunction.getFunctionType())) {
            Property property = (Property) mostFunction;
            supportsNotification = property.supportsNotification();
        }

        final Query query = new Query("INSERT INTO functions (name, most_id, category, function_stereotype_id, description, release_version, account_id, company_id, return_parameter_name, return_parameter_description, return_type_id, supports_notification) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")
            .setParameter(name)
            .setParameter(mostId)
            .setParameter(mostFunction.getFunctionType())
            .setParameter(functionStereotypeId)
            .setParameter(description)
            .setParameter(release)
            .setParameter(authorId)
            .setParameter(companyId)
            .setParameter(returnParameterName)
            .setParameter(returnParameterDescription)
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
        final Query query = new Query("INSERT INTO function_parameters (function_id, parameter_name, parameter_description, parameter_index, most_type_id) VALUES (?, ?, ?, ?, ?)")
            .setParameter(newFunctionId)
            .setParameter(parameter.getName())
            .setParameter(parameter.getDescription())
            .setParameter(parameter.getParameterIndex())
            .setParameter(parameter.getMostType().getId())
        ;

        _databaseConnection.executeSql(query);
    }

    private void _removeInputParametersFromFunction(final long mostFunctionId) throws DatabaseException {
        final Query query = new Query("DELETE FROM function_parameters WHERE function_id = ?")
            .setParameter(mostFunctionId)
        ;

        _databaseConnection.executeSql(query);
    }

    private void _addOperationToFunction(final long newFunctionId, final Operation operation) throws DatabaseException {
        final Query query = new Query("INSERT INTO functions_operations (function_id, operation_id, channel) VALUES (?, ?, ?)")
            .setParameter(newFunctionId)
            .setParameter(operation.getId())
            .setParameter(operation.getChannel())
        ;

        _databaseConnection.executeSql(query);
    }

    private void _removeOperationsFromFunction(final long mostFunctionId) throws DatabaseException {
        final Query query = new Query("DELETE FROM functions_operations WHERE function_id = ?")
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

    private void _disassociateMostFunctionWithMostInterface(final long mostInterfaceId, final long mostFunctionId) throws DatabaseException {
        final Query query = new Query("DELETE FROM interfaces_functions WHERE interface_id = ? AND function_id = ?")
            .setParameter(mostInterfaceId)
            .setParameter(mostFunctionId)
        ;
        _databaseConnection.executeSql(query);
    }

    public void deleteMostFunction(final long mostInterfaceId, final long mostFunctionId) throws DatabaseException {
        _deleteMostFunction(mostInterfaceId, mostFunctionId);
    }

    public void setIsDeletedForMostFunction(final long mostFunctionId, final boolean isDeleted) throws DatabaseException {
        final Query query = new Query("UPDATE functions SET is_deleted = ? WHERE id = ?")
                .setParameter(isDeleted)
                .setParameter(mostFunctionId)
                ;

        _databaseConnection.executeSql(query);

        _setIsDeletedForMostFunctionParentAssociation(mostFunctionId, isDeleted);
    }

    public void restoreMostFunctionFromTrash(final long mostFunctionId) throws DatabaseException {
        setIsDeletedForMostFunction(mostFunctionId, false);
    }

    private void _setIsDeletedForMostFunctionParentAssociation(final long mostFunctionId, final boolean isDeleted) throws DatabaseException {
        final Query query = new Query("UPDATE interfaces_functions SET is_deleted = ? WHERE function_id = ?")
                .setParameter(isDeleted)
                .setParameter(mostFunctionId)
                ;

        _databaseConnection.executeSql(query);
    }

    private void _deleteMostFunction(final long mostInterfaceId, final long mostFunctionId) throws DatabaseException {
        final MostFunctionInflater mostFunctionInflater = new MostFunctionInflater(_databaseConnection);
        final MostFunction mostFunction = mostFunctionInflater.inflateMostFunction(mostFunctionId);

        if (mostFunction.isReleased()) {
            throw new IllegalStateException("Released function catalogs cannot be deleted.");
        }

        if (!mostFunction.isDeleted()) {
            throw new IllegalStateException("Only trashed items can be deleted.");
        }

        if (mostFunction.isApproved()) {
            // approved, be careful
            _markAsPermanentlyDeleted(mostFunctionId);
        }
        else {
            // not approved, delete
            _disassociateMostFunctionWithMostInterface(mostInterfaceId, mostFunctionId);
            _deleteMostFunctionFromDatabase(mostFunctionId);
        }

    }

    private void _markAsPermanentlyDeleted(final long mostFunctionId) throws DatabaseException {
        final Query query = new Query("UPDATE functions SET is_permanently_deleted = 1, permanently_deleted_date = NOW() WHERE id = ?")
                .setParameter(mostFunctionId)
                ;

        _databaseConnection.executeSql(query);
    }

    private void _deleteMostFunctionFromDatabase(final long mostFunctionId) throws DatabaseException {
        // Delete most function's operations and parameters first, then delete function.
        _removeOperationsFromFunction(mostFunctionId);
        _removeInputParametersFromFunction(mostFunctionId);

        final Query query = new Query("DELETE FROM functions WHERE id = ?")
            .setParameter(mostFunctionId)
        ;

        _databaseConnection.executeSql(query);
    }

    public void approveMostFunction(final long mostFunctionId, final long reviewId) throws DatabaseException {
        final Query query = new Query("UPDATE functions SET is_approved = ?, approval_review_id = COALESCE(approval_review_id, ?) WHERE id = ?")
                .setParameter(true)
                .setParameter(reviewId)
                .setParameter(mostFunctionId);

        _databaseConnection.executeSql(query);
    }
}
