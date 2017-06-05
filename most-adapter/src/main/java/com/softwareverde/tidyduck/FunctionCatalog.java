package com.softwareverde.tidyduck;

import java.util.Date;

public class FunctionCatalog {
    private long _id;
    private String _release;
    private Date _releaseDate;
    private Author _author;
    private Company _company;
    private boolean _isCommitted;

    public long getId() {
        return _id;
    }

    public void setId(long id) {
        _id = id;
    }

    public String getRelease() {
        return _release;
    }

    public void setRelease(String release) {
        _release = release;
    }

    public Date getReleaseDate() {
        return _releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        _releaseDate = releaseDate;
    }

    public Author getAuthor() {
        return _author;
    }

    public void setAuthor(Author author) {
        _author = author;
    }

    public Company getCompany() {
        return _company;
    }

    public void setCompany(Company company) {
        _company = company;
    }

    public boolean isCommitted() {
        return _isCommitted;
    }

    public void setCommitted(boolean committed) {
        _isCommitted = committed;
    }
}
