package com.silentmatt.dss.directive;

import com.silentmatt.dss.Declaration;
import com.silentmatt.dss.EvaluationState;
import com.silentmatt.dss.Rule;
import com.silentmatt.dss.SimpleSelector;
import com.silentmatt.dss.css.CssCombinator;
import com.silentmatt.dss.css.CssPageDirective;
import com.silentmatt.dss.css.CssRule;
import com.silentmatt.dss.css.CssSimpleSelector;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Matthew Crumley
 */
public class PageDirective extends DeclarationDirective {
    private final SimpleSelector selector;

    public PageDirective(SimpleSelector pseudo, List<Declaration> declarations) {
        super(declarations);
        this.selector = pseudo;
    }

    public String getName() {
        return "@page";
    }

    public SimpleSelector getSelector() {
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

    @Override
    public CssRule evaluate(EvaluationState state, List<Rule> container) throws IOException {
        CssSimpleSelector pseudo = new CssSimpleSelector();
        pseudo.setCombinator(CssCombinator.None);
        pseudo.setPseudo(selector.toString().replaceFirst(":", ""));
        return new CssPageDirective(pseudo, getDeclarationBlock().evaluateStyle(state, true).getCssDeclarations(state));
    }
}
