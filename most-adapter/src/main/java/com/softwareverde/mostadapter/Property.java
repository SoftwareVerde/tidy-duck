package com.softwareverde.mostadapter;

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
    public String getFunctionType() {
        return "Property";
    }

    @Override
    protected Element generateFunctionClassElement(Document document) {
        Element functionClassElement = document.createElement("FunctionClass");
        // TODO: determine appropriate logic
        functionClassElement.setAttribute("ClassRef", "class_unclassified_property");
        Element functionClassDescriptionElement = document.createElement("FunctionClassDesc");
        functionClassElement.appendChild(functionClassDescriptionElement);

        Element propertyElement = document.createElement("Property");
        propertyElement.setAttribute("Notification", Boolean.toString(_supportsNotification));
        Element classElement = document.createElement("PUnclassified");

        // TODO: add logic for filling in class element (parameters, etc.)

        propertyElement.appendChild(classElement);
        functionClassElement.appendChild(propertyElement);
        return functionClassElement;
    }
}
