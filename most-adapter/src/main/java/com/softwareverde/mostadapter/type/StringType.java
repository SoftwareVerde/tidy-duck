package com.softwareverde.mostadapter.type;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class StringType extends MostType {
    private String _maxSize = "255";

    public String getMaxSize() {
        return _maxSize;
    }

    public void setMaxSize(String maxSize) {
        _maxSize = maxSize;
    }

    @Override
    public String getTypeName() {
        return "TString";
    }

    @Override
    protected String getTypeRef() {
        return "type_string";
    }

    @Override
    protected void appendChildElements(Document document, Element typeElement) {
        typeElement.setAttribute("MaxSize", _maxSize);
    }
}
