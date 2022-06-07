package com.softwareverde.tidyduck.most;

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

    public void setInputParameters(final List<MostFunctionParameter> inputParameters) {_inputParameters = new ArrayList<>(inputParameters);}

    @Override
    public String getFunctionType() {
        return "Method";
    }
}
