package com.silentmatt.dss;

import java.util.List;

/**
 *
 * @author matt
 */
public class MediaDirective implements Directive, RuleSetContainer {
    private List<Medium> mediums;
    private List<RuleSet> ruleSets;

    public MediaDirective(List<Medium> mediums, List<RuleSet> ruleSets) {
        this.mediums = mediums;
        this.ruleSets = ruleSets;
    }

    public List<Medium> getMediums() {
        return mediums;
    }

    public void setMediums(List<Medium> mediums) {
        this.mediums = mediums;
    }

    public List<RuleSet> getRuleSets() {
        return ruleSets;
    }

    public void setRuleSets(List<RuleSet> ruleSets) {
        this.ruleSets = ruleSets;
    }

    public String getName() {
        return "@media";
    }

    public DirectiveType getType() {
        return DirectiveType.Media;
    }

    public String toString() {
        return toString(0, false);
    }

    public String toCompactString() {
        return toString(0, true);
    }

    private String toString(int nesting, boolean compact) {
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

        for (RuleSet rules : ruleSets) {
            txt.append(rules.toString(nesting + 1, compact));
            if (!compact) {
                txt.append("\r\n");
            }
        }

        txt.append("}");
        return txt.toString();
    }

}
