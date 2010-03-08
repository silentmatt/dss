package com.silentmatt.dss.term;

import com.silentmatt.dss.DeclarationList;
import com.silentmatt.dss.EvaluationState;
import com.silentmatt.dss.Expression;

/**
 * A parameter reference.
 *
 * @author Matthew Crumley
 */
public class AtReferenceTerm extends ReferenceTerm {
    public AtReferenceTerm(String name) {
        super(name);
    }

    public AtReferenceTerm clone() {
        AtReferenceTerm result = new AtReferenceTerm(getName());
        result.setSeperator(getSeperator());
        return result;
    }

    /**
     * Gets the param term as a String.
     *
     * @return A String of the form "param(name)".
     */
    @Override
    public String toString() {
        return "@" + getName();
    }

    @Override
    public Expression evaluate(EvaluationState state, DeclarationList container) {
        Expression value = null;

        if (state.getParameters() != null && state.getParameters().containsKey(getName())) {
            value = state.getParameters().get(getName());
        }
        else if (state.getVariables() != null) {
            value = state.getVariables().get(getName());
        }
        else {
            state.getErrors().SemErr("Invalid scope");
        }

        if (value != null && value.getTerms().size() > 0) {
            value = value.clone();
            value.getTerms().get(0).setSeperator(getSeperator());
        }
        else {
            value = toExpression();
        }

        return value;
    }

    @Override
    public Expression substituteValues(EvaluationState state, DeclarationList container, boolean withParams, boolean doCalculations) {
        return evaluate(state, container);
    }
}
