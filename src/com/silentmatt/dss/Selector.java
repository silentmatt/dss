package com.silentmatt.dss;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Matthew Crumley
 */
public class Selector {
    private List<SimpleSelector> simpleSelectors = new ArrayList<SimpleSelector>();

    public Selector() {
    }

    public Selector(Selector parent, Selector child) {
        simpleSelectors.addAll(parent.simpleSelectors);
        simpleSelectors.addAll(child.simpleSelectors);
    }

    public List<SimpleSelector> getSimpleSelectors() {
        return simpleSelectors;
    }

    public void prependSelector(Selector selector) {
        List<SimpleSelector> existingSelectors = simpleSelectors;
        simpleSelectors = new ArrayList<SimpleSelector>(selector.getSimpleSelectors());
        simpleSelectors.addAll(existingSelectors);
    }

    public Selector copy() {
        Selector sel = new Selector();
        sel.simpleSelectors.addAll(simpleSelectors);
        return sel;
    }

    @Override
    public String toString() {
        StringBuilder txt = new StringBuilder();
        boolean first = true;
        for (SimpleSelector ss : simpleSelectors) {
        if (first) { first = false; } else { txt.append(" "); }
            txt.append(ss);
        }
        return txt.toString();
    }
}
