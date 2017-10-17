package com.softwareverde.mostadapter;

import com.softwareverde.util.Util;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.*;

public class FunctionCatalog implements XmlNode {
    private String _name;
    private String _release;
    private String _author;
    private String _company;
    private final List<Modification> _modifications = new ArrayList<>();
    private final List<FunctionBlock> _functionBlocks = new ArrayList<>();
    private final List<ClassDefinition> _classDefinitions = new ArrayList<>();
    private final List<PropertyCommandDefinition> _propertyCommandDefinitions = new ArrayList<>();
    private final List<MethodCommandDefinition> _methodCommandDefinitions = new ArrayList<>();
    private final List<PropertyReportDefinition> _propertyReportDefinitions = new ArrayList<>();
    private final List<MethodReportDefinition> _methodReportDefinitions = new ArrayList<>();
    private final List<TypeDefinition> _typeDefinitions = new ArrayList<>();
    private final List<UnitDefinition> _unitDefinitions = new ArrayList<>();
    private final List<ErrorDefinition> _errorDefinitions = new ArrayList<>();

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

    public void addPropertyCommandDefinition(final PropertyCommandDefinition commandDefinition) {
        _propertyCommandDefinitions.add(commandDefinition);
    }

    public void setPropertyCommandDefinitions(final List<PropertyCommandDefinition> commandDefinitions) {
        _propertyCommandDefinitions.clear();
        _propertyCommandDefinitions.addAll(commandDefinitions);
    }

    public List<PropertyCommandDefinition> getPropertyCommandDefinitions() {
        return Util.copyList(_propertyCommandDefinitions);
    }

    public void addMethodCommandDefinition(final MethodCommandDefinition commandDefinition) {
        _methodCommandDefinitions.add(commandDefinition);
    }

    public void setMethodCommandDefinitions(final List<MethodCommandDefinition> commandDefinitions) {
        _methodCommandDefinitions.clear();
        _methodCommandDefinitions.addAll(commandDefinitions);
    }

    public List<MethodCommandDefinition> getMethodCommandDefinitions() {
        return Util.copyList(_methodCommandDefinitions);
    }

    public void addPropertyReportDefinition(final PropertyReportDefinition commandDefinition) {
        _propertyReportDefinitions.add(commandDefinition);
    }

    public void setPropertyReportDefinitions(final List<PropertyReportDefinition> commandDefinitions) {
        _propertyReportDefinitions.clear();
        _propertyReportDefinitions.addAll(commandDefinitions);
    }

    public List<PropertyReportDefinition> getPropertyReportDefinitions() {
        return Util.copyList(_propertyReportDefinitions);
    }

    public void addMethodReportDefinition(final MethodReportDefinition commandDefinition) {
        _methodReportDefinitions.add(commandDefinition);
    }

    public void setMethodReportDefinitions(final List<MethodReportDefinition> commandDefinitions) {
        _methodReportDefinitions.clear();
        _methodReportDefinitions.addAll(commandDefinitions);
    }

    public List<MethodReportDefinition> getMethodReportDefinitions() {
        return Util.copyList(_methodReportDefinitions);
    }

    public void addTypeDefinition(final TypeDefinition commandDefinition) {
        _typeDefinitions.add(commandDefinition);
    }

    public void setTypeDefinitions(final List<TypeDefinition> commandDefinitions) {
        _typeDefinitions.clear();
        _typeDefinitions.addAll(commandDefinitions);
    }

    public List<TypeDefinition> getTypeDefinitions() {
        return Util.copyList(_typeDefinitions);
    }

    public void addUnitDefinition(final UnitDefinition commandDefinition) {
        _unitDefinitions.add(commandDefinition);
    }

    public void setUnitDefinitions(final List<UnitDefinition> commandDefinitions) {
        _unitDefinitions.clear();
        _unitDefinitions.addAll(commandDefinitions);
    }

    public List<UnitDefinition> getUnitDefinitions() {
        return Util.copyList(_unitDefinitions);
    }

    public void addErrorDefinition(final ErrorDefinition commandDefinition) {
        _errorDefinitions.add(commandDefinition);
    }

    public void setErrorDefinitions(final List<ErrorDefinition> commandDefinitions) {
        _errorDefinitions.clear();
        _errorDefinitions.addAll(commandDefinitions);
    }

    public List<ErrorDefinition> getErrorDefinitions() {
        return Util.copyList(_errorDefinitions);
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

        // ensure function blocks are sorted by ID
        final List<FunctionBlock> sortedFunctionBlocks = _getSortedFunctionBlocks();
        for (final FunctionBlock functionBlock : sortedFunctionBlocks) {
            final Element functionBlockElement = functionBlock.generateXmlElement(document);
            rootElement.appendChild(functionBlockElement);
        }

        final Element definitionElement = document.createElement("Definition");
        {
            for (final ClassDefinition classDefinition : _classDefinitions) {
                definitionElement.appendChild(classDefinition.generateXmlElement(document));
            }

            for (final PropertyCommandDefinition commandDefinition : _propertyCommandDefinitions) {
                definitionElement.appendChild(commandDefinition.generateXmlElement(document));
            }

            for (final MethodCommandDefinition commandDefinition : _methodCommandDefinitions) {
                definitionElement.appendChild(commandDefinition.generateXmlElement(document));
            }

            for (final PropertyReportDefinition reportDefinition : _propertyReportDefinitions) {
                definitionElement.appendChild(reportDefinition.generateXmlElement(document));
            }

            for (final MethodReportDefinition reportDefinition : _methodReportDefinitions) {
                definitionElement.appendChild(reportDefinition.generateXmlElement(document));
            }

            for (final TypeDefinition typeDefinition : _typeDefinitions) {
                definitionElement.appendChild(typeDefinition.generateXmlElement(document));
            }

            for (final UnitDefinition unitDefinition : _unitDefinitions) {
                definitionElement.appendChild(unitDefinition.generateXmlElement(document));
            }

            for (final ErrorDefinition errorDefinition : _errorDefinitions) {
                definitionElement.appendChild(errorDefinition.generateXmlElement(document));
            }
        }
        rootElement.appendChild(definitionElement);

        return rootElement;
    }

    /**
     * <p>Returns a sorted list of the function blocks added to this function catalog.</p>
     * @return
     */
    private List<FunctionBlock> _getSortedFunctionBlocks() {
        final List<FunctionBlock> functionBlocksCopy = new ArrayList<>(_functionBlocks);
        Collections.sort(functionBlocksCopy, new Comparator<FunctionBlock>() {
            @Override
            public int compare(final FunctionBlock o1, final FunctionBlock o2) {
                return o1.getMostId().compareTo(o2.getMostId());
            }
        });
        return functionBlocksCopy;
    }
}
