package com.silentmatt.dss;

import com.silentmatt.dss.css.CssSelector;
import com.silentmatt.dss.css.CssSimpleSelector;
import com.silentmatt.dss.util.JoinedList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A CSS selector.
 *
 * A selector is made up of one or more {@link SimpleSelector}s, separated by
 * {@link Combinator}s, like {@link Combinator#ChildOf} ("&gt;"),
 * {@link Combinator#PrecededBy} ("+"), etc.
 * 
 * @author Matthew Crumley
 */
@Immutable
public final class Selector {
    public static class Builder {
        private final List<SimpleSelector> simpleSelectors;

        public Builder() {
            simpleSelectors = new ArrayList<SimpleSelector>();
        }
        
        public Builder addSimpleSelector(SimpleSelector ss) {
            simpleSelectors.add(ss);
            return this;
        }
        
        public Selector build() {
            return new Selector(simpleSelectors);
        }
    }

    private final List<SimpleSelector> simpleSelectors;
    private final Combinator combinator;
    private final Selector parents;
    private final Selector children;

    /**
     * Evaluate the Selector.
     *
     * This just converts the Selector to an equivalent {@link CssSelector}, since
     * there are currently no special features for DSS selectors.
     *
     * @return A {@link CssSelector} that corresponds to this Selector.
     */
    public CssSelector evaluate() {
        CssSelector result = new CssSelector();

        CssSimpleSelector previous = null;
        for (SimpleSelector ss : getActualSimpleSelectors()) {
            if (ss.getCombinator() == Combinator.None && previous != null) {
                // TODO: It might be better to do this in JoinedSelectorList, or we could remove...
                // [Css]SimpleSelector#child altogether, and just use Combinator.None for that.
                CssSimpleSelector end = previous;
                while (end.getChild() != null) {
                    end = end.getChild();
                }
                end.setChild(ss.evaluate());
            }
            else {
                previous = ss.evaluate();
                result.getSimpleSelectors().add(previous);
            }
        }

        return result;
    }

    /**
     * Constructs a Selector from a list of SimpleSelectors.
     *
     * @param simpleSelectors The list of SimpleSelectors.
     */
    public Selector(List<SimpleSelector> simpleSelectors) {
        this.simpleSelectors = Collections.unmodifiableList(simpleSelectors);
        parents = children = null;
        combinator = Combinator.Descendant;
    }

    /**
     * Constructs a Selector by joining two Selectors with a Combinator.
     *
     * @param parent The first part of the selector.
     * @param cb The {@link Combinator} to combine the two parts with.
     * @param child The second part of the selector.
     */
    public Selector(Selector parent, Combinator cb, Selector child) {
        parents = parent;
        children = child;
        simpleSelectors = new JoinedList<SimpleSelector>(parent.getSimpleSelectors(), child.getSimpleSelectors());
        combinator = cb;
    }

    /**
     * Gets the list of SimpleSelectors that makes up this Selector.
     *
     * @return A {@link List} of {@link SimpleSelector}s.
     */
    public List<SimpleSelector> getSimpleSelectors() {
        return simpleSelectors;
    }

    private List<SimpleSelector> getActualSimpleSelectors() {
        List<SimpleSelector> result = new ArrayList<SimpleSelector>();

        if (parents != null) {
            for (SimpleSelector ss : parents.getActualSimpleSelectors()) {
                result.add(ss);
            }

            boolean first = true;
            for (SimpleSelector ss : children.getSimpleSelectors()) {
                if (first) {
                    result.add(ss.withCombinator(combinator));
                }
                else {
                    result.add(ss);
                }
                first = false;
            }
        }
        else {
            for (SimpleSelector ss : simpleSelectors) {
               result.add(ss);
            }
        }

        return Collections.unmodifiableList(result);
    }

    @Override
    public String toString() {
        StringBuilder txt = new StringBuilder();
        boolean first = true;

        if (parents != null) {
            txt.append(parents);

            if (combinator != Combinator.Descendant) {
                txt.append(' ').append(combinator);
            }
            txt.append(' ');

            txt.append(children);
        }
        else {
            for (SimpleSelector ss : simpleSelectors) {
                if (first) { first = false; } else { txt.append(" "); }
                txt.append(ss);
            }
        }
        return txt.toString();
    }

    /**
     * Generates a selector list as a string from a collection of Selectors.
     *
     * @param selectors An {@link Iterable} collection of Selectors.
     *
     * @return A CSS selector list, i.e. the selectors separated by commas.
     */
    public static String join(Iterable<Selector> selectors) {
        boolean first = true;
        StringBuilder sb = new StringBuilder();
        for (Selector sel : selectors) {
            if (first) {
                first = false;
            }
            else {
                sb.append(", ");
            }
            sb.append(sel.toString());
        }
        return sb.toString();
    }
}
