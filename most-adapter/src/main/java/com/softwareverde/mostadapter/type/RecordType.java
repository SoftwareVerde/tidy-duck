package com.softwareverde.mostadapter.type;

import com.softwareverde.mostadapter.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

public class RecordType extends MostType {
    private String _numberOfElements;
    private String _name;
    private String _description;
    private List<RecordField> _recordFields = new ArrayList<>();

    public String getNumberOfElements() {
        return _numberOfElements;
    }

    public void setNumberOfElements(String numberOfElements) {
        _numberOfElements = numberOfElements;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public String getDescription() {
        return _description;
    }

    public void setDescription(String description) {
        _description = description;
    }

    public List<RecordField> getRecordFields() {
        return _recordFields;
    }

    public void setRecordFields(List<RecordField> recordFields) {
        _recordFields = recordFields;
    }

    @Override
    public String getTypeName() {
        return "TRecord";
    }

    @Override
    protected String getTypeRef() {
        return "type_record";
    }

    @Override
    protected void appendChildElements(Document document, Element typeElement) {
        typeElement.setAttribute("NElements", _numberOfElements);

        if (_name != null) {
            Element recordNameElement = XmlUtil.createTextElement(document, "TRecordName", _name);
            typeElement.appendChild(recordNameElement);
        }
        if (_description != null) {
            Element recordDescriptionElement = XmlUtil.createTextElement(document, "TRecordDesc", _description);
            typeElement.appendChild(recordDescriptionElement);
        }
        for (final RecordField recordField : _recordFields) {
            Element recordFieldElement = recordField.generateXmlElement(document);
            typeElement.appendChild(recordFieldElement);
        }
    }
}
