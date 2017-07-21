package com.softwareverde.mostadapter.type;

import com.softwareverde.mostadapter.XmlNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class MostType implements XmlNode {

    public abstract String getTypeName();

    protected abstract String getTypeRef();

    protected abstract void appendChildElements(Document document, Element typeElement);

    @Override
    public Element generateXmlElement(Document document) {
        Element typeElement = document.createElement(getTypeName());

        String typeRef = getTypeRef();
        if (typeRef != null) {
            typeElement.setAttribute("TypeRef", typeRef);
        }

        appendChildElements(document, typeElement);

        return typeElement;
    }
}
