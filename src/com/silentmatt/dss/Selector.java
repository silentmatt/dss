package com.silentmatt.dss;

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
