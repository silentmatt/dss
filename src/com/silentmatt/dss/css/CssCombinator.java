package com.silentmatt.dss.css;

import com.silentmatt.dss.Combinator;

/**
 * Specifies the type relationship between {@link SimpleSelector}s (child, sibling, etc.).
 * The Combinator is attached to the right-hand selector.
 *
 * @author Matthew Crumley
 */
public enum CssCombinator {
    /**
     * Child selector (immediate descendent): parent > child
     */
    ChildOf(">"),

    /**
     * Immediate sibling: element + next
     */
    PrecededImmediatelyBy("+"),

    /**
     * Sibling: element ~ later
     */
    PrecededBy("~");

    private String cssText;

    private CssCombinator(String cssText) {
        this.cssText = cssText;
    }

    @Override
    public String toString() {
        return this.cssText;
    }

    public static CssCombinator fromDss(Combinator c) {
        switch (c) {
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
