package com.silentmatt.dss;

import java.util.LinkedList;
import java.util.List;

/**
 * An {@link ErrorReporter} implementation that appends error messages to a
 * {@link List}. The messages can be retrieved later by calling {@link #getErrors()}.
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

    /**
     * Gets a list of the errors that have occurred so far.
     *
     * @return a {@link List} of formatted error/warning Strings.
     */
    public java.util.List<String> getErrors() {
        return errors;
    }
}
