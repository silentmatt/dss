package com.silentmatt.dss.term;

import com.silentmatt.dss.Expression;
import com.silentmatt.dss.Scope;
import com.silentmatt.dss.parser.ErrorReporter;

/**
 * A parameter reference.
 *
 * @author Matthew Crumley
 */
public class ParamTerm extends ReferenceTerm {
    public ParamTerm(String name) {
        super(name);
    }

    /**
     * Gets the param term as a String.
     *
     * @return A String of the form "param(name)".
     */
    @Override
    public String toString() {
        return "param(" + getName() + ")";
    }

    @Override
    public Expression evaluate(Scope<Expression> constants, Scope<Expression> parameters, ErrorReporter errors) {
        if (parameters == null) {
            errors.SemErr("param is only valid inside a class");
            return null;
        }
        Expression value = parameters.get(getName());
        if (value == null) {
            if (parameters.containsKey(getName())) {
                errors.SemErr("Missing required class parameter: " + getName());
            }
            else {
                errors.SemErr("Invalid class parameter: " + getName());
            }
        }
        return value;
    }
}
