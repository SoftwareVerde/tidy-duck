package com.softwareverde.mostadapter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Modification extends XmlNode {
    private String _change;
    private String _reason;

    public String getChange() {
        return _change;
    }

    public void setChange(String change) {
        this._change = change;
    }

    public String getReason() {
        return _reason;
    }

    public void setReason(String reason) {
        this._reason = reason;
    }

    @Override
    public Element generateXmlElement(Document document) {
        Element modification = document.createElement("Modification");

        Element change = super.createTextElement(document, "Change", _change);
        modification.appendChild(change);
        Element reason = super.createTextElement(document, "Reason", _reason);
        modification.appendChild(reason);

        return modification;
    }
}
