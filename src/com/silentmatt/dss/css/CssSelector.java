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
        simpleSelectors = new ArrayList<>();
    }

    public List<CssSimpleSelector> getSimpleSelectors() {
        return simpleSelectors;
    }

    @Override
    public String toString() {
        StringBuilder txt = new StringBuilder();

        for (CssSimpleSelector ss : simpleSelectors) {
            txt.append(ss.toString());
        }
        return txt.toString();
    }

    public String toString(boolean compact) {
        if (!compact) {
            return toString();
        }

        StringBuilder txt = new StringBuilder();
        for (CssSimpleSelector ss : simpleSelectors) {
            txt.append(ss.toString(compact));
        }
        return txt.toString();
    }
}
