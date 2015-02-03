package com.silentmatt.dss.error;

/**
 * An {@link ErrorReporter} implementation that throws a {@link RuntimeException}
 * when an error is reported. Warnings are passed through to another ErrorReporter.
 *
 * @author Matthew Crumley
 */
public class ExceptionErrorReporter extends AbstractErrorReporter {
    private int errorCount = 0;
    private final ErrorReporter warningReporter;

    public ExceptionErrorReporter(ErrorReporter warningReporter) {
        this.warningReporter = warningReporter;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void addError(Message msg) {
        errorCount++;
        throw new RuntimeException(msg.toString());
    }

    public void addWarning(Message msg) {
        warningReporter.addWarning(msg);
    }

    public int getWarningCount() {
        return warningReporter.getWarningCount();
    }
}
