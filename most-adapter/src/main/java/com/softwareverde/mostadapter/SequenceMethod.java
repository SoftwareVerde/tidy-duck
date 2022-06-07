package com.softwareverde.mostadapter;

public class SequenceMethod extends Method {
    @Override
    protected String getFunctionClassRef() {
        return "class_sequence_method";
    }

    @Override
    protected String getFunctionClassDescription() {
        return "";
    }

    @Override
    protected String getFunctionClassTagName() {
        return "MSequence";
    }

    @Override
    protected String getTagPrefix() {
        return "MQ";
    }
}
