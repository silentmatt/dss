package com.silentmatt.dss.term;

import com.silentmatt.dss.Immutable;
import com.silentmatt.dss.declaration.DeclarationList;
import com.silentmatt.dss.declaration.Expression;
import com.silentmatt.dss.evaluator.EvaluationState;

/**
 * A constant or parameter reference.
 *
 * @author Matthew Crumley
 */
@Immutable
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
        super(null);
        this.name = name;
    }

    /**
     * Constructor.
     * Sets the name of the reference.
     *
     * @param sep The separator
     * @param name The constant/parameter/whatever to reference.
     */
    public ReferenceTerm(Character sep, String name) {
        super(sep);
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
     * @param container The term's container
     *
     * @return The value of the referenced expression, or null if it doesn't exist.
     */
    public abstract Expression evaluate(EvaluationState state, DeclarationList container);
}
