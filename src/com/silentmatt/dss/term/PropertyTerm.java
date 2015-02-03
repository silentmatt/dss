package com.silentmatt.dss.term;

import com.silentmatt.dss.Immutable;
import com.silentmatt.dss.declaration.DeclarationList;
import com.silentmatt.dss.declaration.Expression;
import com.silentmatt.dss.evaluator.EvaluationState;

/**
 * A property reference.
 *
 * @author Matthew Crumley
 */
@Immutable
public final class PropertyTerm extends ReferenceTerm {
    public PropertyTerm(String name) {
        super(name);
    }

    public PropertyTerm(Character sep, String name) {
        super(sep, name);
    }

    /**
     * Gets the property reference as a String.
     *
     * @return A String of the form "prop(name)".
     */
    @Override
    public String toString() {
        return "prop(" + getName() + ")";
    }

    @Override
    public Expression evaluate(EvaluationState state, DeclarationList container) {
        if (container == null) {
            state.getErrors().SemErr("property reference not valid in this context.");
            return null;
        }
        Expression ret = container.get(getName());
        if (ret != null && ret.getTerms().size() > 0) {
            Expression.Builder retb = new Expression.Builder(ret);
            retb.setTerm(0, retb.getTerm(0).withSeparator(getSeperator()));
            ret = retb.build();
        }
        return ret != null ? ret : toExpression();
    }

    @Override
    public Expression substituteValues(EvaluationState state, DeclarationList container, boolean withParams, boolean doCalculations) {
        return evaluate(state, container);
    }

    @Override
    public PropertyTerm withSeparator(Character separator) {
        return new PropertyTerm(separator, getName());
    }
}
