package com.silentmatt.dss.calc;

import com.silentmatt.dss.DeclarationList;
import com.silentmatt.dss.EvaluationState;

/**
 * A CalcExpression that negates its operand.
 *
 * @author Matthew Crumley
 */
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
    public NegationExpression clone() {
        return new NegationExpression(expr);
    }

    public Value calculateValue(EvaluationState state, DeclarationList container) {
        Value value = expr.calculateValue(state, container);
        if (value == null) {
            return null;
        }
        return value.negate();
    }

    public void substituteValues(EvaluationState state, DeclarationList container, boolean withParams) {
        expr.substituteValues(state, container, withParams);
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
