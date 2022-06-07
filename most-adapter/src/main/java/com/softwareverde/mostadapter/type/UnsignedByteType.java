package com.softwareverde.mostadapter.type;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class UnsignedByteType extends MostType {
    @Override
    public String getTypeName() {
        return "TUByte";
    }

    @Override
    protected String getTypeRef() {
        return "type_unsigned_byte";
    }

    @Override
    protected void appendChildElements(Document document, Element typeElement) {
        // no actions required
    }
}
