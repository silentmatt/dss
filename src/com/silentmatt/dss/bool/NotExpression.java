package com.silentmatt.dss.bool;

import com.silentmatt.dss.EvaluationState;

/**
 * A BooleanExpression that evaluates to the complement of its operand.
 *
 * @author Matthew Crumley
 */
public class NotExpression implements BooleanExpression {
    private final BooleanExpression expression;

    /**
     * Constructs a NotExpression from a BooleanExpression.
     *
     * @param expression The {@link BooleanExpression} to complement.
     */
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
