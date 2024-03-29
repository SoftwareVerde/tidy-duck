package com.softwareverde.tidyduck.most;

import java.util.ArrayList;
import java.util.List;

public class MostFunctionStereotype {
    private Long _id;
    private String _name;
    private boolean _supportsNotification;
    private String _category;
    private List<Operation> _operations;

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
        _name = name;
    }

    public boolean supportsNotification() {
        return _supportsNotification;
    }

    public void setSupportsNotification(boolean supportsNotification) {
        _supportsNotification = supportsNotification;
    }

    public String getCategory() {
        return _category;
    }

    public void setCategory(String category) {
        _category = category;
    }

    public List<Operation> getOperations() {
        return new ArrayList<>(_operations);
    }

    public void addOperation(final Operation operation) {
        _operations.add(operation);
    }

    public void setOperations(final List<Operation> operations) {
        _operations = new ArrayList<>(operations);
    }
}
