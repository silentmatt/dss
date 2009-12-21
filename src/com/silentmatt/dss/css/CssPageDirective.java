package com.silentmatt.dss.css;

import java.util.List;

/**
 *
 * @author Matthew Crumley
 */
public class CssPageDirective extends CssDeclarationDirective {
    private final CssSimpleSelector selector;

    public CssPageDirective(CssSimpleSelector pseudo, List<CssDeclaration> declarations) {
        super(declarations);
        this.selector = pseudo;
    }

    public String getName() {
        return "@page";
    }

    public CssSimpleSelector getSelector() {
        return selector;
    }

    @Override
    public String toString(int nesting) {
        StringBuilder txt = new StringBuilder("@page");
        if (selector != null) {
            txt.append(" ");
            txt.append(selector.toString());
        }
        txt.append(" ");

        txt.append(getDeclarationsString(nesting));
        return txt.toString();
    }
}
