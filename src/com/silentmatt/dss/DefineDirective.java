package com.silentmatt.dss;

import java.util.List;

/**
 *
 * @author matt
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

    public String toCssString(int nesting) {
        return "";
    }
}
