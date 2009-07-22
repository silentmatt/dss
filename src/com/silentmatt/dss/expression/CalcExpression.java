package com.silentmatt.dss.expression;

import com.silentmatt.dss.Expression;
import com.silentmatt.dss.Scope;

/**
 *
 * @author Matthew Crumley
 */
public interface CalcExpression {
    Value calculateValue(Scope<Expression> variables, Scope<Expression> parameters) throws CalculationException;
    int getPrecidence();
    void substituteValues(Scope<Expression> variables, Scope<Expression> parameters) throws CalculationException;
}
