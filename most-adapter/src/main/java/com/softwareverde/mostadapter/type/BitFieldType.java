package com.softwareverde.mostadapter.type;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

public class BitFieldType extends MostType {
    private String _length = "1";
    private List<BoolField> _boolFields = new ArrayList<>();

    public String getLength() {
        return _length;
    }

    public void setLength(String length) {
        _length = length;
    }

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
        return "TBitField";
    }

    @Override
    protected String getTypeRef() {
        return "type_bitfield";
    }

    @Override
    protected void appendChildElements(Document document, Element typeElement) {
        typeElement.setAttribute("Length", _length);

        for (final BoolField boolField : _boolFields) {
            Element boolFieldElement = boolField.generateXmlElement(document);
            typeElement.appendChild(boolFieldElement);
        }
    }
}
