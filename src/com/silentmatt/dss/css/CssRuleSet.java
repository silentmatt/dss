package com.silentmatt.dss.css;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Matthew Crumley
 */
public class CssRuleSet extends CssRule {
    private final List<CssRule> rules = new ArrayList<CssRule>();
    private final List<CssDeclaration> declarations = new ArrayList<CssDeclaration>();
    private List<CssSelector> selectors = new ArrayList<CssSelector>();

    public List<CssDeclaration> getDeclarations() {
        return declarations;
    }

    public void addDeclarations(List<CssDeclaration> declarations) {
        for (CssDeclaration declaration : declarations) {
            this.declarations.add(declaration);
        }
    }

    public List<CssSelector> getSelectors() {
        return selectors;
    }

    public List<CssRule> getRules() {
        return rules;
    }

    public void addRule(CssRule directive) {
        rules.add(directive);
    }

    public CssDeclaration getDeclaration(String name) {
        for (CssDeclaration d : declarations) {
            if (d.getName().equalsIgnoreCase(name)) {
                return d;
            }
        }
        return null;
    }

//    public CssExpression getValue(String name) {
//        return declarations.get(name);
//    }

    @Override
    public String toString() {
        return toString(0);
    }

    public String toString(int nesting) {
        if (declarations.isEmpty()) {
            return "";
        }
        String start = CssRule.getIndent(nesting);

        StringBuilder txt = new StringBuilder();
        boolean first = true;
        for (CssSelector sel : selectors) {
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

        for (CssRule dir : rules) {
            String dirString = dir.toString(nesting + 1);
            if (dirString.length() > 0) {
                txt.append("\n\t" + start);
                txt.append(dirString);
            }
        }
        for (CssDeclaration dec : declarations) {
            txt.append("\n\t" + start);
            txt.append(dec);
            txt.append(";");
        }

        txt.append("\n" + start + "}");

        return txt.toString();
    }
}
