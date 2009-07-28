package com.silentmatt.dss.term;

import com.silentmatt.dss.Expression;
import com.silentmatt.dss.Scope;
import com.silentmatt.dss.parser.ErrorReporter;

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
     * Gets the param term as a String.
     *
     * @return A String of the form "{const,param}(name)".
     */
    @Override
    public abstract String toString();

    public abstract Expression evaluate(Scope<Expression> constants, Scope<Expression> parameters, ErrorReporter errors);
}
