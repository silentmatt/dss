package com.silentmatt.dss.directive;

import com.silentmatt.dss.DSSEvaluator;
import com.silentmatt.dss.Declaration;
import com.silentmatt.dss.Rule;
import com.silentmatt.dss.SimpleSelector;
import java.util.List;

/**
 *
 * @author Matthew Crumley
 */
public class PageDirective extends DeclarationDirective {
    private SimpleSelector selector;

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

    public void setSelector(SimpleSelector selector) {
        this.selector = selector;
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
    public void evaluate(DSSEvaluator.EvaluationState state, List<Rule> container) {
        DSSEvaluator.evaluateStyle(state, getDeclarations(), true);
    }
}
