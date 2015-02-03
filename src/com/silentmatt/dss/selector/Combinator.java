package com.silentmatt.dss.selector;

/**
 * Specifies the type relationship between {@link SimpleSelector}s (child, sibling, etc.).
 * The Combinator is attached to the right-hand selector.
 *
 * @author Matthew Crumley
 */
public enum Combinator {
    /**
     * Special combinator that squeezes the two simple selectors into one.
     */
    None(""),

    /**
     * Descendant selector: parent child
     */
    Descendant(" "),

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

    private final String cssText;

    private Combinator(String cssText) {
        this.cssText = cssText;

    }

    @Override
    public String toString() {
        return this.cssText;
    }
}
