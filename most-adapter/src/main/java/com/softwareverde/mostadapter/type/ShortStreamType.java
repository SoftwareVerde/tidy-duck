package com.softwareverde.mostadapter.type;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

public class ShortStreamType extends MostType {
    private String _maxLength;
    private List<StreamCase> _streamCases = new ArrayList<>();

    public String getMaxLength() {
        return _maxLength;
    }

    public void setMaxLength(String maxLength) {
        _maxLength = maxLength;
    }

    public List<StreamCase> getStreamCases() {
        return new ArrayList<>(_streamCases);
    }

    public void addStreamCase(final StreamCase streamCase) {
        _streamCases.add(streamCase);
    }

    public void setStreamCases(final List<StreamCase> streamCases) {
        _streamCases = new ArrayList<>(streamCases);
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

        for (final StreamCase streamCase : _streamCases) {
            Element streamCaseElement = streamCase.generateXmlElement(document);
            typeElement.appendChild(streamCaseElement);
        }
    }
}
