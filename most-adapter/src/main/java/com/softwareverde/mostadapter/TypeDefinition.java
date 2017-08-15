package com.softwareverde.mostadapter;

import com.softwareverde.util.Util;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TypeDefinition implements XmlNode {
    private final String _typeId;
    private final String _typeName;
    private final Integer _typeSize;
    private final String _typeDescription;

    public TypeDefinition(final String id, final String name, final Integer typeSize, final String description) {
        _typeId = id;
        _typeName = name;
        _typeSize = typeSize;
        _typeDescription = description;
    }

    @Override
    public Element generateXmlElement(final Document document) {
        final Element typeDefinitionElement = document.createElement("TypeDef");
        typeDefinitionElement.setAttribute("TypeID", Util.coalesce(_typeId));

        final Element typeDefinitionNameElement = XmlUtil.createTextElement(document, "TDefName", Util.coalesce(_typeName));
        typeDefinitionElement.appendChild(typeDefinitionNameElement);

        final Element typeDefinitionDescriptionElement = XmlUtil.createTextElement(document, "TDefDesc", Util.coalesce(_typeDescription));
        typeDefinitionElement.appendChild(typeDefinitionDescriptionElement);

        final Element typeDefinitionOperationTypeElement = XmlUtil.createTextElement(document, "TDefSize", Util.coalesce(_typeSize).toString());
        typeDefinitionElement.appendChild(typeDefinitionOperationTypeElement);

        return typeDefinitionElement;
    }
}
