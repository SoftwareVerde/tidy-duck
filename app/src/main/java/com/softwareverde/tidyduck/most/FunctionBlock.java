package com.softwareverde.tidyduck.most;

import com.softwareverde.mostadapter.Modification;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FunctionBlock {
    private Long _id;
    private String _mostId;
    private String _kind = "Proprietary";
    private String _name;
    private String _description;
    private String _release;
    private Date _lastModifiedDate;
    private Author _author;
    private Company _company;
    private String _access;
    private boolean _isCommitted;
    private List<Modification> _modifications = new ArrayList<>();
    private List<MostInterface> _mostInterfaces = new ArrayList<>();

    public Long getId() {
        return _id;
    }

    public void setId(Long id) {
        _id = id;
    }

    public String getMostId() {
        return _mostId;
    }

    public void setMostId(String mostId) {
        _mostId = mostId;
    }

    public String getKind() {
        return _kind;
    }

    public void setKind(String kind) {
        _kind = kind;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public String getDescription() {
        return _description;
    }

    public void setDescription(String description) {
        _description = description;
    }

    public String getRelease() {
        return _release;
    }

    public void setRelease(String release) {
        _release = release;
    }

    public Date getLastModifiedDate() {
        return _lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        _lastModifiedDate = lastModifiedDate;
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

    public void setAccess(String access) {
        _access = access;
    }

    public String getAccess() {
        return _access;
    }

    public boolean isCommitted() {
        return _isCommitted;
    }

    public void setCommitted(boolean committed) {
        _isCommitted = committed;
    }

    public List<Modification> getModifications() {
        return _modifications;
    }

    public void addModification(final Modification modification) {
        _modifications.add(modification);
    }

    public List<MostInterface> getMostInterfaces() {
        return new ArrayList<>(_mostInterfaces);
    }

    public void addMostInterface(MostInterface mostInterface) {
        _mostInterfaces.add(mostInterface);
    }

    public void setMostInterfaces(final List<MostInterface> mostInterfaces) {
        _mostInterfaces = new ArrayList<>(mostInterfaces);
    }
}
