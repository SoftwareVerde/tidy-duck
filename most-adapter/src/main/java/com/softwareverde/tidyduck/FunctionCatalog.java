package com.softwareverde.tidyduck;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FunctionCatalog extends XmlNode {
    private long _id;
    private String _release;
    private Date _releaseDate;
    private Author _author;
    private Company _company;
    private boolean _isCommitted;
    private List<Modification> _modifications = new ArrayList<Modification>();

    public long getId() {
        return _id;
    }

    public void setId(long id) {
        _id = id;
    }

    public String getRelease() {
        return _release;
    }

    public void setRelease(String release) {
        _release = release;
    }

    public Date getReleaseDate() {
        return _releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        _releaseDate = releaseDate;
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

    public boolean isCommitted() {
        return _isCommitted;
    }

    public void setCommitted(boolean committed) {
        _isCommitted = committed;
    }

    public List<Modification> getModifications() {
        return _modifications;
    }

    public void addModification(Modification modification) {
        _modifications.add(modification);
    }

    public void setModifications(List<Modification> modifications) {
        _modifications = modifications;
    }

    @Override
    public Element generateXmlElement(Document document) {
        Element rootElement = document.createElement("FunctionCatalog");

        Element catalogVersion = document.createElement("CatalogVersion");

        Element release = super.createTextElement(document, "Release", _release);
        catalogVersion.appendChild(release);
        Element date = super.createTextElement(document, "Date", getFormattedReleaseDate());
        catalogVersion.appendChild(date);
        Element author = super.createTextElement(document, "Author", _author.getName());
        catalogVersion.appendChild(author);
        Element company = super.createTextElement(document, "Company", _company.getName());
        catalogVersion.appendChild(company);

        for (Modification modification : _modifications) {
            Element modificationElement = modification.generateXmlElement(document);
            catalogVersion.appendChild(modificationElement);
        }

        rootElement.appendChild(catalogVersion);

        // TODO: once function blocks are added, add elements for them

        return rootElement;
    }

    protected String getFormattedReleaseDate() {
        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        return format.format(_releaseDate).toUpperCase();
    }
}
