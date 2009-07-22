package com.silentmatt.dss.parser;

import java.io.PrintStream;

/**
 *
 * @author Matthew Crumley
 */
public class PrintStreamErrorReporter extends AbstractErrorReporter {
    public int count = 0;
    public java.io.PrintStream errorStream;

    public PrintStreamErrorReporter() {
        this(System.err);
    }

    public PrintStreamErrorReporter(PrintStream out) {
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
