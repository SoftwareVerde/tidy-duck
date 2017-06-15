package com.softwareverde.tidyduck;

public class Account {
    protected Long _id;
    protected String _username;
    protected String _password;

    protected String _name;
    protected Company _company;

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
}