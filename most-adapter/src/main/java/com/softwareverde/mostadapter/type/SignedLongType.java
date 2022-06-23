package com.softwareverde.mostadapter.type;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SignedLongType extends MostType {
    @Override
    public String getTypeName() {
        return "TSLong";
    }

    @Override
    protected String getTypeRef() {
        return "type_signed_long";
    }

    @Override
    protected void appendChildElements(Document document, Element typeElement) {
        // no actions required
    }
}
