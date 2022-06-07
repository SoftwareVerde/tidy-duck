package com.softwareverde.mostadapter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class UnclassifiedProperty extends Property {
    private String _length;

    @Override
    protected String getFunctionClassRef() {
        return "class_unclassified_property";
    }

    @Override
    protected String getFunctionClassDescription() {
        return "";
    }

    @Override
    protected String getFunctionClassTagName() {
        return "PUnclassified";
    }

    @Override
    protected String getTagPrefix() {
        return "PU";
    }

    public String getLength() {
        return _length;
    }

    public void setLength(String length) {
        _length = length;
    }

    @Override
    protected void setClassAttributes(Document document, Element trueClassElement) {
        super.setClassAttributes(document, trueClassElement);

        if (_length != null && _length.trim().length() > 0) {
            trueClassElement.setAttribute("Length", _length);
        }
    }
}
