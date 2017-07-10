package com.softwareverde.tidyduck;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * <p>Represents a MOST Property (type of Function).</p>
 *
 * <p>Adds a flag for whether the property supports notification.</p>
 */
public class Property extends MostFunction {
    private boolean _supportsNotification;

    public boolean supportsNotification() {
        return _supportsNotification;
    }

    public void setSupportsNotification(boolean supportsNotification) {
        _supportsNotification = supportsNotification;
    }

    @Override
    protected Element generateFunctionClassElement(Document document) {
        // TODO: implement
        return null;
    }
}
