package com.softwareverde.mostadapter.type;

import com.softwareverde.mostadapter.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

public class EnumType extends MostType {
    private String _enumMax;
    private List<EnumValue> _enumValues = new ArrayList<>();

    public String getEnumMax() {
        return _enumMax;
    }

    public void setEnumMax(String enumMax) {
        _enumMax = enumMax;
    }

    public List<EnumValue> getEnumValues() {
        return new ArrayList<>(_enumValues);
    }

    public void addEnumValue(final EnumValue enumValue) {
        _enumValues.add(enumValue);
    }

    public void setEnumValues(List<EnumValue> enumValues) {
        _enumValues = new ArrayList<>(enumValues);
    }

    @Override
    public String getTypeName() {
        return "TEnum";
    }

    @Override
    protected String getTypeRef() {
        return "type_enum";
    }

    @Override
    protected void appendChildElements(Document document, Element typeElement) {
        if (_enumMax != null) {
            typeElement.setAttribute("TEnumMax", _enumMax);
        }

        for (final EnumValue enumValue : _enumValues) {
            String description = enumValue.getDescription();
            if (description == null) {
                description = "";
            }
            Element enumValueElement = XmlUtil.createTextElement(document, "TEnumValue", description);

            enumValueElement.setAttribute("Code", enumValue.getCode());
            enumValueElement.setAttribute("SymbolicName", enumValue.getName());

            typeElement.appendChild(enumValueElement);
        }
    }
}
