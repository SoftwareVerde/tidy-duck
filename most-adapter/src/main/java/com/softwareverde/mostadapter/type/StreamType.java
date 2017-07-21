package com.softwareverde.mostadapter.type;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

public class StreamType extends MostType {
    private String _length;
    private List<StreamCase> _streamCases = new ArrayList<>();

    public String getLength() {
        return _length;
    }

    public void setLength(String length) {
        _length = length;
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
        return "TStream";
    }

    @Override
    protected String getTypeRef() {
        return "type_stream";
    }

    @Override
    protected void appendChildElements(Document document, Element typeElement) {
        if (_length != null) {
            typeElement.setAttribute("Length", _length);
        }

        for (final StreamCase streamCase : _streamCases) {
            Element streamCaseElement = streamCase.generateXmlElement(document);
            typeElement.appendChild(streamCaseElement);
        }
    }
}
