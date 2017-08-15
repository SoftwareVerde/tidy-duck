package com.softwareverde.mostadapter;

import com.softwareverde.util.Util;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class CommandDefinition implements XmlNode {
    private final String _commandId;
    private final String _commandOperationType;
    private final String _commandName;
    private final String _commandDescription;

    abstract protected String _getTagName();
    abstract protected String _getIdAttributeName();

    public CommandDefinition(final String id, final String operationType, final String name, final String description) {
        _commandId = id;
        _commandOperationType = operationType;
        _commandName = name;
        _commandDescription = description;
    }

    @Override
    public Element generateXmlElement(final Document document) {
        final String tagName = _getTagName();
        final String idAttributeName = _getIdAttributeName();

        final Element commandDefinitionElement = document.createElement(tagName);
        commandDefinitionElement.setAttribute(idAttributeName, Util.coalesce(_commandId));

        final Element commandDefinitionNameElement = XmlUtil.createTextElement(document, "CmdDefName", Util.coalesce(_commandName));
        commandDefinitionElement.appendChild(commandDefinitionNameElement);

        final Element commandDefinitionDescriptionElement = XmlUtil.createTextElement(document, "CmdDefDesc", Util.coalesce(_commandDescription));
        commandDefinitionElement.appendChild(commandDefinitionDescriptionElement);

        final Element commandDefinitionOperationTypeElement = XmlUtil.createTextElement(document, "CmdDefOPType", Util.coalesce(_commandOperationType));
        commandDefinitionElement.appendChild(commandDefinitionOperationTypeElement);

        return commandDefinitionElement;
    }
}
