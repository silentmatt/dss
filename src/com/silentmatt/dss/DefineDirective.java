package com.silentmatt.dss;

import java.util.List;

/**
 *
 * @author matt
 */
public class DefineDirective extends DeclarationDirective {
    DefineDirective(List<Declaration> declarations) {
        super(declarations);
    }

    @Override
    public DirectiveType getType() {
        return DirectiveType.Define;
    }

    public String getName() {
        return "@define";
    }

}
