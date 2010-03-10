package com.silentmatt.dss;

import com.silentmatt.dss.parser.DSSParser;

/**
 * A skeletal implementation of the {@link ErrorReporter} interface.
 *
 * AbstractErrorReporter provides implementations for the convenience methods.
 *
 * @author Matthew Crumley
 */
public abstract class AbstractErrorReporter implements ErrorReporter {
    public void SemErr(int line, int col, String s) {
        addError(new ErrorMessage(new Message.Position(line, col), s));
    }

    public void SemErr(String s) {
        addError(new ErrorMessage(s));
    }

    public void SynErr(int line, int col, int n) {
        String s = DSSParser.getErrorMessage(n);
        addError(new SyntaxErrorMessage(new Message.Position(line, col), s));
    }

    public void Warning(int line, int col, String s) {
        addWarning(new WarningMessage(new Message.Position(line, col), s));
    }

    public void Warning(String s) {
        addWarning(new WarningMessage(s));
    }
}
