package com.softwareverde.mostadapter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class PropertyPositionDescription implements XmlNode {
    public static final String DEFAULT_POSITION_Y = "0";

    private String _value;
    private String _positionX;
    private String _positionY = DEFAULT_POSITION_Y;

    public String getValue() {
        return _value;
    }

    public void setValue(String value) {
        this._value = value;
    }

    public String getPositionX() {
        return _positionX;
    }

    public void setPositionX(String positionX) {
        this._positionX = positionX;
    }

    public String getPositionY() {
        return _positionY;
    }

    public void setPositionY(String positionY) {
        this._positionY = positionY;
    }

    @Override
    public Element generateXmlElement(Document document) {
        Element posDescriptionElement = document.createElement("PosDescription");

        posDescriptionElement.setAttribute("PosX", _positionX);
        posDescriptionElement.setAttribute("PosY", _positionY);

        posDescriptionElement.setTextContent(_value);

        return posDescriptionElement;
    }
}
