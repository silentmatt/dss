package com.silentmatt.dss.term;

import com.silentmatt.dss.Immutable;
import java.util.Locale;

/**
 * A Unicode string.
 *
 * @author Matthew Crumley
 */
@Immutable
public final class UnicodeTerm extends Term {
    private final String value;

    /**
     * Constructs a UnicodeTerm from a String.
     * 
     * @param value The value of the string.
     */
    public UnicodeTerm(String value) {
        super(null);
        this.value = value;
    }

    /**
     * Constructs a UnicodeTerm from a String.
     * 
     * @param sep The separator
     * @param value The value of the string.
     */
    public UnicodeTerm(Character sep, String value) {
        super(sep);
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

    @Override
    public UnicodeTerm withSeparator(Character separator) {
        return new UnicodeTerm(separator, value);
    }
}
