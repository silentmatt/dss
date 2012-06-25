package com.silentmatt.dss.directive;

import com.silentmatt.dss.EvaluationState;
import com.silentmatt.dss.Immutable;
import com.silentmatt.dss.MediaQuery;
import com.silentmatt.dss.Rule;
import com.silentmatt.dss.css.CssMediaDirective;
import com.silentmatt.dss.css.CssMediaQuery;
import com.silentmatt.dss.css.CssRule;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Matthew Crumley
 */
@Immutable
public final class MediaDirective extends Rule {
    private final List<MediaQuery> mediums;
    private final List<Rule> rules;

    public MediaDirective(List<MediaQuery> mediums, List<Rule> rules) {
        super();
        this.mediums = Collections.unmodifiableList(mediums);
        this.rules = Collections.unmodifiableList(rules);
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

    @Override
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
