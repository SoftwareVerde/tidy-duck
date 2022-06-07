package com.softwareverde.mostadapter.type;

import com.softwareverde.mostadapter.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

        final List<EnumValue> sortedEnumValues = _getSortedEnumValues();
        for (final EnumValue enumValue : sortedEnumValues) {
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

    /**
     * <p>Returns the set of enum values added to this type, sorted by code.</p>
     * @return
     */
    private List<EnumValue> _getSortedEnumValues() {
        final List<EnumValue> enumValuesCopy = new ArrayList<>(_enumValues);
        Collections.sort(enumValuesCopy, new Comparator<EnumValue>() {
            @Override
            public int compare(final EnumValue o1, final EnumValue o2) {
                if (o1.getCode().length() < o2.getCode().length()) {
                    // shorter string, smaller value
                    return -1;
                }
                if (o1.getCode().length() > o2.getCode().length()) {
                    // longer string, larger value
                    return 1;
                }
                return o1.getCode().compareTo(o2.getCode());
            }
        });
        return enumValuesCopy;
    }
}
