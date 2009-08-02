package com.silentmatt.dss;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Matthew Crumley
 */
public class ListErrorReporter extends AbstractErrorReporter {
    private final List<String> errors = new LinkedList<String>();

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
