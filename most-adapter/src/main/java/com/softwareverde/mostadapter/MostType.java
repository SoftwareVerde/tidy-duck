package com.softwareverde.mostadapter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class MostType implements XmlNode {
    private String _name;

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        this._name = name;
    }

    @Override
    public Element generateXmlElement(Document document) {
        // TODO: implement
        return null;
    }
}
