package com.silentmatt.dss;

import com.silentmatt.dss.expression.Value;

/**
 * Specifies the type of a {@link Term}.
 *
 * The getters that can be called on a Term object are determined by its TermType.
 *
 * @author Matthew Crumley
 */
public enum TermType {
    /**
     * A number, possibly with an associated unit.
     * @see Value
     */
    Number,

    /**
     * A function reference/"call": rgb(255, 0, 0).
     * @see Function
     */
    Function,

    /**
     * A string literal or a keyword (inherit, bold, etc.).
     */
    String,

    /**
     * A URL: url(http://silentmatt.com/).
     */
    Url,

    /**
     * A Unicode value (U\codepoint).
     *
     * @todo I'm not sure if this works correctly.
     */
    Unicode,

    /**
     * A hexidecimal color: #336699.
     */
    Hex,

    /**
     * A class name with arguments: "inherit: centered&lt;width: 100px&gt;;".
     */
    ClassReference,

    /**
     * A calculated value: calc(const(width) - 2px).
     */
    Calculation
}
