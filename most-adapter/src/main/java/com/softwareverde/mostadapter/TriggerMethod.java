package com.softwareverde.mostadapter;

public class TriggerMethod extends Method {
    @Override
    protected String getFunctionClassRef() {
        return "class_trigger";
    }

    @Override
    protected String getFunctionClassDescription() {
        return "";
    }

    @Override
    protected String getFunctionClassTagName() {
        return "MTrigger";
    }

    @Override
    protected String getTagPrefix() {
        return "MT";
    }
}
