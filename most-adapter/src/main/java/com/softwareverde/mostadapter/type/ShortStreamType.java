package com.softwareverde.mostadapter.type;

import com.softwareverde.mostadapter.type.MostType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ShortStreamType extends MostType {
    private String _maxLength;

    public String getMaxLength() {
        return _maxLength;
    }

    public void setMaxLength(String maxLength) {
        _maxLength = maxLength;
    }

    @Override
    public String getTypeName() {
        return "TShortStream";
    }

    @Override
    protected String getTypeRef() {
        return "type_shortstream";
    }

    @Override
    protected void appendChildElements(Document document, Element typeElement) {
        typeElement.setAttribute("MaxLength", _maxLength);
    }
}
