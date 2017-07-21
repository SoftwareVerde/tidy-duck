package com.softwareverde.mostadapter.type;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class VoidType extends MostType {
    @Override
    public String getTypeName() {
        return "TVoid";
    }

    @Override
    protected String getTypeRef() {
        return "type_void";
    }

    @Override
    protected void appendChildElements(Document document, Element typeElement) {
        // no actions required
    }
}
