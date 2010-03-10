package com.silentmatt.dss;

import com.silentmatt.dss.term.FunctionTerm;

/**
 * A function that performs the substitution for a {@link FunctionTerm}.
 *
 * @author Matthew Crumley
 */
public interface Function {
    /**
     * Applies a FunctionTerm to its argument.
     *
     * @param function The {@link FunctionTerm} to evaluate.
     * @param state The current {@link EvaluationState}.
     *
     * @return The result of the function. If call returns null, the FunctionTerm
     * is not replaced.
     */
    Expression call(FunctionTerm function, EvaluationState state);
}