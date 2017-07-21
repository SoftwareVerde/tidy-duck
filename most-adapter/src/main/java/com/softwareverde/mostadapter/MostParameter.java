package com.softwareverde.mostadapter;

import java.util.ArrayList;
import java.util.List;

public class MostParameter {
    public static final String NULL_PARAMETER_INDEX = "#NULL#";

    private String _name;
    private String _index = NULL_PARAMETER_INDEX;
    private String _description;
    private MostType _type;
    private List<Operation> _operations = new ArrayList<>();

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public String getIndex() {
        return _index;
    }

    public void setIndex(String index) {
        _index = index;
    }

    public String getDescription() {
        return _description;
    }

    public void setDescription(String description) {
        _description = description;
    }

    public boolean hasDetails() {
        return _description != null && _description.length() == 0;
    }

    public MostType getType() {
        return _type;
    }

    public void setType(MostType type) {
        _type = type;
    }

    public List<Operation> getOperations() {
        return new ArrayList<>(_operations);
    }

    public void addOperation(final Operation operation) {
        _operations.add(operation);
    }

    public void setOperations(List<Operation> operations) {
        _operations = new ArrayList<>(operations);
    }
}
