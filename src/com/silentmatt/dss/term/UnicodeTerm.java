package com.silentmatt.dss.term;

/**
 *
 * @author matt
 */
public class UnicodeTerm extends Term {
    private String value;

    public UnicodeTerm(String value) {
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
        return "U\\" + value.toUpperCase();
    }
}
