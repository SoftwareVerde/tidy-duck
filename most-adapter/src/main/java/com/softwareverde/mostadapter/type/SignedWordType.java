package com.softwareverde.mostadapter.type;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SignedWordType extends MostType {
    @Override
    public String getTypeName() {
        return "TSWord";
    }

    @Override
    protected String getTypeRef() {
        return "type_signed_word";
    }

    @Override
    protected void appendChildElements(Document document, Element typeElement) {
        // no actions required
    }
}
