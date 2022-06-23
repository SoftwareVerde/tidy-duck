package com.softwareverde.mostadapter;

public class NumberProperty extends Property {

    @Override
    protected String getFunctionClassRef() {
        return "class_number";
    }

    @Override
    protected String getFunctionClassDescription() {
        return "";
    }

    @Override
    protected String getFunctionClassTagName() {
        return "PNumber";
    }

    @Override
    protected String getTagPrefix() {
        return "PN";
    }
}
