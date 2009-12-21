package com.silentmatt.dss;

import com.silentmatt.dss.css.CssCombinator;
import com.silentmatt.dss.css.CssSelector;
import com.silentmatt.dss.css.CssSimpleSelector;
import com.silentmatt.dss.util.JoinedList;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Matthew Crumley
 */
public class Selector {
    private final List<SimpleSelector> simpleSelectors;
    private final Combinator combinator;
    private final Selector parents;
    private final Selector children;

    public CssSelector evaluate() {
        CssSelector result = new CssSelector();

        if (parents != null) {
            for (SimpleSelector ss : parents.getSimpleSelectors()) {
                result.getSimpleSelectors().add(ss.evaluate());
            }

            boolean first = true;
            for (SimpleSelector ss : children.getSimpleSelectors()) {
                if (first && combinator != null) {
                    CssSimpleSelector toAppend = ss.evaluate();
                    toAppend.setCombinator(CssCombinator.fromDss(combinator));
                    result.getSimpleSelectors().add(toAppend);
                }
                first = false;
                result.getSimpleSelectors().add(ss.evaluate());
            }
        }
        else {
            for (SimpleSelector ss : simpleSelectors) {
               result.getSimpleSelectors().add(ss.evaluate());
            }
        }

        return result;
    }

    public Selector() {
        simpleSelectors = new JoinedList<SimpleSelector>(new ArrayList<SimpleSelector>(), new ArrayList<SimpleSelector>());
        parents = null;
        children = null;
        combinator = null;
    }

    public Selector(Selector parent, Combinator cb, Selector child) {
        parents = parent;
        children = child;
        simpleSelectors = new JoinedList<SimpleSelector>(parent.getSimpleSelectors(), child.getSimpleSelectors());
        combinator = cb;
    }

    public List<SimpleSelector> getSimpleSelectors() {
        return simpleSelectors;
    }

    @Override
    public String toString() {
        StringBuilder txt = new StringBuilder();
        boolean first = true;

        if (parents != null) {
            txt.append(parents);

            if (combinator != null) {
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
}
