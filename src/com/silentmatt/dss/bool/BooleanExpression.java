package com.silentmatt.dss.bool;

import com.silentmatt.dss.DeclarationList;
import com.silentmatt.dss.EvaluationState;

/**
 *
 * @author matt
 */
public interface BooleanExpression {
    Boolean evaluate(EvaluationState state, DeclarationList container);

    /**
     * Gets the relative precidence of the expression.
     *
     * @return A positive integer, with higher numbers representing higher precidence.
     * -1 if the expression is invalid (i.e. a {@link BinaryExpression} has an null {@link Operation}).
     */
    int getPrecidence();
}
