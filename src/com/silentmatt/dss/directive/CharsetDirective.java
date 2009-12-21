package com.silentmatt.dss.directive;

import com.silentmatt.dss.EvaluationState;
import com.silentmatt.dss.Expression;
import com.silentmatt.dss.Rule;
import com.silentmatt.dss.css.CssCharsetDirective;
import com.silentmatt.dss.css.CssRule;
import com.silentmatt.dss.css.CssTerm;
import com.silentmatt.dss.term.Term;
import java.util.List;

/**
 *
 * @author Matthew Crumley
 */
public class CharsetDirective extends ExpressionDirective {
    public CharsetDirective(Term charset) {
        super(new Expression());
        getExpression().getTerms().add(charset);
    }

    @Override
    public String toString() {
        return "@charset " + getExpression() + ";";
    }

    @Override
    public CssRule evaluate(EvaluationState state, List<Rule> container) {
        return new CssCharsetDirective(new CssTerm(getExpression().toString()));
    }
}
