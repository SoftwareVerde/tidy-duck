package com.softwareverde.mostadapter;

import com.softwareverde.util.IoUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

public class FunctionBlock implements XmlNode {
    private static final String COMMON_FUNCTIONS_XML_RESOURCE_PATH = "/common-functions.xml";
    private static final String SOURCE_FUNCTIONS_XML_RESOURCE_PATH = "/source-functions.xml";
    private static final String SINK_FUNCTIONS_XML_RESOURCE_PATH = "/sink-functions.xml";
    private static final String STREAM_DATA_INFO_FUNCTION_XML_RESOURCE_PATH = "/stream-data-info-function.xml";

    private String _mostId;
    private String _kind = "Proprietary";
    private String _name;
    private String _description;
    private String _release;
    private Date _lastModifiedDate;
    private String _author;
    private String _company;
    private String _access;
    private boolean _isSource;
    private boolean _isSink;
    private List<Modification> _modifications = new ArrayList<>();
    private List<MostFunction> _mostFunctions = new ArrayList<>();

    public String getMostId() {
        return _mostId;
    }

    public void setMostId(String mostId) {
        _mostId = mostId;
    }

    public String getKind() {
        return _kind;
    }

    public void setKind(String kind) {
        _kind = kind;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public String getDescription() {
        return _description;
    }

    public void setDescription(String description) {
        _description = description;
    }

    public String getRelease() {
        return _release;
    }

    public void setRelease(String release) {
        _release = release;
    }

    public Date getLastModifiedDate() {
        return _lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        _lastModifiedDate = lastModifiedDate;
    }

    public String getAuthor() {
        return _author;
    }

    public void setAuthor(String author) {
        _author = author;
    }

    public String getCompany() {
        return _company;
    }

    public void setCompany(String company) {
        _company = company;
    }

    public void setAccess(String access) {
        _access = access;
    }

    public String getAccess() {
        return _access;
    }

    public boolean isSource() {
        return _isSource;
    }

    public void setIsSource(final boolean source) {
        _isSource = source;
    }

    public boolean isSink() {
        return _isSink;
    }

    public void setIsSink(final boolean sink) {
        _isSink = sink;
    }

    public List<Modification> getModifications() {
        return _modifications;
    }

    public void addModification(final Modification modification) {
        _modifications.add(modification);
    }

    public List<MostFunction> getMostFunctions() {
        return new ArrayList<>(_mostFunctions);
    }

    public void addMostFunction(MostFunction mostFunction) {
        _mostFunctions.add(mostFunction);
    }

    @Override
    public Element generateXmlElement(final Document document) {
        final Element functionBlock = document.createElement("FBlock");

        final Element mostIdElement = XmlUtil.createTextElement(document, "FBlockID", _mostId);
        functionBlock.appendChild(mostIdElement);
        final Element kindElement = XmlUtil.createTextElement(document, "FBlockKind", _kind);
        functionBlock.appendChild(kindElement);
        final Element nameElement = XmlUtil.createTextElement(document, "FBlockName", _name);
        functionBlock.appendChild(nameElement);
        final Element descriptionElement = XmlUtil.createTextElement(document, "FBlockDescription", _description);
        functionBlock.appendChild(descriptionElement);

        final Element versionElement = document.createElement("FBlockVersion");
        versionElement.setAttribute("Access", _access);

        final Element releaseElement = XmlUtil.createTextElement(document, "Release", _release);
        versionElement.appendChild(releaseElement);
        final Element dateElement = XmlUtil.createTextElement(document, "Date", XmlUtil.formatDate(_lastModifiedDate));
        versionElement.appendChild(dateElement);
        final Element authorElement = XmlUtil.createTextElement(document, "Author", _author);
        versionElement.appendChild(authorElement);
        final Element companyElement = XmlUtil.createTextElement(document, "Company", _company);
        versionElement.appendChild(companyElement);

        functionBlock.appendChild(versionElement);

        for (final Modification modification : _modifications) {
            final Element modificationElement = modification.generateXmlElement(document);
            versionElement.appendChild(modificationElement);
        }

        // append all common functions
        appendFunctions(document, functionBlock, COMMON_FUNCTIONS_XML_RESOURCE_PATH);

        // append source functions
        if (_isSource) {
            appendFunctions(document, functionBlock, SOURCE_FUNCTIONS_XML_RESOURCE_PATH);
        }

        // append sink functions
        if (_isSink) {
            appendFunctions(document, functionBlock, SINK_FUNCTIONS_XML_RESOURCE_PATH);
        }

        // append stream data info function (if sink or source)
        if (_isSink || _isSource) {
            appendFunctions(document, functionBlock, STREAM_DATA_INFO_FUNCTION_XML_RESOURCE_PATH);
        }

        // append user-defined functions
        final List<MostFunction> sortedMostFunctions = _getSortedMostFunctions();
        for (final MostFunction mostFunction : sortedMostFunctions) {
            final Element functionElement = mostFunction.generateXmlElement(document);
            functionBlock.appendChild(functionElement);
        }

        return functionBlock;
    }

    /**
     * <p>Retrieve functions from <code>resourceName</code> and add any nodes there as children of <code>functionBlock</code>.</p>
     * @param document
     * @param functionBlock
     * @param resourceName
     */
    private void appendFunctions(final Document document, final Element functionBlock, final String resourceName) {
        try {
            final NodeList functions = getResourceXmlNodes(resourceName);
            for (int i=0; i<functions.getLength(); i++) {
                Node function = functions.item(i);
                if (function.getNodeType() != Node.TEXT_NODE) {
                    function = document.importNode(function, true);
                    functionBlock.appendChild(function);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to collect functions from resource : " + resourceName, e);
        }
    }

    /**
     * <p>Reads static XML file for function definitions.  Return the list of function nodes therein.</p>
     *
     * <p>The individual nodes must be imported (see Document.import) before appending them to the current document</p>
     * @return
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    private NodeList getResourceXmlNodes(String resourceName) throws ParserConfigurationException, IOException, SAXException {
        final String extractedXml = IoUtil.getResource(resourceName);
        final String staticXml = "<root>" + extractedXml + "</root>";

        final DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Node fragmentNode = documentBuilder.parse(new InputSource(new StringReader(staticXml))).getDocumentElement();
        NodeList functionNodes = fragmentNode.getChildNodes();

        return functionNodes;
    }

    /**
     * <p>Returns a sorted list of the functions added to this function block.</p>
     * @return
     */
    private List<MostFunction> _getSortedMostFunctions() {
        final List<MostFunction> mostFunctionsCopy = new ArrayList<>(_mostFunctions);
        Collections.sort(mostFunctionsCopy, new Comparator<MostFunction>() {
            @Override
            public int compare(final MostFunction o1, final MostFunction o2) {
                return o1.getMostId().compareTo(o2.getMostId());
            }
        });
        return mostFunctionsCopy;
    }
}
