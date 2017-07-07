package com.softwareverde.tidyduck.database;


import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.Query;
import com.softwareverde.util.IoUtil;

import java.sql.Connection;

public class TestDataLoader {
    public static void initDatabase(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
        databaseConnection.executeSql(new Query(IoUtil.getResource("/sql/init.sql")));
    }

    public static void insertFakeCompany(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
        databaseConnection.executeSql(new Query("INSERT INTO companies (name) VALUES ('Software Verde, LLC')"));
    }

    public static void insertFakeAccount(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
        databaseConnection.executeSql(new Query("INSERT INTO accounts (name, company_id) VALUES ('Josh Green', 1)"));
    }

    public static void insertFakeVersion(final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
        databaseConnection.executeSql(new Query("INSERT INTO versions (name, owner_id) VALUES ('Version 1', 1)"));
    }

    public static void insertFakeCompany(final DatabaseConnection<Connection> databaseConnection, final String companyName) throws DatabaseException {
        databaseConnection.executeSql(new Query("INSERT INTO companies (name) VALUES (?)").setParameter(companyName));
    }

    public static void insertFakeAccount(final DatabaseConnection<Connection> databaseConnection, final String accountName, final Long companyId) throws DatabaseException {
        databaseConnection.executeSql(new Query("INSERT INTO accounts (name, company_id) VALUES (?, ?)").setParameter(accountName).setParameter(companyId));
    }
}
