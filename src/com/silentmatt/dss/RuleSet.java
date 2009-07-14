package com.silentmatt.dss;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author matt
 */
public class RuleSet implements DeclarationContainer {
    private List<Declaration> declarations = new ArrayList<Declaration>();
    private List<Selector> selectors = new ArrayList<Selector>();

    public List<Declaration> getDeclarations() {
        return declarations;
    }

    public void setDeclarations(List<Declaration> declarations) {
        this.declarations = declarations;
    }

    public List<Selector> getSelectors() {
        return selectors;
    }

    public void setSelectors(List<Selector> selectors) {
        this.selectors = selectors;
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
        return toString(0, false);
    }

    public String toCompactString() {
        return toString(0, false);
    }

    public String toString(int nesting, boolean compact) {
        String start = "";
        if (!compact) {
            for (int i = 0; i < nesting; i++) {
                start += "\t";
            }
        }

        StringBuilder txt = new StringBuilder();
        boolean first = true;
        for (Selector sel : selectors) {
            if (first) {
                first = false;
                txt.append(start);
            }
            else {
                txt.append(compact ? "," : ", ");
            }
            txt.append(compact ? sel.toCompactString() : sel.toString());
        }
        txt.append(compact ? "{" : " {");
        txt.append(start);

        first = true;
        for (Declaration dec : declarations) {
            if (first) { first = false; } else { txt.append(";"); }
            if (!compact) { txt.append("\r\n\t" + start); }
            txt.append(compact ? dec.toCompactString() : dec.toString());
        }

        txt.append(compact ? "}" : ("\r\n" + start + "}"));

        return txt.toString();
    }
}
