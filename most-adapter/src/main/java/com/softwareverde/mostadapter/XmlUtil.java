package com.softwareverde.mostadapter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.text.SimpleDateFormat;
import java.util.Date;

public class XmlUtil {

    public static Element createTextElement(final Document document, final String name, final String value) {
        final Element element = document.createElement(name);
        element.appendChild(document.createTextNode(value));
        return element;
    }

    public static String formatDate(final Date date) {
        final SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
        return formatter.format(date).toUpperCase();
    }
}
