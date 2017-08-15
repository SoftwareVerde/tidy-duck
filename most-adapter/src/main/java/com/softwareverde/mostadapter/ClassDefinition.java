package com.softwareverde.mostadapter;

import com.softwareverde.util.Util;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ClassDefinition implements XmlNode {
    private final String _classId;
    private final String _className;
    private final String _classDescription;

    public ClassDefinition(final String id, final String name, final String description) {
        _classId = id;
        _className = name;
        _classDescription = description;
    }

    @Override
    public Element generateXmlElement(final Document document) {
        final Element classDefinitionElement = document.createElement("ClassDef");
        classDefinitionElement.setAttribute("ClassID", Util.coalesce(_classId));

        final Element classDefinitionNameElement = XmlUtil.createTextElement(document, "ClassDefName", Util.coalesce(_className));
        classDefinitionElement.appendChild(classDefinitionNameElement);

        final Element classDefinitionDescription = XmlUtil.createTextElement(document, "ClassDefDesc", Util.coalesce(_classDescription));
        classDefinitionElement.appendChild(classDefinitionDescription);

        return classDefinitionElement;
    }
}
