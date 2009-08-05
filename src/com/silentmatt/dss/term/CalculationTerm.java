package com.silentmatt.dss.term;

import com.silentmatt.dss.DeclarationList;
import com.silentmatt.dss.EvaluationState;
import com.silentmatt.dss.Expression;
import com.silentmatt.dss.calc.CalcExpression;
import com.silentmatt.dss.calc.CalculationException;
import com.silentmatt.dss.calc.Value;

/**
 *
 * @author matt
 */
public class CalculationTerm extends Term {
    private final CalcExpression calculation;

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
    public Expression substituteValues(EvaluationState state, DeclarationList container, boolean withParams, boolean doCalculations) {
        // XXX: had "withParams ? state.getParameters() : null". Do we need a withParams flag?
        calculation.substituteValues(state, container);

        if (doCalculations) {
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
