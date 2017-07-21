package com.softwareverde.mostadapter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

public class RecordProperty extends Property {
    private String _numberOfElements;
    private List<PropertyPositionDescription> _propertyPositionDescriptions = new ArrayList<>();

    @Override
    protected void setClassAttributes(Document document, Element trueClassElement) {
        super.setClassAttributes(document, trueClassElement);

        trueClassElement.setAttribute("NElements", _numberOfElements);
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
        return "class_record";
    }

    @Override
    protected String getFunctionClassDescription() {
        return "";
    }

    @Override
    protected String getFunctionClassTagName() {
        return "PRecord";
    }

    @Override
    protected String getTagPrefix() {
        return "PR";
    }

    public String getNumberOfElements() {
        return _numberOfElements;
    }

    public void setNumberOfElements(String nElements) {
        _numberOfElements = nElements;
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
