package com.silentmatt.dss.css;

import java.util.List;

/**
 *
 * @author Matthew Crumley
 */
public class CssFontFaceDirective extends CssDeclarationDirective {
    public CssFontFaceDirective(List<CssDeclaration> declarations) {
        super(declarations);
    }

    @Override
    public String getName() {
        return "@font-face";
    }
}
