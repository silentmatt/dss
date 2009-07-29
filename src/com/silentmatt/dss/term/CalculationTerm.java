package com.silentmatt.dss.term;

import com.silentmatt.dss.Color;
import com.silentmatt.dss.expression.CalcExpression;

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
}
