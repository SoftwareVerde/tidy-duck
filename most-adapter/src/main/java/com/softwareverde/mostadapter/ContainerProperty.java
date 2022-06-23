package com.softwareverde.mostadapter;

public class ContainerProperty extends Property {
    @Override
    protected String getFunctionClassRef() {
        return "class_container";
    }

    @Override
    protected String getFunctionClassDescription() {
        return "";
    }

    @Override
    protected String getFunctionClassTagName() {
        return "PContainer";
    }

    @Override
    protected String getTagPrefix() {
        return "PC";
    }
}
