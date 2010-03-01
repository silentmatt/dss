package com.silentmatt.dss;

import java.io.PrintStream;

/**
 *
 * @author Matthew Crumley
 */
public class PrintStreamErrorReporter extends AbstractErrorReporter {
    private int count = 0;
    private final java.io.PrintStream errorStream;

    public PrintStreamErrorReporter() {
        this(System.err);
    }

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
