package com.silentmatt.dss.directive;

import com.silentmatt.dss.Medium;
import com.silentmatt.dss.Rule;
import com.silentmatt.dss.RuleSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Matthew Crumley
 */
public class MediaDirective extends Directive {
    private List<Medium> mediums;
    private final List<Rule> allRules;
    private final List<RuleSet> ruleSets;
    private final List<Directive> directives;

    public MediaDirective(List<Medium> mediums, List<Rule> rules) {
        super();
        this.mediums = mediums;
        this.allRules = rules;
        this.ruleSets = new ArrayList<RuleSet>();
        this.directives = new ArrayList<Directive>();
        for (Rule rule : rules) {
            switch (rule.getRuleType()) {
            case Directive:
                directives.add((Directive) rule);
                break;
            case RuleSet:
                ruleSets.add((RuleSet) rule);
                break;
            default:
                throw new IllegalStateException("Unknown directive type: " + rule.getRuleType());
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

    @Override
    public String toString() {
        return toString(0);
    }

    public void addDirective(Directive directive) {
        allRules.add(directive);
        directives.add(directive);
    }

    public void addRuleSet(RuleSet ruleSet) {
        allRules.add(ruleSet);
        ruleSets.add(ruleSet);
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

        for (Rule rule : allRules) {
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

        for (Rule rule : allRules) {
            String ruleString = rule.toCssString(nesting + 1);
            if (ruleString.length() > 0) {
                txt.append(ruleString);
                txt.append("\n");
            }
        }

        txt.append("}");
        return txt.toString();
    }
}
