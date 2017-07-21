package com.softwareverde.mostadapter.type;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class UnsignedLongType extends MostType {
    @Override
    public String getTypeName() {
        return "TULong";
    }

    @Override
    protected String getTypeRef() {
        return "type_unsigned_long";
    }

    @Override
    protected void appendChildElements(Document document, Element typeElement) {
        // no actions required
    }
}
