package com.silentmatt.dss.term;

/**
 *
 * @author matt
 */
public class StringTerm extends Term {
    private String value;

    public StringTerm(String value) {
        super();
        this.value = value;
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
