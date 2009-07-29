package com.silentmatt.dss.term;

/**
 *
 * @author matt
 */
public class UnicodeTerm extends Term {
    private String value;

    public UnicodeTerm(String value) {
        super();
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "U\\" + value.toUpperCase();
    }
}
