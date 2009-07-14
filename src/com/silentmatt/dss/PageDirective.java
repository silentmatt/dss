package com.silentmatt.dss;

import java.util.List;

/**
 *
 * @author matt
 */
public class PageDirective extends DeclarationDirective {
    private SimpleSelector ss;

    public PageDirective(SimpleSelector pseudo, List<Declaration> declarations) {
        super(declarations);
        setSelector(pseudo);
    }

    public String getName() {
        return "@class";
    }

    public DirectiveType getType() {
        return DirectiveType.Class;
    }

    public SimpleSelector getSelector() {
        return ss;
    }

    public void setSelector(SimpleSelector ss) {
        this.ss = ss;
    }

    @Override
    public String toString(int nesting, boolean compact) {
        StringBuilder txt = new StringBuilder("@page");
        if (ss != null) {
            txt.append(" ");
            txt.append(ss.toString());
        }
        if (!compact) {
            txt.append(" ");
        }
        txt.append(getDeclarationsString(nesting, compact));
        return txt.toString();
    }
}
