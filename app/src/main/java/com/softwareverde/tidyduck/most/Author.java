package com.softwareverde.tidyduck.most;

public class Author {
    private Long _id;
    private String _name;
    private Company _company;

    public Long getId() {
        return _id;
    }

    public void setId(Long id) {
        _id = id;
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