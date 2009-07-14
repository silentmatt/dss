package com.silentmatt.dss.parser;

/**
 *
 * @author matt
 */
public class ListErrorReporter extends AbstractErrorReporter {
    public java.util.List<String> errors = new java.util.LinkedList<String>();

    protected void addError(String msg) {
        errors.add(msg);
    }

    public int getErrorCount() {
        return errors.size();
    }
}
