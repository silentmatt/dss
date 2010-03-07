package com.silentmatt.dss.term;

import com.silentmatt.dss.Declaration;
import com.silentmatt.dss.Selector;

/**
 * A term that references a ruleset pseudo-class.
 *
 * Example: {.hidden}
 *
 * @author Matthew Crumley
 */
public class RuleSetClassReferenceTerm extends ClassReferenceTerm {
    private final Selector selector;

    /**
     * Constructs a ClassReference from a class name.
     *
     * @param name The name of the class to reference.
     *
     * @see #setName(java.lang.String)
     */
    public RuleSetClassReferenceTerm(Selector selector) {
        super("ruleset(" + selector + ")");
        this.selector = selector;
    }

    @Override
    public RuleSetClassReferenceTerm clone() {
        RuleSetClassReferenceTerm result = new RuleSetClassReferenceTerm(selector);
        result.setSeperator(getSeperator());
        return result;
    }

    @Override
    public void addArgument(Declaration argument) {
        throw new UnsupportedOperationException("Rule set pseudo-classes do not support arguments.");
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
}
