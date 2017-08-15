package com.softwareverde.mostadapter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FunctionBlock implements XmlNode {
    private String _mostId;
    private String _kind = "Proprietary";
    private String _name;
    private String _description;
    private String _release;
    private Date _lastModifiedDate;
    private String _author;
    private String _company;
    private String _access;
    private boolean _isCommitted;
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

        for (final MostFunction mostFunction : _mostFunctions) {
            final Element functionElement = mostFunction.generateXmlElement(document);
            functionBlock.appendChild(functionElement);
        }

        return functionBlock;
    }
}
