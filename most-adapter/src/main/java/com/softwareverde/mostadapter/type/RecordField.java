package com.softwareverde.mostadapter.type;

import com.softwareverde.mostadapter.XmlNode;
import com.softwareverde.mostadapter.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class RecordField implements XmlNode {
    private String _name;
    private String _index;
    private String _description;
    private MostType _type;

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public String getIndex() {
        return _index;
    }

    public void setIndex(String index) {
        _index = index;
    }

    public String getDescription() {
        return _description;
    }

    public void setDescription(String description) {
        _description = description;
    }

    public MostType getType() {
        return _type;
    }

    public void setType(MostType type) {
        _type = type;
    }

    @Override
    public Element generateXmlElement(Document document) {
        Element recordFieldElement = document.createElement("TRecordField");
        recordFieldElement.setAttribute("FieldIdx", _index);

        Element nameElement = XmlUtil.createTextElement(document, "TRecordFieldName", _name);
        Element descriptionElement = XmlUtil.createTextElement(document, "TRecordFieldDesc", _description);
        Element typeElement = _type.generateXmlElement(document);

        recordFieldElement.appendChild(nameElement);
        recordFieldElement.appendChild(descriptionElement);
        recordFieldElement.appendChild(typeElement);

        return recordFieldElement;
    }
}
