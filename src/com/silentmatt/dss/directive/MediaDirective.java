package com.silentmatt.dss.directive;

import com.silentmatt.dss.EvaluationState;
import com.silentmatt.dss.MediaQuery;
import com.silentmatt.dss.Rule;
import com.silentmatt.dss.css.CssMediaDirective;
import com.silentmatt.dss.css.CssMediaQuery;
import com.silentmatt.dss.css.CssRule;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Matthew Crumley
 */
public class MediaDirective extends Rule {
    private final List<MediaQuery> mediums;
    private final List<Rule> rules;

    public MediaDirective(List<MediaQuery> mediums, List<Rule> rules) {
        super();
        this.mediums = mediums;
        this.rules = rules;
    }

    public List<MediaQuery> getMediums() {
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
        for (MediaQuery m : mediums) {
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
        state.pushScope(Rule.getRuleSets(getRules()));
        try {
            List<CssMediaQuery> media = new ArrayList<CssMediaQuery>();
            for (MediaQuery m : mediums) {
                media.add(new CssMediaQuery(m.toString()));
            }
            return new CssMediaDirective(media, Rule.evaluateRules(state, getRules()));
        }
        finally {
            state.popScope();
        }
    }
}
