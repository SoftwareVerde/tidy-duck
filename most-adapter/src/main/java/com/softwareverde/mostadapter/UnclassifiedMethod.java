package com.softwareverde.mostadapter;

public class UnclassifiedMethod extends Method {
    @Override
    protected String getFunctionClassRef() {
        return "class_unclassified_method";
    }

    @Override
    protected String getFunctionClassDescription() {
        return "";
    }

    @Override
    protected String getFunctionClassTagName() {
        return "MUnclassified";
    }

    @Override
    protected String getTagPrefix() {
        return "MU";
    }
}
