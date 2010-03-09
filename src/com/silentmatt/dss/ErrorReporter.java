package com.silentmatt.dss;

import com.silentmatt.dss.parser.DSSParser;

/**
 * Interface for reporting DSS parser and evaluation errors.
 *
 * @todo This interface will probably change a lot in the future, to replace
 * error strings with an error class and better distinguish between warnings and
 * errors.
 *
 * @author Matthew Crumley
 */
public interface ErrorReporter {
    /**
     * Sets the output format for full error messages. This format is used by the
     * {@link #SemErr(int, int, java.lang.String)}, {@link #SynErr(int, int, int)},
     * and {@link #Warning(int, int, java.lang.String)} methods.
     *
     * @param fmt The format string. It should contain these "tags" that will be
     * replaced with the appropriate values:
     *
     * <ul>
     *   <li>{type} - The type of error, one of "Error", "Syntax Error", or "Warning".</li>
     *   <li>{line} - The line where the error occurred.</li>
     *   <li>{column} - The column where the error occurred.</li>
     *   <li>{message} - The warning/error message.</li>
     * </ul>
     */
    void setMessageFormat(String fmt);

    /**
     * Gets the format string for full error messages.
     * @return the message format string.
     */
    String getMessageFormat();

    /**
     * Sets the output format for short error messages. This format is used by the
     * {@link #SemErr(java.lang.String)} and {@link #Warning(java.lang.String)} methods.
     *
     * @param fmt The format string. It should contain these "tags" that will be
     * replaced with the appropriate values:
     *
     * <ul>
     *   <li>{type} - The type of error, one of "Error", "Syntax Error", or "Warning".</li>
     *   <li>{message} - The warning/error message.</li>
     * </ul>
     */
    void setSimpleMessageFormat(String fmt);

    /**
     * Gets the format string for simple error messages.
     * @return the message format string.
     */
    String getSimpleMessageFormat();

    /**
     * Gets the number of errors and warnings reported.
     * @return The number of errors.
     */
    // TODO: Warnings and errors should really be separated by more than the type string
    int getErrorCount();

    /**
     * Reports a symantic error at a specified line and column.
     * Semantic errors are for errors in executing the DSS file.
     *
     * @param line The line where the error occurred.
     * @param col The column where the error occurred.
     * @param s The Error message.
     */
    void SemErr(int line, int col, String s);

    /**
     * Reports a symantic error with no location information.
     * Semantic errors are for errors in executing the DSS file.
     * 
     * @param s The Error message.
     */
    void SemErr(String s);

    /**
     * Reports a syntax error at a specified line and column.
     * Semantic errors are for errors in parsing the DSS file. This will be called
     * by the parser.
     *
     * @param line The line where the error occurred.
     * @param col The column where the error occurred.
     * @param n An integer corresponding to the error message. Call
     *          {@link DSSParser#getErrorMessage(int)} to get the error string.
     */
    void SynErr(int line, int col, int n);

    /**
     * Reports a warning at a specified line and column.
     * Warnings are for issues that do not cause the file to be invalid.
     * 
     * @param line The line where the error occurred.
     * @param col The column where the error occurred.
     * @param s The Warning message.
     */
    void Warning(int line, int col, String s);

    /**
     * Reports a warning.
     * Warnings are for issues that do not cause the file to be invalid.
     * 
     * @param s The Warning message.
     */
    void Warning(String s);
}
