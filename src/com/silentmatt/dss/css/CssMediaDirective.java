package com.silentmatt.dss.css;

import java.util.List;

/**
 *
 * @author Matthew Crumley
 */
public class CssMediaDirective extends CssRule {
    private final List<CssMedium> mediums;
    private final List<CssRule> rules;

    public CssMediaDirective(List<CssMedium> mediums, List<CssRule> rules) {
        super();
        this.mediums = mediums;
        this.rules = rules;
    }

    public List<CssMedium> getMediums() {
        return mediums;
    }

    public List<CssRule> getRules() {
        return rules;
    }

    @Override
    public String toString() {
        return toString(0);
    }

    public void addRule(CssRule rule) {
        this.rules.add(rule);
    }

    public String toString(int nesting) {
        String start = CssRule.getIndent(nesting);
        StringBuilder txt = new StringBuilder(start);
        txt.append("@media ");

        boolean first = true;
        for (CssMedium m : mediums) {
            if (first) {
                first = false;
            } else {
                txt.append(", ");
            }
            txt.append(m.toString());
        }
        txt.append(" {\n");

        for (CssRule rule : rules) {
            String ruleString = rule.toString(nesting + 1);
            if (ruleString.length() > 0) {
                txt.append(ruleString);
                txt.append("\n");
            }
        }

        txt.append(start).append("}");
        return txt.toString();
    }

    @Override
    public String toString(boolean compact, int nesting) {
        if (!compact) {
            return toString(nesting);
        }

        StringBuilder txt = new StringBuilder();
        txt.append("@media");

        boolean first = true;
        for (CssMedium m : mediums) {
            if (first) {
                first = false;
                txt.append(' ');
            } else {
                txt.append(',');
            }
            txt.append(m.toString());
        }
        txt.append('{');

        for (CssRule rule : rules) {
            String ruleString = rule.toString(compact, nesting);
            if (ruleString.length() > 0) {
                txt.append(ruleString);
            }
        }

        txt.append('}');
        return txt.toString();
    }
}
