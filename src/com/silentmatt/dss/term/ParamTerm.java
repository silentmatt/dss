package com.silentmatt.dss.term;

/**
 * A parameter reference.
 *
 * @author Matthew Crumley
 */
public class ParamTerm extends Term {
    /**
     * The parameter name.
     */
    private String name;

    public ParamTerm(String name) {
        super();
        this.name = name;
    }

    /**
     * Gets the name of the parameter to reference.
     *
     * @return The parameter name.
     */
    public String getExpression() {
        return name;
    }

    /**
     * Sets the name of the parameter to reference.
     *
     * @param name The parameter name.
     */
    public void setExpression(String name) {
        this.name = name;
    }

    /**
     * Gets the param term as a String.
     *
     * @return A String of the form "param(name)".
     */
    @Override
    public String toString() {
        return "param(" + name + ")";
    }
}
