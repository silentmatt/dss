package com.silentmatt.dss.bool;

import com.silentmatt.dss.EvaluationState;

/**
 * Represents a boolean expression.
 * 
 * @author Matthew Crumley
 */
public interface BooleanExpression {
    /**
     * Evaluate the expression.
     * 
     * @param state The current {@link EvaluationState}.
     * @return A {@link Boolean}, or null if the expression has an error.
     */
    Boolean evaluate(EvaluationState state);

    /**
     * Gets the relative precidence of the expression.
     *
     * @return A positive integer, with higher numbers representing higher precidence.
     * -1 if the expression is invalid (i.e. a {@link BinaryExpression} has an null {@link Operation}).
     */
    int getPrecidence();
}
