package com.softwareverde.mostadapter;

public class SequenceProperty extends Property {
    @Override
    protected String getFunctionClassRef() {
        return "class_sequence_property";
    }

    @Override
    protected String getFunctionClassDescription() {
        return "";
    }

    @Override
    protected String getFunctionClassTagName() {
        return "PSequence";
    }

    @Override
    protected String getTagPrefix() {
        return "PQ";
    }
}
