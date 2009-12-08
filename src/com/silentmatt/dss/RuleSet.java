package com.silentmatt.dss;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Matthew Crumley
 */
public class RuleSet extends Rule {
    private final List<Rule> rules = new ArrayList<Rule>();
    private final DeclarationList declarations = new DeclarationList();
    private final List<Selector> selectors = new ArrayList<Selector>();
    private final List<RuleSet> nestedRuleSets = new ArrayList<RuleSet>();

    public DeclarationList getDeclarations() {
        return declarations;
    }

    public void addDeclaration(Declaration declaration) {
        declarations.add(declaration);
    }

    public List<Selector> getSelectors() {
        return selectors;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public void addRule(Rule directive) {
        rules.add(directive);
    }

    public Declaration getDeclaration(String name) {
        for (Declaration d : declarations) {
            if (d.getName().equalsIgnoreCase(name)) {
                return d;
            }
        }
        return null;
    }

    public Expression getValue(String name) {
        return declarations.get(name);
    }

    public List<RuleSet> getNestedRuleSets() {
        return nestedRuleSets;
    }

    public void addNestedRuleSet(RuleSet nested) {
        nestedRuleSets.add(nested);
    }

    @Override
    public String toString() {
        return toString(0);
    }

    public String toString(int nesting) {
        String start = Rule.getIndent(nesting);

        StringBuilder txt = new StringBuilder();
        boolean first = true;
        for (Selector sel : selectors) {
            if (first) {
                first = false;
                txt.append(start);
            }
            else {
                txt.append(", ");
            }
            txt.append(sel.toString());
        }
        txt.append(" {");

        for (Rule dir : rules) {
            txt.append("\n\t" + start);
            txt.append(dir.toString(nesting + 1));
        }
        for (Declaration dec : declarations) {
            txt.append("\n\t" + start);
            txt.append(dec.toString());
            txt.append(";");
        }

        txt.append("\n" + start + "}");

        return txt.toString();
    }

    public String toCssString(int nesting) {
        String start = Rule.getIndent(nesting);

        StringBuilder txt = new StringBuilder();
        boolean first = true;
        for (Selector sel : selectors) {
            if (first) {
                first = false;
                txt.append(start);
            }
            else {
                txt.append(", ");
            }
            txt.append(sel.toString());
        }
        txt.append(" {");

        for (Rule dir : rules) {
            String dirString = dir.toCssString(nesting + 1);
            if (dirString.length() > 0) {
                txt.append("\n\t" + start);
                txt.append(dirString);
            }
        }
        for (Declaration dec : declarations) {
            txt.append("\n\t" + start);
            txt.append(dec);
            txt.append(";");
        }

        txt.append("\n" + start + "}");

        for (Selector sel : selectors) {
            for (RuleSet nested : nestedRuleSets) {
                txt.append("\n").append(start);
                txt.append(nested.toString(nesting));
            }
        }

        return txt.toString();
    }

    @Override
    public void evaluate(EvaluationState state, List<Rule> container) throws MalformedURLException, IOException {
        state.pushScope();
        try {
            for (Rule dir : this.getRules()) {
                dir.evaluate(state, null);
            }
            this.getDeclarations().evaluateStyle(state, true);
        }
        finally {
            state.popScope();
        }
    }
}
