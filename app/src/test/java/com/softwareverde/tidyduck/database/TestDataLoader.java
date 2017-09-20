package com.softwareverde.tidyduck.database;


import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.Query;
import com.softwareverde.util.IoUtil;

import java.sql.Connection;

public class TestDataLoader {
    public static int generateRandomAutoIncrementId() {
        // generate random ID but make sure it's greater than zero
        return (int) (((Math.random() * 7777) % 1000) + 1);
    }

    public static void initDatabase(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
        databaseConnection.executeSql(new Query(IoUtil.getResource("/sql/init.sql")));
        databaseConnection.executeSql(new Query(IoUtil.getResource("/sql/migrations/2017-09-06_add_enum_value_description.sql")));
        databaseConnection.executeSql(new Query(IoUtil.getResource("/sql/migrations/2017-09-07_add_ticket_url_to_reviews.sql")));
        databaseConnection.executeSql(new Query(IoUtil.getResource("/sql/migrations/2017-09-08_add_parameter_name_and_description.sql")));
        databaseConnection.executeSql(new Query(IoUtil.getResource("/sql/migrations/2017-09-12_roles.sql")));
        databaseConnection.executeSql(new Query(IoUtil.getResource("/sql/migrations/2017-09-18_add_return_parameter_fields.sql")));
    }

    public static void insertFakeCompany(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
        databaseConnection.executeSql(new Query("INSERT INTO companies (name) VALUES ('Software Verde, LLC')"));
    }

    public static void insertFakeAccount(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
        databaseConnection.executeSql(new Query("INSERT INTO accounts (name, company_id) VALUES ('Josh Green', 1)"));
        databaseConnection.executeSql(new Query("INSERT INTO accounts_roles VALUES (1, 1), (1, 2), (1, 3), (1, 4), (1, 5)"));
    }

    public static void insertFakeCompany(final DatabaseConnection<Connection> databaseConnection, final String companyName) throws DatabaseException {
        databaseConnection.executeSql(new Query("INSERT INTO companies (name) VALUES (?)").setParameter(companyName));
    }

    public static void insertFakeAccount(final DatabaseConnection<Connection> databaseConnection, final String accountName, final Long companyId) throws DatabaseException {
        databaseConnection.executeSql(new Query("INSERT INTO accounts (name, company_id) VALUES (?, ?)").setParameter(accountName).setParameter(companyId));
    }
}
