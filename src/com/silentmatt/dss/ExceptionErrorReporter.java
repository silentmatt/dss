package com.silentmatt.dss;

/**
 * An {@link ErrorReporter} implementation that throws a {@link RuntimeException}
 * when an error is reported.
 *
 * @author Matthew Crumley
 */
public class ExceptionErrorReporter extends AbstractErrorReporter {
    private int count = 0;

    protected void addError(String msg) {
        count++;
        throw new RuntimeException(msg);
    }

    public int getErrorCount() {
        return count;
    }
}
