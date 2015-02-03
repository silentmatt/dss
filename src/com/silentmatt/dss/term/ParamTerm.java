package com.silentmatt.dss.term;

import com.silentmatt.dss.Immutable;
import com.silentmatt.dss.declaration.DeclarationList;
import com.silentmatt.dss.declaration.Expression;
import com.silentmatt.dss.evaluator.EvaluationState;

/**
 * A parameter reference.
 *
 * @author Matthew Crumley
 */
@Immutable
public final class ParamTerm extends ReferenceTerm {
    public ParamTerm(String name) {
        super(name);
    }

    public ParamTerm(Character sep, String name) {
        super(sep, name);
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
    public Expression evaluate(EvaluationState state, DeclarationList container) {
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
        else if (value.getTerms().size() > 0) {
            Expression.Builder valueb = new Expression.Builder(value);
            valueb.setTerm(0, valueb.getTerm(0).withSeparator(getSeperator()));
            value = valueb.build();
        }

        return value;
    }

    @Override
    public Expression substituteValues(EvaluationState state, DeclarationList container, boolean withParams, boolean doCalculations) {
        return withParams ? evaluate(state, container) : toExpression();
    }

    @Override
    public ParamTerm withSeparator(Character separator) {
        return new ParamTerm(separator, getName());
    }
}
