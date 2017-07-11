package com.softwareverde.tidyduck;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Represents a MOST Method (type of Function).</p>
 *
 * <p>Adds input parameters, since methods are callable.</p>
 */
public class Method extends MostFunction {
    private List<MostFunctionParameter> _inputParameters = new ArrayList<>();

    public List<MostFunctionParameter> getInputParameters() {
        return new ArrayList<>(_inputParameters);
    }

    public void addInputParameter(MostFunctionParameter inputParameter) {
        _inputParameters.add(inputParameter);
    }

    @Override
    public Element generateFunctionClassElement(Document document) {
        Element functionClassElement = document.createElement("FunctionClass");
        // TODO: determine appropriate logic
        functionClassElement.setAttribute("ClassRef", "class_unclassified_method");
        Element functionClassDescriptionElement = document.createElement("FunctionClassDesc");
        functionClassElement.appendChild(functionClassDescriptionElement);

        Element propertyElement = document.createElement("Method");
        Element classElement = document.createElement("MUnclassified");

        // TODO: add logic for filling in class element (parameters, etc.)

        propertyElement.appendChild(classElement);
        functionClassElement.appendChild(propertyElement);
        return functionClassElement;
    }
}
