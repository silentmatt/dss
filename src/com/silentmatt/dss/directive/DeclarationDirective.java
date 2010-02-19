package com.silentmatt.dss.directive;

import com.silentmatt.dss.Declaration;
import com.silentmatt.dss.DeclarationBlock;
import com.silentmatt.dss.DeclarationList;
import com.silentmatt.dss.Expression;
import com.silentmatt.dss.Rule;
import java.util.List;

/**
 *
 * @author Matthew Crumley
 */
public abstract class DeclarationDirective extends Rule {
    private final DeclarationBlock declarations;

    public DeclarationDirective(List<Declaration> declarations) {
        super();
        this.declarations = new DeclarationBlock(declarations);
    }

    private DeclarationList getDeclarations() {
        return declarations.getDeclarations();
    }

    public DeclarationBlock getDeclarationBlock() {
        return declarations;
    }

    public void addDeclaration(Declaration declaration) {
        declarations.addDeclaration(declaration);
    }

    public Expression getValue(String name) {
        Declaration declaration = getDeclaration(name);
        return (declaration != null) ? declaration.getExpression() : null;
    }

    public Declaration getDeclaration(String name) {
        for (Declaration d : declarations.getDeclarations()) {
            if (d.getName().equalsIgnoreCase(name)) {
                return d;
            }
        }
        return null;
    }

    public String getDeclarationsString(int nesting) {
        String start = Rule.getIndent(nesting);

        StringBuilder txt = new StringBuilder("{");

        txt.append(declarations.innerString(nesting + 1));

        txt.append("\n" + start + "}");

        return txt.toString();
    }

    public abstract String getName();

    @Override
    public String toString() {
        return toString(0);
    }

    public String toString(int nesting) {
        String start = Rule.getIndent(nesting);
        StringBuilder txt = new StringBuilder(start);
        txt.append(getName());
        txt.append(" ");
        txt.append(getDeclarationsString(nesting));
        return txt.toString();
    }
}
