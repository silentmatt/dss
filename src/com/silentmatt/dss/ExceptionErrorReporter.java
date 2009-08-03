package com.silentmatt.dss;

/**
 *
 * @author Matthew Crumley
 */
public class ExceptionErrorReporter extends AbstractErrorReporter {
    private int count = 0;

    public ExceptionErrorReporter() {
    }

    protected void addError(String msg) {
        count++;
        throw new RuntimeException(msg);
    }

    public int getErrorCount() {
        return count;
    }
}
