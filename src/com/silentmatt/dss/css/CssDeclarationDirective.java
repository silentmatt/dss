package com.silentmatt.dss.css;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Matthew Crumley
 */
public abstract class CssDeclarationDirective extends CssRule {
    private final List<CssDeclaration> declarations;

    public CssDeclarationDirective(List<CssDeclaration> declarations) {
        super();
        this.declarations = new ArrayList<>(declarations);
    }

    public List<CssDeclaration> getDeclarations() {
        return declarations;
    }

    public void addDeclaration(CssDeclaration declaration) {
        declarations.add(declaration);
    }

    public CssExpression getValue(String name) {
        CssDeclaration declaration = getDeclaration(name);
        return (declaration != null) ? declaration.getExpression() : null;
    }

    public CssDeclaration getDeclaration(String name) {
        for (CssDeclaration d : declarations) {
            if (d.getName().equalsIgnoreCase(name)) {
                return d;
            }
        }
        return null;
    }

    public String getDeclarationsString(int nesting) {
        String start = CssRule.getIndent(nesting);

        StringBuilder txt = new StringBuilder("{");

        for (CssDeclaration dec : declarations) {
            txt.append("\n\t").append(start);
            txt.append(dec.toString());
            txt.append(";");
        }

        txt.append("\n").append(start).append("}");

        return txt.toString();
    }

    public String getDeclarationsString(boolean compact, int nesting) {
        if (!compact) {
            return getDeclarationsString(nesting);
        }

        StringBuilder txt = new StringBuilder("{");

        int count = 0;
        for (CssDeclaration dec : declarations) {
            txt.append(dec.toString(compact));
            ++count;
            if (!compact || count < declarations.size()) {
                txt.append(';');
            }
        }

        txt.append('}');

        return txt.toString();
    }

    public abstract String getName();

    @Override
    public String toString() {
        return toString(0);
    }

    @Override
    public String toString(int nesting) {
        String start = CssRule.getIndent(nesting);
        StringBuilder txt = new StringBuilder(start);
        txt.append(getName());
        txt.append(" ");
        txt.append(getDeclarationsString(nesting));
        return txt.toString();
    }

    @Override
    public String toString(boolean compact, int nesting) {
        StringBuilder txt = new StringBuilder();
        txt.append(getName());
        txt.append(getDeclarationsString(compact, nesting));
        return txt.toString();
    }
}
