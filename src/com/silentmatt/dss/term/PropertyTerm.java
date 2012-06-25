package com.silentmatt.dss.term;

import com.silentmatt.dss.DeclarationList;
import com.silentmatt.dss.EvaluationState;
import com.silentmatt.dss.Expression;
import com.silentmatt.dss.Immutable;

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
            retb.getTerms().set(0, retb.getTerms().get(0).withSeparator(getSeperator()));
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
