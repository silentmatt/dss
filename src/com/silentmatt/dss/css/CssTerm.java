package com.silentmatt.dss.css;

/**
 * A CSS term.
 * Terms make up expressions and have an optional separator.
 * For example, in the "font-family" property, font names are separated by commas.
 * The separator is associtated with the term to the right.
 *
 * @author Matthew Crumley
 */
public class CssTerm {
    /**
     * The String value.
     */
    private String value;

    /**
     * Constructs a StringTerm from a String.
     *
     * @param value The String value
     */
    public CssTerm(String value) {
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
     * The separator preceeding this term.
     */
    private Character seperator;

    /**
     * Gets the separator.
     *
     * @return The character separating this term from the previous term, or <code>null</code>.
     */
    public Character getSeperator() {
        return seperator;
    }

    /**
     * Sets the separator.
     *
     * @param Seperator The separator to preceed this term.
     */
    public void setSeperator(Character seperator) {
        this.seperator = seperator;
    }

    /**
     * Converts this term into an expression.
     *
     * @return A new Expression with <code>this</code> as its single term.
     */
    public CssExpression toExpression() {
        return new CssExpression(this);
    }

    /**
     * Converts this term  into a CSS-compatible String.
     *
     * @return The String representation of this term.
     */
    @Override
    public String toString() {
        return value;
    }

    public String toString(boolean compact) {
        return value;
    }
}
