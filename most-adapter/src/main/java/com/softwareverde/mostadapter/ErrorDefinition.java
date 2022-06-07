package com.softwareverde.mostadapter;

import com.softwareverde.util.Util;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ErrorDefinition implements XmlNode {
    private final String _errorId;
    private final String _errorCode;
    private final String _errorDescription;
    private final String _info;
    private final String _infoDescription;

    public ErrorDefinition(final String id, final String errorCode, final String errorDescription, final String info, final String infoDescription) {
        _errorId = id;
        _errorCode = errorCode;
        _errorDescription = errorDescription;
        _info = info;
        _infoDescription = infoDescription;
    }

    @Override
    public Element generateXmlElement(final Document document) {
        final Element errorDefinitionElement = document.createElement("ErrorDef");
        errorDefinitionElement.setAttribute("ErrorID", Util.coalesce(_errorId));

        final Element errorDefinitionCodeElement = XmlUtil.createTextElement(document, "ErrorDefCode", Util.coalesce(_errorCode));
        errorDefinitionElement.appendChild(errorDefinitionCodeElement);

        final Element errorDefinitionCodeDescriptionElement = XmlUtil.createTextElement(document, "ErrorDefCodeDesc", Util.coalesce(_errorDescription));
        errorDefinitionElement.appendChild(errorDefinitionCodeDescriptionElement);

        final Element errorDefinitionInfoElement = XmlUtil.createTextElement(document, "ErrorDefInfo", Util.coalesce(_info));
        errorDefinitionElement.appendChild(errorDefinitionInfoElement);

        final Element errorDefinitionInfoDescriptionElement = XmlUtil.createTextElement(document, "ErrorDefInfoDesc", Util.coalesce(_infoDescription));
        errorDefinitionElement.appendChild(errorDefinitionInfoDescriptionElement);

        return errorDefinitionElement;
    }
}
