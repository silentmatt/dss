package com.silentmatt.dss.term;

import com.silentmatt.dss.DeclarationList;
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

    public ConstTerm clone() {
        ConstTerm result = new ConstTerm(getName());
        result.setSeperator(getSeperator());
        return result;
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
    public Expression evaluate(EvaluationState state, DeclarationList container) {
        if (state.getVariables() == null) {
            state.getErrors().SemErr("Invalid scope");
            return null;
        }
        return state.getVariables().get(getName());
    }

    @Override
    public Expression substituteValues(EvaluationState state, DeclarationList container, boolean withParams, boolean doCalculations) {
        return evaluate(state, container);
    }
}
