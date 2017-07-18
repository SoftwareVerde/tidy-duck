package com.softwareverde.tidyduck;

import com.softwareverde.tidyduck.most.Author;
import com.softwareverde.tidyduck.most.Company;

public class Account {
    protected Long _id;
    protected String _username;
    protected String _password;
    protected String _name;
    protected Company _company;

    protected Settings _settings;

    public Long getId() {
        return _id;
    }

    public void setId(long id) {
        this._id = id;
    }

    public void setUsername(final String username) {
        _username = username;
    }

    public String getUsername() {
        return _username;
    }

    public void setPassword(final String password) {
        _password = password;
    }

    public String getPassword() {
        return _password;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        this._name = name;
    }

    public Company getCompany() {
        return _company;
    }

    public void setCompany(Company company) {
        this._company = company;
    }

    public Settings getSettings() {
        return _settings;
    }

    public void setSettings(Settings settings) {
        this._settings = settings;
    }

    public Author toAuthor() {
        Author author = new Author();
        author.setId(_id);
        author.setName(_name);
        author.setCompany(_company);
        return author;
    }
}