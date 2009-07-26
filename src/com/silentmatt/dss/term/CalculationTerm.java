package com.silentmatt.dss.term;

import com.silentmatt.dss.expression.CalcExpression;

/**
 *
 * @author matt
 */
public class CalculationTerm extends Term {
    private CalcExpression calculation;

    public CalculationTerm(CalcExpression calculation) {
        this.calculation = calculation;
    }

    public CalcExpression getCalculation() {
        return calculation;
    }

    public void setCalculation(CalcExpression calculation) {
        this.calculation = calculation;
    }

    @Override
    public String toString() {
        return "calc(" + calculation.toString() + ")";
    }
}
