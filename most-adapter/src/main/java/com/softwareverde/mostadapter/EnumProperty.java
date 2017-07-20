package com.softwareverde.mostadapter;

public class EnumProperty extends Property {

    @Override
    protected String getFunctionClassRef() {
        return "class_enumeration";
    }

    @Override
    protected String getFunctionClassDescription() {
        return "";
    }

    @Override
    protected String getFunctionClassTagName() {
        return "PEnum";
    }

    @Override
    protected String getTagPrefix() {
        return "PE";
    }
}
