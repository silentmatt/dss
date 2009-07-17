package com.silentmatt.dss;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author matt
 */
public class RuleSet implements Rule {
    private List<Directive> directives = new ArrayList<Directive>();
    private List<Declaration> declarations = new ArrayList<Declaration>();
    private List<Selector> selectors = new ArrayList<Selector>();

    public RuleType getRuleType() {
        return RuleType.RuleSet;
    }

    public List<Declaration> getDeclarations() {
        return declarations;
    }

    public void setDeclarations(List<Declaration> declarations) {
        this.declarations = declarations;
    }

    public void addDeclaration(Declaration declaration) {
        declarations.add(declaration);
    }

    public List<Selector> getSelectors() {
        return selectors;
    }

    public void setSelectors(List<Selector> selectors) {
        this.selectors = selectors;
    }

    public List<Directive> getDirectives() {
        return directives;
    }

    public void setDirectives(List<Directive> directives) {
        this.directives = directives;
    }

    public void addDirective(Directive directive) {
        directives.add(directive);
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
        Declaration d = getDeclaration(name);
        if (d != null) {
            return d.getExpression();
        }
        return null;
    }

    @Override
    public String toString() {
        return toString(0);
    }

    public String toString(int nesting) {
        String start = "";
        for (int i = 0; i < nesting; i++) {
            start += "\t";
        }

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

        for (Directive dir : directives) {
            txt.append("\r\n\t" + start);
            txt.append(dir.toString(nesting + 1));
        }
        for (Declaration dec : declarations) {
            txt.append("\r\n\t" + start);
            txt.append(dec.toString());
            txt.append(";");
        }

        txt.append("\r\n" + start + "}");

        return txt.toString();
    }

    public String toCssString(int nesting) {
        String start = "";
        for (int i = 0; i < nesting; i++) {
            start += "\t";
        }

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

        for (Directive dir : directives) {
            String dirString = dir.toCssString(nesting + 1);
            if (dirString.length() > 0) {
                txt.append("\r\n\t" + start);
                txt.append(dirString);
            }
        }
        for (Declaration dec : declarations) {
            txt.append("\r\n\t" + start);
            txt.append(dec.toString());
            txt.append(";");
        }

        txt.append("\r\n" + start + "}");

        return txt.toString();
    }
}
