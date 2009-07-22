package com.silentmatt.dss;

import java.util.List;

/**
 *
 * @author Matthew Crumley
 */
public class FontFaceDirective extends DeclarationDirective {
    public FontFaceDirective(List<Declaration> declarations) {
        super(declarations);
    }

    @Override
    public DirectiveType getType() {
        return DirectiveType.FontFace;
    }

    public String getName() {
        return "@font-face";
    }
}
