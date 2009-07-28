package com.silentmatt.dss.parser;

/**
 *
 * @author Matthew Crumley
 */
public class ListErrorReporter extends AbstractErrorReporter {
    private java.util.List<String> errors = new java.util.LinkedList<String>();

    protected void addError(String msg) {
        errors.add(msg);
    }

    public int getErrorCount() {
        return errors.size();
    }

    public java.util.List<String> getErrors() {
        return errors;
    }
}
