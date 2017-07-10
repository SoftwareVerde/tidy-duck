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
        // TODO: implement
        return null;
    }
}
