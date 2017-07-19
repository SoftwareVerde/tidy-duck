package com.softwareverde.mostadapter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FunctionCatalog implements XmlNode {
    private Long _id;
    private String _name;
    private String _release;
    private String _author;
    private String _company;
    private List<FunctionBlock> _functionBlocks = new ArrayList<>();
    private boolean _isCommitted;
    private List<Modification> _modifications = new ArrayList<>();

    public Long getId() {
        return _id;
    }

    public void setId(long id) {
        _id = id;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public String getRelease() {
        return _release;
    }

    public void setRelease(String release) {
        _release = release;
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

    public boolean isCommitted() {
        return _isCommitted;
    }

    public void setIsCommitted(boolean committed) {
        _isCommitted = committed;
    }

    public List<FunctionBlock> getFunctionBlocks() {
        return _functionBlocks;
    }

    public void addFunctionBlock(final FunctionBlock functionBlock) {
        _functionBlocks.add(functionBlock);
    }

    public void setFunctionBlocks(List<FunctionBlock> functionBlocks) {
        _functionBlocks = new ArrayList<>(functionBlocks);
    }

    public List<Modification> getModifications() {
        return _modifications;
    }

    public void addModification(Modification modification) {
        _modifications.add(modification);
    }

    public void setModifications(List<Modification> modifications) {
        _modifications = new ArrayList<>(modifications);
    }

    @Override
    public Element generateXmlElement(Document document) {
        Element rootElement = document.createElement("FunctionCatalog");

        Element catalogVersion = document.createElement("CatalogVersion");

        Element release = XmlUtil.createTextElement(document, "Release", _release);
        catalogVersion.appendChild(release);
        final Date currentDate = new Date();
        Element date = XmlUtil.createTextElement(document, "Date", XmlUtil.formatDate(currentDate));
        catalogVersion.appendChild(date);
        Element author = XmlUtil.createTextElement(document, "Author", _author);
        catalogVersion.appendChild(author);
        Element company = XmlUtil.createTextElement(document, "Company", _company);
        catalogVersion.appendChild(company);

        for (final Modification modification : _modifications) {
            Element modificationElement = modification.generateXmlElement(document);
            catalogVersion.appendChild(modificationElement);
        }

        rootElement.appendChild(catalogVersion);

        for (final FunctionBlock functionBlock : _functionBlocks) {
            Element functionBlockElement = functionBlock.generateXmlElement(document);
            rootElement.appendChild(functionBlockElement);
        }

        return rootElement;
    }
}
