package com.softwareverde.tidyduck.most;

import java.util.ArrayList;
import java.util.List;

public class Version {
    private Long _id;
    private String _name;
    private Author _owner;
    private boolean _isReleased;
    private List<FunctionCatalog> _functionCatalogs = new ArrayList<FunctionCatalog>();

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

    public Author getOwner() {
        return _owner;
    }

    public void setOwner(Author owner) {
        _owner = owner;
    }

    public boolean isReleased() {
        return _isReleased;
    }

    public void setReleased(boolean released) {
        _isReleased = released;
    }

    public List<FunctionCatalog> getFunctionCatalogs() {
        return new ArrayList<FunctionCatalog>(_functionCatalogs);
    }

    public void addFunctionCatalog(FunctionCatalog functionCatalog) {
        _functionCatalogs.add(functionCatalog);
    }

    public void setFunctionCatalogs(List<FunctionCatalog> functionCatalogs) {
        _functionCatalogs = new ArrayList<FunctionCatalog>(functionCatalogs);
    }
}
