package com.softwareverde.mostadapter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

public class ArrayProperty extends Property {
    private List<PositionDescription> _positionDescriptions = new ArrayList<>();

    @Override
    protected void setClassAttributes(Document document, Element trueClassElement) {
        super.setClassAttributes(document, trueClassElement);

        trueClassElement.setAttribute("NMax", getMaxSize());
    }

    @Override
    protected void appendPositionDescriptionElements(Document document, Element functionClassElement) {
        super.appendPositionDescriptionElements(document, functionClassElement);

        for (PositionDescription positionDescription : _positionDescriptions) {
            Element posDescriptionElement = positionDescription.generateXmlElement(document);
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

    public List<PositionDescription> getPositionDescriptions() {
        return new ArrayList<>(_positionDescriptions);
    }

    public void addPropertyPositionDescription(PositionDescription positionDescription) {
        _positionDescriptions.add(positionDescription);
    }

    public void setPositionDescriptions(List<PositionDescription> positionDescriptions) {
        _positionDescriptions = new ArrayList<>(positionDescriptions);
    }
}
