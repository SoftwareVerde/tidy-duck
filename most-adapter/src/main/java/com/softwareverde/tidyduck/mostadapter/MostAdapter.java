package com.softwareverde.tidyduck.mostadapter;

import com.softwareverde.tidyduck.Version;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MostAdapter {

    public String getMostXml(Version version) throws MostAdapterException {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document doc = documentBuilder.newDocument();
            // TODO: need to set ensure DOCTYPE element is added
            Element rootElement = doc.createElement("FunctionCatalog");
            // TODO: populate FunctionCatalog
            return doc.toString();
        } catch (Exception e) {
            throw new MostAdapterException("Unable to serialize FunctionCatalog.", e);
        }
    }
}
