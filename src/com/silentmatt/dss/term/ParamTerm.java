package com.silentmatt.dss.term;

import com.silentmatt.dss.EvaluationState;
import com.silentmatt.dss.Expression;

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
    public Expression evaluate(EvaluationState state) {
        if (state.getParameters() == null) {
            state.getErrors().SemErr("param is only valid inside a class");
            return null;
        }
        Expression value = state.getParameters().get(getName());
        if (value == null) {
            if (state.getParameters().containsKey(getName())) {
                state.getErrors().SemErr("Missing required class parameter: " + getName());
            }
            else {
                state.getErrors().SemErr("Invalid class parameter: " + getName());
            }
        }
        return value;
    }
}
