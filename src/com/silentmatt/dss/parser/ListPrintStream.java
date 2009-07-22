package com.silentmatt.dss.parser;

import java.io.PrintStream;

/**
 *
 * @author Matthew Crumley
 */
public class ListPrintStream extends PrintStream {
    public ListPrintStream() {
        super(null, true);
    }
}
