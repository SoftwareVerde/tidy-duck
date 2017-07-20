package com.softwareverde.mostadapter;

public class TextProperty extends Property {

    @Override
    protected String getFunctionClassRef() {
        return "class_text";
    }

    @Override
    protected String getFunctionClassDescription() {
        return "";
    }

    @Override
    protected String getFunctionClassTagName() {
        return "PText";
    }

    @Override
    protected String getTagPrefix() {
        return "PT";
    }
}
