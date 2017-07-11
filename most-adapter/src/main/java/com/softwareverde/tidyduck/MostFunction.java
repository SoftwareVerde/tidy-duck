package com.softwareverde.tidyduck;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Represents a MOST function.</p>
 *
 * <p>This class is abstract as it does not implement the
 * XmlNode generateXmlElement method.  This is left for
 * concrete implementations (e.g. Property and Method) to
 * implements since the XML export may change depending
 * on the function type.</p>
 */
public abstract class MostFunction extends XmlNode {

    private Long _id;
    private String _mostId;
    private String _name;
    private String _description;
    private String _release;
    private Author _author;
    private Company _company;
    private MostType _returnType;
    private List<Operation> _operations = new ArrayList<>();

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

    public String getRelease() {
        return _release;
    }

    public void setRelease(String release) {
        _release = release;
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

    public MostType getReturnType() {
        return _returnType;
    }

    public void setReturnType(MostType returnType) {
        _returnType = returnType;
    }

    public List<Operation> getOperations() {
        return new ArrayList<>(_operations);
    }

    public void addOperation(Operation operation) {
        _operations.add(operation);
    }

    protected abstract Element generateFunctionClassElement(Document document);

    @Override
    public Element generateXmlElement(Document document) {
        Element functionElement = document.createElement("Function");

        Element functionIdElement = super.createTextElement(document, "FunctionID", _mostId);
        functionElement.appendChild(functionIdElement);
        Element functionNameElement = super.createTextElement(document, "FunctionName", _name);
        functionElement.appendChild(functionNameElement);
        Element functionDescription = super.createTextElement(document, "FunctionDescription", _description);
        functionElement.appendChild(functionDescription);

        Element functionVersion = document.createElement("FunctionVersion");

        Element functionRelease = super.createTextElement(document, "Release", _release);
        functionVersion.appendChild(functionRelease);
        Element functionAuthor = super.createTextElement(document, "Author", _author.getName());
        functionVersion.appendChild(functionAuthor);
        Element functionCompany = super.createTextElement(document, "Company", _company.getName());
        functionCompany.appendChild(functionCompany);

        functionElement.appendChild(functionVersion);

        functionElement.appendChild(generateFunctionClassElement(document));

        return functionElement;
    }
}
