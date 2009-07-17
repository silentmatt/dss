package com.silentmatt.dss;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author matt
 */
public class Selector {
    private List<SimpleSelector> simpleSelectors = new ArrayList<SimpleSelector>();

    public List<SimpleSelector> getSimpleSelectors() {
        return simpleSelectors;
    }

    public void setSimpleSelectors(List<SimpleSelector> SimpleSelectors) {
        this.simpleSelectors = SimpleSelectors;
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