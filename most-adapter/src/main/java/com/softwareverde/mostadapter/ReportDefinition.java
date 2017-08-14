package com.softwareverde.mostadapter;

import com.softwareverde.util.Util;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class ReportDefinition implements XmlNode {
    private final String _reportId;
    private final String _reportOperationType;
    private final String _reportName;
    private final String _reportDescription;

    abstract protected String _getTagName();
    abstract protected String _getIdAttributeName();

    public ReportDefinition(final String id, final String operationType, final String name, final String description) {
        _reportId = id;
        _reportOperationType = operationType;
        _reportName = name;
        _reportDescription = description;
    }

    @Override
    public Element generateXmlElement(final Document document) {
        final String tagName = _getTagName();
        final String idAttributeName = _getIdAttributeName();

        final Element reportDefinitionElement = document.createElement(tagName);
        reportDefinitionElement.setAttribute(idAttributeName, Util.coalesce(_reportId));

        final Element reportDefinitionNameElement = XmlUtil.createTextElement(document, "ReportDefName", Util.coalesce(_reportName));
        reportDefinitionElement.appendChild(reportDefinitionNameElement);

        final Element reportDefinitionDescriptionElement = XmlUtil.createTextElement(document, "ReportDefDesc", Util.coalesce(_reportDescription));
        reportDefinitionElement.appendChild(reportDefinitionDescriptionElement);

        final Element reportDefinitionOperationTypeElement = XmlUtil.createTextElement(document, "ReportDefOPType", Util.coalesce(_reportOperationType));
        reportDefinitionElement.appendChild(reportDefinitionOperationTypeElement);

        return reportDefinitionElement;
    }
}
