package com.softwareverde.tidyduck.most;

public class StreamCaseSignal {
    private Long _id;
    private String _signalName;
    private String _signalIndex;
    private String _signalDescription;
    private String _signalBitLength;

    public Long getId() {
        return _id;
    }

    public void setId(Long id) {
        _id = id;
    }

    public String getSignalName() {
        return _signalName;
    }

    public void setSignalName(String signalName) {
        _signalName = signalName;
    }

    public String getSignalIndex() {
        return _signalIndex;
    }

    public void setSignalIndex(String signalIndex) {
        _signalIndex = signalIndex;
    }

    public String getSignalDescription() {
        return _signalDescription;
    }

    public void setSignalDescription(String signalDescription) {
        _signalDescription = signalDescription;
    }

    public String getSignalBitLength() {
        return _signalBitLength;
    }

    public void setSignalBitLength(String signalBitLength) {
        _signalBitLength = signalBitLength;
    }
}
