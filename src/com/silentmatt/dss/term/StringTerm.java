package com.silentmatt.dss.term;

import com.silentmatt.dss.Color;

/**
 * A string or keyword term.
 *
 * @author matt
 */
public class StringTerm extends Term {
    /**
     * The String value.
     */
    private String value;

    /**
     * Constructs a StringTerm from a String.
     *
     * @param value The String value
     */
    public StringTerm(String value) {
        super();
        this.value = value;
    }

    /**
     * Gets the string value how it appeared in the DSS text.
     * Keywords will be unquoted; strings will be surrounded by single or double quotes.
     *
     * @return The string value
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the string value.
     *
     * @param Value The String
     */
    public void setValue(String Value) {
        this.value = Value;
    }

    /**
     * Converts the string or keyword into a String.
     *
     * If this term is a keyword, and it is a valid CSS color, it will be output
     * as a color string in an unspecified way. The intent is that it will be the
     * shortest representation of the color.
     *
     * @return This keyword or string.
     */
    @Override
    public String toString() {
        if (isColor()) {
            return toColor().toString();
        }
        return value;
    }

    /**
     * Returns true if this term is a color.
     *
     * @return <code>true</code> if this term is a color keyword.
     */
    @Override
    public boolean isColor() {
        return toColor() != null;
    }

    /**
     * Converts a named color into a Color object.
     *
     * @return The corresponding Color, or <code>null</code> if <code>this</code>
     *         is not a valid named CSS color.
     */
    @Override
    public Color toColor() {
        try {
            return Color.parse(value);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
