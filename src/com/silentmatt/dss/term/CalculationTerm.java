package com.silentmatt.dss.term;

import com.silentmatt.dss.EvaluationState;
import com.silentmatt.dss.Expression;
import com.silentmatt.dss.expression.CalcExpression;
import com.silentmatt.dss.expression.CalculationException;
import com.silentmatt.dss.expression.Value;

/**
 *
 * @author matt
 */
public class CalculationTerm extends Term {
    private CalcExpression calculation;

    public CalculationTerm(CalcExpression calculation) {
        super();
        this.calculation = calculation;
    }

    public CalcExpression getCalculation() {
        return calculation;
    }

    @Override
    public String toString() {
        return "calc(" + calculation.toString() + ")";
    }

    @Override
    public Expression substituteValues(EvaluationState state, boolean withParams, boolean doCalculations) {
        // XXX: had "withParams ? state.getParameters() : null". Do we need a withParams flag?
        calculation.substituteValues(state);

        if (doCalculations) {
            Value calc = calculation.calculateValue(state);
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
