package com.silentmatt.dss.css;

import com.silentmatt.dss.selector.Combinator;

/**
 * Specifies the type relationship between {@link SimpleSelector}s (child, sibling, etc.).
 * The Combinator is attached to the right-hand selector.
 *
 * @author Matthew Crumley
 */
public enum CssCombinator {
    /**
     * Special combinator that squeezes the two simple selectors into one.
     */
    None("", ""),

    /**
     * Descendant selector: parent child
     */
    Descendant(" ", " "),

    /**
     * Child selector (immediate descendent): parent > child
     */
    ChildOf(" > ", ">"),

    /**
     * Immediate sibling: element + next
     */
    PrecededImmediatelyBy(" + ", "+"),

    /**
     * Sibling: element ~ later
     */
    PrecededBy(" ~ ", "~");

    private final String cssText;
    private final String compactCssText;

    private CssCombinator(String cssText, String compactCssText) {
        this.cssText = cssText;
        this.compactCssText = compactCssText;
    }

    @Override
    public String toString() {
        return this.cssText;
    }

    public String toCompactString() {
        return this.compactCssText;
    }

    public static CssCombinator fromDss(Combinator c) {
        switch (c) {
        case None:
            return None;
        case Descendant:
            return Descendant;
        case ChildOf:
            return ChildOf;
        case PrecededBy:
            return PrecededBy;
        case PrecededImmediatelyBy:
            return PrecededImmediatelyBy;
        default:
            return null;
        }
    }
}
