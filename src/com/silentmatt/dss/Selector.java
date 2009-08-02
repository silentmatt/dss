package com.silentmatt.dss;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Matthew Crumley
 */
public class Selector {
    private final List<SimpleSelector> simpleSelectors = new ArrayList<SimpleSelector>();

    public List<SimpleSelector> getSimpleSelectors() {
        return simpleSelectors;
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
