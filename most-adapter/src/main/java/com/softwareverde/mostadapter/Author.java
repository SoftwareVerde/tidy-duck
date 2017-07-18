package com.softwareverde.mostadapter;

public class Author {
    protected String _name;
    protected Company _company;

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
