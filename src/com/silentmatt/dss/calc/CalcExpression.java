package com.silentmatt.dss.calc;

import com.silentmatt.dss.Immutable;
import com.silentmatt.dss.declaration.DeclarationList;
import com.silentmatt.dss.evaluator.EvaluationState;

/**
 * An expression from a calc(...) Term.
 *
 * @author Matthew Crumley
 */
@Immutable
public interface CalcExpression {
    /**
     * Performs the calculation.
     * In the calculation, the <code>variables</code> parameter will be used to
     * substitute 'const()' terms, and the <code>parameters</code> parameter will
     * be used for 'param()' terms.
     *
     * @param state The current evaluation state.
     * @param container The {@link DeclarationList} this expression is contained in.
     * @return The result of the calculation.
     */
    Value calculateValue(EvaluationState state, DeclarationList container);

    /**
     * Gets the relative precedence of the expression.
     *
     * @return A positive integer, with higher numbers representing higher precedence.
     * -1 if the expression is invalid (i.e. a {@link BinaryExpression} has an null {@link Operation}).
     */
    int getPrecidence();

    /**
     * Replaces any const/param values available.
     * Any missing constants/parameters will not be replaced, but will not cause an exception.
     *
     * @todo make this return a new calculation, instead of modifying this one
     *
     * @param state The current evaluation state.
     * @param container The {@link DeclarationList} this expression is contained in.
     * @param withParams Whether param() terms should be substituted.
     * @param doNestedCalculations Whether nested calculations should be evaluated.
     *
     * @return A new CalcExpression with const/param terms inlined.
     */
    CalcExpression withSubstitutedValues(EvaluationState state, DeclarationList container, boolean withParams, boolean doNestedCalculations);
}
