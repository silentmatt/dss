package com.silentmatt.dss.term;

import com.silentmatt.dss.DeclarationList;
import com.silentmatt.dss.EvaluationState;
import com.silentmatt.dss.Expression;
import com.silentmatt.dss.Immutable;

/**
 * A parameter reference.
 *
 * @author Matthew Crumley
 */
@Immutable
public final class AtReferenceTerm extends ReferenceTerm {
    public AtReferenceTerm(String name) {
        super(name);
    }

    public AtReferenceTerm(Character sep, String name) {
        super(sep, name);
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
            Expression.Builder valueb = new Expression.Builder(value);
            valueb.getTerms().set(0, valueb.getTerms().get(0).withSeparator(getSeperator()));
            value = valueb.build();
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

    @Override
    public Term withSeparator(Character separator) {
        return new AtReferenceTerm(separator, getName());
    }
}
