package com.softwareverde.tidyduck;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class XmlNode {

    public abstract Element generateXmlElement(Document document);

    protected Element createTextElement(Document document, String name, String value) {
        Element element = document.createElement(name);
        element.appendChild(document.createTextNode(value));
        return element;
    }
}
