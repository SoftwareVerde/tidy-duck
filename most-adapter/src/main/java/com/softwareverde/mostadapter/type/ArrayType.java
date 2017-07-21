package com.softwareverde.mostadapter.type;

import com.softwareverde.mostadapter.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ArrayType extends MostType {
    private String _maxSize;
    private String _name;
    private String _description;
    private MostType _elementType;

    public String getMaxSize() {
        return _maxSize;
    }

    public void setMaxSize(String maxSize) {
        _maxSize = maxSize;
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

    public MostType getElementType() {
        return _elementType;
    }

    public void setElementType(MostType elementType) {
        _elementType = elementType;
    }

    @Override
    public String getTypeName() {
        return "TArray";
    }

    @Override
    protected String getTypeRef() {
        return "type_array";
    }

    @Override
    protected void appendChildElements(Document document, Element typeElement) {
        if (_maxSize != null) {
            typeElement.setAttribute("NMax", _maxSize);
        }

        if (_name != null) {
            Element arrayNameElement = XmlUtil.createTextElement(document, "TArrayName", _name);
            typeElement.appendChild(arrayNameElement);
        }
        if (_description != null) {
            Element arrayDescriptionElement = XmlUtil.createTextElement(document, "TArrayDesc", _description);
            typeElement.appendChild(arrayDescriptionElement);
        }
        Element arrayElementTypeElement = document.createElement("TArrayElementType");
        Element elementTypeElement = _elementType.generateXmlElement(document);
        arrayElementTypeElement.appendChild(elementTypeElement);
        typeElement.appendChild(arrayElementTypeElement);
    }
}
