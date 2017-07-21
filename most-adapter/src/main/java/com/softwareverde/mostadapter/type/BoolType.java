package com.softwareverde.mostadapter.type;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

public class BoolType extends MostType {
    private List<BoolField> _boolFields = new ArrayList<>();

    public List<BoolField> getBoolFields() {
        return new ArrayList<>(_boolFields);
    }

    public void addBoolField(final BoolField boolField) {
        _boolFields.add(boolField);
    }

    public void setBoolFields(List<BoolField> boolFields) {
        _boolFields = new ArrayList<>(boolFields);
    }

    @Override
    public String getTypeName() {
        return "TBool";
    }

    @Override
    protected String getTypeRef() {
        return "type_boolean";
    }

    @Override
    protected void appendChildElements(Document document, Element typeElement) {
        for (final BoolField boolField : _boolFields) {
            Element boolFieldElement = boolField.generateXmlElement(document);
            typeElement.appendChild(boolFieldElement);
        }
    }
}
