package com.silentmatt.dss.error;

import com.silentmatt.dss.parser.DSSParser;

/**
 * Interface for reporting DSS parser and evaluation errors.
 *
 * @author Matthew Crumley
 */
public interface ErrorReporter {
    /**
     * Gets the number of errors reported.
     * 
     * @return The number of errors.
     */
    int getErrorCount();

    /**
     * Gets the number of warnings reported.
     *
     * @return The number of warnings.
     */
    int getWarningCount();

    /**
     * Reports a semantic error at a specified line and column.
     * Semantic errors are for errors in executing the DSS file.
     *
     * @param line The line where the error occurred.
     * @param col The column where the error occurred.
     * @param s The Error message.
     */
    void semanticError(int line, int col, String s);

    /**
     * Reports a semantic error with no location information.
     * Semantic errors are for errors in executing the DSS file.
     * 
     * @param s The Error message.
     */
    void semanticError(String s);

    /**
     * Reports a syntax error at a specified line and column.
     * Syntax errors are for errors in parsing the DSS file. This will be called
     * by the parser.
     *
     * @param line The line where the error occurred.
     * @param col The column where the error occurred.
     * @param n An integer corresponding to the error message. Call
     *          {@link DSSParser#getErrorMessage(int)} to get the error string.
     */
    void syntaxError(int line, int col, int n);

    /**
     * Reports a warning at a specified line and column.
     * Warnings are for issues that do not cause the file to be invalid.
     * 
     * @param line The line where the error occurred.
     * @param col The column where the error occurred.
     * @param s The Warning message.
     */
    void warning(int line, int col, String s);

    /**
     * Reports a warning.
     * Warnings are for issues that do not cause the file to be invalid.
     * 
     * @param s The Warning message.
     */
    void warning(String s);

    /**
     * Report an error.
     *
     * @param error The error message to report.
     */
    void addError(Message error);

    /**
     * Report a warning.
     *
     * @param warning The warning message to report.
     */
    void addWarning(Message warning);
}
