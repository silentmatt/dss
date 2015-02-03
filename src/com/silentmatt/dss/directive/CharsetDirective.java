package com.silentmatt.dss.directive;

import com.silentmatt.dss.Immutable;
import com.silentmatt.dss.css.CssCharsetDirective;
import com.silentmatt.dss.css.CssRule;
import com.silentmatt.dss.css.CssTerm;
import com.silentmatt.dss.evaluator.EvaluationState;
import com.silentmatt.dss.rule.Rule;
import com.silentmatt.dss.term.Term;
import java.util.List;

/**
 *
 * @author Matthew Crumley
 */
@Immutable
public final class CharsetDirective extends ExpressionDirective {
    public CharsetDirective(Term charset) {
        super(charset.toExpression());
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
