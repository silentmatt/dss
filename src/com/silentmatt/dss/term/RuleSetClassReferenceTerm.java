package com.silentmatt.dss.term;

import com.silentmatt.dss.Immutable;
import com.silentmatt.dss.selector.Selector;

/**
 * A term that references a ruleset pseudo-class.
 *
 * Example: {.hidden}
 *
 * @author Matthew Crumley
 */
@Immutable
public final class RuleSetClassReferenceTerm extends ClassReferenceTerm {
    private final Selector selector;

    /**
     * Constructs a ClassReference from a class name.
     *
     * @param name The name of the class to reference.
     *
     * @see #setName(java.lang.String)
     */
    public RuleSetClassReferenceTerm(Selector selector) {
        super(null, "ruleset(" + selector + ")");
        this.selector = selector;
    }

    /**
     * Constructs a ClassReference from a class name.
     *
     * @param sep The separator
     * @param name The name of the class to reference.
     *
     * @see #setName(java.lang.String)
     */
    public RuleSetClassReferenceTerm(Character sep, Selector selector) {
        super(sep, "ruleset(" + selector + ")");
        this.selector = selector;
    }

    /**
     * Gets the DSS representation of the class reference.
     *
     * @return A String that looks like this: "{a selector}"
     */
    @Override
    public String toString() {
        return getName();
    }

    public Selector getSelector() {
        return selector;
    }

    @Override
    public RuleSetClassReferenceTerm withSeparator(Character separator) {
        return new RuleSetClassReferenceTerm(separator, selector);
    }
}
