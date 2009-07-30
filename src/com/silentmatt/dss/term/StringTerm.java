package com.silentmatt.dss.term;

import com.silentmatt.dss.Color;

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
        if (isColor()) {
            return toColor().toString();
        }
        return value;
    }

    @Override
    public boolean isColor() {
        return toColor() != null;
    }

    @Override
    public Color toColor() {
        try {
            return Color.parse(value);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
