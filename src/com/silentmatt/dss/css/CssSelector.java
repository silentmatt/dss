package com.silentmatt.dss.css;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Matthew Crumley
 */
public class CssSelector {
    private final List<CssSimpleSelector> simpleSelectors;

    public CssSelector() {
        simpleSelectors = new ArrayList<CssSimpleSelector>();
    }

    public List<CssSimpleSelector> getSimpleSelectors() {
        return simpleSelectors;
    }

    @Override
    public String toString() {
        StringBuilder txt = new StringBuilder();
        boolean first = true;

        for (CssSimpleSelector ss : simpleSelectors) {
            if (first) { first = false; } else { txt.append(" "); }
            txt.append(ss);
        }
        return txt.toString();
    }
}