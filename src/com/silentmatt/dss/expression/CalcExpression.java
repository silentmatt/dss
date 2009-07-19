package com.silentmatt.dss.expression;

import com.silentmatt.dss.Expression;
import com.silentmatt.dss.Scope;

/**
 *
 * @author matt
 */
public interface CalcExpression {
    Value calculateValue(Scope<Expression> variables, Scope<Expression> parameters) throws CalculationException;
    int getPrecidence();
}
