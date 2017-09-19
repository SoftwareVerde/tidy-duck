package com.softwareverde.mostadapter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * <p>Represents a MOST Property (type of Function).</p>
 *
 * <p>Adds a flag for whether the property supports notification.</p>
 */
public abstract class Property extends MostFunction {
    private boolean _supportsNotification;

    public boolean supportsNotification() {
        return _supportsNotification;
    }

    public void setSupportsNotification(boolean supportsNotification) {
        _supportsNotification = supportsNotification;
    }

    @Override
    public String getFunctionType() {
        return "Property";
    }

    @Override
    protected void populateFunctionTypeElement(Document document, Element functionTypeElement) {
        super.populateFunctionTypeElement(document, functionTypeElement);

        functionTypeElement.setAttribute("Notification", Boolean.toString(_supportsNotification));
    }
}
