package com.silentmatt.dss.parser;

/**
 *
 * @author Matthew Crumley
 */
public abstract class AbstractErrorReporter implements ErrorReporter {
    private String errMsgFormat = "-- {type}: line {line} col {column}: {message}";
    private String errSimpleMsgFormat = "{type}: {message}";

    public void setMessageFormat(String fmt) {
        errMsgFormat = fmt;
    }

    public String getMessageFormat() {
        return errMsgFormat;
    }

    public void setSimpleMessageFormat(String fmt) {
        errSimpleMsgFormat = fmt;
    }

    public String getSimpleMessageFormat() {
        return errSimpleMsgFormat;
    }

    public void SemErr(int line, int col, String s) {
        formatError("Error", line, col, s);
    }

    public void SemErr(String s) {
        formatError("Error", s);
    }

    public void SynErr(int line, int col, int n) {
        String s = Parser.getErrorMessage(n);
        formatError("Syntax Error", line, col, s);
    }

    public void Warning(int line, int col, String s) {
        formatError("Warning", line, col, s);
    }

    public void Warning(String s) {
        formatError("Warning", s);
    }

    protected abstract void addError(String msg);

    protected void formatError(String type, String msg) {
        StringBuffer b = new StringBuffer(errSimpleMsgFormat);
        int pos = b.indexOf("{type}");
        if (pos >= 0) {
            b.delete(pos, pos + 6);
            b.insert(pos, type);
        }
        pos = b.indexOf("{message}");
        if (pos >= 0) {
            b.replace(pos, pos + 9, msg);
        }
        addError(b.toString());
    }

    protected void formatError(String type, int line, int column, String msg) {
        StringBuffer b = new StringBuffer(errMsgFormat);
        int pos = b.indexOf("{type}");
        if (pos >= 0) {
            b.delete(pos, pos + 6);
            b.insert(pos, type);
        }
        pos = b.indexOf("{line}");
        if (pos >= 0) {
            b.delete(pos, pos + 6);
            b.insert(pos, line);
        }
        pos = b.indexOf("{column}");
        if (pos >= 0) {
            b.delete(pos, pos + 8);
            b.insert(pos, column);
        }
        pos = b.indexOf("{message}");
        if (pos >= 0) {
            b.replace(pos, pos + 9, msg);
        }
        addError(b.toString());
    }

}
