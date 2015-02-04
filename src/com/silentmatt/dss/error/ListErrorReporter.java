package com.silentmatt.dss.error;

import java.util.LinkedList;
import java.util.List;

/**
 * An {@link ErrorReporter} implementation that appends error messages to a
 * {@link List}. The messages can be retrieved later by calling {@link #getErrors()}.
 *
 * @author Matthew Crumley
 */
public class ListErrorReporter extends AbstractErrorReporter {
    private final List<Message> errors = new LinkedList<>();
    private final List<Message> warnings = new LinkedList<>();

    @Override
    public int getErrorCount() {
        return errors.size();
    }

    @Override
    public int getWarningCount() {
        return warnings.size();
    }

    /**
     * Gets a list of the errors that have occurred so far.
     *
     * @return a {@link List} of error messages.
     */
    public List<Message> getErrors() {
        return errors;
    }

    /**
     * Gets a list of the warnings that have occurred so far.
     *
     * @return a {@link List} of warning messages.
     */
    public List<Message> getWarnings() {
        return errors;
    }

    @Override
    public void addError(Message msg) {
        errors.add(msg);
    }

    @Override
    public void addWarning(Message msg) {
        warnings.add(msg);
    }
}
