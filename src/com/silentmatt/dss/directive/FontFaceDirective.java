package com.silentmatt.dss.directive;

import com.silentmatt.dss.*;
import com.silentmatt.dss.directive.DeclarationDirective;
import com.silentmatt.dss.directive.DirectiveType;
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
