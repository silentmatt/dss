package com.silentmatt.dss;

import java.util.List;

/**
 *
 * @author matt
 */
public class ClassDirective extends DeclarationDirective {
    private String id;

    public ClassDirective(String id, List<Declaration> declarations) {
        super(declarations);
        setID(id);
    }

    public String getName() {
        return "@class";
    }

    public DirectiveType getType() {
        return DirectiveType.Class;
    }

    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }

    @Override
    public String toString(int nesting, boolean compact) {
        String start = "";
        if (!compact) {
            for (int i = 0; i < nesting; i++) {
                start += "\t";
            }
        }

        StringBuilder txt = new StringBuilder(start);
        txt.append("@class ");
        txt.append(getID());
        if (!compact) {
            txt.append(" ");
        }
        txt.append(getDeclarationsString(nesting, compact));
        return txt.toString();
    }

    public void addDeclartion(Declaration declaration) {
        getDeclarations().add(declaration);
    }
}
