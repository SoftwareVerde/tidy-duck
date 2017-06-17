package com.softwareverde.tidyduck;

public class Author {
    protected Long _id;

    protected String _name;
    protected Company _company;

    public Long getId() {
        return _id;
    }

    public void setId(long id) {
        this._id = id;
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