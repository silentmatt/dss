package com.silentmatt.dss;

import java.io.PrintStream;

/**
 * An {@link ErrorReporter} implementation that writes error messages to a
 * {@link PrintStream}. If no PrintStream is specified, it defaults to {@link System#err}.
 *
 * @author Matthew Crumley
 */
public class PrintStreamErrorReporter extends AbstractErrorReporter {
    private int count = 0;
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

    protected void addError(String msg) {
        errorStream.println(msg);
        count++;
    }

    public int getErrorCount() {
        return count;
    }
}
