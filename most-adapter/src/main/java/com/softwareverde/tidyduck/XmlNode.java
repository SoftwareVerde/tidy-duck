package com.softwareverde.tidyduck;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class XmlNode {

    public abstract Element generateXmlElement(Document document);

    protected Element createTextElement(Document document, String name, String value) {
        Element element = document.createElement(name);
        element.appendChild(document.createTextNode(value));
        return element;
    }

    protected String formatDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
        return formatter.format(date).toUpperCase();
    }
}
