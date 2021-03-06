package com.silentmatt.dss.term;

import com.silentmatt.dss.Immutable;
import com.silentmatt.dss.calc.CalcExpression;
import com.silentmatt.dss.calc.CalculationException;
import com.silentmatt.dss.calc.Value;
import com.silentmatt.dss.declaration.DeclarationList;
import com.silentmatt.dss.declaration.Expression;
import com.silentmatt.dss.evaluator.EvaluationState;

/**
 * A "@calc(...)" term.
 *
 * @author Matthew Crumley
 */
@Immutable
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
        super(null);
        this.calculation = calculation;
    }

    /**
     * Constructs a CalculationTerm from an expression.
     *
     * @param sep The separator
     * @param calculation The expression to evaluate
     */
    public CalculationTerm(Character sep, CalcExpression calculation) {
        super(sep);
        this.calculation = calculation;
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
     * @return A String of the form "@calc(expression)"
     */
    @Override
    public String toString() {
        return "@calc(" + calculation.toString() + ")";
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
        // XXX: had "withParams ? state.getParameters() : null". Do we need a withParams flag?
        CalcExpression calcExp = calculation.withSubstitutedValues(state, container, withParams, false);

        if (doCalculations) {
            Value calc = calcExp.calculateValue(state, container);
            if (calc != null) {
                try {
                    return calc.toTerm().toExpression();
                } catch (CalculationException ex) {
                    state.getErrors().semanticError(ex.getMessage());
                    return null;
                }
            }
        }

        return new CalculationTerm(getSeperator(), calcExp).toExpression();
        //return toExpression();
    }

    @Override
    public CalculationTerm withSeparator(Character separator) {
        return new CalculationTerm(separator, calculation);
    }
}
