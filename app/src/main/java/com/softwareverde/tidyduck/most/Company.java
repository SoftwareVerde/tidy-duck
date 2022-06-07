package com.softwareverde.tidyduck.most;

public class Company {
    private Long _id;
    private String _name;

    public Long getId() {
        return this._id;
    }

    public void setId(long id) {
        this._id = id;
    }

    public String getName() {
        return this._name;
    }

    public void setName(String name) {
        this._name = name;
    }
}
