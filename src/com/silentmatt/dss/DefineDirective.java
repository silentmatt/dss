package com.silentmatt.dss;

/**
 *
 * @author matt
 */
public class DefineDirective extends DeclarationDirective {
    @Override
    public DirectiveType getType() {
        return DirectiveType.Define;
    }

    public String getName() {
        return "@define";
    }

}
