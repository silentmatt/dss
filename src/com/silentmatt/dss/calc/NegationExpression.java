package com.silentmatt.dss.calc;

import com.silentmatt.dss.DeclarationList;
import com.silentmatt.dss.EvaluationState;
import com.silentmatt.dss.Immutable;

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

    public Value calculateValue(EvaluationState state, DeclarationList container) {
        Value value = expr.calculateValue(state, container);
        if (value == null) {
            return null;
        }
        return value.negate();
    }

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

    public int getPrecidence() {
        return 3;
    }
}
