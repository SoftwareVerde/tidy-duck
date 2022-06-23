package com.softwareverde.tidyduck.most;

public class BooleanField {
    private Long _id;
    private String _bitPosition;
    private String _trueDescription;
    private String _falseDescription;

    public Long getId() {
        return _id;
    }

    public void setId(Long id) {
        _id = id;
    }

    public String getBitPosition() {
        return _bitPosition;
    }

    public void setBitPosition(String bitPosition) {
        _bitPosition = bitPosition;
    }

    public String getTrueDescription() {
        return _trueDescription;
    }

    public void setTrueDescription(String trueDescription) {
        _trueDescription = trueDescription;
    }

    public String getFalseDescription() {
        return _falseDescription;
    }

    public void setFalseDescription(String falseDescription) {
        _falseDescription = falseDescription;
    }
}
