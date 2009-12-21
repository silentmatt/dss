package com.silentmatt.dss.css;

/**
 * Specifies the type of comparison for an {@link Attribute} selector.
 *
 * @author Matthew Crumley
 */
public enum CssAttributeOperator {
    /**
     * Equality comparison: [attr=value]
     */
    Equals("="),

    /**
     * In space-separated list: [attr~=item]
     */
    InList("~="),

    /**
     * In hyphen-separated list: [attr|=item]
     */
    Hyphenated("|="),

    /**
     * Ends with: [attr$=ending]
     */
    EndsWith("$="),

    /**
     * Starts with: [attr^=start]
     */
    BeginsWith("^="),

    /**
     * Contains: [attr*=middle]
     */
    Contains("*=");

    private CssAttributeOperator(String cssText) {
        this.cssText = cssText;
    }
    private String cssText;

    @Override
    public String toString() {
        return cssText;
    }
}
