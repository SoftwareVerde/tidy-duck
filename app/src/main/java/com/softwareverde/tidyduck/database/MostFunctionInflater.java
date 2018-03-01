package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.Query;
import com.softwareverde.database.Row;
import com.softwareverde.tidyduck.DateUtil;
import com.softwareverde.tidyduck.most.*;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MostFunctionInflater {
    private final DatabaseConnection<Connection> _databaseConnection;

    public MostFunctionInflater(DatabaseConnection<Connection> databaseConnection) {
        _databaseConnection = databaseConnection;
    }

    public List<MostFunction> inflateMostFunctionsFromMostInterfaceId(final long mostInterfaceId) throws DatabaseException {
        return inflateMostFunctionsFromMostInterfaceId(mostInterfaceId, true);
    }

    public List<MostFunction> inflateMostFunctionsFromMostInterfaceId(final long mostInterfaceId, final boolean includeDeleted) throws DatabaseException {
        final Query query = new Query(
            "SELECT function_id FROM interfaces_functions WHERE interface_id = ?" + (includeDeleted ? "" : " and is_deleted = 0")
        );
        query.setParameter(mostInterfaceId);

        List<MostFunction> mostFunctions = new ArrayList<MostFunction>();
        final List<Row> rows = _databaseConnection.query(query);
        for (final Row row : rows) {
            final long mostFunctionId = row.getLong("function_id");
            final MostFunction mostFunction = inflateMostFunction(mostFunctionId);
            mostFunctions.add(mostFunction);
        }
        return mostFunctions;
    }

    public List<MostFunction> inflateTrashedMostFunctionsFromMostInterfaceId(final long mostInterfaceId) throws DatabaseException {
        final Query query = new Query(
                "SELECT * FROM functions WHERE id IN(" +
                    "SELECT DISTINCT interfaces_functions.function_id FROM interfaces_functions WHERE interfaces_functions.interface_id = ?)\n" +
                    "AND is_deleted = 1 and is_permanently_deleted = 0");
        query.setParameter(mostInterfaceId);

        List<MostFunction> mostFunctions = new ArrayList<MostFunction>();
        final List<Row> rows = _databaseConnection.query(query);
        for (final Row row : rows) {
            final long mostFunctionId = row.getLong("id");
            final MostFunction mostFunction = inflateMostFunction(mostFunctionId);
            mostFunctions.add(mostFunction);
        }
        return mostFunctions;
    }

    public MostFunction inflateMostFunction(final long mostFunctionId) throws DatabaseException {
        final Query query = new Query(
                "SELECT * FROM functions WHERE id = ?"
        );
        query.setParameter(mostFunctionId);

        final List<Row> rows = _databaseConnection.query(query);
        if (rows.size() == 0) {
            throw new DatabaseException("Function ID " + mostFunctionId + " not found.");
        }

        final Row row = rows.get(0);

        final Long id = row.getLong("id");
        final String mostId = row.getString("most_id");
        final String name = row.getString("name");
        final String description = row.getString("description");
        final String releaseVersion = row.getString("release_version");
        final String category = row.getString("category");
        final boolean isDeleted = row.getBoolean("is_deleted");
        final String deletedDateString = row.getString("deleted_date");
        Date deletedDate = null;
        if (deletedDateString != null) {
            deletedDate = DateUtil.dateFromDateTimeString(deletedDateString);
        }
        final boolean isPermanentlyDeleted = row.getBoolean("is_permanently_deleted");
        final String permanentlyDeletedDateString = row.getString("permanently_deleted_date");
        Date permanentlyDeletedDate = null;
        if (permanentlyDeletedDateString != null) {
            permanentlyDeletedDate = DateUtil.dateFromDateTimeString(permanentlyDeletedDateString);
        }
        final boolean isApproved = row.getBoolean("is_approved");
        final boolean isReleased = row.getBoolean("is_released");
        final long mostFunctionStereotypeId = row.getLong("function_stereotype_id");
        final String returnParameterName = row.getString("return_parameter_name");
        final String returnParameterDescription = row.getString("return_parameter_description");
        final long returnTypeId = row.getLong("return_type_id");
        final Long accountId = row.getLong("account_id");
        final long companyId = row.getLong("company_id");

        final MostFunctionStereotypeInflater mostFunctionStereotypeInflater = new MostFunctionStereotypeInflater(_databaseConnection);
        final MostFunctionStereotype mostFunctionStereotype = mostFunctionStereotypeInflater.inflateMostFunctionStereotype(mostFunctionStereotypeId);
        final MostTypeInflater mostTypeInflater = new MostTypeInflater(_databaseConnection);
        final MostType returnType = mostTypeInflater.inflateMostType(returnTypeId);

        final AuthorInflater authorInflater = new AuthorInflater(_databaseConnection);
        final Author author = authorInflater.inflateAuthor(accountId);
        final CompanyInflater companyInflater = new CompanyInflater(_databaseConnection);
        final Company company = companyInflater.inflateCompany(companyId);

        MostFunction mostFunction = null;
        if ("Property".equals(category)) {
            Property property = new Property();
            final boolean supportsNotification = row.getBoolean("supports_notification");
            property.setSupportsNotification(supportsNotification);

            mostFunction = property;
        } else {
            // Method
            Method method = new Method();
            method.setInputParameters(inflateMostFunctionParametersFromMostFunctionId(mostFunctionId));

            mostFunction = method;
        }

        mostFunction.setId(id);
        mostFunction.setMostId(mostId);
        mostFunction.setName(name);
        mostFunction.setDescription(description);
        mostFunction.setRelease(releaseVersion);
        mostFunction.setIsDeleted(isDeleted);
        mostFunction.setDeletedDate(deletedDate);
        mostFunction.setIsPermanentlyDeleted(isPermanentlyDeleted);
        mostFunction.setPermanentlyDeletedDate(permanentlyDeletedDate);
        mostFunction.setIsApproved(isApproved);
        mostFunction.setIsReleased(isReleased);
        mostFunction.setFunctionStereotype(mostFunctionStereotype);
        mostFunction.setReturnParameterName(returnParameterName);
        mostFunction.setReturnParameterDescription(returnParameterDescription);
        mostFunction.setReturnType(returnType);
        mostFunction.setAuthor(author);
        mostFunction.setCompany(company);
        mostFunction.setOperations(inflateOperationsFromMostFunctionId(mostFunctionId));

        return mostFunction;
    }

    public List<Operation> inflateOperationsFromMostFunctionId(final long mostFunctionId) throws DatabaseException {
        final Query query = new Query(
                "SELECT operation_id, channel FROM functions_operations WHERE function_id = ?"
        );
        query.setParameter(mostFunctionId);

        OperationInflater operationInflater = new OperationInflater(_databaseConnection);
        List<Operation> operations = new ArrayList<Operation>();

        final List<Row> rows = _databaseConnection.query(query);
        for (final Row row : rows) {
            final Long operationId = row.getLong("operation_id");
            final String channel = row.getString("channel");

            Operation operation = operationInflater.inflateOperation(operationId);
            operation.setChannel(channel);
            operations.add(operation);
        }
        return operations;
    }

    public List<MostFunctionParameter> inflateMostFunctionParametersFromMostFunctionId(final long mostFunctionId) throws DatabaseException {
        final Query query = new Query(
                "SELECT parameter_name, parameter_description, parameter_index, most_type_id FROM function_parameters WHERE function_id = ?"
        );
        query.setParameter(mostFunctionId);

        List<MostFunctionParameter> mostFunctionParameters = new ArrayList<MostFunctionParameter>();
        final List<Row> rows = _databaseConnection.query(query);
        for (final Row row : rows) {
            final String parameterName = row.getString("parameter_name");
            final String parameterDescription = row.getString("parameter_description");
            final Integer parameterIndex = row.getInteger("parameter_index");
            final Long parameterTypeId = row.getLong("most_type_id");
            final MostTypeInflater mostTypeInflater = new MostTypeInflater(_databaseConnection);
            final MostType parameterType = mostTypeInflater.inflateMostType(parameterTypeId);

            MostFunctionParameter mostFunctionParameter = new MostFunctionParameter();
            mostFunctionParameter.setName(parameterName);
            mostFunctionParameter.setDescription(parameterDescription);
            mostFunctionParameter.setParameterIndex(parameterIndex);
            mostFunctionParameter.setMostType(parameterType);
            mostFunctionParameters.add(mostFunctionParameter);
        }
        return mostFunctionParameters;
    }
}
