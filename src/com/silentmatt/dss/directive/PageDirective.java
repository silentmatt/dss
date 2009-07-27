package com.silentmatt.dss.directive;

import com.silentmatt.dss.*;
import com.silentmatt.dss.directive.DeclarationDirective;
import com.silentmatt.dss.directive.DirectiveType;
import java.util.List;

/**
 *
 * @author Matthew Crumley
 */
public class PageDirective extends DeclarationDirective {
    private SimpleSelector ss;

    public PageDirective(SimpleSelector pseudo, List<Declaration> declarations) {
        super(declarations);
        this.ss = pseudo;
    }

    public String getName() {
        return "@page";
    }

    public DirectiveType getType() {
        return DirectiveType.Page;
    }

    public SimpleSelector getSelector() {
        return ss;
    }

    public void setSelector(SimpleSelector ss) {
        this.ss = ss;
    }

    @Override
    public String toString(int nesting) {
        StringBuilder txt = new StringBuilder("@page");
        if (ss != null) {
            txt.append(" ");
            txt.append(ss.toString());
        }
        txt.append(" ");

        txt.append(getDeclarationsString(nesting));
        return txt.toString();
    }
}
