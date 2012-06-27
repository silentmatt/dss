package com.silentmatt.dss.bool;

import com.silentmatt.dss.EvaluationState;
import com.silentmatt.dss.Immutable;
import com.silentmatt.dss.term.StringTerm;

/**
 * Represents a boolean expression.
 * 
 * @author Matthew Crumley
 */
@Immutable
public interface BooleanExpression {
    /**
     * Evaluate the expression.
     * 
     * @param state The current {@link EvaluationState}.
     * @return A {@link Boolean}, or null if the expression has an error.
     */
    Boolean evaluate(EvaluationState state);

    /**
     * Gets the relative precedence of the expression.
     *
     * @return A positive integer, with higher numbers representing higher precedence.
     * -1 if the expression is invalid (i.e. a {@link BinaryExpression} has an null {@link Operation}).
     */
    int getPrecidence();

    public static final BooleanExpression TRUE = new TermBooleanExpression(new StringTerm("true"));
    public static final BooleanExpression FALSE = new TermBooleanExpression(new StringTerm("false"));
}
