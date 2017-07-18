package com.softwareverde.mostadapter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Operation extends XmlNode {
    private String _name;
    private String _opcode;
    private boolean _isInput;

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

    @Override
    public Element generateXmlElement(Document document) {
        // TODO: implement
        return null;
    }
}
