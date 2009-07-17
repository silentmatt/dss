package com.silentmatt.dss;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author matt
 */
public class MediaDirective implements Directive, RuleSetContainer {
    private List<Medium> mediums;
    private List<Rule> allRules;
    private List<RuleSet> ruleSets;
    private List<Directive> directives;

    public MediaDirective(List<Medium> mediums, List<Rule> rules) {
        this.mediums = mediums;
        this.allRules = rules;
        this.ruleSets = new ArrayList<RuleSet>();
        this.directives = new ArrayList<Directive>();
        for (Rule r : rules) {
            switch (r.getRuleType()) {
            case Directive:
                directives.add((Directive) r);
                break;
            case RuleSet:
                ruleSets.add((RuleSet) r);
                break;
            }
        }
    }

    public List<Medium> getMediums() {
        return mediums;
    }

    public void setMediums(List<Medium> mediums) {
        this.mediums = mediums;
    }

    public List<RuleSet> getRuleSets() {
        return Collections.unmodifiableList(ruleSets);
    }

    public List<Directive> getDirectives() {
        return Collections.unmodifiableList(directives);
    }

    public List<Rule> getRules() {
        return allRules;
    }

    public String getName() {
        return "@media";
    }

    public DirectiveType getType() {
        return DirectiveType.Media;
    }

    public RuleType getRuleType() {
        return RuleType.Directive;
    }

    @Override
    public String toString() {
        return toString(0, false);
    }

    public String toCompactString() {
        return toString(0, true);
    }

    public void addDirective(Directive directive) {
        allRules.add(directive);
        directives.add(directive);
    }

    public void addRuleSet(RuleSet ruleSet) {
        allRules.add(ruleSet);
        ruleSets.add(ruleSet);
    }

    public String toString(int nesting, boolean compact) {
        StringBuilder txt = new StringBuilder();
        txt.append("@media");
        if (!compact) { txt.append(" "); }

        boolean first = true;
        for (Medium m : mediums) {
            if (first) {
                first = false;
            } else {
                txt.append(compact ? "," : ", ");
            }
            txt.append(m.toString());
        }
        txt.append(compact ? "{" : " {\r\n");

        for (Rule rule : allRules) {
            txt.append(rule.toString(nesting + 1, compact));
            if (!compact) {
                txt.append("\r\n");
            }
        }

        txt.append("}");
        return txt.toString();
    }
}
