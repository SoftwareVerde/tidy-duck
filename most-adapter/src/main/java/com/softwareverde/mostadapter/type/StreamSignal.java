package com.softwareverde.mostadapter.type;

import com.softwareverde.mostadapter.XmlNode;
import com.softwareverde.mostadapter.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class StreamSignal implements XmlNode {
    private String _name;
    private String _index;
    private String _description;
    private String _bitLength;

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

    public String getBitLength() {
        return _bitLength;
    }

    public void setBitLength(String bitLength) {
        _bitLength = bitLength;
    }

    @Override
    public Element generateXmlElement(Document document) {
        Element streamSignalElement = document.createElement("StreamSignal");

        Element signalNameElement = XmlUtil.createTextElement(document, "SignalName", _name);
        if (_index != null) {
            signalNameElement.setAttribute("SignalIdx", _index);
        }

        Element signalDescriptionElement = XmlUtil.createTextElement(document, "SignalDescription", _description);

        Element signalBitLengthElement = XmlUtil.createTextElement(document, "SignalBitLength", _bitLength);

        streamSignalElement.appendChild(signalNameElement);
        streamSignalElement.appendChild(signalDescriptionElement);
        streamSignalElement.appendChild(signalBitLengthElement);

        return streamSignalElement;
    }
}
