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
    private final List<CssSelector> selectors = new ArrayList<CssSelector>();

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
        return toString(false, nesting);
    }

    public String toString(boolean compact, int nesting) {
        if (declarations.isEmpty()) {
            return "";
        }
        String start = CssRule.getIndent(nesting);

        StringBuilder txt = new StringBuilder();
        boolean first = true;
        for (CssSelector sel : selectors) {
            if (first) {
                first = false;
                if (!compact) {
                    txt.append(start);
                }
            }
            else {
                txt.append(',');
                if (!compact) {
                    txt.append(' ');
                }
            }
            txt.append(sel.toString(compact));
        }

        txt.append(compact ? "{" : " {");

        for (CssRule dir : rules) {
            String dirString = dir.toString(compact, nesting + 1);
            if (dirString.length() > 0) {
                if (!compact) {
                    txt.append("\n\t").append(start);
                }
                txt.append(dirString);
            }
        }

        int count = 0;
        for (CssDeclaration dec : declarations) {
            if (!compact) {
                txt.append("\n\t").append(start);
            }
            txt.append(dec.toString(compact));
            ++count;
            if (!compact || count < declarations.size()) {
                txt.append(";");
            }
        }

        if (!compact) {
            txt.append("\n").append(start);
        }
        txt.append("}");

        return txt.toString();
    }
}
