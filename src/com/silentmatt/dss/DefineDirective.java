package com.silentmatt.dss;

import java.util.List;

/**
 *
 * @author Matthew Crumley
 */
public class DefineDirective extends DeclarationDirective {
    private boolean global;

    DefineDirective(List<Declaration> declarations, boolean global) {
        super(declarations);
        this.global = global;
    }

    @Override
    public DirectiveType getType() {
        return DirectiveType.Define;
    }

    public String getName() {
        return "@define" + (global ? " global" : "");
    }

    public boolean isGlobal() {
        return global;
    }

    @Override
    public String toCssString(int nesting) {
        return "";
    }
}
