package com.softwareverde.mostadapter;

public class SwitchProperty extends Property {

    @Override
    protected String getFunctionClassRef() {
        return "class_switch";
    }

    @Override
    protected String getFunctionClassDescription() {
        return "";
    }

    @Override
    protected String getFunctionClassTagName() {
        return "PSwitch";
    }

    @Override
    protected String getTagPrefix() {
        return "PS";
    }
}
