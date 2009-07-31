package com.silentmatt.dss;

/**
 *
 * @author Matthew Crumley
 */
public interface ErrorReporter {
    void setMessageFormat(String fmt);
    String getMessageFormat();

    void setSimpleMessageFormat(String fmt);
    String getSimpleMessageFormat();

    int getErrorCount();

    void SemErr(int line, int col, String s);
    void SemErr(String s);
    void SynErr(int line, int col, int n);
    void Warning(int line, int col, String s);
    void Warning(String s);
}
