package com.silentmatt.dss;

import java.util.List;

/**
 *
 * @author matt
 */
public class FontFaceDirective extends DeclarationDirective {
    public FontFaceDirective(List<Declaration> declarations) {
        super(declarations);
    }

    @Override
    public DirectiveType getType() {
        return DirectiveType.Define;
    }

    public String getName() {
        return "@font-face";
    }
}
