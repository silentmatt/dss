package com.silentmatt.dss;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author matt
 */
public abstract class DeclarationDirective implements Directive, DeclarationContainer {
    private List<Declaration> declarations = new ArrayList<Declaration>();

    public DeclarationDirective(List<Declaration> declarations) {
        this.declarations = declarations;
    }

    public RuleType getRuleType() {
        return RuleType.Directive;
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

    public Expression getValue(String name) {
        Declaration d = getDeclaration(name);
        if (d != null) {
            return d.getExpression();
        }
        return null;
    }

    public Declaration getDeclaration(String name) {
        for (Declaration d : declarations) {
            if (d.getName().equalsIgnoreCase(name)) {
                return d;
            }
        }
        return null;
    }

    public String getDeclarationsString(int nesting, boolean compact) {
        String start = "";
        if (!compact) {
            for (int i = 0; i < nesting; i++) {
                start += "\t";
            }
        }

        StringBuilder txt = new StringBuilder("{");

        for (Declaration dec : declarations) {
            if (!compact) { txt.append("\r\n\t" + start); }
            txt.append(compact ? dec.toCompactString() : dec.toString());
            txt.append(";");
        }

        txt.append(compact ? "}" : "\r\n" + start + "}");

        return txt.toString();
    }

    @Override
    public String toString() {
        return toString(0, false);
    }

    public String toCompactString() {
        return toString(0, true);
    }

    public String toString(int nesting, boolean compact) {
        StringBuilder txt = new StringBuilder(getName());
        if (!compact) {
            txt.append(" ");
        }
        txt.append(getDeclarationsString(nesting, compact));
        return txt.toString();
    }
}
