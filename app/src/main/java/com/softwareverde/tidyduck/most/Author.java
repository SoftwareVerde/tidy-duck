package com.softwareverde.tidyduck.most;

import com.softwareverde.tidyduck.AccountId;

public class Author {
    private AccountId _id;
    private String _name;
    private Company _company;

    public AccountId getId() {
        return _id;
    }

    public void setId(final AccountId id) {
        _id = id;
    }

    public String getName() {
        return _name;
    }

    public void setName(final String name) {
        this._name = name;
    }

    public Company getCompany() {
        return _company;
    }

    public void setCompany(final Company company) {
        this._company = company;
    }
}
