package com.softwareverde.mostadapter;

import com.softwareverde.util.Util;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FunctionCatalog implements XmlNode {
    private String _name;
    private String _release;
    private String _author;
    private String _company;
    private final List<Modification> _modifications = new ArrayList<>();
    private final List<FunctionBlock> _functionBlocks = new ArrayList<>();
    private final List<ClassDefinition> _classDefinitions = new ArrayList<>();

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

    public List<FunctionBlock> getFunctionBlocks() {
        return Util.copyList(_functionBlocks);
    }

    public void addFunctionBlock(final FunctionBlock functionBlock) {
        _functionBlocks.add(functionBlock);
    }

    public void setFunctionBlocks(final List<FunctionBlock> functionBlocks) {
        _functionBlocks.clear();
        _functionBlocks.addAll(functionBlocks);
    }

    public void addClassDefinition(final ClassDefinition classDefinition) {
        _classDefinitions.add(classDefinition);
    }

    public void setClassDefinitions(final List<ClassDefinition> classDefinitions) {
        _classDefinitions.clear();
        _classDefinitions.addAll(classDefinitions);
    }

    public List<ClassDefinition> getClassDefinitions() {
        return Util.copyList(_classDefinitions);
    }

    public List<Modification> getModifications() {
        return Util.copyList(_modifications);
    }

    public void addModification(final Modification modification) {
        _modifications.add(modification);
    }

    public void setModifications(final List<Modification> modifications) {
        _modifications.clear();
        _modifications.addAll(modifications);
    }

    @Override
    public Element generateXmlElement(Document document) {
        final Element rootElement = document.createElement("FunctionCatalog");

        final Element catalogVersionElement = document.createElement("CatalogVersion");

        final Element releaseElement = XmlUtil.createTextElement(document, "Release", _release);
        catalogVersionElement.appendChild(releaseElement);
        final Date currentDate = new Date();
        final Element dateElement = XmlUtil.createTextElement(document, "Date", XmlUtil.formatDate(currentDate));
        catalogVersionElement.appendChild(dateElement);
        final Element authorAuthor = XmlUtil.createTextElement(document, "Author", _author);
        catalogVersionElement.appendChild(authorAuthor);
        final Element companyElement = XmlUtil.createTextElement(document, "Company", _company);
        catalogVersionElement.appendChild(companyElement);

        for (final Modification modification : _modifications) {
            final Element modificationElement = modification.generateXmlElement(document);
            catalogVersionElement.appendChild(modificationElement);
        }

        rootElement.appendChild(catalogVersionElement);

        for (final FunctionBlock functionBlock : _functionBlocks) {
            final Element functionBlockElement = functionBlock.generateXmlElement(document);
            rootElement.appendChild(functionBlockElement);
        }

        final Element definitionElement = document.createElement("Definition");
        for (final ClassDefinition classDefinition : _classDefinitions) {
            definitionElement.appendChild(classDefinition.generateXmlElement(document));
        }
        rootElement.appendChild(definitionElement);

        return rootElement;
    }
}
