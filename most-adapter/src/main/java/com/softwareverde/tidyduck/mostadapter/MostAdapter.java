package com.softwareverde.tidyduck.mostadapter;

import com.softwareverde.tidyduck.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MostAdapter {

    private final Logger _logger = LoggerFactory.getLogger(getClass());

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
