package com.silentmatt.dss.css;

import com.silentmatt.dss.selector.AttributeOperator;

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
    private final String cssText;

    @Override
    public String toString() {
        return cssText;
    }

    public static CssAttributeOperator fromDss(AttributeOperator op) {
        if (op == null) {
            return null;
        }
        switch (op) {
        case BeginsWith:
            return BeginsWith;
        case Contains:
            return Contains;
        case EndsWith:
            return EndsWith;
        case Equals:
            return Equals;
        case Hyphenated:
            return Hyphenated;
        case InList:
            return InList;
        default:
            return null;
        }
    }
}
