package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.Query;
import com.softwareverde.database.Row;
import com.softwareverde.tidyduck.*;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MostFunctionInflater {
    private final DatabaseConnection<Connection> _databaseConnection;

    public MostFunctionInflater(DatabaseConnection<Connection> databaseConnection) {
        _databaseConnection = databaseConnection;
    }

    public List<MostFunction> inflateMostFunctionsFromMostInterfaceId(long mostInterfaceId) throws DatabaseException {
        final Query query = new Query(
            "SELECT function_id FROM interfaces_functions WHERE interface_id = ?"
        );
        query.setParameter(mostInterfaceId);

        List<MostFunction> mostFunctions = new ArrayList<MostFunction>();
        final List<Row> rows = _databaseConnection.query(query);
        for (final Row row : rows) {
            final long mostFunctionId = row.getLong("function_id");
            MostFunction mostFunction = inflateMostFunction(mostFunctionId);
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
        final boolean isCommitted = row.getBoolean("is_committed");
        final Long mostFunctionStereotypeId = row.getLong("function_stereotype_id");
        final Long returnTypeId = row.getLong("return_type_id");
        final Long accountId = row.getLong("account_id");
        final Long companyId = row.getLong("company_id");

        MostStereotypeInflater mostStereotypeInflater = new MostStereotypeInflater(_databaseConnection);
        final MostFunctionStereotype mostFunctionStereotype = mostStereotypeInflater.inflateMostStereotype(mostFunctionStereotypeId);
        MostTypeInflater mostTypeInflater = new MostTypeInflater(_databaseConnection);
        final MostType mostType = mostTypeInflater.inflateMostType(returnTypeId);

        AuthorInflater authorInflater = new AuthorInflater(_databaseConnection);
        final Author author = authorInflater.inflateAuthor(accountId);
        CompanyInflater companyInflater = new CompanyInflater(_databaseConnection);
        final Company company = companyInflater.inflateCompany(companyId);

        if (category === "Property") {
            Property property = new Property();

            property.setId(id);
            property.setMostId(mostId);
            property.setName(name);
            property.setDescription(description);
            property.setRelease(releaseVersion);
            property.setCommitted(isCommitted);
            property.setFunctionStereotype(mostFunctionStereotype);
            property.setReturnType(mostType);
            property.setAuthor(author);
            property.setCompany(company);
            final boolean supportsNotification = row.getBoolean("supports_notification");
            property.setSupportsNotification;

            return property;
        }

        Method method = new Method();

        method.setId(id);
        method.setMostId(mostId);
        method.setName(name);
        method.setDescription(description);
        method.setRelease(releaseVersion);
        method.setCommitted(isCommitted);
        method.setFunctionStereotype(mostFunctionStereotype);
        method.setReturnType(mostType);
        method.setAuthor(author);
        method.setCompany(company);


        /*
        List<MostFunctionParameter> mostFunctionParameters = new ArrayList<MostFunctionParameter>();
        Parameters are derived from operations and stereotypes...
         */
    }

    public List<Operation> inflateOperationsFromMostFunctionId(final long mostFunctionId) throws DatabaseException {
        final Query query = new Query(
                "SELECT operation_id FROM functions_operations WHERE function_id = ?"
        );
        query.setParameter(mostFunctionId);

        OperationInflater operationInflater = new OperationInflater(_databaseConnection);

        List<Operation> operations = new ArrayList<Operation>();
        final List<Row> rows = _databaseConnection.query(query);
        for (final Row row : rows) {
            final long operationId = row.getLong("operation_id");
            Operation operation = operationInflater.inflateOperation(operationId);
            operations.add(operation);
        }
        return operations;
    }

    public List<MostFunctionParameter> inflateMostFunctionParametersFromMostFunctionId(mostFunctionId) throws DatabaseException {
        final Query query = new Query(
                "SELECT parameter_index, most_type_id, name FROM functions_parameters INNER JOIN most_types ON function_parameters.most_type_id = most_types.id WHERE function_id = ?"
        );
        query.setParameter(mostFunctionId);

        List<MostFunctionParameter> mostFunctionParameters = new ArrayList<MostFunctionParameter>();
        final List<Row> rows = _databaseConnection.query(query);
        for (final Row row : rows) {
            final Long parameterIndex = row.getLong("parameter_index");
            final Long mostTypeId = row.getLong("most_type_id");
            final String mostTypeName = row.getString("name");

            MostType mostType = new MostType();
            mostType.setId(mostTypeId);
            mostType.setName(mostTypeName);
            
            MostFunctionParameter mostFunctionParameter = new MostFunctionParameter();
            mostFunctionParameter.setParameterIndex(parameterIndex);
            mostFunctionParameter.setMostType(mostType);
            mostFunctionParameters.add(mostFunctionParameter);
        }
        return mostFunctionParameters;
    }
}
