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
        return toString(false, nesting);
    }

    @Override
    public String toString(boolean compact, int nesting) {
        StringBuilder txt = new StringBuilder("@page");
        if (selector != null) {
            if (!compact) {
                txt.append(' ');
            }
            txt.append(selector.toString());
        }
        if (!compact) {
            txt.append(' ');
        }

        txt.append(getDeclarationsString(compact, nesting));
        return txt.toString();
    }
}
