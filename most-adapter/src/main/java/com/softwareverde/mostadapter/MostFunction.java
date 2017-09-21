package com.softwareverde.mostadapter;

import com.softwareverde.mostadapter.type.MostType;
import com.softwareverde.util.Util;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Date;
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
public abstract class MostFunction implements XmlNode {
    private String _mostId;
    private String _name;
    private String _description;
    private String _release;
    private String _author;
    private String _company;
    private List<MostParameter> _mostParameters = new ArrayList<>();

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

    public List<MostParameter> getMostParameters() {
        return new ArrayList<>(_mostParameters);
    }

    public void addMostParameter(final MostParameter parameter) {
        _mostParameters.add(parameter);
    }

    public void setMostParameters(final List<MostParameter> mostParameters) {
        _mostParameters = new ArrayList<>(mostParameters);
    }

    public abstract String getFunctionType();

    protected abstract String getFunctionClassRef();

    protected abstract String getFunctionClassDescription();

    protected abstract String getFunctionClassTagName();

    protected abstract String getTagPrefix();

    protected String getParamTagName() {
        return getTagPrefix() + "Param";
    }

    protected String getParamOPTypeTagName() {
        return getTagPrefix() + "ParamOPType";
    }

    protected String getCommandTagName() {
        return getTagPrefix() + "Command";
    }

    protected String getReportTagName() {
        return getTagPrefix() + "Report";
    }

    protected String getParamTypeTagName() {
        return getTagPrefix() + "ParamType";
    }

    @Override
    public Element generateXmlElement(Document document) {
        Element functionElement = document.createElement("Function");

        Element functionIdElement = XmlUtil.createTextElement(document, "FunctionID", _mostId);
        functionElement.appendChild(functionIdElement);
        Element functionNameElement = XmlUtil.createTextElement(document, "FunctionName", _name);
        functionElement.appendChild(functionNameElement);
        Element functionDescription = XmlUtil.createTextElement(document, "FunctionDescription", _description);
        functionElement.appendChild(functionDescription);

        Element functionVersionElement = document.createElement("FunctionVersion");

        Element functionRelease = XmlUtil.createTextElement(document, "Release", _release);
        functionVersionElement.appendChild(functionRelease);
        final Date currentDate = new Date();
        final Element dateElement = XmlUtil.createTextElement(document, "Date", XmlUtil.formatDate(currentDate));
        functionVersionElement.appendChild(dateElement);
        Element functionAuthor = XmlUtil.createTextElement(document, "Author", _author);
        functionVersionElement.appendChild(functionAuthor);
        Element functionCompany = XmlUtil.createTextElement(document, "Company", _company);
        functionVersionElement.appendChild(functionCompany);

        functionElement.appendChild(functionVersionElement);

        Element functionClassElement = getFunctionClassElement(document);
        functionElement.appendChild(functionClassElement);

        return functionElement;
    }

    protected Element getFunctionClassElement(Document document) {
        Element functionClassElement = document.createElement("FunctionClass");
        functionClassElement.setAttribute("ClassRef", getFunctionClassRef());
        Element functionClassDescriptionElement = XmlUtil.createTextElement(document, "FunctionClassDesc", getFunctionClassDescription());
        functionClassElement.appendChild(functionClassDescriptionElement);

        Element functionTypeElement = document.createElement(getFunctionType());
        populateFunctionTypeElement(document, functionTypeElement);

        Element trueClassElement = document.createElement(getFunctionClassTagName());
        populateTrueClassElement(document, trueClassElement);

        functionTypeElement.appendChild(trueClassElement);
        functionClassElement.appendChild(functionTypeElement);

        return functionClassElement;
    }

    /**
     * <p>Should be overridden by function implementations as necessary.</p>
     *
     * @param document
     * @param functionTypeElement
     */
    protected void populateFunctionTypeElement(Document document, Element functionTypeElement) {
        // do nothing, not required for all functions
    }

    /**
     * <p>Should be overridden by function class implementations as necessary.  Most will not need to since
     * these elements only apply to certain function classes.</p>
     * @param document
     * @param trueClassElement
     */
    protected void setClassAttributes(Document document, Element trueClassElement) {
        // do nothing, not required for most function classes
    }

    /**
     * <p></p>
     *
     * <p>Should be overridden by function class implementations as necessary.  Most will not need to since
     * these elements only apply to certain function classes.</p>
     * @param document
     * @param functionClassElement
     */
    protected void appendPositionDescriptionElements(Document document, Element functionClassElement) {
        // do nothing, not required for most function classes
    }

    protected void populateTrueClassElement(final Document document, final Element trueClassElement) {
        setClassAttributes(document, trueClassElement);
        appendPositionDescriptionElements(document, trueClassElement);

        for (final MostParameter mostParameter : _mostParameters) {
            final Element paramElement = document.createElement(this.getParamTagName());
            paramElement.setAttribute("details", mostParameter.hasDetails() ? "true" : "false");

            final Element paramNameElement = XmlUtil.createTextElement(document, "ParamName", Util.coalesce(mostParameter.getName()));
            paramNameElement.setAttribute("ParamIdx", mostParameter.getIndex());
            paramElement.appendChild(paramNameElement);

            final Element paramDescriptionElement = XmlUtil.createTextElement(document, "ParamDescription", Util.coalesce(mostParameter.getDescription()));
            paramElement.appendChild(paramDescriptionElement);

            final Element paramOpTypeElement = document.createElement(getParamOPTypeTagName());
            populateParamOpTypeElement(document, paramOpTypeElement, mostParameter.getOperations());
            paramElement.appendChild(paramOpTypeElement);

            final Element paramTypeElement = document.createElement(getParamTypeTagName());
            populateParamTypeElement(document, paramTypeElement, mostParameter.getType());
            paramElement.appendChild(paramTypeElement);

            trueClassElement.appendChild(paramElement);
        }
    }

    protected void populateParamOpTypeElement(Document document, Element paramOpTypeElement, List<Operation> operations) {
        Element commandElement = document.createElement(getCommandTagName());
        Element reportElement = document.createElement(getReportTagName());
        int commands = 0;
        int reports = 0;

        for (final Operation operation : operations) {
            Element operationElement = document.createElement(operation.getOperationType().getName());
            operationElement.setAttribute("OPTypeRef", operation.getOperationType().getName());

            String parameterPosition = operation.getParameterPosition();
            if (parameterPosition != null) {
                Element paramPosElement = XmlUtil.createTextElement(document, "ParamPos", parameterPosition);
                operationElement.appendChild(paramPosElement);
            }

            if (operation.getOperationType().isInput()) {
                commandElement.appendChild(operationElement);
                commands++;
            } else {
                reportElement.appendChild(operationElement);
                reports++;
            }
        }

        if (commands > 0) {
            paramOpTypeElement.appendChild(commandElement);
        }
        if (reports > 0) {
            paramOpTypeElement.appendChild(reportElement);
        }
    }

    protected void populateParamTypeElement(Document document, Element paramTypeElement, MostType type) {
        Element mostTypeElement = type.generateXmlElement(document);
        paramTypeElement.appendChild(mostTypeElement);
    }
}
