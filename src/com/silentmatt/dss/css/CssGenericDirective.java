package com.silentmatt.dss.css;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Matthew Crumley
 */
public class CssGenericDirective extends CssRule {
    private final List<CssDeclaration> declarations = new ArrayList<CssDeclaration>();
    private final List<CssRule> rules = new ArrayList<CssRule>();
    private final List<CssMedium> mediums = new ArrayList<CssMedium>();
    private String name;
    private CssExpression expression;

    public void addRule(CssRule rule) {
        this.rules.add(rule);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CssMedium> getMediums() {
        return mediums;
    }

    public void addMedium(CssMedium medium) {
        this.mediums.add(medium);
    }

    public CssExpression getExpression() {
        return expression;
    }

    public void setExpression(CssExpression expression) {
        this.expression = expression;
    }

    public List<CssDeclaration> getDeclarations() {
        return declarations;
    }

    public void addDeclaration(CssDeclaration declaration) {
        this.declarations.add(declaration);
    }

    @Override
    public String toString() {
        return toString(0);
    }

    public String toString(int nesting) {
        String start = CssRule.getIndent(nesting);

        StringBuilder txt = new StringBuilder();

        txt.append(name);
        txt.append(" ");

        if (getExpression() != null) {
            txt.append(getExpression().toString());
            txt.append(" ");
        }

        boolean first = true;
        for (CssMedium m : mediums) {
            if (first) {
                first = false;
                txt.append(" ");
            } else {
                txt.append(", ");
            }
            txt.append(m.toString());
        }

        boolean hasBlock = (this.declarations.size() > 0 || this.rules.size() > 0);

        if (!hasBlock) {
            txt.append(";");
            return txt.toString();
        }

        txt.append(" {\n").append(start);

        for (CssRule dir : rules) {
            txt.append(dir.toString(nesting + 1));
            txt.append("\n");
        }

        first = true;
        for (CssDeclaration dec : declarations) {
            if (first) { first = false; } else { txt.append(";"); }
            txt.append("\n\t").append(start);
            txt.append(dec.toString());
        }

        txt.append("\n").append(start).append("}");

        return txt.toString();
    }

    @Override
    public String toString(boolean compact, int nesting) {
        return toString(nesting);
    }
}
