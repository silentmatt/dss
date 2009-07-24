package com.silentmatt.dss.expression;

/**
 * Thrown by a {@link CalcExpression} if there are errors in the expression.
 *
 * @author Matthew Crumley
 */
public class CalculationException extends Exception {
    private static final long serialVersionUID = 198304051234L;

    /**
     * Constructor.
     *
     * @param string A description of the error.
     */
    public CalculationException(String string) {
        super(string);
    }
}
