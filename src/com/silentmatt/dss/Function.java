package com.silentmatt.dss;

import com.silentmatt.dss.term.FunctionTerm;

/**
 *
 * @author Matthew Crumley
 */
public interface Function {
    Expression call(FunctionTerm function);
}