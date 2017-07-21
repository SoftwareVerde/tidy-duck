package com.softwareverde.mostadapter.type;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ClassifiedStreamType extends MostType {
    private String _maxLength;
    private String _mediaType;

    public String getMaxLength() {
        return _maxLength;
    }

    public void setMaxLength(String maxLength) {
        _maxLength = maxLength;
    }

    public String getMediaType() {
        return _mediaType;
    }

    public void setMediaType(String mediaType) {
        _mediaType = mediaType;
    }

    @Override
    public String getTypeName() {
        return "TCStream";
    }

    @Override
    protected String getTypeRef() {
        return "type_cstream";
    }

    @Override
    protected void appendChildElements(Document document, Element typeElement) {
        typeElement.setAttribute("MaxLength", _maxLength);
        typeElement.setAttribute("MediaType", _mediaType);
    }
}
