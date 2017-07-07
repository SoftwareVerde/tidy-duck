package com.softwareverde.tidyduck;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MostInterface extends XmlNode {

    private Long _id;
    private String _mostId;
    private String _name;
    private String _description;
    private String _version;
    private Date _lastModifiedDate;
    private boolean _isCommitted;
    private List<Modification> _modifications = new ArrayList<>();


    public Long getId() {
        return _id;
    }

    public void setId(Long id) {
        _id = id;
    }

    public String getMostId() {
        return _mostId;
    }

    public void setMostId(String mostId) {
        _mostId = mostId;
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

    public String getVersion() {
        return _version;
    }

    public void setVersion(String version) {
        _version = version;
    }

    public Date getLastModifiedDate() {
        return _lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        _lastModifiedDate = lastModifiedDate;
    }

    public boolean isCommitted() {
        return _isCommitted;
    }

    public void setCommitted(boolean committed) {
        _isCommitted = committed;
    }

    public List<Modification> getModifications() {
        return _modifications;
    }

    public void addModification(final Modification modification) {
        _modifications.add(modification);
    }

    @Override
    public Element generateXmlElement(Document document) {
        Element mostInterface = document.createElement("Interface");

        Element mostIdElement = super.createTextElement(document, "InterfaceID", _mostId);
        mostInterface.appendChild(mostIdElement);
        Element nameElement = super.createTextElement(document, "InterfaceName", _name);
        mostInterface.appendChild(nameElement);
        Element descriptionElement = super.createTextElement(document, "InterfaceDescription", _description);
        mostInterface.appendChild(descriptionElement);

        //Element versionElement = document.createElement("InterfaceVersion");

        Element versionElement = super.createTextElement(document, "Version", _version);
        versionElement.appendChild(versionElement);
        Element dateElement = super.createTextElement(document, "Date", super.formatDate(_lastModifiedDate));
        versionElement.appendChild(dateElement);

        for (final Modification modification : _modifications) {
            Element modificationElement = modification.generateXmlElement(document);
            versionElement.appendChild(modificationElement);
        }

        // TODO: append functions once available

        mostInterface.appendChild(versionElement);

        return mostInterface;
    }
}
