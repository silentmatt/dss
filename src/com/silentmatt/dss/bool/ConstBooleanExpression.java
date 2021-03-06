package com.silentmatt.dss.bool;

import com.silentmatt.dss.Immutable;
import com.silentmatt.dss.evaluator.EvaluationState;

/**
 *
 * @author mcrumley
 */
@Immutable
final class ConstBooleanExpression implements BooleanExpression {
    private final boolean answer;

    ConstBooleanExpression(boolean answer) {
        this.answer = answer;
    }

    @Override
    public Boolean evaluate(EvaluationState state) {
        return answer;
    }

    @Override
    public int getPrecidence() {
        return 4;
    }

    @Override
    public String toString() {
        return Boolean.toString(answer);
    }
}
