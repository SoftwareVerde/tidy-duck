package com.softwareverde.tidyduck.most;

import com.softwareverde.mostadapter.Modification;

import java.util.ArrayList;
import java.util.List;

public class FunctionCatalog {
    private Long _id;
    private String _name;
    private String _release;
    private Author _author;
    private Company _company;
    private List<FunctionBlock> _functionBlocks = new ArrayList<>();
    private boolean _isCommitted;
    private List<Modification> _modifications = new ArrayList<>();

    public Long getId() {
        return _id;
    }

    public void setId(long id) {
        _id = id;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public String getRelease() {
        return _release;
    }

    public void setRelease(String release) {
        _release = release;
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

    public void setIsCommitted(boolean committed) {
        _isCommitted = committed;
    }

    public List<FunctionBlock> getFunctionBlocks() {
        return _functionBlocks;
    }

    public void addFunctionBlock(final FunctionBlock functionBlock) {
        _functionBlocks.add(functionBlock);
    }

    public void setFunctionBlocks(List<FunctionBlock> functionBlocks) {
        _functionBlocks = new ArrayList<>(functionBlocks);
    }

    public List<Modification> getModifications() {
        return _modifications;
    }

    public void addModification(Modification modification) {
        _modifications.add(modification);
    }

    public void setModifications(List<Modification> modifications) {
        _modifications = new ArrayList<>(modifications);
    }

    public com.softwareverde.mostadapter.FunctionCatalog toMostAdapterFunctionCatalog() {
        com.softwareverde.mostadapter.FunctionCatalog functionCatalog = new com.softwareverde.mostadapter.FunctionCatalog();
        // TODO: convert to MostAdapter classes
        return functionCatalog;
    }
}
