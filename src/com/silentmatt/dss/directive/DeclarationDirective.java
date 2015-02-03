package com.silentmatt.dss.directive;

import com.silentmatt.dss.Immutable;
import com.silentmatt.dss.declaration.Declaration;
import com.silentmatt.dss.declaration.DeclarationList;
import com.silentmatt.dss.declaration.Expression;
import com.silentmatt.dss.rule.DeclarationBlock;
import com.silentmatt.dss.rule.Rule;

/**
 *
 * @author Matthew Crumley
 */
@Immutable
public abstract class DeclarationDirective extends Rule {
    private final DeclarationBlock declarations;

    public DeclarationDirective(DeclarationList declarations) {
        super();
        this.declarations = new DeclarationBlock(declarations);
    }

    public DeclarationBlock getDeclarationBlock() {
        return declarations;
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

        txt.append("\n").append(start).append("}");

        return txt.toString();
    }

    public abstract String getName();

    @Override
    public String toString() {
        return toString(0);
    }

    @Override
    public String toString(int nesting) {
        String start = Rule.getIndent(nesting);
        StringBuilder txt = new StringBuilder(start);
        txt.append(getName());
        txt.append(" ");
        txt.append(getDeclarationsString(nesting));
        return txt.toString();
    }
}
