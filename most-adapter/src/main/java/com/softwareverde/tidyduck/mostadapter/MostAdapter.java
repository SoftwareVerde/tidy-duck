package com.softwareverde.tidyduck.mostadapter;

import com.softwareverde.tidyduck.FunctionCatalog;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

public class MostAdapter {

    private boolean _isIndented = false;

    public boolean isIndented() {
        return _isIndented;
    }

    public void setIndented(boolean indented) {
        _isIndented = indented;
    }

    public String getMostXml(FunctionCatalog functionCatalog) throws MostAdapterException {
        try {
            Document document = getNewDocument();
            Element rootElement = functionCatalog.generateXmlElement(document);
            document.appendChild(rootElement);
            return convertToString(document);
        } catch (Exception e) {
            throw new MostAdapterException("Unable to serialize FunctionCatalog.", e);
        }
    }

    private Document getNewDocument() throws ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();
        return document;
    }

    private String convertToString(Document document) throws TransformerException {
        // samples are standalone
        document.setXmlStandalone(true);

        // create DOCTYPE node
        DOMImplementation domImplementation = document.getImplementation();
        DocumentType documentType = domImplementation.createDocumentType("FunctionCatalog", null, "fcat.dtd");

        // create transformer that allows for outputting the DOCTYPE
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, documentType.getSystemId());
        transformer.setOutputProperty(OutputKeys.INDENT, this._isIndented ?"yes":"no");
        // write document to string and return
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(writer));
        String xml = writer.toString();
        // fix potential bug where doctype is followed by newline
        if (!_isIndented) {
            xml = xml.replaceAll("\r|\n", "");
        }
        return xml;
    }

}
