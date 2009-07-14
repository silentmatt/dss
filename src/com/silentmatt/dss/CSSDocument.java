package com.silentmatt.dss;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author matt
 */
public class CSSDocument implements RuleSetContainer, DirectiveContainer, Statement {
    private List<RuleSet> ruleSet = new ArrayList<RuleSet>();
    private String charset;
    private List<Directive> directives = new ArrayList<Directive>();

    public List<RuleSet> getRuleSets() {
        return ruleSet;
    }

    public void setRuleSets(List<RuleSet> ruleSet) {
        this.ruleSet = ruleSet;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public List<Directive> getDirectives() {
        return directives;
    }

    public void setDirectives(List<Directive> directives) {
        this.directives = directives;
    }

    @Override
    public String toString() {
        StringBuilder txt = new StringBuilder();
        for (Directive dr : directives) {
            txt.append(dr).append("\r\n");
        }
        if (txt.length() > 0) { txt.append("\r\n"); }
        for (RuleSet rules : getRuleSets()) {
            txt.append(rules).append("\r\n");
        }
        return txt.toString();
    }

    public String toCompactString() {
        StringBuilder txt = new StringBuilder();
        for (Directive dr : directives) {
            txt.append(dr.toCompactString());
        }
        for (RuleSet rules : getRuleSets()) {
            txt.append(rules.toCompactString());
        }
        return txt.toString();
    }
}
