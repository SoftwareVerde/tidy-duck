package com.softwareverde.mostadapter;

import com.softwareverde.util.Util;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class PositionDescription implements XmlNode {
    public static final String DEFAULT_POSITION_Y = "0";
    public static final String NULL = "#NULL#";

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
        Element posDescriptionElement = XmlUtil.createTextElement(document, "PosDescription", Util.coalesce(_value));

        String positionX = _nullIfEmpty(_positionX);
        String positionY = _nullIfEmpty(_positionY);

        posDescriptionElement.setAttribute("PosX", positionX);
        posDescriptionElement.setAttribute("PosY", positionY);

        return posDescriptionElement;
    }

    private String _nullIfEmpty(String value) {
        if (value == null || value.trim().isEmpty()) {
            return NULL;
        } else {
            return value;
        }
    }
}
