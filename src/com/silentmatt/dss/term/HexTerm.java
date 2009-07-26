package com.silentmatt.dss.term;

/**
 *
 * @author matt
 */
public class HexTerm extends Term {
    private String value;

    public HexTerm(String value) {
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
        return value.toUpperCase();
    }
}
