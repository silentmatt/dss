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

    public int getErrorCount() {
        return errorCount;
    }

    public int getWarningCount() {
        return warningCount;
    }

    public void addError(Message error) {
        errorCount++;
    }

    public void addWarning(Message warning) {
        warningCount++;
    }
}
