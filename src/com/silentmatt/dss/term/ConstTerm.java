package com.silentmatt.dss.term;

import com.silentmatt.dss.Expression;
import com.silentmatt.dss.Scope;
import com.silentmatt.dss.parser.ErrorReporter;

/**
 * A constant reference.
 *
 * @author Matthew Crumley
 */
public class ConstTerm extends ReferenceTerm {
    public ConstTerm(String name) {
        super(name);
    }

    /**
     * Gets the const term as a String.
     *
     * @return A String of the form "const(name)".
     */
    @Override
    public String toString() {
        return "const(" + getName() + ")";
    }

    @Override
    public Expression evaluate(Scope<Expression> constants, Scope<Expression> parameters, ErrorReporter errors) {
        if (constants == null) {
            errors.SemErr("Invalid scope");
            return null;
        }
        return constants.get(getName());
    }
}
