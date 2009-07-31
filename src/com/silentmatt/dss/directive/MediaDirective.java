package com.silentmatt.dss.directive;

import com.silentmatt.dss.EvaluationState;
import com.silentmatt.dss.Medium;
import com.silentmatt.dss.Rule;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

/**
 *
 * @author Matthew Crumley
 */
public class MediaDirective extends Rule {
    private List<Medium> mediums;
    private final List<Rule> rules;

    public MediaDirective(List<Medium> mediums, List<Rule> rules) {
        super();
        this.mediums = mediums;
        this.rules = rules;
    }

    public List<Medium> getMediums() {
        return mediums;
    }

    public List<Rule> getRules() {
        return rules;
    }

    @Override
    public String toString() {
        return toString(0);
    }

    public void addRule(Rule rule) {
        this.rules.add(rule);
    }

    public String toString(int nesting) {
        StringBuilder txt = new StringBuilder();
        txt.append("@media ");

        boolean first = true;
        for (Medium m : mediums) {
            if (first) {
                first = false;
            } else {
                txt.append(", ");
            }
            txt.append(m.toString());
        }
        txt.append(" {\n");

        for (Rule rule : rules) {
            txt.append(rule.toString(nesting + 1));
            txt.append("\n");
        }

        txt.append("}");
        return txt.toString();
    }

    public String toCssString(int nesting) {
        StringBuilder txt = new StringBuilder();
        txt.append("@media ");

        boolean first = true;
        for (Medium m : mediums) {
            if (first) {
                first = false;
            } else {
                txt.append(", ");
            }
            txt.append(m.toString());
        }
        txt.append(" {\n");

        for (Rule rule : rules) {
            String ruleString = rule.toCssString(nesting + 1);
            if (ruleString.length() > 0) {
                txt.append(ruleString);
                txt.append("\n");
            }
        }

        txt.append("}");
        return txt.toString();
    }

    @Override
    public void evaluate(EvaluationState state, List<Rule> container) throws MalformedURLException, IOException {
        state.pushScope();
        try {
            Rule.evaluateRules(state, getRules());
        }
        finally {
            state.popScope();
        }
    }
}
