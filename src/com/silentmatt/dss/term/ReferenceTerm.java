package com.silentmatt.dss.term;

import com.silentmatt.dss.EvaluationState;
import com.silentmatt.dss.Expression;

/**
 * A constant or parameter reference.
 *
 * @author Matthew Crumley
 */
public abstract class ReferenceTerm extends Term {
    /**
     * The name of the reference.
     */
    private final String name;

    /**
     * Constructor.
     * Sets the name of the reference.
     *
     * @param name The constant/parameter/whatever to reference.
     */
    public ReferenceTerm(String name) {
        super();
        this.name = name;
    }

    /**
     * Gets the name of the value to reference.
     *
     * @return The reference name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the term as a String.
     *
     * @return A String of the form "{const,param}(name)".
     */
    @Override
    public abstract String toString();

    /**
     * Gets the referenced expression.
     *
     * @param state The current evaluation state, containing the current scopes.
     * @return The value of the referenced expression, or null if it doesn't exist.
     */
    public abstract Expression evaluate(EvaluationState state);
}
