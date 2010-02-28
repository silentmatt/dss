package com.silentmatt.dss.directive;

import com.silentmatt.dss.EvaluationState;
import com.silentmatt.dss.Medium;
import com.silentmatt.dss.Rule;
import com.silentmatt.dss.css.CssMediaDirective;
import com.silentmatt.dss.css.CssMedium;
import com.silentmatt.dss.css.CssRule;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Matthew Crumley
 */
public class MediaDirective extends Rule {
    private final List<Medium> mediums;
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
        String start = Rule.getIndent(nesting);
        StringBuilder txt = new StringBuilder(start);
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

        txt.append(start).append("}");
        return txt.toString();
    }

    @Override
    public CssRule evaluate(EvaluationState state, List<Rule> container) throws IOException {
        state.pushScope();
        try {
            List<CssMedium> media = new ArrayList<CssMedium>();
            for (Medium m : mediums) {
                media.add(CssMedium.valueOf(m.toString()));
            }
            return new CssMediaDirective(media, Rule.evaluateRules(state, getRules()));
        }
        finally {
            state.popScope();
        }
    }
}
