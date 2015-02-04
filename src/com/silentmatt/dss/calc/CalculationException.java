package com.silentmatt.dss.calc;

/**
 * Thrown by a {@link CalcExpression} if there are errors in the expression.
 *
 * @author Matthew Crumley
 */
public class CalculationException extends Exception {
    private static final long serialVersionUID = 198304051234L;

    /**
     * Constructor with an original cause.
     *
     * @param string A description of the error.
     * @param cause The Exception that caused this Exception.
     */
    public CalculationException(String string, Throwable cause) {
        super(string, cause);
    }

    /**
     * Constructor.
     *
     * @param string A description of the error.
     */
    public CalculationException(String string) {
        super(string);
    }
}
