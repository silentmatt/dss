package com.silentmatt.dss.css;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author matt
 */
public class CssRuleList extends CssRule {
    private final List<CssRule> rules = new ArrayList<CssRule>();

    public CssRuleList() {
    }

    public CssRuleList(List<CssRule> list) {
        rules.addAll(list);
    }

    public void addRule(CssRule r) {
        rules.add(r);
    }

    public List<CssRule> getRules() {
        return rules;
    }

    @Override
    public String toString() {
        return toString(0);
    }

    @Override
    public String toString(int nesting) {
        StringBuilder sb = new StringBuilder();
        for (CssRule r : rules) {
            String toAppend = r.toString(nesting);
            if (toAppend.length() > 0) {
                sb.append('\n').append(toAppend);
            }
        }
        return sb.length() > 0 ? sb.substring(1).toString() : "";
    }

    @Override
    public String toString(boolean compact, int nesting) {
        if (!compact) {
            return toString(nesting);
        }

        StringBuilder sb = new StringBuilder();
        for (CssRule r : rules) {
            String toAppend = r.toString(compact, nesting);
            if (toAppend.length() > 0) {
                sb.append(toAppend);
            }
        }
        return sb.toString();
    }
}
