package com.softwareverde.tidyduck.database;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.tidyduck.Account;
import com.softwareverde.tidyduck.Author;

import java.sql.Connection;

public class AuthorInflater {

    private final DatabaseConnection<Connection> _databaseConnection;

    public AuthorInflater(DatabaseConnection<Connection> databaseConnection) {
        _databaseConnection = databaseConnection;
    }
    
    public Author inflateAuthor(Long accountId) throws DatabaseException {
        final AccountInflater accountInflater = new AccountInflater(_databaseConnection);
        final Account account = accountInflater.inflateAccount(accountId);

        Author author = new Author();
        author.setId(account.getId());
        author.setName(account.getName());
        author.setCompany(account.getCompany());

        return author;
    }
}
