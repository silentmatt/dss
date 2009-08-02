package com.silentmatt.dss.term;

import com.silentmatt.dss.Color;
import com.silentmatt.dss.EvaluationState;
import com.silentmatt.dss.Expression;

/**
 *
 * @author Matthew Crumley
 */
public abstract class Term {
    private Character seperator;

    public Character getSeperator() {
        return seperator;
    }

    public void setSeperator(Character Seperator) {
        this.seperator = Seperator;
    }

    public Expression toExpression() {
        return new Expression(this);
    }

    // XXX: Most Terms are going to return a single Term, but ConstTerm and ParamTerm need a full expression...
    public Expression substituteValues(EvaluationState state, boolean withParams, boolean doCalculations) {
        return toExpression();
    }

    public boolean isColor() {
        return toColor() != null;
    }

    public Color toColor() {
        return null;
    }
}
