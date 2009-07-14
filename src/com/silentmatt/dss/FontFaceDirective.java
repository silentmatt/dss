package com.silentmatt.dss;

/**
 *
 * @author matt
 */
public class FontFaceDirective extends DeclarationDirective {
    @Override
    public DirectiveType getType() {
        return DirectiveType.Define;
    }

    public String getName() {
        return "@font-face";
    }
}
