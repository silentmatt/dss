/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.silentmatt.dss.term;

/**
 *
 * @author matt
 */
public class StringTerm extends Term {
    private String value;

    public StringTerm(String s) {
        super();
        value = s;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String Value) {
        this.value = Value;
    }

    @Override
    public String toString() {
        return value;
    }
}
