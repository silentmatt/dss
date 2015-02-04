package com.silentmatt.dss.error;

import com.silentmatt.dss.parser.DSSParser;

/**
 * A skeletal implementation of the {@link ErrorReporter} interface.
 *
 * AbstractErrorReporter provides implementations for the convenience methods.
 *
 * @author Matthew Crumley
 */
public abstract class AbstractErrorReporter implements ErrorReporter {
    @Override
    public void semanticError(int line, int col, String s) {
        addError(new ErrorMessage(new Message.Position(line, col), s));
    }

    @Override
    public void semanticError(String s) {
        addError(new ErrorMessage(s));
    }

    @Override
    public void syntaxError(int line, int col, int n) {
        String s = DSSParser.getErrorMessage(n);
        addError(new SyntaxErrorMessage(new Message.Position(line, col), s));
    }

    @Override
    public void warning(int line, int col, String s) {
        addWarning(new WarningMessage(new Message.Position(line, col), s));
    }

    @Override
    public void warning(String s) {
        addWarning(new WarningMessage(s));
    }
}
