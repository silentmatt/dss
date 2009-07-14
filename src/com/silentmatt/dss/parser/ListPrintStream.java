package com.silentmatt.dss.parser;

import java.io.PrintStream;

/**
 *
 * @author matt
 */
public class ListPrintStream extends PrintStream {
    public ListPrintStream() {
        super(null, true);
    }
}
