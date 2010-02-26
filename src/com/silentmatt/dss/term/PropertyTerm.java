package com.silentmatt.dss.term;

import com.silentmatt.dss.DeclarationList;
import com.silentmatt.dss.EvaluationState;
import com.silentmatt.dss.Expression;

/**
 * A property reference.
 *
 * @author Matthew Crumley
 */
public class PropertyTerm extends ReferenceTerm {
    public PropertyTerm(String name) {
        super(name);
    }

    public PropertyTerm clone() {
        PropertyTerm result = new PropertyTerm(getName());
        result.setSeperator(getSeperator());
        return result;
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
        return ret != null ? ret : toExpression();
    }

    @Override
    public Expression substituteValues(EvaluationState state, DeclarationList container, boolean withParams, boolean doCalculations) {
        return evaluate(state, container);
    }
}
