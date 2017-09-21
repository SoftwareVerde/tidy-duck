package com.softwareverde.mostadapter.type;

import com.softwareverde.mostadapter.PositionDescription;
import com.softwareverde.mostadapter.XmlNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

public class StreamCase implements XmlNode {
    private PositionDescription _positionDescription;
    private List<StreamParameter> _streamParameters = new ArrayList<>();
    private List<StreamSignal> _streamSignals = new ArrayList<>();

    public PositionDescription getPositionDescription() {
        return _positionDescription;
    }

    public void setPositionDescription(PositionDescription positionDescription) {
        _positionDescription = positionDescription;
    }

    public List<StreamParameter> getStreamParameters() {
        return new ArrayList<>(_streamParameters);
    }

    public void addStreamParameter(final StreamParameter streamParameter) {
        _streamParameters.add(streamParameter);
    }

    public void setStreamParameters(final List<StreamParameter> streamParameters) {
        _streamParameters = new ArrayList<>(streamParameters);
    }

    public List<StreamSignal> getStreamSignals() {
        return new ArrayList<>(_streamSignals);
    }

    public void addStreamSignal(final StreamSignal streamSignal) {
        _streamSignals.add(streamSignal);
    }

    public void setStreamSignals(final List<StreamSignal> streamSignals) {
        _streamSignals = new ArrayList<>(streamSignals);
    }

    @Override
    public Element generateXmlElement(Document document) {
        Element streamCaseElement = document.createElement("StreamCase");

        if (_positionDescription != null) {
            Element positionDescriptionElement = _positionDescription.generateXmlElement(document);
            streamCaseElement.appendChild(positionDescriptionElement);
        }

        for (final StreamParameter streamParameter : _streamParameters) {
            Element streamParameterElement = streamParameter.generateXmlElement(document);
            streamCaseElement.appendChild(streamParameterElement);
        }

        for (final StreamSignal streamSignal : _streamSignals) {
            Element streamSignalElement = streamSignal.generateXmlElement(document);
            streamCaseElement.appendChild(streamSignalElement);
        }

        return streamCaseElement;
    }
}
