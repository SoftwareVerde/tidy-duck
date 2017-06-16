package com.softwareverde.tidyduck;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FunctionBlock extends XmlNode {

    public enum Kind {
        PROPRIETARY("Proprietary");

        private final String _xmlText;

        Kind(String xmlText) {
            _xmlText = xmlText;
        }

        public String getXmlText() {
            return _xmlText;
        }
    }

    private Long _id;
    private String _mostId;
    private Kind _kind = Kind.PROPRIETARY;
    private String _name;
    private String _description;
    private String _release;
    private Date _lastModifiedDate;
    private Author _author;
    private Company _company;
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

    public Kind getKind() {
        return _kind;
    }

    public void setKind(Kind kind) {
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

    public Author getAuthor() {
        return _author;
    }

    public void setAuthor(Author author) {
        _author = author;
    }

    public Company getCompany() {
        return _company;
    }

    public void setCompany(Company company) {
        _company = company;
    }

    public List<Modification> getModifications() {
        return _modifications;
    }

    public void addModification(final Modification modification) {
        _modifications.add(modification);
    }

    @Override
    public Element generateXmlElement(Document document) {
        Element functionBlock = document.createElement("FBlock");

        Element mostIdElement = super.createTextElement(document, "FBlockID", _mostId);
        functionBlock.appendChild(mostIdElement);
        Element kindElement = super.createTextElement(document, "FBlockKind", _kind.getXmlText());
        functionBlock.appendChild(kindElement);
        Element nameElement = super.createTextElement(document, "FBlockName", _name);
        functionBlock.appendChild(nameElement);
        Element descriptionElement = super.createTextElement(document, "FBlockDescription", _description);
        functionBlock.appendChild(descriptionElement);

        Element versionElement = document.createElement("FBlockVersion");
        versionElement.setAttribute("Access", "public");

        Element releaseElement = super.createTextElement(document, "Release", _release);
        versionElement.appendChild(releaseElement);
        Element dateElement = super.createTextElement(document, "Date", super.formatDate(_lastModifiedDate));
        versionElement.appendChild(dateElement);
        Element authorElement = super.createTextElement(document, "Author", _author.getName());
        versionElement.appendChild(authorElement);
        Element companyElement = super.createTextElement(document, "Company", _company.getName());
        versionElement.appendChild(companyElement);

        for (final Modification modification : _modifications) {
            Element modificationElement = modification.generateXmlElement(document);
            versionElement.appendChild(modificationElement);
        }

        // TODO: append functions once available

        functionBlock.appendChild(versionElement);

        return functionBlock;
    }
}
