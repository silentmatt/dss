package com.silentmatt.dss.term;

import com.silentmatt.dss.Immutable;
import com.silentmatt.dss.declaration.DeclarationList;
import com.silentmatt.dss.declaration.Expression;
import com.silentmatt.dss.evaluator.EvaluationState;

/**
 * A constant reference.
 *
 * @author Matthew Crumley
 */
@Immutable
public final class ConstTerm extends ReferenceTerm {
    public ConstTerm(String name) {
        super(name);
    }

    public ConstTerm(Character sep, String name) {
        super(sep, name);
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
            state.getErrors().semanticError("Invalid scope");
            return null;
        }
        Expression result = state.getVariables().get(getName());
        if (result != null && result.getTerms().size() > 0) {
            Expression.Builder resultb = new Expression.Builder(result);
            resultb.setTerm(0, resultb.getTerm(0).withSeparator(getSeperator()));
            result = resultb.build();
        }
        return result;
    }

    @Override
    public Expression substituteValues(EvaluationState state, DeclarationList container, boolean withParams, boolean doCalculations) {
        return evaluate(state, container);
    }

    @Override
    public ConstTerm withSeparator(Character separator) {
        return new ConstTerm(separator, getName());
    }
}
