package com.silentmatt.dss.bool;

import com.silentmatt.dss.Immutable;
import com.silentmatt.dss.evaluator.EvaluationState;

/**
 * A BooleanExpression that evaluates to the complement of its operand.
 *
 * @author Matthew Crumley
 */
@Immutable
public final class NotExpression implements BooleanExpression {
    private final BooleanExpression expression;

    /**
     * Constructs a NotExpression from a BooleanExpression.
     *
     * @param expression The {@link BooleanExpression} to complement.
     */
    public NotExpression(BooleanExpression expression) {
        this.expression = expression;
    }

    @Override
    public Boolean evaluate(EvaluationState state) {
        Boolean value = expression.evaluate(state);
        return value == null ? null : !value;
    }

    @Override
    public int getPrecidence() {
        return 3;
    }

    @Override
    public String toString() {
        boolean parens = expression.getPrecidence() < getPrecidence();

        return parens ? ("!(" + expression + ")") : ("!" + expression);
    }
}
