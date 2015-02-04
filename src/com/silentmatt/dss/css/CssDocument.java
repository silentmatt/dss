package com.silentmatt.dss.css;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Matthew Crumley
 */
public class CssDocument {
    private final List<CssRule> rules = new ArrayList<>();

    public void addRule(CssRule rule) {
        this.rules.add(rule);
    }

    public List<CssRule> getRules() {
        return rules;
    }

    @Override
    public String toString() {
        StringBuilder txt = new StringBuilder();
        for (CssRule r : rules) {
            String ruleString = r.toString(0);
            if (ruleString.length() > 0) {
                txt.append(ruleString).append("\n");
            }
        }
        return txt.toString();
    }

    public String toString(boolean compact) {
        if (compact) {
            StringBuilder txt = new StringBuilder();
            for (CssRule r : rules) {
                String ruleString = r.toString(compact, 0);
                if (ruleString.length() > 0) {
                    txt.append(ruleString);
                }
            }
            return txt.toString();
        }
        else {
            return toString();
        }
    }
}
