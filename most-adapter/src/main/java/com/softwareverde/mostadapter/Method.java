package com.softwareverde.mostadapter;

/**
 * <p>Represents a MOST Method (type of Function).</p>
 *
 * <p>Adds input parameters, since methods are callable.</p>
 */
public abstract class Method extends MostFunction {

    @Override
    public String getFunctionType() {
        return "Method";
    }
}
