package com.silentmatt.dss.bool;

import com.silentmatt.dss.EvaluationState;

/**
 *
 * @author matt
 */
public class NotExpression implements BooleanExpression {
    private final BooleanExpression expression;

    public NotExpression(BooleanExpression expression) {
        this.expression = expression;
    }

    public Boolean evaluate(EvaluationState state) {
        Boolean value = expression.evaluate(state);
        return value == null ? null : !value;
    }

    public int getPrecidence() {
        return 3;
    }

    @Override
    public String toString() {
        boolean parens = expression.getPrecidence() < getPrecidence();

        return parens ? ("!(" + expression + ")") : ("!" + expression);
    }
}
