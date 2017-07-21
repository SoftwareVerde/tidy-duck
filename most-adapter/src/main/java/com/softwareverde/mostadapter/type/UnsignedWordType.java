package com.softwareverde.mostadapter.type;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class UnsignedWordType extends MostType {
    @Override
    public String getTypeName() {
        return "TUWord";
    }

    @Override
    protected String getTypeRef() {
        return "type_unsigned_word";
    }

    @Override
    protected void appendChildElements(Document document, Element typeElement) {
        // no actions required
    }
}
