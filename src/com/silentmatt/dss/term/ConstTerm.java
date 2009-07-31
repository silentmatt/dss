package com.silentmatt.dss.term;

import com.silentmatt.dss.EvaluationState;
import com.silentmatt.dss.Expression;

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
    public Expression evaluate(EvaluationState state) {
        if (state.getVariables() == null) {
            state.getErrors().SemErr("Invalid scope");
            return null;
        }
        return state.getVariables().get(getName());
    }
}
