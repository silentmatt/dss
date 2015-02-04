package com.silentmatt.dss.calc;

import com.silentmatt.dss.Immutable;
import com.silentmatt.dss.declaration.DeclarationList;
import com.silentmatt.dss.evaluator.EvaluationState;

/**
 * A CalcExpression that negates its operand.
 *
 * @author Matthew Crumley
 */
@Immutable
public class NegationExpression implements CalcExpression {
    private final CalcExpression expr;

    /**
     * Constructs a NegationExpression with the specified operand.
     *
     * @param expr The operand.
     */
    public NegationExpression(CalcExpression expr) {
        this.expr = expr;
    }

    @Override
    public Value calculateValue(EvaluationState state, DeclarationList container) {
        Value value = expr.calculateValue(state, container);
        if (value == null) {
            return null;
        }
        return value.negate();
    }

    @Override
    public NegationExpression withSubstitutedValues(EvaluationState state, DeclarationList container, boolean withParams, boolean doNestedCalculations) {
        return new NegationExpression(expr.withSubstitutedValues(state, container, withParams, doNestedCalculations));
    }

    /**
     * Gets the expression as a string.
     *
     * The resulting String will be parsable to an identical NegationExpression.
     *
     * @return The String representation of this expression.
     */
    @Override
    public String toString() {
        return "-(" + expr + ")";
    }

    @Override
    public int getPrecidence() {
        return 3;
    }
}
