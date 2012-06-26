package com.silentmatt.dss;

/**
 * An {@link ErrorReporter} implementation that counts, then throws away the error messages.
 *
 * @author Matthew Crumley
 */
public class NullErrorReporter extends AbstractErrorReporter {
    private int errorCount = 0;
    private int warningCount = 0;

    /**
     * Default constructor.
     */
    public NullErrorReporter() {
    }

    @Override
    public int getErrorCount() {
        return errorCount;
    }

    @Override
    public int getWarningCount() {
        return warningCount;
    }

    @Override
    public void addError(Message error) {
        errorCount++;
    }

    @Override
    public void addWarning(Message warning) {
        warningCount++;
    }
}
