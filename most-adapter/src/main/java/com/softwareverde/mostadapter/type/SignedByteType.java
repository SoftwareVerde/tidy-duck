package com.softwareverde.mostadapter.type;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SignedByteType extends MostType {
    @Override
    public String getTypeName() {
        return "TSByte";
    }

    @Override
    protected String getTypeRef() {
        return "type_signed_byte";
    }

    @Override
    protected void appendChildElements(Document document, Element typeElement) {
        // no actions required
    }
}
