package com.softwareverde.mostadapter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

public class ArrayProperty extends Property {
    private List<PropertyPositionDescription> _propertyPositionDescriptions = new ArrayList<>();

    @Override
    protected void setClassAttributes(Document document, Element trueClassElement) {
        super.setClassAttributes(document, trueClassElement);

        trueClassElement.setAttribute("NMax", getMaxSize());
    }

    @Override
    protected void appendPositionDescriptionElements(Document document, Element functionClassElement) {
        super.appendPositionDescriptionElements(document, functionClassElement);

        for (PropertyPositionDescription propertyPositionDescription : _propertyPositionDescriptions) {
            Element posDescriptionElement = propertyPositionDescription.generateXmlElement(document);
            functionClassElement.appendChild(posDescriptionElement);
        }
    }

    @Override
    protected String getFunctionClassRef() {
        return "class_array";
    }

    @Override
    protected String getFunctionClassDescription() {
        return "";
    }

    @Override
    protected String getFunctionClassTagName() {
        return "PArray";
    }

    @Override
    protected String getTagPrefix() {
        return "PA";
    }

    public String getMaxSize() {
        return "255";
    }

    public List<PropertyPositionDescription> getPropertyPositionDescriptions() {
        return new ArrayList<>(_propertyPositionDescriptions);
    }

    public void addPropertyPositionDescription(PropertyPositionDescription propertyPositionDescription) {
        _propertyPositionDescriptions.add(propertyPositionDescription);
    }

    public void setPropertyPositionDescriptions(List<PropertyPositionDescription> propertyPositionDescriptions) {
        _propertyPositionDescriptions = new ArrayList<>(propertyPositionDescriptions);
    }
}
