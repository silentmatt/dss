package com.silentmatt.dss.term;

import java.util.Locale;

/**
 * A unicode string.
 *
 * @author matt
 */
public class UnicodeTerm extends Term {
    private final String value;

    /**
     * Constructs a UnicodeTerm from a String.
     * @param value The value of the string.
     */
    public UnicodeTerm(String value) {
        super();
        this.value = value;
    }

    /**
     * Gets the string value.
     * @return The value of this term.
     */
    public String getValue() {
        return value;
    }

    /**
     * Converts this term into a String.
     *
     * @return An upper-case String of the form "U\value"
     */
    @Override
    public String toString() {
        return "U\\" + value.toUpperCase(Locale.ENGLISH);
    }
}
