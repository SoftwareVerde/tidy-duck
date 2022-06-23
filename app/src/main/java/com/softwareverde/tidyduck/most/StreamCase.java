package com.softwareverde.tidyduck.most;

import java.util.ArrayList;
import java.util.List;

public class StreamCase {
    private Long _id;
    private String _streamPositionX;
    private String _streamPositionY;
    private List<StreamCaseParameter> _streamCaseParameters = new ArrayList<>();
    private List<StreamCaseSignal> _streamCaseSignals = new ArrayList<>();

    public Long getId() {
        return _id;
    }

    public void setId(Long id) {
        _id = id;
    }

    public String getStreamPositionX() {
        return _streamPositionX;
    }

    public void setStreamPositionX(String streamPositionX) {
        _streamPositionX = streamPositionX;
    }

    public String getStreamPositionY() {
        return _streamPositionY;
    }

    public void setStreamPositionY(String streamPositionY) {
        _streamPositionY = streamPositionY;
    }

    public List<StreamCaseParameter> getStreamCaseParameters() {
        return new ArrayList<>(_streamCaseParameters);
    }

    public void addStreamCaseParameter(final StreamCaseParameter streamCaseParameter) {
        _streamCaseParameters.add(streamCaseParameter);
    }

    public void setStreamCaseParameters(final List<StreamCaseParameter> streamCaseParameters) {
        _streamCaseParameters = new ArrayList<>(streamCaseParameters);
    }

    public List<StreamCaseSignal> getStreamCaseSignals() {
        return new ArrayList<>(_streamCaseSignals);
    }

    public void addStreamCaseSignal(final StreamCaseSignal streamCaseSignal) {
        _streamCaseSignals.add(streamCaseSignal);
    }

    public void setStreamCaseSignals(final List<StreamCaseSignal> streamCaseSignals) {
        _streamCaseSignals = new ArrayList<>(streamCaseSignals);
    }
}
