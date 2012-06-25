package com.silentmatt.dss.directive;

import com.silentmatt.dss.EvaluationState;
import com.silentmatt.dss.Expression;
import com.silentmatt.dss.Immutable;
import com.silentmatt.dss.Rule;
import com.silentmatt.dss.css.CssNamespaceDirective;
import com.silentmatt.dss.css.CssRule;
import com.silentmatt.dss.css.CssTerm;
import com.silentmatt.dss.term.UrlTerm;
import java.util.List;

/**
 * @todo Why does this extend ExpressionDirective?
 * @author Matthew Crumley
 */
@Immutable
public final class NamespaceDirective extends ExpressionDirective {
    private final String prefix;

    public NamespaceDirective(String prefix, UrlTerm namespace) {
        super(namespace.toExpression());
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

    @Override
    public String toString() {
        return "@namespace " + (prefix != null ? (prefix + " ") : "") + getExpression().toString() + ";";
    }

    @Override
    public CssRule evaluate(EvaluationState state, List<Rule> container) {
        return new CssNamespaceDirective(prefix, new CssTerm(getExpression().toString()));
    }
}
