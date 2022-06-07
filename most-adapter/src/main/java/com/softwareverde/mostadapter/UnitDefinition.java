package com.softwareverde.mostadapter;

import com.softwareverde.util.Util;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class UnitDefinition implements XmlNode {
    private final String _unitId;
    private final String _unitName;
    private final String _unitCode;
    private final String _unitGroup;

    public UnitDefinition(final String id, final String name, final String code, final String group) {
        _unitId = id;
        _unitName = name;
        _unitCode = code;
        _unitGroup = group;
    }

    @Override
    public Element generateXmlElement(final Document document) {
        final Element unitDefinitionElement = document.createElement("UnitDef");
        unitDefinitionElement.setAttribute("UnitID", Util.coalesce(_unitId));

        final Element unitDefinitionNameElement = XmlUtil.createTextElement(document, "UnitDefName", Util.coalesce(_unitName));
        unitDefinitionElement.appendChild(unitDefinitionNameElement);

        final Element unitDefinitionDescriptionElement = XmlUtil.createTextElement(document, "UnitDefCode", Util.coalesce(_unitCode));
        unitDefinitionElement.appendChild(unitDefinitionDescriptionElement);

        final Element unitDefinitionOperationUnitElement = XmlUtil.createTextElement(document, "UnitDefGroup", Util.coalesce(_unitGroup));
        unitDefinitionElement.appendChild(unitDefinitionOperationUnitElement);

        return unitDefinitionElement;
    }
}
