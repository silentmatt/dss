package com.silentmatt.dss.term;

import com.silentmatt.dss.DeclarationList;
import com.silentmatt.dss.EvaluationState;
import com.silentmatt.dss.Expression;
import com.silentmatt.dss.calc.CalcExpression;
import com.silentmatt.dss.calc.CalculationException;
import com.silentmatt.dss.calc.Value;

/**
 * A "calc(...)" term.
 *
 * @author matt
 */
public class CalculationTerm extends Term {
    /**
     * The expression to evaluate.
     */
    private final CalcExpression calculation;

    /**
     * Constructs a CalculationTerm from an expression.
     *
     * @param calculation The expression to evaluate
     */
    public CalculationTerm(CalcExpression calculation) {
        super();
        this.calculation = calculation;
    }

    public CalculationTerm clone() {
        CalculationTerm result = new CalculationTerm(calculation.clone());
        result.setSeperator(getSeperator());
        return result;
    }

    /**
     * Gets the expression to evaluate.
     *
     * @return The CalcExpression to evaluate
     */
    public CalcExpression getCalculation() {
        return calculation;
    }

    /**
     * Gets the term as a String.
     *
     * @return A String of the form "calc(expression)"
     */
    @Override
    public String toString() {
        return "calc(" + calculation.toString() + ")";
    }

    /**
     * Substitute values in the calculation, and optionally evaluate the result.
     *
     * @param state Current evaluation state
     * @param withParams <code>true</code> if parameters should be substituted
     * @param doCalculations <code>true</code> if the expression should be evaluated
     * @return The resulting calculation, or its result, or <code>null</code> if there was an error
     */
    @Override
    public Expression substituteValues(EvaluationState state, DeclarationList container, boolean withParams, boolean doCalculations) {
        if (doCalculations) {
            // XXX: had "withParams ? state.getParameters() : null". Do we need a withParams flag?
            calculation.substituteValues(state, container, withParams);

            Value calc = calculation.calculateValue(state, container);
            if (calc != null) {
                try {
                    return calc.toTerm().toExpression();
                } catch (CalculationException ex) {
                    state.getErrors().SemErr(ex.getMessage());
                    return null;
                }
            }
        }

        return toExpression();
    }
}
