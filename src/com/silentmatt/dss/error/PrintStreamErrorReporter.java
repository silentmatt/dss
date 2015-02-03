package com.silentmatt.dss.error;

import java.io.PrintStream;

/**
 * An {@link ErrorReporter} implementation that writes error messages to a
 * {@link PrintStream}. If no PrintStream is specified, it defaults to {@link System#err}.
 *
 * @author Matthew Crumley
 */
public class PrintStreamErrorReporter extends AbstractErrorReporter {
    private int errorCount = 0;
    private int warningCount = 0;
    private final java.io.PrintStream errorStream;

    /**
     * Constructs a PrintStreamErrorReporter that prints messages to {@link System#err}.
     */
    public PrintStreamErrorReporter() {
        this(System.err);
    }

    /**
     * Constructs a PrintStreamErrorReporter that prints messages to a specified
     * {@link PrintStream}.
     *
     * @param out The PrintStream to print messages to.
     */
    public PrintStreamErrorReporter(PrintStream out) {
        super();
        errorStream = out;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public int getWarningCount() {
        return warningCount;
    }

    public void addError(Message error) {
        errorStream.println(error);
        errorCount++;
    }

    public void addWarning(Message warning) {
        errorStream.println(warning);
        warningCount++;
    }
}
