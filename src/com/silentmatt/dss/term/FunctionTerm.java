package com.silentmatt.dss.term;

import com.silentmatt.dss.Function;

/**
 *
 * @author matt
 */
public class FunctionTerm extends Term {
    private Function function;

    public FunctionTerm(Function function) {
        super();
        this.function = function;
    }

    public Function getFunction() {
        return function;
    }

    public void setFunction(Function Function) {
        this.function = Function;
    }

    @Override
    public String toString() {
        return function.toString();
    }
}
