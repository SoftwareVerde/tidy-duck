package com.softwareverde.mostadapter.type;

import com.softwareverde.mostadapter.XmlNode;
import com.softwareverde.mostadapter.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class BoolField implements XmlNode {
    private String _bitPosition;
    private String _trueDescription;
    private String _falseDescription;

    public String getBitPosition() {
        return _bitPosition;
    }

    public void setBitPosition(String bitPosition) {
        this._bitPosition = bitPosition;
    }

    public String getTrueDescription() {
        return _trueDescription;
    }

    public void setTrueDescription(String trueDescription) {
        this._trueDescription = trueDescription;
    }

    public String getFalseDescription() {
        return _falseDescription;
    }

    public void setFalseDescription(String falseDescription) {
        this._falseDescription = falseDescription;
    }

    @Override
    public Element generateXmlElement(Document document) {
        Element boolFieldElement = document.createElement("TBoolField");

        boolFieldElement.setAttribute("BitPos", _bitPosition);

        Element bitTrueDescElement = XmlUtil.createTextElement(document, "BitTrueDesc", _trueDescription);
        boolFieldElement.appendChild(bitTrueDescElement);

        Element bitFalseDescElement = XmlUtil.createTextElement(document, "BitFalseDesc", _falseDescription);
        boolFieldElement.appendChild(bitFalseDescElement);

        return boolFieldElement;
    }
}
