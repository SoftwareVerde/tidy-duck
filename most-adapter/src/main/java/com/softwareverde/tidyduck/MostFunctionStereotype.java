package com.softwareverde.tidyduck;

import java.util.ArrayList;
import java.util.List;

public class MostFunctionStereotype {
    private Long _id;
    private String _name;
    private boolean _supportsNotification;
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
