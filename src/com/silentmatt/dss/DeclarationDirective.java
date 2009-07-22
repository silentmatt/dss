package com.silentmatt.dss;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Matthew Crumley
 */
public abstract class DeclarationDirective implements Directive {
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

    public String getDeclarationsString(int nesting) {
        String start = "";
        for (int i = 0; i < nesting; i++) {
            start += "\t";
        }

        StringBuilder txt = new StringBuilder("{");

        for (Declaration dec : declarations) {
            txt.append("\n\t" + start);
            txt.append(dec.toString());
            txt.append(";");
        }

        txt.append("\n" + start + "}");

        return txt.toString();
    }

    @Override
    public String toString() {
        return toString(0);
    }

    public String toString(int nesting) {
        StringBuilder txt = new StringBuilder(getName());
        txt.append(" ");
        txt.append(getDeclarationsString(nesting));
        return txt.toString();
    }

    public String toCssString(int nesting) {
        return toString(nesting);
    }
}
