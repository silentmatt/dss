package com.silentmatt.dss.term;

/**
 * A constant reference.
 *
 * @author Matthew Crumley
 */
public class ConstTerm extends Term {
    /**
     * The const name.
     */
    private String name;

    public ConstTerm(String name) {
        super();
        this.name = name;
    }

    /**
     * Gets the name of the constant to reference.
     *
     * @return The constant name.
     */
    public String getExpression() {
        return name;
    }

    /**
     * Sets the name of the constant to reference.
     *
     * @param name The constant name.
     */
    public void setExpression(String name) {
        this.name = name;
    }

    /**
     * Gets the const term as a String.
     *
     * @return A String of the form "const(name)".
     */
    @Override
    public String toString() {
        return "const(" + name + ")";
    }
}
