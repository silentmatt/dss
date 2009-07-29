package com.silentmatt.dss.term;

/**
 *
 * @author matt
 */
public class HexTerm extends Term {
    private String value;

    public HexTerm(String value) {
        super();
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.toUpperCase();
    }
}
