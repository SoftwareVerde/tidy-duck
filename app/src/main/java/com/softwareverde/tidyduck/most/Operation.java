package com.softwareverde.tidyduck.most;

public class Operation {
    private Long _id;
    private String _name;
    private String _opcode;
    private boolean _isInput;

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

    public String getOpcode() {
        return _opcode;
    }

    public void setOpcode(String opcode) {
        _opcode = opcode;
    }

    public boolean isInput() {
        return _isInput;
    }

    public void setInput(boolean input) {
        _isInput = input;
    }

}
