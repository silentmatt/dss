package com.silentmatt.dss.directive;

import com.silentmatt.dss.EvaluationState;
import com.silentmatt.dss.Expression;
import com.silentmatt.dss.Rule;
import com.silentmatt.dss.term.UrlTerm;
import java.util.List;

/**
 * @todo Why does this extend ExpressionDirective?
 * @author Matthew Crumley
 */
public class NamespaceDirective extends ExpressionDirective {
    private String prefix;

    public NamespaceDirective(String prefix, UrlTerm namespace) {
        super(new Expression());
        getExpression().getTerms().add(namespace);
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
    public void evaluate(EvaluationState state, List<Rule> container) {
        // Do nothing
    }
}
